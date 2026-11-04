package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public final class Cc {
   private Cc() {
   }

   private static Cc.b a(int var0, int var1) {
      boolean var2 = true;
      var1 = var1;
      var0 = var0;
      return new Cc.b(var0, var1, 1);
   }

   private static Cc.b a(int var0, int var1, int var2) {
      return new Cc.b(var0, var1, 1);
   }

   public static class a implements Iterator<Integer> {
      private static final <unrepresentable> f = new Cc.a.a() {
         @Override
         public final void a(int var1) {
            throw new pc(var1);
         }
      };
      final int a;
      public final int b;
      final int c;
      final Cc.a.a d;
      public int e = 0;

      a(int var1, int var2, int var3, Cc.a.a var4) {
         if (var4 == null) {
            throw new pl();
         }

         this.a = var1;
         this.b = var2;
         this.c = var3;
         this.d = var4;
         this.e = var1;
      }

      public static Cc.a a() {
         return new Cc.a(0, 0, 1, f);
      }

      public final Cc.a a(int var1) {
         return new Cc.a(var1, this.b, this.c, this.d);
      }

      public final Cc.a b(int var1) {
         return new Cc.a(this.a, var1, this.c, this.d);
      }

      public final Cc.a c(int var1) {
         if (var1 == 0) {
            throw new pr();
         } else {
            return new Cc.a(this.a, this.b, var1, this.d);
         }
      }

      public final Cc.a a(Cc.a.a var1) {
         return new Cc.a(this.a, this.b, this.c, var1);
      }

      public final int b() {
         return this.b;
      }

      public final int c() {
         return this.e;
      }

      private boolean e() {
         return this.e(1);
      }

      private boolean e(int var1) {
         var1 = this.e + var1 * this.c;
         return this.c < 0 ? var1 > this.b : var1 < this.b;
      }

      public final void d(int var1) {
         if (var1 <= 0) {
            throw new pk(var1);
         }

         if (!this.e(0)) {
            this.d.a(this.b);
         }

         this.e = this.e + var1 * this.c;
      }

      public final void d() {
         this.d(1);
      }

      @Override
      public final boolean hasNext() {
         return this.e(0);
      }

      private Integer f() {
         int var1 = this.e;
         this.d(1);
         return var1;
      }

      @Override
      public final void remove() {
         throw new pb();
      }

      // $VF: synthetic method
      @Override
      public final Object next() {
         Cc.a var1 = this;
         int var2 = this.e;
         var1.d(1);
         return var2;
      }

      public interface a {
         void a(int var1);
      }
   }

   public static class b implements Iterable<Integer> {
      private final int a;
      private final int b;
      private final int c;
      private final int d;

      public b(int var1, int var2, int var3) {
         this.b = var1;
         this.c = var2;
         this.d = var3;
         var1 = (var2 - var1) / var3 + 1;
         this.a = var1 < 0 ? 0 : var1;
      }

      private int a() {
         return this.a;
      }

      @Override
      public final Iterator<Integer> iterator() {
         Cc.a var10000 = Cc.a.a().a(this.b).b(this.c + (this.d > 0 ? 1 : -1));
         int var2 = this.d;
         Cc.a var1 = var10000;
         if (var2 == 0) {
            throw new pr();
         } else {
            return new Cc.a(var1.a, var1.b, var2, var1.d);
         }
      }
   }
}
