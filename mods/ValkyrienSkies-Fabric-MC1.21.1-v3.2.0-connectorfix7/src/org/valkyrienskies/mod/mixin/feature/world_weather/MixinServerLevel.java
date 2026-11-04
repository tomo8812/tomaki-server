package org.valkyrienskies.mod.mixin.feature.world_weather;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.class_1959;
import net.minecraft.class_2338;
import net.minecraft.class_2818;
import net.minecraft.class_3218;
import net.minecraft.class_6880;
import net.minecraft.class_1959.class_1963;
import net.minecraft.class_2902.class_2903;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.CompatUtil;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(class_3218.class)
public abstract class MixinServerLevel {
   @WrapOperation(
      method = "tickChunk",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/server/level/ServerLevel;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;"
      )
   )
   private class_2338 occlude(
      class_3218 level,
      class_2903 types,
      class_2338 pos,
      Operation<class_2338> original,
      @Local(argsOnly = true) class_2818 chunk,
      @Share("ship") LocalRef<Ship> shipRef
   ) {
      class_2338 result = (class_2338)original.call(new Object[]{level, types, pos});
      class_2338 failure = class_2338.field_10980.method_10086(level.method_31607() - 1);
      Ship ship = VSGameUtilsKt.getShipManagingPos(level, chunk.method_12004());
      shipRef.set(ship);
      if (ship != null) {
         class_2338 worldPos = class_2338.method_49638(CompatUtil.INSTANCE.toSameSpaceAs(level, result.method_46558(), (Ship)null, ship));
         class_2338 worldHeight = (class_2338)original.call(new Object[]{level, types, worldPos});
         if (worldHeight.method_10264() > worldPos.method_10264()) {
            return failure;
         }
      }

      return result;
   }

   @WrapOperation(
      method = "tickChunk",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;getBiome(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/Holder;")
   )
   private class_6880<class_1959> useBiomeAtWorldPos(
      class_3218 level, class_2338 pos, Operation<class_6880<class_1959>> original, @Share("ship") LocalRef<Ship> shipRef
   ) {
      return (class_6880<class_1959>)original.call(
         new Object[]{level, class_2338.method_49638(CompatUtil.INSTANCE.toSameSpaceAs(level, pos.method_46558(), (Ship)null, (Ship)shipRef.get()))}
      );
   }

   @WrapOperation(
      method = "tickChunk",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/biome/Biome;getPrecipitationAt(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/biome/Biome$Precipitation;"
      )
   )
   private class_1963 usePrecipitationAtWorldPos(class_1959 instance, class_2338 pos, Operation<class_1963> original, @Share("ship") LocalRef<Ship> shipRef) {
      class_3218 level = class_3218.class.cast(this);
      return (class_1963)original.call(
         new Object[]{instance, class_2338.method_49638(CompatUtil.INSTANCE.toSameSpaceAs(level, pos.method_46558(), (Ship)null, (Ship)shipRef.get()))}
      );
   }
}
