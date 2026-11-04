package org.valkyrienskies.core.impl.shadow;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

public final class Ie implements If {
   public final Ih a;
   public final List<HX> b;
   public final List<Ih> c;
   public final List<Ih> d;

   public Ie(Ih var1, List<HX> var2, List<Ih> var3, List<Ih> var4) {
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var4, "");
      super();
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
   }

   @Override
   public final Ih a() {
      return this.a;
   }

   @Override
   public final List<HX> b() {
      return this.b;
   }

   public final List<Ih> c() {
      return this.c;
   }

   public final List<Ih> d() {
      return this.d;
   }

   @Override
   public final Ig f() {
      return Ig.SOLID_BOXES;
   }

   private Ih e() {
      return this.a;
   }

   private List<HX> g() {
      return this.b;
   }

   private List<Ih> h() {
      return this.c;
   }

   private List<Ih> i() {
      return this.d;
   }

   private static Ie a(Ih var0, List<HX> var1, List<Ih> var2, List<Ih> var3) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      return new Ie(var0, var1, var2, var3);
   }

   // $VF: synthetic method
   private static Ie a(Ie var0, Ih var1, List var2, List var3, List var4, int var5) {
      if ((var5 & 1) != 0) {
         var1 = var0.a;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.b;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.c;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.d;
      }

      List var10 = var4;
      List var9 = var3;
      List var8 = var2;
      Ih var7 = var1;
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var8, "");
      Intrinsics.checkNotNullParameter(var9, "");
      Intrinsics.checkNotNullParameter(var10, "");
      return new Ie(var7, var8, var9, var10);
   }

   @Override
   public final String toString() {
      return "Lod1SolidBoxesCollisionShape(overallBoundingBox="
         + this.a
         + ", collisionPoints="
         + this.b
         + ", solidBoxes="
         + this.c
         + ", negativeBoxes="
         + this.d
         + ')';
   }

   @Override
   public final int hashCode() {
      return ((this.a.hashCode() * 31 + this.b.hashCode()) * 31 + this.c.hashCode()) * 31 + this.d.hashCode();
   }

   @Override
   public final boolean equals(Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof Ie)) {
         return false;
      } else {
         other = (Ie)other;
         if (!Intrinsics.areEqual(this.a, ((Ie)other).a)) {
            return false;
         } else if (!Intrinsics.areEqual(this.b, ((Ie)other).b)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.c, ((Ie)other).c) ? false : Intrinsics.areEqual(this.d, ((Ie)other).d);
         }
      }
   }
}
