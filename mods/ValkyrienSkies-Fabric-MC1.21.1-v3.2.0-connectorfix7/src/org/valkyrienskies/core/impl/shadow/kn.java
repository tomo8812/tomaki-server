package org.valkyrienskies.core.impl.shadow;

import java.util.List;

public final class kn<T> {
   public final List<T> a;
   public final List<T> b;
   private final fg<? super T> c;
   private final int[] d;
   private final int[] e;

   private kn(List<T> var1, List<T> var2) {
      this(var1, var2, gW.a());
   }

   public kn(List<T> var1, List<T> var2, fg<? super T> var3) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      int var4 = var1.size() + var2.size() + 2;
      this.d = new int[var4];
      this.e = new int[var4];
   }

   public final ki<T> a() {
      ki var1 = new ki();
      this.a(0, this.a.size(), 0, this.b.size(), var1);
      return var1;
   }

   private kn.a a(int var1, int var2, int var3, int var4) {
      int var5 = var1;

      while (var5 - var2 < var4 && var5 < var3 && this.c.a(this.a.get(var5), this.b.get(var5 - var2))) {
         var5++;
      }

      return new kn.a(var1, var5, var2);
   }

   private kn.a b(int var1, int var2, int var3, int var4) {
      int var5 = var2 - var1;
      int var6 = var4 - var3;
      if (var5 != 0 && var6 != 0) {
         int var7 = var5 - var6;
         int var12;
         var5 = ((var12 = var6 + var5) % 2 == 0 ? var12 : var12 + 1) / 2;
         this.d[var5 + 1] = var1;
         this.e[var5 + 1] = var2 + 1;

         for (int var14 = 0; var14 <= var5; var14++) {
            for (int var8 = -var14; var8 <= var14; var8 += 2) {
               int var9 = var8 + var5;
               if (var8 != -var14 && (var8 == var14 || this.d[var9 - 1] >= this.d[var9 + 1])) {
                  this.d[var9] = this.d[var9 - 1] + 1;
               } else {
                  this.d[var9] = this.d[var9 + 1];
               }

               int var10;
               for (int var11 = (var10 = this.d[var9]) - var1 + var3 - var8;
                  var10 < var2 && var11 < var4 && this.c.a(this.a.get(var10), this.b.get(var11));
                  var11++
               ) {
                  this.d[var9] = ++var10;
               }

               if (var7 % 2 != 0 && var7 - var14 <= var8 && var8 <= var7 + var14 && this.e[var9 - var7] <= this.d[var9]) {
                  return this.a(this.e[var9 - var7], var8 + var1 - var3, var2, var4);
               }
            }

            for (int var15 = var7 - var14; var15 <= var7 + var14; var15 += 2) {
               int var16 = var15 + var5 - var7;
               if (var15 != var7 - var14 && (var15 == var7 + var14 || this.e[var16 + 1] > this.e[var16 - 1])) {
                  this.e[var16] = this.e[var16 - 1];
               } else {
                  this.e[var16] = this.e[var16 + 1] - 1;
               }

               int var17;
               for (int var18 = (var17 = this.e[var16] - 1) - var1 + var3 - var15;
                  var17 >= var1 && var18 >= var3 && this.c.a(this.a.get(var17), this.b.get(var18));
                  var18--
               ) {
                  this.e[var16] = var17--;
               }

               if (var7 % 2 == 0 && -var14 <= var15 && var15 <= var14 && this.e[var16] <= this.d[var16 + var7]) {
                  return this.a(this.e[var16], var15 + var1 - var3, var2, var4);
               }
            }
         }

         throw new RuntimeException("Internal Error");
      } else {
         return null;
      }
   }

   public void a(int var1, int var2, int var3, int var4, ki<T> var5) {
      kn.a var6;
      while ((var6 = this.b(var1, var2, var3, var4)) != null && (var6.a != var2 || var6.c != var2 - var4) && (var6.b != var1 || var6.c != var1 - var3)) {
         this.a(var1, var6.a, var3, var6.a - var6.c, var5);

         for (int var7 = var6.a; var7 < var6.b; var7++) {
            var5.a(new kk<>(this.a.get(var7)));
         }

         int var10003 = var6.b - var6.c;
         var4 = var4;
         var3 = var10003;
         var2 = var2;
         var1 = var6.b;
         this = this;
      }

      int var10 = var1;
      int var9 = var3;

      while (var10 < var2 || var9 < var4) {
         if (var10 < var2 && var9 < var4 && this.c.a(this.a.get(var10), this.b.get(var9))) {
            var5.a(new kk<>(this.a.get(var10)));
            var10++;
            var9++;
         } else if (var2 - var1 > var4 - var3) {
            var5.a(new kg<>(this.a.get(var10)));
            var10++;
         } else {
            var5.a(new kj<>(this.b.get(var9)));
            var9++;
         }
      }
   }

   static class a {
      final int a;
      final int b;
      final int c;

      public a(int var1, int var2, int var3) {
         this.a = var1;
         this.b = var2;
         this.c = var3;
      }

      public final int a() {
         return this.a;
      }

      public final int b() {
         return this.b;
      }

      public final int c() {
         return this.c;
      }
   }
}
