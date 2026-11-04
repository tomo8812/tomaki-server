package org.valkyrienskies.core.impl.shadow;

public final class Du {
   private final int[] c;
   private final int d;
   int a;
   int b;
   private int e;

   public Du(int var1) {
      this.c = new int[var1 * 3];
      this.d = var1;
      this.a = -1;
      this.b = -1;
      this.e = 0;
      this.a();
   }

   public final void a(int var1) {
      int var2 = this.c(var1);
      if (this.e == 0) {
         this.a = this.b = var1;
         this.b(var1, -1);
         this.c(var1, -1);
      } else if (var2 == 0) {
         if (var1 < this.a) {
            this.b(this.a, var1);
            this.c(var1, this.a);
            this.a = var1;
         } else if (var1 > this.b) {
            this.c(this.b, var1);
            this.b(var1, this.b);
            this.b = var1;
         } else {
            int var3 = -1;

            for (int var4 = var1 - 1; var4 >= 0; var4--) {
               if (this.c(var4) != 0) {
                  var3 = var4;
                  break;
               }
            }

            int var5 = this.e(var3);
            this.c(var1, var5);
            this.b(var1, var3);
            this.c(var3, var1);
            this.b(var5, var1);
         }
      }

      this.a(var1, var2 + 1);
      this.e++;
   }

   public final void b(int var1) {
      if (this.e <= 0) {
         throw new IllegalArgumentException("Cannot decrement when list is empty");
      }

      int var2;
      if ((var2 = this.c(var1)) <= 0) {
         throw new IllegalArgumentException("Cannot store negative values");
      }

      if (var2 == 1) {
         if (this.e == 1) {
            this.c(var1, -1);
            this.b(var1, -1);
            this.a = -1;
            this.b = -1;
         } else if (var1 == this.a) {
            var2 = this.e(this.a);
            this.c(this.a, -1);
            this.b(var2, -1);
            this.a = var2;
         } else if (var1 == this.b) {
            var2 = this.d(this.b);
            this.b(this.b, -1);
            this.c(var2, -1);
            this.b = var2;
         } else {
            var2 = this.d(var1);
            int var3 = this.e(var1);
            this.c(var2, var3);
            this.b(var3, var2);
         }

         this.a(var1, 0);
      } else {
         this.a(var1, var2 - 1);
      }

      this.e--;
   }

   private void a(int var1, int var2) {
      this.f(var1 * 3);
      this.c[var1 * 3] = var2;
   }

   private void b(int var1, int var2) {
      this.f(var1 * 3 + 1);
      this.c[var1 * 3 + 1] = var2;
   }

   private void c(int var1, int var2) {
      this.f(var1 * 3 + 2);
      this.c[var1 * 3 + 2] = var2;
   }

   private int c(int var1) {
      this.f(var1 * 3);
      return this.c[var1 * 3];
   }

   private int d(int var1) {
      this.f(var1 * 3 + 1);
      return this.c[var1 * 3 + 1];
   }

   private int e(int var1) {
      this.f(var1 * 3 + 2);
      return this.c[var1 * 3 + 2];
   }

   private void f(int var1) {
      if (var1 < 0 || var1 > this.d * 3) {
         throw new IllegalArgumentException("Cannot store key of value " + var1);
      }
   }

   public final void a() {
      this.e = 0;

      for (int var1 = 0; var1 < this.d; var1++) {
         this.a(var1, 0);
         this.b(var1, -1);
         this.c(var1, -1);
      }
   }

   public final int b() {
      return this.a;
   }

   public final int c() {
      return this.b;
   }
}
