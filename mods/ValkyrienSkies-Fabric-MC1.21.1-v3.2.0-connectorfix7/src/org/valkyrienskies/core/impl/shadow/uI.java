package org.valkyrienskies.core.impl.shadow;

public class uI<T extends kO<T>> {
   public final T a;
   public final T[] b;
   private final T[][] c;

   private uI(T var1, T[] var2) {
      this((T)var1, (T[])var2, null);
   }

   public uI(T var1, T[] var2, T[][] var3) {
      this.a = (T)var1;
      this.b = (T[])((kO[])var2.clone());
      this.c = (T[][])a(var1.c(), (T[][])var3);
   }

   protected static T[][] a(kM<T> var0, T[][] var1) {
      if (var1 == null) {
         return null;
      }

      kO[][] var3 = Ch.a(var0, var1.length, -1);

      for (int var2 = 0; var2 < var1.length; var2++) {
         var3[var2] = (kO[])var1[var2].clone();
      }

      return (T[][])var3;
   }

   public final T a() {
      return this.a;
   }

   public final int b() {
      return this.b.length;
   }

   public final T[] c() {
      return (T[])((kO[])this.b.clone());
   }

   public final int d() {
      return this.c == null ? 0 : this.c.length;
   }

   public final int a(int var1) {
      return var1 == 0 ? this.b.length : this.c[var1 - 1].length;
   }

   public final T[] b(int var1) {
      return (T[])(var1 == 0 ? (kO[])this.b.clone() : (kO[])this.c[var1 - 1].clone());
   }
}
