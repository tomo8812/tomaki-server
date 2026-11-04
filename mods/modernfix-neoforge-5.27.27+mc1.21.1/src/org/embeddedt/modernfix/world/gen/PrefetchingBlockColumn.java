package org.embeddedt.modernfix.world.gen;

import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BlockColumn;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.Heightmap.Types;

public class PrefetchingBlockColumn implements BlockColumn {
   private static final BlockState AIR = Blocks.AIR.defaultBlockState();
   private static final BlockState VOID_AIR = Blocks.VOID_AIR.defaultBlockState();
   private final BlockState[] states;
   private final MutableBlockPos cursor = new MutableBlockPos();
   private ChunkAccess chunk;
   private LevelChunkSection[] sections;
   private int minBuildHeight;
   private int localX;
   private int localZ;

   public PrefetchingBlockColumn(int height) {
      this.states = new BlockState[height];
   }

   public int getExpectedHeight() {
      return this.states.length;
   }

   public void prefetch(ChunkAccess chunk, int localX, int localZ) {
      if (chunk.getHeight() != this.states.length) {
         throw new IllegalStateException();
      }

      this.chunk = chunk;
      this.sections = chunk.getSections();
      this.minBuildHeight = chunk.getMinBuildHeight();
      this.localX = localX;
      this.localZ = localZ;
      LevelChunkSection[] sections = this.sections;
      BlockState[] states = this.states;
      int offset = 0;

      for (LevelChunkSection section : sections) {
         if (section.hasOnlyAir()) {
            for (int y = 0; y < 16; y++) {
               states[offset + y] = AIR;
            }
         } else {
            PalettedContainer<BlockState> container = section.getStates();

            for (int y = 0; y < 16; y++) {
               states[offset + y] = (BlockState)container.get(localX, y, localZ);
            }
         }

         offset += 16;
      }
   }

   public void dispose() {
      this.chunk = null;
      this.sections = null;
   }

   public BlockState getBlock(int y) {
      int idx = y - this.minBuildHeight;
      return idx >= 0 && idx < this.states.length ? this.states[idx] : VOID_AIR;
   }

   private void markPostprocessing(int y) {
      this.cursor
         .set(SectionPos.sectionToBlockCoord(this.chunk.getPos().x, this.localX), y, SectionPos.sectionToBlockCoord(this.chunk.getPos().z, this.localZ));
      this.chunk.markPosForPostprocessing(this.cursor);
   }

   private void updateHeightmap(Types type, int y, BlockState newState) {
      this.chunk.getOrCreateHeightmapUnprimed(type).update(this.localX, y, this.localZ, newState);
   }

   public void setBlock(int y, BlockState state) {
      int idx = y - this.minBuildHeight;
      BlockState[] states = this.states;
      if (idx >= 0 && idx < states.length) {
         BlockState oldState = states[idx];
         if (oldState == state) {
            return;
         }

         states[idx] = state;
         int sectionIdx = idx >> 4;
         this.sections[sectionIdx].setBlockState(this.localX, y & 15, this.localZ, state, false);
         if (!state.getFluidState().isEmpty()) {
            this.markPostprocessing(y);
         }

         if (oldState.isAir() != state.isAir()) {
            this.updateHeightmap(Types.WORLD_SURFACE_WG, y, state);
         }

         if (oldState.blocksMotion() != state.blocksMotion()) {
            this.updateHeightmap(Types.OCEAN_FLOOR_WG, y, state);
         }
      }
   }
}
