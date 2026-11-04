package org.valkyrienskies.core.impl.shadow

import it.unimi.dsi.fastutil.objects.Object2DoubleMap
import it.unimi.dsi.fastutil.objects.Object2DoubleMaps
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectIterator
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.Map.Entry
import java.util.function.DoubleUnaryOperator
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.jvm.internal.TypeIntrinsics
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.jvm.internal.Ref.ObjectRef
import org.joml.Vector3i
import org.joml.Vector3ic
import org.valkyrienskies.core.api.events.MergeEvent
import org.valkyrienskies.core.api.events.SplitEvent
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.util.DoublePair
import org.valkyrienskies.core.api.world.LevelYRange
import org.valkyrienskies.core.api.world.connectivity.Component
import org.valkyrienskies.core.api.world.connectivity.ConnectionStatus
import org.valkyrienskies.core.api.world.connectivity.DoubleAugmentation
import org.valkyrienskies.core.api.world.connectivity.DoubleComponentAugmentation
import org.valkyrienskies.core.api.world.connectivity.SparseVoxelPosition
import org.valkyrienskies.core.impl.api_impl.connectivity.MergeEventImpl
import org.valkyrienskies.core.impl.api_impl.connectivity.SplitEventImpl
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.impl.game.ChunkClaimImpl
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.core.util.datastructures.SparseVoxelOpenHashMap

public class DU : DT {
   private boolean b;
   private boolean c;
   private LevelYRange d;
   private DZ e;
   private int f;
   private DU.c g;
   private DA h;
   private HashSet<SplitEvent> i;
   private HashSet<MergeEvent> j;
   private SparseVoxelOpenHashMap<DB> k;
   HashSet<Vector3i> a;
   private HashMap<Vector3i, In> l;
   private DB m;
   private DB n;
   private HashMap<DD, DU.d> o;
   private HashMap<ChunkClaim, HashSet<DD>> p;
   private DoubleAugmentation q;
   private DoubleAugmentation r;
   private DoubleAugmentation s;
   private DoubleAugmentation t;
   private DoubleAugmentation u;
   private java.lang.String v;

   fun DU(var1: Boolean, var2: Boolean, var3: LevelYRange, var4: DZ, var5: Int) {
      this.b = var1;
      this.c = var2;
      this.d = var3;
      this.e = var4;
      this.f = var5;
      this.g = DU.c(this);
      this.h = DA(DU.a(null, 0, 3, null), this.g);
      this.i = HashSet<>();
      this.j = HashSet<>();
      this.k = SparseVoxelOpenHashMap<>();
      this.a = HashSet<>();
      this.l = HashMap<>();
      this.m = DB();
      this.n = DB();
      this.o = HashMap<>();
      this.p = HashMap<>();
      this.q = this.e.a("core:x");
      this.r = this.e.a("core:y");
      this.s = this.e.a("core:z");
      this.t = this.e.a("core:extent");
      this.u = this.e.a("core:volume");
      this.v = if (this.f == 0) "Air" else "Solid";
   }

   @JvmSynthetic
   fun DU(var1: Boolean, var2: Boolean, var3: LevelYRange, var4: DZ, var5: Int, var6: Int, var7: DefaultConstructorMarker) {
      if ((var6 and 1) != 0) {
         var1 = false;
      }

      if ((var6 and 2) != 0) {
         var2 = false;
      }

      if ((var6 and 16) != 0) {
         var5 = 0;
      }

      this(var1, var2, var3, var4, var5);
   }

   @JvmName(name = "c")
   fun c(): Boolean {
      this.b;
   }

   @JvmName(name = "d")
   fun d(): Boolean {
      this.c;
   }

   @JvmName(name = "e")
   fun e(): LevelYRange {
      this.d;
   }

   @JvmName(name = "f")
   fun f(): DZ {
      this.e;
   }

   @JvmName(name = "g")
   fun g(): Int {
      this.f;
   }

   @JvmName(name = "h")
   fun h(): DU.c {
      this.g;
   }

   @JvmName(name = "i")
   fun i(): DA {
      this.h;
   }

   @JvmName(name = "j")
   fun j(): HashSet<SplitEvent> {
      this.i;
   }

   @JvmName(name = "k")
   fun k(): HashSet<MergeEvent> {
      this.j;
   }

   @JvmName(name = "l")
   fun l(): SparseVoxelOpenHashMap<DB> {
      this.k;
   }

   @JvmName(name = "b")
   fun b(): HashSet<Vector3i> {
      this.a;
   }

   @JvmName(name = "m")
   fun m(): HashMap<Vector3i, In> {
      this.l;
   }

   @JvmName(name = "n")
   fun n(): DB {
      this.m;
   }

   @JvmName(name = "o")
   fun o(): HashMap<DD, DU.d> {
      this.o;
   }

   @JvmName(name = "p")
   fun p(): HashMap<ChunkClaim, HashSet<DD>> {
      this.p;
   }

   @JvmName(name = "q")
   fun q(): DoubleAugmentation {
      this.q;
   }

   @JvmName(name = "r")
   fun r(): DoubleAugmentation {
      this.r;
   }

   @JvmName(name = "s")
   fun s(): DoubleAugmentation {
      this.s;
   }

   @JvmName(name = "t")
   fun t(): DoubleAugmentation {
      this.t;
   }

   @JvmName(name = "u")
   fun u(): DoubleAugmentation {
      this.u;
   }

   fun a(var1: DE): DB {
      if (var1 == null) {
         null;
      } else {
         val var2: java.util.Iterator = this.h.a.entrySet().iterator();

         var var10000: Any;
         while (true) {
            if (var2.hasNext()) {
               val var3: Any;
               if (!((((var3 = var2.next()) as Entry).getValue() as DG).a == var1)) {
                  continue
               }

               var10000 = var3;
               break
            }

            var10000 = null;
            break
         }

         if (var10000 as Entry != null) (var10000 as Entry).getKey() as DB else null;
      }
   }

