package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Ck implements Iterable<Integer> {
   private final int a;
   private final int[] b;
   private final int[] c;
   private final int d;
   private final int e;

   private Ck(int... var1) {
      this.a = var1.length;
      int[] var2 = var1;
      this.c = Ch.a(var1, var2.length);
      this.b = new int[this.a];
      this.e = this.a - 1;
      int var6 = var1[this.e];

      for (int var3 = 0; var3 < this.e; var3++) {
         int var4 = 1;

         for (int var5 = var3 + 1; var5 < this.a; var5++) {
            var4 *= var1[var5];
         }

         this.b[var3] = var4;
         var6 *= var1[var3];
      }

      this.b[this.e] = 0;
      if (var6 <= 0) {
         throw new pk(var6);
      }

      this.d = var6;
   }

   private Ck.a a() {
      return new Ck.a();
   }

   private int b() {
      return this.a;
   }

   private int[] a(int var1) {
      if (var1 >= 0 && var1 < this.d) {
         int[] var2 = new int[this.a];
         int var3 = 0;

         for (int var4 = 0; var4 < this.e; var4++) {
            int var5 = 0;
            int var6 = this.b[var4];

            while (var3 <= var1) {
               var3 += var6;
               var5++;
            }

            var5--;
            var3 -= var6;
            var2[var4] = var5;
         }

         var2[this.e] = var1 - var3;
         return var2;
      } else {
         throw new po(var1, 0, this.d);
      }
   }

   private int a(int... var1) {
      if (var1.length != this.a) {
         throw new oS(var1.length, this.a);
      }

      int var2 = 0;

      for (int var3 = 0; var3 < this.a; var3++) {
         int var4;
         if ((var4 = var1[var3]) < 0 || var4 >= this.c[var3]) {
            throw new po(var4, 0, this.c[var3] - 1);
         }

         var2 += this.b[var3] * var1[var3];
      }

      return var2 + var1[this.e];
   }

   private int c() {
      return this.d;
   }

   private int[] d() {
      int[] var1 = this.c;
      return Ch.a(this.c, var1.length);
   }

   @Override
   public final String toString() {
      StringBuilder var1 = new StringBuilder();

      for (int var2 = 0; var2 < this.a; var2++) {
         StringBuilder var10000 = var1.append("[");
         int[] var4 = new int[]{var2};
         Ck var3 = this;
         if (var4.length != var3.a) {
            throw new oS(var4.length, var3.a);
         }

         int var5 = 0;

         for (int var6 = 0; var6 < var3.a; var6++) {
            int var7;
            if ((var7 = var4[var6]) < 0 || var7 >= var3.c[var6]) {
               throw new po(var7, 0, var3.c[var6] - 1);
            }

            var5 += var3.b[var6] * var4[var6];
         }

         var10000.append(var5 + var4[var3.e]).append("]");
      }

      return var1.toString();
   }

   // $VF: synthetic method
   @Override
   public final Iterator iterator() {
      Ck var1 = this;
      return var1.new a();
   }

   public class a implements Iterator<Integer> {
      private final int[] a = new int[Ck.this.a];
      private int b = -1;
      private final int c = Ck.this.d - 1;

      a() {
         this.a[Ck.this.e] = -1;
      }

      @Override
      public final boolean hasNext() {
         return this.b < this.c;
      }

      private Integer a() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         }

         for (int var1 = Ck.this.e; var1 >= 0; var1--) {
            if (this.a[var1] != Ck.this.c[var1] - 1) {
               this.a[var1]++;
               break;
            }

            this.a[var1] = 0;
         }

         return ++this.b;
      }

      private int b() {
         return this.b;
      }

      private int[] c() {
         int[] var1 = this.a;
         return Ch.a(this.a, var1.length);
      }

      private int a(int var1) {
         return this.a[var1];
      }

      @Override
      public final void remove() {
         throw new UnsupportedOperationException();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         Ck.a var1 = this;
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         }

         for (int var2 = Ck.this.e; var2 >= 0; var2--) {
            if (var1.a[var2] != Ck.this.c[var2] - 1) {
               var1.a[var2]++;
               break;
            }

            var1.a[var2] = 0;
         }

         return ++var1.b;
      }
   }
}
