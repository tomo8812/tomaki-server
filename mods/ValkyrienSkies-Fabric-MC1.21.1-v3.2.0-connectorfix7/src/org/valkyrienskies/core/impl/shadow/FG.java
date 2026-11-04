package org.valkyrienskies.core.impl.shadow;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;
import org.valkyrienskies.core.api.world.LevelYRange;
import org.valkyrienskies.core.internal.joints.VSJointAndId;
import org.valkyrienskies.core.internal.world.VsiPlayerState;
import org.valkyrienskies.core.util.datastructures.BlockPos2ObjectOpenHashMap;

public final class FG {
   final List<FC> a;
   final List<Long> b;
   final Long2ObjectMap<? extends FF> c;
   final Long2ObjectMap<? extends BlockPos2ObjectOpenHashMap<Ip>> d;
   final List<VSJointAndId> e;
   private final List<VSJointAndId> k;
   private final List<Integer> l;
   final List<VsiPlayerState> f;
   final List<FB> g;
   final List<FB> h;
   final Map<String, Vector3dc> i;
   final Map<String, LevelYRange> j;

   public FG(
      List<FC> var1,
      List<Long> var2,
      Long2ObjectMap<? extends FF> var3,
      Long2ObjectMap<? extends BlockPos2ObjectOpenHashMap<Ip>> var4,
      List<VSJointAndId> var5,
      List<VSJointAndId> var6,
      List<Integer> var7,
      List<VsiPlayerState> var8,
      List<FB> var9,
      List<FB> var10,
      Map<String, ? extends Vector3dc> var11,
      Map<String, LevelYRange> var12
   ) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var5, "");
      Intrinsics.checkNotNullParameter(var6, "");
      Intrinsics.checkNotNullParameter(var7, "");
      Intrinsics.checkNotNullParameter(var8, "");
      Intrinsics.checkNotNullParameter(var9, "");
      Intrinsics.checkNotNullParameter(var10, "");
      Intrinsics.checkNotNullParameter(var11, "");
      Intrinsics.checkNotNullParameter(var12, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
      this.k = var6;
      this.l = var7;
      this.f = var8;
      this.g = var9;
      this.h = var10;
      this.i = var11;
      this.j = var12;
   }

   public final List<FC> a() {
      return this.a;
   }

   public final List<Long> b() {
      return this.b;
   }

   public final Long2ObjectMap<? extends FF> c() {
      return this.c;
   }

   public final Long2ObjectMap<? extends BlockPos2ObjectOpenHashMap<Ip>> d() {
      return this.d;
   }

   public final List<VSJointAndId> e() {
      return this.e;
   }

   private List<VSJointAndId> k() {
      return this.k;
   }

   private List<Integer> l() {
      return this.l;
   }

   public final List<VsiPlayerState> f() {
      return this.f;
   }

   public final List<FB> g() {
      return this.g;
   }

   public final List<FB> h() {
      return this.h;
   }

   public final Map<String, Vector3dc> i() {
      return this.i;
   }

   public final Map<String, LevelYRange> j() {
      return this.j;
   }

   private List<FC> m() {
      return this.a;
   }

   private List<Long> n() {
      return this.b;
   }

   private Long2ObjectMap<? extends FF> o() {
      return this.c;
   }

   private Long2ObjectMap<? extends BlockPos2ObjectOpenHashMap<Ip>> p() {
      return this.d;
   }

   private List<VSJointAndId> q() {
      return this.e;
   }

   private List<VSJointAndId> r() {
      return this.k;
   }

   private List<Integer> s() {
      return this.l;
   }

   private List<VsiPlayerState> t() {
      return this.f;
   }

   private List<FB> u() {
      return this.g;
   }

   private List<FB> v() {
      return this.h;
   }

   private Map<String, Vector3dc> w() {
      return this.i;
   }

   private Map<String, LevelYRange> x() {
      return this.j;
   }

   private static FG a(
      List<FC> var0,
      List<Long> var1,
      Long2ObjectMap<? extends FF> var2,
      Long2ObjectMap<? extends BlockPos2ObjectOpenHashMap<Ip>> var3,
      List<VSJointAndId> var4,
      List<VSJointAndId> var5,
      List<Integer> var6,
      List<VsiPlayerState> var7,
      List<FB> var8,
      List<FB> var9,
      Map<String, ? extends Vector3dc> var10,
      Map<String, LevelYRange> var11
   ) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var5, "");
      Intrinsics.checkNotNullParameter(var6, "");
      Intrinsics.checkNotNullParameter(var7, "");
      Intrinsics.checkNotNullParameter(var8, "");
      Intrinsics.checkNotNullParameter(var9, "");
      Intrinsics.checkNotNullParameter(var10, "");
      Intrinsics.checkNotNullParameter(var11, "");
      return new FG(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   // $VF: synthetic method
   private static FG a(
      FG var0,
      List var1,
      List var2,
      Long2ObjectMap var3,
      Long2ObjectMap var4,
      List var5,
      List var6,
      List var7,
      List var8,
      List var9,
      List var10,
      Map var11,
      Map var12,
      int var13
   ) {
      if ((var13 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var13 & 2) != 0) {
         var2 = var0.b;
      }

      if ((var13 & 4) != 0) {
         var3 = var0.c;
      }

      if ((var13 & 8) != 0) {
         var4 = var0.d;
      }

      if ((var13 & 16) != 0) {
         var5 = var0.e;
      }

      if ((var13 & 32) != 0) {
         var6 = var0.k;
      }

      if ((var13 & 64) != 0) {
         var7 = var0.l;
      }

      if ((var13 & 128) != 0) {
         var8 = var0.f;
      }

      if ((var13 & 256) != 0) {
         var9 = var0.g;
      }

      if ((var13 & 512) != 0) {
         var10 = var0.h;
      }

      if ((var13 & 1024) != 0) {
         var11 = var0.i;
      }

      if ((var13 & 2048) != 0) {
         var12 = var0.j;
      }

      Map var25 = var12;
      Map var24 = var11;
      List var23 = var10;
      List var22 = var9;
      List var21 = var8;
      List var20 = var7;
      List var19 = var6;
      List var18 = var5;
      Long2ObjectMap var17 = var4;
      Long2ObjectMap var16 = var3;
      List var15 = var2;
      List var14 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var15, "");
      Intrinsics.checkNotNullParameter(var16, "");
      Intrinsics.checkNotNullParameter(var17, "");
      Intrinsics.checkNotNullParameter(var18, "");
      Intrinsics.checkNotNullParameter(var19, "");
      Intrinsics.checkNotNullParameter(var20, "");
      Intrinsics.checkNotNullParameter(var21, "");
      Intrinsics.checkNotNullParameter(var22, "");
      Intrinsics.checkNotNullParameter(var23, "");
      Intrinsics.checkNotNullParameter(var24, "");
      Intrinsics.checkNotNullParameter(var25, "");
      return new FG(var14, var15, var16, var17, var18, var19, var20, var21, var22, var23, var24, var25);
   }

   @Override
   public final String toString() {
      return "VSGameFrame(newShips="
         + this.a
         + ", deletedShips="
         + this.b
         + ", updatedShips="
         + this.c
         + ", voxelUpdatesMap="
         + this.d
         + ", constraintsCreatedThisTick="
         + this.e
         + ", constraintsUpdatedThisTick="
         + this.k
         + ", constraintsDeletedThisTick="
         + this.l
         + ", playerStates="
         + this.f
         + ", disabledCollisionPairsThisTick="
         + this.g
         + ", enabledCollisionPairsThisTick="
         + this.h
         + ", dimensionGravity="
         + this.i
         + ", dimensionYRange="
         + this.j
         + ")";
   }

   @Override
   public final int hashCode() {
      return (
               (
                        (
                                 (
                                          (
                                                   (
                                                            (
                                                                     (
                                                                              ((this.a.hashCode() * 31 + this.b.hashCode()) * 31 + this.c.hashCode()) * 31
                                                                                 + this.d.hashCode()
                                                                           )
                                                                           * 31
                                                                        + this.e.hashCode()
                                                                  )
                                                                  * 31
                                                               + this.k.hashCode()
                                                         )
                                                         * 31
                                                      + this.l.hashCode()
                                                )
                                                * 31
                                             + this.f.hashCode()
                                       )
                                       * 31
                                    + this.g.hashCode()
                              )
                              * 31
                           + this.h.hashCode()
                     )
                     * 31
                  + this.i.hashCode()
            )
            * 31
         + this.j.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FG var2)) {
         return false;
      } else if (!Intrinsics.areEqual(this.a, var2.a)) {
         return false;
      } else if (!Intrinsics.areEqual(this.b, var2.b)) {
         return false;
      } else if (!Intrinsics.areEqual(this.c, var2.c)) {
         return false;
      } else if (!Intrinsics.areEqual(this.d, var2.d)) {
         return false;
      } else if (!Intrinsics.areEqual(this.e, var2.e)) {
         return false;
      } else if (!Intrinsics.areEqual(this.k, var2.k)) {
         return false;
      } else if (!Intrinsics.areEqual(this.l, var2.l)) {
         return false;
      } else if (!Intrinsics.areEqual(this.f, var2.f)) {
         return false;
      } else if (!Intrinsics.areEqual(this.g, var2.g)) {
         return false;
      } else if (!Intrinsics.areEqual(this.h, var2.h)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.i, var2.i) ? false : Intrinsics.areEqual(this.j, var2.j);
      }
   }
}
