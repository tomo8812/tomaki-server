package org.valkyrienskies.core.impl.shadow;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleUnaryOperator;
import kotlin.Pair;
import kotlin.Triple;
import kotlin.Unit;
import kotlin.jvm.functions.Function4;
import org.joml.Vector3ic;
import org.valkyrienskies.core.api.events.MergeEvent;
import org.valkyrienskies.core.api.events.SplitEvent;
import org.valkyrienskies.core.api.ships.properties.ChunkClaim;
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus;
import org.valkyrienskies.core.api.world.connectivity.DoubleAugmentation;
import org.valkyrienskies.core.api.world.connectivity.DoubleComponentAugmentation;
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition;
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet;

public interface DT {
   void a(int var1, int var2, int var3, Long var4);

   // $VF: synthetic method
   static void a(DT var0, int var1, int var2, int var3, Long var4, int var5, Object var6) {
      var0.a(var1, var2, var3, (Long)null);
   }

   void a(int var1, int var2, int var3, long var4, Long var6);

   // $VF: synthetic method
   static void a(DT var0, int var1, int var2, int var3, long var4, Long var6, int var7, Object var8) {
      if (var8 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: addVoxel");
      }

      if ((var7 & 16) != 0) {
         var6 = null;
      }

      var0.a(var1, var2, var3, var4, var6);
   }

   void a(int var1, int var2, int var3);

   SparseVoxelPosition b(int var1, int var2, int var3);

   void c(int var1, int var2, int var3);

   ConnectionStatus a(int var1, int var2, int var3, int var4, int var5, int var6);

   ConnectionStatus d(int var1, int var2, int var3);

   void b(int var1, int var2, int var3, Long var4);

   // $VF: synthetic method
   static void b(DT var0, int var1, int var2, int var3, Long var4, int var5, Object var6) {
      if (var6 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: markChunkLoaded");
      }

      if ((var5 & 8) != 0) {
         var4 = null;
      }

      var0.b(var1, var2, var3, var4);
   }

   void c(int var1, int var2, int var3, Long var4);

   // $VF: synthetic method
   static void c(DT var0, int var1, int var2, int var3, Long var4, int var5, Object var6) {
      if (var6 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: markChunkUnloaded");
      }

      if ((var5 & 8) != 0) {
         var4 = null;
      }

      var0.c(var1, var2, var3, var4);
   }

   void d(int var1, int var2, int var3, Long var4);

   void a(int var1, int var2, int var3, In var4);

   void e(int var1, int var2, int var3);

   double a(DoubleAugmentation var1, int var2, int var3, int var4);

   void a(DoubleAugmentation var1, int var2, int var3, int var4, double var5);

   void a(DoubleAugmentation var1, int var2, int var3, int var4, DoubleUnaryOperator var5);

   void b(DoubleAugmentation var1, int var2, int var3, int var4);

   double c(DoubleAugmentation var1, int var2, int var3, int var4);

   boolean a(DoubleComponentAugmentation var1, int var2, int var3, int var4, double var5);

   double a(DoubleComponentAugmentation var1, int var2, int var3, int var4);

   boolean b(DoubleComponentAugmentation var1, int var2, int var3, int var4);

   long f(int var1, int var2, int var3);

   Map<Triple<Integer, Integer, Integer>, Double> a(DoubleAugmentation var1, ChunkClaim var2);

   // $VF: synthetic method
   static Map a(DT var0, DoubleAugmentation var1, ChunkClaim var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: gatherFromEachComponent");
      }

      if ((var3 & 2) != 0) {
         var2 = null;
      }

      return var0.a(var1, var2);
   }

   Map<Triple<Integer, Integer, Integer>, Double> a(DoubleComponentAugmentation var1, ChunkClaim var2);

   // $VF: synthetic method
   static Map a(DT var0, DoubleComponentAugmentation var1, ChunkClaim var2, int var3, Object var4) {
      if (var4 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: gatherFromEachComponentRoot");
      }

      if ((var3 & 2) != 0) {
         var2 = null;
      }

      return var0.a(var1, var2);
   }

   Set<Vector3ic> a(ChunkClaim var1);

   void a(int var1, int var2, int var3, Function4<? super Integer, ? super Integer, ? super Integer, ? super Long, Unit> var4);

   DenseBlockPosSet a(int var1, int var2, int var3, Set<Vector3ic> var4);

   // $VF: synthetic method
   static DenseBlockPosSet a(DT var0, int var1, int var2, int var3, Set var4, int var5, Object var6) {
      if (var6 != null) {
         throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getComponentBlocks");
      }

      if ((var5 & 8) != 0) {
         var4 = new DenseBlockPosSet();
      }

      return var0.a(var1, var2, var3, var4);
   }

   Set<SparseVoxelPosition> b(int var1, int var2, int var3, Set<SparseVoxelPosition> var4);

   Pair<HashSet<SplitEvent>, HashSet<MergeEvent>> a();
}
