package org.valkyrienskies.core.impl.shadow

import java.util.HashSet
import java.util.LinkedHashMap
import java.util.function.DoubleUnaryOperator
import javax.inject.Inject
import javax.inject.Named
import kotlin.jvm.internal.TypeIntrinsics
import org.apache.logging.log4j.Logger
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.api.events.MergeEvent
import org.valkyrienskies.core.api.events.SplitEvent
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.world.LevelYRange
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus
import org.valkyrienskies.core.api.world.connectivity.DoubleAugmentation
import org.valkyrienskies.core.api.world.connectivity.DoubleComponentAugmentation
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet

public class DW {
   @JvmStatic
   public DW.a a = DW.a(null);
   private java.util.Map<java.lang.String, DP> b;
   private DZ c;
   private ChunkAllocatorProvider d;
   private java.util.Map<java.lang.String, DY> e;
   @JvmStatic
   private Logger f = Gb.a("(VS Core) 5G Cell Tower");

   @Inject
   fun DW(@Named("dimensionInfo") var1: MutableMap<java.lang.String, DP>, var2: DZ, var3: ChunkAllocatorProvider) {
      this.b = var1
      this.c = var2
      this.d = var3
      this.e = LinkedHashMap<>()
   }

   fun a(var1: java.lang.String, var2: java.lang.String): DoubleAugmentation {
      this.c.a("$var1:$var2")
   }

   fun b(var1: java.lang.String, var2: java.lang.String): DoubleComponentAugmentation {
      var2 = "$var1:$var2"
      val var4: DZ = this.c
      if (var4.a.containsKey(var2)) {
         val var6: Any = var4.a.get(var2)
         var6 as DoubleComponentAugmentation
      } else {
         val var3: Ee = Ee(var2)
         var4.a.put(var2, var3)
         var3 as DoubleComponentAugmentation
      }
   }

   fun a(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String): Double {
      this.c(var5).a.a(var1, var2, var3, var4)
   }

   fun a(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String, var6: Double) {
      this.c(var5).a.a(var1, var2, var3, var4, var6)
   }

   fun a(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String, var6: DoubleUnaryOperator) {
      this.c(var5).a.a(var1, var2, var3, var4, var6)
   }

   fun b(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String) {
      this.c(var5).a.b(var1, var2, var3, var4)
   }

   fun c(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String): Double {
      this.c(var5).a.c(var1, var2, var3, var4)
   }

   fun a(var1: DoubleComponentAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String, var6: Double) {
      this.c(var5).a.a(var1, var2, var3, var4, var6)
   }

   fun a(var1: DoubleComponentAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String): Double {
      this.c(var5).a.a(var1, var2, var3, var4)
   }

   fun b(var1: DoubleComponentAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String) {
      this.c(var5).a.b(var1, var2, var3, var4)
   }

   fun a(var1: Int, var2: Int, var3: Int, var4: java.lang.String): Long {
      this.c(var4).a.f(var1, var2, var3)
   }

   fun a(var1: DoubleAugmentation, var2: java.lang.String, var3: ChunkClaim): MutableMap<Triple<Integer, Integer, Integer>, java.lang.Double> {
      this.c(var2).a.a(var1, var3)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: DW, var1: DoubleAugmentation, var2: java.lang.String, var3: ChunkClaim, var4: Int): java.util.Map {
      if ((var4 and 4) != 0) {
         var3 = null
      }

      var0.a(var1, var2, var3)
   }

   fun a(var1: DoubleComponentAugmentation, var2: java.lang.String, var3: ChunkClaim): MutableMap<Triple<Integer, Integer, Integer>, java.lang.Double> {
      this.c(var2).a.a(var1, var3)
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: DW, var1: DoubleComponentAugmentation, var2: java.lang.String, var3: ChunkClaim, var4: Int): java.util.Map {
      if ((var4 and 4) != 0) {
         var3 = null
      }

      var0.a(var1, var2, var3)
   }

   fun a(var1: ChunkClaim, var2: java.lang.String): MutableSet<Vector3ic> {
      this.c(var2).a.a(var1)
   }

   fun a(var1: Int, var2: Int, var3: Int, var4: (Int?, Int?, Int?, java.lang.Long?) -> Unit, var5: java.lang.String) {
      this.c(var5).a.a(var1, var2, var3, var4)
   }

