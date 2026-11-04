package org.valkyrienskies.mod.mixin.feature.explosions;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import java.util.Collections;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1927;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import net.minecraft.class_3726;
import net.minecraft.class_3959;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.impl.config.VSCoreConfig;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.config.VSGameConfig;
import org.valkyrienskies.mod.common.util.GameToPhysicsAdapter;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_1927.class)
public abstract class MixinExplosion {
   @Shadow
   @Final
   private class_1937 field_9187;
   @Shadow
   @Final
   @Mutable
   private double field_9195;
   @Shadow
   @Final
   @Mutable
   private double field_9192;
   @Shadow
   @Final
   @Mutable
   private double field_9189;
   @Shadow
   @Final
   @Mutable
   private float field_9190;
   @Unique
   private boolean isModifyingExplosion = false;

   @Shadow
   public abstract void method_8348();

   @Unique
   private void doExplodeForce() {
      if (!this.field_9187.field_9236) {
         Vector3d originPos = new Vector3d(this.field_9195, this.field_9192, this.field_9189);
         class_2338 explodePos = class_2338.method_49637(originPos.x(), originPos.y(), originPos.z());
         int radius = (int)Math.ceil(this.field_9190);

         for (int x = radius; x >= -radius; x--) {
            for (int y = radius; y >= -radius; y--) {
               for (int z = radius; z >= -radius; z--) {
                  class_3965 result = this.field_9187
                     .method_17742(
                        new class_3959(
                           class_243.method_24953(explodePos),
                           class_243.method_24953(explodePos.method_10069(x, y, z)),
                           class_3960.field_17558,
                           class_242.field_1348,
                           class_3726.method_16194()
                        )
                     );
                  if (result.method_17783() == class_240.field_1332) {
                     class_2338 blockPos = result.method_17777();
                     LoadedServerShip ship = VSGameUtilsKt.getLoadedShipManagingPos((class_3218)this.field_9187, blockPos);
                     if (ship != null) {
                        Vector3d forceVector = VectorConversionsMCKt.toJOML(class_243.method_24953(explodePos));
                        Double distanceMult = Math.max(
                           0.5, 1.0 - this.field_9190 / forceVector.distance(VectorConversionsMCKt.toJOML(class_243.method_24953(blockPos)))
                        );
                        Double powerMult = Math.max(0.1, this.field_9190 / 4.0F);
                        forceVector.sub(VectorConversionsMCKt.toJOML(class_243.method_24953(blockPos)));
                        forceVector.normalize();
                        forceVector.mul(-1.0 * VSGameConfig.SERVER.getExplosionBlastForce());
                        forceVector.mul(distanceMult);
                        forceVector.mul(powerMult);
                        Vector3dc modelPos = VectorConversionsMCKt.toJOML(class_243.method_24953(blockPos));
                        if (VSCoreConfig.SERVER.getSp().getEnableSplitting()) {
                           ValkyrienSkiesMod.splitHandler
                              .split(
                                 this.field_9187,
                                 ship.getId(),
                                 ship1 -> ValkyrienSkiesMod.getOrCreateGTPA(ship.getChunkClaimDimension())
                                    .applyWorldForceToModelPos(
                                       ship1.getId(),
                                       forceVector,
                                       ship1.getTransform()
                                          .getWorldToShip()
                                          .transformPosition(ship.getTransform().getShipToWorld().transformPosition(modelPos, new Vector3d()))
                                    )
                              );
                        }

                        GameToPhysicsAdapter forceApplier = ValkyrienSkiesMod.getOrCreateGTPA(ship.getChunkClaimDimension());
                        if (forceVector.isFinite()) {
                           forceApplier.applyWorldForceToModelPos(ship.getId(), forceVector, VectorConversionsMCKt.toJOML(class_243.method_24953(blockPos)));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Inject(at = @At("TAIL"), method = "explode")
   private void afterExplode(CallbackInfo ci) {
      if (this.isModifyingExplosion) {
         this.doExplodeForce();
      } else {
         this.isModifyingExplosion = true;
         double origX = this.field_9195;
         double origY = this.field_9192;
         double origZ = this.field_9189;
         VSGameUtilsKt.transformToNearbyShipsAndWorld(this.field_9187, this.field_9195, this.field_9192, this.field_9189, this.field_9190, (x, y, z) -> {
            this.field_9195 = x;
            this.field_9192 = y;
            this.field_9189 = z;
            this.method_8348();
         });
         this.field_9195 = origX;
         this.field_9192 = origY;
         this.field_9189 = origZ;
         this.isModifyingExplosion = false;
      }
   }

   @WrapOperation(method = "getSeenPercent", at = @At(value = "NEW", target = "Lnet/minecraft/world/level/ClipContext;"))
   private static class_3959 getSeenPercent$ClipContext$new(
      class_243 from, class_243 to, class_3960 blockClip, class_242 fluidClip, class_1297 source, Operation<class_3959> operation
   ) {
      if (source != null) {
         class_1937 level = source.method_37908();
         from = VSGameUtilsKt.toWorldCoordinates(level, from);
         to = VSGameUtilsKt.toWorldCoordinates(level, to);
      }

      return (class_3959)operation.call(new Object[]{from, to, blockClip, fluidClip, source});
   }

   @WrapOperation(
      method = "explode",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;)Ljava/util/List;"
      )
   )
   private List<class_1297> noRayTrace(class_1937 instance, class_1297 entity, class_238 aabb, Operation<List<class_1297>> getEntities) {
      return this.isModifyingExplosion ? Collections.emptyList() : (List)getEntities.call(new Object[]{instance, entity, aabb});
   }
}
