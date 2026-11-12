package maki.chunksmithcontroller;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.platform.World;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import net.neoforged.fml.ModList;

/** Delegates storage ownership to the optional FULL cache. No cache policy here. */
public final class FullCacheIntegration {
    private static Method generated,request;
    private FullCacheIntegration() {}
    private static synchronized boolean available(){
        if(!ModList.get().isLoaded("maki_chunksmith_chunk_cache"))return false;
        if(request!=null)return true;
        try{
            Class<?> api=Class.forName("maki.chunksmithcache.CacheManager");
            generated=api.getMethod("observeGenerated",World.class,int.class,int.class,GenerationTask.class,CompletableFuture.class);
            request=api.getMethod("request",World.class,int.class,int.class,GenerationTask.class,java.util.function.Supplier.class);
            return true;
        }catch(ReflectiveOperationException error){throw new IllegalStateException("FULL cache API unavailable",error);}
    }
    @SuppressWarnings("unchecked")
    private static <T> T invoke(Method method,Object... args){
        try{return (T)method.invoke(null,args);}
        catch(InvocationTargetException error){
            if(error.getCause() instanceof RuntimeException runtime)throw runtime;
            if(error.getCause() instanceof Error fatal)throw fatal;
            throw new IllegalStateException(error.getCause());
        }catch(ReflectiveOperationException error){throw new IllegalStateException(error);}
    }
    public static CompletableFuture<Boolean> generated(World world,int x,int z,GenerationTask owner,CompletableFuture<Boolean> nativeResult){
        return available()?invoke(generated,world,x,z,owner,nativeResult):nativeResult;
    }
    public static CompletableFuture<Void> request(World world,int x,int z,GenerationTask owner){
        java.util.function.Supplier<CompletableFuture<Void>> nativeRequest=()->RequestContext.with(owner,()->world.getChunkAtAsync(x,z));
        return available()?invoke(request,world,x,z,owner,nativeRequest):nativeRequest.get();
    }
}
