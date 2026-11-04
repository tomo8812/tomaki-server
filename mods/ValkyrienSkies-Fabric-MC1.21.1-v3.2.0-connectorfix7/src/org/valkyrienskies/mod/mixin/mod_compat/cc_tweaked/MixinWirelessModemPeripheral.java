package org.valkyrienskies.mod.mixin.mod_compat.cc_tweaked;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dan200.computercraft.shared.peripheral.modem.wireless.WirelessModemPeripheral;
import net.minecraft.class_243;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Pseudo
@Mixin(value = WirelessModemPeripheral.class, priority = 2000)
public abstract class MixinWirelessModemPeripheral {
   @WrapOperation(
      method = "getRange",
      at = @At(
         value = "INVOKE",
         target = "Ldan200/computercraft/shared/peripheral/modem/wireless/WirelessModemPeripheral;getPosition()Lnet/minecraft/world/phys/Vec3;"
      )
   )
   public class_243 getRange$getPosition(WirelessModemPeripheral modem, Operation<class_243> original) {
      return VSGameUtilsKt.toWorldCoordinates(modem.getLevel(), (class_243)original.call(new Object[]{modem}));
   }
}
