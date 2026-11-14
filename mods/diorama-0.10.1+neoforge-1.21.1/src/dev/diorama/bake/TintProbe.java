package dev.diorama.bake;

import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jspecify.annotations.Nullable;

final class TintProbe implements BlockAndTintGetter {
   private @Nullable ColorResolver asked;

   void reset() {
      this.asked = null;
   }

   boolean askedForBiome() {
      return this.asked != null;
   }

   @Nullable ColorResolver asked() {
      return this.asked;
   }

   byte kind() {
      ColorResolver resolver = this.asked;
      if (resolver == BiomeColors.GRASS_COLOR_RESOLVER) {
         return 1;
      } else if (resolver == BiomeColors.FOLIAGE_COLOR_RESOLVER) {
         return 2;
      } else {
         return (byte)(resolver != BiomeColors.WATER_COLOR_RESOLVER && resolver != BiomeTints.water() ? 0 : 4);
      }
   }

   public int getBlockTint(BlockPos pos, ColorResolver resolver) {
      if (this.asked == null) {
         this.asked = resolver;
      }

      return -1;
   }

   public @Nullable BlockEntity getBlockEntity(BlockPos pos) {
      return null;
   }

   public BlockState getBlockState(BlockPos pos) {
      return Blocks.AIR.defaultBlockState();
   }

   public FluidState getFluidState(BlockPos pos) {
      return Fluids.EMPTY.defaultFluidState();
   }

   public int getHeight() {
      return 384;
   }

   public int getMinBuildHeight() {
      return -64;
   }

   public @Nullable LevelLightEngine getLightEngine() {
      return null;
   }

   public int getBrightness(LightLayer layer, BlockPos pos) {
      return 15;
   }

   public int getRawBrightness(BlockPos pos, int darkening) {
      return 15 - darkening;
   }

   public float getShade(Direction direction, boolean shade) {
      return 1.0F;
   }
}
