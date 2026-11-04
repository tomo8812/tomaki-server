package org.valkyrienskies.mod.mixin.mod_compat.cc_tweaked;

import dan200.computercraft.api.network.PacketReceiver;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessNetwork;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Pseudo
@Mixin(value = WirelessNetwork.class, priority = 2000)
public class MixinWirelessNetwork {
   @Redirect(method = "tryTransmit", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;distanceToSqr(Lnet/minecraft/world/phys/Vec3;)D"))
   private static double tryTransmit$distanceToSqr(class_243 origin, class_243 pos, PacketReceiver receiver) {
      return VSGameUtilsKt.squaredDistanceBetweenInclShips(
         receiver.getLevel(), origin.field_1352, origin.field_1351, origin.field_1350, pos.field_1352, pos.field_1351, pos.field_1350
      );
   }
}
