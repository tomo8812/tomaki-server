package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3fc;

public final class Ic implements If {
   public final Ih a;
   public final List<HX> b;
   public final Ih c;
   public final HZ d;
   public final Vector3fc e;

   public Ic(Ih var1, List<HX> var2, Ih var3, HZ var4, Vector3fc var5) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      Intrinsics.checkNotNullParameter(var5, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
      this.e = var5;
   }

   @Override
   public final Ih a() {
      return this.a;
   }

   @Override
   public final List<HX> b() {
      return this.b;
   }

   public final Ih c() {
      return this.c;
   }

   public final HZ d() {
      return this.d;
   }

   public final Vector3fc e() {
      return this.e;
   }

   @Override
   public final Ig f() {
      return Ig.BELT;
   }

   private Ih g() {
      return this.a;
   }

   private List<HX> h() {
      return this.b;
   }

   private Ih i() {
      return this.c;
   }

   private HZ j() {
      return this.d;
   }

   private Vector3fc k() {
      return this.e;
   }

   private static Ic a(Ih var0, List<HX> var1, Ih var2, HZ var3, Vector3fc var4) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      return new Ic(var0, var1, var2, var3, var4);
   }

   // $VF: synthetic method
   private static Ic a(Ic var0, Ih var1, List var2, Ih var3, HZ var4, Vector3fc var5, int var6) {
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

      Vector3fc var12 = var5;
      HZ var11 = var4;
      Ih var10 = var3;
      List var9 = var2;
      Ih var8 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var9, "");
      Intrinsics.checkNotNullParameter(var10, "");
      Intrinsics.checkNotNullParameter(var11, "");
      Intrinsics.checkNotNullParameter(var12, "");
      return new Ic(var8, var9, var10, var11, var12);
   }

   @Override
   public final String toString() {
      return "Lod1SolidBeltCollisionShape(overallBoundingBox="
         + this.a
         + ", collisionPoints="
         + this.b
         + ", localBoundingBox="
         + this.c
         + ", beltType="
         + this.d
         + ", topVel="
         + this.e
         + ')';
   }

   @Override
   public final int hashCode() {
      return (((this.a.hashCode() * 31 + this.b.hashCode()) * 31 + this.c.hashCode()) * 31 + this.d.hashCode()) * 31 + this.e.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Ic)) {
         return false;
      } else {
         other = (Ic)other;
         if (!Intrinsics.areEqual(this.a, ((Ic)other).a)) {
            return false;
         } else if (!Intrinsics.areEqual(this.b, ((Ic)other).b)) {
            return false;
         } else if (!Intrinsics.areEqual(this.c, ((Ic)other).c)) {
            return false;
         } else {
            return this.d != ((Ic)other).d ? false : Intrinsics.areEqual(this.e, ((Ic)other).e);
         }
      }
   }
}
