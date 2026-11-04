package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class uC<T extends kO<T>> implements wv<T> {
   private T a;
   private T b;
   private boolean c;
   private int d;
   private List<ww<T>> e = new ArrayList<>();

   public uC() {
      this.a = null;
      this.b = null;
      this.c = true;
      this.d = 0;
   }

   private void a(uC<T> var1) {
      if (var1.e.size() != 0) {
         if (this.e.size() == 0) {
            this.a = var1.a;
            this.c = var1.c;
         } else {
            uJ var2 = this.e.get(0).c();
            uJ var3 = var1.e.get(0).c();
            a(var2.b.length, var3.b.length);
            a(var2.d(), var3.d());

            for (int var4 = 0; var4 < var2.d(); var4++) {
               a(var2.a(var4), var3.a(var4));
            }

            if (this.c ^ var1.c) {
               throw new oV(px.PROPAGATION_DIRECTION_MISMATCH);
            }

            ww var12;
            kO var6 = (var12 = this.e.get(this.d)).d().a;
            kO var9 = var12.c().a;
            kO var10 = var6.b(var9);
            kO var7;
            if ((var7 = var1.a.b((T)var6)).e().b(var10.e().c(0.001)).d() > 0.0) {
               throw new oV(px.HOLE_BETWEEN_MODELS_TIME_RANGES, var7.e().d());
            }
         }

         for (ww var11 : var1.e) {
            this.e.add(var11);
         }

         this.d = this.e.size() - 1;
         this.b = this.e.get(this.d).d().a;
      }
   }

   private static void a(int var0, int var1) {
      if (var0 != var1) {
         throw new oS(var1, var0);
      }
   }

   @Override
   public final void a(uJ<T> var1, T var2) {
      this.a = var1.a;
      this.b = (T)var2;
      this.c = true;
      this.d = 0;
      this.e.clear();
   }

   @Override
   public final void a(ww<T> var1, boolean var2) {
      if (this.e.size() == 0) {
         this.a = var1.c().a;
         this.c = var1.e();
      }

      this.e.add(var1);
      if (var2) {
         this.b = var1.d().a;
         this.d = this.e.size() - 1;
      }
   }

   private T a() {
      return this.a;
   }

   private T b() {
      return this.b;
   }

   private uJ<T> a(T var1) {
      int var2 = 0;
      ww var3;
      kO var4 = (var3 = this.e.get(0)).c().a.a(var3.d().a).c(0.5);
      int var5 = this.e.size() - 1;
      ww var6;
      kO var7 = (var6 = this.e.get(var5)).c().a.a(var6.d().a).c(0.5);
      if (this.a((T)var1, var3) <= 0) {
         this.d = 0;
         return var3.a((T)var1);
      }

      if (this.a((T)var1, var6) >= 0) {
         this.d = var5;
         return var6.a((T)var1);
      }

      while (var5 - var2 > 5) {
         var3 = this.e.get(this.d);
         int var16;
         if ((var16 = this.a((T)var1, var3)) < 0) {
            var5 = this.d;
            var7 = var3.c().a.a(var3.d().a).c(0.5);
         } else {
            if (var16 <= 0) {
               return var3.a((T)var1);
            }

            var2 = this.d;
            var4 = var3.c().a.a(var3.d().a).c(0.5);
         }

         int var14 = (var2 + var5) / 2;
         kO var18;
         if (!((var18 = (var6 = this.e.get(var14)).c().a.a(var6.d().a).c(0.5)).b(var4).e().b(1.0E-6).d() < 0.0) && !(var7.b(var18).e().b(1.0E-6).d() < 0.0)) {
            kO var8 = var7.b(var18);
            kO var9 = var18.b(var4);
            kO var10 = var7.b(var4);
            kO var11 = var1.b(var7);
            kO var19 = var1.b(var18);
            kO var12 = var1.b(var4);
            kO var15 = ((kO)((kO)((kO)var19.c(var12).c(var9).a(var5)).b(var11.c(var12).c(var10).a(var14))).a(var11.c(var19).c(var8).a(var2)))
               .d(var8.c(var9).c(var10));
            this.d = (int)BY.A(var15.d());
         } else {
            this.d = var14;
         }

         int var20 = BY.b(var2 + 1, (var2 * 9 + var5) / 10);
         int var21 = BY.a(var5 - 1, (var2 + var5 * 9) / 10);
         if (this.d < var20) {
            this.d = var20;
         } else if (this.d > var21) {
            this.d = var21;
         }
      }

      this.d = var2;

      while (this.d <= var5 && this.a((T)var1, this.e.get(this.d)) > 0) {
         this.d++;
      }

      return this.e.get(this.d).a((T)var1);
   }

   private int a(T var1, ww<T> var2) {
      if (this.c) {
         if (((kO)var1.b(var2.c().a)).d() < 0.0) {
            return -1;
         } else {
            return ((kO)var1.b(var2.d().a)).d() > 0.0 ? 1 : 0;
         }
      } else if (((kO)var1.b(var2.c().a)).d() > 0.0) {
         return -1;
      } else {
         return ((kO)var1.b(var2.d().a)).d() < 0.0 ? 1 : 0;
      }
   }
}
