package maki.chunksmithfixes;

import java.util.concurrent.CompletionException;
import net.minecraft.server.level.ChunkResult;

public final class NativeChunkResult {
   private NativeChunkResult() {
   }

   public static Void complete(ChunkResult<?> result) {
      if (result != null && result.isSuccess()) {
         return null;
      } else {
         throw new CompletionException(
            new IllegalStateException("Chunksmith FULL request failed: " + (result == null ? "null ChunkResult" : result.getError()))
         );
      }
   }
}
