package org.valkyrienskies.core.impl.shadow;

import java.util.HashMap;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;
import org.joml.Vector3dc;

public final class Hu implements GX {
   private final Map<Integer, GX.a> a = new HashMap<>();

   @Override
   public final Map<Integer, GX.a> a() {
      return this.a;
   }

   @Override
   public final GX.a a(int var1, GW var2, Hg var3, double var4, Vector3dc var6, Ha var7) {
      Intrinsics.checkNotNullParameter(var2, "");
      Intrinsics.checkNotNullParameter(var3, "");
      Intrinsics.checkNotNullParameter(var6, "");
      Hu.a var9 = new Hu.a(var2, var3, var4, var6, var7);
      Map var10 = this.a;
      Integer var8 = var1;
      var10.put(var8, var9);
      return var9;
   }

   @Override
   public final boolean a(int var1) {
      return this.a.remove(var1) != null;
   }

   @Override
   public final void close() {
   }

   static final class a implements GX.a {
      private GW a;
      private Hg b;
      private double c;
      private Vector3dc d;
      private Ha e;

      public a(GW var1, Hg var2, double var3, Vector3dc var5, Ha var6) {
         Intrinsics.checkNotNullParameter(var1, "");
         Intrinsics.checkNotNullParameter(var2, "");
         Intrinsics.checkNotNullParameter(var5, "");
         super();
         this.a = var1;
         this.b = var2;
         this.c = var3;
         this.d = var5;
         this.e = var6;
      }

      @Override
      public final GW a() {
         return this.a;
      }

      @Override
      public final void a(GW var1) {
         Intrinsics.checkNotNullParameter(var1, "");
         this.a = var1;
      }

      @Override
      public final Hg b() {
         return this.b;
      }

      @Override
      public final void a(Hg var1) {
         Intrinsics.checkNotNullParameter(var1, "");
         this.b = var1;
      }

      @Override
      public final double c() {
         return this.c;
      }

      @Override
      public final void a(double var1) {
         this.c = var1;
      }

      @Override
      public final Vector3dc d() {
         return this.d;
      }

      @Override
      public final void a(Vector3dc var1) {
         Intrinsics.checkNotNullParameter(var1, "");
         this.d = var1;
      }

      @Override
      public final Ha e() {
         return this.e;
      }

      @Override
      public final void a(Ha var1) {
         this.e = var1;
      }

      private GW f() {
         return this.a;
      }

      private Hg g() {
         return this.b;
      }

      private double h() {
         return this.c;
      }

      private Vector3dc i() {
         return this.d;
      }

      private Ha j() {
         return this.e;
      }

      private static Hu.a a(GW var0, Hg var1, double var2, Vector3dc var4, Ha var5) {
         Intrinsics.checkNotNullParameter(var0, "");
         Intrinsics.checkNotNullParameter(var1, "");
         Intrinsics.checkNotNullParameter(var4, "");
         return new Hu.a(var0, var1, var2, var4, var5);
      }

      // $VF: synthetic method
      private static Hu.a a(Hu.a var0, GW var1, Hg var2, double var3, Vector3dc var5, Ha var6, int var7) {
         if ((var7 & 1) != 0) {
            var1 = var0.a;
         }

         if ((var7 & 2) != 0) {
            var2 = var0.b;
         }

         if ((var7 & 4) != 0) {
            var3 = var0.c;
         }

         if ((var7 & 8) != 0) {
            var5 = var0.d;
         }

         if ((var7 & 16) != 0) {
            var6 = var0.e;
         }

         Ha var15 = var6;
         Vector3dc var14 = var5;
         double var10 = var3;
         Hg var13 = var2;
         GW var12 = var1;
         Intrinsics.checkNotNullParameter(var1, "");
         Intrinsics.checkNotNullParameter(var13, "");
         Intrinsics.checkNotNullParameter(var14, "");
         return new Hu.a(var12, var13, var10, var14, var15);
      }

      @Override
      public final String toString() {
         return "DummyCompoundShapeSegmentReference(collisionShape="
            + this.a
            + ", poseVel="
            + this.b
            + ", collisionShapeScaling="
            + this.c
            + ", collisionShapeOffset="
            + this.d
            + ", nextPoseVelFunction="
            + this.e
            + ')';
      }

      @Override
      public final int hashCode() {
         return (((this.a.hashCode() * 31 + this.b.hashCode()) * 31 + Double.hashCode(this.c)) * 31 + this.d.hashCode()) * 31
            + (this.e == null ? 0 : this.e.hashCode());
      }

      @Override
      public final boolean equals(Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof Hu.a)) {
            return false;
         } else {
            other = (Hu.a)other;
            if (!Intrinsics.areEqual(this.a, ((Hu.a)other).a)) {
               return false;
            } else if (!Intrinsics.areEqual(this.b, ((Hu.a)other).b)) {
               return false;
            } else if (!Intrinsics.areEqual(this.c, ((Hu.a)other).c)) {
               return false;
            } else {
               return !Intrinsics.areEqual(this.d, ((Hu.a)other).d) ? false : Intrinsics.areEqual(this.e, ((Hu.a)other).e);
            }
         }
      }
   }
}
