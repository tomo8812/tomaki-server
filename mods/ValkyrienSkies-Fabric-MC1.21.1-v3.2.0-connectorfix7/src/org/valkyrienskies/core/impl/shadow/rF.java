package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;

public final class rF implements Serializable, rI<rv, rB> {
   private static final long a = 20140129L;
   private final rB[] b;
   private final double c;
   private transient rz[] d;

   public rF(rB[] var1, double var2) {
      boolean var10000;
      label41: {
         this.c = var2;
         rB[] var3 = var1;
         rF var15 = this;
         if (var3.length >= 3) {
            int var4 = 0;

            for (int var5 = 0; var5 < var3.length; var5++) {
               rB var6 = var3[var5 == 0 ? var3.length - 1 : var5 - 1];
               rB var7 = var3[var5];
               rB var8 = var3[var5 == var3.length - 1 ? 0 : var5 + 1];
               var6 = var7.g(var6);
               var7 = var8.g(var7);
               int var17;
               if ((var17 = Cq.a(Ch.a(var6.c, var7.d, -var6.d, var7.c), 0.0, var15.c)) != 0.0) {
                  if (var4 != 0.0 && var17 != var4) {
                     var10000 = false;
                     break label41;
                  }

                  var4 = var17;
               }
            }
         }

         var10000 = true;
      }

      if (!var10000) {
         throw new oV(px.NOT_CONVEX);
      }

      this.b = (rB[])var1.clone();
   }

   private boolean a(rB[] var1) {
      if (var1.length < 3) {
         return true;
      }

      int var2 = 0;

      for (int var3 = 0; var3 < var1.length; var3++) {
         rB var4 = var1[var3 == 0 ? var1.length - 1 : var3 - 1];
         rB var5 = var1[var3];
         rB var6 = var1[var3 == var1.length - 1 ? 0 : var3 + 1];
         var4 = var5.g(var4);
         var5 = var6.g(var5);
         int var12;
         if ((var12 = Cq.a(Ch.a(var4.c, var5.d, -var4.d, var5.c), 0.0, this.c)) != 0.0) {
            if (var2 != 0.0 && var12 != var2) {
               return false;
            }

            var2 = var12;
         }
      }

      return true;
   }

   private rB[] c() {
      return (rB[])this.b.clone();
   }

   private rz[] d() {
      return (rz[])this.e().clone();
   }

   private rz[] e() {
      if (this.d == null) {
         int var1;
         if ((var1 = this.b.length) <= 1) {
            this.d = new rz[0];
         } else if (var1 == 2) {
            this.d = new rz[1];
            rB var8 = this.b[0];
            rB var2 = this.b[1];
            this.d[0] = new rz(var8, var2, new rw(var8, var2, this.c));
         } else {
            this.d = new rz[var1];
            rB var9 = null;
            rB var10 = null;
            int var3 = 0;
            rB[] var4 = this.b;
            int var5 = this.b.length;

            for (int var6 = 0; var6 < var5; var6++) {
               rB var7 = var4[var6];
               if (var10 == null) {
                  var9 = var7;
                  var10 = var7;
               } else {
                  this.d[var3++] = new rz(var10, var7, new rw(var10, var7, this.c));
                  var10 = var7;
               }
            }

            this.d[var3] = new rz(var10, var9, new rw(var10, var9, this.c));
         }
      }

      return this.d;
   }

   @Override
   public final rY<rv> a() {
      if (this.b.length < 3) {
         throw new oT();
      }

      rZ var1 = new rZ();
      rz[] var2;
      rw[] var3 = new rw[(var2 = this.e()).length];

      for (int var4 = 0; var4 < var2.length; var4++) {
         rz var5;
         var3[var4] = (var5 = var2[var4]).i;
      }

      return var1.a(var3);
   }

   // $VF: synthetic method
   @Override
   public final rB[] b() {
      return (rB[])this.b.clone();
   }
}
