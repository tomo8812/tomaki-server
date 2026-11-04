package maki.lodcpu;

/** Bounded A/B/A experiments on persisted LOD throughput. No Minecraft or OS calls. */
public final class AdaptivePolicy {
    public int cap=2;
    public String reason="warmup", phase="baseline", lastExperiment="none";
    public double lastWindowRate, baselineRate, probeRate, confirmedBaselineRate;
    public long experimentNumber;
    private Window window=new Window();
    private double settle, cooldown, sinceReduction=1000, withoutProgress;
    private int pressureSamples, baselineCap, candidateCap, failedExperiments;
    private boolean tryLower;
    private Result baseline, probe, confirmation;

    public int reset(Settings s,String why){
        cap=Math.max(s.min,Math.min(s.max,2));reason=why;phase="baseline";
        window=new Window();settle=s.settleSeconds;cooldown=0;pressureSamples=0;
        baseline=probe=null;tryLower=false;withoutProgress=0;failedExperiments=0;
        return cap;
    }
    public int unavailable(Settings s,String why){reset(s,why);cap=1;return cap;}

    public int update(AdaptiveSample x,int players,Settings s){
        cap=Math.max(s.min,Math.min(s.max,cap));
        if(players>0){reset(s,"players-online-paused");cap=1;return cap;}
        if(!Double.isFinite(x.seconds())||x.seconds()<=0)return cap;
        double dt=x.seconds();sinceReduction+=dt;cooldown=Math.max(0,cooldown-dt);
        // An unavailable CPU counter alone does not erase valid throughput/memory data.
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
                int drop=hard?Math.max(1,cap/2):Math.max(1,cap/4);
                cap=Math.max(s.min,cap-drop);sinceReduction=0;pressureSamples=0;
                abandon(s,"pressure-recovery",s.retrySeconds);
            }else {
                if(!phase.equals("baseline"))cap=Math.min(cap,baselineCap);
                window=new Window();baseline=probe=null;phase="baseline";settle=s.settleSeconds;
            }
            reason=hard?"emergency-"+(pressure.isEmpty()?"heap":pressure):pressure;
            return cap;
        }
        pressureSamples=0;
        if(x.storeAvailable()?x.lodWrites()>0:x.completions()>0)withoutProgress=0;
        else if(x.inFlight()>0||x.completions()>0)withoutProgress+=dt;
        else withoutProgress=0;
        if(withoutProgress>=s.noProgressSeconds&&sinceReduction>=10){
            cap=Math.max(s.min,cap-Math.max(1,cap/2));sinceReduction=0;withoutProgress=0;
            abandon(s,"no-progress",s.retrySeconds);return cap;
        }
        if(cooldown>0){reason="observe-cooldown";return cap;}
        if(settle>0){settle-=dt;reason="settling-"+phase;return cap;}
        // Do not attribute already dispatched work at the old width to the new width.
        if(x.inFlight()>cap+1){reason="draining-old-width";return cap;}
        window.add(x);
        if(window.seconds<s.windowSeconds || (window.events()<s.minimumEvents && window.seconds<3*s.windowSeconds)){
            reason="measure-"+phase;return cap;
        }
        Result r=window.result();window=new Window();lastWindowRate=r.rate;
        if(r.rate<=0){abandon(s,"waiting-for-output",s.retrySeconds);return cap;}
        if(phase.equals("baseline")){
            baseline=r;baselineRate=r.rate;baselineCap=cap;
            if(cap==s.max)tryLower=true;
            if(cap==s.min)tryLower=false;
            // CPU only changes experiment size. Saturation never requests a decrease.
            int step=Math.max(1,Math.min(s.maxStep,cap/2));
            if(Double.isFinite(r.cpu)&&r.cpu<70&&!tryLower)step=Math.max(step,Math.min(s.maxStep,cap));
            candidateCap=tryLower?Math.max(s.min,cap-step):Math.min(s.max,cap+step);
            if(candidateCap==cap){abandon(s,"at-configured-bound",s.retrySeconds);return cap;}
            cap=candidateCap;phase="probe";settle=s.settleSeconds;reason="probe-width";
        }else if(phase.equals("probe")){
            probe=r;probeRate=r.rate;cap=baselineCap;phase="confirm";settle=s.settleSeconds;reason="confirm-baseline";
        }else{
            boolean repeated=phase.equals("repeat");
            if(!repeated)confirmation=r;
            confirmedBaselineRate=confirmation.rate;
            double reference=(baseline.rate+confirmation.rate)/2;
            boolean terrainStable=Math.max(baseline.rate,confirmation.rate)<=1.35*Math.min(baseline.rate,confirmation.rate);
            boolean delivered=probe.width>=candidateCap*.8;
            boolean queuesStable=probe.queueGrowth<=64&&probe.synchronousWrites==0;
            boolean sameBasis=baseline.store==probe.store&&probe.store==confirmation.store && probe.store==r.store;
            boolean lower=candidateCap<baselineCap;
            boolean faster=probe.rate>=reference*(lower?.98:1+s.minimumGainPercent/100);
            double strongRate=Math.max(baseline.rate,confirmation.rate)*(1+s.minimumGainPercent/100);
            boolean promising=!lower && delivered && queuesStable && sameBasis && probe.rate>=strongRate;
            if(!terrainStable && promising && !repeated){
                cap=candidateCap;
                phase="repeat";
                settle=s.settleSeconds;
                reason="repeat-promising-width";
                return cap;
            }
            boolean repeatPassed=repeated && promising && r.rate>=strongRate
                && r.width>=candidateCap*.8 && r.queueGrowth<=64 && r.synchronousWrites==0;
            boolean accept=repeatPassed || (terrainStable&&delivered&&queuesStable&&sameBasis&&faster);
            cap=accept?candidateCap:baselineCap;
            lastExperiment="A="+baselineCap+" B="+candidateCap+" rates="+round(baseline.rate)+"/"+round(probe.rate)+"/"+round(confirmation.rate)+(repeated?"/"+round(r.rate):"")+" accepted="+accept;
            experimentNumber++;
            if(accept){tryLower=false;failedExperiments=0;reason=lower?"same-speed-lower-width":"throughput-gain";}
            else {
                tryLower=!tryLower;
                reason=!sameBasis?"output-source-changed":repeated?"repeat-not-confirmed":!terrainStable?"baseline-varied":!delivered?"native-width-limited":!queuesStable?"storage-backlog":"no-throughput-gain";
                failedExperiments++;
                cooldown=Math.min(600,s.retrySeconds*Math.pow(2,Math.min(3,failedExperiments/2)));
            }
            phase="baseline";baseline=probe=null;settle=s.settleSeconds;
        }
        return cap;
    }
    public double retryRemainingSeconds(){return cooldown;}
    private void abandon(Settings s,String why,double wait){
        if(!phase.equals("baseline"))cap=Math.min(cap,baselineCap);
        phase="baseline";window=new Window();baseline=probe=null;settle=s.settleSeconds;
        cooldown=wait;reason=why;
    }
    private static double round(double v){return Math.rint(v*1000)/1000;}
    private record Result(double rate,double width,double cpu,boolean store,int queueGrowth,long synchronousWrites){}
    private static final class Window {
        double seconds,widthSeconds,cpuSeconds,cpuKnownSeconds;long complete,writes,synchronousWrites;
        boolean store=true;int firstQueue=-1,lastQueue;
        void add(AdaptiveSample x){
            seconds+=x.seconds();widthSeconds+=x.nativeWidth()*x.seconds();
            if(Double.isFinite(x.cpu())){cpuSeconds+=x.cpu()*x.seconds();cpuKnownSeconds+=x.seconds();}
            complete+=x.completions();writes+=x.lodWrites();store&=x.storeAvailable();
            synchronousWrites+=x.synchronousWrites();if(firstQueue<0)firstQueue=x.lodQueue();lastQueue=x.lodQueue();
        }
        // A burst of rewrites or a draining writer queue is not new generation speed.
        // Require both completed chunk requests and successful store writes.
        long events(){return store?Math.min(writes,complete):complete;}
        Result result(){return new Result(events()/seconds,widthSeconds/seconds,cpuKnownSeconds>0?cpuSeconds/cpuKnownSeconds:Double.NaN,store,lastQueue-firstQueue,synchronousWrites);}
    }
}
