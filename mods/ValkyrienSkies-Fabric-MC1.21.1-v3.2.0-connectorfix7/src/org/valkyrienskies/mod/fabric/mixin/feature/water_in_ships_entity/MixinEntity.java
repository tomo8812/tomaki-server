package org.valkyrienskies.mod.fabric.mixin.feature.water_in_ships_entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1297;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_3610;
import net.minecraft.class_3611;
import net.minecraft.class_6862;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

@Mixin(class_1297.class)
public abstract class MixinEntity {
   @Unique
   private boolean isModifyingWaterState = false;
   @Shadow
   public class_1937 field_6002;
   @Shadow
   private class_238 field_6005;
   @Unique
   private boolean isShipWater = false;
   @Unique
   private class_238 valkyrienskies$fluidPushAABB = null;

   @Shadow
   public abstract double method_23320();

   @Shadow
   public abstract double method_23317();

   @Shadow
   public abstract double method_23321();

   @Shadow
   public abstract boolean method_5692(class_6862<class_3611> var1, double var2);

   @Unique
   private boolean inShipContext() {
      return this.valkyrienskies$fluidPushAABB != null;
   }

   @ModifyVariable(method = "updateFluidHeightAndDoFluidPushing()V", at = @At("STORE"), remap = false)
   private class_238 setFluidPushInShipContext(class_238 original) {
      return this.inShipContext() ? this.valkyrienskies$fluidPushAABB : original;
   }

   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
      ),
      method = "updateFluidOnEyes"
   )
   private class_3610 getFluidStateRedirect(class_1937 level, class_2338 blockPos, Operation<class_3610> getFluidState) {
      class_3610[] fluidState = new class_3610[]{(class_3610)getFluidState.call(new Object[]{level, blockPos})};
      this.isShipWater = false;
      if (fluidState[0].method_15769()) {
         double d = this.method_23320() - 0.11111111F;
         double origX = this.method_23317();
         double origY = d;
         double origZ = this.method_23321();
         VSGameUtilsKt.transformToNearbyShipsAndWorld(
            this.field_6002,
            origX,
            origY,
            origZ,
            this.field_6005.method_995(),
            (x, y, z) -> fluidState[0] = (class_3610)getFluidState.call(new Object[]{level, class_2338.method_49637(x, y, z)})
         );
         this.isShipWater = true;
      }

      return fluidState[0];
   }

   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/material/FluidState;getHeight(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)F"
      ),
      method = "updateFluidOnEyes"
   )
   private float fluidHeightOverride(class_3610 instance, class_1922 arg, class_2338 arg2, Operation<Float> getHeight) {
      return !instance.method_15769() && this.field_6002 instanceof class_1937 && this.isShipWater && instance.method_15771()
         ? 1.0F
         : (Float)getHeight.call(new Object[]{instance, arg, arg2});
   }

   @Unique
   @Inject(method = "updateFluidHeightAndDoFluidPushing()V", at = @At("TAIL"), remap = false)
   private void connectorfix$afterNeoForgeFluidScan(CallbackInfo var1) {
      if (this.valkyrienskies$fluidPushAABB == null) {
         if (!((IEntityDraggingInformationProvider)this).vs$isInSealedArea()) {
            VSGameUtilsKt.transformFromWorldToNearbyShips(this.field_6002, this.field_6005, this::connectorfix$scanShipAabb);
         }
      }
   }

   @Unique
   private void connectorfix$scanShipAabb(class_238 var1) {
      this.valkyrienskies$fluidPushAABB = var1;
      ((class_1297)this).updateFluidHeightAndDoFluidPushing();
      this.valkyrienskies$fluidPushAABB = null;
   }
}
