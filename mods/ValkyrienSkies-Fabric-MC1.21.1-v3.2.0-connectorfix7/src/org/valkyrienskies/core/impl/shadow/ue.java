package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public final class ue implements Serializable {
   private static final long b = 20130207L;
   public final long a;
   private final int c;
   private final AtomicReference<double[]> d;
   private final AtomicLong e = new AtomicLong(0L);
   private final AtomicLong f = new AtomicLong(0L);

   ue(long var1, double[] var3) {
      this.a = var1;
      this.c = var3.length;
      this.d = new AtomicReference<>((double[])var3.clone());
   }

   public final synchronized ue a() {
      ue var1;
      (var1 = new ue(this.a, this.c())).e.set(this.e.get());
      var1.f.set(this.f.get());
      return var1;
   }

   public final long b() {
      return this.a;
   }

   private int d() {
      return this.c;
   }

   public final double[] c() {
      return (double[])this.d.get().clone();
   }

   public final boolean a(double[] var1, double[] var2) {
      if (var2.length != this.c) {
         throw new oS(var2.length, this.c);
      }

      double[] var3 = this.d.get();
      double[] var5 = var1;
      double[] var4 = var3;
      ue var7 = this;
      if (var5.length != var7.c) {
         throw new oS(var5.length, var7.c);
      }

      int var6 = 0;

      boolean var10000;
      while (true) {
         if (var6 >= var7.c) {
            var10000 = true;
            break;
         }

         if (!Cq.a(var4[var6], var5[var6])) {
            var10000 = false;
            break;
         }

         var6++;
      }

      if (!var10000) {
         return false;
      } else {
         this.e.incrementAndGet();
         if (this.d.compareAndSet(var3, (double[])var2.clone())) {
            this.f.incrementAndGet();
            return true;
         } else {
            return false;
         }
      }
   }

   private long e() {
      return this.e.get();
   }

   private long f() {
      return this.f.get();
   }

   private boolean b(double[] var1, double[] var2) {
      if (var2.length != this.c) {
         throw new oS(var2.length, this.c);
      }

      for (int var3 = 0; var3 < this.c; var3++) {
         if (!Cq.a(var1[var3], var2[var3])) {
            return false;
         }
      }

      return true;
   }

   private static void g() {
      throw new IllegalStateException();
   }

   private ue.a h() {
      return new ue.a(this.a, this.d.get());
   }

   static class a implements Serializable {
      private static final long a = 20130207L;
      private final double[] b;
      private final long c;

      a(long var1, double[] var3) {
         this.c = var1;
         this.b = var3;
      }

      private ue a() {
         return new ue(this.c, this.b);
      }
   }
}
