package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public abstract class jM<K, V> extends jK<K, V> implements fM<K, V> {
   protected jM() {
   }

   protected jM(Map<K, ? extends Set<V>> var1) {
      super(var1);
   }

   @Override
   protected final Map<K, Set<V>> j() {
      return (Map<K, Set<V>>)super.j();
   }

   protected abstract Set<V> k();

   @Override
   public final Set<V> a(K var1) {
      return this.h((K)var1);
   }

   private Set<V> h(K var1) {
      return new jM.a(var1);
   }

   @Override
   public final Set<V> b(Object var1) {
      return fL.a((Set<V>)super.j().remove(var1));
   }

   // $VF: synthetic method
   @Override
   public final Collection f(Object var1) {
      Object var2 = var1;
      return fL.a((Set<V>)super.j().remove(var2));
   }

   // $VF: synthetic method
   @Override
   final Collection g(Object var1) {
      return this.h((K)var1);
   }

   // $VF: synthetic method
   @Override
   public final Collection e(Object var1) {
      Object var2 = var1;
      return this.h((K)var2);
   }

   // $VF: synthetic method
   @Override
   protected Collection l() {
      return this.k();
   }

   class a extends jK<K, V>.h implements Set<V> {
      public a(K var2) {
         super(var2);
      }

      @Override
      public final boolean equals(Object other) {
         Set var2;
         if ((var2 = (Set)this.a()) == null) {
            return Collections.emptySet().equals(other);
         }

         if (!(other instanceof Set)) {
            return false;
         }

         Set var3 = (Set)other;
         return fL.a(var2, var3);
      }

      @Override
      public final int hashCode() {
         return fL.a((Collection)((Set)this.a()));
      }
   }
}
