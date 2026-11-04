package org.valkyrienskies.core.impl.shadow;

import java.util.List;

public final class sm {
   final st a;
   st b;
   final double c;
   final sl d;

   sm(st var1, st var2, double var3, sl var5) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var5;
      sm var7 = this;
      st var6 = var1;
      var1.c = var7;
      var6.a(var7.d);
      var2.a(this);
   }

   public final st a() {
      return this.a;
   }

   public final st b() {
      return this.b;
   }

   public final double c() {
      return this.c;
   }

   public final sl d() {
      return this.d;
   }

   private rs a(double var1) {
      st var3 = this.a;
      sp var4 = this.a.a;
      return this.d.a(var1 + this.d.a(this.a.a.c));
   }

   final void a(sm var1) {
      this.b = var1.a;
      this.b.a(this);
      this.b.a(this.d);
   }

   final void a(sl var1, List<sm> var2, List<sm> var3) {
      st var13 = this.a;
      sp var21 = this.a.a;
      double var4 = this.d.a(this.a.a.c);
      sf var6;
      double var7;
      double var9;
      double var11 = (var9 = (var7 = Ci.b((var6 = this.d.a(var1)).a, var4 + Math.PI) - var4) + var6.c()) - (Math.PI * 2);
      sl var22 = this.d;
      double var23 = this.d.d;
      st var18 = this.a;
      if (var11 >= this.c - var23) {
         var3.add(this);
      } else {
         double var16 = 0.0;
         if (var11 >= 0.0) {
            var18 = this.a(var18, new st(new sp(this.d.a(var4 + var11))), var11, var3, var1);
            var16 = var11;
         }

         if (var7 >= this.c - var23) {
            if (var11 >= 0.0) {
               this.a(var18, this.b, this.c - var16, var2, var1);
            } else {
               var2.add(this);
            }
         } else {
            st var19 = this.a(var18, new st(new sp(this.d.a(var4 + var7))), var7 - var16, var2, var1);
            var16 = var7;
            if (var9 >= this.c - var23) {
               this.a(var19, this.b, this.c - var16, var3, var1);
            } else {
               st var20 = this.a(var19, new st(new sp(this.d.a(var4 + var7))), var7 - var16, var3, var1);
               var16 = var7;
               this.a(var20, this.b, this.c - var16, var2, var1);
            }
         }
      }
   }

   private st a(st var1, st var2, double var3, List<sm> var5, sl var6) {
      sl var7 = this.d;
      if (var3 <= this.d.d) {
         return var1;
      }

      var2.a(var6);
      sm var8 = new sm(var1, var2, var3, this.d);
      var5.add(var8);
      return var2;
   }
}