   fun a(var1: DB): SparseVoxelPosition {
      if (var1 == null) {
         null;
      } else {
         val var10000: java.util.Set = this.k.entrySet();
         val var2: java.util.Iterator = var10000.iterator();

         while (true) {
            if (var2.hasNext()) {
               val var3: Any;
               if (!(((var3 = var2.next()) as Entry).getValue() == var1)) {
                  continue
               }

               var5 = var3;
               break
            }

            var5 = null;
            break
         }

         if (var5 as Entry != null) (var5 as Entry).getKey() as SparseVoxelPosition else null;
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: DU, var1: Vector3ic, var2: java.lang.Long, var3: Int) {
      if ((var3 and 2) != 0) {
         var2 = null;
      }

      var0.a(var1, var2);
   }

   fun a(var1: SparseVoxelPosition) {
      if (this.k.get((Object)var1) != null) {
         val var2: DB = this.k.get((Object)var1);

         for (var4 in this.b(var1)) {
            this.h.b(var2, var4);
         }

         this.k.remove((Object)var1);
      }
   }

   override final fun a(var1: Int, var2: Int, var3: Int, var4: java.lang.Long) {
      this.a(Vector3i(var1, var2, var3), var4);
   }

   fun a(var1: SparseVoxelPosition, var2: java.lang.Long) {
      this.a(var1.x, var1.y, var1.z, var1.extent, var2);
   }

   fun a(var1: Vector3ic) {
      if (this.k.containsKey(var1)) {
         var var10000: DB = this.k.findContainingVoxel(var1.x(), var1.y(), var1.z());
         var10000 = this.k.get(var1);
         val var3: DB = var10000;
         val var4: Any = this.h.b(var10000);
         val var5: java.util.Set;
         if ((var5 = this.b(var1)).isEmpty()) {
            var var6: java.util.Map = this.o;
            val var23: DG = this.h.a.get(var3);
            if (var6.get(if (var23 != null && var23.a != null && var23.a.c != null) var23.a.c.e() as DD else null) != null) {
               var6 = this.o;
               val var27: DG = this.h.a.get(var3);
               val var7: DD = if (var27 != null && var27.a != null && var27.a.c != null) var27.a.c.e() as DD else null;
               TypeIntrinsics.asMutableMap(var6).remove(var7);
               val var31: HashSet = this.p.get(ChunkClaimImpl.Companion.getClaim(var10000.x() shr 4, var10000.z() shr 4));
               if (var31 != null) {
                  val var8: java.util.Collection = var31;
                  val var32: DG = this.h.a.get(var3);
                  val var14: DD = if (var32 != null && var32.a != null && var32.a.c != null) var32.a.c.e() as DD else null;
                  TypeIntrinsics.asMutableCollection(var8).remove(var14);
               }
            }
         }

         this.k.removeCertain(var10000);
         if (var10000.extent > 1L) {
            val var10002: java.util.Set = SetsKt.minus(var10000.getContaining(), var1);
            val var9: Byte = 8;
            val var16: java.util.Set;
            if ((var16 = this.a(var10000, var10002, null, 4096)).size() == 1) {
               this.a((Vector3ic)(CollectionsKt.first(var16) as Vector3ic), (java.lang.Long)null);
               if (this.k.get(CollectionsKt.first(var16)) != null) {
                  val var36: DA = this.h;
                  val var38: Any = this.k.get(CollectionsKt.first(var16));
                  var36.a(var38 as DB, var4);
               }
            } else if (!var16.isEmpty()) {
               for (var21 in var16) {
                  this.a(var21, null);
               }

               if (this.k.get(CollectionsKt.first(var16)) != null) {
                  val var37: DA = this.h;
                  val var39: Any = this.k.get(CollectionsKt.first(var16));
                  var37.a(var39 as DB, var4);
               }
            }
         }

         for (var20 in var5) {
            this.h.b(var3, var20);
         }

         if (VSCoreConfig.SERVER.sp.verboseConnectivityLogging) {
            a.info("[${this.v} Graph]: Deleted vertex : $var1");
         }
      }
   }

   override final fun a(var1: Int, var2: Int, var3: Int) {
      val var11: Vector3ic = Vector3i(var1, var2, var3);
      val var10: DU = this;
      if (this.k.containsKey(var11)) {
         var var10000: DB = this.k.findContainingVoxel(var11.x(), var11.y(), var11.z());
         var10000 = this.k.get(var11);
         val var4: DB = var10000;
         val var5: Any = this.h.b(var10000);
         val var6: java.util.Set;
         if ((var6 = this.b(var11)).isEmpty()) {
            var var7: java.util.Map = this.o;
            val var27: DG = this.h.a.get(var4);
            if (var7.get(if (var27 != null && var27.a != null && var27.a.c != null) var27.a.c.e() as DD else null) != null) {
               var7 = this.o;
               val var31: DG = this.h.a.get(var4);
               val var8: DD = if (var31 != null && var31.a != null && var31.a.c != null) var31.a.c.e() as DD else null;
               TypeIntrinsics.asMutableMap(var7).remove(var8);
               val var35: HashSet = this.p.get(ChunkClaimImpl.Companion.getClaim(var10000.x() shr 4, var10000.z() shr 4));
               if (var35 != null) {
                  val var9: java.util.Collection = var35;
                  val var36: DG = this.h.a.get(var4);
                  val var18: DD = if (var36 != null && var36.a != null && var36.a.c != null) var36.a.c.e() as DD else null;
                  TypeIntrinsics.asMutableCollection(var9).remove(var18);
               }
            }
         }

         this.k.removeCertain(var10000);
         if (var10000.extent > 1L) {
            val var10002: java.util.Set = SetsKt.minus(var10000.getContaining(), var11);
            val var13: Byte = 8;
            val var20: java.util.Set;
            if ((var20 = this.a(var10000, var10002, null, 4096)).size() == 1) {
               this.a((Vector3ic)(CollectionsKt.first(var20) as Vector3ic), (java.lang.Long)null);
               if (this.k.get(CollectionsKt.first(var20)) != null) {
                  val var41: DA = this.h;
                  val var43: Any = this.k.get(CollectionsKt.first(var20));
                  var41.a(var43 as DB, var5);
               }
            } else if (!var20.isEmpty()) {
               for (var25 in var20) {
                  var10.a(var25, null);
               }

               if (var10.k.get(CollectionsKt.first(var20)) != null) {
                  val var40: DA = var10.h;
                  val var42: Any = var10.k.get(CollectionsKt.first(var20));
                  var40.a(var42 as DB, var5);
               }
            }
         }

         for (var24 in var6) {
            var10.h.b(var4, var24);
         }

         if (VSCoreConfig.SERVER.sp.verboseConnectivityLogging) {
            a.info("[${var10.v} Graph]: Deleted vertex : $var11");
         }
      }
   }

   override final fun b(var1: Int, var2: Int, var3: Int): SparseVoxelPosition {
      if (!this.k.containsKey(var1, var2, var3)) {
         null;
      } else {
         val var10000: SparseVoxelPosition = this.k.findContainingVoxel(var1, var2, var3);
         val var15: Any = this.k.get(var1, var2, var3);
         val var6: DB = var15 as DB;
         val var7: java.util.Set;
         if ((var7 = this.b(var10000)).isEmpty()) {
            val var8: java.util.Map = this.o;
            var var16: DG = this.h.a.get(var6);
            if (var8.get(if (var16 != null && var16.a != null && var16.a.c != null) var16.a.c.e() as DD else null) != null) {
               val var9: java.util.Map = this.o;
               var16 = this.h.a.get(var6);
               var var5: DD = if (var16 != null && var16.a != null && var16.a.c != null) var16.a.c.e() as DD else null;
               TypeIntrinsics.asMutableMap(var9).remove(var5);
               val var24: HashSet = this.p.get(ChunkClaimImpl.Companion.getClaim(var10000.x() shr 4, var10000.z() shr 4));
               if (var24 != null) {
                  val var11: java.util.Collection = var24;
                  var16 = this.h.a.get(var6);
                  var5 = if (var16 != null && var16.a != null && var16.a.c != null) var16.a.c.e() as DD else null;
                  TypeIntrinsics.asMutableCollection(var11).remove(var5);
               }
            }
         }

         this.k.remove((Object)var10000);

         for (var14 in var7) {
            this.h.b(var6, var14);
         }

         this.h.b(var6);
         if (VSCoreConfig.SERVER.sp.verboseConnectivityLogging) {
            a.info("[${this.v} Graph]:Deleted voxel : $var10000");
         }

         var10000;
      }
   }

   override final fun c(var1: Int, var2: Int, var3: Int) {
      val var4: HashSet = HashSet();
      var var5: Int = var1;

      for (var16 in var1 + 16..var5) {
         var var6: Int = var2;

         for (var7 in var2 + 16..var6) {
            var var8: Int = var3;

            for (var9 in var3 + 16..var8) {
               if (this.k.containsKey(var5, var6, var8)) {
                  var var10000: DG = this.k.findContainingVoxel(var5, var6, var8);
                  val var18: java.util.Iterator = var4.iterator();

                  while (true) {
                     if (var18.hasNext()) {
                        val var12: Any;
                        if (!((var12 = var18.next()) as SparseVoxelPosition).contains(var5, var6, var8)) {
                           continue
                        }

                        var10000 = var12;
                        break
                     }

                     var10000 = null;
                     break
                  }

                  if (var10000 == null) {
                     var4.add(var10000);
                     val var17: DU = this;
                     if (this.k.containsKey(var5, var6, var8)) {
                        var10000 = this.k.findContainingVoxel(var5, var6, var8);
                        var10000 = this.k.get(var5, var6, var8);
                        val var20: DB = var10000 as DB;
                        val var22: java.util.Set;
                        if ((var22 = this.b(var10000 as Vector3ic)).isEmpty()) {
                           var var23: java.util.Map = this.o;
                           var10000 = this.h.a.get(var20);
                           if (var23.get(if (var10000 != null && var10000.a != null && var10000.a.c != null) var10000.a.c.e() as DD else null) != null) {
                              var23 = this.o;
                              val var38: DG = this.h.a.get(var20);
                              var var15: DD = if (var38 != null && var38.a != null && var38.a.c != null) var38.a.c.e() as DD else null;
                              TypeIntrinsics.asMutableMap(var23).remove(var15);
                              val var42: HashSet = this.p
                                 .get(ChunkClaimImpl.Companion.getClaim(((Vector3i)var10000).x() shr 4, ((Vector3i)var10000).z() shr 4));
                              if (var42 != null) {
                                 val var26: java.util.Collection = var42;
                                 var10000 = this.h.a.get(var20);
                                 var15 = if (var10000 != null && var10000.a != null && var10000.a.c != null) var10000.a.c.e() as DD else null;
                                 TypeIntrinsics.asMutableCollection(var26).remove(var15);
                              }
                           }
                        }

                        this.k.remove(var10000);

                        for (var29 in var22) {
                           var17.h.b(var20, var29);
                        }

                        var17.h.b(var20);
                        if (VSCoreConfig.SERVER.sp.verboseConnectivityLogging) {
                           a.info("[${var17.v} Graph]:Deleted voxel : $var10000");
                        }
                     }
                  }
               }
            }
         }
      }
   }

   override final fun a(var1: Int, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int): ConnectionStatus {
      this.a(Vector3i(var1, var2, var3), Vector3i(var4, var5, var6));
   }

   override final fun d(var1: Int, var2: Int, var3: Int): ConnectionStatus {
      this.a(Vector3i(var1, var2, var3));
   }

   fun a(var1: Vector3i, var2: Vector3i): ConnectionStatus {
      val var3: DB = this.k.get(var1);
      val var4: DB = this.k.get(var2);
      if (var3 == var4) {
         ConnectionStatus.CONNECTED;
      } else if (var3 == null || var4 == null) {
         ConnectionStatus.UNKNOWN;
      } else if (this.h.c(var3, var4)) {
         ConnectionStatus.CONNECTED;
      } else {
         if (this.c(var3) && this.c(var4)) ConnectionStatus.UNKNOWN else ConnectionStatus.DISCONNECTED;
      }
   }

   fun a(var1: Vector3ic, var2: Vector3ic): ConnectionStatus {
      this.a(Vector3i(var1), Vector3i(var2));
   }

   fun a(var1: Vector3i): ConnectionStatus {
      this.b(this.k.get(var1));
   }

   fun b(var1: DB): ConnectionStatus {
      if (var1 == null) {
         ConnectionStatus.UNKNOWN;
      } else if (this.h.c(var1, this.m)) {
         ConnectionStatus.CONNECTED;
      } else {
         if (this.c(var1)) ConnectionStatus.UNKNOWN else ConnectionStatus.DISCONNECTED;
      }
   }

   fun b(var1: Vector3ic): MutableSet<DB> {
      if (!this.k.containsKey(var1)) {
         SetsKt.emptySet();
      } else {
         val var10002: DA = this.h;
         val var10003: Any = this.k.get(var1);
         HashSet(var10002.a(var10003 as DB)) as java.util.Set;
      }
   }

   fun v() {
      val var1: ArrayDeque = ArrayDeque();
      val var2: ArrayDeque = ArrayDeque();

      for (var4 in this.o.entrySet()) {
         val var5: java.util.Iterator = this.h.a.values().iterator();

         var var10000: Any;
         while (true) {
            if (!var5.hasNext()) {
               var10000 = null;
               break
            }

            val var6: Any;
            if (((var6 = var5.next()) as DG).a == (var4.getKey() as DD).b) {
               var10000 = var6;
               break
            }
         }

         if (var10000 == null) {
            var1.add(var4.getKey());
         } else {
            val var16: DD;
            if ((var16 = (var4.getKey() as DD).b.c.e() as DD) != null && var16 == var4.getKey() && !var1.contains(var16)) {
               val var9: Any = if (var16.b != null) var16.b.f else null;
               var10000 = if ((if (var16.b != null) var16.b.f else null) !is DU.a)
                  null
                  else
                  Vector3i((int)((if (var16.b != null) var16.b.f else null) as DU.a).a(this.q), (int)(var9 as DU.a).a(this.r), (int)(var9 as DU.a).a(this.s));
               if (var10000 != null && this.a(Vector3i((Vector3ic)var10000)) != ConnectionStatus.DISCONNECTED) {
                  var1.add(var4.getKey());
               }
            } else {
               var1.add(var4.getKey());
               if (var16 != null && !var1.contains(var16)) {
                  var2.add(Pair(var4.getKey(), var16));
               }
            }
         }
      }

      val var13: Pair;
      while ((var13 = var2.removeFirstOrNull() as Pair) != null) {
         var var28: DU.d = this.o.get(var13.getSecond());
         if (var28 == null) {
            var28 = DU.d(HashMap<>());
         }

         val var29: Any = this.o.get(var13.getFirst());
         this.o.put((DD)var13.getSecond(), var28.a(var29 as DU.d));
         if ((var2 as java.util.Collection).isEmpty()) {
            break
         }
      }

      while ((var14 = var1.removeFirstOrNull() as DD) != null) {
         this.o.remove(var14);
         if ((var1 as java.util.Collection).isEmpty()) {
            break
         }
      }

      val var21: DU = this;
      this.p.clear();

      for (var24 in this.o.entrySet()) {
         val var30: SparseVoxelPosition = var21.b(var24.getKey() as DD);
         if (var30 != null) {
            val var26: ChunkClaim = ChunkClaimImpl.Companion.getClaim(var30.x() shr 4, var30.z() shr 4);
            val var10: java.util.Map;
            val var11: Any;
            val var31: Any;
            if ((var11 = (var10 = var21.p).get(var26)) == null) {
               var11 = HashSet();
               var10.put(var26, var11);
               var31 = var11;
            } else {
               var31 = var11;
            }

            (var31 as HashSet).add(var24.getKey());
         }
      }
   }

   fun b(var1: ChunkClaim) {
      this.p.put(var1, HashSet<>());
      val var10000: Any = this.p.get(var1);
      val var2: HashSet = var10000 as HashSet;

      for (var4 in this.o.entrySet()) {
         val var6: SparseVoxelPosition = this.b(var4.getKey() as DD);
         if (var6 != null && ChunkClaimImpl.Companion.getClaim(var6.x() shr 4, var6.z() shr 4) == var1) {
            var2.add(var4.getKey());
         }
      }

      if (var2.isEmpty()) {
         this.p.remove(var1);
      }
   }

   fun w() {
      this.p.clear();

      for (var2 in this.o.entrySet()) {
         val var10000: SparseVoxelPosition = this.b(var2.getKey() as DD);
         if (var10000 != null) {
            val var6: ChunkClaim = ChunkClaimImpl.Companion.getClaim(var10000.x() shr 4, var10000.z() shr 4);
            val var4: java.util.Map;
            val var5: Any;
            val var8: Any;
            if ((var5 = (var4 = this.p).get(var6)) == null) {
               var5 = HashSet();
               var4.put(var6, var5);
               var8 = var5;
            } else {
               var8 = var5;
            }

            (var8 as HashSet).add(var2.getKey());
         }
      }
   }

   fun a(var1: Int, var2: Int, var3: Int, var4: Long): MutableSet<SparseVoxelPosition> {
      val var6: HashSet = HashSet();
      val var7: SparseVoxelPosition = SparseVoxelPosition(var1, var2, var3, var4);
      var var8: Int = var1 - 1;
      val var9: Int = var1 + (int)var4;
      if (var8 <= var1 + (int)var4) {
         while (true) {
            var var10: Int = var2 - 1;
            val var11: Int = var2 + (int)var4;
            if (var10 <= var2 + (int)var4) {
               while (true) {
                  var var12: Int = var3 - 1;
                  val var13: Int = var3 + (int)var4;
                  if (var12 <= var3 + (int)var4) {
                     while (true) {
                        if (!var7.contains(var8, var10, var12)) {
                           val var14: Array<Int>;
                           (var14 = arrayOfNulls(3))[0] = if (var8 < var1) 1 else (if (var8 == var1 + (int)var4) 1 else 0);
                           var14[1] = if (var10 < var2) 1 else (if (var10 == var2 + (int)var4) 1 else 0);
                           var14[2] = if (var12 < var3) 1 else (if (var12 == var3 + (int)var4) 1 else 0);
                           val var16: java.util.List = CollectionsKt.listOf(var14);
                           if ((this.c || CollectionsKt.sumOfInt(var16) != 3) && (this.b || CollectionsKt.sumOfInt(var16) != 2)) {
                              var var10000: Boolean;
                              run label119@{
                                 val var17: java.lang.Iterable;
                                 if (!((var17 = var6) as java.util.Collection).isEmpty()) {
                                    val var18: java.util.Iterator = var17.iterator();

                                    while (var18.hasNext()) {
                                       if ((var18.next() as SparseVoxelPosition).contains(var8, var10, var12)) {
                                          var10000 = true;
                                          return@label119
                                       }
                                    }
                                 }

                                 var10000 = false;
                              }

                              if (!var10000) {
                                 val var20: SparseVoxelPosition = this.k.findContainingVoxel(var8, var10, var12);
                                 if (var20 != null) {
                                    var6.add(var20);
                                 }
                              }
                           }
                        }

                        if (var12 == var13) {
                           break
                        }

                        var12++;
                     }
                  }

                  if (var10 == var11) {
                     break
                  }

                  var10++;
               }
            }

            if (var8 == var9) {
               break
            }

            var8++;
         }
      }

      var6 as java.util.Set;
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: DU, var1: Int, var2: Int, var3: Int, var4: Long, var6: Int): java.util.Set {
      if ((var6 and 8) != 0) {
         var4 = 1L;
      }

      var0.a(var1, var2, var3, var4);
   }

   fun c(var1: DB): Boolean {
      val var10000: Any = this.h.d(var1);
      (var10000 as DU.a).b > 0;
   }

   override final fun c(var1: Int, var2: Int, var3: Int, var4: java.lang.Long) {
      this.a.remove(Vector3i(var1, var2, var3));
      if (var4 == null) {
         val var13: HashSet = HashSet();
         var var5: Int = var1 - 1;
         val var6: Int = var1 + 1;
         if (var5 <= var1 + 1) {
            while (true) {
               var var7: Int = var2 - 1;
               val var8: Int = var2 + 1;
               if (var7 <= var2 + 1) {
                  while (true) {
                     var var9: Int = var3 - 1;
                     val var10: Int = var3 + 1;
                     if (var9 <= var3 + 1) {
                        while (true) {
                           if (this.a.contains(Vector3i(var5, var7, var9))
                              && (this.b || (var5 == var1 || var7 == var2) && (var5 == var1 || var9 == var3) && (var7 == var2 || var9 == var3))
                              && (this.c || var5 == var1 || var7 == var2 || var9 == var3)) {
                              for (var12 in this.k.getBorderingDirection(var5, var7, var9, Vector3i(var1 - var5, var2 - var7, var3 - var9))) {
                                 if (!var13.contains(var12)) {
                                    var13.add(var12);
                                    if (this.a(var12) === ConnectionStatus.UNKNOWN) {
                                       this.a(var12, 1);
                                    }
                                 }
                              }
                           }

                           if (var9 == var10) {
                              break
                           }

                           var9++;
                        }
                     }

                     if (var7 == var8) {
                        break
                     }

                     var7++;
                  }
               }

               if (var5 == var6) {
                  break
               }

               var5++;
            }
         }
      }
   }

   fun a(var1: SparseVoxelPosition, var2: MutableSet<Vector3ic>, var3: java.lang.Long, var4: Int): MutableSet<SparseVoxelPosition> {
      if (var2.size() == var4) {
         SetsKt.setOf(var1);
      } else if (var1.extent == 1L && var2.isEmpty()) {
         SetsKt.emptySet();
      } else {
         var4 = var1.x + Math.max(1, (int)var1.extent / 2);
         val var5: Int = var1.y + Math.max(1, (int)var1.extent / 2);
         var var6: Int = var1.z + Math.max(1, (int)var1.extent / 2);
         val var7: HashSet;
         (var7 = HashSet()).add(SparseVoxelPosition(var1.x, var1.y, var1.z, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var4, var1.y, var1.z, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var1.x, var5, var1.z, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var4, var5, var1.z, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var1.x, var1.y, var6, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var4, var1.y, var6, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var1.x, var5, var6, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var4, var5, var6, var1.extent / 2L));
         val var14: HashSet = HashSet();
         val var16: java.util.Iterator = var7.iterator();

         while (var16.hasNext()) {
            val var17: SparseVoxelPosition;
            var6 = (int)(var17 = var16.next() as SparseVoxelPosition).getVolume();
            val var20: java.lang.Iterable = var2;
            val var8: java.util.Collection = ArrayList();
            val var22: java.util.Iterator = var20.iterator();

            while (var22.hasNext()) {
               val var9: Any;
               if (var17.contains((var9 = var22.next()) as Vector3ic)) {
                  var8.add(var9);
               }
            }

            var14.addAll(this.a(var17, CollectionsKt.toSet(var8 as java.util.List), var3, var6));
         }

         var14 as java.util.Set;
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: DU, var1: SparseVoxelPosition, var2: java.util.Set, var3: java.lang.Long, var4: Int, var5: Int): java.util.Set {
      var0.a(var1, var2, null, 4096);
   }

   fun a(var1: SparseVoxelPosition, var2: IntArray, var3: java.lang.Long, var4: Int): MutableSet<SparseVoxelPosition> {
      val var5: Int;
      if ((var5 = this.a(var2, var1.x shr 4, var1.y shr 4, var1.z shr 4, var1.x, var1.y, var1.z, (int)var1.extent)) == var4) {
         SetsKt.setOf(var1);
      } else if (var1.extent == 1L && var5 == 0) {
         SetsKt.emptySet();
      } else {
         var4 = var1.x + Math.max(1, (int)var1.extent / 2);
         var5 = var1.y + Math.max(1, (int)var1.extent / 2);
         var var6: Int = var1.z + Math.max(1, (int)var1.extent / 2);
         val var7: HashSet;
         (var7 = HashSet()).add(SparseVoxelPosition(var1.x, var1.y, var1.z, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var4, var1.y, var1.z, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var1.x, var5, var1.z, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var4, var5, var1.z, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var1.x, var1.y, var6, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var4, var1.y, var6, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var1.x, var5, var6, var1.extent / 2L));
         var7.add(SparseVoxelPosition(var4, var5, var6, var1.extent / 2L));
         val var8: HashSet = HashSet();
         val var10: java.util.Iterator = var7.iterator();

         while (var10.hasNext()) {
            val var12: SparseVoxelPosition;
            var6 = (int)(var12 = var10.next() as SparseVoxelPosition).getVolume();
            var8.addAll(this.a(var12, var2, var3, var6));
         }

         var8 as java.util.Set;
      }
   }

   @JvmStatic
   @JvmSynthetic
   fun `a$default`(var0: DU, var1: SparseVoxelPosition, var2: IntArray, var3: java.lang.Long, var4: Int, var5: Int): java.util.Set {
      var0.a(var1, var2, var3, 4096);
   }

   fun a(var1: IntArray): Int {
      var var2: Int = 0;
      var var3: Int = 0;

      for (var4 in var1.length..var3) {
         val var5: Int;
         if ((var5 = var1[var3]) != -1) {
            var var10000: Boolean;
            when (var5 = var5) {
               2, 3 -> var10000 = true;
               else -> var10000 = var5 == this.f;
            }

            if (var10000) {
               var2++;
            }
         }
      }

      var2;
   }

   fun a(var1: IntArray, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int, var7: Int, var8: Int): Int {
      var var9: Int = 0;
      val var10: Int = var5 + var8;
      val var11: Int = var6 + var8;
      var8 = var7 + var8;
      var var12: Int = 0;

      for (var13 in var1.length..var12) {
         if ((var12 and 15) + (var2 shl 4) >= var5
            && (var12 and 15) + (var2 shl 4) < var10
            && (var12 shr 8 and 15) + (var3 shl 4) >= var6
            && (var12 shr 8 and 15) + (var3 shl 4) < var11
            && (var12 shr 4 and 15) + (var4 shl 4) >= var7
            && (var12 shr 4 and 15) + (var4 shl 4) < var8) {
            val var18: Int = var1[var12];
            if (this.f == 1 && var1[var12] > 0 || this.f == 0 && (var1[var12] == 0 || var1[var12] == 3 || var1[var12] == 2)) {
               var9++;
            }
         }
      }

      var9;
   }

   @JvmStatic
   fun a(var0: IntArray, var1: Int, var2: Int, var3: Int, var4: Int): IntArray {
      val var5: IntArray = IntArray(4096);
      var var6: Int = 0;

      for (var7 in var0.length..var6) {
         var5[var6] = if ((var6 and 15) >= var1
               && (var6 and 15) < var1 + var4
               && (var6 shr 4 and 15) >= var2
               && (var6 shr 4 and 15) < var2 + var4
               && (var6 shr 8 and 15) >= var3
               && (var6 shr 8 and 15) < var3 + var4)
            var0[var6]
            else
            -1;
      }

      var5;
   }

   @JvmStatic
   fun b(var0: IntArray, var1: Int, var2: Int, var3: Int, var4: Int, var5: Int, var6: Int, var7: Int): IntArray {
      val var8: IntArray = IntArray(4096);
      val var9: Int = var4 + var7;
      val var10: Int = var5 + var7;
      var7 = var6 + var7;
      var var11: Int = 0;

      for (var12 in var0.length..var11) {
         var8[var11] = if ((var11 and 15) + (var1 shl 4) >= var4
               && (var11 and 15) + (var1 shl 4) < var9
               && (var11 shr 4 and 15) + (var2 shl 4) >= var5
               && (var11 shr 4 and 15) + (var2 shl 4) < var10
               && (var11 shr 8 and 15) + (var3 shl 4) >= var6
               && (var11 shr 8 and 15) + (var3 shl 4) < var7)
            var0[var11]
            else
            -1;
      }

      var8;
   }

   override final fun a(var1: Int, var2: Int, var3: Int, var4: In) {
      if (!this.a.contains(Vector3i(var1 shr 4, var2 shr 4, var3 shr 4))) {
         this.l.put(Vector3i(var1 shr 4, var2 shr 4, var3 shr 4), var4);
      }
   }

   override final fun e(var1: Int, var2: Int, var3: Int) {
      if (!this.a.contains(Vector3i(var1 shr 4, var2 shr 4, var3 shr 4))) {
         this.l.put(Vector3i(var1 shr 4, var2 shr 4, var3 shr 4), In(var1 shr 4, var2 shr 4, var3 shr 4, null, 8, null));
      }
   }

   fun a(var1: final DD, var2: final DD, var3: final DD): Boolean {
      val var4: DU.d = this.o.remove(var1);
      val var5: DU.d = this.o.remove(var2);
      val var6: SparseVoxelPosition = this.b(var1);
      val var7: SparseVoxelPosition = this.b(var2);
      if (var4 == null && var5 == null) {
         this.j.add(MergeEventImpl(false, if (var6 != null) var6 else Vector3i(), if (var7 != null) var7 else Vector3i(), Vector3i(), this.f));
         this.v();
         false;
      } else {
         var var10000: DU.d;
         if (var4 != null && var5 != null) {
            var10000 = var4.a(var5);
         } else {
            var10000 = var4;
            if (var4 == null) {
               var10000 = var5;
               if (var5 == null) {
                  var10000 = DU.d(HashMap<>());
               }
            }
         }

         val var17: SparseVoxelPosition;
         if ((var17 = this.b(var3)) != null && this.k.containsKey((Object)var17) && this.a(Vector3i(var17)) != ConnectionStatus.DISCONNECTED) {
            this.p.forEach({ p0: ChunkClaim, p1: HashSet<DD> ->
               var2x.remove(var1);
               var2x.remove(var2);
            } as Function2.org/valkyrienskies/core/impl/shadow/DU##Lambda_11_1000({ p0: ChunkClaim, p1: HashSet<DD> ->
               var2x.remove(var1);
               var2x.remove(var2);
            } as Function2));
            this.j.add(MergeEventImpl(false, if (var6 != null) var6 else Vector3i(), if (var7 != null) var7 else Vector3i(), var17, this.f));
            this.v();
            false;
         } else {
            this.o.put(var3, var10000);
            val var14: ObjectRef;
            (var14 = ObjectRef()).element = Pair(ChunkClaimImpl.Companion.getClaim(-1, -1), var3);
            this.p.forEach({ p0: ChunkClaim, p1: HashSet<DD> ->
               var var3x: Boolean = false;
               if (var2x.remove(var1) || var2x.remove(var2)) {
                  var2x.add(var3);
                  var3x = true;
               }

               if (!var3x && var17 != null) {
                  var14.element = Pair(ChunkClaimImpl.Companion.getClaim(var17.x() shr 4, var17.z() shr 4), var3);
               }
            } as Function2.org/valkyrienskies/core/impl/shadow/DU##Lambda_12_1000({ p0: ChunkClaim, p1: HashSet<DD> ->
               var var3x: Boolean = false;
               if (var2x.remove(var1) || var2x.remove(var2)) {
                  var2x.add(var3);
                  var3x = true;
               }

               if (!var3x && var17 != null) {
                  var14.element = Pair(ChunkClaimImpl.Companion.getClaim(var17.x() shr 4, var17.z() shr 4), var3);
               }
            } as Function2));
            if (!((var14.element as Pair).getFirst() == ChunkClaimImpl.Companion.getClaim(-1, -1))) {
               val var8: java.util.Map = this.p;
               val var10: Any = (var14.element as Pair).getFirst();
               val var11: Any;
               val var18: Any;
               if ((var11 = var8.get(var10)) == null) {
                  var11 = HashSet();
                  var8.put(var10, var11);
                  var18 = var11;
               } else {
                  var18 = var11;
               }

               (var18 as HashSet).add((var14.element as Pair).getSecond());
            }

            this.j
               .add(
                  MergeEventImpl(
                     true, if (var6 != null) var6 else Vector3i(), if (var7 != null) var7 else Vector3i(), if (var17 != null) var17 else Vector3i(), this.f
                  )
               );
            this.v();
            true;
         }
      }
   }

   fun b(var1: final DD, var2: final DD, var3: final DD): Boolean {
      var var4: DU.d = this.o.remove(var3);
      val var5: SparseVoxelPosition = this.b(var1);
      val var6: SparseVoxelPosition = this.b(var2);
      val var7: SparseVoxelPosition = this.b(var3);
      val var8: DU.b = DU.b(var1, 1L);
      val var9: DU.b = DU.b(var2, 0L, 2, null);
      val var10: Boolean = true;
      val var11: BooleanRef;
      (var11 = BooleanRef()).element = true;
      if (var5 != null && this.k.containsKey((Object)var5) && this.a(Vector3i(var5)) != ConnectionStatus.DISCONNECTED) {
         var10 = false;
      }

      if (var6 != null && this.k.containsKey((Object)var6) && this.a(Vector3i(var6)) != ConnectionStatus.DISCONNECTED) {
         var11.element = false;
      }

      var10 = var8.getSize() == 0L || !var10;
      val var49: Pair;
      if (var4 != null) {
         val var10001: Component = var8;
         val var13: Component = var9;
         val var12: Component = var10001;
         var4 = var4;
         val var14: HashMap = HashMap();
         val var15: HashMap = HashMap();

         for (var17 in var4.a.keySet()) {
            val var10000: Any = var4.a.get(var17);
            val var18: DoublePair;
            val var31: Double = (var18 = var17.splitDouble((var10000 as java.lang.Number).doubleValue(), var12, var13)).component1();
            val var33: Double = var18.component2();
            val var46: java.lang.Double = var31;
            val var48: java.util.Map = var14;
            var48.put(var17, var46);
            var15.put(var17, var33);
         }

         var49 = Pair(DU.d(var14), DU.d(var15));
      } else {
         var49 = Pair(DU.d(HashMap<>()), DU.d(HashMap<>()));
      }

      val var45: DU.d = var49.component1() as DU.d;
      var4 = var49.component2() as DU.d;
      var8.a = true;
      var9.a = true;
      if (!var10) {
         this.o.put(var1, var45);
      }

      if (var11.element) {
         this.o.put(var2, var4);
      }

      val var43: ObjectRef;
      (var43 = ObjectRef()).element = Pair(ChunkClaimImpl.Companion.getClaim(-1, -1), HashSet());
      this.p
         .forEach(
            { p0: ChunkClaim, p1: HashSet<DD> ->
               // $VF: Couldn't be decompiled
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent.toJava(int)" because the return value of "org.vineflower.kotlin.expr.KExitExprent.getValue()" is null
               //   at org.vineflower.kotlin.expr.KExitExprent.toJava(KExitExprent.java:41)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
               //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
               //
               // Bytecode:
               // 000: aload 1
               // 001: ldc ""
               // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
               // 006: aload 2
               // 007: ldc ""
               // 009: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
               // 00c: aload 2
               // 00d: aload 0
               // 00e: getfield org/valkyrienskies/core/impl/shadow/DU$3.$a Lorg/valkyrienskies/core/impl/shadow/DD;
               // 011: invokevirtual java/util/HashSet.remove (Ljava/lang/Object;)Z
               // 014: istore 1
               // 015: bipush 0
               // 016: istore 3
               // 017: bipush 0
               // 018: istore 4
               // 01a: iload 1
               // 01b: ifeq 046
               // 01e: aload 0
               // 01f: getfield org/valkyrienskies/core/impl/shadow/DU$3.$b Z
               // 022: ifne 030
               // 025: aload 2
               // 026: aload 0
               // 027: getfield org/valkyrienskies/core/impl/shadow/DU$3.$c Lorg/valkyrienskies/core/impl/shadow/DD;
               // 02a: invokevirtual java/util/HashSet.add (Ljava/lang/Object;)Z
               // 02d: pop
               // 02e: bipush 1
               // 02f: istore 3
               // 030: aload 0
               // 031: getfield org/valkyrienskies/core/impl/shadow/DU$3.$d Lkotlin/jvm/internal/Ref$BooleanRef;
               // 034: getfield kotlin/jvm/internal/Ref$BooleanRef.element Z
               // 037: ifeq 046
               // 03a: aload 2
               // 03b: aload 0
               // 03c: getfield org/valkyrienskies/core/impl/shadow/DU$3.$e Lorg/valkyrienskies/core/impl/shadow/DD;
               // 03f: invokevirtual java/util/HashSet.add (Ljava/lang/Object;)Z
               // 042: pop
               // 043: bipush 1
               // 044: istore 4
               // 046: iload 3
               // 047: ifne 115
               // 04a: iload 4
               // 04c: ifne 115
               // 04f: aload 0
               // 050: getfield org/valkyrienskies/core/impl/shadow/DU$3.$f Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
               // 053: ifnull 099
               // 056: aload 0
               // 057: getfield org/valkyrienskies/core/impl/shadow/DU$3.$b Z
               // 05a: ifne 099
               // 05d: aload 0
               // 05e: getfield org/valkyrienskies/core/impl/shadow/DU$3.$f Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
               // 061: invokevirtual org/joml/Vector3i.x ()I
               // 064: bipush 4
               // 065: ishr
               // 066: istore 1
               // 067: aload 0
               // 068: getfield org/valkyrienskies/core/impl/shadow/DU$3.$f Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
               // 06b: invokevirtual org/joml/Vector3i.z ()I
               // 06e: bipush 4
               // 06f: ishr
               // 070: istore 2
               // 071: getstatic org/valkyrienskies/core/impl/game/ChunkClaimImpl.Companion Lorg/valkyrienskies/core/impl/game/ChunkClaimImpl$Companion;
               // 074: iload 1
               // 075: iload 2
               // 076: invokevirtual org/valkyrienskies/core/impl/game/ChunkClaimImpl$Companion.getClaim (II)Lorg/valkyrienskies/core/api/ships/properties/ChunkClaim;
               // 079: astore 1
               // 07a: aload 0
               // 07b: getfield org/valkyrienskies/core/impl/shadow/DU$3.$g Lkotlin/jvm/internal/Ref$ObjectRef;
               // 07e: new kotlin/Pair
               // 081: dup
               // 082: aload 1
               // 083: bipush 1
               // 084: anewarray 120
               // 087: dup
               // 088: astore 1
               // 089: bipush 0
               // 08a: aload 0
               // 08b: getfield org/valkyrienskies/core/impl/shadow/DU$3.$c Lorg/valkyrienskies/core/impl/shadow/DD;
               // 08e: aastore
               // 08f: aload 1
               // 090: invokestatic kotlin/collections/SetsKt.hashSetOf ([Ljava/lang/Object;)Ljava/util/HashSet;
               // 093: invokespecial kotlin/Pair.<init> (Ljava/lang/Object;Ljava/lang/Object;)V
               // 096: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 099: aload 0
               // 09a: getfield org/valkyrienskies/core/impl/shadow/DU$3.$h Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
               // 09d: ifnull 115
               // 0a0: aload 0
               // 0a1: getfield org/valkyrienskies/core/impl/shadow/DU$3.$d Lkotlin/jvm/internal/Ref$BooleanRef;
               // 0a4: getfield kotlin/jvm/internal/Ref$BooleanRef.element Z
               // 0a7: ifeq 115
               // 0aa: aload 0
               // 0ab: getfield org/valkyrienskies/core/impl/shadow/DU$3.$h Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
               // 0ae: invokevirtual org/joml/Vector3i.x ()I
               // 0b1: bipush 4
               // 0b2: ishr
               // 0b3: istore 1
               // 0b4: aload 0
               // 0b5: getfield org/valkyrienskies/core/impl/shadow/DU$3.$h Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
               // 0b8: invokevirtual org/joml/Vector3i.z ()I
               // 0bb: bipush 4
               // 0bc: ishr
               // 0bd: istore 2
               // 0be: getstatic org/valkyrienskies/core/impl/game/ChunkClaimImpl.Companion Lorg/valkyrienskies/core/impl/game/ChunkClaimImpl$Companion;
               // 0c1: iload 1
               // 0c2: iload 2
               // 0c3: invokevirtual org/valkyrienskies/core/impl/game/ChunkClaimImpl$Companion.getClaim (II)Lorg/valkyrienskies/core/api/ships/properties/ChunkClaim;
               // 0c6: astore 1
               // 0c7: aload 0
               // 0c8: getfield org/valkyrienskies/core/impl/shadow/DU$3.$g Lkotlin/jvm/internal/Ref$ObjectRef;
               // 0cb: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 0ce: checkcast kotlin/Pair
               // 0d1: invokevirtual kotlin/Pair.getSecond ()Ljava/lang/Object;
               // 0d4: checkcast java/util/HashSet
               // 0d7: invokevirtual java/util/HashSet.isEmpty ()Z
               // 0da: ifeq 0fd
               // 0dd: aload 0
               // 0de: getfield org/valkyrienskies/core/impl/shadow/DU$3.$g Lkotlin/jvm/internal/Ref$ObjectRef;
               // 0e1: new kotlin/Pair
               // 0e4: dup
               // 0e5: aload 1
               // 0e6: bipush 1
               // 0e7: anewarray 120
               // 0ea: dup
               // 0eb: astore 1
               // 0ec: bipush 0
               // 0ed: aload 0
               // 0ee: getfield org/valkyrienskies/core/impl/shadow/DU$3.$e Lorg/valkyrienskies/core/impl/shadow/DD;
               // 0f1: aastore
               // 0f2: aload 1
               // 0f3: invokestatic kotlin/collections/SetsKt.hashSetOf ([Ljava/lang/Object;)Ljava/util/HashSet;
               // 0f6: invokespecial kotlin/Pair.<init> (Ljava/lang/Object;Ljava/lang/Object;)V
               // 0f9: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 0fc: return
               // 0fd: aload 0
               // 0fe: getfield org/valkyrienskies/core/impl/shadow/DU$3.$g Lkotlin/jvm/internal/Ref$ObjectRef;
               // 101: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
               // 104: checkcast kotlin/Pair
               // 107: invokevirtual kotlin/Pair.getSecond ()Ljava/lang/Object;
               // 10a: checkcast java/util/HashSet
               // 10d: aload 0
               // 10e: getfield org/valkyrienskies/core/impl/shadow/DU$3.$e Lorg/valkyrienskies/core/impl/shadow/DD;
               // 111: invokevirtual java/util/HashSet.add (Ljava/lang/Object;)Z
               // 114: pop
               // 115: return
            } as Function2.org/valkyrienskies/core/impl/shadow/DU##Lambda_13_1000(
               { p0: ChunkClaim, p1: HashSet<DD> ->
                  // $VF: Couldn't be decompiled
                  // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
                  // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.modules.decompiler.exps.Exprent.toJava(int)" because the return value of "org.vineflower.kotlin.expr.KExitExprent.getValue()" is null
                  //   at org.vineflower.kotlin.expr.KExitExprent.toJava(KExitExprent.java:41)
                  //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:925)
                  //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:87)
                  //
                  // Bytecode:
                  // 000: aload 1
                  // 001: ldc ""
                  // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
                  // 006: aload 2
                  // 007: ldc ""
                  // 009: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
                  // 00c: aload 2
                  // 00d: aload 0
                  // 00e: getfield org/valkyrienskies/core/impl/shadow/DU$3.$a Lorg/valkyrienskies/core/impl/shadow/DD;
                  // 011: invokevirtual java/util/HashSet.remove (Ljava/lang/Object;)Z
                  // 014: istore 1
                  // 015: bipush 0
                  // 016: istore 3
                  // 017: bipush 0
                  // 018: istore 4
                  // 01a: iload 1
                  // 01b: ifeq 046
                  // 01e: aload 0
                  // 01f: getfield org/valkyrienskies/core/impl/shadow/DU$3.$b Z
                  // 022: ifne 030
                  // 025: aload 2
                  // 026: aload 0
                  // 027: getfield org/valkyrienskies/core/impl/shadow/DU$3.$c Lorg/valkyrienskies/core/impl/shadow/DD;
                  // 02a: invokevirtual java/util/HashSet.add (Ljava/lang/Object;)Z
                  // 02d: pop
                  // 02e: bipush 1
                  // 02f: istore 3
                  // 030: aload 0
                  // 031: getfield org/valkyrienskies/core/impl/shadow/DU$3.$d Lkotlin/jvm/internal/Ref$BooleanRef;
                  // 034: getfield kotlin/jvm/internal/Ref$BooleanRef.element Z
                  // 037: ifeq 046
                  // 03a: aload 2
                  // 03b: aload 0
                  // 03c: getfield org/valkyrienskies/core/impl/shadow/DU$3.$e Lorg/valkyrienskies/core/impl/shadow/DD;
                  // 03f: invokevirtual java/util/HashSet.add (Ljava/lang/Object;)Z
                  // 042: pop
                  // 043: bipush 1
                  // 044: istore 4
                  // 046: iload 3
                  // 047: ifne 115
                  // 04a: iload 4
                  // 04c: ifne 115
                  // 04f: aload 0
                  // 050: getfield org/valkyrienskies/core/impl/shadow/DU$3.$f Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
                  // 053: ifnull 099
                  // 056: aload 0
                  // 057: getfield org/valkyrienskies/core/impl/shadow/DU$3.$b Z
                  // 05a: ifne 099
                  // 05d: aload 0
                  // 05e: getfield org/valkyrienskies/core/impl/shadow/DU$3.$f Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
                  // 061: invokevirtual org/joml/Vector3i.x ()I
                  // 064: bipush 4
                  // 065: ishr
                  // 066: istore 1
                  // 067: aload 0
                  // 068: getfield org/valkyrienskies/core/impl/shadow/DU$3.$f Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
                  // 06b: invokevirtual org/joml/Vector3i.z ()I
                  // 06e: bipush 4
                  // 06f: ishr
                  // 070: istore 2
                  // 071: getstatic org/valkyrienskies/core/impl/game/ChunkClaimImpl.Companion Lorg/valkyrienskies/core/impl/game/ChunkClaimImpl$Companion;
                  // 074: iload 1
                  // 075: iload 2
                  // 076: invokevirtual org/valkyrienskies/core/impl/game/ChunkClaimImpl$Companion.getClaim (II)Lorg/valkyrienskies/core/api/ships/properties/ChunkClaim;
                  // 079: astore 1
                  // 07a: aload 0
                  // 07b: getfield org/valkyrienskies/core/impl/shadow/DU$3.$g Lkotlin/jvm/internal/Ref$ObjectRef;
                  // 07e: new kotlin/Pair
                  // 081: dup
                  // 082: aload 1
                  // 083: bipush 1
                  // 084: anewarray 120
                  // 087: dup
                  // 088: astore 1
                  // 089: bipush 0
                  // 08a: aload 0
                  // 08b: getfield org/valkyrienskies/core/impl/shadow/DU$3.$c Lorg/valkyrienskies/core/impl/shadow/DD;
                  // 08e: aastore
                  // 08f: aload 1
                  // 090: invokestatic kotlin/collections/SetsKt.hashSetOf ([Ljava/lang/Object;)Ljava/util/HashSet;
                  // 093: invokespecial kotlin/Pair.<init> (Ljava/lang/Object;Ljava/lang/Object;)V
                  // 096: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
                  // 099: aload 0
                  // 09a: getfield org/valkyrienskies/core/impl/shadow/DU$3.$h Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
                  // 09d: ifnull 115
                  // 0a0: aload 0
                  // 0a1: getfield org/valkyrienskies/core/impl/shadow/DU$3.$d Lkotlin/jvm/internal/Ref$BooleanRef;
                  // 0a4: getfield kotlin/jvm/internal/Ref$BooleanRef.element Z
                  // 0a7: ifeq 115
                  // 0aa: aload 0
                  // 0ab: getfield org/valkyrienskies/core/impl/shadow/DU$3.$h Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
                  // 0ae: invokevirtual org/joml/Vector3i.x ()I
                  // 0b1: bipush 4
                  // 0b2: ishr
                  // 0b3: istore 1
                  // 0b4: aload 0
                  // 0b5: getfield org/valkyrienskies/core/impl/shadow/DU$3.$h Lorg/valkyrienskies/core/api/world/connectivity/SparseVoxelPosition;
                  // 0b8: invokevirtual org/joml/Vector3i.z ()I
                  // 0bb: bipush 4
                  // 0bc: ishr
                  // 0bd: istore 2
                  // 0be: getstatic org/valkyrienskies/core/impl/game/ChunkClaimImpl.Companion Lorg/valkyrienskies/core/impl/game/ChunkClaimImpl$Companion;
                  // 0c1: iload 1
                  // 0c2: iload 2
                  // 0c3: invokevirtual org/valkyrienskies/core/impl/game/ChunkClaimImpl$Companion.getClaim (II)Lorg/valkyrienskies/core/api/ships/properties/ChunkClaim;
                  // 0c6: astore 1
                  // 0c7: aload 0
                  // 0c8: getfield org/valkyrienskies/core/impl/shadow/DU$3.$g Lkotlin/jvm/internal/Ref$ObjectRef;
                  // 0cb: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
                  // 0ce: checkcast kotlin/Pair
                  // 0d1: invokevirtual kotlin/Pair.getSecond ()Ljava/lang/Object;
                  // 0d4: checkcast java/util/HashSet
                  // 0d7: invokevirtual java/util/HashSet.isEmpty ()Z
                  // 0da: ifeq 0fd
                  // 0dd: aload 0
                  // 0de: getfield org/valkyrienskies/core/impl/shadow/DU$3.$g Lkotlin/jvm/internal/Ref$ObjectRef;
                  // 0e1: new kotlin/Pair
                  // 0e4: dup
                  // 0e5: aload 1
                  // 0e6: bipush 1
                  // 0e7: anewarray 120
                  // 0ea: dup
                  // 0eb: astore 1
                  // 0ec: bipush 0
                  // 0ed: aload 0
                  // 0ee: getfield org/valkyrienskies/core/impl/shadow/DU$3.$e Lorg/valkyrienskies/core/impl/shadow/DD;
                  // 0f1: aastore
                  // 0f2: aload 1
                  // 0f3: invokestatic kotlin/collections/SetsKt.hashSetOf ([Ljava/lang/Object;)Ljava/util/HashSet;
                  // 0f6: invokespecial kotlin/Pair.<init> (Ljava/lang/Object;Ljava/lang/Object;)V
                  // 0f9: putfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
                  // 0fc: return
                  // 0fd: aload 0
                  // 0fe: getfield org/valkyrienskies/core/impl/shadow/DU$3.$g Lkotlin/jvm/internal/Ref$ObjectRef;
                  // 101: getfield kotlin/jvm/internal/Ref$ObjectRef.element Ljava/lang/Object;
                  // 104: checkcast kotlin/Pair
                  // 107: invokevirtual kotlin/Pair.getSecond ()Ljava/lang/Object;
                  // 10a: checkcast java/util/HashSet
                  // 10d: aload 0
                  // 10e: getfield org/valkyrienskies/core/impl/shadow/DU$3.$e Lorg/valkyrienskies/core/impl/shadow/DD;
                  // 111: invokevirtual java/util/HashSet.add (Ljava/lang/Object;)Z
                  // 114: pop
                  // 115: return
               } as Function2
            )
         );
      if (!((var43.element as Pair).getSecond() as java.util.Collection).isEmpty()) {
         val var35: java.util.Map = this.p;
         val var37: Any = (var43.element as Pair).getFirst();
         val var38: Any;
         val var50: Any;
         if ((var38 = var35.get(var37)) == null) {
            var38 = HashSet();
            var35.put(var37, var38);
            var50 = var38;
         } else {
            var50 = var38;
         }

         (var50 as HashSet).addAll((var43.element as Pair).getSecond() as java.util.Collection);
      }

      this.i
         .add(
            SplitEventImpl(true, if (var7 != null) var7 else Vector3i(), if (var5 != null) var5 else Vector3i(), if (var6 != null) var6 else Vector3i(), this.f)
         );
      this.v();
      true;
   }

   fun g(var1: Int, var2: Int, var3: Int): DU.a {
      val var4: DB = this.k.get(var1, var2, var3);

      try {
         if (var4 == null) {
            throw IllegalArgumentException(("Cannot get augmentation info for node $var1 $var2 $var3 not in graph").toString());
         }
      } catch (var5: Exception) {
         a.warn(var5);
         null;
      }

      this.h.c(var4) as DU.a;
   }

   override final fun a(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int): Double {
      val var10000: DU.a = this.g(var2, var3, var4);
      if (var10000 != null) var10000.a(var1) else 0.0;
   }

   override final fun a(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int, var5: DoubleUnaryOperator) {
      this.a(var1, var2, var3, var4, var5.applyAsDouble(this.a(var1, var2, var3, var4)));
   }

   override final fun c(var1: DoubleAugmentation, var2: Int, var3: Int, var4: Int): Double {
      val var5: DB = this.k.get(var2, var3, var4);

      try {
         if (var5 == null) {
            throw IllegalArgumentException(("Cannot collect augmentation from node $var2 $var3 $var4 not in graph").toString());
         }

         if (this.b(var5) != ConnectionStatus.DISCONNECTED) {
            throw IllegalArgumentException("Cannot collect augmentation for node connected to the world".toString());
         }
      } catch (var6: Exception) {
         a.warn(var6);
         0.0;
      }

      val var10000: DU.a = this.h.d(var5) as DU.a;
      if (var10000 == null) 0.0 else var10000.a(var1);
   }

   override final fun a(var1: DoubleComponentAugmentation, var2: Int, var3: Int, var4: Int, var5: Double): Boolean {
      val var7: DB = this.k.get(var2, var3, var4);

      try {
         if (var7 == null) {
            throw IllegalArgumentException(("Cannot store to component on node $var2 $var3 $var4 not in graph").toString());
         }

         if (this.b(var7) != ConnectionStatus.DISCONNECTED) {
            throw IllegalArgumentException("Cannot store to component for node connected to the world".toString());
         }
      } catch (var17: Exception) {
         a.warn(var17);
         false;
      }

      val var10000: DG = this.h.a.get(var7);
      val var30: DD = if (var10000 != null && var10000.a != null && var10000.a.c != null) var10000.a.c.e() as DD else null;
      if (var30 == null) {
         false;
      } else {
         var var31: DU.d = this.o.get(var30);
         if (var31 == null) {
            var31 = DU.d(HashMap<>());
         }

         var31.a.put(var1, var5);
         this.o.put(var30, var31);
         val var19: ChunkClaim = ChunkClaimImpl.Companion.getClaim(var2 shl 4, var4 shl 4);
         if (this.p.get(var19) != null) {
            val var32: Any = this.p.get(var19);
            (var32 as HashSet).add(var30);
         } else {
            val var20: java.util.Map = this.p;
            val var23: Array<DD>;
            (var23 = arrayOfNulls(1))[0] = var30;
            var20.put(var19, SetsKt.hashSetOf(var23));
         }

         true;
      }
   }

   override final fun a(var1: DoubleComponentAugmentation, var2: Int, var3: Int, var4: Int): Double {
      val var5: DB = this.k.get(var2, var3, var4);

      try {
         if (var5 == null) {
            throw IllegalArgumentException(("Cannot retrieve from component on node $var2 $var3 $var4 not in graph").toString());
         }

         if (this.b(var5) != ConnectionStatus.DISCONNECTED) {
            throw IllegalArgumentException("Cannot store to component for node connected to the world".toString());
         }
      } catch (var6: Exception) {
         a.warn(var6);
         0.0;
      }

      val var10000: DG = this.h.a.get(var5);
      val var12: DD = if (var10000 != null && var10000.a != null && var10000.a.c != null) var10000.a.c.e() as DD else null;
      if (var12 == null) {
         0.0;
      } else {
         val var13: DU.d = this.o.get(var12);
         if (var13 == null) 0.0 else var13.a(var1);
      }
   }

   override final fun b(var1: DoubleComponentAugmentation, var2: Int, var3: Int, var4: Int): Boolean {
      val var5: DB = this.k.get(var2, var3, var4);

      try {
         if (var5 == null) {
            throw IllegalArgumentException(("Cannot remove from component on node $var2 $var3 $var4 not in graph").toString());
         }

         if (this.b(var5) != ConnectionStatus.DISCONNECTED) {
            throw IllegalArgumentException("Cannot store to component for node connected to the world".toString());
         }
      } catch (var6: Exception) {
         a.warn(var6);
         false;
      }

      val var10000: DG = this.h.a.get(var5);
      val var15: DD = if (var10000 != null && var10000.a != null && var10000.a.c != null) var10000.a.c.e() as DD else null;
      if (var15 == null) {
         false;
      } else {
         val var16: DU.d = this.o.get(var15);
         if (var16 == null) {
            false;
         } else {
            var16.a.remove(var1);
            this.o.put(var15, var16);
            true;
         }
      }
   }

   override final fun f(var1: Int, var2: Int, var3: Int): Long {
      val var4: DB = this.k.get(var1, var2, var3);

      try {
         if (var4 == null) {
            throw IllegalArgumentException(("Cannot get component size for node $var1 $var2 $var3 not in graph").toString());
         }

         if (this.b(var4) != ConnectionStatus.DISCONNECTED) {
            throw IllegalArgumentException("Cannot store to component for node connected to the world".toString());
         }
      } catch (var8: Exception) {
         a.warn(var8);
         -1L;
      }

      val var10000: DG = this.h.a.get(var4);
      if (var10000 != null && var10000.a != null && var10000.a.c != null && var10000.a.c.e() as DD != null) (long)this.c(this.u, var1, var2, var3) else -1L;
   }

   override final fun a(var1: DoubleAugmentation, var2: ChunkClaim): MutableMap<Triple<Integer, Integer, Integer>, java.lang.Double> {
      val var3: HashMap = HashMap();
      var var4: HashSet = HashSet<>(this.o.keySet());
      var var5: HashSet = HashSet();
      val var6: HashSet;
      if (var2 != null && (var6 = this.p.get(var2)) != null) {
         var var7: java.lang.Iterable = var6;
         var var8: java.util.Collection = ArrayList();
         val var18: java.util.Iterator = var7.iterator();

         while (var18.hasNext()) {
            val var9: Any;
            if (!this.o.containsKey((var9 = var18.next()) as DD)) {
               var8.add(var9);
            }
         }

         var5 = CollectionsKt.toHashSet(var8 as java.util.List);
         var7 = var6;
         var8 = ArrayList();
         val var20: java.util.Iterator = var7.iterator();

         while (var20.hasNext()) {
            val var26: Any;
            if (this.o.containsKey((var26 = var20.next()) as DD)) {
               var8.add(var26);
            }
         }

         var4 = CollectionsKt.toHashSet(var8 as java.util.List);
      }

      val var13: java.util.Iterator = var4.iterator();

      while (var13.hasNext()) {
         val var24: DD;
         val var21: DD = var24 = var13.next() as DD;
         val var10000: SparseVoxelPosition = this.b(var21);
         if (var10000 != null) {
            val var30: Int = var10000.x();
            val var16: Int = var10000.y();
            val var22: Int = var10000.z();
            val var28: ConnectionStatus;
            if ((var28 = this.d(var30, var16, var22)) === ConnectionStatus.DISCONNECTED) {
               var3.put(Triple(var30, var16, var22), this.c(var1, var30, var16, var22));
            } else if (var28 === ConnectionStatus.CONNECTED) {
               this.o.remove(var24);
            }
         }
      }

      for (var25 in var6 = var5) {
         val var31: Any = this.p.get(var2);
         (var31 as HashSet).remove(var25);
      }

      var3 as java.util.Map;
   }

   override final fun a(var1: DoubleComponentAugmentation, var2: ChunkClaim): MutableMap<Triple<Integer, Integer, Integer>, java.lang.Double> {
      val var3: HashMap = HashMap();
      var var4: HashSet = HashSet<>(this.o.keySet());
      var var5: HashSet = HashSet();
      val var6: HashSet;
      if (var2 != null && (var6 = this.p.get(var2)) != null) {
         var var7: java.lang.Iterable = var6;
         var var8: java.util.Collection = ArrayList();
         val var19: java.util.Iterator = var7.iterator();

         while (var19.hasNext()) {
            val var9: Any;
            if (!this.o.containsKey((var9 = var19.next()) as DD)) {
               var8.add(var9);
            }
         }

         var5 = CollectionsKt.toHashSet(var8 as java.util.List);
         var7 = var6;
         var8 = ArrayList();
         val var21: java.util.Iterator = var7.iterator();

         while (var21.hasNext()) {
            val var27: Any;
            if (this.o.containsKey((var27 = var21.next()) as DD)) {
               var8.add(var27);
            }
         }

         var4 = CollectionsKt.toHashSet(var8 as java.util.List);
      }

      val var14: java.util.Iterator = var4.iterator();

      while (var14.hasNext()) {
         val var25: DD;
         val var22: DD = var25 = var14.next() as DD;
         val var10000: DU.d = this.o.get(var25);
         if (var10000 == null) {
            throw IllegalStateException("Component info not found for root, but it's in the index!");
         }

         val var32: SparseVoxelPosition = this.b(var22);
         if (var32 != null) {
            val var17: Int = var32.x();
            val var23: Int = var32.y();
            val var31: Int = var32.z();
            val var11: ConnectionStatus;
            if ((var11 = this.d(var17, var23, var31)) === ConnectionStatus.DISCONNECTED) {
               var3.put(Triple(var17, var23, var31), var10000.a(var1));
            } else if (var11 === ConnectionStatus.CONNECTED) {
               this.o.remove(var25);
            }
         }
      }

      for (var26 in var6 = var5) {
         val var33: Any = this.p.get(var2);
         (var33 as HashSet).remove(var26);
      }

      var3 as java.util.Map;
   }

   override final fun a(var1: ChunkClaim): MutableSet<Vector3ic> {
      while (true) {
         val var2: HashSet;
         if ((var2 = this.p.get(var1)) != null) {
            val var11: HashSet = HashSet();

            for (var8 in var2 = var2) {
               val var9: SparseVoxelPosition;
               if ((var9 = this.b(var8)) != null) {
                  var11.add(var9);
               }
            }

            var11 as java.util.Set;
         }

         this.p.put(var1, HashSet<>());

         for (var6 in this.o.entrySet()) {
            val var4: SparseVoxelPosition;
            if ((var4 = this.b(var6.getKey() as DD)) != null && var1.contains(var4.x() shr 4, var4.z() shr 4)) {
               val var10000: Any = this.p.get(var1);
               (var10000 as HashSet).add(var6.getKey());
            }
         }

         val var10: java.util.Collection;
         if ((var10 = this.p.get(var1)) == null || var10.isEmpty()) {
            SetsKt.emptySet();
         }

         this.v();
         this = this;
      }
   }

   override final fun a(var1: Int, var2: Int, var3: Int, var4: MutableSet<Vector3ic>): DenseBlockPosSet {
      val var5: DB = this.k.get(var1, var2, var3);

      try {
         if (var5 == null) {
            throw IllegalArgumentException(("Cannot index component referencing node $var1 $var2 $var3 not in graph").toString());
         }
      } catch (var8: Exception) {
         a.warn(var8);
         DenseBlockPosSet();
      }

      val var9: ArrayDeque = ArrayDeque();
      val var11: HashSet = HashSet();

      try {
         val var10000: DG = this.h.a.get(var5);
         var12 = if (var10000 != null && var10000.a != null && var10000.a.c != null) var10000.a.c.e() as DD else null;
      } catch (var7: java.lang.Throwable) {
         a.warn("Exception caught in getComponentBlocks during arbitraryVisit.root() for node=$var5: ", var7);
         var12 = null;
      }

      val var13: DD;
      if ((var13 = var12) != null) {
         var9.add(var13);
      }

      do {
         val var14: DD = var9.removeFirst() as DD;
         var11.add(var14);
         val var6: SparseVoxelPosition;
         if ((var6 = this.b(var14)) != null) {
            var4.add(var6);
         }

         if (var14.j != null && !var11.contains(var14.j)) {
            val var10001: DH = var14.j;
            var9.add(var10001);
         }

         if (var14.k != null && !var11.contains(var14.k)) {
            val var19: DH = var14.k;
            var9.add(var19);
         }
      } while (!(var9 as java.util.Collection).isEmpty())

      val var15: DenseBlockPosSet;
      (var15 = DenseBlockPosSet()).addAll(var4);
      var15;
   }

   override final fun b(var1: Int, var2: Int, var3: Int, var4: MutableSet<SparseVoxelPosition>): MutableSet<SparseVoxelPosition> {
      val var5: DB = this.k.get(var1, var2, var3);

      try {
         if (var5 == null) {
            throw IllegalArgumentException(("Cannot index component referencing node $var1 $var2 $var3 not in graph").toString());
         }
      } catch (var8: Exception) {
         a.warn("Exception caught in getComponentVoxels: $var8");
         var4;
      }

      val var9: ArrayDeque = ArrayDeque();
      val var11: HashSet = HashSet();

      try {
         val var10000: DG = this.h.a.get(var5);
         var12 = if (var10000 != null && var10000.a != null && var10000.a.c != null) var10000.a.c.e() as DD else null;
      } catch (var7: java.lang.Throwable) {
         a.warn("Exception caught in getComponentVoxels during arbitraryVisit.root() for node=$var5: ", var7);
         var12 = null;
      }

      val var13: DD;
      if ((var13 = var12) != null) {
         var9.add(var13);
      }

      do {
         val var14: DD;
         if ((var14 = var9.removeFirst() as DD) != null) {
            var11.add(var14);
            val var6: SparseVoxelPosition;
            if ((var6 = this.b(var14)) != null) {
               var4.add(var6);
            }

            if (var14.j != null && !var11.contains(var14.j)) {
               val var10001: DH = var14.j;
               var9.add(var10001);
            }

            if (var14.k != null && !var11.contains(var14.k)) {
               val var18: DH = var14.k;
               var9.add(var18);
            }
         }
      } while (!(var9 as java.util.Collection).isEmpty())

      var4;
   }

   override final fun a(): Pair<HashSet<SplitEvent>, HashSet<MergeEvent>> {
      val var1: Pair = Pair(HashSet<>(this.i), HashSet<>(this.j));
      this.i.clear();
      this.j.clear();
      var1;
   }

   fun a(var1: Any): Vector3ic {
      if (var1 !is DU.a) null else Vector3i((int)(var1 as DU.a).a(this.q), (int)(var1 as DU.a).a(this.r), (int)(var1 as DU.a).a(this.s)) as Vector3ic;
   }

   fun b(var1: Any): SparseVoxelPosition {
      if (var1 !is DU.a)
         null
         else
         SparseVoxelPosition((int)(var1 as DU.a).a(this.q), (int)(var1 as DU.a).a(this.r), (int)(var1 as DU.a).a(this.s), (long)(var1 as DU.a).a(this.t) + 1L);
   }

   fun a(var1: DD): Vector3ic {
      this.a(if (var1.b != null) var1.b.f else null);
   }

   fun b(var1: DD): SparseVoxelPosition {
      this.b(if (var1.b != null) var1.b.f else null);
   }

   @JvmStatic
   fun a(var0: Function2, var1: Any, var2: Any) {
      var0(var1, var2);
   }

   @JvmStatic
   fun b(var0: Function2, var1: Any, var2: Any) {
      var0(var1, var2);
   }

   @JvmStatic
   fun c(var0: Function2, var1: Any, var2: Any) {
      var0(var1, var2);
   }

   private data class a(p0: Object2DoubleMap<DoubleAugmentation> = Object2DoubleMaps.emptyMap(), p1: Int = 0) : Dy {
      public final val a: Object2DoubleMap<DoubleAugmentation>
      public final val b: Int

      init {
         this.a = var1;
         this.b = var2;
      }

      public fun a(p0: DoubleAugmentation): Double {
         val var10000: Object2DoubleMap = this.a;
         return var10000.getOrDefault(var1, 0.0);
      }

      public override fun a(p0: Any?, p1: Any?): Any {
         val var3: Object2DoubleMap;
         if ((var1 as DU.a).a.size() <= 0 && (var2 as DU.a).a.size() <= 0) {
            val var17: Object2DoubleMap = Object2DoubleMaps.emptyMap();
            var3 = var17;
         } else {
            var3 = Object2DoubleOpenHashMap() as Object2DoubleMap;
            var var4: ObjectIterator = (var1 as DU.a).a.object2DoubleEntrySet().iterator();

            while (var4.hasNext()) {
               val var5: it.unimi.dsi.fastutil.objects.Object2DoubleMap.Entry;
               val var6: DoubleAugmentation = (var5 = var4.next() as it.unimi.dsi.fastutil.objects.Object2DoubleMap.Entry).getKey() as DoubleAugmentation;
               val var7: Double = var5.getDoubleValue();
               val var10000: DU.a = var2 as DU.a;
               var3.put(var6, var6.combineDouble(var7, var10000.a(var6)));
            }

            var4 = (var2 as DU.a).a.object2DoubleEntrySet().iterator();

            while (var4.hasNext()) {
               val var12: it.unimi.dsi.fastutil.objects.Object2DoubleMap.Entry;
               val var13: DoubleAugmentation = (var12 = var4.next() as it.unimi.dsi.fastutil.objects.Object2DoubleMap.Entry).getKey() as DoubleAugmentation;
               val var14: Double = var12.getDoubleValue();
               val var16: DU.a = var1 as DU.a;
               var3.put(var13, var13.combineDouble(var14, var16.a(var13)));
            }
         }

         return DU.a(var3, (var1 as DU.a).b + (var2 as DU.a).b);
      }

      public operator fun c(): Object2DoubleMap<DoubleAugmentation> {
         return this.a;
      }

      public operator fun d(): Int {
         return this.b;
      }

      @JvmStatic
      public fun a(p0: Object2DoubleMap<DoubleAugmentation> = ..., p1: Int = ...): org.valkyrienskies.core.impl.shadow.DU.a {
         return DU.a(var0, var1);
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: DU.a, var1: Object2DoubleMap, var2: Int, var3: Int): DU.a {
         if ((var3 and 1) != 0) {
            var1 = var0.a;
         }

         if ((var3 and 2) != 0) {
            var2 = var0.b;
         }

         DU.a(var1, var2);
      }

      public override fun toString(): String {
         return "AugData(doubleAugmentations=${this.a}, unloaded=${this.b})";
      }

      public override fun hashCode(): Int {
         return this.a.hashCode() * 31 + Integer.hashCode(this.b);
      }

      public override operator fun equals(p0: Any?): Boolean {
         label28@
         if (this === other) {
            return true;
         } else {
            return other is DU.a && this.a == (other as DU.a).a && this.b == (other as DU.a).b;
         }
      }

      fun a() {
         this(null, 0, 3, null);
      }
   }

   public class b(p0: DD, p1: Long = 0L) : Component {
      public final val b: DD
      public final var c: Long
      public final var a: Boolean

      init {
         this.b = var1;
         this.c = var2;
      }

      public fun a() {
         this.a = true;
      }

      public override fun getSize(): Long {
         if (this.a) {
            throw IllegalStateException("Component is closed, how did you get this");
         } else {
            val var10000: Dy = this.b.f;
            return this.b.c - this.b.c / 2 - this.c + (long)(var10000 as DU.a).a(Ed("core:extent"));
         }
      }

      public override fun collectAugmentation(p0: DoubleAugmentation): Double {
         if (this.a) {
            throw IllegalStateException("Component is closed, how did you get this");
         } else {
            val var10000: Any = this.b.g;
            return (var10000 as DU.a).a(key);
         }
      }
   }

   // $VF: Class flags could not be determined
   internal class c : DF {
      private DU a;

      fun c(var1: DU) {
         this.a = var1;
      }

      fun a(): DU {
         this.a;
      }

      override final fun a(var1: DB, var2: DB, var3: DD, var4: DD): Boolean {
         true;
      }

      override final fun a(var1: DB, var2: DB, var3: DD): Boolean {
         true;
      }

      override final fun a(var1: DB, var2: DB, var3: DD, var4: DD, var5: DD): Boolean {
         this.a.a(var3, var4, var5);
      }

      override final fun b(var1: DB, var2: DB, var3: DD, var4: DD, var5: DD): Boolean {
         this.a.b(var3, var4, var5);
      }
   }

   // $VF: Class flags could not be determined
   internal class d {
      HashMap<DoubleComponentAugmentation, java.lang.Double> a;

      fun d(var1: HashMap<DoubleComponentAugmentation, java.lang.Double>) {
         this.a = var1;
      }

      fun a(var1: DoubleComponentAugmentation): Double {
         val var10000: java.lang.Double = this.a.get(var1);
         if (var10000 == null) 0.0 else var10000;
      }

      fun a(var1: DoubleComponentAugmentation, var2: Double) {
         this.a.put(var1, var2);
      }

      fun b(var1: DoubleComponentAugmentation) {
         this.a.remove(var1);
      }

      fun a(var1: DU.d): DU.d {
         val var2: HashMap = HashMap();

         for (var4 in var1.a.keySet()) {
            val var10000: java.util.Map = var2;
            var10000.put(var4, var4.combineDouble(this.a(var4), var1.a(var4)));
         }

         DU.d(var2);
      }

      fun a(var1: Component, var2: Component): Pair<DU.d, DU.d> {
         val var3: HashMap = HashMap();
         val var4: HashMap = HashMap();

         for (var6 in this.a.keySet()) {
            val var10000: Any = this.a.get(var6);
            val var14: DoublePair;
            val var10: Double = (var14 = var6.splitDouble((var10000 as java.lang.Number).doubleValue(), var1, var2)).component1();
            val var12: Double = var14.component2();
            val var15: java.lang.Double = var10;
            val var17: java.util.Map = var3;
            var17.put(var6, var15);
            var4.put(var6, var12);
         }

         Pair(DU.d(var3), DU.d(var4));
      }

      fun a(): HashMap<DoubleComponentAugmentation, java.lang.Double> {
         this.a;
      }

      @JvmStatic
      fun a(var0: HashMap<DoubleComponentAugmentation, java.lang.Double>): DU.d {
         DU.d(var0);
      }

      @JvmStatic
      @JvmSynthetic
      fun a(var0: DU.d, var1: HashMap, var2: Int): DU.d {
         if ((var2 and 1) != 0) {
            var1 = var0.a;
         }

         DU.d(var1);
      }

      override final fun toString(): java.lang.String {
         "ComponentData(data=${this.a})";
      }

      override final fun hashCode(): Int {
         this.a.hashCode();
      }

      override final fun equals(other: Any): Boolean {
         label22@
         if (this === other) {
            true;
         } else {
            other is DU.d && this.a == (other as DU.d).a;
         }
      }
   }
}
