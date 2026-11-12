package maki.chunksmithcontroller;

import com.sun.management.OperatingSystemMXBean;
import maki.chunksmithcache.CacheManager;
import java.lang.management.ManagementFactory;
import java.nio.file.*;
import java.io.Reader;
import java.util.*;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CpuGovernor {
    private static final Logger LOG=LoggerFactory.getLogger("MakiChunksmithController");
    private static final Path CONFIG=Path.of("config/maki-chunksmith-controller.properties");
    private static final Path PREGEN_PROFILE=Path.of("config/maki-chunksmith-controller-pregen-profile.properties");
    private static final AdaptivePolicy POLICY=new AdaptivePolicy();
    private static Settings settings=new Settings();
    private static volatile int cap=2;
    private static volatile long taskActivity;
    private static volatile WorkTelemetry observedWork;
    private static WorkTelemetry previousWork;
    private static LodCounters previousLod;
    private static long lastTime,lastCpu,lastGc=-1,lastOom=-1,lastThrottled=-1,lastStamp=Long.MIN_VALUE,lastReport;
    private static long reportedExperiment;
    private static long previousPairConfirmed=-1,previousPairNew=-1,previousPairRepair=-1,previousUncertain=-1;
    private static WorkTelemetry profileWork;
    private static boolean profileDecisionMade;
    private static long profileNewPairs,profileRepairPairs,lastProfileSavedMillis;
    private static volatile String pipelinePressure;
    private static boolean configGood=true;
    private static double capacity=detectCapacity();

    public static Settings settings(){return settings;}
    public static boolean healthyConfig(){return configGood;}
    public static int cap(){return cap;}
    public static String pipelinePressure(){refreshPipelinePressure();return pipelinePressure;}
    public static boolean fullWorkPending(MinecraftServer server,int centerX,int centerZ,int radiusBlocks){
        return CacheManager.fullWorkPending(server,centerX,centerZ,radiusBlocks);
    }
    public static boolean pregenReady(MinecraftServer server){return CacheManager.pregenReady(server);}
    static void refreshPipelinePressure(){
        String full=CacheManager.resourcePressure();
        pipelinePressure=full!=null?full:GenerationStorageBudget.pressure();
    }

    private record ControllerObservation(boolean available,long pairConfirmed,long pairNew,long pairRepair,long uncertain,
                                         long oldestMillis,boolean admissionBlocked,boolean sensorUnknown,
                                         long fastSignature,String reason){}

    private static ControllerObservation controllerObservation() {
        // Completion events come directly from the installed FULL cache and native LOD sink.
        PregenFlowController.Snapshot state=PregenFlowController.snapshot();
        return new ControllerObservation(true,state.pairConfirmedRuntime(),state.pairNew(),
            state.pairRepair(),state.uncertain(),state.oldestMillis(),state.blocked(),
            state.sensorUnknown(),state.fastSignature(),
            "elite-local-control");
    }

    private static void notifyGenerationCapChanged(int nextCap) {
        PregenFlowController.generationCapChanged(nextCap);
    }

    public static void markActive(){taskActivity=System.nanoTime();}
    public static void observe(WorkTelemetry work){observedWork=work;}

    public static void tick(double mspt,int players){
        long now=System.nanoTime();
        if(lastTime!=0&&now-lastTime<settings.interval*1e9)return;
        reload();
        long cpu=cpuTime(),gc=gcMillis();
        if(lastTime==0){
            lastTime=now;lastCpu=cpu;lastGc=gc;
            LOG.info("Maki Chunksmith Controller E-lite ready: allocated {} cores, ceiling {}..{}; generation uses stage WIP and confirmed FULL/LOD results",capacity,settings.min,settings.max);
            return;
        }

        double seconds=(now-lastTime)/1e9;
        double used=cpu>=lastCpu&&lastCpu>=0?100.0*(cpu-lastCpu)/(now-lastTime)/capacity:Double.NaN;
        double gcPercent=gc>=lastGc&&lastGc>=0?100.0*(gc-lastGc)/(seconds*1000):Double.NaN;
        lastTime=now;lastCpu=cpu;lastGc=gc;

        var memory=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double heap=memory.getMax()>0?100.0*memory.getUsed()/memory.getMax():Double.NaN;

        MemoryBudget budget=MemoryBudget.read();
        LodCounters lod=LodCounters.read();
        boolean comparable=previousLod!=null&&lod.identity()==previousLod.identity()&&lod.available()&&previousLod.available();
        long writes=comparable?Math.max(0,lod.written()-previousLod.written()):0;
        long sync=comparable?Math.max(0,lod.synchronousWrites()-previousLod.synchronousWrites()):0;
        previousLod=lod;
        long oom=budget.oomKills()>=0&&lastOom>=0?Math.max(0,budget.oomKills()-lastOom):0;
        long throttled=budget.throttledMicros()>=0&&lastThrottled>=0
            ?Math.max(0,budget.throttledMicros()-lastThrottled):0;
        lastOom=budget.oomKills();
        lastThrottled=budget.throttledMicros();

        WorkTelemetry work=observedWork;
        WorkTelemetry.NativeState state=work==null?null:work.state;
        boolean stateFresh=state!=null&&now>=state.time()&&now-state.time()<10_000_000_000L;
        boolean active=stateFresh&&taskActivity!=0&&now-taskActivity<10_000_000_000L;
        WorkTelemetry.Batch batch=work==null?new WorkTelemetry.Batch(0,0):work.drain();

        refreshPipelinePressure();
        int previous=cap;
        if(work!=previousWork){
            POLICY.reset(settings,"new-generation-task");previousWork=work;
            profileWork=work;profileDecisionMade=false;profileNewPairs=profileRepairPairs=0;
        }
        AdaptiveSample sample=new AdaptiveSample(seconds,used,mspt,heap,gcPercent,budget.headroomMb(),batch.completions(),writes,
            comparable,stateFresh?state.width():0,stateFresh?state.inFlight():0,lod.queue(),stateFresh?state.worldWrites():-1,
            sync,(stateFresh&&state.held()) || pipelinePressure!=null,batch.latencyP95Ms(),oom,throttled);

        refreshPipelinePressure();
        ControllerObservation controller=controllerObservation();
        long pairDelta=controller.available()&&previousPairConfirmed>=0
            ?Math.max(0,controller.pairConfirmed()-previousPairConfirmed):0;
        long newDelta=controller.available()&&previousPairNew>=0?Math.max(0,controller.pairNew()-previousPairNew):0;
        long repairDelta=controller.available()&&previousPairRepair>=0?Math.max(0,controller.pairRepair()-previousPairRepair):0;
        long uncertainDelta=controller.available()&&previousUncertain>=0?Math.max(0,controller.uncertain()-previousUncertain):0;
        long unknownDelta=Math.max(0L,pairDelta-newDelta-repairDelta);
        previousPairConfirmed=controller.available()?controller.pairConfirmed():-1;
        previousPairNew=controller.available()?controller.pairNew():-1;
        previousPairRepair=controller.available()?controller.pairRepair():-1;
        previousUncertain=controller.available()?controller.uncertain():-1;
        boolean pairAvailable=controller.available()&&!controller.sensorUnknown();
        if(work==profileWork&&controller.available()){
            profileNewPairs+=newDelta;
            profileRepairPairs+=repairDelta;
        }
        String workClassBand=PregenPerformanceProfile.workClassBand(profileNewPairs,profileRepairPairs);

        if(!profileDecisionMade&&active&&players==0&&pairAvailable
            &&!"UNKNOWN".equals(workClassBand)){
            PregenPerformanceProfile.Environment environment=performanceEnvironment(controller.fastSignature(),workClassBand);
            OptionalInt warm=PregenPerformanceProfile.load(PREGEN_PROFILE,environment,settings,System.currentTimeMillis());
            if(warm.isPresent()){
                cap=POLICY.warmStart(settings,warm.getAsInt(),"warm-start-profile");
                LOG.info("Applied pregen c warm-start profile: c={}, workClassBand={}, fastSignature={}",
                    cap,workClassBand,controller.fastSignature());
            }
            profileDecisionMade=true;
        }
        if(!configGood)cap=POLICY.unavailable(settings,"invalid-config");
        else if(players>0)cap=POLICY.update(sample,players,settings,pairDelta,pairAvailable,controller.admissionBlocked(),
            controller.fastSignature(),newDelta,repairDelta,uncertainDelta,controller.oldestMillis());
        else if(!active)cap=POLICY.reset(settings,"generation-idle");
        else {
            cap=POLICY.update(sample,players,settings,pairDelta,pairAvailable,controller.admissionBlocked(),
                controller.fastSignature(),newDelta,repairDelta,uncertainDelta,controller.oldestMillis());
        }
        if(previous!=cap)notifyGenerationCapChanged(cap);

        boolean experimentFinished=reportedExperiment!=POLICY.experimentNumber;
        if(experimentFinished){
            LOG.info("LOD c-neighbourhood experiment #{}: {}; decision {}; rate is confirmed runtime FULL+LOD PAIR/s",POLICY.experimentNumber,POLICY.lastExperiment,POLICY.reason);
            reportedExperiment=POLICY.experimentNumber;
        }
        long wallNow=System.currentTimeMillis();
        if(active&&players==0&&pairAvailable&&!controller.admissionBlocked()
            &&!"UNKNOWN".equals(workClassBand)&&"baseline".equals(POLICY.phase)
            &&wallNow-lastProfileSavedMillis>=300_000L){
            PregenPerformanceProfile.Environment environment=performanceEnvironment(controller.fastSignature(),workClassBand);
            if(PregenPerformanceProfile.save(PREGEN_PROFILE,environment,cap,wallNow))
                lastProfileSavedMillis=wallNow;
        }
        if(previous!=cap||experimentFinished||now-lastReport>=30_000_000_000L){
            LOG.info("LOD CPU ceiling {} -> {}; CPU {}% of {} cores, MSPT {}, heap {}%, players {}, reason {} (follow-owned); confirmed PAIR {}/s ({} in sample; new {}, reused FULL {}, unclassified {}), all LOD saved {}/s ({} in sample), follow requests {}/s ({} in sample), GC {}%, native width {}, in-flight {}, LOD queue {}, phase {}, admissionBlocked={}, controllerReason={}",
                previous,cap,fmt(used),fmt(capacity),fmt(mspt),fmt(heap),players,POLICY.reason,
                fmt(pairDelta/seconds),pairDelta,newDelta,repairDelta,unknownDelta,fmt(writes/seconds),writes,fmt(batch.completions()/seconds),batch.completions(),fmt(gcPercent),
                sample.nativeWidth(),sample.inFlight(),sample.lodQueue(),POLICY.phase,controller.admissionBlocked(),controller.reason());
            lastReport=now;
        }
    }

    private static PregenPerformanceProfile.Environment performanceEnvironment(long fastSignature,String workClassBand){
        long heapMax=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax();
        return new PregenPerformanceProfile.Environment(
            PregenPerformanceProfile.CONTROLLER_VERSION,cpuAffinity(),cpuQuota(),capacity,heapMax,
            chunksmithConfigSignature(),pairFeedVersion(),fastSignature,workClassBand);
    }

    private static String cpuAffinity(){
        try{
            return Files.readAllLines(Path.of("/proc/self/status")).stream()
                .filter(line->line.startsWith("Cpus_allowed_list:")).findFirst()
                .map(line->line.substring(line.indexOf(':')+1).trim()).orElse("unknown");
        }catch(Exception unavailable){return "unknown";}
    }

    private static String cpuQuota(){
        try{return Files.readString(Path.of("/sys/fs/cgroup/cpu.max")).trim();}
        catch(Exception ignored){
            try{
                return Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_quota_us")).trim()+"/"
                    +Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_period_us")).trim();
            }catch(Exception unavailable){return "unknown";}
        }
    }

    private static String chunksmithConfigSignature(){
        try{
            var config=com.kishku7.chunksmith.ChunksmithProvider.get().getConfig();
            return config.isIoThrottleEnabled()+"|"+config.getThrottleMaxChunkMillis()+"|"+config.getDispatchMaxConcurrent();
        }catch(Throwable unavailable){return "unknown";}
    }

    private static String pairFeedVersion(){
        return "E_LITE_FULL_PROVENANCE_V2";
    }

    private static String fmt(double n){return String.format(Locale.ROOT,"%.1f",n);}
    private static long cpuTime(){var bean=ManagementFactory.getOperatingSystemMXBean();return bean instanceof OperatingSystemMXBean os?os.getProcessCpuTime():-1;}
    private static long gcMillis(){long sum=0;boolean found=false;for(var bean:ManagementFactory.getGarbageCollectorMXBeans()){long n=bean.getCollectionTime();if(n>=0){sum+=n;found=true;}}return found?sum:-1;}

    static double detectCapacity(){
        double cores=Runtime.getRuntime().availableProcessors();
        try{cores=CpuTopology.restrict(cores,Files.readString(Path.of("/sys/devices/system/cpu/online")));}catch(Exception ignored){}
        try{
            String allowed=Files.readAllLines(Path.of("/proc/self/status")).stream()
                    .filter(line->line.startsWith("Cpus_allowed_list:")).findFirst().orElseThrow();
            cores=CpuTopology.restrict(cores,allowed.substring(allowed.indexOf(':')+1));
        }catch(Exception ignored){}
        try {
            String[] max=Files.readString(Path.of("/sys/fs/cgroup/cpu.max")).trim().split("\\s+");
            if(!max[0].equals("max")) cores=Math.min(cores,Double.parseDouble(max[0])/Double.parseDouble(max[1]));
        } catch(Exception ignored) {
            try {
                double quota=Double.parseDouble(Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_quota_us")).trim());
                double period=Double.parseDouble(Files.readString(Path.of("/sys/fs/cgroup/cpu/cpu.cfs_period_us")).trim());
                if(quota>0&&period>0)cores=Math.min(cores,quota/period);
            } catch(Exception unavailable) { }
        }
        return Math.max(0.1,cores);
    }

    /**
     * One-time migration of the real pre-rename config, preserving user settings.
     * This does not affect terrain/cache identity and never overwrites a new config.
     */
    private static void migrateConfig() throws java.io.IOException {
        Path legacy=Path.of("config/maki-lod-cpu.properties");
        if(Files.exists(CONFIG)||!Files.isRegularFile(legacy))return;
        Files.createDirectories(CONFIG.getParent());
        try {
            Files.move(legacy,CONFIG,StandardCopyOption.ATOMIC_MOVE);
        } catch(AtomicMoveNotSupportedException unsupported) {
            Files.move(legacy,CONFIG);
        }
        LOG.info("Migrated Chunksmith controller config to {}",CONFIG);
    }

    private static void reload(){
        try{
            migrateConfig();
            long stamp=Files.getLastModifiedTime(CONFIG).toMillis();
            if(stamp==lastStamp)return;
            Properties p=new Properties();
            try(Reader reader=Files.newBufferedReader(CONFIG)){p.load(reader);}
            Settings fresh=Settings.parse(p);
            settings=fresh;
            capacity=fresh.cpuCapacity>0?fresh.cpuCapacity:detectCapacity();
            lastStamp=stamp;
            configGood=true;
            cap=POLICY.reset(settings,"config-reloaded");
            profileDecisionMade=false;profileNewPairs=profileRepairPairs=0;
            LOG.info("Loaded {}; CPU budget {} cores, ceiling {}..{}; confirmed-PAIR c-neighbourhood window {}s, settle {}s",
                CONFIG,capacity,fresh.min,fresh.max,fresh.windowSeconds,fresh.settleSeconds);
        }catch(Exception e){
            if(configGood)LOG.error("Throughput governor config missing/invalid; limiting generation until fixed: {}",e.toString());
            configGood=false;
        }
    }
}
