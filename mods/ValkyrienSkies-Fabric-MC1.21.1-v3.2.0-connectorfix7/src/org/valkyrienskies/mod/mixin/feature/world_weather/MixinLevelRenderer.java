package org.valkyrienskies.mod.mixin.feature.world_weather;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_3610;
import net.minecraft.class_3965;
import net.minecraft.class_4538;
import net.minecraft.class_761;
import net.minecraft.class_2902.class_2903;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.CompatUtil;

@Mixin(class_761.class)
public class MixinLevelRenderer {
   @WrapOperation(
      method = "tickRain(Lnet/minecraft/client/Camera;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/LevelReader;getHeightmapPos(Lnet/minecraft/world/level/levelgen/Heightmap$Types;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/core/BlockPos;"
      )
   )
   private class_2338 includeShipsInWeatherTickOcclusion(
      class_4538 levelReader,
      class_2903 types,
      class_2338 pos,
      Operation<class_2338> original,
      @Share("weatherSurfaceLookupPos") LocalRef<class_2338> weatherSurfaceLookupPos
   ) {
      weatherSurfaceLookupPos.set(null);
      if (levelReader instanceof class_1937 level) {
         class_2338 vanillaHeight = (class_2338)original.call(new Object[]{levelReader, types, pos});
         class_2338 worldHeight = class_2338.method_49638(CompatUtil.INSTANCE.toSameSpaceAs(level, vanillaHeight.method_46558(), (Ship)null, null));
         class_3965 shipSurfaceHit = CompatUtil.INSTANCE.getShipHeightmapHitAboveWorldHeight(level, types, worldHeight);
         weatherSurfaceLookupPos.set(shipSurfaceHit == null ? null : shipSurfaceHit.method_17777().method_10062());
         return shipSurfaceHit == null
            ? worldHeight
            : new class_2338(
               worldHeight.method_10263(), class_3532.method_15357(Math.nextDown(shipSurfaceHit.method_17784().field_1351)) + 1, worldHeight.method_10260()
            );
      } else {
         return (class_2338)original.call(new Object[]{levelReader, types, pos});
      }
   }

   @WrapOperation(
      method = "tickRain(Lnet/minecraft/client/Camera;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/LevelReader;getBlockState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/block/state/BlockState;"
      )
   )
   private class_2680 includeShipsInWeatherTickSurfaceState(
      class_4538 levelReader, class_2338 pos, Operation<class_2680> original, @Share("weatherSurfaceLookupPos") LocalRef<class_2338> weatherSurfaceLookupPos
   ) {
      class_2338 lookupPos = (class_2338)weatherSurfaceLookupPos.get();
      return (class_2680)original.call(new Object[]{levelReader, lookupPos != null ? lookupPos : pos});
   }

   @WrapOperation(
      method = "tickRain(Lnet/minecraft/client/Camera;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/LevelReader;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;"
      )
   )
   private class_3610 includeShipsInWeatherTickSurfaceFluid(
      class_4538 levelReader, class_2338 pos, Operation<class_3610> original, @Share("weatherSurfaceLookupPos") LocalRef<class_2338> weatherSurfaceLookupPos
   ) {
      class_2338 lookupPos = (class_2338)weatherSurfaceLookupPos.get();
      return (class_3610)original.call(new Object[]{levelReader, lookupPos != null ? lookupPos : pos});
   }

   @WrapOperation(
      method = "tickRain(Lnet/minecraft/client/Camera;)V",
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/level/block/state/BlockState;getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;"
      )
   )
   private class_265 includeShipsInWeatherTickCollisionShape(
      class_2680 state,
      class_1922 levelReader,
      class_2338 pos,
      Operation<class_265> original,
      @Share("weatherSurfaceLookupPos") LocalRef<class_2338> weatherSurfaceLookupPos
   ) {
      class_2338 lookupPos = (class_2338)weatherSurfaceLookupPos.get();
      return (class_265)original.call(new Object[]{state, levelReader, lookupPos != null ? lookupPos : pos});
   }

   @WrapOperation(
      method = "renderSnowAndRain(Lnet/minecraft/client/renderer/LightTexture;FDDD)V",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getHeight(Lnet/minecraft/world/level/levelgen/Heightmap$Types;II)I")
   )
   private int includeShipsInWeatherRenderOcclusion(class_1937 level, class_2903 types, int x, int z, Operation<Integer> original) {
      return CompatUtil.INSTANCE.getWorldHeightIncludingShips(level, types, x, z);
   }
}
