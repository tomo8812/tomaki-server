package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;
import maki.chunksmithcontroller.TaskOwnership;
import net.minecraft.server.level.ServerLevel;

/** Uses the Controller's task identity; never adopts a task. */
final class FollowCaptureOwnership {
    private FollowCaptureOwnership() {}
    static boolean owns(ServerLevel level){
        return owns(ChunksmithProvider.get().getGenerationTasks().get(level.dimension().location().toString()));
    }
    static boolean owns(GenerationTask task){return task!=null&&TaskOwnership.isOwned(task);}
    static boolean active(ServerLevel level,GenerationTask task){
        return level!=null&&owns(task)&&!task.isStopping()
            &&ChunksmithProvider.get().getGenerationTasks().get(level.dimension().location().toString())==task;
    }
}