   fun a(var1: Int, var2: Int, var3: Int, var4: MutableSet<Vector3ic>, var5: java.lang.String): MutableSet<Vector3ic> {
      this.c(var5).a.a(var1, var2, var3, TypeIntrinsics.asMutableSet(var4)) as java.util.Set
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: DW, var1: Int, var2: Int, var3: Int, var4: java.util.Set, var5: java.lang.String, var6: Int): java.util.Set {
      var0.a(var1, var2, var3, DenseBlockPosSet(), var5)
   }

   fun b(var1: Int, var2: Int, var3: Int, var4: MutableSet<SparseVoxelPosition>, var5: java.lang.String): MutableSet<SparseVoxelPosition> {
      this.c(var5).a.b(var1, var2, var3, TypeIntrinsics.asMutableSet(var4))
   }

   fun a(var1: java.lang.String): Pair<HashSet<SplitEvent>, HashSet<MergeEvent>> {
      this.c(var1).a.a()
   }

   fun d(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String): Double {
      this.c(var5).b.a(var1, var2, var3, var4)
   }

   fun b(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String, var6: Double) {
      this.c(var5).b.a(var1, var2, var3, var4, var6)
   }

   fun b(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String, var6: DoubleUnaryOperator) {
      this.c(var5).b.a(var1, var2, var3, var4, var6)
   }

   fun e(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String) {
      this.c(var5).b.b(var1, var2, var3, var4)
   }

   fun f(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String): Double {
      this.c(var5).b.c(var1, var2, var3, var4)
   }

   fun b(var1: DoubleComponentAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String, var6: Double) {
      this.c(var5).b.a(var1, var2, var3, var4, var6)
   }

   fun c(var1: DoubleComponentAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String): Double {
      this.c(var5).b.a(var1, var2, var3, var4)
   }

   fun d(var1: DoubleComponentAugmentation, var2: Int, var3: Int, var4: Int, var5: java.lang.String) {
      this.c(var5).b.b(var1, var2, var3, var4)
   }

   fun b(var1: Int, var2: Int, var3: Int, var4: java.lang.String): Long {
      this.c(var4).b.f(var1, var2, var3)
   }

   fun b(var1: DoubleAugmentation, var2: java.lang.String, var3: ChunkClaim): MutableMap<Triple<Integer, Integer, Integer>, java.lang.Double> {
      this.c(var2).b.a(var1, var3)
   }

   @JvmStatic
   @JvmSynthetic
   fun `b$default`(var0: DW, var1: DoubleAugmentation, var2: java.lang.String, var3: ChunkClaim, var4: Int): java.util.Map {
      if ((var4 and 4) != 0) {
         var3 = null
      }

      var0.b(var1, var2, var3)
   }

   fun b(var1: DoubleComponentAugmentation, var2: java.lang.String, var3: ChunkClaim): MutableMap<Triple<Integer, Integer, Integer>, java.lang.Double> {
      this.c(var2).b.a(var1, var3)
   }

   @JvmStatic
   @JvmSynthetic
   fun `b$default`(var0: DW, var1: DoubleComponentAugmentation, var2: java.lang.String, var3: ChunkClaim, var4: Int): java.util.Map {
      if ((var4 and 4) != 0) {
         var3 = null
      }

      var0.b(var1, var2, var3)
   }

   fun b(var1: ChunkClaim, var2: java.lang.String): MutableSet<Vector3ic> {
      this.c(var2).b.a(var1)
   }

   fun b(var1: Int, var2: Int, var3: Int, var4: (Int?, Int?, Int?, java.lang.Long?) -> Unit, var5: java.lang.String) {
      this.c(var5).b.a(var1, var2, var3, var4)
   }

   fun a(var1: Int, var2: Int, var3: Int, var4: DenseBlockPosSet, var5: java.lang.String): MutableSet<Vector3ic> {
      this.c(var5).b.a(var1, var2, var3, var4) as java.util.Set
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: DW, var1: Int, var2: Int, var3: Int, var4: DenseBlockPosSet, var5: java.lang.String, var6: Int): java.util.Set {
      var4 = DenseBlockPosSet()
      var0.c(var5).b.a(var1, var2, var3, var4) as java.util.Set
   }

   fun c(var1: Int, var2: Int, var3: Int, var4: MutableSet<Vector3ic>, var5: java.lang.String): MutableSet<Vector3ic> {
      this.c(var5).b.a(var1, var2, var3, TypeIntrinsics.asMutableSet(var4)) as java.util.Set
   }

   fun d(var1: Int, var2: Int, var3: Int, var4: MutableSet<SparseVoxelPosition>, var5: java.lang.String): MutableSet<SparseVoxelPosition> {
      this.c(var5).b.b(var1, var2, var3, TypeIntrinsics.asMutableSet(var4))
   }

