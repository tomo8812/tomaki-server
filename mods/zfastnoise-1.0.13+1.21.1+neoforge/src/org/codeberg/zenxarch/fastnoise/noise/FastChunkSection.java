package org.codeberg.zenxarch.fastnoise.noise;

import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.Palette.Factory;
import net.minecraft.world.level.chunk.PalettedContainer.Configuration;
import net.minecraft.world.level.chunk.PalettedContainer.Data;
import net.minecraft.world.level.chunk.PalettedContainer.Strategy;

public final class FastChunkSection {
   private final LevelChunkSection section;
   private int defaultIdx = 0;
   private int minIdx = 0;
   private final long[] storage;
   private final LinearPalette<BlockState> palette;
   private final BlockState[] states;
   private static final Factory ARRAY = Strategy.LINEAR_PALETTE_FACTORY;
   private static final Configuration<BlockState> ARRAY_4_TYPE = new Configuration(ARRAY, 4);

   public FastChunkSection(LevelChunkSection section) {
      this.section = section;
      this.states = new BlockState[16];
      this.states[0] = FastWorldgen.AIR;
      this.storage = new long[256];
      this.palette = new LinearPalette(this.section.states.registry, this.states, this.section.states, 4, 1);
      Data<BlockState> newData = new Data(ARRAY_4_TYPE, new SimpleBitStorage(4, 4096, this.storage), this.palette);
      section.states.data = newData;
   }

   public void setDefaultBlockState(int x, int y, int z, BlockState state) {
      if (this.defaultIdx == 0) {
         this.states[this.defaultIdx = this.palette.size++] = state;
      }

      this.setBlockState(x, y, z, this.defaultIdx);
   }

   private int getIndex(BlockState state) {
      if (this.minIdx == 0) {
         this.states[this.minIdx = this.palette.size++] = state;
         return this.minIdx;
      }

      for (int i = this.minIdx; i < this.palette.size; i++) {
         if (this.states[i] == state) {
            return i;
         }
      }

      this.states[this.palette.size] = state;
      return this.palette.size++;
   }

   public void setBlockState(int x, int y, int z, BlockState state) {
      int valIdx = this.getIndex(state);
      this.setBlockState(x, y, z, valIdx);
   }

   private void setBlockState(int x, int y, int z, long value) {
      this.storage[y << 4 | z] = this.storage[y << 4 | z] | value << (x << 2);
   }

   public void recalculateCounts() {
      this.section.recalcBlockCounts();
   }
}
