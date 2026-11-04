package org.valkyrienskies.mod.mixin.feature.entity_collision;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_1313;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_2388;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_2464;
import net.minecraft.class_2680;
import net.minecraft.class_4048;
import net.minecraft.class_5819;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.EntityShipCollisionUtils;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_1297.class)
public abstract class MixinEntity implements IEntityDraggingInformationProvider {
   @Shadow
   public class_1937 field_6002;
   @Shadow
   private class_243 field_22467;
   @Shadow
   @Final
   protected class_5819 field_5974;
   @Shadow
   private class_4048 field_18065;

   @Inject(at = @At("HEAD"), method = "move", cancellable = true)
   private void beforeMove(class_1313 type, class_243 pos, CallbackInfo ci) {
      if (EntityShipCollisionUtils.isCollidingWithUnloadedShips(class_1297.class.cast(this))) {
         ci.cancel();
      }
   }

   @ModifyVariable(method = "move", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private class_243 vs$holdGravityDuringShipTransition(class_243 pos) {
      if (pos.field_1351 < 0.0 && EntityShipCollisionUtils.shouldHoldGravity(class_1297.class.cast(this))) {
         class_1297 self = class_1297.class.cast(this);
         class_243 dm = self.method_18798();
         self.method_18800(dm.field_1352, 0.0, dm.field_1350);
         return new class_243(pos.field_1352, 0.0, pos.field_1350);
      } else {
         return pos;
      }
   }

   @WrapOperation(
      method = "move",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;collide(Lnet/minecraft/world/phys/Vec3;)Lnet/minecraft/world/phys/Vec3;")
   )
   public class_243 collideWithShips(class_1297 entity, class_243 movement, Operation<class_243> collide) {
      class_238 box = this.method_5829();
      movement = EntityShipCollisionUtils.INSTANCE.adjustEntityMovementForShipCollisions(entity, movement, box, this.field_6002);
      class_243 collisionResultWithWorld = (class_243)collide.call(new Object[]{entity, movement});
      if (collisionResultWithWorld.method_1025(movement) > 1.0E-12) {
         EntityDraggingInformation entityDraggingInformation = this.getDraggingInformation();
         entityDraggingInformation.setLastShipStoodOn(null);
         entityDraggingInformation.setAddedMovementLastTick(new Vector3d());
         entityDraggingInformation.setAddedYawRotLastTick(0.0);
      }

      return collisionResultWithWorld;
   }

   @Inject(
      method = "move",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setDeltaMovement(DDD)V"),
      locals = LocalCapture.CAPTURE_FAILHARD,
      cancellable = true
   )
   private void redirectSetVelocity(class_1313 moverType, class_243 movement, CallbackInfo callbackInfo, class_243 movementAdjustedForCollisions) {
      Vector3dc collisionResponseHorizontal = new Vector3d(
         movementAdjustedForCollisions.field_1352 - movement.field_1352, 0.0, movementAdjustedForCollisions.field_1350 - movement.field_1350
      );
      if (collisionResponseHorizontal.lengthSquared() > 1.0E-6) {
         class_243 deltaMovement = this.method_18798();
         Vector3dc collisionResponseHorizontalNormal = collisionResponseHorizontal.normalize(new Vector3d());
         double parallelHorizontalVelocityComponent = collisionResponseHorizontalNormal.dot(deltaMovement.field_1352, 0.0, deltaMovement.field_1350);
         this.method_18800(
            deltaMovement.field_1352 - collisionResponseHorizontalNormal.x() * parallelHorizontalVelocityComponent,
            deltaMovement.field_1351,
            deltaMovement.field_1350 - collisionResponseHorizontalNormal.z() * parallelHorizontalVelocityComponent
         );
      }

      this.method_36974();
      callbackInfo.cancel();
   }

   @Unique
   private class_2338 getPosStandingOnFromShips(Vector3dc blockPosInGlobal) {
      double radius = 0.5;
      AABBdc testAABB = new AABBd(
         blockPosInGlobal.x() - 0.5,
         blockPosInGlobal.y() - 0.5,
         blockPosInGlobal.z() - 0.5,
         blockPosInGlobal.x() + 0.5,
         blockPosInGlobal.y() + 0.5,
         blockPosInGlobal.z() + 0.5
      );

      for (Ship ship : VSGameUtilsKt.getShipsIntersecting(this.field_6002, testAABB)) {
         Vector3dc blockPosInLocal = ship.getTransform().getWorldToShip().transformPosition(blockPosInGlobal, new Vector3d());
         class_2338 blockPos = class_2338.method_49637(Math.floor(blockPosInLocal.x()), Math.floor(blockPosInLocal.y()), Math.floor(blockPosInLocal.z()));
         class_2680 blockState = this.field_6002.method_8320(blockPos);
         if (!blockState.method_26215()) {
            return blockPos;
         }

         Vector3dc blockPosInLocal2 = ship.getTransform()
            .getWorldToShip()
            .transformPosition(new Vector3d(blockPosInGlobal.x(), blockPosInGlobal.y() - 1.0, blockPosInGlobal.z()));
         class_2338 blockPos2 = class_2338.method_49637(Math.round(blockPosInLocal2.x()), Math.round(blockPosInLocal2.y()), Math.round(blockPosInLocal2.z()));
         class_2680 blockState2 = this.field_6002.method_8320(blockPos2);
         if (!blockState2.method_26215()) {
            return blockPos2;
         }
      }

      return null;
   }

   @Inject(method = "getBlockPosBelowThatAffectsMyMovement", at = @At("HEAD"), cancellable = true)
   private void preGetBlockPosBelowThatAffectsMyMovement(CallbackInfoReturnable<class_2338> cir) {
      Vector3dc blockPosInGlobal = new Vector3d(this.field_22467.field_1352, this.method_5829().field_1322 - 0.5, this.field_22467.field_1350);
      class_2338 blockPosStandingOnFromShip = this.getPosStandingOnFromShips(blockPosInGlobal);
      if (blockPosStandingOnFromShip != null) {
         cir.setReturnValue(blockPosStandingOnFromShip);
      }
   }

   @Inject(method = "getOnPos", at = @At("HEAD"), cancellable = true)
   private void preGetOnPos(CallbackInfoReturnable<class_2338> cir) {
      Vector3dc blockPosInGlobal = new Vector3d(this.field_22467.field_1352, this.field_22467.field_1351 - 0.2, this.field_22467.field_1350);
      class_2338 blockPosStandingOnFromShip = this.getPosStandingOnFromShips(blockPosInGlobal);
      if (blockPosStandingOnFromShip != null) {
         cir.setReturnValue(blockPosStandingOnFromShip);
      }
   }

   @Inject(method = "spawnSprintParticle", at = @At("HEAD"), cancellable = true)
   private void preSpawnSprintParticle(CallbackInfo ci) {
      Vector3dc blockPosInGlobal = new Vector3d(this.field_22467.field_1352, this.field_22467.field_1351 - 0.2, this.field_22467.field_1350);
      class_2338 blockPosStandingOnFromShip = this.getPosStandingOnFromShips(blockPosInGlobal);
      if (blockPosStandingOnFromShip != null) {
         class_2680 blockState = this.field_6002.method_8320(blockPosStandingOnFromShip);
         if (blockState.method_26217() != class_2464.field_11455) {
            class_243 vec3 = this.method_18798();
            this.field_6002
               .method_8406(
                  new class_2388(class_2398.field_11217, blockState),
                  this.method_23317() + (this.field_5974.method_43058() - 0.5) * this.field_18065.comp_2185(),
                  this.method_23318() + 0.1,
                  this.method_23321() + (this.field_5974.method_43058() - 0.5) * this.field_18065.comp_2185(),
                  vec3.field_1352 * -4.0,
                  1.5,
                  vec3.field_1350 * -4.0
               );
            ci.cancel();
         }
      }
   }

   @Shadow
   public abstract class_238 method_5829();

   @Shadow
   public abstract void method_18800(double var1, double var3, double var5);

   @Shadow
   protected abstract void method_36974();

   @Shadow
   protected abstract class_243 method_17835(class_243 var1);

   @Shadow
   public abstract class_243 method_18798();

   @Shadow
   public abstract double method_23321();

   @Shadow
   public abstract double method_23318();

   @Shadow
   public abstract double method_23317();
}
