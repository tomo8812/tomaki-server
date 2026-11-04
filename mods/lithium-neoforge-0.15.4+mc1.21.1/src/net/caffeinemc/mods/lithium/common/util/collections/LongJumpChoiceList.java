package net.caffeinemc.mods.lithium.common.util.collections;

import it.unimi.dsi.fastutil.bytes.ByteBytePair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.behavior.LongJumpToRandomPos.PossibleJump;

public class LongJumpChoiceList extends AbstractList<PossibleJump> {
   private static final ConcurrentHashMap<ByteBytePair, LongJumpChoiceList> CHOICE_LISTS = new ConcurrentHashMap<>();
   private static final LongJumpChoiceList FROG_JUMP = new LongJumpChoiceList((byte)4, (byte)2);
   private static final LongJumpChoiceList GOAT_JUMP = new LongJumpChoiceList((byte)5, (byte)5);
   private final BlockPos origin;
   private final IntArrayList[] packedOffsetsByDistanceSq;
   private final int[] weightByDistanceSq;
   private int totalWeight;

   public LongJumpChoiceList(byte horizontalRange, byte verticalRange) {
      if (horizontalRange >= 0 && verticalRange >= 0) {
         this.origin = BlockPos.ZERO;
         int maxSqDistance = horizontalRange * horizontalRange * 2 + verticalRange * verticalRange;
         this.packedOffsetsByDistanceSq = new IntArrayList[maxSqDistance];
         this.weightByDistanceSq = new int[maxSqDistance];

         for (int x = -horizontalRange; x <= horizontalRange; x++) {
            for (int y = -verticalRange; y <= verticalRange; y++) {
               for (int z = -horizontalRange; z <= horizontalRange; z++) {
                  int squaredDistance = x * x + y * y + z * z;
                  int index = squaredDistance - 1;
                  if (index >= 0) {
                     int packedOffset = this.packOffset(x, y, z);
                     IntArrayList offsets = this.packedOffsetsByDistanceSq[index];
                     if (offsets == null) {
                        this.packedOffsetsByDistanceSq[index] = offsets = new IntArrayList();
                     }

                     offsets.add(packedOffset);
                     this.weightByDistanceSq[index] = this.weightByDistanceSq[index] + squaredDistance;
                     this.totalWeight += squaredDistance;
                  }
               }
            }
         }
      } else {
         throw new IllegalArgumentException("The ranges must be within 0..127!");
      }
   }

   public LongJumpChoiceList(BlockPos origin, IntArrayList[] packedOffsetsByDistanceSq, int[] weightByDistanceSq, int totalWeight) {
      this.origin = origin;
      this.packedOffsetsByDistanceSq = packedOffsetsByDistanceSq;
      this.weightByDistanceSq = weightByDistanceSq;
      this.totalWeight = totalWeight;
   }

   private int packOffset(int x, int y, int z) {
      return x + 128 | y + 128 << 8 | z + 128 << 16;
   }

   private int unpackX(int packedOffset) {
      return (packedOffset & 0xFF) - 128;
   }

   private int unpackY(int packedOffset) {
      return (packedOffset >>> 8 & 0xFF) - 128;
   }

   private int unpackZ(int packedOffset) {
      return (packedOffset >>> 16 & 0xFF) - 128;
   }

   public static LongJumpChoiceList forCenter(BlockPos centerPos, byte horizontalRange, byte verticalRange) {
      if (horizontalRange >= 0 && verticalRange >= 0) {
         short range = (short)(horizontalRange << 8 | verticalRange);
         LongJumpChoiceList jumpDestinationsList;
         if (range == 1026) {
            jumpDestinationsList = FROG_JUMP;
         } else if (range == 1285) {
            jumpDestinationsList = GOAT_JUMP;
         } else {
            jumpDestinationsList = CHOICE_LISTS.computeIfAbsent(
               ByteBytePair.of(horizontalRange, verticalRange), key -> new LongJumpChoiceList(key.leftByte(), key.rightByte())
            );
         }

         return jumpDestinationsList.offsetCopy(centerPos);
      } else {
         throw new IllegalArgumentException("The ranges must be within 0..127!");
      }
   }

