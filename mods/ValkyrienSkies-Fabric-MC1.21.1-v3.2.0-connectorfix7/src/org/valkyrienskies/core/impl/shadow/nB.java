package org.valkyrienskies.core.impl.shadow;

public class nB<T extends kO<T>> implements nw<T> {
   private static final int a = 2;
   private final kM<T> b;
   private final int c;
   private final T d;
   private final T e;
   private final T f;
   private Cc.a g;

   public nB(T var1, T var2, T var3, int var4) {
      if (var4 < 2) {
         throw new pn(var4, 2, true);
      }

      this.b = var1.c();
      this.c = var4;
      this.e = (T)var2;
      this.f = (T)var1;
      this.d = (T)var3;
      this.g = Cc.a.a();
   }

   private int f() {
      return this.c;
   }

   @Override
   public final int a() {
      Cc.a var1 = this.g;
      return this.g.b;
   }

   @Override
   public final int b() {
      Cc.a var1 = this.g;
      return this.g.e;
   }

   @Override
   public T c() {
      return this.e;
   }

   @Override
   public T d() {
      return this.f;
   }

   @Override
   public T e() {
      return this.d;
   }

   @Override
   public final T a(int var1, la<T> var2, T var3, T var4, int var5) {
      return this.a(var1, var2, (T)var3, (T)var4, (T)var3.a(var4).d(2.0), var5);
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final T a(int var1, la<T> var2, T var3, T var4, T var5, int var6) {
      Ci.a(var2);
      this.g = this.g.b(var1).a(0);
      kO var24;
      kO var7 = (var24 = this.b.a()).a(Double.NaN);
      kO[] var8 = Ch.a(this.b, this.c + 1);
      kO[] var9 = Ch.a(this.b, this.c + 1);
      var8[0] = var3;
      var8[1] = var5;
      var8[2] = var4;
      Cc.a var20 = this.g;
      this.g.d(1);
      var9[1] = var2.a((T)var8[1]);
      if (Cq.b(var9[1].d(), 0.0, 1)) {
         return (T)var8[1];
      }

      var20 = this.g;
      this.g.d(1);
      var9[0] = var2.a((T)var8[0]);
      if (Cq.b(var9[0].d(), 0.0, 1)) {
         return (T)var8[0];
      }

      int var25;
      int var26;
      if (var9[0].c(var9[1]).d() < 0.0) {
         var25 = 2;
         var26 = 1;
      } else {
         var20 = this.g;
         this.g.d(1);
         var9[2] = var2.a((T)var8[2]);
         if (Cq.b(var9[2].d(), 0.0, 1)) {
            return (T)var8[2];
         }

         if (!(var9[1].c(var9[2]).d() < 0.0)) {
            throw new pe(var8[0].d(), var8[2].d(), var9[0].d(), var9[2].d());
         }

         var25 = 3;
         var26 = 2;
      }

      kO[] var27 = Ch.a(this.b, var8.length);
      kO var10 = var8[var26 - 1];
      kO var11 = var9[var26 - 1];
      kO var12 = var10.e();
      kO var13 = var11.e();
      int var14 = 0;
      kO var15 = var8[var26];
      kO var16 = var9[var26];
      kO var17 = var15.e();
      kO var18 = var16.e();
      int var19 = 0;

      while (true) {
         kO var30 = var12.b(var17).d() < 0.0 ? var17 : var12;
         kO var21 = var13.b(var18).d() < 0.0 ? var18 : var13;
         kO var31 = this.e.a(this.f.c((T)var30));
         if (var15.b(var10).b(var31).d() <= 0.0 || ((kO)var21.b(this.d)).d() < 0.0) {
            switch (<unrepresentable>.a[var6 - 1]) {
               case 1:
                  if (var13.b(var18).d() < 0.0) {
                     return (T)var10;
                  }

                  return (T)var15;
               case 2:
                  return (T)var10;
               case 3:
                  return (T)var15;
               case 4:
                  if (var11.d() <= 0.0) {
                     return (T)var10;
                  }

                  return (T)var15;
               case 5:
                  if (var11.d() < 0.0) {
                     return (T)var15;
                  }

                  return (T)var10;
               default:
                  throw new oY(null);
            }
         }

         kO var32;
         if (var14 >= 2) {
            var32 = (kO)var16.d(16.0).a();
         } else if (var19 >= 2) {
            var32 = (kO)var11.d(16.0).a();
         } else {
            var32 = var24;
         }

         int var22 = 0;
         int var23 = var25;

         do {
            System.arraycopy(var8, var22, var27, var22, var23 - var22);
            if (!((var21 = this.a((T)var32, (T[])var27, (T[])var9, var22, var23)).b(var10).d() > 0.0) || !(var21.b(var15).d() < 0.0)) {
               if (var26 - var22 >= var23 - var26) {
                  var22++;
               } else {
                  var23--;
               }

               var21 = var7;
            }
         } while (Double.isNaN(var21.d()) && var23 - var22 > 1);

         if (Double.isNaN(var21.d())) {
            var21 = var10.a(var15.b(var10).d(2.0));
            var22 = var26 - 1;
            var23 = var26;
         }

         var20 = this.g;
         this.g.d(1);
         kO var34;
         if (Cq.b((var34 = var2.a((T)var21)).d(), 0.0, 1)) {
            return (T)var21;
         }

         if (var25 > 2 && var23 - var22 != var25) {
            var25 = var23 - var22;
            System.arraycopy(var8, var22, var8, 0, var25);
            System.arraycopy(var9, var22, var9, 0, var25);
            var26 -= var22;
         } else if (var25 == var8.length) {
            var25--;
            if (var26 >= (var8.length + 1) / 2) {
               System.arraycopy(var8, 1, var8, 0, var25);
               System.arraycopy(var9, 1, var9, 0, var25);
               var26--;
            }
         }

         System.arraycopy(var8, var26, var8, var26 + 1, var25 - var26);
         var8[var26] = var21;
         System.arraycopy(var9, var26, var9, var26 + 1, var25 - var26);
         var9[var26] = var34;
         var25++;
         if (var34.c(var11).d() <= 0.0) {
            var15 = var21;
            var16 = var34;
            var18 = var34.e();
            var14++;
            var19 = 0;
         } else {
            var10 = var21;
            var11 = var34;
            var13 = var34.e();
            var14 = 0;
            var19++;
            var26++;
         }
      }
   }

   private T a(T var1, T[] var2, T[] var3, int var4, int var5) {
      for (int var6 = var4; var6 < var5 - 1; var6++) {
         int var7 = var6 + 1 - var4;

         for (int var8 = var5 - 1; var8 > var6; var8--) {
            var2[var8] = var2[var8].b(var2[var8 - 1]).d(var3[var8].b(var3[var8 - var7]));
         }
      }

      kO var9 = this.b.a();

      for (int var10 = var5 - 1; var10 >= var4; var10--) {
         var9 = var2[var10].a(var9.c(var1.b(var3[var10])));
      }

      return (T)var9;
   }
}
