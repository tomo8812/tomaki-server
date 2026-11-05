package maki.shipsave.mixin;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.lod.LodHorizon.Existing;
import maki.shipsave.SaveProtection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GenerationTask.class, remap = false)
public abstract class GenerationTaskMixin {
   @Shadow
   @Final
   private Selection selection;

   @Redirect(
      method = "run()V",
      at = @At(
         value = "INVOKE",
         target = "Lcom/kishku7/chunksmith/lod/LodHorizon;arm(Ljava/lang/String;IIDZLcom/kishku7/chunksmith/lod/LodHorizon$Existing;)V"
      )
   )
   private void maki$armScoped(String world, int centerChunkX, int centerChunkZ, double radius, boolean discard, Existing existing) {
      GenerationTask task = (GenerationTask)this;
      SaveProtection.arm(task, world, centerChunkX, centerChunkZ, radius, discard, SaveProtection.scopedExisting(existing, task.getShape()));
   }

   @Inject(method = "run()V", at = @At("RETURN"))
   private void maki$taskFinished(CallbackInfo ci) {
      SaveProtection.taskFinished(this.selection.world().getName(), (GenerationTask)this);
   }
}