   private LongJumpChoiceList offsetCopy(BlockPos offset) {
      IntArrayList[] packedOffsetsByDistanceSq = new IntArrayList[this.packedOffsetsByDistanceSq.length];

      for (int i = 0; i < packedOffsetsByDistanceSq.length; i++) {
         IntArrayList packedOffsets = this.packedOffsetsByDistanceSq[i];
         if (packedOffsets != null) {
            packedOffsetsByDistanceSq[i] = packedOffsets.clone();
         }
      }

      return new LongJumpChoiceList(
         this.origin.offset(offset), packedOffsetsByDistanceSq, Arrays.copyOf(this.weightByDistanceSq, this.weightByDistanceSq.length), this.totalWeight
      );
   }

   public PossibleJump removeRandomWeightedByDistanceSq(RandomSource random) {
      int targetWeight = random.nextInt(this.totalWeight);

      for (int index = 0; targetWeight >= 0 && index < this.weightByDistanceSq.length; index++) {
         targetWeight -= this.weightByDistanceSq[index];
         if (targetWeight < 0) {
            int distanceSq = index + 1;
            IntArrayList elementsOfDistance = this.packedOffsetsByDistanceSq[index];
            int elementIndex = random.nextInt(elementsOfDistance.size());
            elementsOfDistance.set(elementIndex, elementsOfDistance.set(elementsOfDistance.size() - 1, elementsOfDistance.getInt(elementIndex)));
            int packedOffset = elementsOfDistance.removeInt(elementsOfDistance.size() - 1);
            this.weightByDistanceSq[index] = this.weightByDistanceSq[index] - distanceSq;
            this.totalWeight -= distanceSq;
            return new PossibleJump(this.origin.offset(this.unpackX(packedOffset), this.unpackY(packedOffset), this.unpackZ(packedOffset)), distanceSq);
         }
      }

      return null;
   }

   public PossibleJump get(int index) {
      int elementIndex = index;
      IntArrayList[] offsetsByDistanceSq = this.packedOffsetsByDistanceSq;

      for (int distanceSq = 0; distanceSq < offsetsByDistanceSq.length; distanceSq++) {
         IntArrayList packedOffsets = offsetsByDistanceSq[distanceSq];
         if (packedOffsets != null) {
            if (elementIndex < packedOffsets.size()) {
               int packedOffset = packedOffsets.getInt(elementIndex);
               return new PossibleJump(this.origin.offset(this.unpackX(packedOffset), this.unpackY(packedOffset), this.unpackZ(packedOffset)), distanceSq);
            }

            elementIndex -= packedOffsets.size();
         }
      }

      throw new IndexOutOfBoundsException();
   }

   @Override
   public boolean isEmpty() {
      return this.totalWeight == 0;
   }

   @Override
   public int size() {
      int size = 0;

      for (IntArrayList packedOffsets : this.packedOffsetsByDistanceSq) {
         if (packedOffsets != null) {
            size += packedOffsets.size();
         }
      }

      return size;
   }

   public PossibleJump remove(int index) {
      int elementIndex = index;
      IntArrayList[] offsetsByDistanceSq = this.packedOffsetsByDistanceSq;

      for (int distanceSq = 0; distanceSq < offsetsByDistanceSq.length; distanceSq++) {
         IntArrayList packedOffsets = offsetsByDistanceSq[distanceSq];
         if (packedOffsets != null) {
            if (elementIndex < packedOffsets.size()) {
               int packedOffset = packedOffsets.getInt(elementIndex);
               packedOffsets.set(elementIndex, packedOffsets.set(packedOffsets.size() - 1, packedOffsets.getInt(elementIndex)));
               packedOffsets.removeInt(packedOffsets.size() - 1);
               this.weightByDistanceSq[distanceSq] = this.weightByDistanceSq[distanceSq] - distanceSq;
               this.totalWeight -= distanceSq;
               return new PossibleJump(this.origin.offset(this.unpackX(packedOffset), this.unpackY(packedOffset), this.unpackZ(packedOffset)), distanceSq);
            }

            elementIndex -= packedOffsets.size();
         }
      }

      throw new IndexOutOfBoundsException();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object remove(int param1) {
      return this.remove(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object get(int param1) {
      return this.get(var1);
   }
}
