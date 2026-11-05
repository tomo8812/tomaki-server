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
   private void maki$armScoped(String var1, int var2, int var3, double var4, boolean var6, Existing var7) {
      SaveProtection.arm(
         (GenerationTask)this,
         var1,
         var2,
         var3,
         var4,
         var6,
         SaveProtection.scopedExisting(
            var7, this.selection.centerChunkX(), this.selection.centerChunkZ(), this.selection.radiusChunksX(), this.selection.radiusChunksZ()
         )
      );
   }

   @Inject(method = "run()V", at = @At("RETURN"))
   private void maki$taskFinished(CallbackInfo var1) {
      SaveProtection.taskFinished(this.selection.world().getName(), (GenerationTask)this);
   }
}
