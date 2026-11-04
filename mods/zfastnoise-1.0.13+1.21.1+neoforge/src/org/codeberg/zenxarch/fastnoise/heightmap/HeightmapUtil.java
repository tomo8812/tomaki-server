package org.codeberg.zenxarch.fastnoise.heightmap;

import java.util.EnumSet;
import java.util.function.Predicate;
import net.minecraft.util.BitStorage;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.LinearPalette;
import net.minecraft.world.level.chunk.Palette;
import net.minecraft.world.level.chunk.SingleValuePalette;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import org.codeberg.zenxarch.fastnoise.mixin.HeightmapAccessor;

public final class HeightmapUtil {
   private HeightmapUtil() {
      throw new IllegalStateException("Utility class");
   }

   public static Types[] calculateHeightmaps(ChunkStatus status) {
      Types[] result = new Types[status.heightmapsAfter().size()];
      return status.heightmapsAfter().toArray(result);
   }

   public static boolean updateHeightmap(
      int lx, int lz, BitStorage storage, Predicate<BlockState> predicate, BlockState state, int y, LevelChunkSection[] sections
   ) {
      int idx = lx + (lz << 4);
      int current = storage.get(idx);
      if (y + 2 > current) {
         if (!predicate.test(state)) {
            storage.set(idx, getHeightmapY(idx, sections, y - 1, predicate));
            return false;
         }

         if (y >= current) {
            storage.set(idx, y + 1);
         }
      }

      return true;
   }

   private static int getHeightmapY(int hidx, LevelChunkSection[] sections, int startY, Predicate<BlockState> predicate) {
      int ly = startY & 15;

      for (int sy = startY >> 4; sy >= 0; sy--) {
         LevelChunkSection section = sections[sy];
         Palette<BlockState> palette = section.states.data.palette();
         BitStorage storage = section.states.data.storage();

         while (ly >= 0) {
            if (predicate.test((BlockState)palette.valueFor(storage.get((ly << 8) + hidx)))) {
               return (sy << 4) + ly + 1;
            }

            ly--;
         }

         ly = 15;
      }

      return 0;
   }

   private static int getLocalY(LevelChunkSection section, int hidx, HeightmapUtil.StatePredicateCache cache, Predicate<BlockState> predicate) {
      Palette<BlockState> palette = section.states.data.palette();
      BitStorage storage = section.states.data.storage();
      if (palette instanceof SingleValuePalette) {
         return -1;
      }

      LinearPalette<BlockState> array = (LinearPalette<BlockState>)palette;

      for (int ly = 15; ly >= 0; ly--) {
         int val = storage.get((ly << 8) + hidx);
         if (val != 0 && cache.get((BlockState)array.valueFor(val), predicate)) {
            return ly;
         }
      }

      return -1;
   }

   private static int findFirstNonEmptySection(LevelChunkSection[] sections, HeightmapUtil.StatePredicateCache cache, Predicate<BlockState> predicate) {
      for (int sy = sections.length - 1; sy >= 0; sy--) {
         Palette<BlockState> palette = sections[sy].states.data.palette();
         int psize = palette.getSize();
         if (psize != 1) {
            LinearPalette<BlockState> array = (LinearPalette<BlockState>)palette;

            for (int i = 1; i < array.size; i++) {
               if (cache.get((BlockState)array.valueFor(i), predicate)) {
                  return sy;
               }
            }
         }
      }

      return -1;
   }

   public static void populateHeightmapPostNoise(ChunkAccess chunk, Types typex, BlockState defaultBlockState, BlockState AIR) {
      Heightmap heightmap = chunk.getOrCreateHeightmapUnprimed(typex);
      Predicate<BlockState> predicate = ((HeightmapAccessor)heightmap).zenxarch$getBlockPredicate();
      BitStorage storage = ((HeightmapAccessor)heightmap).zenxarch$getStorage();
      if (predicate.test(AIR)) {
         Heightmap.primeHeightmaps(chunk, EnumSet.of(typex));
      } else {
         LevelChunkSection[] sections = chunk.getSections();
         HeightmapUtil.StatePredicateCache cache = new HeightmapUtil.StatePredicateCache();
         cache.get(defaultBlockState, predicate);
         int nonEmptySection = findFirstNonEmptySection(sections, cache, predicate);
         if (nonEmptySection != -1) {
            for (int hidx = 0; hidx < 256; hidx++) {
               for (int sy = nonEmptySection; sy >= 0; sy--) {
                  int ly = getLocalY(sections[sy], hidx, cache, predicate);
                  if (ly >= 0) {
                     storage.set(hidx, (sy << 4) + ly + 1);
                     break;
                  }
               }
            }
         }
      }
   }

   private static class StatePredicateCache {
      public BlockState[] states = new BlockState[16];
      public boolean[] values = new boolean[16];
      private int size = 0;

      public boolean get(BlockState state, Predicate<BlockState> predicate) {
         for (int i = 0; i < this.size; i++) {
            if (this.states[i] == state) {
               return this.values[i];
            }
         }

         this.states[this.size] = state;
         this.values[this.size] = predicate.test(state);
         return this.values[this.size++];
      }
   }
}
