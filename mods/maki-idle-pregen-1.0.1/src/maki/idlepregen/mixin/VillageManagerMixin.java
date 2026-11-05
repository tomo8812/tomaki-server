package maki.idlepregen.mixin;

import maki.idlepregen.IdlePregenApi;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "org.millenaire.village.VillageManager", remap = false)
public abstract class VillageManagerMixin {
   @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
   private void maki$pauseVillageProgress(ServerLevel var1, CallbackInfo var2) {
      if (IdlePregenApi.isActive()) {
         var2.cancel();
      }
   }
}
