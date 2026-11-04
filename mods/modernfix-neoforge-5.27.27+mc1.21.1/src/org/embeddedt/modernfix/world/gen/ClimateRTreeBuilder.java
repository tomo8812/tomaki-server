package org.embeddedt.modernfix.world.gen;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.level.biome.Climate.Parameter;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.Climate.RTree.Leaf;
import net.minecraft.world.level.biome.Climate.RTree.Node;
import net.minecraft.world.level.biome.Climate.RTree.SubTree;

public final class ClimateRTreeBuilder<T> {
   private static final int P = 7;
   private static final int MAX_RANK_TABLE = 65536;
   private final int n;
   private final Leaf<T>[] leaves;
   private final long[][] boxMin = new long[7][];
   private final long[][] boxMax = new long[7][];
   private final long[][] mid = new long[7][];
   private final long[] absMidSum;
   private final long[] midLo = new long[7];
   private final long[] midHi = new long[7];
   private final long[] rootSpan = new long[14];
   private final int[][] rank = new int[7][];
   private final int[][] rankStart = new int[7][];
   private final int[][] rankTable = new int[7][];
   private final int[][] sorted = new int[7][];
   private final int[] bucketOf;
   private final int[] scratch;

   private ClimateRTreeBuilder(List<Pair<ParameterPoint, T>> points) {
      this.n = points.size();
      this.leaves = new Leaf[this.n];

      for (int d = 0; d < 7; d++) {
         this.boxMin[d] = new long[this.n];
         this.boxMax[d] = new long[this.n];
         this.mid[d] = new long[this.n];
      }

      this.absMidSum = new long[this.n];
      this.bucketOf = new int[this.n];
      this.scratch = new int[this.n];
      Arrays.fill(this.midLo, Long.MAX_VALUE);
      Arrays.fill(this.midHi, Long.MIN_VALUE);
      Arrays.fill(this.rootSpan, 0, 7, Long.MAX_VALUE);
      Arrays.fill(this.rootSpan, 7, 14, Long.MIN_VALUE);

      for (int i = 0; i < this.n; i++) {
         this.addLeaf(i, points.get(i));
      }

      this.computeRanks();
      this.sortAxes();
   }

   public static <T> Node<T> build(List<Pair<ParameterPoint, T>> points) {
      ClimateRTreeBuilder<T> builder = new ClimateRTreeBuilder<>(points);
      int n = points.size();
      if (n == 1) {
         return builder.leaves[0];
      }

      if (n > 6) {
         return builder.build(0, n, builder.rootSpan, 0);
      }

      int[] identity = new int[n];
      int i = 0;

      while (i < n) {
         identity[i] = i++;
      }

      return builder.buildSmall(identity, 0, n, builder.rootSpan, 0);
   }

   private void addLeaf(int i, Pair<ParameterPoint, T> pair) {
      ParameterPoint point = (ParameterPoint)pair.getFirst();
      this.leaves[i] = new Leaf(point, pair.getSecond());
      this.setAxis(i, 0, point.temperature().min(), point.temperature().max());
      this.setAxis(i, 1, point.humidity().min(), point.humidity().max());
      this.setAxis(i, 2, point.continentalness().min(), point.continentalness().max());
      this.setAxis(i, 3, point.erosion().min(), point.erosion().max());
      this.setAxis(i, 4, point.depth().min(), point.depth().max());
      this.setAxis(i, 5, point.weirdness().min(), point.weirdness().max());
      this.setAxis(i, 6, point.offset(), point.offset());
      long sum = 0L;

      for (int d = 0; d < 7; d++) {
         sum += Math.abs(this.mid[d][i]);
      }

      this.absMidSum[i] = sum;
   }

   private void setAxis(int i, int d, long min, long max) {
      long m = (min + max) / 2L;
      this.boxMin[d][i] = min;
      this.boxMax[d][i] = max;
      this.mid[d][i] = m;
      if (m < this.midLo[d]) {
         this.midLo[d] = m;
      }

      if (m > this.midHi[d]) {
         this.midHi[d] = m;
      }

      if (min < this.rootSpan[d]) {
         this.rootSpan[d] = min;
      }

      if (max > this.rootSpan[7 + d]) {
         this.rootSpan[7 + d] = max;
      }
   }

