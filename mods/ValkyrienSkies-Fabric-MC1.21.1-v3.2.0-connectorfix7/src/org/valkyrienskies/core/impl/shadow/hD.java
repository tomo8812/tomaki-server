package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Map;
import java.util.Map.Entry;

public final class hD<E> implements Serializable, fb<E> {
   private static final long a = 3518477308466486130L;
   private final fF<? super E>[] b;
   private final fb<? super E>[] c;
   private final fb<? super E> d;

   public static <E> fb<E> a(fF<? super E>[] var0, fb<? super E>[] var1, fb<? super E> var2) {
      hf.b(var0);
      hf.b(var1);
      if (var0.length != var1.length) {
         throw new IllegalArgumentException("The predicate and closure arrays must be the same size");
      } else if (var0.length == 0) {
         return var2 == null ? ho.a() : var2;
      } else {
         return new hD<>(var0, var1, var2);
      }
   }

   public static <E> fb<E> a(Map<fF<E>, fb<E>> var0) {
      if (var0 == null) {
         throw new NullPointerException("The predicate and closure map must not be null");
      }

      fb var1 = (fb)var0.remove(null);
      int var2;
      if ((var2 = var0.size()) == 0) {
         return var1 == null ? ho.a() : var1;
      }

      fb[] var3 = new fb[var2];
      fF[] var7 = new fF[var2];
      int var4 = 0;

      for (Entry var5 : var0.entrySet()) {
         var7[var4] = (fF)var5.getKey();
         var3[var4] = (fb)var5.getValue();
         var4++;
      }

      return new hD<>(false, var7, var3, var1);
   }

   private hD(boolean var1, fF<? super E>[] var2, fb<? super E>[] var3, fb<? super E> var4) {
      this.b = var1 ? hf.a(var2) : var2;
      this.c = var1 ? hf.a(var3) : var3;
      this.d = var4 == null ? ho.a() : var4;
   }

   private hD(fF<? super E>[] var1, fb<? super E>[] var2, fb<? super E> var3) {
      this(true, var1, var2, var3);
   }

   @Override
   public final void a(E var1) {
      for (int var2 = 0; var2 < this.b.length; var2++) {
         if (this.b[var2].a((E)var1)) {
            this.c[var2].a((E)var1);
            return;
         }
      }

      this.d.a((E)var1);
   }

   private fF<? super E>[] a() {
      return hf.a(this.b);
   }

   private fb<? super E>[] b() {
      return hf.a(this.c);
   }

   private fb<? super E> c() {
      return this.d;
   }
}
