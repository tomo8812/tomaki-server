package org.valkyrienskies.core.impl.shadow;

import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.Triple;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.valkyrienskies.core.api.util.AerodynamicUtils;

public final class DL implements AerodynamicUtils {
   private final DL.a b = new DL.a(962.0, 62.0, 0.0, 4, null);
   public final ConcurrentHashMap<String, DL.a> a = new ConcurrentHashMap<>();

   private DL.a b() {
      return this.b
   }

   public final ConcurrentHashMap<String, DL.a> a() {
      return this.a
   }

   @Override
   public final void registerDimension(String dimensionId, double maxY, double seaLevel, double gravity) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      this.a.put(dimensionId, new DL.a(maxY, seaLevel, gravity))
   }

   @Override
   public final void updateGravityForDimension(String dimensionId, double gravity) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      DL.a var10000 = this.a.get(dimensionId)
      if (var10000 != null) {
         double var6 = gravity
         var10000.c = var6
      }
   }

   @Override
   public final void updateAerodynamicInfoForDimension(String dimensionId, Double maxY, Double seaLevel) {
      Intrinsics.checkNotNullParameter(dimensionId, "")
      DL.a var10000 = this.a.get(dimensionId)
      if (var10000 != null) {
         DL.a var4 = var10000
         DL.a var5 = new DL.a(maxY != null ? maxY : var4.a, seaLevel != null ? seaLevel : var4.b, var4.c)
         this.a.put(dimensionId, var5)
      }
   }

   @Override
   public final Triple<Double, Double, Double> getAtmosphereForDimension(String id) {
      Intrinsics.checkNotNullParameter(id, "")
      DL.a var10000 = this.a.get(id)
      return var10000 != null ? var10000.d() : this.b.d()
   }

   @Override
   public final double getAirDensityForY(double y, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      Triple var27
      double var5 = ((Number)(var27 = this.getAtmosphereForDimension(dimension)).component1()).doubleValue()
      double var7 = ((Number)var27.component2()).doubleValue()
      double var9 = ((Number)var27.component3()).doubleValue()
      if (var5 <= 0.0) {
         return 0.0
      }

      double var11 = 71000.0 / (var5 - var7)
      double var13
      int var26
      if ((
            var26 = (var13 = (y - var7) * var11 >= 0.0 ? (y - var7) * var11 : 0.0) < 11000.0
               ? 0
               : (
                  var13 < 20000.0
                     ? 1
                     : (var13 < 32000.0 ? 2 : (var13 < 47000.0 ? 3 : (var13 < 51000.0 ? 4 : (var13 < 71000.0 ? 5 : (var13 < 84852.0 ? 6 : 7)))))
               )
         )
         > 6) {
         return 0.0
      }

      double var16 = switch (var26) {
         case 0 -> 0.0;
         case 1 -> 11000.0;
         case 2 -> 20000.0;
         case 3 -> 32000.0;
         case 4 -> 47000.0;
         case 5 -> 51000.0;
         case 6 -> 71000.0;
         default -> 0.0;
      }

      double var18 = switch (var26) {
         case 0 -> 1.225;
         case 1 -> 0.36391;
         case 2 -> 0.08803;
         case 3 -> 0.01322;
         case 4 -> 0.00143;
         case 5 -> 8.6E-4;
         case 6 -> 6.4E-5;
         default -> 0.0;
      }

      double var20 = switch (var26) {
         case 0 -> 288.15;
         case 1 -> 216.65;
         case 2 -> 216.65;
         case 3 -> 228.65;
         case 4 -> 270.65;
         case 5 -> 270.65;
         case 6 -> 214.65;
         default -> 0.0;
      }
      double var22 = var9

      double var24
      return (var24 = switch (var26) {
            case 0 -> 0.0065;
            case 1 -> 0.0;
            case 2 -> -0.001;
            case 3 -> -0.0028;
            case 4 -> 0.0;
            case 5 -> 0.0028;
            case 6 -> 0.002;
            default -> 0.0;
         }) != 0.0
         ? var18 * Math.pow((var20 - (var13 - var16) * var24) / var20, var22 * 0.0289644 / (var24 * 8.314) - 1.0)
         : var18 * Math.exp(-var22 * 0.0289644 * (var13 - var16) / (var20 * 8.314))
      }

   @Override
   public final double getAirPressureForY(double y, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      Triple var27
      double var5 = ((Number)(var27 = this.getAtmosphereForDimension(dimension)).component1()).doubleValue()
      double var7 = ((Number)var27.component2()).doubleValue()
      double var9 = ((Number)var27.component3()).doubleValue()
      if (var5 <= 0.0) {
         return 0.0
      }

      double var11 = 71000.0 / (var5 - var7)
      double var13
      int var26
      if ((
            var26 = (var13 = (y - var7) * var11 >= 0.0 ? (y - var7) * var11 : 0.0) < 11000.0
               ? 0
               : (
                  var13 < 20000.0
                     ? 1
                     : (var13 < 32000.0 ? 2 : (var13 < 47000.0 ? 3 : (var13 < 51000.0 ? 4 : (var13 < 71000.0 ? 5 : (var13 < 84852.0 ? 6 : 7)))))
               )
         )
         > 6) {
         return 0.0
      }

      double var16 = switch (var26) {
         case 0 -> 0.0;
         case 1 -> 11000.0;
         case 2 -> 20000.0;
         case 3 -> 32000.0;
         case 4 -> 47000.0;
         case 5 -> 51000.0;
         case 6 -> 71000.0;
         default -> 0.0;
      }

      double var18 = switch (var26) {
         case 0 -> 101325.0;
         case 1 -> 22632.1;
         case 2 -> 5474.89;
         case 3 -> 868.02;
         case 4 -> 110.91;
         case 5 -> 66.94;
         case 6 -> 3.96;
         default -> 0.0;
      }

      double var20 = switch (var26) {
         case 0 -> 288.15;
         case 1 -> 216.65;
         case 2 -> 216.65;
         case 3 -> 228.65;
         case 4 -> 270.65;
         case 5 -> 270.65;
         case 6 -> 214.65;
         default -> 0.0;
      }
      double var22 = var9

      double var24
      return (var24 = switch (var26) {
            case 0 -> 0.0065;
            case 1 -> 0.0;
            case 2 -> -0.001;
            case 3 -> -0.0028;
            case 4 -> 0.0;
            case 5 -> 0.0028;
            case 6 -> 0.002;
            default -> 0.0;
         }) != 0.0
         ? var18 * Math.pow(1.0 - var24 / var20 * (var13 - var16), var22 * 0.0289644 / (var24 * 8.314))
         : var18 * Math.exp(-var22 * 0.0289644 * (var13 - var16) / (var20 * 8.314))
      }

   @Override
   public final double getAirTemperatureForY(double y, String dimension) {
      Intrinsics.checkNotNullParameter(dimension, "")
      Triple var21
      double var5 = ((Number)(var21 = this.getAtmosphereForDimension(dimension)).component1()).doubleValue()
      double var7 = ((Number)var21.component2()).doubleValue()
      if (var5 <= 0.0) {
         return 0.0
      }

      double var9 = 71000.0 / (var5 - var7)
      double var11
      int var20
      if ((
            var20 = (var11 = (y - var7) * var9 >= 0.0 ? (y - var7) * var9 : 0.0) < 11000.0
               ? 0
               : (
                  var11 < 20000.0
                     ? 1
                     : (var11 < 32000.0 ? 2 : (var11 < 47000.0 ? 3 : (var11 < 51000.0 ? 4 : (var11 < 71000.0 ? 5 : (var11 < 84852.0 ? 6 : 7)))))
               )
         )
         > 6) {
         return 0.0
      }

      double var14 = switch (var20) {
         case 0 -> 0.0;
         case 1 -> 11000.0;
         case 2 -> 20000.0;
         case 3 -> 32000.0;
         case 4 -> 47000.0;
         case 5 -> 51000.0;
         case 6 -> 71000.0;
         default -> 0.0;
      }

      double var16 = switch (var20) {
         case 0 -> 288.15;
         case 1 -> 216.65;
         case 2 -> 216.65;
         case 3 -> 228.65;
         case 4 -> 270.65;
         case 5 -> 270.65;
         case 6 -> 214.65;
         default -> 0.0;
      }

      double var18
      return (var18 = switch (var20) {
         case 0 -> 0.0065;
         case 1 -> 0.0;
         case 2 -> -0.001;
         case 3 -> -0.0028;
         case 4 -> 0.0;
         case 5 -> 0.0028;
         case 6 -> 0.002;
         default -> 0.0;
      }) != 0.0 ? var16 - (var11 - var14) * var18 : var16
   }

   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0002¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\b\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\n\u0010\tJ\u0010\u0010\u000b\u001a\u00020\u0002HÆ\u0003¢\u0006\u0004\b\u000b\u0010\tJ.\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\b\b\u0002\u0010\u0004\u001a\u00020\u00022\b\b\u0002\u0010\u0005\u001a\u00020\u0002HÆ\u0001¢\u0006\u0004\b\f\u0010\rJ\u001a\u0010\u000f\u001a\u00020\u000e2\b\u0010\u0003\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0012\u001a\u00020\u0011HÖ\u0001¢\u0006\u0004\b\u0012\u0010\u0013J\u0010\u0010\u0015\u001a\u00020\u0014HÖ\u0001¢\u0006\u0004\b\u0015\u0010\u0016J\u001f\u0010\u0018\u001a\u0014\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00020\u0017¢\u0006\u0004\b\u0018\u0010\u0019R\"\u0010\f\u001a\u00020\u00028\u0007@\u0007X\u0086\u000e¢\u0006\u0012\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u001a\u0010\t\"\u0004\b\f\u0010\u001cR\u0017\u0010\u001d\u001a\u00020\u00028\u0007¢\u0006\f\n\u0004\b\f\u0010\u001b\u001a\u0004\b\f\u0010\tR\u0017\u0010\u001a\u001a\u00020\u00028\u0007¢\u0006\f\n\u0004\b\u001d\u0010\u001b\u001a\u0004\b\u001d\u0010\t",
      d2 = {
            "Lorg/valkyrienskies/core/impl/shadow/DL$a;",
            "",
            "",
            "p0",
            "p1",
            "p2",
            "<init>",
            "(DDD)V",
            "e",
            "()D",
            "f",
            "g",
            "a",
            "(DDD)Lorg/valkyrienskies/core/impl/shadow/DL$a;",
            "",
            "equals",
            "(Ljava/lang/Object;)Z",
            "",
            "hashCode",
            "()I",
            "",
            "toString",
            "()Ljava/lang/String;",
            "Lkotlin/Triple;",
            "d",
            "()Lkotlin/Triple;",
            "c",
            "D",
            "(D)V",
            "b"
      }
   )
   public static final class a {
      final double a;
      final double b;
      double c;

      public a(double var1, double var3, double var5) {
         this.a = var1
         this.b = var3
         this.c = var5
      }

      // $VF: synthetic method
      public a(double var1, double var3, double var5, int var7, DefaultConstructorMarker var8) {
         if ((var7 & 4) != 0) {
            var5 = 10.0
         }

         this(var1, var3, var5)
      }

      @JvmName(name = "a")
      public final double a() {
         return this.a
      }

      @JvmName(name = "b")
      public final double b() {
         return this.b
      }

      @JvmName(name = "c")
      public final double c() {
         return this.c
      }

      @JvmName(name = "a")
      public final void a(double var1) {
         this.c = var1
      }

      public final Triple<Double, Double, Double> d() {
         return new Triple(this.a, this.b, this.c)
      }

      private double e() {
         return this.a
      }

      private double f() {
         return this.b
      }

      private double g() {
         return this.c
      }

      private static DL.a a(double var0, double var2, double var4) {
         return new DL.a(var0, var2, var4)
      }

      // $VF: synthetic method
      private static DL.a a$default(DL.a var0, double var1, double var3, double var5, int var7) {
         if ((var7 & 1) != 0) {
            var1 = var0.a
         }

         if ((var7 & 2) != 0) {
            var3 = var0.b
         }

         if ((var7 & 4) != 0) {
            var5 = var0.c
         }

         double var12 = var5
         double var10 = var3
         double var8 = var1
         return new DL.a(var8, var10, var12)
      }

      @Override
      public final String toString() {
         return "Parameters(maxY=" + this.a + ", seaLevel=" + this.b + ", gravity=" + this.c + ")"
      }

      @Override
      public final int hashCode() {
         return (Double.hashCode(this.a) * 31 + Double.hashCode(this.b)) * 31 + Double.hashCode(this.c)
      }

      @Override
      public final boolean equals(Object other) {
         if (this == other) {
            return true
         } else if (!(other instanceof DL.a var2)) {
            return false
         } else if (Double.compare(this.a, var2.a) != 0) {
            return false
         } else {
            return Double.compare(this.b, var2.b) != 0 ? false : Double.compare(this.c, var2.c) == 0
         }
      }
   }
}
