package org.valkyrienskies.core.impl.shadow;

public final class uJ<T extends kO<T>> extends uI<T> {
   private final T[] c;
   private final T[][] d;

   public uJ(T var1, T[] var2, T[] var3) {
      this((T)var1, (T[])var2, (T[])var3, null, null);
   }

   public uJ(T var1, T[] var2, T[] var3, T[][] var4, T[][] var5) {
      super((T)var1, (T[])var2, (T[][])var4);
      this.c = (T[])((kO[])var3.clone());
      this.d = (T[][])a(var1.c(), (T[][])var5);
   }

   public final T[] e() {
      return (T[])((kO[])this.c.clone());
   }

   public final T[] c(int var1) {
      return (T[])(var1 == 0 ? (kO[])this.c.clone() : (kO[])this.d[var1 - 1].clone());
   }
}
