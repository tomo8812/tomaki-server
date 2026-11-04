package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Map.Entry;

public abstract class kG<K, V> extends AbstractMap<K, V> implements Serializable, fS<K, V> {
   private static final long b = 5826987063535505652L;
   final kI<? super K> a;

   protected kG(kI<? super K> var1) {
      if (var1 == null) {
         throw new NullPointerException("keyAnalyzer");
      }

      this.a = var1;
   }

   protected final kI<? super K> a() {
      return this.a;
   }

   @Override
   public String toString() {
      StringBuilder var1;
      (var1 = new StringBuilder()).append("Trie[").append(this.size()).append("]={\n");

      for (Entry var3 : this.entrySet()) {
         var1.append("  ").append(var3).append("\n");
      }

      var1.append("}\n");
      return var1.toString();
   }

   static K b(Object var0) {
      return (K)var0;
   }

   final int e(K var1) {
      return var1 == null ? 0 : this.a.a((K)var1);
   }

   private int b() {
      return this.a.a();
   }

   final boolean a(K var1, int var2, int var3) {
      return var1 == null ? false : this.a.a((K)var1, var2, var3);
   }

   final int a(K var1, K var2) {
      return this.a.a((K)var1, 0, this.e((K)var1), (K)var2, 0, this.e((K)var2));
   }

   final boolean b(K var1, K var2) {
      if (var1 == null) {
         return var2 == null;
      } else {
         return var2 == null ? false : this.a.compare((K)var1, (K)var2) == 0;
      }
   }

   static boolean c(Object var0, Object var1) {
      return var0 == null ? var1 == null : var0.equals(var1);
   }

   abstract static class a<K, V> implements Serializable, Entry<K, V> {
      private static final long c = -944364551314110330L;
      protected K a;
      protected V b;

      private a(K var1) {
         this.a = (K)var1;
      }

      public a(K var1, V var2) {
         this.a = (K)var1;
         this.b = (V)var2;
      }

      public final V a(K var1, V var2) {
         this.a = (K)var1;
         return this.setValue((V)var2);
      }

      @Override
      public K getKey() {
         return this.a;
      }

      @Override
      public V getValue() {
         return this.b;
      }

      @Override
      public V setValue(V value) {
         Object var2 = this.b;
         this.b = value;
         return (V)var2;
      }

      @Override
      public int hashCode() {
         return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^ (this.getValue() == null ? 0 : this.getValue().hashCode());
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         }

         if (!(o instanceof Entry)) {
            return false;
         }

         o = (Entry)o;
         return kG.c(this.a, o.getKey()) && kG.c(this.b, o.getValue());
      }

      @Override
      public String toString() {
         return this.a + "=" + this.b;
      }
   }
}
