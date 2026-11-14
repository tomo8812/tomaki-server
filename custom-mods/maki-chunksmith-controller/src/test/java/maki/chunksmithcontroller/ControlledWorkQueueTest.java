package maki.chunksmithcontroller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import maki.chunksmithfixes.GenerationWorkEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ControlledWorkQueueTest {
    @Test void jobsRunWithinCapacityAndCompletionReleasesSlot(){
        var queue=new ControlledWorkQueue();var first=new CompletableFuture<Void>();var second=new CompletableFuture<Void>();
        queue.submit(new GenerationWorkEvent(()->first));queue.submit(new GenerationWorkEvent(()->second));
        queue.tick(1,true);assertEquals(1,queue.running());assertEquals(1,queue.waiting());
        first.complete(null);queue.tick(1,true);assertEquals(1,queue.running());assertEquals(0,queue.waiting());
        second.complete(null);assertEquals(0,queue.running());
    }
    @Test void resourcePressureHoldsWorkUntilBudgetRecovers(){
        var queue=new ControlledWorkQueue();var started=new AtomicInteger();
        queue.submit(new GenerationWorkEvent(()->{started.incrementAndGet();return CompletableFuture.completedFuture(null);}));
        queue.tick(1,false);assertEquals(0,started.get());queue.tick(1,true);assertEquals(1,started.get());
    }
    @Test void failedWorkIsNotRetriedOrRememberedByController(){
        var queue=new ControlledWorkQueue();var started=new AtomicInteger();
        queue.submit(new GenerationWorkEvent(()->{started.incrementAndGet();return CompletableFuture.failedFuture(new IllegalStateException("producer failure"));}));
        queue.tick(1,true);queue.tick(1,true);assertEquals(1,started.get());assertEquals(0,queue.running());assertEquals(0,queue.waiting());
    }
    @Test void synchronousCompletionsDoNotDrainUnboundedWorkInOneTick(){
        var queue=new ControlledWorkQueue();for(int i=0;i<5;i++)queue.submit(new GenerationWorkEvent(()->CompletableFuture.completedFuture(null)));
        queue.tick(2,true);assertEquals(3,queue.waiting());assertEquals(0,queue.running());
    }
    @Test void lateCompletionFromStoppedServerDoesNotReleaseNewServerSlot(){
        var queue=new ControlledWorkQueue();var old=new CompletableFuture<Void>();var current=new CompletableFuture<Void>();
        queue.submit(new GenerationWorkEvent(()->old));queue.tick(1,true);queue.clear();
        queue.submit(new GenerationWorkEvent(()->current));queue.tick(1,true);old.complete(null);assertEquals(1,queue.running());
        current.complete(null);assertEquals(0,queue.running());
    }
}
