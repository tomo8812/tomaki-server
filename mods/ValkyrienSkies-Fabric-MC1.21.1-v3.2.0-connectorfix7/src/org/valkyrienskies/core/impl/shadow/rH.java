package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class rH extends rD {
   public rH() {
      this(false);
   }

   private rH(boolean var1) {
      super(false);
   }

   private rH(boolean var1, double var2) {
      super(var1, var2);
   }

   @Override
   public final Collection<rB> b(Collection<rB> var1) {
      ArrayList var6;
      Collections.sort(var6 = new ArrayList(var1), new Comparator<rB>() {
         private int a(rB var1, rB var2) {
            double var3 = rH.this.a();
            int var5;
            return (var5 = Cq.a(var1.c, var2.c, var3)) == 0 ? Cq.a(var1.d, var2.d, var3) : var5;
         }

         // $VF: synthetic method
         @Override
         public final int compare(Object var1, Object var2) {
            rB var10001 = (rB)var1;
            rB var3 = var2;
            var2 = var10001;
            double var6 = rH.this.a();
            int var8;
            return (var8 = Cq.a(var2.c, var3.c, var6)) == 0 ? Cq.a(var2.d, var3.d, var6) : var8;
         }
      });
      ArrayList var2 = new ArrayList();

      for (rB var4 : var6) {
         this.a(var4, var2);
      }

      ArrayList var7 = new ArrayList();

      for (int var8 = var6.size() - 1; var8 >= 0; var8--) {
         rB var5 = (rB)var6.get(var8);
         this.a(var5, var7);
      }

      ArrayList var9 = new ArrayList(var2.size() + var7.size() - 2);

      for (int var10 = 0; var10 < var2.size() - 1; var10++) {
         var9.add(var2.get(var10));
      }

      for (int var11 = 0; var11 < var7.size() - 1; var11++) {
         var9.add(var7.get(var11));
      }

      if (var9.isEmpty() && !var2.isEmpty()) {
         var9.add(var2.get(0));
      }

      return var9;
   }

   private void a(rB var1, List<rB> var2) {
      double var3 = super.a();
      if (var2.size() == 1) {
         rB var10000 = (rB)var2.get(0);
         rB var9 = var1;
         if (var10000.a(var9) < var3) {
            return;
         }
      }

      while (var2.size() >= 2) {
         int var5 = var2.size();
         rB var6 = (rB)var2.get(var5 - 2);
         rB var7 = (rB)var2.get(var5 - 1);
         rw var19 = new rw(var6, var7, var3);
         rB var15 = var1;
         double var8;
         if (BY.w(var8 = var19.a((qM<rv>)var15)) < var3) {
            var15 = var1;
            double var10;
            if (!((var10 = var6.a(var15)) < var3)) {
               var15 = var1;
               if (!(var7.a(var15) < var3)) {
                  var15 = var7;
                  double var12 = var6.a(var15);
                  if (super.b()) {
                     int var14 = var10 < var12 ? var5 - 1 : var5;
                     var2.add(var14, var1);
                     return;
                  }

                  if (var10 > var12) {
                     var2.remove(var5 - 1);
                     var2.add(var1);
                  }

                  return;
               }
            }

            return;
         }

         if (!(var8 > 0.0)) {
            break;
         }

         var2.remove(var5 - 1);
      }

      var2.add(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final rF a(Collection var1) {
      return super.a(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final boolean b() {
      return super.b();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public final double a() {
      return super.a();
   }
}
