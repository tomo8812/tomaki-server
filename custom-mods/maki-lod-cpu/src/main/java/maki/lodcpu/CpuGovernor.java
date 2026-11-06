package maki.lodcpu;

import com.sun.management.OperatingSystemMXBean;
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
    private static final SharedWorkBudget SHARED_BUDGET=new SharedWorkBudget();
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
    public static int cap(){return SHARED_BUDGET.effectiveFollowCap(cap);}
    public static int baseCap(){return cap;}

    static SharedWorkBudget.Decision reserveBackfill(String taskMode){
        if(!configGood)return new SharedWorkBudget.Decision(false,false,"invalid-config",0L);
        return SHARED_BUDGET.reserve(System.nanoTime(),taskMode,cap,settings);
    }
    static boolean backfillReservationHeld(){return SHARED_BUDGET.reserved();}
    static void backfillStarted(){SHARED_BUDGET.backfillStarted(System.nanoTime());}
    static void releaseBackfill(String outcome){SHARED_BUDGET.release(System.nanoTime(),outcome,settings);}
    static String sharedBudgetStatus(){return SHARED_BUDGET.summary(System.nanoTime(),cap);}

    public static void markActive(){taskActivity=System.nanoTime();}
    public static void observe(WorkTelemetry work){observedWork=work;}

    public static void tick(double mspt,int players){
        long now=System.nanoTime();
        if(lastTime!=0&&now-lastTime<settings.interval*1e9)return;
        reload();
        long cpu=cpuTime(),gc=gcMillis();
        if(lastTime==0){
            lastTime=now;lastCpu=cpu;lastGc=gc;
            LOG.info("Throughput governor 1.4.0 ready: allocated {} cores, ceiling {}..{}; follow and backfill share one pressure/fairness budget, and CPU saturation alone does not reduce idle generation",capacity,settings.min,settings.max);
            return;
        }
        double seconds=(now-lastTime)/1e9;
        double used=cpu>=lastCpu&&lastCpu>=0?100.0*(cpu-lastCpu)/(now-lastTime)/capacity:Double.NaN;
        double gcPercent=gc>=lastGc&&lastGc>=0?100.0*(gc-lastGc)/(seconds*1000):Double.NaN;
        lastTime=now;lastCpu=cpu;lastGc=gc;
        var memory=ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        double heap=memory.getMax()>0?100.0*memory.getUsed()/memory.getMax():Double.NaN;
        observedCpu=used;observedMspt=mspt;observedHeap=heap;
        MemoryBudget budget=MemoryBudget.read();
        LodCounters lod=LodCounters.read();
        boolean comparable=previousLod!=null&&lod.identity()==previousLod.identity()&&lod.available()&&previousLod.available();
        long writes=comparable?Math.max(0,lod.written()-previousLod.written()):0;
        long bytes=comparable?Math.max(0,lod.bytes()-previousLod.bytes()):0;
        long sync=comparable?Math.max(0,lod.synchronousWrites()-previousLod.synchronousWrites()):0;
        previousLod=lod;
        long oom=budget.oomKills()>=0&&lastOom>=0?Math.max(0,budget.oomKills()-lastOom):0;
        lastOom=budget.oomKills();
        long throttled=budget.throttledMicros()>=0&&lastThrottled>=0?Math.max(0,budget.throttledMicros()-lastThrottled):0;
        lastThrottled=budget.throttledMicros();

        WorkTelemetry work=observedWork;
        WorkTelemetry.NativeState state=work==null?null:work.state;
        boolean active=state!=null&&now-state.time()<10_000_000_000L&&taskActivity!=0&&now-taskActivity<10_000_000_000L;
        WorkTelemetry.Batch batch=work==null?new WorkTelemetry.Batch(0,0):work.drain();
        int previous=cap;
        if(work!=previousWork){POLICY.reset(settings,"new-generation-task");previousWork=work;}
        AdaptiveSample sample=new AdaptiveSample(seconds,used,mspt,heap,gcPercent,budget.headroomMb(),batch.completions(),writes,
            comparable,state==null?0:state.width(),state==null?0:state.inFlight(),lod.queue(),state==null?-1:state.worldWrites(),
            sync,state!=null&&state.held(),batch.latencyP95Ms(),oom);

        SHARED_BUDGET.observe(new SharedWorkBudget.Sample(
            now,players,mspt,heap,gcPercent,budget.headroomMb(),budget.memoryPsi(),budget.ioPsi(),
            writes/seconds,batch.completions()/seconds,state==null?0:state.width(),state==null?0:state.inFlight(),
            lod.queue(),state==null?-1:state.worldWrites(),sync,state!=null&&state.held()));

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
            LOG.info("LOD CPU ceiling {} -> {} (effective follow {}); CPU {}% of {} cores, MSPT {}, heap {}%, players {}, reason {} (follow-owned); all LOD saved {}/s ({} in sample), follow requests {}/s ({} in sample), GC {}%, native width {}, in-flight {}, LOD queue {}, phase {}; shared budget [{}]",
                previous,cap,cap(),fmt(used),fmt(capacity),fmt(mspt),fmt(heap),players,POLICY.reason,
                fmt(writes/seconds),writes,fmt(batch.completions()/seconds),batch.completions(),fmt(gcPercent),
                sample.nativeWidth(),sample.inFlight(),sample.lodQueue(),POLICY.phase,SHARED_BUDGET.summary(now,cap));
            lastReport=now;
        }
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

    private static void reload(){
        try{
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
            LOG.info("Loaded {}; CPU budget {} cores, ceiling {}..{}; A/B/A saved-LOD throughput window {}s, settle {}s; shared backfill fairness <= {}s when continuously safe",
                CONFIG,capacity,fresh.min,fresh.max,fresh.windowSeconds,fresh.settleSeconds,fresh.backfillMaxSafeWaitSeconds);
        }catch(Exception e){
            if(configGood)LOG.error("Throughput governor config missing/invalid; limiting generation and blocking new backfill until fixed: {}",e.toString());
            configGood=false;
        }
    }

    private static Object finite(double n){return Double.isFinite(n)?n:null;}
}
