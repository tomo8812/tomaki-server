package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3f;

public final class Ib {
   public final Ih a;
   public final float b;
   public final float c;
   public final Vector3f d;
   public final int e;

   public Ib(Ih var1, float var2, float var3, Vector3f var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var4, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
   }

   public final Ih a() {
      return this.a;
   }

   public final float b() {
      return this.b;
   }

   public final float c() {
      return this.c;
   }

   public final Vector3f d() {
      return this.d;
   }

   public final int e() {
      return this.e;
   }

   private Ih f() {
      return this.a;
   }

   private float g() {
      return this.b;
   }

   private float h() {
      return this.c;
   }

   private Vector3f i() {
      return this.d;
   }

   private int j() {
      return this.e;
   }

   private static Ib a(Ih var0, float var1, float var2, Vector3f var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var3, "");
      return new Ib(var0, var1, var2, var3, var4);
   }

   // $VF: synthetic method
   private static Ib a(Ib var0, Ih var1, float var2, float var3, Vector3f var4, int var5, int var6) {
      if ((var6 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var6 & 2) != 0) {
         var2 = var0.b;
      }

      if ((var6 & 4) != 0) {
         var3 = var0.c;
      }

      if ((var6 & 8) != 0) {
         var4 = var0.d;
      }

      if ((var6 & 16) != 0) {
         var5 = var0.e;
      }

      int var11 = var5;
      Vector3f var10 = var4;
      float var9 = var3;
      float var8 = var2;
      Ih var7 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var10, "");
      return new Ib(var7, var8, var9, var10, var11);
   }

   @Override
   public final String toString() {
      return "Lod1LiquidBlockState(boundingBox="
         + this.a
         + ", density="
         + this.b
         + ", dragCoefficient="
         + this.c
         + ", fluidVel="
         + this.d
         + ", lod1LiquidBlockStateId="
         + this.e
         + ')';
   }

   @Override
   public final int hashCode() {
      return (((this.a.hashCode() * 31 + Float.hashCode(this.b)) * 31 + Float.hashCode(this.c)) * 31 + this.d.hashCode()) * 31 + Integer.hashCode(this.e);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Ib)) {
         return false;
      } else {
         other = (Ib)other;
         if (!Intrinsics.areEqual(this.a, ((Ib)other).a)) {
            return false;
         } else if (!Intrinsics.areEqual(this.b, ((Ib)other).b)) {
            return false;
         } else if (!Intrinsics.areEqual(this.c, ((Ib)other).c)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.d, ((Ib)other).d) ? false : this.e == ((Ib)other).e;
         }
      }
   }
}
