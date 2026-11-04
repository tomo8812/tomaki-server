package org.valkyrienskies.core.impl.shadow;

public abstract class wH<PAIR> extends wI<PAIR> {
   private final wI<PAIR> a;
   private int b;
   private int c;
   private zH d;
   private wO[] e;
   private int f = -1;
   private int g = -1;

   public wH(wI<PAIR> var1, int var2, zH var3) {
      super(var1.j());
      if (var2 <= 0) {
         throw new pk(var2);
      }

      this.a = var1;
      this.c = var2;
      this.d = var3;
   }

   @Override
   public final int a() {
      return this.b;
   }

   public abstract PAIR[] b();

   @Override
   public final PAIR a(wO... var1) {
      this.e = var1;
      return super.a(var1);
   }

   @Override
   protected final PAIR c() {
      for (int var1 = 0; var1 < this.e.length; var1++) {
         if (this.e[var1] instanceof wM) {
            this.e[var1] = null;
            this.f = var1;
         }

         if (this.e[var1] instanceof wL) {
            this.e[var1] = null;
            this.g = var1;
         }
      }

      if (this.f == -1) {
         throw new oX();
      }

      if (this.g == -1) {
         throw new oX();
      }

      RuntimeException var11 = null;
      this.b = 0;
      this.d();
      int var2 = this.h();
      double[] var3 = this.f();
      double[] var4 = this.g();
      double[] var5 = this.e();

      for (int var6 = 0; var6 < this.c; var6++) {
         try {
            this.e[this.f] = new wM(var2 - this.b);
            double[] var7 = null;
            if (var6 == 0) {
               var7 = var5;
            } else {
               int var8 = 0;

               while (var7 == null) {
                  if (var8++ >= this.h()) {
                     throw new pp(this.h());
                  }

                  var7 = this.d.a();

                  for (int var9 = 0; var7 != null && var9 < var7.length; var9++) {
                     if (var3 != null && var7[var9] < var3[var9] || var4 != null && var7[var9] > var4[var9]) {
                        var7 = null;
                     }
                  }
               }
            }

            this.e[this.g] = new wL(var7);
            Object var13 = this.a.a(this.e);
            this.a((PAIR)var13);
         } catch (RuntimeException var10) {
            var11 = var10;
         }

         this.b = this.b + this.a.a();
      }

      Co[] var12;
      if ((var12 = this.b()).length == 0) {
         throw var11;
      } else {
         return (PAIR)var12[0];
      }
   }

   protected abstract void a(PAIR var1);

   protected abstract void d();
}
