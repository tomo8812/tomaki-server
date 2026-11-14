package com.kishku7.chunksmith.iterator;

import com.kishku7.chunksmith.Selection;

public final class ChunkIteratorFactory {
   private ChunkIteratorFactory() {
   }

   public static ChunkIterator getChunkIterator(Selection selection, long count) {
      if (selection.pattern().getType().equals("world")) {
         return new WorldChunkIterator(selection);
      }

      String shape = selection.shape();
      if (!"rectangle".equals(shape) && !"ellipse".equals(shape) && !"oval".equals(shape)) {
         return switch (selection.pattern().getType()) {
            case "loop" -> new Loop2ChunkIterator(selection, count);
            case "spiral" -> new SpiralChunkIterator(selection, count);
            case "csv" -> new CsvChunkIterator(selection, count);
            case "concentric" -> new ConcentricChunkIterator(selection, count);
            default -> new RegionChunkIterator(selection, count);
         };
      } else {
         return new Loop2ChunkIterator(selection, count);
      }
   }

   public static ChunkIterator getChunkIterator(Selection selection) {
      return getChunkIterator(selection, 0L);
   }
}
