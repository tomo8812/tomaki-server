package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class rx {
   private rB[] a;
   private List<rx> b;
   private rY<rv> c;
   private boolean d;
   private final double e;

   private rx(double var1) {
      this.b = new ArrayList<>();
      this.e = var1;
   }

   private rx(rB[] var1, double var2) {
      if (var1[0] == null) {
         throw new oV(px.OUTLINE_BOUNDARY_LOOP_OPEN);
      }

      this.a = var1;
      this.b = new ArrayList<>();
      this.e = var2;
      ArrayList var4 = new ArrayList();
      rB var5 = var1[var1.length - 1];

      for (int var6 = 0; var6 < var1.length; var6++) {
         rB var7 = var5;
         var5 = var1[var6];
         rw var8 = new rw(var7, var5, var2);
         qW var9 = new qW(var8.e(var7).d, var8.e(var5).d, var2);
         var4.add(new rA(var8, var9));
      }

      this.c = new ry(var4, var2);
      if (Double.isInfinite(this.c.g())) {
         this.c = new rZ<rv>().a(this.c);
         this.d = false;
      } else {
         this.d = true;
      }
   }

   private void a(rB[] var1) {
      this.a(new rx(var1, this.e));
   }

   private void a(rx var1) {
      label35:
      while (true) {
         Iterator var2 = this.b.iterator();

         while (var2.hasNext()) {
            rx var3;
            if ((var3 = (rx)var2.next()).c.a(var1.c)) {
               this = var3;
               continue label35;
            }
         }

         var2 = this.b.iterator();

         while (var2.hasNext()) {
            rx var7 = (rx)var2.next();
            if (var1.c.a(var7.c)) {
               var1.b.add(var7);
               var2.remove();
            }
         }

         rZ var6 = new rZ();

         for (rx var4 : this.b) {
            if (!var6.b(var1.c, var4.c).d()) {
               throw new oV(px.CROSSING_BOUNDARY_LOOPS);
            }
         }

         this.b.add(var1);
         return;
      }
   }

   private void a() {
      Iterator var1 = this.b.iterator();

      while (var1.hasNext()) {
         ((rx)var1.next()).a(true);
      }
   }

   private void a(boolean var1) {
      if (this.d ^ var1) {
         int var2 = -1;
         int var3 = this.a.length;

         while (true) {
            var2++;
            if (var2 >= --var3) {
               break;
            }

            rB var4 = this.a[var2];
            this.a[var2] = this.a[var3];
            this.a[var3] = var4;
         }
      }

      Iterator var5 = this.b.iterator();

      while (var5.hasNext()) {
         ((rx)var5.next()).a(!var1);
      }
   }
}