   private void computeRanks() {
      long[][] distinct = new long[7][];
      int[] distinctCount = new int[7];

      for (int d = 0; d < 7; d++) {
         this.rank[d] = new int[this.n];
         if (this.midHi[d] - this.midLo[d] < 65536L) {
            this.rankTable[d] = new int[(int)(this.midHi[d] - this.midLo[d]) + 1];
            distinct[d] = new long[16];
         }
      }

      for (int i = 0; i < this.n; i++) {
         this.collectDistinct(i, distinct, distinctCount);
      }

      for (int d = 0; d < 7; d++) {
         if (this.rankTable[d] != null) {
            Arrays.sort(distinct[d], 0, distinctCount[d]);

            for (int r = 0; r < distinctCount[d]; r++) {
               this.rankTable[d][(int)(distinct[d][r] - this.midLo[d])] = r + 1;
            }
         } else {
            long[] values = (long[])this.mid[d].clone();
            Arrays.sort(values);
            int count = 0;

            for (int i = 0; i < this.n; i++) {
               if (count == 0 || values[i] != values[count - 1]) {
                  values[count++] = values[i];
               }
            }

            distinct[d] = values;
            distinctCount[d] = count;
         }

         this.rankStart[d] = new int[distinctCount[d]];
      }

      for (int i = 0; i < this.n; i++) {
         this.assignRanks(i, distinct, distinctCount);
      }

      for (int d = 0; d < 7; d++) {
         int[] start = this.rankStart[d];
         int sum = 0;

         for (int r = 0; r < start.length; r++) {
            int count = start[r];
            start[r] = sum;
            sum += count;
         }
      }
   }

   private void collectDistinct(int i, long[][] distinct, int[] distinctCount) {
      for (int d = 0; d < 7; d++) {
         int[] table = this.rankTable[d];
         if (table != null) {
            int index = (int)(this.mid[d][i] - this.midLo[d]);
            if (table[index] == 0) {
               table[index] = -1;
               if (distinctCount[d] == distinct[d].length) {
                  distinct[d] = Arrays.copyOf(distinct[d], distinctCount[d] * 2);
               }

               distinct[d][distinctCount[d]++] = this.mid[d][i];
            }
         }
      }
   }

   private void assignRanks(int i, long[][] distinct, int[] distinctCount) {
      for (int d = 0; d < 7; d++) {
         int[] table = this.rankTable[d];
         int r = table != null ? table[(int)(this.mid[d][i] - this.midLo[d])] - 1 : Arrays.binarySearch(distinct[d], 0, distinctCount[d], this.mid[d][i]);
         this.rank[d][i] = r;
         this.rankStart[d][r]++;
      }
   }

   private void sortAxes() {
      int[] order = new int[this.n];
      int i = 0;

      while (i < this.n) {
         order[i] = i++;
      }

      for (int d = 6; d >= 0; d--) {
         order = this.countingPass(order, d);
      }

      this.sorted[0] = order;

      for (int a = 6; a >= 1; a--) {
         this.sorted[a] = this.countingPass(this.sorted[(a + 1) % 7], a);
      }
   }

   private int[] countingPass(int[] src, int d) {
      int[] next = (int[])this.rankStart[d].clone();
      int[] ranks = this.rank[d];
      int[] dst = new int[this.n];

      for (int i = 0; i < this.n; i++) {
         int id = src[i];
         dst[next[ranks[id]]++] = id;
      }

      return dst;
   }

   private Node<T> build(int from, int to, long[] span, int incomingAxis) {
      int count = to - from;
      if (count <= 6) {
         return this.buildSmall(this.sorted[incomingAxis], from, to, span, 0);
      }

      int bucketSize = (int)Math.pow(6.0, Math.floor(Math.log(count - 0.01) / Math.log(6.0)));
      int bucketCount = (count + bucketSize - 1) / bucketSize;
      long bestCost = Long.MAX_VALUE;
      int bestAxis = -1;
      long[] bestBounds = null;
      long[] bounds = new long[bucketCount * 7 * 2];

      for (int axis = 0; axis < 7; axis++) {
         long cost = this.computeBucketBounds(this.sorted[axis], from, to, bucketSize, bucketCount, bounds, span);
         if (bestCost > cost) {
            bestCost = cost;
            bestAxis = axis;
            bestBounds = (long[])bounds.clone();
         }
      }

      int[] bucketOrder = new int[bucketCount];
      int i = 0;

      while (i < bucketCount) {
         bucketOrder[i] = i++;
      }

      sortBuckets(bucketOrder, bestBounds, bestAxis);
      Node<T>[] children = new Node[bucketCount];
      int[] best = this.sorted[bestAxis];
      if (bucketSize <= 6) {
         for (int ix = 0; ix < bucketCount; ix++) {
            int bucket = bucketOrder[ix];
            int start = from + bucket * bucketSize;
            children[ix] = this.buildSmall(best, start, Math.min(to, start + bucketSize), bestBounds, bucket * 7 * 2);
         }
      } else {
         int[] bucketStart = new int[bucketCount];
         int offset = from;

         for (int ix = 0; ix < bucketCount; ix++) {
            int bucket = bucketOrder[ix];
            bucketStart[bucket] = offset;
            offset += Math.min(to, from + (bucket + 1) * bucketSize) - (from + bucket * bucketSize);
         }

         int bucket = 0;

         for (int ix = from; ix < to; bucket++) {
            for (int end = Math.min(to, ix + bucketSize); ix < end; ix++) {
               this.bucketOf[best[ix]] = bucket;
            }
         }

         for (int axis = 0; axis < 7; axis++) {
            this.partition(this.sorted[axis], from, to, bucketStart);
         }

         for (int ix = 0; ix < bucketCount; ix++) {
            int bucketx = bucketOrder[ix];
            int start = bucketStart[bucketx];
            int size = Math.min(to, from + (bucketx + 1) * bucketSize) - (from + bucketx * bucketSize);
            children[ix] = (Node<T>)(size == 1
               ? this.leaves[this.sorted[0][start]]
               : this.build(start, start + size, Arrays.copyOfRange(bestBounds, bucketx * 7 * 2, (bucketx + 1) * 7 * 2), bestAxis));
         }
      }

      return new SubTree(toParameters(span, 0), Arrays.asList(children));
   }

