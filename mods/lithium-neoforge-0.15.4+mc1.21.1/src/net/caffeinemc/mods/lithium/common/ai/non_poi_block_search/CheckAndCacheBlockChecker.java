package net.caffeinemc.mods.lithium.common.ai.non_poi_block_search;

import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.caffeinemc.mods.lithium.common.util.collections.FixedChunkAccessSectionBitBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class CheckAndCacheBlockChecker {
   private final FixedChunkAccessSectionBitBuffer chunkSections2MaybeContainsMatchingBlock;
   private final LevelReader levelReader;
   public final boolean shouldChunkLoad;
   public final Predicate<BlockState> blockStatePredicate;
   private int unloadedPossibleChunkSections = 0;
   public final int minSectionY;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !CheckAndCacheBlockChecker.class.desiredAssertionStatus();

   public CheckAndCacheBlockChecker(
      BlockPos origin,
      int horizontalRangeInclusive,
      int verticalRangeInclusive,
      LevelReader levelReader,
      Predicate<BlockState> blockStatePredicate,
      boolean shouldChunkLoad
   ) {
      this.chunkSections2MaybeContainsMatchingBlock = new FixedChunkAccessSectionBitBuffer(origin, horizontalRangeInclusive, verticalRangeInclusive);
      this.levelReader = levelReader;
      this.shouldChunkLoad = shouldChunkLoad;
      this.blockStatePredicate = blockStatePredicate;
      this.minSectionY = levelReader.getMinSection();
   }

   public void initializeChunks() {
      this.initializeChunks(null);
   }

   public void initializeChunks(Consumer<Long> chunkCollector) {
      boolean nullChunkCollector = chunkCollector == null;
      LongIterator var3 = this.chunkSections2MaybeContainsMatchingBlock.getChunkPosInRange().iterator();

      while (var3.hasNext()) {
         long chunkPos = (Long)var3.next();
         int x = ChunkPos.getX(chunkPos);
         int z = ChunkPos.getZ(chunkPos);
         boolean chunkMaybeHas = false;
         ChunkAccess chunkAccess = this.levelReader.getChunk(x, z, ChunkStatus.FULL, false);
         if (chunkAccess != null) {
            this.chunkSections2MaybeContainsMatchingBlock.setChunkAccess(chunkPos, chunkAccess);
            IntIterator var12 = this.chunkSections2MaybeContainsMatchingBlock.getSectionYInRange().iterator();

            while (var12.hasNext()) {
               int y = (Integer)var12.next();
               chunkMaybeHas = this.checkChunkSection(chunkAccess, x, y, z) || chunkMaybeHas;
            }
         } else if (this.shouldChunkLoad) {
            for (IntIterator var10 = this.chunkSections2MaybeContainsMatchingBlock.getSectionYInRange().iterator();
               var10.hasNext();
               this.unloadedPossibleChunkSections++
            ) {
               int y = (Integer)var10.next();
               this.chunkSections2MaybeContainsMatchingBlock
                  .setChunkSectionStatus(SectionPos.asLong(x, y, z), !this.levelReader.isOutsideBuildHeight(SectionPos.sectionToBlockCoord(y)));
            }

            chunkMaybeHas = true;
         }

         if (!nullChunkCollector && chunkMaybeHas) {
            chunkCollector.accept(chunkPos);
         }
      }
   }

   public int getChunkSize() {
      return this.chunkSections2MaybeContainsMatchingBlock.numChunks;
   }

   public boolean hasUnloadedPossibleChunks() {
      return this.unloadedPossibleChunkSections > 0;
   }

   private boolean checkChunkSection(ChunkAccess chunkAccess, int chunkX, int chunkY, int chunkZ) {
      int chunkSectionYIndex = chunkY - this.minSectionY;
      LevelChunkSection[] chunkSections = chunkAccess.getSections();
      if (chunkSectionYIndex >= 0 && chunkSectionYIndex < chunkSections.length && chunkSections[chunkSectionYIndex].maybeHas(this.blockStatePredicate)) {
         this.chunkSections2MaybeContainsMatchingBlock.setChunkSectionStatus(SectionPos.asLong(chunkX, chunkY, chunkZ), true);
         return true;
      } else {
         return false;
      }
   }

   public boolean checkCachedSection(int chunkX, int chunkY, int chunkZ) {
      return this.chunkSections2MaybeContainsMatchingBlock.getChunkSectionBit(chunkX, chunkY, chunkZ);
   }

   public ChunkAccess getCachedChunkAccess(long chunkPos) {
      return this.chunkSections2MaybeContainsMatchingBlock.getChunkAccess(chunkPos);
   }

   public ChunkAccess getCachedChunkAccess(BlockPos blockPos) {
      return this.chunkSections2MaybeContainsMatchingBlock.getChunkAccess(blockPos);
   }

   public boolean shouldStop() {
      return this.chunkSections2MaybeContainsMatchingBlock.hasNoTrueChunkSections();
   }

   public boolean checkPosition(BlockPos blockPos) {
      if (!this.chunkSections2MaybeContainsMatchingBlock.getChunkSectionBit(blockPos)) {
         return false;
      }

      ChunkAccess chunkAccess = this.chunkSections2MaybeContainsMatchingBlock.getChunkAccess(blockPos);
      if (chunkAccess == null) {
         if (!this.shouldChunkLoad) {
            return false;
         }

         int chunkX = SectionPos.blockToSectionCoord(blockPos.getX());
         int chunkY = SectionPos.blockToSectionCoord(blockPos.getY());
         int chunkZ = SectionPos.blockToSectionCoord(blockPos.getZ());
         chunkAccess = this.levelReader.getChunk(chunkX, chunkZ, ChunkStatus.FULL, true);
         assert chunkAccess != null;
         this.chunkSections2MaybeContainsMatchingBlock.setChunkAccess(blockPos, chunkAccess);
         if (!this.checkChunkSection(chunkAccess, chunkX, chunkY, chunkZ)) {
            this.unloadedPossibleChunkSections--;
            return false;
         }
      }

      return this.blockStatePredicate.test(chunkAccess.getBlockState(blockPos));
   }
}
