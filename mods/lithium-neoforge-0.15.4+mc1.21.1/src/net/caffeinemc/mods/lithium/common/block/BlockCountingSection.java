package net.caffeinemc.mods.lithium.common.block;

import net.minecraft.world.level.block.state.BlockState;

public interface BlockCountingSection {
   boolean lithium$mayContainAny(TrackedBlockStatePredicate var1);

   void lithium$trackBlockStateChange(BlockState var1, BlockState var2);
}
