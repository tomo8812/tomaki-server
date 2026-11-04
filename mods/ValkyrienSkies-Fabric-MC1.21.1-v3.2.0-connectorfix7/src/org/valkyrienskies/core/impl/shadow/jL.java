package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public abstract class jL<K, V> implements Serializable, fA<K, V> {
   private static final long a = 20150612L;
   private final fA<K, V> b;

   protected jL(fA<K, V> var1) {
      if (var1 == null) {
         throw new NullPointerException("MultiValuedMap must not be null.");
      }

      this.b = var1;
   }

   protected final fA<K, V> j() {
      return this.b;
   }

   @Override
   public final int a() {
      return this.b.a();
   }

   @Override
   public final boolean b() {
      return this.b.b();
   }

   @Override
   public final boolean c(Object var1) {
      return this.b.c(var1);
   }

   @Override
   public final boolean d(Object var1) {
      return this.b.d(var1);
   }

   @Override
   public final boolean a(Object var1, Object var2) {
      return this.b.a(var1, var2);
   }

   @Override
   public Collection<V> e(K var1) {
      return this.b.e((K)var1);
   }

   @Override
   public Collection<V> f(Object var1) {
      return this.b.f(var1);
   }

   @Override
   public boolean c(Object var1, Object var2) {
      return this.b.c(var1, var2);
   }

   @Override
   public void c() {
      this.b.c();
   }

   @Override
   public boolean b(K var1, V var2) {
      return this.b.b((K)var1, (V)var2);
   }

   @Override
   public Set<K> f() {
      return this.b.f();
   }

   @Override
   public Collection<Entry<K, V>> d() {
      return this.b.d();
   }

   @Override
   public fy<K> e() {
      return this.b.e();
   }

   @Override
   public Collection<V> g() {
      return this.b.g();
   }

   @Override
   public Map<K, Collection<V>> h() {
      return this.b.h();
   }

   @Override
   public boolean a(K var1, Iterable<? extends V> var2) {
      return this.b.a((K)var1, var2);
   }

   @Override
   public boolean a(Map<? extends K, ? extends V> var1) {
      return this.b.a(var1);
   }

   @Override
   public boolean a(fA<? extends K, ? extends V> var1) {
      return this.b.a(var1);
   }

   @Override
   public fu<K, V> i() {
      return this.b.i();
   }

   @Override
   public boolean equals(Object object) {
      return object == this ? true : this.b.equals(object);
   }

   @Override
   public int hashCode() {
      return this.b.hashCode();
   }

   @Override
   public String toString() {
      return this.b.toString();
   }
}
