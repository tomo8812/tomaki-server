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
    @Test void loginDuringIoDoesNotEnterNativeDispatch(){
        var open=new AtomicBoolean(true);var scan=new CompletableFuture<Void>();var nativeCalls=new AtomicInteger();
        var prepared=PendingDispatch.prepare(open::get,()->scan,Runnable::run);
        var request=prepared.thenRun(nativeCalls::incrementAndGet);
        open.set(false);scan.complete(null);
        assertTrue(request.isCompletedExceptionally());assertEquals(0,nativeCalls.get());
    }
}
