package maki.lodcpu;

import java.util.concurrent.atomic.AtomicLong;

/** Bounded, task-local telemetry. No extra tickets, chunk loads or storage writes. */
public final class WorkTelemetry {
    private static final long[] BOUNDS={1,4,16,64,256,1000,4000,16000,60000,Long.MAX_VALUE};
    private final long[] histogram=new long[BOUNDS.length];
    private long completions;
    private final AtomicLong observed=new AtomicLong();
    public volatile NativeState state=new NativeState(0,0,0,-1,false,false,false,-1);
    public boolean due(){
        long now=System.nanoTime(),prev=observed.get();
        return (prev==0||now-prev>=250_000_000L)&&observed.compareAndSet(prev,now);
    }
    public void observe(int width,int inFlight,long writes,boolean writeHeld,boolean residencyHeld,boolean heapHeld,long added){
        state=new NativeState(System.nanoTime(),width,inFlight,writes,writeHeld,residencyHeld,heapHeld,added);
    }
    public synchronized void completed(long millis){
        completions++;int i=0;while(i<BOUNDS.length-1&&millis>BOUNDS[i])i++;histogram[i]++;
    }
    public synchronized Batch drain(){
        long count=completions;completions=0;long need=(long)Math.ceil(count*.95),sum=0,p95=0;
        for(int i=0;i<histogram.length;i++){sum+=histogram[i];if(p95==0&&need>0&&sum>=need)p95=BOUNDS[i];histogram[i]=0;}
        return new Batch(count,p95);
    }
    public record NativeState(long time,int width,int inFlight,long worldWrites,boolean writeHeld,boolean residencyHeld,boolean heapHeld,long addedChunks){
        public boolean held(){return writeHeld||residencyHeld||heapHeld;}
    }
    public record Batch(long completions,double latencyP95Ms){}
}
