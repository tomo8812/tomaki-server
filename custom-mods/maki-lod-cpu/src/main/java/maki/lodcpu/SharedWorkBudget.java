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

    record Decision(boolean reserved, boolean ready, String reason, long waitMillis, int completedInEpoch) {}

    private Sample sample;
    private boolean exclusive;
    private long reservationAtNanos;
    private long epochStartedAtNanos;
    private long followSliceUntilNanos;
    private int completedInEpoch;
    private String lastReason="warming-up";

    synchronized void observe(Sample next) {
        sample=next;
    }

    synchronized Decision request(long nowNanos,String taskMode,Settings settings) {
        if("external".equals(taskMode)) {
            releaseInternal(nowNanos,settings,false);
            lastReason="external-chunksmith-task";
            return decision(nowNanos,false,false,lastReason);
        }

        if(!exclusive && nowNanos<followSliceUntilNanos) {
            lastReason="follow-min-slice";
            return decision(nowNanos,false,false,lastReason);
        }

        if(!exclusive) {
            exclusive=true;
            reservationAtNanos=nowNanos;
            epochStartedAtNanos=0L;
            completedInEpoch=0;
        }

        // A follow-owned task must drain completely before backfill receives the machine.
        if("follow".equals(taskMode)) {
            lastReason="pausing-follow";
            return decision(nowNanos,true,false,lastReason);
        }

        String pressure=pressure(nowNanos,settings);
        if(pressure!=null) {
            long wait=elapsedMillis(nowNanos,reservationAtNanos);
            if(wait>=settings.backfillDrainWaitSeconds*1000L) {
                releaseInternal(nowNanos,settings,true);
                lastReason="pressure-timeout-"+pressure;
                return decision(nowNanos,false,false,lastReason);
            }
            lastReason="waiting-"+pressure;
            return decision(nowNanos,true,false,lastReason);
        }

        if(epochStartedAtNanos==0L)epochStartedAtNanos=nowNanos;
        lastReason="granted";
        return decision(nowNanos,true,true,lastReason);
    }

    synchronized boolean completed(long nowNanos,String outcome,Settings settings) {
        if(!exclusive)return false;
        completedInEpoch++;

        String pressure=pressure(nowNanos,settings);
        if(pressure!=null) {
            releaseInternal(nowNanos,settings,true);
            lastReason="yield-"+pressure;
            return false;
        }

        if(outcome==null||!outcome.startsWith("completed")) {
            releaseInternal(nowNanos,settings,true);
            lastReason="yield-"+(outcome==null?"unknown-outcome":outcome);
            return false;
        }

        int chunkLimit=dynamicChunkLimit(settings);
        long timeLimitMillis=dynamicTimeLimitMillis(settings);
        long elapsed=elapsedMillis(nowNanos,epochStartedAtNanos);
        if(completedInEpoch>=chunkLimit||elapsed>=timeLimitMillis) {
            releaseInternal(nowNanos,settings,true);
            lastReason=completedInEpoch>=chunkLimit?"yield-chunk-quota":"yield-time-quota";
            return false;
        }

        lastReason="continue-exclusive";
        return true;
    }

    synchronized void release(long nowNanos,String outcome,Settings settings,boolean giveFollowSlice) {
        releaseInternal(nowNanos,settings,giveFollowSlice);
        lastReason="released-"+(outcome==null?"unspecified":outcome);
    }

    synchronized boolean reserved(){return exclusive;}

    synchronized String summary(long nowNanos) {
        Sample current=sample;
        String p=current==null?"measurement-unavailable":pressure(nowNanos,CpuGovernor.settings());
        return "state="+(exclusive?(epochStartedAtNanos==0L?"draining":"exclusive"):"follow")
            +",reason="+lastReason
            +",completedInEpoch="+completedInEpoch
            +",epochMs="+elapsedMillis(nowNanos,epochStartedAtNanos)
            +",followSliceRemainingMs="+Math.max(0L,(followSliceUntilNanos-nowNanos)/1_000_000L)
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
        if(Double.isFinite(current.gc())&&current.gc()>=settings.gcPressurePercent)return "gc-pressure";
        if(current.ioPsi()>=0&&current.ioPsi()>=settings.backfillIoPressurePercent)return "io-pressure";
        if(current.nativeHeld()||current.synchronousWrites()>0)return "native-backpressure";
        if(current.lodQueue()>=settings.backfillLodQueueHigh)return "lod-save-queue";
        if(current.worldWriteQueue()>=settings.backfillWriteQueueHigh)return "chunk-write-queue";
        return null;
    }

    private int dynamicChunkLimit(Settings settings) {
        int limit=settings.backfillExclusiveMaxChunks;
        if(abundant(settings))limit=Math.min(1024,limit*2);
        return limit;
    }

    private long dynamicTimeLimitMillis(Settings settings) {
        long limit=settings.backfillExclusiveMaxSeconds*1000L;
        if(abundant(settings))limit=Math.min(3_600_000L,limit*2L);
        return limit;
    }

    private boolean abundant(Settings settings) {
        Sample current=sample;
        return current!=null
            &&Double.isFinite(current.cpu())&&current.cpu()<60
            &&Double.isFinite(current.mspt())&&current.mspt()<25
            &&current.memoryHeadroomMb()>=settings.memoryReserveMb*2
            &&(current.ioPsi()<0||current.ioPsi()<settings.backfillIoPressurePercent*.5)
            &&current.lodQueue()<Math.max(1,settings.backfillLodQueueHigh/2)
            &&current.worldWriteQueue()<Math.max(1,settings.backfillWriteQueueHigh/2);
    }

    private void releaseInternal(long nowNanos,Settings settings,boolean giveFollowSlice) {
        exclusive=false;
        reservationAtNanos=0L;
        epochStartedAtNanos=0L;
        completedInEpoch=0;
        if(giveFollowSlice)followSliceUntilNanos=nowNanos+settings.backfillFollowMinSliceSeconds*1_000_000_000L;
    }

    private Decision decision(long nowNanos,boolean reserved,boolean ready,String reason) {
        long wait=exclusive?elapsedMillis(nowNanos,reservationAtNanos):Math.max(0L,(followSliceUntilNanos-nowNanos)/1_000_000L);
        return new Decision(reserved,ready,reason,wait,completedInEpoch);
    }

    private static long elapsedMillis(long now,long then) {
        return then<=0L||now<then?0L:(now-then)/1_000_000L;
    }

    private static String value(double v) {
        return Double.isFinite(v)?String.format(java.util.Locale.ROOT,"%.1f",v):"unknown";
    }
}
