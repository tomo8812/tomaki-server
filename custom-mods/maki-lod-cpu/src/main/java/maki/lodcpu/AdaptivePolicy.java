package maki.lodcpu;

/**
 * Low-frequency neighbourhood search for Chunksmith concurrency c. Throughput
 * observations are confirmed runtime FULL+LOD PAIRs only; downstream FAST WIP
 * control remains active and identical at every tested c.
 */
public final class AdaptivePolicy {
    public int cap=2;
    public String reason="warmup",phase="baseline",lastExperiment="none";
    public double lastWindowRate,baselineRate,probeRate,confirmedBaselineRate;
    public long experimentNumber;
    private Window window=new Window();
    private double settle,cooldown,sinceReduction=1000,withoutProgress;
    private int pressureSamples,baselineCap,candidateCap,failedExperiments;
    private Result baseline;

    public int reset(Settings s,String why){
        cap=Math.max(s.min,Math.min(s.max,2));reason=why;phase="baseline";
        window=new Window();settle=s.settleSeconds;cooldown=0;pressureSamples=0;
        baseline=null;withoutProgress=0;failedExperiments=0;
        return cap;
    }
    public int unavailable(Settings s,String why){reset(s,why);cap=1;return cap;}

    public int update(AdaptiveSample x,int players,Settings s){
        return update(x,players,s,0,false,false);
    }

    public int update(AdaptiveSample x,int players,Settings s,long pairCompletions,boolean pairAvailable,boolean admissionBlocked){
        cap=Math.max(s.min,Math.min(s.max,cap));
        if(players>0){reset(s,"players-online-paused");cap=1;return cap;}
        if(!Double.isFinite(x.seconds())||x.seconds()<=0)return cap;
        double dt=x.seconds();sinceReduction+=dt;cooldown=Math.max(0,cooldown-dt);
        if(!Double.isFinite(x.mspt())||!Double.isFinite(x.heap()))return unavailable(s,"measurement-unavailable");
        boolean hard=x.mspt()>=s.emergencyMspt||x.heap()>=90||x.oomKills()>0;
        String pressure=x.oomKills()>0?"cgroup-oom":x.mspt()>=s.highMspt?"tick-pressure":
            x.heap()>=s.highHeap?"heap-pressure":
            x.memoryHeadroomMb()>=0&&x.memoryHeadroomMb()<s.memoryReserveMb?"memory-headroom":
            x.gc()>=50||(x.gc()>=s.gcPressurePercent&&x.heap()>=55)?"gc-pressure":
            x.nativeHeld()||x.synchronousWrites()>0?"native-backpressure":"";
        if(hard||!pressure.isEmpty()){
            pressureSamples++;
            if(hard||(pressureSamples>=3&&sinceReduction>=10)){
                int drop=hard?Math.max(1,cap/2):1;
                cap=Math.max(s.min,cap-drop);sinceReduction=0;pressureSamples=0;
                abandon(s,"pressure-recovery",s.retrySeconds);
            }else if(!phase.equals("baseline")&&candidateCap>baselineCap){
                cap=baselineCap;phase="baseline";window=new Window();baseline=null;settle=s.settleSeconds;
            }
            reason=hard?"emergency-"+(pressure.isEmpty()?"heap":pressure):pressure;
            return cap;
        }
        pressureSamples=0;

        if(!pairAvailable){
            window=new Window();withoutProgress=0;reason="pair-proof-unavailable";
            return cap;
        }
        if(pairCompletions>0)withoutProgress=0;
        else if(x.inFlight()>0||x.completions()>0)withoutProgress+=dt;
        else withoutProgress=0;
        if(withoutProgress>=s.noProgressSeconds&&sinceReduction>=10){
            cap=Math.max(s.min,cap-1);sinceReduction=0;withoutProgress=0;
            abandon(s,"no-pair-progress",s.retrySeconds);return cap;
        }
        if(cooldown>0){reason="observe-cooldown";return cap;}
        if(settle>0){settle-=dt;reason="settling-"+phase;return cap;}
        if(x.inFlight()>cap+1){reason="draining-old-width";return cap;}

        if(phase.equals("probe")&&candidateCap>baselineCap&&admissionBlocked){
            cap=baselineCap;lastExperiment="c="+baselineCap+" -> "+candidateCap+" inconclusive: downstream WIP constrained";
            experimentNumber++;failedExperiments++;abandon(s,"trial-downstream-constrained",s.retrySeconds);return cap;
        }

        window.add(x,pairCompletions,admissionBlocked);
        if(!window.ready(s)){reason="measure-"+phase;return cap;}
        Result result=window.result();window=new Window();lastWindowRate=result.rate;
        if(result.events<s.minimumEvents||result.rate<=0){
            if(phase.equals("probe"))cap=baselineCap;
            lastExperiment="insufficient confirmed PAIR samples at c="+cap;
            experimentNumber++;abandon(s,"insufficient-pair-samples",s.retrySeconds);return cap;
        }

        if(phase.equals("baseline")){
            baseline=result;baselineRate=result.rate;confirmedBaselineRate=result.rate;baselineCap=cap;
            boolean storageBound=result.admissionFraction>=.25||result.synchronousWrites>0||result.queueGrowth>16||x.nativeHeld();
            boolean capBound=result.width>=Math.max(1.0,cap*.8);
            if(storageBound&&cap>s.min)candidateCap=cap-1;
            else if(!storageBound&&cap<s.max&&capBound)candidateCap=cap+1;
            else {
                reason=storageBound?"storage-bound-at-floor":"stable-not-cap-bound";
                cooldown=s.retrySeconds;baseline=null;return cap;
            }
            cap=candidateCap;phase="probe";settle=s.settleSeconds;reason=candidateCap>baselineCap?"probe-c-plus-one":"probe-c-minus-one";
            return cap;
        }

        probeRate=result.rate;
        boolean lower=candidateCap<baselineCap;
        boolean tailSafe=result.queueGrowth<=Math.max(16,baseline.queueGrowth+16)
            &&result.synchronousWrites<=baseline.synchronousWrites;
        boolean rateSafe=lower?result.rate>=baseline.rate*.98
            :result.rate>=baseline.rate*(1+s.minimumGainPercent/100.0);
        boolean efficiencyBetter=!Double.isFinite(baseline.cpu)||!Double.isFinite(result.cpu)
            ||result.cpu<=baseline.cpu||result.queueGrowth<baseline.queueGrowth;
        boolean accept=tailSafe&&rateSafe&&(!lower||efficiencyBetter);
        cap=accept?candidateCap:baselineCap;
        lastExperiment="c="+baselineCap+" -> "+candidateCap+" pair/s="+round(baseline.rate)+"/"+round(result.rate)
            +" tailSafe="+tailSafe+" accepted="+accept;
        experimentNumber++;
        if(accept){failedExperiments=0;reason=lower?"same-goodput-lower-c":"pair-goodput-gain";}
        else {failedExperiments++;reason=tailSafe?"no-pair-goodput-gain":"tail-debt-regressed";}
        phase="baseline";baseline=null;settle=s.settleSeconds;
        cooldown=Math.min(600,s.retrySeconds*Math.pow(2,Math.min(3,failedExperiments/2)));
        return cap;
    }

