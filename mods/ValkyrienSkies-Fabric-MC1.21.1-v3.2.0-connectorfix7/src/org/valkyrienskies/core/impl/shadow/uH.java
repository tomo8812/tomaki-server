package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.List;

public final class uH<T extends kO<T>> {
   final uN<T> a;
   List<uK<T>> b;
   public uG<T> c;

   private uH(uN<T> var1) {
      this.a = var1;
      this.b = new ArrayList<>();
      this.c = new uG<>(null, var1.a());
   }

   public final uG<T> a() {
      return this.c;
   }

   private int a(uK<T> var1) {
      this.b.add(var1);
      this.c = new uG<>(this.c, var1.a());
      return this.b.size();
   }

   public final void a(T var1, T[] var2, T var3) {
      int var4 = 0;
      this.c.a(0, (T[])var2);

      while (true) {
         var4++;
         uG var5 = this.c;
         if (var4 >= this.c.a.length - 1) {
            return;
         }

         this.c.a(var4, (T[])var2);
         uK var10000 = this.b.get(var4 - 1);
      }
   }

   public final T[] a(T var1, T[] var2) {
      kO[] var5 = Ch.a(var1.c(), this.c.b());
      int var3 = 0;
      this.c.a(0, (T[])var2);
      kO[] var4 = this.a.c();
      this.c.a(0, (T[])var4, (T[])var5);

      while (true) {
         var3++;
         uG var6 = this.c;
         if (var3 >= this.c.a.length - 1) {
            return (T[])var5;
         }

         this.c.a(var3, (T[])var2);
         var4 = this.b.get(var3 - 1).c();
         this.c.a(var3, (T[])var4, (T[])var5);
      }
   }
}
