package maki.chunksmithcache;

/** Percentiles are bucket upper bounds, not interpolated measurements. */
final class LatencyHistogram {
    private static final long[] BOUNDS = {1,4,16,64,256,1000,4000,16000,60000,Long.MAX_VALUE};
    private final long[] buckets = new long[BOUNDS.length];
    private long count, maxNanos, overSecond;
    void record(long nanos) {
        nanos = Math.max(0, nanos);
        int i = 0;
        while (i < BOUNDS.length-1 && nanos > BOUNDS[i]*1_000_000L) i++;
        buckets[i]++; count++; maxNanos = Math.max(maxNanos, nanos);
        if (nanos > 1_000_000_000L) overSecond++;
    }
    long percentile(double fraction) {
        if (count == 0) return 0;
        long need = (long)Math.ceil(count*fraction), sum = 0;
        for (int i=0;i<buckets.length;i++) if ((sum+=buckets[i])>=need) return BOUNDS[i];
        return Long.MAX_VALUE;
    }
    @Override public String toString() {
        return "n="+count+",p50Upper="+percentile(.5)+",p95Upper="+percentile(.95)
            +",max="+maxNanos/1_000_000.0+",over1000="+overSecond;
    }
}
