package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class BQ implements Iterable<int[]> {
   private final int a;
   private final int b;
   private final int c;

   public BQ(int var1, int var2) {
      this(var1, var2, BQ.a.LEXICOGRAPHIC$6aecea3e);
   }

   private BQ(int var1, int var2, int var3) {
      BR.e(var1, var2);
      this.a = var1;
      this.b = var2;
      this.c = var3;
   }

   private int a() {
      return this.a;
   }

   private int b() {
      return this.b;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public final Iterator<int[]> iterator() {
      if (this.b != 0 && this.b != this.a) {
         switch (<unrepresentable>.a[this.c - 1]) {
            case 1:
               return new BQ.c(this.a, this.b);
            default:
               throw new oY();
         }
      } else {
         return new BQ.d(Ch.a(this.b));
      }
   }

   private Comparator<int[]> c() {
      return new BQ.b(this.a, this.b);
   }

   enum a {
      public static final int LEXICOGRAPHIC$6aecea3e = 1;
      // $VF: synthetic field
      private static final int[] $VALUES$47939f79 = new int[]{LEXICOGRAPHIC$6aecea3e};

      public static int[] values$40fb79f8() {
         return (int[])$VALUES$47939f79.clone();
      }

      private a(String var1, int var2) {
         super(var1, var2);
      }
   }

   static class b implements Serializable, Comparator<int[]> {
      private static final long a = 20130906L;
      private final int b;
      private final int c;

      b(int var1, int var2) {
         this.b = var1;
         this.c = var2;
      }

      private int a(int[] var1, int[] var2) {
         if (var1.length != this.c) {
            throw new oS(var1.length, this.c);
         } else if (var2.length != this.c) {
            throw new oS(var2.length, this.c);
         } else {
            int[] var3 = var1;
            int[] var9;
            Arrays.sort(var9 = Ch.a(var1, var3.length));
            var3 = var2;
            int[] var10;
            Arrays.sort(var10 = Ch.a(var2, var3.length));
            long var5 = this.a(var9);
            long var7 = this.a(var10);
            if (var5 < var7) {
               return -1;
            } else {
               return var5 > var7 ? 1 : 0;
            }
         }
      }

      private long a(int[] var1) {
         long var2 = 0L;

         for (int var4 = 0; var4 < var1.length; var4++) {
            int var5;
            if ((var5 = var1[var4]) < 0 || var5 >= this.b) {
               throw new po(var5, 0, this.b - 1);
            }

            var2 += var1[var4] * BM.e(this.b, var4);
         }

         return var2;
      }

      // $VF: synthetic method
      @Override
      public final int compare(Object var1, Object var2) {
         int[] var10001 = (int[])var1;
         int[] var3 = var2;
         var2 = var10001;
         BQ.b var12 = this;
         if (var2.length != var12.c) {
            throw new oS(var2.length, var12.c);
         } else if (var3.length != var12.c) {
            throw new oS(var3.length, var12.c);
         } else {
            int[] var4 = var2;
            int[] var14;
            Arrays.sort(var14 = Ch.a(var2, var4.length));
            var4 = var3;
            int[] var15;
            Arrays.sort(var15 = Ch.a(var3, var4.length));
            long var8 = var12.a(var14);
            long var10 = var12.a(var15);
            if (var8 < var10) {
               return -1;
            } else {
               return var8 > var10 ? 1 : 0;
            }
         }
      }
   }

   static class c implements Iterator<int[]> {
      private final int a;
      private final int[] b;
      private boolean c = true;
      private int d;

      c(int var1, int var2) {
         this.a = var2;
         this.b = new int[var2 + 3];
         if (var2 != 0 && var2 < var1) {
            for (int var3 = 1; var3 <= var2; var3++) {
               this.b[var3] = var3 - 1;
            }

            this.b[var2 + 1] = var1;
            this.b[var2 + 2] = 0;
            this.d = var2;
         } else {
            this.c = false;
         }
      }

      @Override
      public final boolean hasNext() {
         return this.c;
      }

      private int[] a() {
         if (!this.c) {
            throw new NoSuchElementException();
         }

         int[] var1 = new int[this.a];
         System.arraycopy(this.b, 1, var1, 0, this.a);
         int var2 = 0;
         if (this.d > 0) {
            var2 = this.d;
            this.b[this.d] = var2;
            this.d--;
            return var1;
         }

         if (this.b[1] + 1 < this.b[2]) {
            this.b[1]++;
            return var1;
         }

         this.d = 2;
         boolean var3 = false;

         while (!var3) {
            this.b[this.d - 1] = this.d - 2;
            if ((var2 = this.b[this.d] + 1) == this.b[this.d + 1]) {
               this.d++;
            } else {
               var3 = true;
            }
         }

         if (this.d > this.a) {
            this.c = false;
            return var1;
         } else {
            this.b[this.d] = var2;
            this.d--;
            return var1;
         }
      }

      @Override
      public final void remove() {
         throw new UnsupportedOperationException();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         BQ.c var1 = this;
         if (!this.c) {
            throw new NoSuchElementException();
         }

         int[] var2 = new int[var1.a];
         System.arraycopy(var1.b, 1, var2, 0, var1.a);
         int var3 = 0;
         if (var1.d > 0) {
            var3 = var1.d;
         } else {
            if (var1.b[1] + 1 < var1.b[2]) {
               var1.b[1]++;
               return var2;
            }

            var1.d = 2;
            boolean var4 = false;

            while (!var4) {
               var1.b[var1.d - 1] = var1.d - 2;
               if ((var3 = var1.b[var1.d] + 1) == var1.b[var1.d + 1]) {
                  var1.d++;
               } else {
                  var4 = true;
               }
            }

            if (var1.d > var1.a) {
               var1.c = false;
               return var2;
            }
         }

         var1.b[var1.d] = var3;
         var1.d--;
         return var2;
      }
   }

   static class d implements Iterator<int[]> {
      private final int[] a;
      private boolean b = true;

      d(int[] var1) {
         this.a = var1;
      }

      @Override
      public final boolean hasNext() {
         return this.b;
      }

      private int[] a() {
         if (this.b) {
            this.b = false;
            return this.a;
         } else {
            throw new NoSuchElementException();
         }
      }

      @Override
      public final void remove() {
         throw new UnsupportedOperationException();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         BQ.d var1 = this;
         if (this.b) {
            var1.b = false;
            return var1.a;
         } else {
            throw new NoSuchElementException();
         }
      }
   }
}
