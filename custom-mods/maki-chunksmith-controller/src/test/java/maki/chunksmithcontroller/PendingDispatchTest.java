package maki.chunksmithcontroller;

import java.util.ArrayDeque;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PendingDispatchTest {
    @Test void ownershipFailureRetriesWithoutExecutingNativeMark(){
        var attempts=new AtomicInteger();var retry=new ArrayDeque<Runnable>();var marked=new AtomicInteger();
        var prepared=PendingDispatch.prepare(()->true,()->attempts.incrementAndGet()==1
            ?CompletableFuture.failedFuture(new IllegalStateException("scan busy")):CompletableFuture.completedFuture(null),retry::add);
        var nativeRequest=prepared.thenRun(marked::incrementAndGet);
        assertFalse(nativeRequest.isDone());assertEquals(0,marked.get());
        retry.remove().run();assertTrue(nativeRequest.isDone());assertEquals(1,marked.get());
    }
    @Test void permanentFailureFinishesBeforeWipCanStallTheRange(){
        var retry=new ArrayDeque<Runnable>();var calls=new AtomicInteger();var nativeCalls=new AtomicInteger();
        var first=PendingDispatch.prepare(()->true,()->{calls.incrementAndGet();return CompletableFuture.failedFuture(new IllegalStateException("unreadable coordinate"));},retry::add)
            .thenRun(nativeCalls::incrementAndGet);
        retry.remove().run();retry.remove().run();
        assertTrue(first.isCompletedExceptionally());assertEquals(3,calls.get());assertTrue(retry.isEmpty());
        var next=PendingDispatch.prepare(()->true,()->CompletableFuture.completedFuture(null),retry::add).thenRun(nativeCalls::incrementAndGet);
        assertTrue(next.isDone());assertEquals(1,nativeCalls.get());
    }
    @Test void loginDuringIoDoesNotEnterNativeDispatch(){
        var open=new AtomicBoolean(true);var scan=new CompletableFuture<Void>();var nativeCalls=new AtomicInteger();
        var prepared=PendingDispatch.prepare(open::get,()->scan,Runnable::run);
        var request=prepared.thenRun(nativeCalls::incrementAndGet);
        open.set(false);scan.complete(null);
        assertTrue(request.isCompletedExceptionally());assertEquals(0,nativeCalls.get());
    }
}
