package maki.chunksmithcache;

import com.kishku7.chunksmith.GenerationTask;
import maki.chunksmithcontroller.ResourceBudgetApi;
import maki.chunksmithcontroller.RequestContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/** FULL lifecycle notifications for this server's Controller. */
final class FullTelemetryBridge {
    private FullTelemetryBridge() {}

    static GenerationTask currentTask(){return RequestContext.current();}
    static String token(ServerLevel level,ChunkPos pos,GenerationTask owner){
        return ResourceBudgetApi.attemptToken(level.dimension().location().toString(),pos.x,pos.z,owner);
    }
    static void started(String token,String stage){if(token!=null)ResourceBudgetApi.stageStarted(token,stage);}
    static void finished(String token,String stage,long nanos,boolean success){if(token!=null)ResourceBudgetApi.stageFinished(token,stage,nanos,success);}
    static void published(String token){if(token!=null)ResourceBudgetApi.fullPublished(token);}
    static void reused(String token){if(token!=null)ResourceBudgetApi.fullReused(token);}
    static void failed(String token){if(token!=null)ResourceBudgetApi.captureFailed(token);}
}
