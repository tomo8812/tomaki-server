package org.valkyrienskies.core.impl.shadow;

import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;

public final class HU {
   public final HV a;
   public final Vector3dc b;
   public final Vector3dc c;
   public final float d;
   public final Vector3dc e;

   public HU(HV var1, Vector3dc var2, Vector3dc var3, float var4, Vector3dc var5) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var5, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
   }

   public final HV a() {
      return this.a;
   }

   public final Vector3dc b() {
      return this.b;
   }

   public final Vector3dc c() {
      return this.c;
   }

   public final float d() {
      return this.d;
   }

   public final Vector3dc e() {
      return this.e;
   }

   private HV f() {
      return this.a;
   }

   private Vector3dc g() {
      return this.b;
   }

   private Vector3dc h() {
      return this.c;
   }

   private float i() {
      return this.d;
   }

   private Vector3dc j() {
      return this.e;
   }

   private static HU a(HV var0, Vector3dc var1, Vector3dc var2, float var3, Vector3dc var4) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var4, "");
      return new HU(var0, var1, var2, var3, var4);
   }

   // $VF: synthetic method
   private static HU a(HU var0, HV var1, Vector3dc var2, Vector3dc var3, float var4, Vector3dc var5, int var6) {
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

      Vector3dc var11 = var5;
      float var10 = var4;
      Vector3dc var9 = var3;
      Vector3dc var8 = var2;
      HV var7 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var8, "");
      Intrinsics.checkNotNullParameter(var9, "");
      Intrinsics.checkNotNullParameter(var11, "");
      return new HU(var7, var8, var9, var10, var11);
   }

   @Override
   public final String toString() {
      return "CollisionEventInfo(type=" + this.a + ", point=" + this.b + ", normal=" + this.c + ", separation=" + this.d + ", relativeVelocity=" + this.e + ')';
   }

   @Override
   public final int hashCode() {
      return (((this.a.hashCode() * 31 + this.b.hashCode()) * 31 + this.c.hashCode()) * 31 + Float.hashCode(this.d)) * 31 + this.e.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof HU)) {
         return false;
      } else {
         other = (HU)other;
         if (this.a != ((HU)other).a) {
            return false;
         } else if (!Intrinsics.areEqual(this.b, ((HU)other).b)) {
            return false;
         } else if (!Intrinsics.areEqual(this.c, ((HU)other).c)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.d, ((HU)other).d) ? false : Intrinsics.areEqual(this.e, ((HU)other).e);
         }
      }
   }
}
