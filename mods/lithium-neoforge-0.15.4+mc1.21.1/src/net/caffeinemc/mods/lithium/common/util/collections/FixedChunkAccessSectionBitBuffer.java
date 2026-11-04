package net.caffeinemc.mods.lithium.common.util.collections;

import it.unimi.dsi.fastutil.ints.IntIterable;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.LongIterable;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.NotNull;

public class FixedChunkAccessSectionBitBuffer {
   public final int xMin;
   public final int yMin;
   public final int zMin;
   public final int xLength;
   public final int yLength;
   public final int zLength;
   public final int numChunks;
   public final int numSections;
   public final BitSet chunkSectionBits;
   public final ArrayList<ChunkAccess> chunkAccesses;

   public FixedChunkAccessSectionBitBuffer(int x0, int x1, int y0, int y1, int z0, int z1) {
      this.xMin = Math.min(x0, x1);
      this.yMin = Math.min(y0, y1);
      this.zMin = Math.min(z0, z1);
      this.xLength = Math.max(x0, x1) - this.xMin + 1;
      this.yLength = Math.max(y0, y1) - this.yMin + 1;
      this.zLength = Math.max(z0, z1) - this.zMin + 1;
      this.numChunks = this.xLength * this.zLength;
      this.numSections = this.yLength * this.xLength * this.zLength;
      this.chunkSectionBits = new BitSet(this.numSections);
      this.chunkAccesses = new ArrayList<>(Collections.nCopies(this.xLength * this.zLength, null));
   }

   public FixedChunkAccessSectionBitBuffer(BlockPos center, int horizontalRangeInclusive, int verticalRangeInclusive) {
      this(
         SectionPos.blockToSectionCoord(center.getX() - horizontalRangeInclusive),
         SectionPos.blockToSectionCoord(center.getX() + horizontalRangeInclusive),
         SectionPos.blockToSectionCoord(center.getY() - verticalRangeInclusive),
         SectionPos.blockToSectionCoord(center.getY() + verticalRangeInclusive),
         SectionPos.blockToSectionCoord(center.getZ() - horizontalRangeInclusive),
         SectionPos.blockToSectionCoord(center.getZ() + horizontalRangeInclusive)
      );
   }

   public int getSectionIndex(int x, int y, int z) {
      int dx = x - this.xMin;
      int dy = y - this.yMin;
      int dz = z - this.zMin;
      return (dx * this.zLength + dz) * this.yLength + dy;
   }

   public int getSectionIndex(long sectionPos) {
      return this.getSectionIndex(SectionPos.x(sectionPos), SectionPos.y(sectionPos), SectionPos.z(sectionPos));
   }

   public boolean getChunkSectionBit(BlockPos blockPos) {
      return this.getChunkSectionBit(
         SectionPos.blockToSectionCoord(blockPos.getX()), SectionPos.blockToSectionCoord(blockPos.getY()), SectionPos.blockToSectionCoord(blockPos.getZ())
      );
   }

   public boolean getChunkSectionBit(int chunkX, int chunkY, int chunkZ) {
      return this.chunkSectionBits.get(this.getSectionIndex(chunkX, chunkY, chunkZ));
   }

   public void setChunkSectionStatus(long sectionPos, boolean value) {
      this.chunkSectionBits.set(this.getSectionIndex(sectionPos), value);
   }

   public int getChunkIndex(int x, int z) {
      int dx = x - this.xMin;
      int dz = z - this.zMin;
      return dx * this.zLength + dz;
   }

   public int getChunkIndex(long chunkPos) {
      return this.getChunkIndex(ChunkPos.getX(chunkPos), ChunkPos.getZ(chunkPos));
   }

   public ChunkAccess getChunkAccess(long chunkPos) {
      return this.chunkAccesses.get(this.getChunkIndex(chunkPos));
   }

   public ChunkAccess getChunkAccess(BlockPos blockPos) {
      return this.getChunkAccess(ChunkPos.asLong(blockPos));
   }

   public void setChunkAccess(long chunkPos, ChunkAccess chunkAccess) {
      this.chunkAccesses.set(this.getChunkIndex(chunkPos), chunkAccess);
   }

   public void setChunkAccess(BlockPos blockPos, ChunkAccess chunkAccess) {
      this.setChunkAccess(ChunkPos.asLong(blockPos), chunkAccess);
   }

   public boolean hasNoTrueChunkSections() {
      return this.chunkSectionBits.nextSetBit(0) == -1;
   }

   public LongIterable getChunkPosInRange() {
      return new LongIterable() {
         @NotNull
         public LongIterator iterator() {
            return FixedChunkAccessSectionBitBuffer.this.getChunkPosInRangeIterator();
         }

         // $VF: synthetic method
         // $VF: bridge method
         @NotNull
         public Iterator iterator() {
            return this.iterator();
         }
      };
   }

   public LongIterator getChunkPosInRangeIterator() {
      final int xMin = this.xMin;
      final int xMax = this.xMin + this.xLength - 1;
      final int zMin = this.zMin;
      final int zMax = this.zMin + this.zLength - 1;
      return new LongIterator() {
         int x = xMin;
         int z = zMin;

         public long nextLong() {
            long result = ChunkPos.asLong(this.x, this.z);
            if (this.z < zMax) {
               this.z++;
            } else {
               this.z = zMin;
               this.x++;
            }

            return result;
         }

         public boolean hasNext() {
            return this.x <= xMax;
         }
      };
   }

   public IntIterable getSectionYInRange() {
      return new IntIterable() {
         @NotNull
         public IntIterator iterator() {
            return FixedChunkAccessSectionBitBuffer.this.getSectionYInRangeIterator();
         }

         // $VF: synthetic method
         // $VF: bridge method
         @NotNull
         public Iterator iterator() {
            return this.iterator();
         }
      };
   }

   public IntIterator getSectionYInRangeIterator() {
      final int yMin = this.yMin;
      final int yLimit = yMin + this.yLength;
      return new IntIterator() {
         int y = yMin;

         public int nextInt() {
            return this.y++;
         }

         public boolean hasNext() {
            return this.y < yLimit;
         }
      };
   }
}
