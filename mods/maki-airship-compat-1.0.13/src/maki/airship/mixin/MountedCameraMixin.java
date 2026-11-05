package maki.airship.mixin;

import maki.airship.MountedCameraRotation;
import maki.airship.SleepCompat;
import maki.airship.SleepingCameraPose;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.mod.client.ShipMountPerspective;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;
import org.valkyrienskies.mod.common.entity.ShipMountingEntity;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

@Mixin(value = Camera.class, remap = false)
public abstract class MountedCameraMixin {
   @Shadow
   @Final
   private Quaternionf rotation;
   @Shadow
   @Final
   private Vector3f forwards;
   @Shadow
   @Final
   private Vector3f up;
   @Shadow
   @Final
   private Vector3f left;
   @Shadow
   private float xRot;
   @Shadow
   private float yRot;
   @Shadow
   private float eyeHeight;
   @Shadow
   private float eyeHeightOld;
   @Shadow
   private Vec3 position;

   @Shadow
   protected abstract void setPosition(double var1, double var3, double var5);

   @Shadow
   protected abstract void move(float var1, float var2, float var3);

   @Inject(method = "setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V", at = @At("TAIL"), remap = false)
   private void maki$normalMountedView(BlockGetter var1, Entity var2, boolean var3, boolean var4, float var5, CallbackInfo var6) {
      if (var1 instanceof Level var7) {
         boolean var12 = false;
         ClientShip var8;
         Vector3d var9;
         float var10;
         float var11;
         if (var2 instanceof LivingEntity var13 && SleepCompat.anchored(var13) && !var13.isPassenger()) {
            BlockPos var33 = (BlockPos)var13.getSleepingPos().orElseThrow();
            if (!(VSGameUtilsKt.getLoadedShipManagingPos(var7, var33) instanceof ClientShip var37)) {
               return;
            }

            var8 = var37;
            Vec3 var17 = SleepCompat.bedPoint(var33);
            var9 = new Vector3d(var17.x, var17.y, var17.z);
            var10 = SleepingCameraPose.localYaw(var13.getBedOrientation());
            var11 = 0.0F;
            var12 = true;
         } else {
            if (!(var2.getVehicle() instanceof ShipMountingEntity) || ShipMountPerspective.isShipViewEngaged()) {
               return;
            }

            ShipMountedToData var14 = VSGameUtilsKt.getShipMountedToData(var2, var5);
            if (var14 == null || !(var14.getShipMountedTo() instanceof ClientShip var15)) {
               return;
            }

            var8 = var15;
            var9 = new Vector3d(var14.getMountPosInShip());
            var10 = var2.getViewYRot(var5);
            var11 = var2.getViewXRot(var5);
         }

         ShipTransform var32 = var8.getRenderTransform();
         this.rotation.set(MountedCameraRotation.worldRotation(var32.getShipToWorldRotation(), var10, var11, var3 && var4));
         this.forwards.set(0.0F, 0.0F, -1.0F);
         this.rotation.transform(this.forwards);
         this.up.set(0.0F, 1.0F, 0.0F);
         this.rotation.transform(this.up);
         this.left.set(-1.0F, 0.0F, 0.0F);
         this.rotation.transform(this.left);
         this.yRot = (float)Math.toDegrees(Math.atan2(-this.forwards.x, this.forwards.z));
         this.xRot = (float)Math.toDegrees(Math.asin(Math.clamp(-this.forwards.y, -1.0, 1.0)));
         Vector3d var34 = var32.getShipToWorld().transformPosition(var9, new Vector3d());
         double var36 = this.eyeHeightOld + (this.eyeHeight - this.eyeHeightOld) * var5;
         if (var12 && !var3) {
            var36 += 0.3;
         }

         Vector3d var38 = var32.getShipToWorldRotation().transform(new Vector3d(0.0, var36, 0.0)).add(var34);
         this.setPosition(var38.x, var38.y, var38.z);
         if (var3) {
            double var18 = 4.0 * (var2 instanceof LivingEntity var20 ? var20.getScale() : 1.0);
            double var39 = var18;

            for (int var22 = 0; var22 < 8; var22++) {
               double var23 = ((var22 & 1) * 2 - 1) * 0.1;
               double var25 = ((var22 >> 1 & 1) * 2 - 1) * 0.1;
               double var27 = ((var22 >> 2 & 1) * 2 - 1) * 0.1;
               Vec3 var29 = this.position.add(var23, var25, var27);
               Vec3 var30 = var29.add(-this.forwards.x * var18, -this.forwards.y * var18, -this.forwards.z * var18);
               BlockHitResult var31 = RaycastUtilsKt.clipIncludeShips(var7, new ClipContext(var29, var30, Block.VISUAL, Fluid.NONE, var2), true, null, false);
               if (var31.getType() != Type.MISS) {
                  var39 = Math.min(var39, var31.getLocation().distanceTo(this.position));
               }
            }

            this.move((float)(-var39), 0.0F, 0.0F);
         }
      }
   }
}
