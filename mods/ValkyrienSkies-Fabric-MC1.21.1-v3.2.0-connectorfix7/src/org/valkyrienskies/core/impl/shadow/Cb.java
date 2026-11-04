package org.valkyrienskies.core.impl.shadow;

@Deprecated
public class Cb {
   public int a;
   public int b = 0;
   private final Cb.a c;

   public Cb() {
      this(0);
   }

   private Cb(int var1) {
      this(0, new Cb.a() {
         @Override
         public final void a(int var1) {
            throw new pc(var1);
         }
      });
   }

   public Cb(int var1, Cb.a var2) {
      if (var2 == null) {
         throw new pl();
      }

      this.a = var1;
      this.c = var2;
   }

   public void a(int var1) {
      this.a = var1;
   }

   public final int a() {
      return this.a;
   }

   public final int b() {
      return this.b;
   }

   private boolean e() {
      return this.b < this.a;
   }

   public final void b(int var1) {
      for (int var2 = 0; var2 < var1; var2++) {
         this.c();
      }
   }

   public void c() {
      if (++this.b > this.a) {
         this.c.a(this.a);
      }
   }

   public void d() {
      this.b = 0;
   }

   public static <unrepresentable> a(Cc.a var0) {
      return new Cb(var0) {
         private Cc.a c;
         // $VF: synthetic field
         private Cc.a d;

         {
            this.d = var1;
            this.c = this.d;
            var1 = this.c;
            super.a(this.c.b);
            var1 = this.c;
            int var2 = this.c.e;
            var var6 = this;

            for (int var3 = 0; var3 < var2; var3++) {
               var6.c();
            }
         }

         @Override
         public final void a(int var1) {
            super.a(var1);
            this.c = this.c.b(var1);
         }

         @Override
         public final void d() {
            super.d();
            this.c = this.c.a(0);
         }

         @Override
         public final void c() {
            super.c();
            Cc.a var1 = this.c;
            this.c.d(1);
         }
      };
   }

   public interface a {
      void a(int var1);
   }
}
