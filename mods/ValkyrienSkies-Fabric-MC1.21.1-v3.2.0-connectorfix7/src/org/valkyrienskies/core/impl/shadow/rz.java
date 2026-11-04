package org.valkyrienskies.core.impl.shadow;

public class rz {
   final rB g;
   final rB h;
   public final rw i;

   public rz(rB var1, rB var2, rw var3) {
      this.g = var1;
      this.h = var2;
      this.i = var3;
   }

   public final rB g() {
      return this.g;
   }

   public final rB h() {
      return this.h;
   }

   public final rw i() {
      return this.i;
   }

   private double a(rB var1) {
      rB var10 = this.h;
      var10 = this.g;
      double var2 = this.h.c - this.g.c;
      var10 = this.h;
      var10 = this.g;
      double var4 = this.h.d - this.g.d;
      var10 = this.g;
      double var10000 = (var1.c - this.g.c) * var2;
      var10 = this.g;
      double var6;
      if (!((var6 = (var10000 + (var1.d - this.g.d) * var4) / (var2 * var2 + var4 * var4)) < 0.0) && !(var6 > 1.0)) {
         var10 = this.g;
         double var12 = this.g.c + var6 * var2;
         var10 = this.g;
         double var21 = this.g.d + var6 * var4;
         return new rB(var12, var21).a(var1);
      } else {
         double var8 = this.g.a(var1);
         double var18 = this.h.a(var1);
         return BY.d(var8, var18);
      }
   }
}
