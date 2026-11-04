package com.kishku7.chunksmith.mixin;

import com.kishku7.chunksmith.worldenter.WorldEnterPregen;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TickRateManager.class)
public abstract class TickRateManagerMixin {
   @Shadow
   public abstract boolean runsNormally();

   @Inject(method = "isEntityFrozen", at = @At("HEAD"), cancellable = true)
   private void chunksmith$freezePlayerDuringWorldEnter(Entity entity, CallbackInfoReturnable<Boolean> cir) {
      if (entity instanceof Player && WorldEnterPregen.isFrozen() && !this.runsNormally()) {
         cir.setReturnValue(true);
      }
   }
}
