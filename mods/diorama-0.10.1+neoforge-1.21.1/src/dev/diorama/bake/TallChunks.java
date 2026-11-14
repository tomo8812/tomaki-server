package dev.diorama.bake;

import dev.diorama.Projection;
import dev.diorama.data.ChunkSnapshot;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.jspecify.annotations.Nullable;

public final class TallChunks {
   public static final int ORDINARY_TOP = 320;
   final int missingTop;
   private final int firstChunkX;
   private final int[][] tall;

   private TallChunks(int missingTop, int firstChunkX, int[][] tall) {
      this.missingTop = missingTop;
      this.firstChunkX = firstChunkX;
      this.tall = tall;
   }

   static @Nullable TallChunks around(TileKey key, ChunkSource source, int startY) {
      if (startY <= 320) {
         return null;
      }

      int missingTop = Math.min(startY, Math.max(source.minY() + 1, TileBaker.localTop(key, source, 0, 320)));
      double minV = key.minV();
      double maxV = minV + 128.0;
      int cx0 = Math.floorDiv((int)Math.floor(key.minU()) - 1, 16);
      int cx1 = Math.floorDiv((int)Math.ceil(key.minU() + 128.0) + 1, 16);
      int cz0 = Math.floorDiv((int)Math.floor(Projection.zAt(minV - 1.0, missingTop)), 16);
      int cz1 = Math.floorDiv((int)Math.ceil(Projection.zAt(maxV + 1.0, startY)), 16);
      int[][] tall = new int[cx1 - cx0 + 1][];

      for (int cx = cx0; cx <= cx1; cx++) {
         IntArrayList column = new IntArrayList();

         for (int cz = cz1; cz >= cz0; cz--) {
            ChunkSnapshot chunk = source.chunk(cx, cz);
            if (chunk != null && chunk.maxY >= 320) {
               column.add(cz);
               column.add(chunk.firstSectionFrom(320));
               column.add(chunk.maxY + 1);
               column.add(chunk.lastSectionTopBelow(320));
            }
         }

         tall[cx - cx0] = column.toIntArray();
      }

      return new TallChunks(missingTop, cx0, tall);
   }

   int rayStart(int x, double v, double sin, double cos) {
      int[] chunks = this.tall[(x >> 4) - this.firstChunkX];

      for (int i = 0; i < chunks.length; i += 4) {
         double northEdge = ((chunks[i] << 4) * sin - v) / cos;
         double southEdge = ((chunks[i] + 1 << 4) * sin - v) / cos;
         if (northEdge < chunks[i + 2] && southEdge > chunks[i + 1]) {
            return Math.max(this.missingTop, Math.min(chunks[i + 2], (int)Math.ceil(southEdge)));
         }

         if (northEdge < chunks[i + 3]) {
            return Math.max(this.missingTop, Math.min(chunks[i + 3], (int)Math.ceil(southEdge)));
         }
      }

      return this.missingTop;
   }
}
