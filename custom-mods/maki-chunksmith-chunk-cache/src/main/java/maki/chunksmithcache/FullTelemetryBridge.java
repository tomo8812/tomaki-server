package maki.chunksmithcache;

import com.kishku7.chunksmith.GenerationTask;
import java.lang.reflect.Method;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/** FULL-only result notifications to the required Controller API. */
final class FullTelemetryBridge {
    private static final Class<?> API;
    private static final Method TOKEN,STARTED,FINISHED,READY,FAILED,CURRENT;
    static {
        try {
            API=Class.forName("maki.chunksmithcontroller.ResourceBudgetApi");
            TOKEN=API.getMethod("attemptToken",String.class,int.class,int.class,GenerationTask.class);
            STARTED=API.getMethod("stageStarted",String.class,String.class);
            FINISHED=API.getMethod("stageFinished",String.class,String.class,long.class,boolean.class);
            READY=API.getMethod("fullReady",String.class);
            FAILED=API.getMethod("captureFailed",String.class);
            CURRENT=Class.forName("maki.chunksmithcontroller.RequestContext").getMethod("current");
        }catch(ReflectiveOperationException error){throw new ExceptionInInitializerError(error);}
    }
    private FullTelemetryBridge() {}
    private static Object call(Method method,Object... args){
        try{return method.invoke(null,args);}
        catch(ReflectiveOperationException error){throw new IllegalStateException("Controller FULL notification failed",error);}
    }
    static void connect(){
        try{API.getMethod("connectPairTelemetry",String.class).invoke(null,"E_LITE_PAIR_V1");}
        catch(ReflectiveOperationException error){throw new IllegalStateException(error);}
    }
    static GenerationTask currentTask(){return (GenerationTask)call(CURRENT);}
    static String token(ServerLevel level,ChunkPos pos,GenerationTask owner){
        return (String)call(TOKEN,level.dimension().location().toString(),pos.x,pos.z,owner);
    }
    static void started(String token,String stage){if(token!=null)call(STARTED,token,stage);}
    static void finished(String token,String stage,long nanos,boolean success){if(token!=null)call(FINISHED,token,stage,nanos,success);}
    static void ready(String token){if(token!=null)call(READY,token);}
    static void failed(String token){if(token!=null)call(FAILED,token);}
}
