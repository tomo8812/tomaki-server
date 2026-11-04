package org.valkyrienskies.mod.mixin.entity;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import java.util.Set;
import net.minecraft.class_128;
import net.minecraft.class_129;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_148;
import net.minecraft.class_1657;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_3611;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_6862;
import net.minecraft.class_1297.class_4738;
import net.minecraft.class_2338.class_2339;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.core.api.ships.ClientShip;
import org.valkyrienskies.core.api.ships.LoadedShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.core.api.ships.properties.ShipTransform;
import org.valkyrienskies.core.api.world.ShipWorld;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.entity.ShipMountedToData;
import org.valkyrienskies.mod.common.util.EntityDragger;
import org.valkyrienskies.mod.common.util.EntityDraggingInformation;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.common.world.RaycastUtilsKt;

@Mixin(class_1297.class)
public abstract class MixinEntity implements IEntityDraggingInformationProvider {
   @Unique
   private final EntityDraggingInformation draggingInformation = new EntityDraggingInformation();
   @Unique
   private boolean vs$isInSealedArea = false;
   @Unique
   private class_2338 vs$lastCheckedSealedPos = class_2338.field_10980;
   @Shadow
   public class_1937 field_6002;
   @Shadow
   public boolean field_6007;
   @Shadow
   protected boolean field_5957;
   @Shadow
   protected boolean field_6000;
   @Shadow
   @Final
   private Set<class_6862<class_3611>> field_25599;

