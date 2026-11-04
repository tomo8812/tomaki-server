package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.ships.ShipPhysicsListener;
import org.valkyrienskies.core.api.ships.WingManagerChanges;
import org.valkyrienskies.core.api.ships.properties.PhysPose;
import org.valkyrienskies.core.impl.game.ships.ShipPhysicsData;
import org.valkyrienskies.core.internal.physics.VSCollisionShapeData;

public final class FC {
   final long a;
   final String b;
   final VSCollisionShapeData c;
   final double d;
   final Em e;
   private final ShipPhysicsData q;
   final Hg f;
   final boolean g;
   final PhysPose h;
   final List<ShipPhysicsListener> i;
   final WingManagerChanges j;
   final int k;
   final int l;
   final double m;
   final double n;
   final double o;
   final boolean p;

   public FC(
      long var1,
      String var3,
      VSCollisionShapeData var4,
      double var5,
      Em var7,
      ShipPhysicsData var8,
      Hg var9,
      boolean var10,
      PhysPose var11,
      List<? extends ShipPhysicsListener> var12,
      WingManagerChanges var13,
      int var14,
      int var15,
      double var16,
      double var18,
      double var20,
      boolean var22
   ) {
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var7, "");
      Intrinsics.checkNotNullParameter(var8, "");
      Intrinsics.checkNotNullParameter(var9, "");
      Intrinsics.checkNotNullParameter(var12, "");
      super();
      this.a = var1;
      this.b = var3;
      this.c = var4;
      this.d = var5;
      this.e = var7;
      this.q = var8;
      this.f = var9;
      this.g = var10;
      this.h = var11;
      this.i = var12;
      this.j = var13;
      this.k = var14;
      this.l = var15;
      this.m = var16;
      this.n = var18;
      this.o = var20;
      this.p = var22;
   }

   public final long a() {
      return this.a;
   }

   public final String b() {
      return this.b;
   }

   public final VSCollisionShapeData c() {
      return this.c;
   }

   public final double d() {
      return this.d;
   }

   public final Em e() {
      return this.e;
   }

   private ShipPhysicsData q() {
      return this.q;
   }

   public final Hg f() {
      return this.f;
   }

   public final boolean g() {
      return this.g;
   }

   public final PhysPose h() {
      return this.h;
   }

   public final List<ShipPhysicsListener> i() {
      return this.i;
   }

   public final WingManagerChanges j() {
      return this.j;
   }

   public final int k() {
      return this.k;
   }

   public final int l() {
      return this.l;
   }

   public final double m() {
      return this.m;
   }

   public final double n() {
      return this.n;
   }

   public final double o() {
      return this.o;
   }

   public final boolean p() {
      return this.p;
   }

   private long r() {
      return this.a;
   }

   private String s() {
      return this.b;
   }

   private VSCollisionShapeData t() {
      return this.c;
   }

   private double u() {
      return this.d;
   }

   private Em v() {
      return this.e;
   }

   private ShipPhysicsData w() {
      return this.q;
   }

   private Hg x() {
      return this.f;
   }

   private boolean y() {
      return this.g;
   }

   private PhysPose z() {
      return this.h;
   }

   private List<ShipPhysicsListener> A() {
      return this.i;
   }

   private WingManagerChanges B() {
      return this.j;
   }

   private int C() {
      return this.k;
   }

   private int D() {
      return this.l;
   }

   private double E() {
      return this.m;
   }

   private double F() {
      return this.n;
   }

   private double G() {
      return this.o;
   }

   private boolean H() {
      return this.p;
   }

   private static FC a(
      long var0,
      String var2,
      VSCollisionShapeData var3,
      double var4,
      Em var6,
      ShipPhysicsData var7,
      Hg var8,
      boolean var9,
      PhysPose var10,
      List<? extends ShipPhysicsListener> var11,
      WingManagerChanges var12,
      int var13,
      int var14,
      double var15,
      double var17,
      double var19,
      boolean var21
   ) {
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var6, "");
      Intrinsics.checkNotNullParameter(var7, "");
      Intrinsics.checkNotNullParameter(var8, "");
      Intrinsics.checkNotNullParameter(var11, "");
      return new FC(var0, var2, var3, var4, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var17, var19, var21);
   }

   // $VF: synthetic method
   private static FC a(
      FC var0,
      long var1,
      String var3,
      VSCollisionShapeData var4,
      double var5,
      Em var7,
      ShipPhysicsData var8,
      Hg var9,
      boolean var10,
      PhysPose var11,
      List var12,
      WingManagerChanges var13,
      int var14,
      int var15,
      double var16,
      double var18,
      double var20,
      boolean var22,
      int var23
   ) {
      if ((var23 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var23 & 2) != 0) {
         var3 = var0.b;
      }

      if ((var23 & 4) != 0) {
         var4 = var0.c;
      }

      if ((var23 & 8) != 0) {
         var5 = var0.d;
      }

      if ((var23 & 16) != 0) {
         var7 = var0.e;
      }

      if ((var23 & 32) != 0) {
         var8 = var0.q;
      }

      if ((var23 & 64) != 0) {
         var9 = var0.f;
      }

      if ((var23 & 128) != 0) {
         var10 = var0.g;
      }

      if ((var23 & 256) != 0) {
         var11 = var0.h;
      }

      if ((var23 & 512) != 0) {
         var12 = var0.i;
      }

      if ((var23 & 1024) != 0) {
         var13 = var0.j;
      }

      if ((var23 & 2048) != 0) {
         var14 = var0.k;
      }

      if ((var23 & 4096) != 0) {
         var15 = var0.l;
      }

      if ((var23 & 8192) != 0) {
         var16 = var0.m;
      }

      if ((var23 & 16384) != 0) {
         var18 = var0.n;
      }

      if ((var23 & 32768) != 0) {
         var20 = var0.o;
      }

      if ((var23 & 65536) != 0) {
         var22 = var0.p;
      }

      boolean var54 = var22;
      double var43 = var20;
      double var41 = var18;
      double var39 = var16;
      int var53 = var15;
      int var52 = var14;
      WingManagerChanges var51 = var13;
      List var50 = var12;
      PhysPose var6 = var11;
      boolean var49 = var10;
      Hg var48 = var9;
      ShipPhysicsData var47 = var8;
      Em var2 = var7;
      double var28 = var5;
      VSCollisionShapeData var46 = var4;
      String var45 = var3;
      long var24 = var1;
      Intrinsics.checkNotNullParameter(var45, "");
      Intrinsics.checkNotNullParameter(var46, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var47, "");
      Intrinsics.checkNotNullParameter(var48, "");
      Intrinsics.checkNotNullParameter(var50, "");
      return new FC(var24, var45, var46, var28, var2, var47, var48, var49, var6, var50, var51, var52, var53, var39, var41, var43, var54);
   }

   @Override
   public final String toString() {
      return "NewShipInGameFrameData(uuid="
         + this.a
         + ", dimension="
         + this.b
         + ", collisionShapeData="
         + this.c
         + ", collisionShapeScaling="
         + this.d
         + ", inertiaData="
         + this.e
         + ", physicsData="
         + this.q
         + ", poseVel="
         + this.f
         + ", isStatic="
         + this.g
         + ", kinematicTarget="
         + this.h
         + ", physicsListeners="
         + this.i
         + ", wingManagerChanges="
         + this.j
         + ", shipTeleportId="
         + this.k
         + ", collisionMask="
         + this.l
         + ", staticFrictionCoefficient="
         + this.m
         + ", dynamicFrictionCoefficient="
         + this.n
         + ", restitutionCoefficient="
         + this.o
         + ", isGround="
         + this.p
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
                                                                                                                           (
                                                                                                                                    (
                                                                                                                                             Long.hashCode(
                                                                                                                                                      this.a
                                                                                                                                                   )
                                                                                                                                                   * 31
                                                                                                                                                + this.b
                                                                                                                                                   .hashCode()
                                                                                                                                          )
                                                                                                                                          * 31
                                                                                                                                       + this.c.hashCode()
                                                                                                                                 )
                                                                                                                                 * 31
                                                                                                                              + Double.hashCode(this.d)
                                                                                                                        )
                                                                                                                        * 31
                                                                                                                     + this.e.hashCode()
                                                                                                               )
                                                                                                               * 31
                                                                                                            + this.q.hashCode()
                                                                                                      )
                                                                                                      * 31
                                                                                                   + this.f.hashCode()
                                                                                             )
                                                                                             * 31
                                                                                          + Boolean.hashCode(this.g)
                                                                                    )
                                                                                    * 31
                                                                                 + (this.h == null ? 0 : this.h.hashCode())
                                                                           )
                                                                           * 31
                                                                        + this.i.hashCode()
                                                                  )
                                                                  * 31
                                                               + (this.j == null ? 0 : this.j.hashCode())
                                                         )
                                                         * 31
                                                      + Integer.hashCode(this.k)
                                                )
                                                * 31
                                             + Integer.hashCode(this.l)
                                       )
                                       * 31
                                    + Double.hashCode(this.m)
                              )
                              * 31
                           + Double.hashCode(this.n)
                     )
                     * 31
                  + Double.hashCode(this.o)
            )
            * 31
         + Boolean.hashCode(this.p);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FC var2)) {
         return false;
      } else if (this.a != var2.a) {
         return false;
      } else if (!Intrinsics.areEqual(this.b, var2.b)) {
         return false;
      } else if (!Intrinsics.areEqual(this.c, var2.c)) {
         return false;
      } else if (Double.compare(this.d, var2.d) != 0) {
         return false;
      } else if (!Intrinsics.areEqual(this.e, var2.e)) {
         return false;
      } else if (!Intrinsics.areEqual(this.q, var2.q)) {
         return false;
      } else if (!Intrinsics.areEqual(this.f, var2.f)) {
         return false;
      } else if (this.g != var2.g) {
         return false;
      } else if (!Intrinsics.areEqual(this.h, var2.h)) {
         return false;
      } else if (!Intrinsics.areEqual(this.i, var2.i)) {
         return false;
      } else if (!Intrinsics.areEqual(this.j, var2.j)) {
         return false;
      } else if (this.k != var2.k) {
         return false;
      } else if (this.l != var2.l) {
         return false;
      } else if (Double.compare(this.m, var2.m) != 0) {
         return false;
      } else if (Double.compare(this.n, var2.n) != 0) {
         return false;
      } else {
         return Double.compare(this.o, var2.o) != 0 ? false : this.p == var2.p;
      }
   }
}
