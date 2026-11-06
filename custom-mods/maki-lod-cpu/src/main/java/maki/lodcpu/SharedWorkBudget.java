package maki.lodcpu;

final class SharedWorkBudget {
    record Sample(
        long observedAtNanos,
        int players,
        double cpu,
        double mspt,
        double heap,
        double gc,
        double memoryHeadroomMb,
        double memoryPsi,
        double ioPsi,
        double lodWritesPerSecond,
        double followRequestsPerSecond,
        int nativeWidth,
        int inFlight,
        int lodQueue,
        long worldWriteQueue,
        long synchronousWrites,
        boolean nativeHeld
    ) {}

    record Decision(boolean reserved, boolean ready, String reason, long waitMillis) {}

    private Sample sample;
    private boolean reserved;
    private boolean followDrained;
    private long reservationAtNanos;
    private long workStartedAtNanos;
    private long demandSinceNanos;
    private long nextEligibleAtNanos;
    private long transitionSerial;
    private double averageBackfillMillis=-1.0;
    private String lastReason="warming-up";

    synchronized void observe(Sample next) {
        sample=next;
    }

    synchronized Decision request(long nowNanos,String taskMode,Settings settings) {
        if("external".equals(taskMode)) {
            cancelPending();
            lastReason="external-chunksmith-task";
            return decision(nowNanos,false,false,lastReason);
        }

        String pressure=pressure(nowNanos,settings);
        if(pressure!=null) {
            cancelPending();
            lastReason=pressure;
            return decision(nowNanos,false,false,lastReason);
        }

        if(demandSinceNanos==0L)demandSinceNanos=nowNanos;
        if(!reserved) {
            if(nowNanos<nextEligibleAtNanos) {
                lastReason="dynamic-cooldown";
                return decision(nowNanos,false,false,lastReason);
            }
            reserved=true;
            transitionSerial++;
            reservationAtNanos=nowNanos;
            workStartedAtNanos=0L;
            followDrained="none".equals(taskMode);
        }

        if("none".equals(taskMode))followDrained=true;
        if("follow".equals(taskMode)&&!followDrained) {
            if(elapsedMillis(nowNanos,reservationAtNanos)>=settings.backfillDrainWaitSeconds*1000L) {
                cancelPending();
                nextEligibleAtNanos=nowNanos+settings.backfillMinSpacingSeconds*1_000_000_000L;
                lastReason="follow-drain-timeout";
                return decision(nowNanos,false,false,lastReason);
            }
            lastReason="draining-follow-dispatch";
            return decision(nowNanos,true,false,lastReason);
        }

        lastReason="granted";
        return decision(nowNanos,true,true,lastReason);
    }

    synchronized void noteFollowInFlight(int count) {
        if(reserved&&workStartedAtNanos==0L&&count==0) {
            followDrained=true;
            lastReason="follow-drained";
        }
    }

    synchronized long transitionSerial() {
        return transitionSerial;
    }

    synchronized boolean blockFollowDispatch() {
        // The task itself remains alive. The dispatch thread waits here while
        // current requests drain and during the one admitted backfill.
        return reserved;
    }

    synchronized void backfillStarted(long nowNanos) {
        if(reserved&&followDrained&&workStartedAtNanos==0L) {
            workStartedAtNanos=nowNanos;
            lastReason="backfill-active";
        }
    }

    synchronized void release(long nowNanos,String outcome,Settings settings) {
        if(!reserved)return;

        if(workStartedAtNanos!=0L&&nowNanos>=workStartedAtNanos) {
            double elapsed=(nowNanos-workStartedAtNanos)/1_000_000.0;
            averageBackfillMillis=averageBackfillMillis<0
                ?elapsed
                :averageBackfillMillis*.75+elapsed*.25;
        }

        double measured=averageBackfillMillis<0
            ?settings.backfillMinSpacingSeconds*1000.0
            :averageBackfillMillis;
        double factor=2.0; // follow receives the larger time share by default
        Sample current=sample;
        if(current!=null) {
            boolean abundant=Double.isFinite(current.mspt())&&current.mspt()<25
                &&current.memoryHeadroomMb()>=settings.memoryReserveMb*2
                &&(current.ioPsi()<0||current.ioPsi()<settings.backfillIoPressurePercent*.5)
                &&current.lodQueue()<Math.max(1,settings.backfillLodQueueHigh/2)
                &&current.worldWriteQueue()<Math.max(1,settings.backfillWriteQueueHigh/2);
            if(abundant)factor=1.0;
            if(current.ioPsi()>=0&&current.ioPsi()>=settings.backfillIoPressurePercent*.5)factor*=1.5;
            if(current.followRequestsPerSecond()>0
                &&current.lodWritesPerSecond()<current.followRequestsPerSecond()*.8)factor*=1.5;
        }
        if(outcome!=null&&!outcome.startsWith("completed")&&!outcome.equals("no-candidate"))factor*=2.0;

        long min=settings.backfillMinSpacingSeconds*1000L;
        long max=settings.backfillMaxSafeWaitSeconds*1000L;
        long cooldown=Math.max(min,Math.min(max,Math.round(measured*factor)));
        nextEligibleAtNanos=nowNanos+cooldown*1_000_000L;

        transitionSerial++;
        reserved=false;
        followDrained=false;
        reservationAtNanos=0L;
        workStartedAtNanos=0L;
        demandSinceNanos=0L;
        lastReason="cooldown-after-"+(outcome==null?"release":outcome);
    }

