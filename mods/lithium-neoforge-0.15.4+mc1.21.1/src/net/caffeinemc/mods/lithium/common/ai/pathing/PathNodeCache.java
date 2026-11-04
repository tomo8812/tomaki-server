package net.caffeinemc.mods.lithium.common.ai.pathing;

import net.caffeinemc.mods.lithium.common.block.BlockCountingSection;
import net.caffeinemc.mods.lithium.common.block.BlockStateFlags;
import net.caffeinemc.mods.lithium.common.util.Pos;
import net.caffeinemc.mods.lithium.common.world.ChunkView;
import net.caffeinemc.mods.lithium.common.world.WorldHelper;
import net.caffeinemc.mods.lithium.mixin.ai.pathing.PathfindingContextAccessor;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;

public abstract class PathNodeCache {
   private static boolean isChunkSectionDangerousNeighbor(LevelChunkSection section) {
      return section.getStates().maybeHas(state -> getNeighborPathNodeType(state) != PathType.OPEN);
   }

   public static PathType getPathNodeType(BlockState state) {
      return ((BlockStatePathingCache)state).lithium$getPathNodeType();
   }

   public static PathType getNeighborPathNodeType(BlockStateBase state) {
      return ((BlockStatePathingCache)state).lithium$getNeighborPathNodeType();
   }

   public static boolean isSectionSafeAsNeighbor(LevelChunkSection section) {
      if (section.hasOnlyAir()) {
         return true;
      } else {
         return BlockStateFlags.ENABLED
            ? !((BlockCountingSection)section).lithium$mayContainAny(BlockStateFlags.PATH_NOT_OPEN)
            : !isChunkSectionDangerousNeighbor(section);
      }
   }

   public static PathType getNodeTypeFromNeighbors(PathfindingContext context, int x, int y, int z, PathType fallback) {
      BlockGetter world = context.level();
      LevelChunkSection section = null;
      if (world instanceof ChunkView chunkView && WorldHelper.areNeighborsWithinSameChunkSection(x, y, z)) {
         if (!world.isOutsideBuildHeight(y)) {
            ChunkAccess chunk = chunkView.lithium$getLoadedChunk(Pos.ChunkCoord.fromBlockCoord(x), Pos.ChunkCoord.fromBlockCoord(z));
            if (chunk != null) {
               section = chunk.getSections()[Pos.SectionYIndex.fromBlockCoord(world, y)];
            }
         }

         if (section == null || isSectionSafeAsNeighbor(section)) {
            return fallback;
         }
      }

      int xStart = x - 1;
      int yStart = y - 1;
      int zStart = z - 1;
      int xEnd = x + 1;
      int yEnd = y + 1;
      int zEnd = z + 1;

      for (int adjX = xStart; adjX <= xEnd; adjX++) {
         for (int adjY = yStart; adjY <= yEnd; adjY++) {
            for (int adjZ = zStart; adjZ <= zEnd; adjZ++) {
               if (adjX != x || adjZ != z) {
                  BlockState state;
                  if (section != null) {
                     state = section.getBlockState(adjX & 15, adjY & 15, adjZ & 15);
                  } else {
                     MutableBlockPos pos = ((PathfindingContextAccessor)context).getLastNodePos().set(adjX, adjY, adjZ);
                     state = world.getBlockState(pos);
                  }

                  if (!state.isAir()) {
                     PathType neighborType = getNeighborPathNodeType(state);
                     if (neighborType == null) {
                        neighborType = WalkNodeEvaluator.checkNeighbourBlocks(context, adjX + 1, adjY + 1, adjZ + 1, null);
                        if (neighborType == null) {
                           neighborType = PathType.OPEN;
                        }
                     }

                     if (neighborType != PathType.OPEN) {
                        return neighborType;
                     }
                  }
               }
            }
         }
      }

      return fallback;
   }
}