   @Redirect(
      method = "pick",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;clip(Lnet/minecraft/world/level/ClipContext;)Lnet/minecraft/world/phys/BlockHitResult;"
      )
   )
   public class_3965 addShipsToRaycast(class_1937 receiver, class_3959 ctx) {
      return RaycastUtilsKt.clipIncludeShips(receiver, ctx);
   }

   @Inject(method = "baseTick", at = @At("HEAD"))
   private void onBaseTick(CallbackInfo ci) {
      if (this.field_6002 != null && !this.method_31481()) {
         class_1297 entity = (class_1297)this;
         class_243 relativePosition = class_243.field_1353;
         if (this.getDraggingInformation().isEntityBeingDraggedByAShip()) {
            relativePosition = EntityDragger.INSTANCE.serversidePosition(entity);
         } else if (VSGameUtilsKt.getShipMountedTo(entity) != null) {
            relativePosition = VectorConversionsMCKt.toMinecraft(
               VSGameUtilsKt.getShipMountedToData(entity, null).getMountPosInShip().add(0.0, entity.method_18381(entity.method_18376()), 0.0, new Vector3d())
            );
         }

         boolean isInSealedArea = false;
         if (!isInSealedArea) {
            if (relativePosition != class_243.field_1353 && VSGameUtilsKt.isBlockInShipyard(this.field_6002, class_2338.method_49638(relativePosition))) {
               if (class_2338.method_49638(relativePosition).equals(this.vs$lastCheckedSealedPos)) {
                  isInSealedArea = this.vs$isInSealedArea();
               } else {
                  isInSealedArea = VSGameUtilsKt.isPositionSealed(this.field_6002, class_2338.method_49638(relativePosition));
                  this.vs$lastCheckedSealedPos = class_2338.method_49638(relativePosition);
               }
            } else if (!VSGameUtilsKt.isBlockInShipyard(this.field_6002, class_2338.method_49638(relativePosition))) {
               ShipWorld shipWorld = VSGameUtilsKt.getShipObjectWorld(this.field_6002);

               for (Ship ship : shipWorld.getAllShips()
                  .getIntersecting(VectorConversionsMCKt.toJOML(entity.method_5829().method_1014(1.0)), VSGameUtilsKt.getDimensionId(this.field_6002))) {
                  relativePosition = VectorConversionsMCKt.toMinecraft(
                     ship.getWorldToShip().transformPosition(VectorConversionsMCKt.toJOML(entity.method_19538()), new Vector3d())
                  );
                  if (VSGameUtilsKt.isPositionSealed(this.field_6002, class_2338.method_49638(relativePosition))) {
                     this.vs$lastCheckedSealedPos = class_2338.method_49638(relativePosition);
                     isInSealedArea = true;
                     break;
                  }
               }
            }
         }

         this.vs$setInSealedArea(isInSealedArea && ValkyrienSkies.isConnectivityEnabled(this.field_6002.field_9236));
      }
   }

   @WrapMethod(method = "updateFluidOnEyes")
   private void onFluidOnEyes(Operation<Void> original) {
      if (this.vs$isInSealedArea() && ValkyrienSkies.isConnectivityEnabled(this.field_6002.field_9236)) {
         this.field_6000 = false;
         this.field_25599.clear();
      } else {
         original.call(new Object[0]);
      }
   }

   @WrapMethod(method = "updateSwimming")
   private void onUpdateSwimming(Operation<Void> original) {
      if (this.vs$isInSealedArea && ValkyrienSkies.isConnectivityEnabled(this.field_6002.field_9236)) {
         this.field_5957 = false;
         this.method_5796(false);
      } else {
         original.call(new Object[0]);
      }
   }

   @Inject(method = "updateInWaterStateAndDoWaterCurrentPushing", at = @At("HEAD"), cancellable = true)
   private void onUpdateInWaterStateAndDoWaterCurrentPushing(CallbackInfo ci) {
      if (this.vs$isInSealedArea && ValkyrienSkies.isConnectivityEnabled(this.field_6002.field_9236)) {
         this.field_5957 = false;
         ci.cancel();
      }
   }

   @WrapMethod(method = "isInBubbleColumn")
   private boolean onIsInBubbleColumn(Operation<Boolean> original) {
      return this.vs$isInSealedArea && ValkyrienSkies.isConnectivityEnabled(this.field_6002.field_9236) ? false : (Boolean)original.call(new Object[0]);
   }

   @Inject(at = @At("TAIL"), method = "checkInsideBlocks")
   private void afterCheckInside(CallbackInfo ci) {
      AABBd boundingBox = VectorConversionsMCKt.toJOML(this.method_5829());
      AABBd temp = new AABBd();

      for (Ship ship : VSGameUtilsKt.getShipsIntersecting(this.field_6002, boundingBox)) {
         AABBd inShipBB = boundingBox.transform(ship.getShipTransform().getWorldToShipMatrix(), temp);
         this.originalCheckInside(inShipBB);
      }
   }

   @Unique
   private void originalCheckInside(AABBd aABB) {
      class_1297 self = class_1297.class.cast(this);
      class_2338 blockPos = class_2338.method_49637(aABB.minX + 0.001, aABB.minY + 0.001, aABB.minZ + 0.001);
      class_2338 blockPos2 = class_2338.method_49637(aABB.maxX - 0.001, aABB.maxY - 0.001, aABB.maxZ - 0.001);
      class_2339 mutableBlockPos = new class_2339();
      if (this.field_6002.method_22343(blockPos, blockPos2)) {
         for (int i = blockPos.method_10263(); i <= blockPos2.method_10263(); i++) {
            for (int j = blockPos.method_10264(); j <= blockPos2.method_10264(); j++) {
               for (int k = blockPos.method_10260(); k <= blockPos2.method_10260(); k++) {
                  mutableBlockPos.method_10103(i, j, k);
                  class_2680 blockState = this.field_6002.method_8320(mutableBlockPos);

                  try {
                     blockState.method_26178(this.field_6002, mutableBlockPos, self);
                     this.method_5622(blockState);
                  } catch (Throwable var12) {
                     class_128 crashReport = class_128.method_560(var12, "Colliding entity with block");
                     class_129 crashReportCategory = crashReport.method_562("Block being collided with");
                     class_129.method_586(crashReportCategory, this.field_6002, mutableBlockPos, blockState);
                     throw new class_148(crashReport);
                  }
               }
            }
         }
      }
   }

   @Inject(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
   private void preGetEyePosition(float partialTicks, CallbackInfoReturnable<class_243> cir) {
      ShipMountedToData shipMountedToData = VSGameUtilsKt.getShipMountedToData(class_1297.class.cast(this), partialTicks);
      if (shipMountedToData == null) {
         if (class_1297.class.cast(this) instanceof class_1657 player
            && player instanceof IEntityDraggingInformationProvider dragProvider
            && dragProvider.getDraggingInformation().isEntityBeingDraggedByAShip()
            && dragProvider.getDraggingInformation().getServerRelativePlayerYaw() != null) {
            Ship shipDraggedBy = VSGameUtilsKt.getAllShips(this.field_6002).getById(dragProvider.getDraggingInformation().getLastShipStoodOn());
            if (shipDraggedBy != null) {
               class_243 localEyePosition = EntityDragger.INSTANCE.serversideEyePosition(player);
               if (!VSGameUtilsKt.isBlockInShipyard(this.field_6002, localEyePosition)) {
                  return;
               }

               ShipTransform shipTransform;
               if (shipDraggedBy instanceof ClientShip) {
                  shipTransform = ((ClientShip)shipDraggedBy).getRenderTransform();
               } else {
                  shipTransform = shipDraggedBy.getShipTransform();
               }

               class_243 worldEyePosition = VectorConversionsMCKt.toMinecraft(
                  shipTransform.getShipToWorld().transformPosition(VectorConversionsMCKt.toJOML(localEyePosition))
               );
               cir.setReturnValue(worldEyePosition);
            }
         }
      } else {
         LoadedShip shipMountedTo = shipMountedToData.getShipMountedTo();
         ShipTransform shipTransform;
         if (shipMountedTo instanceof ClientShip) {
            shipTransform = ((ClientShip)shipMountedTo).getRenderTransform();
         } else {
            shipTransform = shipMountedTo.getTransform();
         }

         Vector3dc basePos = shipTransform.getShipToWorld().transformPosition(shipMountedToData.getMountPosInShip(), new Vector3d());
         Vector3dc eyeRelativePos = shipTransform.getShipToWorldRotation().transform(new Vector3d(0.0, this.method_5751(), 0.0));
         class_243 newEyePos = VectorConversionsMCKt.toMinecraft(basePos.add(eyeRelativePos, new Vector3d()));
         cir.setReturnValue(newEyePos);
      }
   }

   @Inject(method = "getEyePosition()Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
   private void preGetEyePositionServer(CallbackInfoReturnable<class_243> cir) {
      ShipMountedToData shipMountedToData = VSGameUtilsKt.getShipMountedToData(class_1297.class.cast(this), null);
      if (shipMountedToData != null) {
         LoadedShip shipMountedTo = shipMountedToData.getShipMountedTo();
         ShipTransform shipTransform;
         if (shipMountedTo instanceof ClientShip) {
            shipTransform = ((ClientShip)shipMountedTo).getRenderTransform();
         } else {
            shipTransform = shipMountedTo.getShipTransform();
         }

         Vector3dc basePos = shipTransform.getShipToWorldMatrix().transformPosition(shipMountedToData.getMountPosInShip(), new Vector3d());
         Vector3dc eyeRelativePos = shipTransform.getShipCoordinatesToWorldCoordinatesRotation().transform(new Vector3d(0.0, this.method_5751(), 0.0));
         class_243 newEyePos = VectorConversionsMCKt.toMinecraft(basePos.add(eyeRelativePos, new Vector3d()));
         cir.setReturnValue(newEyePos);
      }
   }

   @Inject(method = "calculateViewVector", at = @At("HEAD"), cancellable = true)
   private void preCalculateViewVector(float xRot, float yRot, CallbackInfoReturnable<class_243> cir) {
      LoadedShip shipMountedTo = VSGameUtilsKt.getShipMountedTo(class_1297.class.cast(this));
      if (shipMountedTo != null) {
         float f = xRot * (float) (Math.PI / 180.0);
         float g = -yRot * (float) (Math.PI / 180.0);
         float h = class_3532.method_15362(g);
         float i = class_3532.method_15374(g);
         float j = class_3532.method_15362(f);
         float k = class_3532.method_15374(f);
         Vector3dc originalViewVector = new Vector3d(i * j, -k, h * j);
         ShipTransform shipTransform;
         if (shipMountedTo instanceof ClientShip) {
            shipTransform = ((ClientShip)shipMountedTo).getRenderTransform();
         } else {
            shipTransform = shipMountedTo.getTransform();
         }

         class_243 newViewVector = VectorConversionsMCKt.toMinecraft(shipTransform.getShipToWorldRotation().transform(originalViewVector, new Vector3d()));
         cir.setReturnValue(newViewVector);
      }
   }

   @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
   private void onShouldRender(double d, double e, double f, CallbackInfoReturnable<Boolean> cir) {
      if (this.draggingInformation.isEntityBeingDraggedByAShip()) {
         Ship ship = VSGameUtilsKt.getShipObjectWorld(this.field_6002).getAllShips().getById(this.draggingInformation.getLastShipStoodOn());
         if (ship != null) {
            ShipTransform shipTransform = ship instanceof ClientShip ? ((ClientShip)ship).getRenderTransform() : ship.getTransform();
            if (this.draggingInformation.getRelativePositionOnShip() != null) {
               Vector3dc redir = shipTransform.getShipToWorld().transformPosition(this.draggingInformation.getRelativePositionOnShip(), new Vector3d());
               double distX = redir.x() - d;
               double distY = redir.y() - e;
               double distZ = redir.z() - f;
               double sqrDist = distX * distX + distY * distY + distZ * distZ;
               cir.setReturnValue(this.method_5640(sqrDist));
            }
         }
      }
   }

   @Shadow
   public abstract class_238 method_5829();

   @Shadow
   protected abstract void method_5865(class_1297 var1, class_4738 var2);

   @Shadow
   protected abstract void method_5622(class_2680 var1);

   @Shadow
   public abstract double method_23321();

   @Shadow
   public abstract double method_23318();

   @Shadow
   public abstract double method_23317();

   @Shadow
   public abstract float method_5751();

   @Shadow
   public abstract class_1299<?> method_5864();

   @Shadow
   public abstract boolean method_5640(double var1);

   @Shadow
   public abstract void method_5762(double var1, double var3, double var5);

   @Shadow
   public abstract boolean method_31481();

   @Shadow
   public abstract void method_5796(boolean var1);

   @Shadow
   protected abstract boolean method_5795(int var1);

   @NotNull
   @Override
   public EntityDraggingInformation getDraggingInformation() {
      return this.draggingInformation;
   }

   @Override
   public boolean vs$shouldDrag() {
      return true;
   }

   @Override
   public boolean vs$isGliding() {
      return this.method_5795(7);
   }

   @Override
   public boolean vs$isInSealedArea() {
      return this.vs$isInSealedArea;
   }

   @Override
   public void vs$setInSealedArea(boolean inSealedArea) {
      this.vs$isInSealedArea = inSealedArea;
   }

   @Override
   public void vs$dragImmediately(Ship ship) {
      if (ship != null) {
         this.draggingInformation.setLastShipStoodOn(ship.getId());
         this.draggingInformation.setShouldImpulseMovement(false);
      }
   }
}
