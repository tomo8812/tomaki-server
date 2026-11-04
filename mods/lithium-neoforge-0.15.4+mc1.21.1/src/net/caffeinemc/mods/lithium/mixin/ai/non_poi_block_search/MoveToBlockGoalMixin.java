package net.caffeinemc.mods.lithium.mixin.ai.non_poi_block_search;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongListIterator;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import net.caffeinemc.mods.lithium.common.ai.non_poi_block_search.CheckAndCacheBlockChecker;
import net.caffeinemc.mods.lithium.common.ai.non_poi_block_search.LithiumMoveToBlockGoal;
import net.caffeinemc.mods.lithium.common.ai.non_poi_block_search.NonPOISearchDistances;
import net.caffeinemc.mods.lithium.common.util.Pos;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MoveToBlockGoal.class)
public abstract class MoveToBlockGoalMixin implements LithiumMoveToBlockGoal {
   @Shadow
   @Final
   protected PathfinderMob mob;
   @Shadow
   @Final
   private int searchRange;
   @Shadow
   @Final
   private int verticalSearchRange;
   @Shadow
   protected int verticalSearchStart;
   @Shadow
   protected BlockPos blockPos;

   @Override
   public boolean lithium$findNearestBlock(
      Predicate<BlockState> requiredBlock, BiPredicate<ChunkAccess, MutableBlockPos> lithium$isValidTarget, boolean shouldChunkLoad
   ) {
      BlockPos center = this.mob.blockPosition().offset(0, -1, 0);
      LevelReader levelReader = this.mob.level();
      CheckAndCacheBlockChecker checker = new CheckAndCacheBlockChecker(
         center, this.searchRange - 1, this.verticalSearchRange, levelReader, requiredBlock, shouldChunkLoad
      );
      LongArrayList sortedChunksMaybeWithBlock = new LongArrayList(checker.getChunkSize());
      checker.initializeChunks(sortedChunksMaybeWithBlock::addLast);
      if (checker.shouldStop()) {
         return false;
      }

      int minY = Pos.BlockCoord.getMinY(levelReader);
      int maxY = Pos.BlockCoord.getMaxYInclusive(levelReader);
      return !checker.hasUnloadedPossibleChunks()
         ? this.lithium$chunkAwareSearch(center, lithium$isValidTarget, checker, sortedChunksMaybeWithBlock, minY, maxY)
         : this.lithium$vanillaOrderSearch(center, lithium$isValidTarget, checker, minY, maxY);
   }

   @Unique
   private boolean lithium$vanillaOrderSearch(
      BlockPos center, BiPredicate<ChunkAccess, MutableBlockPos> lithium$isValidTarget, CheckAndCacheBlockChecker checker, int minY, int maxY
   ) {
      MutableBlockPos currentPos = new MutableBlockPos();
      int centerY = center.getY();

      for (int layer = this.verticalSearchStart; layer <= this.verticalSearchRange; layer = layer > 0 ? -layer : 1 - layer) {
         int y = centerY + layer;
         if (y >= minY && y <= maxY) {
            for (int ring = 0; ring < this.searchRange; ring++) {
               for (int dX = 0; dX <= ring; dX = dX > 0 ? -dX : 1 - dX) {
                  for (int dZ = dX < ring && dX > -ring ? ring : 0; dZ <= ring; dZ = dZ > 0 ? -dZ : 1 - dZ) {
                     currentPos.setWithOffset(center, dX, layer, dZ);
                     if (this.mob.isWithinRestriction(currentPos) && checker.checkPosition(currentPos)) {
                        ChunkAccess chunkAccess = checker.getCachedChunkAccess(currentPos);
                        if (lithium$isValidTarget.test(chunkAccess, currentPos)) {
                           this.blockPos = currentPos;
                           return true;
                        }
                     }
                  }
               }
            }
         }
      }

      return false;
   }

