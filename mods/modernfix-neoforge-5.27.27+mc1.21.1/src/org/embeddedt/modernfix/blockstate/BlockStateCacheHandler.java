package org.embeddedt.modernfix.blockstate;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.embeddedt.modernfix.duck.IBlockState;

public class BlockStateCacheHandler {
   public static void invalidateCache() {
      synchronized (BlockStateBase.class) {
         for (BlockState blockState : Block.BLOCK_STATE_REGISTRY) {
            ((IBlockState)blockState).clearCache();
         }
      }
   }
}
