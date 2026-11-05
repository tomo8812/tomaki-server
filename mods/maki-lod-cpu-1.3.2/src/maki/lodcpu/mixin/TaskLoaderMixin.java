package maki.lodcpu.mixin;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.platform.World;
import java.util.Optional;
import maki.lodcpu.TaskOwnership;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "com.kishku7.chunksmith.util.TaskLoader", remap = false)
public abstract class TaskLoaderMixin {
   @Inject(method = "loadTask(Lcom/kishku7/chunksmith/platform/World;)Ljava/util/Optional;", at = @At("RETURN"), require = 1)
   private void maki$claimMarkedTask(World world, CallbackInfoReturnable<Optional<GenerationTask>> cir) {
      Optional<GenerationTask> task = (Optional<GenerationTask>)cir.getReturnValue();
      if (task.isPresent()) {
         GenerationTask loaded = task.get();
         TaskOwnership.PersistedOwner owner = TaskOwnership.persistedOwner(loaded.getChunky(), world.getName());
         if (owner == TaskOwnership.PersistedOwner.CURRENT_SESSION || owner == TaskOwnership.PersistedOwner.PREVIOUS_SESSION) {
            TaskOwnership.claim(loaded, "persisted-marker");
         }
      }
   }

   @Inject(method = "saveTask(Lcom/kishku7/chunksmith/GenerationTask;)V", at = @At("RETURN"), require = 1)
   private void maki$markSavedTask(GenerationTask task, CallbackInfo ci) {
      TaskOwnership.markSaved(task);
   }
}
