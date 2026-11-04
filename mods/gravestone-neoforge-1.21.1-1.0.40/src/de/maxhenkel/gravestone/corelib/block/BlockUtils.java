package de.maxhenkel.gravestone.corelib.block;

import net.minecraft.world.level.block.Block;

public class BlockUtils {
   public static boolean isAir(Block block) {
      return block.defaultBlockState().isAir();
   }
}
