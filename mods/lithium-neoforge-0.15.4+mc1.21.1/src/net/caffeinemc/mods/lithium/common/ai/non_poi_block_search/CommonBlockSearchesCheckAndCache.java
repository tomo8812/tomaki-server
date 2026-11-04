package net.caffeinemc.mods.lithium.common.ai.non_poi_block_search;

import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class CommonBlockSearchesCheckAndCache {
   public static Optional<BlockPos> blockPosFindClosestMatch(
      LevelReader levelReader,
      LivingEntity livingEntity,
      int horizontalRange,
      int verticalRange,
      Predicate<BlockState> blockStatePredicate,
      boolean shouldChunkLoad
   ) {
      BlockPos mobPos = livingEntity.blockPosition();
      CheckAndCacheBlockChecker checker = new CheckAndCacheBlockChecker(
         mobPos, horizontalRange, verticalRange, levelReader, blockStatePredicate, shouldChunkLoad
      );
      checker.initializeChunks();
      return checker.shouldStop() ? Optional.empty() : BlockPos.findClosestMatch(mobPos, horizontalRange, verticalRange, checker::checkPosition);
   }
}
