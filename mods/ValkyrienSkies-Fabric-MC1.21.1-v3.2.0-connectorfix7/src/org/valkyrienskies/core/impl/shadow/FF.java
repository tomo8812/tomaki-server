package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Quaterniondc;
import org.joml.Vector3dc;
import org.joml.primitives.AABBic;
import org.valkyrienskies.core.api.ships.ShipPhysicsListener;
import org.valkyrienskies.core.api.ships.WingManagerChanges;
import org.valkyrienskies.core.api.ships.properties.PhysPose;
import org.valkyrienskies.core.impl.game.ships.ShipPhysicsData;

public final class FF {
   private final long n;
   final double a;
   final Em b;
   private final ShipPhysicsData o;
   final boolean c;
   final PhysPose d;
   final boolean e;
   final List<ShipPhysicsListener> f;
   final WingManagerChanges g;
   final int h;
   final Vector3dc i;
   final Quaterniondc j;
   final Vector3dc k;
   final Vector3dc l;
   final AABBic m;

   public FF(
      long var1,
      double var3,
      Em var5,
      ShipPhysicsData var6,
      boolean var7,
      PhysPose var8,
      boolean var9,
      List<? extends ShipPhysicsListener> var10,
      WingManagerChanges var11,
      int var12,
      Vector3dc var13,
      Quaterniondc var14,
      Vector3dc var15,
      Vector3dc var16,
      AABBic var17
   ) {
      Intrinsics.checkNotNullParameter(var5, "");
      Intrinsics.checkNotNullParameter(var6, "");
      Intrinsics.checkNotNullParameter(var10, "");
      Intrinsics.checkNotNullParameter(var13, "");
      Intrinsics.checkNotNullParameter(var14, "");
      Intrinsics.checkNotNullParameter(var15, "");
      Intrinsics.checkNotNullParameter(var16, "");
      super();
      this.n = var1;
      this.a = var3;
      this.b = var5;
      this.o = var6;
      this.c = var7;
      this.d = var8;
      this.e = var9;
      this.f = var10;
      this.g = var11;
      this.h = var12;
      this.i = var13;
      this.j = var14;
      this.k = var15;
      this.l = var16;
      this.m = var17;
   }

   private long n() {
      return this.n;
   }

   public final double a() {
      return this.a;
   }

   public final Em b() {
      return this.b;
   }

   private ShipPhysicsData o() {
      return this.o;
   }

   public final boolean c() {
      return this.c;
   }

   public final PhysPose d() {
      return this.d;
   }

   public final boolean e() {
      return this.e;
   }

   public final List<ShipPhysicsListener> f() {
      return this.f;
   }

   public final WingManagerChanges g() {
      return this.g;
   }

   public final int h() {
      return this.h;
   }

   public final Vector3dc i() {
      return this.i;
   }

   public final Quaterniondc j() {
      return this.j;
   }

   public final Vector3dc k() {
      return this.k;
   }

   public final Vector3dc l() {
      return this.l;
   }

   public final AABBic m() {
      return this.m;
   }

   private long p() {
      return this.n;
   }

   private double q() {
      return this.a;
   }

   private Em r() {
      return this.b;
   }

   private ShipPhysicsData s() {
      return this.o;
   }

   private boolean t() {
      return this.c;
   }

   private PhysPose u() {
      return this.d;
   }

   private boolean v() {
      return this.e;
   }

   private List<ShipPhysicsListener> w() {
      return this.f;
   }

   private WingManagerChanges x() {
      return this.g;
   }

   private int y() {
      return this.h;
   }

   private Vector3dc z() {
      return this.i;
   }

   private Quaterniondc A() {
      return this.j;
   }

   private Vector3dc B() {
      return this.k;
   }

   private Vector3dc C() {
      return this.l;
   }

   private AABBic D() {
      return this.m;
   }

   private static FF a(
      long var0,
      double var2,
      Em var4,
      ShipPhysicsData var5,
      boolean var6,
      PhysPose var7,
      boolean var8,
      List<? extends ShipPhysicsListener> var9,
      WingManagerChanges var10,
      int var11,
      Vector3dc var12,
      Quaterniondc var13,
      Vector3dc var14,
      Vector3dc var15,
      AABBic var16
   ) {
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var5, "");
      Intrinsics.checkNotNullParameter(var9, "");
      Intrinsics.checkNotNullParameter(var12, "");
      Intrinsics.checkNotNullParameter(var13, "");
      Intrinsics.checkNotNullParameter(var14, "");
      Intrinsics.checkNotNullParameter(var15, "");
      return new FF(var0, var2, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
   }

