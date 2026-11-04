package org.valkyrienskies.core.impl.shadow;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class fj<E> implements Iterable<E> {
   public final Iterable<E> a;

   private static <T> fj<T> b() {
      return fp.a;
   }

   private static <T> fj<T> a(T var0) {
      return a((Iterable<T>)fq.a(new iu<>(var0, false)));
   }

   private static <T> fj<T> a(T... var0) {
      return a(Arrays.asList((T[])var0));
   }

   public static <T> fj<T> a(Iterable<T> var0) {
      fp.i(var0);
      return var0 instanceof fj ? (fj)var0 : new fj(var0);
   }

   fj() {
      this.a = this;
   }

   private fj(Iterable<E> var1) {
      this.a = var1;
   }

   private fj<E> b(E... var1) {
      List var2 = Arrays.asList(var1);
      return a(fp.a(this.a, var2));
   }

   private fj<E> b(Iterable<? extends E> var1) {
      return a(fp.a(this.a, var1));
   }

   private fj<E> c(Iterable<? extends E> var1) {
      return a(fp.b(this.a, var1));
   }

   private fj<E> a(Iterable<? extends E> var1, Comparator<? super E> var2) {
      return a(fp.a(var2, this.a, var1));
   }

   private fj<E> c() {
      return a(fp.g(this.a));
   }

   private fj<E> a(fF<? super E> var1) {
      return a(fp.a(this.a, var1));
   }

   private fj<E> a(long var1) {
      return a(fp.a(this.a, var1));
   }

   public final fj<E> a() {
      return a(fp.a(this.a));
   }

   private fj<E> d() {
      return a(fp.b(this.a));
   }

   private fj<E> b(long var1) {
      return a(fp.b(this.a, var1));
   }

   public final <O> fj<O> a(fQ<? super E, ? extends O> var1) {
      return a(fp.a(this.a, var1));
   }

   private fj<E> e() {
      return a(fp.c(this.a));
   }

   private fj<E> f() {
      return a(fp.d(this.a));
   }

   private fj<E> d(Iterable<? extends E> var1) {
      return a(fp.c(this.a, var1));
   }

   private fj<E> a(Iterable<? extends E>... var1) {
      return a(fp.a(this.a, var1));
   }

   @Override
   public Iterator<E> iterator() {
      return this.a.iterator();
   }

   private Enumeration<E> g() {
      return fq.b(this.iterator());
   }

   private boolean b(fF<? super E> var1) {
      return fp.d(this.a, var1);
   }

   private boolean c(fF<? super E> var1) {
      return fp.e(this.a, var1);
   }

   private boolean h() {
      return fp.e(this.a);
   }

   private boolean b(Object var1) {
      return fp.a(this.a, var1);
   }

   private void a(fb<? super E> var1) {
      fp.a(this.a, var1);
   }

   private E a(int var1) {
      return fp.a(this.a, var1);
   }

   private int i() {
      return fp.f(this.a);
   }

   private void a(Collection<? super E> var1) {
      if (var1 == null) {
         throw new NullPointerException("Collection must not be null");
      }

      fd.a(var1, this.a);
   }

   private E[] a(Class<E> var1) {
      return fq.a(this.iterator(), var1);
   }

   private List<E> j() {
      return fp.g(this.a);
   }

   @Override
   public String toString() {
      return fp.h(this.a);
   }
}
