package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

public abstract class rK<S extends qN, T extends qN> implements rY<S> {
   private rM<S> b;
   public final double a;
   private double c;
   private qM<S> d;

   public rK(double var1) {
      this.b = new rM<>(Boolean.TRUE);
      this.a = var1;
   }

   public rK(rM<S> var1, double var2) {
      this.b = var1;
      this.a = var2;
   }

   public rK(Collection<sb<S>> var1, double var2) {
      this.a = var2;
      if (var1.size() == 0) {
         this.b = new rM<>(Boolean.TRUE);
      } else {
         TreeSet var4;
         (var4 = new TreeSet<>(new Comparator<sb<S>>() {
            private static int a(sb<S> var0, sb<S> var1x) {
               double var2x = var0.a();
               if (var1x.a() < var2x) {
                  return -1;
               } else {
                  return var0 == var1x ? 0 : 1;
               }
            }

            // $VF: synthetic method
            @Override
            public final int compare(Object var1, Object var2) {
               sb var10000 = var1;
               var2 = var2;
               var1 = var10000;
               double var5 = var10000.a();
               if (var2.a() < var5) {
                  return -1;
               } else {
                  return var1 == var2 ? 0 : 1;
               }
            }
         })).addAll(var1);
         this.b = new rM<>();
         this.a(this.b, var4);
         this.b.a(new rN<S>() {
            @Override
            public final int a(rM<S> var1) {
               return rN.a.PLUS_SUB_MINUS$672d2271;
            }

            @Override
            public final void b(rM<S> var1) {
            }

            @Override
            public final void c(rM<S> var1) {
               if (var1.d != null) {
                  rM var2x = var1.d;
                  if (var1 != var1.d.c) {
                     Boolean var4x = Boolean.FALSE;
                     var1.e = var4x;
                     return;
                  }
               }

               Boolean var3 = Boolean.TRUE;
               var1.e = var3;
            }
         });
      }
   }

   public rK(rV<S>[] var1, double var2) {
      this.a = var2;
      if (var1 != null && var1.length != 0) {
         this.b = var1[0].e().a(false);
         rM var8 = this.b;
         Boolean var6 = Boolean.TRUE;
         rM var5 = this.b;
         this.b.e = var6;
         rw[] var7;
         int var3 = (var7 = var1).length;

         for (int var4 = 0; var4 < var3; var4++) {
            rw var9 = var7[var4];
            if (var8.a(var9)) {
               var6 = null;
               var8.e = var6;
               var6 = Boolean.FALSE;
               var5 = var8.b;
               var8.b.e = var6;
               rM var10000 = var8 = var8.c;
               var6 = Boolean.TRUE;
               var10000.e = var6;
            }
         }
      } else {
         this.b = new rM<>(Boolean.FALSE);
      }
   }

   public abstract rK<S, T> a(rM<S> var1);

