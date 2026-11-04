package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;

public final class Id {
   public final If a;
   public final float b;
   public final float c;
   public final float d;
   public final int e;

   public Id(If var1, float var2, float var3, float var4, int var5) {
      Intrinsics.checkNotNullParameter(var1, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
   }

   public final If a() {
      return this.a;
   }

   public final float b() {
      return this.b;
   }

   public final float c() {
      return this.c;
   }

   public final float d() {
      return this.d;
   }

   public final int e() {
      return this.e;
   }

   private If f() {
      return this.a;
   }

   private float g() {
      return this.b;
   }

   private float h() {
      return this.c;
   }

   private float i() {
      return this.d;
   }

   private int j() {
      return this.e;
   }

   private static Id a(If var0, float var1, float var2, float var3, int var4) {
      Intrinsics.checkNotNullParameter(var0, "");
      return new Id(var0, var1, var2, var3, var4);
   }

   // $VF: synthetic method
   private static Id a(Id var0, If var1, float var2, float var3, float var4, int var5, int var6) {
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
      float var10 = var4;
      float var9 = var3;
      float var8 = var2;
      If var7 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      return new Id(var7, var8, var9, var10, var11);
   }

   @Override
   public final String toString() {
      return "Lod1SolidBlockState(collisionShape="
         + this.a
         + ", elasticity="
         + this.b
         + ", friction="
         + this.c
         + ", hardness="
         + this.d
         + ", lod1SolidBlockStateId="
         + this.e
         + ')';
   }

   @Override
   public final int hashCode() {
      return (((this.a.hashCode() * 31 + Float.hashCode(this.b)) * 31 + Float.hashCode(this.c)) * 31 + Float.hashCode(this.d)) * 31 + Integer.hashCode(this.e);
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Id)) {
         return false;
      } else {
         other = (Id)other;
         if (!Intrinsics.areEqual(this.a, ((Id)other).a)) {
            return false;
         } else if (!Intrinsics.areEqual(this.b, ((Id)other).b)) {
            return false;
         } else if (!Intrinsics.areEqual(this.c, ((Id)other).c)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.d, ((Id)other).d) ? false : this.e == ((Id)other).e;
         }
      }
   }
}
