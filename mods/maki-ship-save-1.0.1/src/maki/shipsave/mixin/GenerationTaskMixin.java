package maki.shipsave.mixin;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.lod.LodHorizon;
import com.kishku7.chunksmith.lod.LodHorizon.Existing;
import maki.shipsave.SaveProtection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GenerationTask.class, remap = false)
public abstract class GenerationTaskMixin {
   @Redirect(
      method = "run()V",
      at = @At(
         value = "INVOKE",
         target = "Lcom/kishku7/chunksmith/lod/LodHorizon;arm(Ljava/lang/String;IIDZLcom/kishku7/chunksmith/lod/LodHorizon$Existing;)V"
      )
   )
   private void maki$armScoped(String var1, int var2, int var3, double var4, boolean var6, Existing var7) {
      GenerationTask var8 = (GenerationTask)this;
      LodHorizon.arm(var1, var2, var3, var4, var6, SaveProtection.scopedExisting(var7, var8.getShape()));
   }
}
