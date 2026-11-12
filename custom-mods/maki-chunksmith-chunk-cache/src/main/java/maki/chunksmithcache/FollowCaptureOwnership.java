package maki.chunksmithcache;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.GenerationTask;
import net.minecraft.server.level.ServerLevel;

/** Consults the existing owner; never adopts tasks or duplicates ownership state. */
final class FollowCaptureOwnership {
    private FollowCaptureOwnership() {}
    static boolean owns(ServerLevel level) {
        try {
            GenerationTask task = ChunksmithProvider.get().getGenerationTasks()
                .get(level.dimension().location().toString());
            return owns(task);
        } catch (LinkageError | RuntimeException error) {
            return false;
        }
    }

    static boolean owns(GenerationTask task) {
        if(task==null)return false;
        try {
            return Boolean.TRUE.equals(Class.forName("maki.chunksmithcontroller.TaskOwnership")
                .getMethod("isOwned", GenerationTask.class).invoke(null, task));
        } catch (ReflectiveOperationException | LinkageError | RuntimeException error) {
            return false;
        }
    }

    static boolean active(ServerLevel level,GenerationTask task) {
        if(level==null||task==null||task.isStopping()||!owns(task))return false;
        try {
            return ChunksmithProvider.get().getGenerationTasks()
                .get(level.dimension().location().toString())==task;
        } catch (LinkageError | RuntimeException error) {
            return false;
        }
    }
}