   // $VF: synthetic method
   private static FF a(
      FF var0,
      long var1,
      double var3,
      Em var5,
      ShipPhysicsData var6,
      boolean var7,
      PhysPose var8,
      boolean var9,
      List var10,
      WingManagerChanges var11,
      int var12,
      Vector3dc var13,
      Quaterniondc var14,
      Vector3dc var15,
      Vector3dc var16,
      AABBic var17,
      int var18
   ) {
      if ((var18 & 1) != 0) {
         var1 = var0.n;
      }

      if ((var18 & 2) != 0) {
         var3 = var0.a;
      }

      if ((var18 & 4) != 0) {
         var5 = var0.b;
      }

      if ((var18 & 8) != 0) {
         var6 = var0.o;
      }

      if ((var18 & 16) != 0) {
         var7 = var0.c;
      }

      if ((var18 & 32) != 0) {
         var8 = var0.d;
      }

      if ((var18 & 64) != 0) {
         var9 = var0.e;
      }

      if ((var18 & 128) != 0) {
         var10 = var0.f;
      }

      if ((var18 & 256) != 0) {
         var11 = var0.g;
      }

      if ((var18 & 512) != 0) {
         var12 = var0.h;
      }

      if ((var18 & 1024) != 0) {
         var13 = var0.i;
      }

      if ((var18 & 2048) != 0) {
         var14 = var0.j;
      }

      if ((var18 & 4096) != 0) {
         var15 = var0.k;
      }

      if ((var18 & 8192) != 0) {
         var16 = var0.l;
      }

      if ((var18 & 16384) != 0) {
         var17 = var0.m;
      }

      AABBic var33 = var17;
      Vector3dc var32 = var16;
      Vector3dc var31 = var15;
      Quaterniondc var30 = var14;
      Vector3dc var29 = var13;
      int var28 = var12;
      WingManagerChanges var27 = var11;
      List var26 = var10;
      boolean var4 = var9;
      PhysPose var25 = var8;
      boolean var2 = var7;
      ShipPhysicsData var24 = var6;
      Em var23 = var5;
      double var21 = var3;
      long var19 = var1;
      Intrinsics.checkNotNullParameter(var23, "");
      Intrinsics.checkNotNullParameter(var24, "");
      Intrinsics.checkNotNullParameter(var26, "");
      Intrinsics.checkNotNullParameter(var29, "");
      Intrinsics.checkNotNullParameter(var30, "");
      Intrinsics.checkNotNullParameter(var31, "");
      Intrinsics.checkNotNullParameter(var32, "");
      return new FF(var19, var21, var23, var24, var2, var25, var4, var26, var27, var28, var29, var30, var31, var32, var33);
   }

   @Override
   public final String toString() {
      return "UpdateShipInGameFrameData(uuid="
         + this.n
         + ", collisionShapeScaling="
         + this.a
         + ", inertiaData="
         + this.b
         + ", physicsData="
         + this.o
         + ", isStatic="
         + this.c
         + ", kinematicTarget="
         + this.d
         + ", shipVoxelsFullyLoaded="
         + this.e
         + ", physicsListeners="
         + this.f
         + ", wingManagerChanges="
         + this.g
         + ", shipTeleportId="
         + this.h
         + ", currentShipPos="
         + this.i
         + ", currentShipRot="
         + this.j
         + ", currentShipVel="
         + this.k
         + ", currentShipOmega="
         + this.l
         + ", definedVoxelAABB="
         + this.m
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
                                                                              (
                                                                                       (
                                                                                                (
                                                                                                         (
                                                                                                                  (
                                                                                                                           Long.hashCode(this.n) * 31
                                                                                                                              + Double.hashCode(this.a)
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + this.b.hashCode()
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this.o.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + Boolean.hashCode(this.c)
                                                                                             )
                                                                                             * 31
                                                                                          + (this.d == null ? 0 : this.d.hashCode())
                                                                                    )
                                                                                    * 31
                                                                                 + Boolean.hashCode(this.e)
                                                                           )
                                                                           * 31
                                                                        + this.f.hashCode()
                                                                  )
                                                                  * 31
                                                               + (this.g == null ? 0 : this.g.hashCode())
                                                         )
                                                         * 31
                                                      + Integer.hashCode(this.h)
                                                )
                                                * 31
                                             + this.i.hashCode()
                                       )
                                       * 31
                                    + this.j.hashCode()
                              )
                              * 31
                           + this.k.hashCode()
                     )
                     * 31
                  + this.l.hashCode()
            )
            * 31
         + (this.m == null ? 0 : this.m.hashCode());
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FF var2)) {
         return false;
      } else if (this.n != var2.n) {
         return false;
      } else if (Double.compare(this.a, var2.a) != 0) {
         return false;
      } else if (!Intrinsics.areEqual(this.b, var2.b)) {
         return false;
      } else if (!Intrinsics.areEqual(this.o, var2.o)) {
         return false;
      } else if (this.c != var2.c) {
         return false;
      } else if (!Intrinsics.areEqual(this.d, var2.d)) {
         return false;
      } else if (this.e != var2.e) {
         return false;
      } else if (!Intrinsics.areEqual(this.f, var2.f)) {
         return false;
      } else if (!Intrinsics.areEqual(this.g, var2.g)) {
         return false;
      } else if (this.h != var2.h) {
         return false;
      } else if (!Intrinsics.areEqual(this.i, var2.i)) {
         return false;
      } else if (!Intrinsics.areEqual(this.j, var2.j)) {
         return false;
      } else if (!Intrinsics.areEqual(this.k, var2.k)) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.l, var2.l) ? false : Intrinsics.areEqual(this.m, var2.m);
      }
   }
}
