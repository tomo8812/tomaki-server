package org.valkyrienskies.mod.mixin.feature.block_placement;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1657;
import net.minecraft.class_1747;
import net.minecraft.class_1750;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3726;
import org.joml.Intersectiond;
import org.joml.Matrix4dc;
import org.joml.Vector3d;
import org.joml.primitives.AABBd;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.PlayerUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

@Mixin(class_1747.class)
public abstract class MixinBlockItem {
   @WrapOperation(
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/item/BlockItem;getPlacementState(Lnet/minecraft/world/item/context/BlockPlaceContext;)Lnet/minecraft/world/level/block/state/BlockState;"
      ),
      method = "place"
   )
   private class_2680 transformPlayerWhenPlacing(class_1747 _instance, class_1750 _ctx, Operation<class_2680> original, class_1750 ctx) {
      return ctx != null && ctx.method_8036() != null
         ? PlayerUtil.transformPlayerTemporarily(
            ctx.method_8036(), ctx.method_8045(), ctx.method_8037(), () -> (class_2680)original.call(new Object[]{this, ctx})
         )
         : (class_2680)original.call(new Object[]{this, ctx});
   }

   @WrapOperation(
      method = "canPlace",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/Level;isUnobstructed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Z"
      )
   )
   private boolean checkObstructionByPlayer(
      class_1937 level,
      class_2680 blockState,
      class_2338 blockPos,
      class_3726 ctx,
      Operation<Boolean> original,
      @Local(argsOnly = true) class_1750 blockPlaceContext
   ) {
      boolean result = (Boolean)original.call(new Object[]{level, blockState, blockPos, ctx});
      if (result) {
         Ship ship = VSGameUtilsKt.getShipManagingPos(level, blockPos);
         if (ship != null) {
            class_265 voxelShape = blockState.method_26194(level, blockPos, ctx);
            class_1657 player = blockPlaceContext.method_8036();
            if (player != null
               && !player.method_31481()
               && player.field_23807
               && !voxelShape.method_1110()
               && vs_testObAab(
                  VectorConversionsMCKt.toJOML(voxelShape.method_1107().method_996(blockPos)),
                  ship.getShipToWorld(),
                  VectorConversionsMCKt.toJOML(player.method_5829().method_1011(0.1))
               )) {
               return false;
            }
         }
      }

      return result;
   }

   @Unique
   private static boolean vs_testObAab(AABBd aabb, Matrix4dc transform, AABBd other) {
      Vector3d tX = new Vector3d(transform.m00(), transform.m01(), transform.m02()).normalize();
      Vector3d tY = new Vector3d(transform.m10(), transform.m11(), transform.m12()).normalize();
      Vector3d tZ = new Vector3d(transform.m20(), transform.m21(), transform.m22()).normalize();
      Vector3d transformedCenter = transform.transformPosition(aabb.center(new Vector3d()));
      Vector3d wX = new Vector3d(1.0, 0.0, 0.0);
      Vector3d wY = new Vector3d(0.0, 1.0, 0.0);
      Vector3d wZ = new Vector3d(0.0, 0.0, 1.0);
      return Intersectiond.testObOb(
         transformedCenter, tX, tY, tZ, aabb.getSize(new Vector3d()).mul(0.5), other.center(new Vector3d()), wX, wY, wZ, other.getSize(new Vector3d()).mul(0.5)
      );
   }
}
