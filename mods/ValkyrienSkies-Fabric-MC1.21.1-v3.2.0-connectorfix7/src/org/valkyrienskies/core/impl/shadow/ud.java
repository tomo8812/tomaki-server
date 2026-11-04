package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class ud implements Serializable, Iterable<ue> {
   private static final long a = 20130207L;
   private final ConcurrentHashMap<Long, ue> b = new ConcurrentHashMap<>();
   private final AtomicLong c;
   private final int d;
   private final ConcurrentHashMap<Long, Set<Long>> e = new ConcurrentHashMap<>();

   ud(long var1, int var3, ue[] var4, long[][] var5) {
      int var6;
      if ((var6 = var4.length) != var5.length) {
         throw new oX();
      }

      for (int var7 = 0; var7 < var6; var7++) {
         ue var8;
         long var9;
         if ((var9 = (var8 = var4[var7]).a) >= var1) {
            throw new oX();
         }

         this.b.put(var9, var8);
         this.e.put(var9, new HashSet<>());
      }

      for (int var13 = 0; var13 < var6; var13++) {
         ue var16;
         long var14 = (var16 = var4[var13]).a;
         Set var10 = this.e.get(var14);
         long[] var15;
         int var17 = (var15 = var5[var13]).length;

         for (int var11 = 0; var11 < var17; var11++) {
            Long var12 = var15[var11];
            if (this.b.get(var12) == null) {
               throw new oX();
            }

            a(var10, var12);
         }
      }

      this.c = new AtomicLong(var1);
      this.d = var3;
   }

   public ud(long var1, int var3) {
      this.c = new AtomicLong(var1);
      this.d = var3;
   }

   public final synchronized ud a() {
      ud var1 = new ud(this.c.get(), this.d);

      for (Entry var3 : this.b.entrySet()) {
         var1.b.put((Long)var3.getKey(), ((ue)var3.getValue()).a());
      }

      for (Entry var5 : this.e.entrySet()) {
         var1.e.put((Long)var5.getKey(), new HashSet<>((Collection<? extends Long>)var5.getValue()));
      }

      return var1;
   }

   @Override
   public final Iterator<ue> iterator() {
      return this.b.values().iterator();
   }

   private Collection<ue> a(Comparator<ue> var1) {
      ArrayList var2;
      (var2 = new ArrayList()).addAll(this.b.values());
      Collections.sort(var2, var1);
      return var2;
   }

   public final long a(double[] var1) {
      if (var1.length != this.d) {
         throw new oS(var1.length, this.d);
      }

      long var2 = this.c.getAndIncrement();
      this.b.put(var2, new ue(var2, var1));
      this.e.put(var2, new HashSet<>());
      return var2;
   }

   private void b(ue var1) {
      ue var2 = var1;

      for (ue var3 : this.a(var2, (Iterable<ue>)null)) {
         ue var5 = var1;
         ue var4 = var3;
         ud var16 = this;
         long var8 = var4.a;
         long var10 = var5.a;
         if (var4 != var16.a(var8)) {
            throw new NoSuchElementException(Long.toString(var8));
         }

         if (var5 != var16.a(var10)) {
            throw new NoSuchElementException(Long.toString(var10));
         }

         Set var10000 = var16.e.get(var8);
         long var13 = var10;
         var10000.remove(var13);
      }

      this.b.remove(var1.a);
   }

   private int b() {
      return this.d;
   }

   public final void a(ue var1, ue var2) {
      long var3 = var1.a;
      long var5 = var2.a;
      if (var1 != this.a(var3)) {
         throw new NoSuchElementException(Long.toString(var3));
      }

      if (var2 != this.a(var5)) {
         throw new NoSuchElementException(Long.toString(var5));
      }

      a(this.e.get(var3), var5);
   }

   private static void a(Set<Long> var0, long var1) {
      var0.add(var1);
   }

   private void b(ue var1, ue var2) {
      long var3 = var1.a;
      long var5 = var2.a;
      if (var1 != this.a(var3)) {
         throw new NoSuchElementException(Long.toString(var3));
      }

      if (var2 != this.a(var5)) {
         throw new NoSuchElementException(Long.toString(var5));
      }

      b(this.e.get(var3), var5);
   }

   private static void b(Set<Long> var0, long var1) {
      var0.remove(var1);
   }

   public final ue a(long var1) {
      ue var3;
      if ((var3 = this.b.get(var1)) == null) {
         throw new NoSuchElementException(Long.toString(var1));
      } else {
         return var3;
      }
   }

   private Collection<ue> a(Iterable<ue> var1) {
      return this.a(var1, null);
   }

   public final Collection<ue> a(Iterable<ue> var1, Iterable<ue> var2) {
      HashSet var3 = new HashSet();

      for (ue var4 : var1) {
         var3.addAll(this.e.get(var4.a));
      }

      if (var2 != null) {
         for (ue var9 : var2) {
            var3.remove(var9.a);
         }
      }

      var1 = new ArrayList();

      for (Long var8 : var3) {
         var1.add(this.a(var8));
      }

      return var1;
   }

   public final Collection<ue> a(ue var1) {
      return this.a(var1, (Iterable<ue>)null);
   }

   public final Collection<ue> a(ue var1, Iterable<ue> var2) {
      Set var4 = this.e.get(var1.a);
      var2 = new ArrayList();

      for (Long var3 : var4) {
         var2.add(this.a(var3));
      }

      return var2;
   }

   private Long c() {
      return this.c.getAndIncrement();
   }

   private static void d() {
      throw new IllegalStateException();
   }

   private ud.b e() {
      ue[] var1;
      long[][] var2 = new long[(var1 = this.b.values().toArray(new ue[0])).length][];

      for (int var3 = 0; var3 < var1.length; var3++) {
         ue var4 = var1[var3];
         Collection var8;
         long[] var5 = new long[(var8 = this.a(var4, (Iterable<ue>)null)).size()];
         int var6 = 0;

         for (ue var7 : var8) {
            var5[var6] = var7.a;
            var6++;
         }

         var2[var3] = var5;
      }

      return new ud.b(this.c.get(), this.d, var1, var2);
   }

   public static class a implements Serializable, Comparator<ue> {
      private static final long a = 20130207L;

      private static int a(ue var0, ue var1) {
         long var2 = var0.a;
         long var4 = var1.a;
         if (var2 < var4) {
            return -1;
         } else {
            return var2 > var4 ? 1 : 0;
         }
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public final int compare(Object var1, Object var2) {
         ue var10000 = (ue)var1;
         var2 = var2;
         long var5 = var10000.a;
         long var7 = var2.a;
         if (var5 < var7) {
            return -1;
         } else {
            return var5 > var7 ? 1 : 0;
         }
      }
   }

   static class b implements Serializable {
      private static final long a = 20130207L;
      private final long b;
      private final int c;
      private final ue[] d;
      private final long[][] e;

      b(long var1, int var3, ue[] var4, long[][] var5) {
         this.b = var1;
         this.c = var3;
         this.d = var4;
         this.e = var5;
      }

      private ud a() {
         return new ud(this.b, this.c, this.d, this.e);
      }
   }
}
