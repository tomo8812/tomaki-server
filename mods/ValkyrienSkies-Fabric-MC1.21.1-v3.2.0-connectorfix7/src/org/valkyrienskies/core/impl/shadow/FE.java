package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;
import org.joml.primitives.AABBdc;

public final class FE {
   private final long e;
   private final Hb f;
   final Hg a;
   final Vector3dc b;
   final double c;
   private final AABBdc g;
   final int d;

   public FE(long var1, Hb var3, Hg var4, Vector3dc var5, double var6, AABBdc var8, int var9) {
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var5, "");
      Intrinsics.checkNotNullParameter(var8, "");
      super();
      this.e = var1;
      this.f = var3;
      this.a = var4;
      this.b = var5;
      this.c = var6;
      this.g = var8;
      this.d = var9;
   }

   private long e() {
      return this.e;
   }

   private Hb f() {
      return this.f;
   }

   public final Hg a() {
      return this.a;
   }

   public final Vector3dc b() {
      return this.b;
   }

   public final double c() {
      return this.c;
   }

   private AABBdc g() {
      return this.g;
   }

   public final int d() {
      return this.d;
   }

   private long h() {
      return this.e;
   }

   private Hb i() {
      return this.f;
   }

   private Hg j() {
      return this.a;
   }

   private Vector3dc k() {
      return this.b;
   }

   private double l() {
      return this.c;
   }

   private AABBdc m() {
      return this.g;
   }

   private int n() {
      return this.d;
   }

   private static FE a(long var0, Hb var2, Hg var3, Vector3dc var4, double var5, AABBdc var7, int var8) {
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var7, "");
      return new FE(var0, var2, var3, var4, var5, var7, var8);
   }

   // $VF: synthetic method
   private static FE a(FE var0, long var1, Hb var3, Hg var4, Vector3dc var5, double var6, AABBdc var8, int var9, int var10) {
      if ((var10 & 1) != 0) {
         var1 = var0.e;
      }

      if ((var10 & 2) != 0) {
         var3 = var0.f;
      }

      if ((var10 & 4) != 0) {
         var4 = var0.a;
      }

      if ((var10 & 8) != 0) {
         var5 = var0.b;
      }

      if ((var10 & 16) != 0) {
         var6 = var0.c;
      }

      if ((var10 & 32) != 0) {
         var8 = var0.g;
      }

      if ((var10 & 64) != 0) {
         var9 = var0.d;
      }

      int var21 = var9;
      AABBdc var20 = var8;
      double var16 = var6;
      Vector3dc var2 = var5;
      Hg var19 = var4;
      Hb var18 = var3;
      long var11 = var1;
      Intrinsics.checkNotNullParameter(var18, "");
      Intrinsics.checkNotNullParameter(var19, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var20, "");
      return new FE(var11, var18, var19, var2, var16, var20, var21);
   }

   @Override
   public final String toString() {
      return "ShipInPhysicsFrameData(uuid="
         + this.e
         + ", inertiaData="
         + this.f
         + ", poseVel="
         + this.a
         + ", shipVoxelOffset="
         + this.b
         + ", scaling="
         + this.c
         + ", aabb="
         + this.g
         + ", lastShipTeleportId="
         + this.d
         + ")";
   }

   @Override
   public final int hashCode() {
      return (
               ((((Long.hashCode(this.e) * 31 + this.f.hashCode()) * 31 + this.a.hashCode()) * 31 + this.b.hashCode()) * 31 + Double.hashCode(this.c)) * 31
                  + this.g.hashCode()
            )
            * 31
         + Integer.hashCode(this.d);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof FE var2)) {
         return false;
      } else if (this.e != var2.e) {
         return false;
      } else if (!Intrinsics.areEqual(this.f, var2.f)) {
         return false;
      } else if (!Intrinsics.areEqual(this.a, var2.a)) {
         return false;
      } else if (!Intrinsics.areEqual(this.b, var2.b)) {
         return false;
      } else if (Double.compare(this.c, var2.c) != 0) {
         return false;
      } else {
         return !Intrinsics.areEqual(this.g, var2.g) ? false : this.d == var2.d;
      }
   }
}