   fun b(var1: java.lang.String): Pair<HashSet<SplitEvent>, HashSet<MergeEvent>> {
      this.c(var1).b.a()
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: DW, var1: java.lang.String, var2: Int, var3: Int, var4: Int, var5: Int, var6: java.lang.Long, var7: Int) {
      if ((var7 and 32) != 0) {
         var6 = null
      }

      var0.a(var1, var2, var3, var4, var5, var6)
   }

   fun c(var1: java.lang.String): DY {
      val var2: java.util.Map = this.e
      val var3: Any
      val var8: Any
      if ((var3 = this.e.get(var1)) == null) {
         val var10000: DP = this.b.get(var1)
         val var7: LevelYRange = if (var10000 != null) var10000.b else null
         if ((if (var10000 != null) var10000.b else null) == null) {
            throw IllegalArgumentException(("Requested ConnGraph for $var1 but have no information about it").toString())
         }

         var3 = DY(
            DU(VSCoreConfig.SERVER.sp.enableBlockEdgeConnectivity, VSCoreConfig.SERVER.sp.enableBlockCornerConnectivity, var7, this.c, 1),
            DU(false, false, var7, this.c, 0, 19, null)
         )
         var2.put(var1, var3)
         var8 = var3
      } else {
         var8 = var3
      }

      var8 as DY
   }

   @JvmStatic
   fun a(var0: DY, var1: Int, var2: Int, var3: Int) {
      val var6: DT = var0.b
      var0.a.c(var1, var2, var3)
      var6.c(var1, var2, var3)
   }

   fun a(var1: java.lang.String, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int, var7: Int, var8: Int): ConnectionStatus {
      when (var8) {
         0 -> {
            val var11: DY
            var11.b.a(var2, var3, var4, var5, var6, var7)
         }
         1 -> this.c(var1).a.a(var2, var3, var4, var5, var6, var7)
         else -> throw IllegalStateException("Unknown voxelType = $var8")
      }
   }

   fun a(var1: java.lang.String, var2: Int, var3: Int, var4: Int, var5: Int): ConnectionStatus {
      when (var5) {
         0 -> {
            val var8: DY
            var8.b.d(var2, var3, var4)
         }
         1 -> this.c(var1).a.d(var2, var3, var4)
         else -> throw IllegalStateException("Unknown voxelType = $var5")
      }
   }

   fun a(var1: java.lang.String, var2: Int, var3: Int, var4: Int, var5: java.lang.Long, var6: Ip) {
      val var7: DY
      val var8: DT = (var7 = this.c(var1)).a
      val var16: DT = var7.b
      val var9: java.lang.Long = if (this.d.forDimension(var1).isChunkInShipyard(var2, var4)) 1L else null
      var var10000: java.lang.Long = var5
      if (var5 == null) {
         var10000 = var9
      }

      if (var6.d() === It.EMPTY) {
         var16.e(var2 shl 4, var3 shl 4, var4 shl 4)
      } else if (var6.d() === It.DENSE) {
         val var13: In = var6 as In
         var8.a(var2 shl 4, var3 shl 4, var4 shl 4, var6 as In)
         var16.a(var2 shl 4, var3 shl 4, var4 shl 4, var13)
      }

      val var17: Long = System.currentTimeMillis()
      var8.b(var2, var3, var4, var10000)
      var16.b(var2, var3, var4, var10000)
      if (VSCoreConfig.SERVER.sp.verboseConnectivityLogging) {
         f.info("Loaded chunk at ($var2, $var3, $var4) in dimension $var1 in ${System.currentTimeMillis() - var17}ms")
      }
   }

   fun a(var1: java.lang.String, var2: Int, var3: Int, var4: Int) {
      val var5: DY
      val var6: DT = (var5 = this.c(var1)).a
      val var7: DT = var5.b
      val var8: Int = var2 shl 4
      val var9: Int = var3 shl 4
      val var10: Int = var4 shl 4
      val var11: java.lang.Long = if (this.d.forDimension(var1).isChunkInShipyard(var2, var4)) 1L else null
      val var15: DT = var5.b
      var5.a.c(var8, var9, var10)
      var15.c(var8, var9, var10)
      if (VSCoreConfig.SERVER.sp.verboseConnectivityLogging) {
         f.info("Unloaded chunk at ($var2, $var3, $var4) in dimension $var1")
      }

      var6.c(var2, var3, var4, var11)
      var7.c(var2, var3, var4, var11)
   }

   public companion object a {
      public final val a: FY
   }
}
