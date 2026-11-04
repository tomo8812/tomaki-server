package net.caffeinemc.mods.lithium.mixin.world.inline_height;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Level.class)
public abstract class LevelMixin implements LevelHeightAccessor {
   private int bottomY;
   private int height;
   private int topYInclusive;

   @Inject(method = "<init>", at = @At("RETURN"))
   private void initHeightCache(
      WritableLevelData properties,
      ResourceKey<?> registryRef,
      RegistryAccess registryManager,
      Holder<DimensionType> dimensionEntry,
      Supplier<?> profiler,
      boolean isClient,
      boolean debugWorld,
      long biomeAccess,
      int maxChainedNeighborUpdates,
      CallbackInfo ci
   ) {
      this.height = ((DimensionType)dimensionEntry.value()).height();
      this.bottomY = ((DimensionType)dimensionEntry.value()).minY();
      this.topYInclusive = this.bottomY + this.height - 1;
   }

   public int getHeight() {
      return this.height;
   }

   public int getMinBuildHeight() {
      return this.bottomY;
   }

   public int getSectionsCount() {
      return (this.topYInclusive >> 4) + 1 - (this.bottomY >> 4);
   }

   public int getMinSection() {
      return this.bottomY >> 4;
   }

   public int getMaxSection() {
      return (this.topYInclusive >> 4) + 1;
   }

   public boolean isOutsideBuildHeight(BlockPos pos) {
      int y = pos.getY();
      return y < this.bottomY || y > this.topYInclusive;
   }

   public boolean isOutsideBuildHeight(int y) {
      return y < this.bottomY || y > this.topYInclusive;
   }

   public int getSectionIndex(int y) {
      return (y >> 4) - (this.bottomY >> 4);
   }

   public int getSectionIndexFromSectionY(int coord) {
      return coord - (this.bottomY >> 4);
   }

   public int getSectionYFromSectionIndex(int index) {
      return index + (this.bottomY >> 4);
   }

   public int getMaxBuildHeight() {
      return this.topYInclusive + 1;
   }
}
