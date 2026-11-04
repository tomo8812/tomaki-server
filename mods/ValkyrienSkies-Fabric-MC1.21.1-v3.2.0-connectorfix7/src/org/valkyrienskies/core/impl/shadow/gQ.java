package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;

public final class gQ<T> implements Serializable, fQ<T, T> {
   private static final long a = 3514945074733160196L;
   private final fQ<? super T, ? extends T>[] b;

   public static <T> fQ<T, T> a(fQ<? super T, ? extends T>... var0) {
      hf.b(var0);
      return var0.length == 0 ? hp.a() : new gQ<>(var0);
   }

   public static <T> fQ<T, T> a(Collection<? extends fQ<? super T, ? extends T>> var0) {
      if (var0 == null) {
         throw new NullPointerException("Transformer collection must not be null");
      }

      if (var0.size() == 0) {
         return hp.a();
      }

      fQ[] var1;
      hf.b(var1 = var0.toArray(new fQ[var0.size()]));
      return new gQ<>(false, var1);
   }

   private gQ(boolean var1, fQ<? super T, ? extends T>[] var2) {
      this.b = var1 ? hf.a(var2) : var2;
   }

   private gQ(fQ<? super T, ? extends T>... var1) {
      this(true, var1);
   }

   @Override
   public final T a(T var1) {
      fQ[] var2 = this.b;
      int var3 = this.b.length;

      for (int var4 = 0; var4 < var3; var4++) {
         fQ var5;
         var1 = (var5 = var2[var4]).a(var1);
      }

      return (T)var1;
   }

   private fQ<? super T, ? extends T>[] a() {
      return hf.a(this.b);
   }
}
