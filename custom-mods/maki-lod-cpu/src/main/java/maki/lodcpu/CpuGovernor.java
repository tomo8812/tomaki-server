package maki.lodcpu;

import com.sun.management.OperatingSystemMXBean;
import com.google.gson.Gson;
import java.lang.management.ManagementFactory;
import java.nio.file.*;
import java.io.Reader;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CpuGovernor {
    private static final Logger LOG=LoggerFactory.getLogger("MakiLodCpu");
    private static final Path CONFIG=Path.of("config/maki-lod-cpu.properties");
    private static final AdaptivePolicy POLICY=new AdaptivePolicy();
    private static final Gson JSON=new Gson();
    private static Settings settings=new Settings();
    private static volatile int cap=2;
    private static volatile long taskActivity;
    private static volatile WorkTelemetry observedWork;
    private static WorkTelemetry previousWork;
    private static LodCounters previousLod;
    private static long lastTime,lastCpu,lastGc=-1,lastOom=-1,lastThrottled=-1,lastStamp=Long.MIN_VALUE,lastReport;
    private static long reportedExperiment;
    private static boolean configGood=true;
    private static double capacity=detectCapacity();
    private static double observedCpu=Double.NaN,observedMspt=Double.NaN,observedHeap=100;
    public static Settings settings(){return settings;}
    public static boolean healthyConfig(){return configGood;}
    public static boolean onlineOverload(){return !configGood||!Double.isFinite(observedCpu)||observedCpu>=98||observedMspt>=45||observedHeap>=settings.highHeap;}
    public static boolean recovered(){return configGood&&Double.isFinite(observedCpu)&&observedCpu<80&&observedMspt<25&&observedHeap<settings.highHeap-10;}
    public static int cap(){return cap;}
    public static void markActive(){taskActivity=System.nanoTime();}
    public static void observe(WorkTelemetry work){observedWork=work;}

    public static void tick(double mspt,int players){
        long now=System.nanoTime();
        if(lastTime!=0&&now-lastTime<settings.interval*1e9)return;
        reload();long cpu=cpuTime(),gc=gcMillis();
        if(lastTime==0){
            lastTime=now;lastCpu=cpu;lastGc=gc;
            LOG.info("Throughput governor 1.3.2 ready: allocated {} cores, ceiling {}..{}; CPU saturation alone does not reduce idle generation",capacity,settings.min,settings.max);return;
        }
        double seconds=(now-lastTime)/1e9;
        double used=cpu>=lastCpu&&lastCpu>=0?100.0*(cpu-lastCpu)/(now-lastTime)/capacity:Double.NaN;
        double gcPercent=gc>=lastGc&&lastGc>=0?100.0*(gc-lastGc)/(seconds*1000):Double.NaN;
        lastTime=now;lastCpu=cpu;lastGc=gc;
        var memory=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double heap=memory.getMax()>0?100.0*memory.getUsed()/memory.getMax():Double.NaN;
        observedCpu=used;observedMspt=mspt;observedHeap=heap;
        MemoryBudget budget=MemoryBudget.read();LodCounters lod=LodCounters.read();
        boolean comparable=previousLod!=null&&lod.identity()==previousLod.identity()&&lod.available()&&previousLod.available();
        long writes=comparable?Math.max(0,lod.written()-previousLod.written()):0;
        long bytes=comparable?Math.max(0,lod.bytes()-previousLod.bytes()):0;
        long sync=comparable?Math.max(0,lod.synchronousWrites()-previousLod.synchronousWrites()):0;
        previousLod=lod;
        long oom=budget.oomKills()>=0&&lastOom>=0?Math.max(0,budget.oomKills()-lastOom):0;lastOom=budget.oomKills();
        long throttled=budget.throttledMicros()>=0&&lastThrottled>=0?Math.max(0,budget.throttledMicros()-lastThrottled):0;lastThrottled=budget.throttledMicros();
        WorkTelemetry work=observedWork;
        WorkTelemetry.NativeState state=work==null?null:work.state;
        boolean active=state!=null&&now-state.time()<10_000_000_000L&&taskActivity!=0&&now-taskActivity<10_000_000_000L;
        WorkTelemetry.Batch batch=work==null?new WorkTelemetry.Batch(0,0):work.drain();
        int previous=cap;
        if(work!=previousWork){POLICY.reset(settings,"new-generation-task");previousWork=work;}
        AdaptiveSample sample=new AdaptiveSample(seconds,used,mspt,heap,gcPercent,budget.headroomMb(),batch.completions(),writes,
            comparable,state==null?0:state.width(),state==null?0:state.inFlight(),lod.queue(),state==null?-1:state.worldWrites(),
            sync,state!=null&&state.held(),batch.latencyP95Ms(),oom);
        if(!configGood)cap=POLICY.unavailable(settings,"invalid-config");
        else if(players>0)cap=POLICY.update(sample,players,settings);
        else if(!active)cap=POLICY.reset(settings,"generation-idle");
        else cap=POLICY.update(sample,players,settings);
        boolean experimentFinished=reportedExperiment!=POLICY.experimentNumber;
        if(experimentFinished){
            LOG.info("LOD throughput experiment #{}: {}; decision {}; rates are completed-request AND successful-LOD-write pipeline chunks/s",POLICY.experimentNumber,POLICY.lastExperiment,POLICY.reason);
            reportedExperiment=POLICY.experimentNumber;
        }
        if(previous!=cap||experimentFinished||now-lastReport>=30_000_000_000L){
            LOG.info("LOD CPU ceiling {} -> {}; CPU {}% of {} cores, MSPT {}, heap {}%, players {}, reason {} (follow-owned); all LOD saved {}/s ({} in sample), follow requests {}/s ({} in sample), GC {}%, native width {}, in-flight {}, LOD queue {}, phase {}",previous,cap,fmt(used),fmt(capacity),fmt(mspt),fmt(heap),players,POLICY.reason,fmt(writes/seconds),writes,fmt(batch.completions()/seconds),batch.completions(),fmt(gcPercent),sample.nativeWidth(),sample.inFlight(),sample.lodQueue(),POLICY.phase);
            report(sample,state,budget,bytes,throttled,players,active);lastReport=now;
        }
    }
    private static String fmt(double n){return String.format(Locale.ROOT,"%.1f",n);}
    private static long cpuTime(){var bean=ManagementFactory.getOperatingSystemMXBean();return bean instanceof OperatingSystemMXBean os?os.getProcessCpuTime():-1;}
    private static long gcMillis(){long sum=0;boolean found=false;for(var bean:ManagementFactory.getGarbageCollectorMXBeans()){long n=bean.getCollectionTime();if(n>=0){sum+=n;found=true;}}return found?sum:-1;}
    static double detectCapacity(){
        double cores=Runtime.getRuntime().availableProcessors();
        // ActiveProcessorCount is used to size Minecraft's worker pool. Linux's
        // actual online/allowed CPU sets must still bound the CPU usage denominator.
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
    private static void reload(){
        try{
            long stamp=Files.getLastModifiedTime(CONFIG).toMillis();if(stamp==lastStamp)return;
            Properties p=new Properties();try(Reader reader=Files.newBufferedReader(CONFIG)){p.load(reader);}
            Settings fresh=Settings.parse(p);settings=fresh;capacity=fresh.cpuCapacity>0?fresh.cpuCapacity:detectCapacity();
            lastStamp=stamp;configGood=true;cap=POLICY.reset(settings,"config-reloaded");
            LOG.info("Loaded {}; CPU budget {} cores, ceiling {}..{}; A/B/A saved-LOD throughput window {}s, settle {}s",CONFIG,capacity,fresh.min,fresh.max,fresh.windowSeconds,fresh.settleSeconds);
        }catch(Exception e){if(configGood)LOG.error("Throughput governor config missing/invalid; limiting generation to 1 until fixed: {}",e.toString());configGood=false;}
    }
    private static Object finite(double n){return Double.isFinite(n)?n:null;}
    private static void report(AdaptiveSample x,WorkTelemetry.NativeState state,MemoryBudget memory,long bytes,long throttled,int players,boolean active){
        try{
            Map<String,Object> m=new LinkedHashMap<>();
            m.put("time",java.time.Instant.now().toString());m.put("statusSchemaVersion",2);m.put("controllerVersion","1.3.2");
            m.put("cpuBudgetCores",capacity);m.put("cpuBudgetUsedPercent",finite(x.cpu()));
            m.put("mspt",finite(x.mspt()));m.put("heapUsedPercent",finite(x.heap()));m.put("players",players);
            m.put("cpuConcurrencyCeiling",cap);m.put("reason",POLICY.reason);m.put("reasonScope","follow-owned-generation-task");m.put("active",active);
            m.put("nativeDispatchWidth",x.nativeWidth());m.put("inFlight",x.inFlight());m.put("sampleSeconds",x.seconds());
            m.put("lodSavedPerSecond",finite(x.lodWrites()/x.seconds()));m.put("lodSavedBytesPerSecond",finite(bytes/x.seconds()));m.put("lodSavedInSample",x.lodWrites());
            m.put("lodSavedScope","all-chunksmith-cslod-store-writes");
            m.put("requestCompletionsPerSecond",finite(x.completions()/x.seconds()));m.put("requestCompletionsInSample",x.completions());
            m.put("requestCompletionScope","follow-owned-generation-task");
            m.put("throughputBasis",x.storeAvailable()?"successful-native-LOD-store-writes":"completed-requests-fallback");
            m.put("lodWriteQueue",x.lodQueue());m.put("worldWriteQueue",x.worldWriteQueue());m.put("synchronousLodWrites",x.synchronousWrites());
            m.put("nativeBackpressure",x.nativeHeld());m.put("gcTimePercent",finite(x.gc()));
            m.put("nativeWriteHeld",state!=null&&state.writeHeld());m.put("nativeResidencyHeld",state!=null&&state.residencyHeld());
            m.put("nativeHeapHeld",state!=null&&state.heapHeld());m.put("nativeAddedChunks",state==null?-1:state.addedChunks());
            m.put("systemMemoryHeadroomMb",memory.headroomMb());m.put("processRssMb",memory.rssMb());
            m.put("memoryPsiSomeAvg10",memory.memoryPsi());m.put("ioPsiSomeAvg10",memory.ioPsi());
            m.put("cgroupThrottledMicrosInSample",throttled);m.put("cgroupOomKillsInSample",x.oomKills());
            m.put("requestLatencyP95BucketUpperMs",x.latencyP95Ms());
            m.put("experimentPhase",POLICY.phase);m.put("lastWindowRate",POLICY.lastWindowRate);
            m.put("baselineRate",POLICY.baselineRate);m.put("probeRate",POLICY.probeRate);m.put("confirmedBaselineRate",POLICY.confirmedBaselineRate);
            m.put("lastExperiment",POLICY.lastExperiment);
            m.put("experimentNumber",POLICY.experimentNumber);
            m.put("experimentRetryRemainingSeconds",POLICY.retryRemainingSeconds());
            Path tmp=Path.of("logs/maki-lod-cpu-status.json.tmp"),dest=Path.of("logs/maki-lod-cpu-status.json");
            Files.writeString(tmp,JSON.toJson(m)+"\n");
            try{Files.move(tmp,dest,StandardCopyOption.ATOMIC_MOVE,StandardCopyOption.REPLACE_EXISTING);}catch(AtomicMoveNotSupportedException e){Files.move(tmp,dest,StandardCopyOption.REPLACE_EXISTING);}
        }catch(Exception e){LOG.debug("Cannot write throughput status",e);}
    }
}
