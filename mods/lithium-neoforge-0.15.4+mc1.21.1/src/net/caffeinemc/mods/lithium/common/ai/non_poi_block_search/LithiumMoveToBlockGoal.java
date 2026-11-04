package net.caffeinemc.mods.lithium.common.ai.non_poi_block_search;

import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;

public interface LithiumMoveToBlockGoal {
   boolean lithium$findNearestBlock(Predicate<BlockState> var1, BiPredicate<ChunkAccess, MutableBlockPos> var2, boolean var3);
}
