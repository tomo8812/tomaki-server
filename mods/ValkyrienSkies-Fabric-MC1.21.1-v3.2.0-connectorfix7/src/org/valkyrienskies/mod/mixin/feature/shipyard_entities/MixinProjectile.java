package org.valkyrienskies.mod.mixin.feature.shipyard_entities;

import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1671;
import net.minecraft.class_1676;
import net.minecraft.class_1937;
import net.minecraft.class_265;
import net.minecraft.class_3965;
import net.minecraft.class_8046;
import org.joml.Vector3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.handling.DefaultShipyardEntityHandler;
import org.valkyrienskies.mod.common.entity.handling.WorldEntityHandler;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_1676.class)
public abstract class MixinProjectile extends class_1297 implements class_8046 {
   @Shadow
   protected abstract void method_26962();

   public MixinProjectile(class_1299<?> entityType, class_1937 level) {
      super(entityType, level);
   }

   @Inject(method = "onHitBlock", at = @At("HEAD"))
   private void sendToShipyard(class_3965 blockHitResult, CallbackInfo ci) {
      if (!(this instanceof class_1671)) {
         Ship ship;
         if ((ship = VSGameUtilsKt.getShipManagingPos(this.method_37908(), blockHitResult.method_17777())) != null) {
            Vector3d hitLocation = ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(blockHitResult.field_1329));
            blockHitResult.field_1329 = VectorConversionsMCKt.toMinecraft(hitLocation);
            DefaultShipyardEntityHandler.INSTANCE.moveEntityFromWorldToShipyard(this, ship);
         }
      }
   }

   @Inject(method = "tick", at = @At("HEAD"))
   private void returnFromShipyard(CallbackInfo ci) {
      Ship ship;
      if ((ship = VSGameUtilsKt.getShipManaging(this)) != null) {
         Iterable<class_265> result = this.method_37908().method_20812(this, this.method_5829().method_1014(0.1));
         if (!result.iterator().hasNext()) {
            WorldEntityHandler.INSTANCE.moveEntityFromShipyardToWorld(this, ship);
         }
      }
   }
}
