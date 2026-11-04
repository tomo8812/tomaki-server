package net.caffeinemc.mods.lithium.mixin.world.inline_height;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin implements LevelHeightAccessor {
   @Shadow
   @Final
   Level level;

   public int getMaxBuildHeight() {
      return this.level.getMaxBuildHeight();
   }

   public int getSectionsCount() {
      return this.level.getSectionsCount();
   }

   public int getMinSection() {
      return this.level.getMinSection();
   }

   public int getMaxSection() {
      return this.level.getMaxSection();
   }

   public boolean isOutsideBuildHeight(BlockPos pos) {
      return this.level.isOutsideBuildHeight(pos);
   }

   public boolean isOutsideBuildHeight(int y) {
      return this.level.isOutsideBuildHeight(y);
   }

   public int getSectionIndex(int y) {
      return this.level.getSectionIndex(y);
   }

   public int getSectionIndexFromSectionY(int coord) {
      return this.level.getSectionIndexFromSectionY(coord);
   }

   public int getSectionYFromSectionIndex(int index) {
      return this.level.getSectionYFromSectionIndex(index);
   }
}
