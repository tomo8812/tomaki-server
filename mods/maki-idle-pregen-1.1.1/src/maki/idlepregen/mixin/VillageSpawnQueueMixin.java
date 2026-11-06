package maki.idlepregen.mixin;

import maki.idlepregen.IdlePregenApi;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "org.millenaire.world.VillageSpawnQueue", remap = false)
public abstract class VillageSpawnQueueMixin {
   @Inject(method = "trySpawnNext", at = @At("HEAD"), cancellable = true)
   private void maki$pauseNaturalGeneration(ServerLevel level, CallbackInfo ci) {
      if (IdlePregenApi.isActive()) {
         ci.cancel();
      }
   }
}
