package org.valkyrienskies.mod.mixin.client.renderer;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1297;
import net.minecraft.class_310;
import net.minecraft.class_4184;
import net.minecraft.class_4604;
import net.minecraft.class_746;
import net.minecraft.class_757;
import net.minecraft.class_761;
import net.minecraft.class_765;
import net.minecraft.class_8679;
import net.minecraft.class_898;
import net.minecraft.class_9779;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.client.IVSCamera;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;

@Mixin(class_761.class)
public abstract class MixinLevelRenderer {
   @Shadow
   @Final
   private class_8679 field_45615;
   @Shadow
   @Final
   private class_310 field_4088;
   @Unique
   private ShipTransform valkyrienskies$prevShipMountedToTransform = null;

   @WrapOperation(
      method = "renderLevel",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderDispatcher;shouldRender(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/client/renderer/culling/Frustum;DDD)Z"
      ),
      require = 1
   )
   private boolean valkyrienskies$forceShouldRenderForMountedPlayer(
      class_898 dispatcher, class_1297 entity, class_4604 frustum, double d, double e, double f, Operation<Boolean> original
   ) {
      if (!(entity instanceof class_746)) {
         return (Boolean)original.call(new Object[]{dispatcher, entity, frustum, d, e, f});
      }

      class_1297 vehicle = entity.method_5854();
      return vehicle != null && vehicle instanceof ShipMountingEntity
         ? frustum.method_23093(entity.method_5829().method_1014(0.5))
         : (Boolean)original.call(new Object[]{dispatcher, entity, frustum, d, e, f});
   }

   @WrapOperation(method = "renderLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;isDetached()Z"), require = 1)
   private boolean valkyrienskies$forceDetachedForShipMount(class_4184 camera, Operation<Boolean> original, @Local class_1297 entity) {
      if ((Boolean)original.call(new Object[]{camera})) {
         return true;
      }

      if (this.field_4088.field_1690.method_31044().method_31034()) {
         return false;
      }

      if (entity != camera.method_19331()) {
         return false;
      }

      class_1297 vehicle = entity.method_5854();
      return vehicle == null ? false : vehicle instanceof ShipMountingEntity;
   }

   @ModifyExpressionValue(method = "renderLevel", at = @At(value = "CONSTANT", args = "doubleValue=1024", ordinal = 0))
   private double disableBlockDamageDistanceCheck(double originalBlockDamageDistanceConstant) {
      return Double.MAX_VALUE;
   }

   @Inject(method = "renderLevel", at = @At("HEAD"))
   private void preRenderLevel(
      class_9779 deltaTracker,
      boolean bl,
      class_4184 camera,
      class_757 gameRenderer,
      class_765 lightTexture,
      Matrix4f matrix4f,
      Matrix4f matrix4f2,
      CallbackInfo ci
   ) {
      ShipTransform shipMountedRenderTransform = ((IVSCamera)camera).getShipMountedRenderTransform();
      if (this.valkyrienskies$prevShipMountedToTransform != shipMountedRenderTransform) {
         if (this.valkyrienskies$prevShipMountedToTransform != null && shipMountedRenderTransform != null) {
            double rotDot = Math.abs(
               this.valkyrienskies$prevShipMountedToTransform.getShipToWorldRotation().dot(shipMountedRenderTransform.getShipToWorldRotation())
            );
            rotDot = Math.min(rotDot, 1.0);
            double angle = 2.0 * Math.acos(rotDot);
            if (Math.toDegrees(angle) > 1.0) {
               this.valkyrienskies$prevShipMountedToTransform = shipMountedRenderTransform;
               this.field_45615.method_52817();
            }
         } else {
            this.valkyrienskies$prevShipMountedToTransform = shipMountedRenderTransform;
            this.field_45615.method_52817();
         }
      }
   }
}
