package net.caffeinemc.mods.lithium.common.tracking.block;

import it.unimi.dsi.fastutil.objects.Reference2DoubleArrayMap;
import net.caffeinemc.mods.lithium.common.block.BlockStateFlags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;

public final class BlockCache {
   private static final int MIN_DELAY = 180;
   private int initDelay;
   private AABB trackedPos;
   private SectionedBlockChangeTracker tracker = null;
   private long trackingSince;
   private boolean canSkipSupportingBlockSearch;
   private BlockState cachedSupportingBlock;
   private boolean canSkipBlockTouching;
   private byte cachedTouchingFireLava;
   private byte cachedIsSuffocating;
   private final Reference2DoubleArrayMap<TagKey<Fluid>> fluidType2FluidHeightMap;

   public BlockCache() {
      this.trackedPos = null;
      this.initDelay = 0;
      this.fluidType2FluidHeightMap = new Reference2DoubleArrayMap(2);
   }

   public boolean isTracking() {
      return this.tracker != null;
   }

   public void initTracking(Entity entity) {
      if (this.isTracking()) {
         throw new IllegalStateException("Cannot init cache that is already initialized!");
      }

      this.tracker = SectionedBlockChangeTracker.registerAt(entity.level(), entity.getBoundingBox(), BlockStateFlags.ANY);
      this.initDelay = 0;
      this.resetCachedInfo();
   }

   public void updateCache(Entity entity) {
      if (!this.isTracking() && this.initDelay < 180) {
         this.initDelay++;
      } else {
         AABB boundingBox = entity.getBoundingBox();
         if (boundingBox.equals(this.trackedPos)) {
            if (!this.isTracking()) {
               this.initTracking(entity);
            } else if (!this.tracker.isUnchangedSince(this.trackingSince)) {
               this.resetCachedInfo();
            }
         } else {
            if (this.isTracking() && !this.tracker.matchesMovedBox(boundingBox)) {
               this.tracker.unregister();
               this.tracker = null;
            }

            this.resetTrackedPos(boundingBox);
         }
      }
   }

   public void resetTrackedPos(AABB boundingBox) {
      this.trackedPos = boundingBox;
      this.initDelay = 0;
      this.resetCachedInfo();
   }

   public void resetCachedInfo() {
      this.trackingSince = !this.isTracking() ? Long.MIN_VALUE : this.tracker.getWorldTime();
      this.canSkipSupportingBlockSearch = false;
      this.cachedSupportingBlock = null;
      this.cachedIsSuffocating = -1;
      this.cachedTouchingFireLava = -1;
      this.canSkipBlockTouching = false;
      this.fluidType2FluidHeightMap.clear();
   }

   public void remove() {
      if (this.tracker != null) {
         this.tracker.unregister();
      }
   }

   public boolean canSkipBlockTouching() {
      return this.isTracking() && this.canSkipBlockTouching;
   }

   public void setCanSkipBlockTouching(boolean value) {
      this.canSkipBlockTouching = value;
   }

   public double getStationaryFluidHeightOrDefault(TagKey<Fluid> fluid, double defaultValue) {
      return this.isTracking() ? this.fluidType2FluidHeightMap.getOrDefault(fluid, defaultValue) : defaultValue;
   }

   public void setCachedFluidHeight(TagKey<Fluid> fluid, double fluidHeight) {
      this.fluidType2FluidHeightMap.put(fluid, fluidHeight);
   }

   public byte getIsTouchingFireLava() {
      return this.isTracking() ? this.cachedTouchingFireLava : -1;
   }

   public void setCachedTouchingFireLava(boolean b) {
      this.cachedTouchingFireLava = (byte)(b ? 1 : 0);
   }

   public byte getIsSuffocating() {
      return this.isTracking() ? this.cachedIsSuffocating : -1;
   }

   public void setCachedIsSuffocating(boolean b) {
      this.cachedIsSuffocating = (byte)(b ? 1 : 0);
   }

   public boolean canSkipSupportingBlockSearch() {
      return this.isTracking() && this.canSkipSupportingBlockSearch;
   }

   public void setCanSkipSupportingBlockSearch(boolean canSkip) {
      this.canSkipSupportingBlockSearch = canSkip;
      this.cachedSupportingBlock = null;
   }

   public void cacheSupportingBlock(BlockState blockState) {
      this.cachedSupportingBlock = blockState;
   }

   public BlockState getCachedSupportingBlock() {
      return !this.isTracking() ? null : this.cachedSupportingBlock;
   }
}