   private Node<T> buildSmall(int[] order, int from, int to, long[] bounds, int boundsOffset) {
      int count = to - from;
      if (count == 1) {
         return this.leaves[order[from]];
      }

      int[] ids = Arrays.copyOfRange(order, from, to);

      for (int i = 1; i < count; i++) {
         int value = ids[i];

         int j;
         for (j = i - 1; j >= 0 && this.absMidSum[ids[j]] > this.absMidSum[value]; j--) {
            ids[j + 1] = ids[j];
         }

         ids[j + 1] = value;
      }

      Node<T>[] children = new Node[count];

      for (int i = 0; i < count; i++) {
         children[i] = this.leaves[ids[i]];
      }

      return new SubTree(toParameters(bounds, boundsOffset), Arrays.asList(children));
   }

   private void partition(int[] order, int from, int to, int[] bucketStart) {
      int[] next = (int[])bucketStart.clone();

      for (int i = from; i < to; i++) {
         int id = order[i];
         this.scratch[next[this.bucketOf[id]]++] = id;
      }

      System.arraycopy(this.scratch, from, order, from, to - from);
   }

   private long computeBucketBounds(int[] order, int from, int to, int bucketSize, int bucketCount, long[] bounds, long[] span) {
      long total = 0L;

      for (int bucket = 0; bucket < bucketCount; bucket++) {
         int start = from + bucket * bucketSize;
         int end = Math.min(to, start + bucketSize);
         int out = bucket * 7 * 2;

         for (int d = 0; d < 7; d++) {
            long min = scanMin(order, start, end, this.boxMin[d], span[d]);
            long max = scanMax(order, start, end, this.boxMax[d], span[7 + d]);
            bounds[out + d] = min;
            bounds[out + 7 + d] = max;
            total += Math.abs(max - min);
         }
      }

      return total;
   }

   private static long scanMin(int[] order, int start, int end, long[] values, long floor) {
      long min = Long.MAX_VALUE;

      for (int i = start; i < end; i++) {
         long value = values[order[i]];
         if (value < min) {
            min = value;
            if (min == floor) {
               break;
            }
         }
      }

      return min;
   }

   private static long scanMax(int[] order, int start, int end, long[] values, long ceiling) {
      long max = Long.MIN_VALUE;

      for (int i = start; i < end; i++) {
         long value = values[order[i]];
         if (value > max) {
            max = value;
            if (max == ceiling) {
               break;
            }
         }
      }

      return max;
   }

   private static List<Parameter> toParameters(long[] bounds, int offset) {
      Parameter[] parameters = new Parameter[7];

      for (int d = 0; d < 7; d++) {
         parameters[d] = new Parameter(bounds[offset + d], bounds[offset + 7 + d]);
      }

      return Arrays.asList(parameters);
   }

   private static void sortBuckets(int[] bucketOrder, long[] bounds, int axis) {
      for (int i = 1; i < bucketOrder.length; i++) {
         int value = bucketOrder[i];

         int j;
         for (j = i - 1; j >= 0 && compareBuckets(bucketOrder[j], value, bounds, axis) > 0; j--) {
            bucketOrder[j + 1] = bucketOrder[j];
         }

         bucketOrder[j + 1] = value;
      }
   }

   private static int compareBuckets(int left, int right, long[] bounds, int axis) {
      int leftBase = left * 7 * 2;
      int rightBase = right * 7 * 2;

      for (int i = 0; i < 7; i++) {
         int a = axis + i;
         if (a >= 7) {
            a -= 7;
         }

         long leftMid = Math.abs((bounds[leftBase + a] + bounds[leftBase + 7 + a]) / 2L);
         long rightMid = Math.abs((bounds[rightBase + a] + bounds[rightBase + 7 + a]) / 2L);
         int result = Long.compare(leftMid, rightMid);
         if (result != 0) {
            return result;
         }
      }

      return 0;
   }
}