   @Unique
   private boolean lithium$chunkAwareSearch(
      BlockPos center,
      BiPredicate<ChunkAccess, MutableBlockPos> lithium$isValidTarget,
      CheckAndCacheBlockChecker checker,
      LongArrayList sortedChunksMaybeWithBlock,
      int minY,
      int maxY
   ) {
      sortedChunksMaybeWithBlock.sort(
         (chunkLong0, chunkLong1) -> NonPOISearchDistances.MoveToBlockGoalDistances.getMinimumSortOrderOfChunk(center, chunkLong0)
            - NonPOISearchDistances.MoveToBlockGoalDistances.getMinimumSortOrderOfChunk(center, chunkLong1)
      );
      Predicate<BlockState> requiredBlock = checker.blockStatePredicate;
      int minSectionY = checker.minSectionY;
      MutableBlockPos foundPos = new MutableBlockPos();
      MutableBlockPos currentPos = new MutableBlockPos();

      for (int layer = this.verticalSearchStart; layer <= this.verticalSearchRange; layer = layer > 0 ? -layer : 1 - layer) {
         int y = center.getY() + layer;
         if (y >= minY && y <= maxY) {
            int chunkY = SectionPos.blockToSectionCoord(y);
            int ySectionIndex = chunkY - minSectionY;
            int closestFound = Integer.MAX_VALUE;
            int ringMax = this.searchRange - 1;
            LongListIterator var17 = sortedChunksMaybeWithBlock.iterator();

            while (var17.hasNext()) {
               long chunkPos = (Long)var17.next();
               int chunkX = ChunkPos.getX(chunkPos);
               int chunkZ = ChunkPos.getZ(chunkPos);
               if (closestFound < NonPOISearchDistances.MoveToBlockGoalDistances.getMinimumSortOrderOfChunk(center, chunkX, chunkZ)) {
                  break;
               }

               if (checker.checkCachedSection(chunkX, chunkY, chunkZ)) {
                  ChunkAccess chunkAccess = checker.getCachedChunkAccess(chunkPos);
                  int chunkBlockX = SectionPos.sectionToBlockCoord(chunkX);
                  int xMin = Math.max(center.getX() - ringMax, chunkBlockX);
                  int xMax = Math.min(center.getX() + ringMax, chunkBlockX + 15);
                  int chunkBlockZ = SectionPos.sectionToBlockCoord(chunkZ);
                  int zMin = Math.max(center.getZ() - ringMax, chunkBlockZ);
                  int zMax = Math.min(center.getZ() + ringMax, chunkBlockZ + 15);
                  LevelChunkSection levelChunkSection = chunkAccess.getSections()[ySectionIndex];

                  for (int z = zMin; z <= zMax; z++) {
                     for (int x = xMin; x <= xMax; x++) {
                        int dX = x - center.getX();
                        int dZ = z - center.getZ();
                        int ring = NonPOISearchDistances.MoveToBlockGoalDistances.getRing(dX, dZ);
                        int currentDistance = NonPOISearchDistances.MoveToBlockGoalDistances.getVanillaSortOrderInt(ring, dX, dZ);
                        if (currentDistance < closestFound
                           && this.mob.isWithinRestriction(currentPos.set(x, y, z))
                           && requiredBlock.test(levelChunkSection.getBlockState(x & 15, y & 15, z & 15))
                           && lithium$isValidTarget.test(chunkAccess, currentPos)) {
                           ringMax = ring;
                           xMin = Math.max(center.getX() - ringMax, chunkBlockX);
                           xMax = Math.min(center.getX() + ringMax, chunkBlockX + 15);
                           zMax = Math.min(center.getZ() + ringMax, chunkBlockZ + 15);
                           foundPos.set(x, y, z);
                           closestFound = currentDistance;
                        }
                     }
                  }
               }
            }

            if (closestFound < Integer.MAX_VALUE) {
               this.blockPos = foundPos;
               return true;
            }
         }
      }

      return false;
   }
}
