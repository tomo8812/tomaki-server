package maki.chunksmithcontroller;

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

    public int warmStart(Settings s,int storedCap,String why){
        reset(s,why);
        cap=Math.max(s.min,Math.min(s.max,storedCap));
        settle=s.settleSeconds;
        reason=why;
        return cap;
    }

    public int update(AdaptiveSample x,int players,Settings s){
        return update(x,players,s,0,false,false);
    }

    public int update(AdaptiveSample x,int players,Settings s,long pairCompletions,boolean pairAvailable,boolean admissionBlocked){
        return update(x,players,s,pairCompletions,pairAvailable,admissionBlocked,0L);
    }

    public int update(AdaptiveSample x,int players,Settings s,long pairCompletions,boolean pairAvailable,
                      boolean admissionBlocked,long fastSignature){
        return update(x,players,s,pairCompletions,pairAvailable,admissionBlocked,fastSignature,
            pairCompletions,0,0,0);
    }

    public int update(AdaptiveSample x,int players,Settings s,long pairCompletions,boolean pairAvailable,
                      boolean admissionBlocked,long fastSignature,long newPairs,long repairPairs,
                      long uncertainPairs,long oldestWipMillis){
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

        window.add(x,pairCompletions,admissionBlocked,fastSignature,newPairs,repairPairs,uncertainPairs,oldestWipMillis);
        if(!window.ready(s)){reason="measure-"+phase;return cap;}
        Result result=window.result();window=new Window();lastWindowRate=result.rate;
        if(!result.fastStable||(phase.equals("probe")&&baseline!=null&&result.fastSignature!=baseline.fastSignature)){
            if(phase.equals("probe"))cap=baselineCap;
            lastExperiment="inconclusive: FAST WIP state changed";
            experimentNumber++;abandon(s,"fast-state-changed",s.retrySeconds);return cap;
        }
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
        if(Math.abs(result.inFlight-baseline.inFlight)<0.15){
            cap=baselineCap;lastExperiment="c="+baselineCap+" -> "+candidateCap+" inconclusive: actual in-flight unchanged";
            experimentNumber++;abandon(s,"native-dispatch-unchanged",s.retrySeconds);return cap;
        }
        if(result.classifiedEvents>=s.minimumEvents&&baseline.classifiedEvents>=s.minimumEvents
            &&Math.abs(result.newRatio-baseline.newRatio)>.20){
            cap=baselineCap;lastExperiment="c="+baselineCap+" -> "+candidateCap+" inconclusive: NEW/REPAIR mix changed";
            experimentNumber++;abandon(s,"work-class-mix-changed",s.retrySeconds);return cap;
        }
        if(result.sourceProofRate+.05<baseline.sourceProofRate){
            cap=baselineCap;lastExperiment="c="+baselineCap+" -> "+candidateCap+" rejected: source proof rate regressed";
            experimentNumber++;abandon(s,"source-proof-regressed",s.retrySeconds);return cap;
        }
        boolean tailSafe=result.queueGrowth<=Math.max(16,baseline.queueGrowth+16)
            &&result.synchronousWrites<=baseline.synchronousWrites
            &&result.oldestWipMillis<=Math.max(15_000,baseline.oldestWipMillis+5_000);
        boolean rateSafe=lower?result.rate>=baseline.rate*.98
            :result.rate>=baseline.rate*(1+s.minimumGainPercent/100.0);
        boolean quotaSafe=result.throttledMicrosPerSecond
            <=Math.max(baseline.throttledMicrosPerSecond*1.25,baseline.throttledMicrosPerSecond+10_000.0);
        boolean efficiencyBetter=!Double.isFinite(baseline.cpuPerPair)||!Double.isFinite(result.cpuPerPair)
            ||result.cpuPerPair<=baseline.cpuPerPair||result.queueGrowth<baseline.queueGrowth;
        boolean accept=tailSafe&&quotaSafe&&rateSafe&&(!lower||efficiencyBetter);
        cap=accept?candidateCap:baselineCap;
        lastExperiment="c="+baselineCap+" -> "+candidateCap+" pair/s="+round(baseline.rate)+"/"+round(result.rate)
            +" tailSafe="+tailSafe+" quotaSafe="+quotaSafe+" accepted="+accept;
        experimentNumber++;
        if(accept){failedExperiments=0;reason=lower?"same-goodput-lower-c":"pair-goodput-gain";}
        else {
            failedExperiments++;
            reason=!tailSafe?"tail-debt-regressed":!quotaSafe?"cpu-quota-throttle-regressed":"no-pair-goodput-gain";
        }
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

    private record Result(double rate,long events,double width,double inFlight,double cpu,double cpuPerPair,
                          int queueGrowth,long synchronousWrites,double admissionFraction,long fastSignature,
                          boolean fastStable,long classifiedEvents,double newRatio,double sourceProofRate,long oldestWipMillis,
                          double throttledMicrosPerSecond){}
    private static final class Window {
        double seconds,widthSeconds,inFlightSeconds,cpuSeconds,cpuKnownSeconds,blockedSeconds;
        long pairs,synchronousWrites,newPairs,repairPairs,uncertainPairs,oldestWipMillis,throttledMicros;
        int firstQueue=-1,lastQueue;
        long fastSignature=Long.MIN_VALUE;
        boolean fastStable=true;
        void add(AdaptiveSample x,long pairCompletions,boolean admissionBlocked,long signature,
                 long newPairCompletions,long repairPairCompletions,long uncertain,long oldestMillis){
            seconds+=x.seconds();pairs+=Math.max(0,pairCompletions);widthSeconds+=x.nativeWidth()*x.seconds();
            inFlightSeconds+=x.inFlight()*x.seconds();
            newPairs+=Math.max(0,newPairCompletions);repairPairs+=Math.max(0,repairPairCompletions);
            uncertainPairs+=Math.max(0,uncertain);oldestWipMillis=Math.max(oldestWipMillis,Math.max(0,oldestMillis));
            if(Double.isFinite(x.cpu())){cpuSeconds+=x.cpu()*x.seconds();cpuKnownSeconds+=x.seconds();}
            synchronousWrites+=x.synchronousWrites();
            throttledMicros+=Math.max(0,x.throttledMicros());
            if(firstQueue<0)firstQueue=x.lodQueue();lastQueue=x.lodQueue();
            if(admissionBlocked)blockedSeconds+=x.seconds();
            if(fastSignature==Long.MIN_VALUE)fastSignature=signature;
            else if(fastSignature!=signature)fastStable=false;
        }
        boolean ready(Settings s){return seconds>=s.windowSeconds&&(pairs>=s.minimumEvents||seconds>=3.0*s.windowSeconds);}
        Result result(){
            long classified=newPairs+repairPairs;
            double proofDenominator=pairs+uncertainPairs;
            return new Result(seconds>0?pairs/seconds:0,pairs,seconds>0?widthSeconds/seconds:0,
                seconds>0?inFlightSeconds/seconds:0,cpuKnownSeconds>0?cpuSeconds/cpuKnownSeconds:Double.NaN,
                pairs>0&&cpuKnownSeconds>0?cpuSeconds/pairs:Double.NaN,lastQueue-firstQueue,synchronousWrites,
                seconds>0?blockedSeconds/seconds:0,fastSignature==Long.MIN_VALUE?0L:fastSignature,fastStable,
                classified,classified>0?(double)newPairs/classified:0,proofDenominator>0?pairs/proofDenominator:1.0,oldestWipMillis,
                seconds>0?throttledMicros/seconds:0);
        }
    }
}
