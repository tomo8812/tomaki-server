package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;

public final class IP implements Hi {
   final int b;
   final double c;
   final Vector3dc d;
   final Vector3dc e;
   final Vector3dc f;

   public IP(int var1, double var2, Vector3dc var4, Vector3dc var5, Vector3dc var6) {
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var5, "");
      Intrinsics.checkNotNullParameter(var6, "");
      super();
      this.b = var1;
      this.c = var2;
      this.d = var4;
      this.e = var5;
      this.f = var6;
   }

   @Override
   public final int a() {
      return this.b;
   }

   @Override
   public final double b() {
      return this.c;
   }

   @Override
   public final Vector3dc c() {
      return this.d;
   }

   @Override
   public final Vector3dc d() {
      return this.e;
   }

   @Override
   public final Vector3dc e() {
      return this.f;
   }

   private int f() {
      return this.b;
   }

   private double g() {
      return this.c;
   }

   private Vector3dc h() {
      return this.d;
   }

   private Vector3dc i() {
      return this.e;
   }

   private Vector3dc j() {
      return this.f;
   }

   private static IP a(int var0, double var1, Vector3dc var3, Vector3dc var4, Vector3dc var5) {
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var5, "");
      return new IP(var0, var1, var3, var4, var5);
   }

   // $VF: synthetic method
   private static IP a(IP var0, int var1, double var2, Vector3dc var4, Vector3dc var5, Vector3dc var6, int var7) {
      if ((var7 & 1) != 0) {
         var1 = var0.b;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.c;
      }

      if ((var7 & 4) != 0) {
         var4 = var0.d;
      }

      if ((var7 & 8) != 0) {
         var5 = var0.e;
      }

      if ((var7 & 16) != 0) {
         var6 = var0.f;
      }

      Vector3dc var3 = var6;
      Vector3dc var13 = var5;
      Vector3dc var12 = var4;
      double var9 = var2;
      int var11 = var1;
      Intrinsics.checkNotNullParameter(var12, "");
      Intrinsics.checkNotNullParameter(var13, "");
      Intrinsics.checkNotNullParameter(var3, "");
      return new IP(var11, var9, var12, var13, var3);
   }

   @Override
   public final String toString() {
      return "RayCastResultWithVelocityImpl(hitBodyId="
         + this.b
         + ", distance="
         + this.c
         + ", hitNormal="
         + this.d
         + ", velocity="
         + this.e
         + ", angularVelocity="
         + this.f
         + ')';
   }

   @Override
   public final int hashCode() {
      return (((Integer.hashCode(this.b) * 31 + Double.hashCode(this.c)) * 31 + this.d.hashCode()) * 31 + this.e.hashCode()) * 31 + this.f.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof IP)) {
         return false;
      } else {
         other = (IP)other;
         if (this.b != ((IP)other).b) {
            return false;
         } else if (!Intrinsics.areEqual(this.c, ((IP)other).c)) {
            return false;
         } else if (!Intrinsics.areEqual(this.d, ((IP)other).d)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.e, ((IP)other).e) ? false : Intrinsics.areEqual(this.f, ((IP)other).f);
         }
      }
   }
}
