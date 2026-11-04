package org.valkyrienskies.mod.mixin.feature.save_mob_location_on_ship;

import net.minecraft.class_1308;
import net.minecraft.class_2487;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.config.VSGameConfig;
import org.valkyrienskies.mod.common.entity.ShipyardPosSavable;

@Mixin(class_1308.class)
public class MixinMob implements ShipyardPosSavable {
   @Unique
   public Vector3d valkyrienskies$unloadedShipyardPos = null;

   @Override
   public Vector3d valkyrienskies$getUnloadedShipyardPos() {
      return this.valkyrienskies$unloadedShipyardPos;
   }

   @Override
   public void valkyrienskies$setUnloadedShipyardPos(Vector3d vector3d) {
      this.valkyrienskies$unloadedShipyardPos = vector3d;
   }

   @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
   public void addAdditionalSaveDataMixin(class_2487 nbt, CallbackInfo info) {
      Vector3d position = this.valkyrienskies$getUnloadedShipyardPos();
      if (position != null && VSGameConfig.SERVER.getSaveMobsPositionOnShip()) {
         nbt.method_10549("valkyrienskies$unloadedX", position.x);
         nbt.method_10549("valkyrienskies$unloadedY", position.y);
         nbt.method_10549("valkyrienskies$unloadedZ", position.z);
      } else {
         nbt.method_10551("valkyrienskies$unloadedX");
         nbt.method_10551("valkyrienskies$unloadedY");
         nbt.method_10551("valkyrienskies$unloadedZ");
      }
   }

   @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
   public void readAdditionalSaveData(class_2487 nbt, CallbackInfo info) {
      if (nbt.method_10545("valkyrienskies$unloadedX") && nbt.method_10545("valkyrienskies$unloadedY") && nbt.method_10545("valkyrienskies$unloadedZ")) {
         double[] xyz = new double[]{
            nbt.method_10574("valkyrienskies$unloadedX"), nbt.method_10574("valkyrienskies$unloadedY"), nbt.method_10574("valkyrienskies$unloadedZ")
         };
         this.valkyrienskies$setUnloadedShipyardPos(new Vector3d(xyz));
      }
   }
}
