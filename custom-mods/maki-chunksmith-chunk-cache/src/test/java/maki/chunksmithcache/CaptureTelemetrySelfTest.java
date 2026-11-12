package maki.chunksmithcache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public final class CaptureTelemetrySelfTest {
    static final class Events implements CaptureTelemetry.Events {
        final List<String> log=java.util.Collections.synchronizedList(new ArrayList<>());
        public void started(String stage){log.add("start:"+stage);}
        public void finished(String stage,long nanos,boolean success){log.add("finish:"+stage+":"+success);}
        public void failed(){log.add("failed");}
    }
    public static void main(String[] args)throws Exception{
        Events queued=new Events();var expired=new CompletableFuture<Boolean>();
        var telemetry=new CaptureTelemetry(expired,queued);
        expired.complete(false);
        require(!telemetry.start("FULL")&&queued.log.equals(List.of("failed")),"expired queued work never physically starts");
        Events started=new Events();var timeout=new CompletableFuture<Boolean>();
        var active=new CaptureTelemetry(timeout,started);
        require(active.start("VALIDATE"),"validation starts");
        timeout.complete(false);
        active.finish("VALIDATE",100,true);
        require(started.log.equals(List.of("start:VALIDATE","failed","finish:VALIDATE:true")),"logical timeout keeps physical terminal observable");
        require(!active.start("FULL"),"late validation cannot enqueue physical FULL after timeout");
        for(int i=0;i<200;i++){
            Events events=new Events();var result=new CompletableFuture<Boolean>();
            var concurrent=new CaptureTelemetry(result,events);var gate=new CountDownLatch(1);
            Thread start=new Thread(()->{await(gate);concurrent.start("FULL");});
            Thread fail=new Thread(()->{await(gate);result.complete(false);});
            start.start();fail.start();gate.countDown();start.join();fail.join();
            require(!events.log.equals(List.of("failed","start:FULL")),"timeout cannot return credit before a physical start");
        }
        System.out.println("CaptureTelemetrySelfTest PASS (queued timeout, late ACK, start/timeout races)");
    }
    private static void await(CountDownLatch gate){try{gate.await();}catch(InterruptedException error){throw new AssertionError(error);}}
    private static void require(boolean ok,String reason){if(!ok)throw new AssertionError(reason);}
}
