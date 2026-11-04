package org.valkyrienskies.mod.mixin.client.player;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_746;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.mixinducks.client.player.LocalPlayerDuck;

@Mixin(class_746.class)
public abstract class MixinLocalPlayer extends class_1309 implements LocalPlayerDuck {
   @Shadow
   private float field_3941;
   @Shadow
   private float field_3925;
   @Unique
   private class_243 lastPosition = null;
   @Unique
   private Vector3dc velocity = new Vector3d();

   protected MixinLocalPlayer() {
      super(null, null);
   }

   @Inject(method = "getViewYRot", at = @At("HEAD"), cancellable = true)
   private void preGetViewYRot(float partialTick, CallbackInfoReturnable<Float> cir) {
      if (this.method_5765()) {
         cir.setReturnValue(super.method_5705(partialTick));
      } else {
         cir.setReturnValue(class_3532.method_16439(partialTick, this.field_5982, this.method_36454()));
      }
   }

   @Override
   public Vector3dc vs$getVelocity() {
      return this.velocity;
   }

   @Inject(method = "tick", at = @At("HEAD"))
   private void tick(CallbackInfo ci) {
      class_243 pos = this.method_19538();
      if (this.lastPosition != null) {
         this.velocity = new Vector3d(
            pos.field_1352 - this.lastPosition.field_1352, pos.field_1351 - this.lastPosition.field_1351, pos.field_1350 - this.lastPosition.field_1350
         );
      }

      this.lastPosition = pos;
   }

   @WrapMethod(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z")
   private boolean adjustLookOnMount(class_1297 entity, boolean bl, Operation<Boolean> original) {
      Vector3d lookVector = VectorConversionsMCKt.toJOML(this.method_5720());
      if ((Boolean)original.call(new Object[]{entity, bl})) {
         Ship ship = VSGameUtilsKt.getShipMountedTo(class_1297.class.cast(this));
         if (ship != null) {
            Vector3d transformedLook = ship.getTransform().getWorldToShip().transformDirection(lookVector);
            double yaw = Math.atan2(-transformedLook.x, transformedLook.z) * 180.0 / Math.PI;
            double pitch = Math.atan2(-transformedLook.y, Math.sqrt(transformedLook.x * transformedLook.x + transformedLook.z * transformedLook.z))
               * 180.0
               / Math.PI;
            this.method_36456((float)yaw);
            this.method_36457((float)pitch);
            this.field_5982 = this.method_36454();
            this.field_3941 = this.method_36454();
            this.field_6241 = this.method_36454();
            this.field_6259 = this.method_36454();
            this.field_6004 = this.method_36455();
            this.field_3925 = this.method_36455();
         }

         return true;
      } else {
         return false;
      }
   }
}
