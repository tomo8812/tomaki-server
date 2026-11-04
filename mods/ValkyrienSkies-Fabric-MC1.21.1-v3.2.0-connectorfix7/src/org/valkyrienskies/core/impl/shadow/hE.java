package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

public final class hE<I, O> implements Serializable, fQ<I, O> {
   private static final long a = -6404460890903469332L;
   private final fF<? super I>[] b;
   private final fQ<? super I, ? extends O>[] c;
   private final fQ<? super I, ? extends O> d;

   public static <I, O> fQ<I, O> a(fF<? super I>[] var0, fQ<? super I, ? extends O>[] var1, fQ<? super I, ? extends O> var2) {
      hf.b(var0);
      hf.b(var1);
      if (var0.length != var1.length) {
         throw new IllegalArgumentException("The predicate and transformer arrays must be the same size");
      } else if (var0.length == 0) {
         return var2 == null ? gV.a() : var2;
      } else {
         return new hE<>(var0, var1, var2);
      }
   }

   public static <I, O> fQ<I, O> a(Map<? extends fF<? super I>, ? extends fQ<? super I, ? extends O>> var0) {
      if (var0 == null) {
         throw new NullPointerException("The predicate and transformer map must not be null");
      }

      if (var0.size() == 0) {
         return gV.a();
      }

      fQ var1 = (fQ)var0.remove(null);
      int var2;
      if ((var2 = var0.size()) == 0) {
         return var1 == null ? gV.a() : var1;
      }

      fQ[] var3 = new fQ[var2];
      fF[] var7 = new fF[var2];
      int var4 = 0;

      for (Entry var5 : var0.entrySet()) {
         var7[var4] = (fF)var5.getKey();
         var3[var4] = (fQ)var5.getValue();
         var4++;
      }

      return new hE<>(false, var7, var3, var1);
   }

   private hE(boolean var1, fF<? super I>[] var2, fQ<? super I, ? extends O>[] var3, fQ<? super I, ? extends O> var4) {
      this.b = var1 ? hf.a(var2) : var2;
      this.c = var1 ? hf.a(var3) : var3;
      this.d = var4 == null ? gV.a() : var4;
   }

   private hE(fF<? super I>[] var1, fQ<? super I, ? extends O>[] var2, fQ<? super I, ? extends O> var3) {
      this(true, var1, var2, var3);
   }

   @Override
   public final O a(I var1) {
      for (int var2 = 0; var2 < this.b.length; var2++) {
         if (this.b[var2].a((I)var1)) {
            return (O)this.c[var2].a((I)var1);
         }
      }

      return (O)this.d.a((I)var1);
   }

   private fF<? super I>[] a() {
      return hf.a(this.b);
   }

   private fQ<? super I, ? extends O>[] b() {
      return hf.a(this.c);
   }

   private fQ<? super I, ? extends O> c() {
      return this.d;
   }
}
