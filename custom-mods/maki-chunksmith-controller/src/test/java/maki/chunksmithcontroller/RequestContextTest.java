package maki.chunksmithcontroller;

import java.util.ArrayDeque;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RequestContextTest {
    @Test void delayedSafePointsRetainDistinctRequestTokensForSameCoordinate(){
        var queue=new ArrayDeque<Runnable>();
        var old=new RequestContext.Request("old","minecraft:overworld",4,7);
        var fresh=new RequestContext.Request("fresh","minecraft:overworld",4,7);
        RequestContext.with(old,()->{
            var executor=RequestContext.wrap(queue::add);
            executor.execute(()->assertEquals("old",RequestContext.token("minecraft:overworld",4,7)));
            return null;
        });
        RequestContext.with(fresh,()->{
            var executor=RequestContext.wrap(queue::add);
            executor.execute(()->assertEquals("fresh",RequestContext.token("minecraft:overworld",4,7)));
            queue.remove().run();
            assertEquals(fresh,RequestContext.current());
            return null;
        });
        assertNull(RequestContext.current());
        queue.remove().run();assertNull(RequestContext.current());
    }
    @Test void exceptionAndNestedUnownedWorkRestoreOuterRequest(){
        var request=new RequestContext.Request("one","minecraft:overworld",1,2);
        RequestContext.with(request,()->{
            assertThrows(IllegalStateException.class,()->RequestContext.with(null,()->{
                assertNull(RequestContext.current());throw new IllegalStateException("test");
            }));
            assertEquals(request,RequestContext.current());return null;
        });
        assertNull(RequestContext.current());
    }
}
