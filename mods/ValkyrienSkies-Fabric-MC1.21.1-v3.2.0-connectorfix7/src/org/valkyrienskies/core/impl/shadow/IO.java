package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;

public final class IO implements Hh {
   final int b;
   final double c;
   final Vector3dc d;

   public IO(int var1, double var2, Vector3dc var4) {
      Intrinsics.checkNotNullParameter(var4, "");
      super();
      this.b = var1;
      this.c = var2;
      this.d = var4;
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

   private int d() {
      return this.b;
   }

   private double e() {
      return this.c;
   }

   private Vector3dc f() {
      return this.d;
   }

   private static IO a(int var0, double var1, Vector3dc var3) {
      Intrinsics.checkNotNullParameter(var3, "");
      return new IO(var0, var1, var3);
   }

   // $VF: synthetic method
   private static IO a(IO var0, int var1, double var2, Vector3dc var4, int var5) {
      if ((var5 & 1) != 0) {
         var1 = var0.b;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.c;
      }

      if ((var5 & 4) != 0) {
         var4 = var0.d;
      }

      Vector3dc var10 = var4;
      double var7 = var2;
      int var9 = var1;
      Intrinsics.checkNotNullParameter(var10, "");
      return new IO(var9, var7, var10);
   }

   @Override
   public final String toString() {
      return "RayCastResultImpl(hitBodyId=" + this.b + ", distance=" + this.c + ", hitNormal=" + this.d + ')';
   }

   @Override
   public final int hashCode() {
      return (Integer.hashCode(this.b) * 31 + Double.hashCode(this.c)) * 31 + this.d.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof IO)) {
         return false;
      } else {
         other = (IO)other;
         if (this.b != ((IO)other).b) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.c, ((IO)other).c) ? false : Intrinsics.areEqual(this.d, ((IO)other).d);
         }
      }
   }
}
