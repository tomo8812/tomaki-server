package org.valkyrienskies.mod.mixin.feature.shipyard_entities;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_4604;
import net.minecraft.class_897;
import net.minecraft.class_898;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(value = class_898.class, priority = 500)
public class MixinEntityRenderDispatcher {
   @Shadow
   public class_4184 field_4686;

   @Inject(method = "distanceToSqr(Lnet/minecraft/world/entity/Entity;)D", at = @At("HEAD"), cancellable = true)
   private void preDistanceToSqr(class_1297 entity, CallbackInfoReturnable<Double> cir) {
      class_243 pos = entity.method_19538();
      if (entity != null) {
         cir.setReturnValue(VSGameUtilsKt.squaredDistanceToInclShips(entity, pos.field_1352, pos.field_1351, pos.field_1350));
      }
   }

   @Inject(method = "distanceToSqr(DDD)D", at = @At("HEAD"), cancellable = true)
   private void preDistanceToSqr(double x, double y, double z, CallbackInfoReturnable<Double> cir) {
      if (this.field_4686.method_19331() != null) {
         cir.setReturnValue(VSGameUtilsKt.squaredDistanceToInclShips(this.field_4686.method_19331(), x, y, z));
      }
   }

   @Inject(
      method = "render",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
         shift = Shift.BEFORE
      ),
      locals = LocalCapture.CAPTURE_FAILHARD
   )
   <T extends class_1297> void render(
      T entity,
      double x,
      double y,
      double z,
      float rotationYaw,
      float partialTicks,
      class_4587 matrixStack,
      class_4597 buffer,
      int packedLight,
      CallbackInfo ci,
      class_897<T> entityRenderer
   ) {
      ShipMountedToData shipMountedToData = VSGameUtilsKt.getShipMountedToData(entity, partialTicks);
      if (shipMountedToData != null) {
         matrixStack.method_22909();
         matrixStack.method_22903();
         ShipTransform renderTransform = ((ClientShip)shipMountedToData.getShipMountedTo()).getRenderTransform();
         class_243 entityPosition = entity.method_30950(partialTicks);
         Vector3dc transformed = renderTransform.getShipToWorld().transformPosition(shipMountedToData.getMountPosInShip(), new Vector3d());
         double camX = x - entityPosition.field_1352;
         double camY = y - entityPosition.field_1351;
         double camZ = z - entityPosition.field_1350;
         class_243 offset = entityRenderer.method_23169(entity, partialTicks);
         Vector3dc scale = renderTransform.getShipToWorldScaling();
         matrixStack.method_22904(transformed.x() + camX, transformed.y() + camY, transformed.z() + camZ);
         matrixStack.method_22907(new Quaternionf(renderTransform.getShipToWorldRotation()));
         matrixStack.method_22905((float)scale.x(), (float)scale.y(), (float)scale.z());
         matrixStack.method_22904(offset.field_1352, offset.field_1351, offset.field_1350);
      } else {
         ClientShip ship = (ClientShip)VSGameUtilsKt.getLoadedShipManagingPos(entity.method_37908(), entity.method_24515());
         if (ship != null) {
            matrixStack.method_22909();
            matrixStack.method_22903();
            VSEntityManager.INSTANCE
               .getHandler(entity)
               .applyRenderTransform(ship, entity, entityRenderer, x, y, z, rotationYaw, partialTicks, matrixStack, buffer, packedLight);
         } else if (entity.method_5765()) {
            ClientShip vehicleShip = (ClientShip)VSGameUtilsKt.getLoadedShipManagingPos(entity.method_37908(), entity.method_5854().method_24515());
            if (vehicleShip != null) {
               VSEntityManager.INSTANCE
                  .getHandler(entity.method_5854())
                  .applyRenderOnMountedEntity(vehicleShip, entity.method_5854(), entity, partialTicks, matrixStack);
            }
         }
      }
   }

   @ModifyReturnValue(method = "shouldRender", at = @At("RETURN"))
   boolean shouldRender(boolean returns, class_1297 entity, class_4604 frustum, double camX, double camY, double camZ) {
      if (!returns) {
         ClientShip ship = (ClientShip)VSGameUtilsKt.getLoadedShipManagingPos(entity.method_37908(), entity.method_24515());
         if (ship != null) {
            class_238 aABB = entity.method_5830().method_1014(0.5);
            if (aABB.method_1013() || aABB.method_995() == 0.0) {
               aABB = new class_238(
                  entity.method_23317() - 2.0,
                  entity.method_23318() - 2.0,
                  entity.method_23321() - 2.0,
                  entity.method_23317() + 2.0,
                  entity.method_23318() + 2.0,
                  entity.method_23321() + 2.0
               );
            }

            AABBd aabb = VectorConversionsMCKt.toJOML(aABB);
            aabb.transform(ship.getRenderTransform().getShipToWorld());
            return frustum.method_23093(VectorConversionsMCKt.toMinecraft(aabb));
         }
      }

      return returns;
   }
}
