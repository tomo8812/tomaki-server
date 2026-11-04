package org.valkyrienskies.mod.mixin.feature.entity_ship_light;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.class_1297;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_897;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;

@Mixin(class_897.class)
public abstract class MixinEntityRenderer {
   @ModifyReturnValue(method = "getBlockLightLevel", at = @At("RETURN"))
   private int vs$addShipBlockLight(int original, class_1297 entity, class_2338 worldPos) {
      ShipMountedToData mounted = VSGameUtilsKt.getShipMountedToData(entity, null);
      if (mounted != null) {
         Vector3dc m = mounted.getMountPosInShip();
         return Math.max(original, entity.method_37908().method_8314(class_1944.field_9282, class_2338.method_49637(m.x(), m.y(), m.z())));
      }

      class_243 probe = entity.method_31166(1.0F);
      int best = original;

      for (Ship ship : ValkyrienSkies.getShipsIntersecting(entity.method_37908(), probe.field_1352, probe.field_1351, probe.field_1350)) {
         Vector3d shipPos = new Vector3d(probe.field_1352, probe.field_1351, probe.field_1350);
         ship.getTransform().getWorldToShip().transformPosition(shipPos);
         int shipBlockLight = entity.method_37908().method_8314(class_1944.field_9282, class_2338.method_49637(shipPos.x(), shipPos.y(), shipPos.z()));
         if (shipBlockLight > best) {
            best = shipBlockLight;
         }
      }

      return best;
   }
}
