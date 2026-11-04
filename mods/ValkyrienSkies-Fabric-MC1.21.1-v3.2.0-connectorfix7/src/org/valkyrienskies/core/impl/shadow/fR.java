package org.valkyrienskies.core.impl.shadow;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

public final class fR {
   private fR() {
   }

   private static <I, O> fQ<I, O> c() {
      return hb.a();
   }

   private static <I, O> fQ<I, O> d() {
      return gV.a();
   }

   public static <T> fQ<T, T> a() {
      return hp.a();
   }

   private static <T> fQ<T, T> e() {
      return gR.a();
   }

   private static <I, O> fQ<I, O> a(O var0) {
      return gV.b((O)var0);
   }

   private static <T> fQ<T, T> a(fb<? super T> var0) {
      return gS.a(var0);
   }

   private static <T> fQ<T, Boolean> a(fF<? super T> var0) {
      return hA.a(var0);
   }

   private static <I, O> fQ<I, O> a(fh<? extends O> var0) {
      return hc.a(var0);
   }

   private static <T> fQ<T, T> a(fQ<? super T, ? extends T>... var0) {
      return gQ.a(var0);
   }

   private static <T> fQ<T, T> a(Collection<? extends fQ<? super T, ? extends T>> var0) {
      return gQ.a(var0);
   }

   private static <T> fQ<T, T> a(fF<? super T> var0, fQ<? super T, ? extends T> var1) {
      return hi.a(var0, var1);
   }

   private static <I, O> fQ<I, O> a(fF<? super I> var0, fQ<? super I, ? extends O> var1, fQ<? super I, ? extends O> var2) {
      return hi.a(var0, var1, var2);
   }

   @Deprecated
   private static <I, O> fQ<I, O> b(fF<? super I> var0, fQ<? super I, ? extends O> var1, fQ<? super I, ? extends O> var2) {
      return hE.a(new fF[]{var0}, new fQ[]{var1}, var2);
   }

   private static <I, O> fQ<I, O> a(fF<? super I>[] var0, fQ<? super I, ? extends O>[] var1) {
      return hE.a(var0, var1, null);
   }

   private static <I, O> fQ<I, O> a(fF<? super I>[] var0, fQ<? super I, ? extends O>[] var1, fQ<? super I, ? extends O> var2) {
      return hE.a(var0, var1, var2);
   }

   private static <I, O> fQ<I, O> a(Map<fF<I>, fQ<I, O>> var0) {
      return hE.a(var0);
   }

   private static <I, O> fQ<I, O> b(Map<I, fQ<I, O>> var0) {
      if (var0 == null) {
         throw new NullPointerException("The object and transformer map must not be null");
      }

      fQ var1 = (fQ)var0.remove(null);
      int var2;
      fQ[] var3 = new fQ[var2 = var0.size()];
      fF[] var8 = new fF[var2];
      int var4 = 0;

      for (Entry var5 : var0.entrySet()) {
         var8[var4] = gX.b(var5.getKey());
         var3[var4++] = (fQ)var5.getValue();
      }

      fQ var9 = var1;
      fQ[] var7 = var3;
      return hE.a(var8, var7, var9);
   }

   private static <T> fQ<Class<? extends T>, T> f() {
      return hl.a();
   }

   private static <T> fQ<Class<? extends T>, T> a(Class<?>[] var0, Object[] var1) {
      return hl.a(var0, var1);
   }

   private static <I, O> fQ<I, O> c(Map<? super I, ? extends O> var0) {
      return hn.a(var0);
   }

   private static <I, O> fQ<I, O> a(String var0) {
      return hm.a(var0, null, null);
   }

   private static <I, O> fQ<I, O> a(String var0, Class<?>[] var1, Object[] var2) {
      return hm.a(var0, var1, var2);
   }

   public static <T> fQ<T, String> b() {
      return hC.a();
   }
}
