package dev.diorama.craft;

import dev.diorama.bake.BlockLooks;
import dev.diorama.bake.CraftBlocks;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public interface Body {
   int MAX_CELLS = 4194304;

   UUID id();

   default long version() {
      return 0L;
   }

   Body.@Nullable Blocks read(BlockLooks var1);

   boolean pose(int[] var1, float var2, double[] var3);

   record Blocks(CraftBlocks blocks, int[] min) {
   }
}