    synchronized boolean reserved(){return reserved;}

    synchronized String summary(long nowNanos) {
        Sample current=sample;
        String p=current==null?"measurement-unavailable":pressure(nowNanos,CpuGovernor.settings());
        return "state="+(reserved?(workStartedAtNanos==0L?"draining":"backfill"):"follow")
            +",reason="+lastReason
            +",safeWaitMs="+elapsedMillis(nowNanos,demandSinceNanos)
            +",drainMs="+elapsedMillis(nowNanos,reservationAtNanos)
            +",avgBackfillMs="+value(averageBackfillMillis)
            +",cooldownRemainingMs="+Math.max(0L,(nextEligibleAtNanos-nowNanos)/1_000_000L)
            +",pressure="+(p==null?"none":p)
            +",cpu="+value(current==null?Double.NaN:current.cpu())
            +",mspt="+value(current==null?Double.NaN:current.mspt())
            +",heap="+value(current==null?Double.NaN:current.heap())
            +",gc="+value(current==null?Double.NaN:current.gc())
            +",memoryHeadroomMb="+value(current==null?Double.NaN:current.memoryHeadroomMb())
            +",memoryPsi="+value(current==null?Double.NaN:current.memoryPsi())
            +",ioPsi="+value(current==null?Double.NaN:current.ioPsi())
            +",lodWritesPerSec="+value(current==null?Double.NaN:current.lodWritesPerSecond())
            +",followRequestsPerSec="+value(current==null?Double.NaN:current.followRequestsPerSecond())
            +",inFlight="+(current==null?-1:current.inFlight())
            +",lodQueue="+(current==null?-1:current.lodQueue())
            +",worldWriteQueue="+(current==null?-1:current.worldWriteQueue());
    }

    private String pressure(long nowNanos,Settings settings) {
        Sample current=sample;
        if(current==null)return "measurement-unavailable";
        long maxAge=settings.backfillSampleMaxAgeSeconds*1_000_000_000L;
        if(current.observedAtNanos()<=0L||nowNanos<current.observedAtNanos()
            ||nowNanos-current.observedAtNanos()>maxAge)return "measurement-stale";
        if(current.players()>0)return "players-online";
        if(!Double.isFinite(current.mspt())||current.mspt()>=settings.highMspt)return "mspt-pressure";
        if(!Double.isFinite(current.heap())||current.heap()>=settings.highHeap)return "heap-pressure";
        if(current.memoryHeadroomMb()>=0&&current.memoryHeadroomMb()<settings.memoryReserveMb)return "memory-headroom";
        if(current.memoryPsi()>=0&&current.memoryPsi()>=settings.backfillMemoryPsiPressurePercent)return "memory-pressure";
        if(Double.isFinite(current.gc())&&current.gc()>=settings.gcPressurePercent)return "gc-pressure";
        if(current.ioPsi()>=0&&current.ioPsi()>=settings.backfillIoPressurePercent)return "io-pressure";
        if(current.nativeHeld()||current.synchronousWrites()>0)return "native-backpressure";
        if(current.lodQueue()>=settings.backfillLodQueueHigh)return "lod-save-queue";
        if(current.worldWriteQueue()>=settings.backfillWriteQueueHigh)return "chunk-write-queue";
        return null;
    }

    private void cancelPending() {
        if(reserved&&workStartedAtNanos==0L) {
            transitionSerial++;
            reserved=false;
            followDrained=false;
            reservationAtNanos=0L;
        }
        demandSinceNanos=0L;
    }

    private Decision decision(long nowNanos,boolean hasReservation,boolean ready,String reason) {
        long wait=demandSinceNanos==0L?0L:elapsedMillis(nowNanos,demandSinceNanos);
        return new Decision(hasReservation,ready,reason,wait);
    }

    private static long elapsedMillis(long now,long then) {
        return then<=0L||now<then?0L:(now-then)/1_000_000L;
    }

    private static String value(double v) {
        return Double.isFinite(v)&&v>=0?String.format(java.util.Locale.ROOT,"%.1f",v):"unknown";
    }
}