    /**
     * Backfill's exclusive slice is intentionally excluded from c learning. Only
     * hard safety may reduce the cap while that barrier is held.
     */
    public int updateSafetyOnly(AdaptiveSample x,int players,Settings s){
        cap=Math.max(s.min,Math.min(s.max,cap));
        if(players>0){reset(s,"players-online-paused");cap=1;return cap;}
        if(!Double.isFinite(x.seconds())||x.seconds()<=0)return cap;
        sinceReduction+=x.seconds();
        if(!Double.isFinite(x.mspt())||!Double.isFinite(x.heap())){reason="shared-backfill-measurement-unavailable";return cap;}
        boolean hard=x.mspt()>=s.emergencyMspt||x.heap()>=90||x.oomKills()>0;
        String pressure=x.oomKills()>0?"cgroup-oom":x.mspt()>=s.highMspt?"tick-pressure":
            x.heap()>=s.highHeap?"heap-pressure":
            x.memoryHeadroomMb()>=0&&x.memoryHeadroomMb()<s.memoryReserveMb?"memory-headroom":
            x.gc()>=50||(x.gc()>=s.gcPressurePercent&&x.heap()>=55)?"gc-pressure":
            x.nativeHeld()||x.synchronousWrites()>0?"native-backpressure":"";
        if(hard||!pressure.isEmpty()){
            pressureSamples++;
            if(hard||(pressureSamples>=3&&sinceReduction>=10)){
                int drop=hard?Math.max(1,cap/2):1;
                cap=Math.max(s.min,cap-drop);sinceReduction=0;pressureSamples=0;
                abandon(s,"pressure-recovery",s.retrySeconds);
            }
            reason=hard?"emergency-"+(pressure.isEmpty()?"heap":pressure):pressure;
            return cap;
        }
        pressureSamples=0;withoutProgress=0;reason="shared-backfill-pause";return cap;
    }

    public double retryRemainingSeconds(){return cooldown;}
    private void abandon(Settings s,String why,double wait){
        if(!phase.equals("baseline")&&baseline!=null)cap=Math.min(cap,baselineCap);
        phase="baseline";window=new Window();baseline=null;settle=s.settleSeconds;cooldown=wait;reason=why;
    }
    private static double round(double v){return Math.rint(v*1000)/1000;}

    private record Result(double rate,long events,double width,double cpu,int queueGrowth,long synchronousWrites,double admissionFraction){}
    private static final class Window {
        double seconds,widthSeconds,cpuSeconds,cpuKnownSeconds,blockedSeconds;
        long pairs,synchronousWrites;
        int firstQueue=-1,lastQueue;
        void add(AdaptiveSample x,long pairCompletions,boolean admissionBlocked){
            seconds+=x.seconds();pairs+=Math.max(0,pairCompletions);widthSeconds+=x.nativeWidth()*x.seconds();
            if(Double.isFinite(x.cpu())){cpuSeconds+=x.cpu()*x.seconds();cpuKnownSeconds+=x.seconds();}
            synchronousWrites+=x.synchronousWrites();
            if(firstQueue<0)firstQueue=x.lodQueue();lastQueue=x.lodQueue();
            if(admissionBlocked)blockedSeconds+=x.seconds();
        }
        boolean ready(Settings s){return seconds>=s.windowSeconds&&(pairs>=s.minimumEvents||seconds>=3.0*s.windowSeconds);}
        Result result(){return new Result(seconds>0?pairs/seconds:0,pairs,seconds>0?widthSeconds/seconds:0,
            cpuKnownSeconds>0?cpuSeconds/cpuKnownSeconds:Double.NaN,lastQueue-firstQueue,synchronousWrites,
            seconds>0?blockedSeconds/seconds:0);}
    }
}
