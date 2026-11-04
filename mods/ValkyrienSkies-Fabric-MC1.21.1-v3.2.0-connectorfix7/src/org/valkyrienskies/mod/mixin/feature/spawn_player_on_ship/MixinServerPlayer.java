package org.valkyrienskies.mod.mixin.feature.spawn_player_on_ship;

import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.mixinducks.feature.tickets.PlayerKnownShipsDuck;

@Mixin(class_3222.class)
public class MixinServerPlayer {
   @Inject(method = "restoreFrom", at = @At("RETURN"))
   private void copyFrom(class_3222 serverPlayer, boolean bl, CallbackInfo ci) {
      ((PlayerKnownShipsDuck)this).vs_setKnownShips(((PlayerKnownShipsDuck)serverPlayer).vs_getKnownShips());
   }
}
