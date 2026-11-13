package maki.millenairelod;

import java.lang.reflect.Constructor;
import net.minecraft.core.BlockPos;

/** Constructs the fixed Millenaire 9.1.0 package-private retry record. */
final class NativeLoneCandidate {
    private static final Constructor<?> CONSTRUCTOR=constructor();
    private NativeLoneCandidate() {}
    private static Constructor<?> constructor(){
        try{
            Class<?> type=Class.forName("org.millenaire.world.VillageSpawnQueue$LBCandidate");
            Constructor<?> constructor=type.getDeclaredConstructor(BlockPos.class,double.class,long.class,long.class);
            constructor.setAccessible(true);
            return constructor;
        }catch(ReflectiveOperationException error){throw new ExceptionInInitializerError(error);}
    }
    static Object restore(BlockPos pos,double distance,long enqueuedTick,long retryAfterTick){
        try{return CONSTRUCTOR.newInstance(pos,distance,enqueuedTick,retryAfterTick);}
        catch(ReflectiveOperationException error){throw new IllegalStateException("Cannot restore Millenaire 9.1.0 lone candidate",error);}
    }
}