   public final double c() {
      return this.a;
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   private void a(rM<S> var1, Collection<sb<S>> var2) {
      while (true) {
         Iterator var8 = var2.iterator();
         rV var3 = null;

         while (var3 == null && var8.hasNext()) {
            var3 = ((sb)var8.next()).c();
            if (!var1.a(var3.g())) {
               var3 = null;
            }
         }

         if (!var8.hasNext()) {
            return;
         }

         ArrayList var4 = new ArrayList();
         ArrayList var5 = new ArrayList();

         while (var8.hasNext()) {
            sb var6;
            sb.a var7 = (var6 = (sb)var8.next()).a(var3);
            switch (<unrepresentable>.a[var7.c() - 1]) {
               case 1:
                  var4.add(var6);
                  break;
               case 2:
                  var5.add(var6);
                  break;
               case 3:
                  var4.add(var7.a);
                  var5.add(var7.b);
            }
         }

         this.a(var1.b, var4);
         var2 = var5;
         var1 = var1.c;
         this = this;
      }
   }

   private rK<S, T> b() {
      return this.a(this.b.a());
   }

   @Override
   public final boolean d() {
      return this.c(this.b);
   }

   @Override
   public final boolean c(rM<S> var1) {
      return var1.a == null ? !(Boolean)var1.e : this.c(var1.c) && this.c(var1.b);
   }

   @Override
   public final boolean e() {
      return this.d(this.b);
   }

   @Override
   public final boolean d(rM<S> var1) {
      return var1.a == null ? (Boolean)var1.e : this.d(var1.c) && this.d(var1.b);
   }

   @Override
   public final boolean a(rY<S> var1) {
      rZ var10000 = new rZ();
      rK var3 = this;
      rY var2 = var1;
      rZ var4 = var10000;
      rM var5;
      (var5 = var2.a(false).a(var3.a(false), var4.new a(var2, var3))).a(var4.a);
      return var2.b(var5).d();
   }

   @Override
   public rQ<S> a(qM<S> var1) {
      rR var3 = new rR(var1);
      this.a(true).a(var3);
      rR var4;
      rR var10000 = var4 = var3;
      rM var2 = var4.c;
      var10000.d = BY.h(var4.d, (Boolean)var4.c.e ? -1.0 : 1.0);
      return new rQ<>(var4.a, var4.b, var4.d);
   }

   private int a(qO<S> var1) {
      return this.b((qM<S>)var1);
   }

   @Override
   public final int b(qM<S> var1) {
      return this.a(this.b, var1);
   }

   private int a(rM<S> var1, qO<S> var2) {
      return this.a(var1, (qM<S>)var2);
   }

   private int a(rM<S> var1, qM<S> var2) {
      if ((var1 = var1.a(var2, this.a)).a == null) {
         return (Boolean)var1.e ? rY.a.INSIDE$3ad793e5 : rY.a.OUTSIDE$3ad793e5;
      }

      int var3 = this.a(var1.c, var2);
      int var6 = this.a(var1.b, var2);
      return var3 == var6 ? var3 : rY.a.BOUNDARY$3ad793e5;
   }

   @Override
   public final rM<S> a(boolean var1) {
      if (var1) {
         rM var2 = this.b;
         if (this.b.a != null) {
            rM var3 = this.b;
            if (this.b.e == null) {
               this.b.a(new rP<>());
            }
         }
      }

      return this.b;
   }

   @Override
   public final double f() {
      rS var1 = new rS();
      this.a(true).a(var1);
      return var1.a;
   }

   @Override
   public final double g() {
      if (this.d == null) {
         this.a();
      }

      return this.c;
   }

   public final void a(double var1) {
      this.c = var1;
   }

   @Override
   public final qM<S> h() {
      if (this.d == null) {
         this.a();
      }

      return this.d;
   }

   private void b(qO<S> var1) {
      qO var2 = var1;
      this.d = var2;
   }

   public final void c(qM<S> var1) {
      this.d = var1;
   }

   protected abstract void a();

   @Deprecated
   @Override
   public final int a(rV<S> var1) {
      rW var2;
      (var2 = new rW<>(this)).a(this.b, var1.f());
      if (var2.a) {
         return var2.b ? sa.BOTH$7c70dd6b : sa.PLUS$7c70dd6b;
      } else {
         return var2.b ? sa.MINUS$7c70dd6b : sa.HYPER$7c70dd6b;
      }
   }

   @Override
   public final sb<S> a(sb<S> var1) {
      return this.a(this.b, var1);
   }

   private sb<S> a(rM<S> var1, sb<S> var2) {
      while (var1.a != null) {
         rV var3 = var1.a.c();
         sb.a var7;
         if ((var7 = var2.a(var3)).a != null) {
            if (var7.b != null) {
               var2 = this.a(var1.b, var7.a);
               sb var5 = this.a(var1.c, var7.b);
               if (var2 == null) {
                  return var5;
               }

               if (var5 == null) {
                  return var2;
               }

               return var2.a(var5);
            }

            var1 = var1.b;
            this = this;
         } else if (var7.b != null) {
            var1 = var1.c;
            this = this;
         } else {
            rM var10001 = var1.b;
            var2 = this.a(var1.c, var2);
            var1 = var10001;
            this = this;
         }
      }

      return (Boolean)var1.e ? var2.e() : null;
   }

   public final rK<S, T> a(sc<S, T> var1) {
      HashMap var2 = new HashMap();
      rM var8 = this.a(this.a(false), var1, var2);
      Iterator var3 = var2.entrySet().iterator();

      while (var3.hasNext()) {
         Entry var4;
         rO var5;
         if (((rM)(var4 = (Entry)var3.next()).getKey()).a != null && (var5 = (rO)((rM)var4.getKey()).e) != null) {
            rO var9 = (rO)((rM)var4.getValue()).e;

            for (rM var6 : var5.c) {
               var9.c.a((rM<S>)var2.get(var6));
            }
         }
      }

      return this.a(var8);
   }

   private rM<S> a(rM<S> var1, sc<S, T> var2, Map<rM<S>, rM<S>> var3) {
      rM var8;
      if (var1.a == null) {
         var8 = new rM(var1.e);
      } else {
         sb var4 = var1.a;
         var4 = ((rL)var1.a).a(var2);
         rO var5;
         if ((var5 = (rO)var1.e) != null) {
            rL var6 = var5.a == null ? null : ((rL)var5.a).a(var2);
            rL var10 = var5.b == null ? null : ((rL)var5.b).a(var2);
            var5 = new rO<>(var6, var10, new rX<>());
         }

         var8 = new rM<>(var4, this.a(var1.b, var2, var3), this.a(var1.c, var2, var3), var5);
      }

      var3.put(var1, var8);
      return var8;
   }

   // $VF: synthetic method
   @Override
   public final rK i() {
      rK var1 = this;
      return this.a(var1.b.a());
   }

   // $VF: synthetic method
   @Override
   public rK b(rM var1) {
      return this.a(var1);
   }
}
