package maki.chunksmithcontroller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DispatchControlTest {
    @Test void disabledNativeThrottleRecoversWidthButHonorsQueueAndSafety(){
        assertEquals(3,DispatchCap.apply(8,3,64,false,false,0,16));
        assertEquals(8,DispatchCap.apply(3,8,64,false,false,0,16));
        assertEquals(3,DispatchCap.apply(3,8,64,false,false,17,16));
        assertEquals(3,DispatchCap.apply(3,8,64,false,true,0,16));
        assertEquals(3,DispatchCap.apply(3,8,64,true,false,0,16));
        assertEquals(4,DispatchCap.apply(3,8,4,false,false,0,16));
    }
}
