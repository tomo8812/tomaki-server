package maki.chunksmithcache;

import java.util.function.Consumer;
import net.minecraft.server.level.ChunkResult;

final class ChunkResultBridge {
   private ChunkResultBridge() {
   }

   static void ifSuccess(ChunkResult var0, Consumer var1) {
      var0.ifSuccess(var1);
   }
}
