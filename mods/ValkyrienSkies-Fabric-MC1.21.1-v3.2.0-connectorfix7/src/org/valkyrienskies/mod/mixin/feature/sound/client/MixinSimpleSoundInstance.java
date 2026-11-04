package org.valkyrienskies.mod.mixin.feature.sound.client;

import net.minecraft.class_1109;
import net.minecraft.class_1113;
import net.minecraft.class_243;
import net.minecraft.class_310;
import net.minecraft.class_3414;
import net.minecraft.class_3419;
import net.minecraft.class_1113.class_1114;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.client.audio.SimpleSoundInstanceOnShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_1109.class)
public class MixinSimpleSoundInstance {
   @Inject(at = @At("HEAD"), method = "forJukeboxSong", cancellable = true)
   private static void forJukeboxSong(class_3414 sound, class_243 pos, CallbackInfoReturnable<class_1109> cir) {
      Ship ship = VSGameUtilsKt.getShipManagingPos(class_310.method_1551().field_1687, pos.method_10216(), pos.method_10214(), pos.method_10215());
      if (ship != null) {
         cir.setReturnValue(
            new SimpleSoundInstanceOnShip(
               sound,
               class_3419.field_15247,
               4.0F,
               1.0F,
               class_1113.method_43221(),
               false,
               0,
               class_1114.field_5476,
               pos.method_10216(),
               pos.method_10214(),
               pos.method_10215(),
               ship
            )
         );
      }
   }
}
