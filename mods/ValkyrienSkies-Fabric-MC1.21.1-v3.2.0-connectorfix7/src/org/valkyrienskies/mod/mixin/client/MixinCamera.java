package org.valkyrienskies.mod.mixin.client;

import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_239;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_3959;
import net.minecraft.class_4184;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaterniond;
import org.joml.Quaterniondc;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3f;
import org.joml.primitives.AABBi;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.client.IVSCamera;
import org.valkyrienskies.mod.client.ShipCameraZoom;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

@Mixin(class_4184.class)
public abstract class MixinCamera implements IVSCamera {
   @Shadow
   private boolean field_18709;
   @Shadow
   private class_1922 field_18710;
   @Shadow
   private class_1297 field_18711;
   @Shadow
   @Final
   private Vector3f field_18714;
   @Shadow
   @Final
   private Vector3f field_18715;
   @Shadow
   @Final
   private Vector3f field_18716;
   @Shadow
   private float field_18717;
   @Shadow
   private float field_18718;
   @Shadow
   @Final
   private Quaternionf field_21518;
   @Shadow
   private boolean field_18719;
   @Shadow
   private float field_18721;
   @Shadow
   private float field_18722;
   @Shadow
   private class_243 field_18712;
   @Unique
   private ShipTransform valkyrienskies$shipMountedToTransform = null;

   @Shadow
   private float method_19318(float startingDistance) {
      return 0.0F;
   }

   @Shadow
   protected abstract void method_19324(float var1, float var2, float var3);

   @Shadow
   protected abstract void method_19327(double var1, double var3, double var5);

   @Override
   public void setupWithShipMounted(
      @NotNull class_1922 level,
      @NotNull class_1297 renderViewEntity,
      boolean thirdPerson,
      boolean thirdPersonReverse,
      float partialTicks,
      @NotNull ClientShip shipMountedTo,
      @NotNull Vector3dc inShipPlayerPosition
   ) {
      ShipTransform renderTransform = shipMountedTo.getRenderTransform();
      this.valkyrienskies$shipMountedToTransform = renderTransform;
      Vector3dc playerBasePos = renderTransform.getShipToWorldMatrix().transformPosition(inShipPlayerPosition, new Vector3d());
      Vector3dc playerEyePos = renderTransform.getShipCoordinatesToWorldCoordinatesRotation()
         .transform(new Vector3d(0.0, class_3532.method_16439(partialTicks, this.field_18722, this.field_18721), 0.0))
         .add(playerBasePos);
      this.field_18709 = true;
      this.field_18710 = level;
      this.field_18711 = renderViewEntity;
      this.field_18719 = thirdPerson;
      this.valkyrienskies$setRotationWithShipTransform(renderViewEntity.method_5705(partialTicks), renderViewEntity.method_5695(partialTicks), renderTransform);
      this.method_19327(playerEyePos.x(), playerEyePos.y(), playerEyePos.z());
      if (thirdPerson) {
         if (thirdPersonReverse) {
            this.valkyrienskies$setRotationWithShipTransform(this.field_18718 + 180.0F, -this.field_18717, renderTransform);
         }

         AABBi boundingBox = (AABBi)shipMountedTo.getShipVoxelAABB();
         double dist = (boundingBox.lengthX() + boundingBox.lengthY() + boundingBox.lengthZ()) / 3.0 * 1.5;
         dist = dist > 4.0 ? dist : 4.0;
         dist *= ShipCameraZoom.getMultiplier();
         if (this.field_18710 instanceof class_1937) {
            this.method_19324((float)(-this.valkyrienskies$getMaxZoomIgnoringMountedShip((class_1937)this.field_18710, dist, shipMountedTo)), 0.0F, 0.0F);
         } else {
            this.method_19324(-this.method_19318((float)dist), 0.0F, 0.0F);
         }
      }
   }

   @Override
   public ShipTransform getShipMountedRenderTransform() {
      return this.valkyrienskies$shipMountedToTransform;
   }

   @Override
   public void resetShipMountedRenderTransform() {
      this.valkyrienskies$shipMountedToTransform = null;
   }

   @Unique
   private void valkyrienskies$setRotationWithShipTransform(float yaw, float pitch, ShipTransform renderTransform) {
      Quaterniondc originalRotation = new Quaterniond().rotationYXZ((float) Math.PI - yaw * (float) (Math.PI / 180.0), -pitch * (float) (Math.PI / 180.0), 0.0);
      Quaterniondc newRotation = renderTransform.getShipToWorldRotation().mul(originalRotation, new Quaterniond());
      this.field_18717 = pitch;
      this.field_18718 = yaw;
      this.field_21518.set(newRotation);
      this.field_18714.set(0.0F, 0.0F, -1.0F);
      this.field_21518.transform(this.field_18714);
      this.field_18715.set(0.0F, 1.0F, 0.0F);
      this.field_21518.transform(this.field_18715);
      this.field_18716.set(-1.0F, 0.0F, 0.0F);
      this.field_21518.transform(this.field_18716);
   }

   @Unique
   private double valkyrienskies$getMaxZoomIgnoringMountedShip(class_1937 level, double maxZoom, @NotNull ClientShip toIgnore) {
      for (int i = 0; i < 8; i++) {
         float f = (i & 1) * 2 - 1;
         float g = (i >> 1 & 1) * 2 - 1;
         float h = (i >> 2 & 1) * 2 - 1;
         f *= 0.1F;
         g *= 0.1F;
         h *= 0.1F;
         class_243 vec3 = this.field_18712.method_1031(f, g, h);
         class_243 vec32 = new class_243(
            this.field_18712.field_1352 - this.field_18714.x() * maxZoom + f + h,
            this.field_18712.field_1351 - this.field_18714.y() * maxZoom + g,
            this.field_18712.field_1350 - this.field_18714.z() * maxZoom + h
         );
         class_239 hitResult = RaycastUtilsKt.clipIncludeShips(
            level, new class_3959(vec3, vec32, class_3960.field_23142, class_242.field_1348, this.field_18711), true, toIgnore.getId()
         );
         if (hitResult.method_17783() != class_240.field_1333) {
            double e = hitResult.method_17784().method_1022(this.field_18712);
            if (e < maxZoom) {
               maxZoom = e;
            }
         }
      }

      return maxZoom;
   }
}
