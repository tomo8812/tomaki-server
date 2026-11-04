package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public final class jP<K, V> extends jL<K, V> {
   private static final long a = 20150612L;
   private final fQ<? super K, ? extends K> b;
   private final fQ<? super V, ? extends V> c;

   public static <K, V> jP<K, V> a(fA<K, V> var0, fQ<? super K, ? extends K> var1, fQ<? super V, ? extends V> var2) {
      return new jP<>(var0, var1, var2);
   }

   private static <K, V> jP<K, V> b(fA<K, V> var0, fQ<? super K, ? extends K> var1, fQ<? super V, ? extends V> var2) {
      jP var4 = new jP(var0, var1, var2);
      if (!var0.b()) {
         var0 = new jN(var0);
         var4.c();
         var4.a(var0);
      }

      return var4;
   }

   private jP(fA<K, V> var1, fQ<? super K, ? extends K> var2, fQ<? super V, ? extends V> var3) {
      super(var1);
      this.b = var2;
      this.c = var3;
   }

   private K a(K var1) {
      return (K)(this.b == null ? var1 : this.b.a((K)var1));
   }

   private V b(V var1) {
      return (V)(this.c == null ? var1 : this.c.a((V)var1));
   }

   @Override
   public final boolean b(K var1, V var2) {
      fA var10000 = this.j();
      Object var10001 = this.a((K)var1);
      var2 = var2;
      var1 = this;
      return var10000.b(var10001, this.c == null ? var2 : var1.c.a((V)var2));
   }

   @Override
   public final boolean a(K var1, Iterable<? extends V> var2) {
      if (var2 == null) {
         throw new NullPointerException("Values must not be null.");
      }

      fj var10000 = fj.a(var2);
      fQ var3 = this.c;
      Iterator var4;
      return (var4 = fj.a(fp.a(var10000.a, var3)).iterator()).hasNext() && fd.a(this.j().e(this.a((K)var1)), var4);
   }

   @Override
   public final boolean a(Map<? extends K, ? extends V> var1) {
      if (var1 == null) {
         throw new NullPointerException("Map must not be null.");
      }

      boolean var2 = false;

      for (Entry var3 : var1.entrySet()) {
         var2 |= this.b((K)var3.getKey(), (V)var3.getValue());
      }

      return var2;
   }

   @Override
   public final boolean a(fA<? extends K, ? extends V> var1) {
      if (var1 == null) {
         throw new NullPointerException("Map must not be null.");
      }

      boolean var2 = false;

      for (Entry var3 : var1.d()) {
         var2 |= this.b((K)var3.getKey(), (V)var3.getValue());
      }

      return var2;
   }
}
