package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public final class sr extends rK<sq, sj> {
   private List<st> b;

   public sr(double var1) {
      super(var1);
   }

   private sr(rs var1, double var2) {
      super(new rM<>(new sl(var1, var2).h(), new rM<>(Boolean.FALSE), new rM<>(Boolean.TRUE), null), var2);
   }

   private sr(rs var1, rs var2, double var3, int var5, double var6) {
      this(var6, a(var1, var2, var3, var5));
   }

   public sr(rM<sq> var1, double var2) {
      super(var1, var2);
   }

   private sr(Collection<sb<sq>> var1, double var2) {
      super(var1, var2);
   }

   private sr(double var1, sp... var3) {
      super(a(var1, var3), var1);
   }

   private static sp[] a(rs var0, rs var1, double var2, int var4) {
      sp[] var5 = new sp[var4];
      rk var7 = new rk(rs.c(var0, var1), var2, rl.VECTOR_OPERATOR$59e4743c);
      var5[0] = new sp(var7.a(var0));
      rk var6 = new rk(var0, (Math.PI * 2) / var4, rl.VECTOR_OPERATOR$59e4743c);

      for (int var8 = 1; var8 < var4; var8++) {
         sp var9;
         var5[var8] = new sp(var6.a((var9 = var5[var8 - 1]).c));
      }

      return var5;
   }

   private static rM<sq> a(double var0, sp... var2) {
      int var3;
      if ((var3 = var2.length) == 0) {
         return new rM<>(Boolean.TRUE);
      }

      st[] var4 = new st[var3];

      for (int var5 = 0; var5 < var3; var5++) {
         var4[var5] = new st(var2[var5]);
      }

      ArrayList var15 = new ArrayList(var3);
      st var14 = var4[var3 - 1];

      for (int var6 = 0; var6 < var3; var6++) {
         st var7 = var14;
         var14 = var4[var6];
         st var9 = var14;
         Iterator var8 = var7.d.iterator();

         sl var10000;
         label50:
         while (true) {
            if (!var8.hasNext()) {
               var10000 = null;
               break;
            }

            sl var10 = (sl)var8.next();

            for (sl var12 : var9.d) {
               if (var10 == var12) {
                  var10000 = var10;
                  break label50;
               }
            }
         }

         sl var17 = var10000;
         if (var10000 == null) {
            var17 = new sl(var7.a, var14.a, var0);
         }

         sp var13 = var7.a;
         var13 = var14.a;
         var15.add(new sm(var7, var14, rs.a(var7.a.c, var14.a.c), var17));
         st[] var18 = var4;
         int var19 = var4.length;

         for (int var20 = 0; var20 < var19; var20++) {
            st var21;
            if ((var21 = var18[var20]) != var7 && var21 != var14 && BY.w(var17.a(var21.a)) <= var0) {
               var21.a(var17);
            }
         }
      }

      rM var16 = new rM();
      a(var0, var16, var15);
      return var16;
   }

   private static void a(double var0, rM<sq> var2, List<sm> var3) {
      int var4 = 0;
      sm var5 = null;

      while (var5 == null && var4 < var3.size()) {
         var5 = (sm)var3.get(var4++);
         if (!var2.a(var5.d)) {
            var5 = null;
         }
      }

      if (var5 == null) {
         rM var14 = var2.d;
         if (var2.d != null && var2 != var14.c) {
            Boolean var12 = Boolean.FALSE;
            var2.e = var12;
         } else {
            Boolean var11 = Boolean.TRUE;
            var2.e = var11;
         }
      } else {
         ArrayList var13 = new ArrayList();
         ArrayList var6 = new ArrayList();
         Iterator var8 = var3.iterator();

         while (var8.hasNext()) {
            sm var7;
            if ((var7 = (sm)var8.next()) != var5) {
               var7.a(var5.d, var13, var6);
            }
         }

         if (!var13.isEmpty()) {
            a(var0, var2.b, var13);
         } else {
            Boolean var9 = Boolean.FALSE;
            rM var15 = var2.b;
            var2.b.e = var9;
         }

         if (!var6.isEmpty()) {
            a(var0, var2.c, var6);
         } else {
            Boolean var10 = Boolean.TRUE;
            rM var16 = var2.c;
            var2.c.e = var10;
         }
      }
   }

   private sr e(rM<sq> var1) {
      return new sr(var1, this.c());
   }

   @Override
   public final void a() {
      rM var1;
      sr var10000;
      sp var10001;
      if ((var1 = this.a(true)).a == null) {
         if (var1.a == null && (Boolean)var1.e) {
            this.a(Math.PI * 4);
            var10000 = this;
            var10001 = new sp(0.0, 0.0);
         } else {
            this.a(0.0);
            var10000 = this;
            var10001 = sp.b;
         }
      } else {
         so var2 = new so(this.c());
         var1.a(var2);
         this.a(var2.a);
         var10000 = this;
         so var3 = var2;
         var10001 = var2.b.f() == 0.0 ? sp.b : new sp(var3.b);
      }

      var10000.c(var10001);
   }

   public final List<st> b() {
      if (this.b == null) {
         if (this.a(false).a == null) {
            this.b = Collections.emptyList();
         } else {
            rM var1 = this.a(true);
            sn var2 = new sn(var1, this.c());
            var1.a(var2);
            List var5 = var2.a();
            this.b = new ArrayList<>();

            while (!var5.isEmpty()) {
               sm var6;
               st var3 = (var6 = (sm)var5.get(0)).a;
               this.b.add(var3);

               do {
                  Iterator var4 = var5.iterator();

                  while (var4.hasNext()) {
                     if (var4.next() == var6) {
                        var4.remove();
                        break;
                     }
                  }

                  st var7 = var6.b;
               } while ((var6 = var6.b.c).a != var3);
            }
         }
      }

      return Collections.unmodifiableList(this.b);
   }

   private qR<sq, sp> j() {
      if (this.d()) {
         return new qR<>(sp.a, Double.NEGATIVE_INFINITY);
      }

      if (this.e()) {
         return new qR<>(sp.a, Double.POSITIVE_INFINITY);
      }

      rM var1 = this.a(false);
      if (this.c(var1.c) && this.d(var1.b)) {
         sl var42 = (sl)var1.a.c();
         return new qR<>(new sp(var42.a).d(), Math.PI / 2);
      }

      if (this.d(var1.c) && this.c(var1.b)) {
         sl var41 = (sl)var1.a.c();
         return new qR<>(new sp(var41.a), Math.PI / 2);
      }

      sr var2 = this;
      so var3 = new so(var2.c());
      var2.a(true).a(var3);
      List var37 = var3.c;

      for (st var51 : this.b()) {
         int var4 = 0;

         for (st var5 = var51; var4 == 0 || var5 != var51; var5 = var5.c.b) {
            var4++;
            sp var12 = var5.a;
            var37.add(var5.a.c);
            sm var68 = var5.c;
         }
      }

      ro var44 = new ro();
      qT var10000 = new qT<>(this.c(), var44);
      List var52 = var37;
      qT var45 = var10000;
      qR var69;
      if (var52 != null && var52.iterator().hasNext()) {
         List var38 = var52;
         qT var46 = var45;
         qM var53 = (qM)var38.iterator().next();
         ArrayList var58 = new ArrayList(var53.a().a() + 1);
         ArrayList var61 = new ArrayList(var53.a().a() + 1);
         var58.add(var53);
         qR var54 = var46.a(var58, var58.size(), var61);

         while (true) {
            qR var7 = var54;
            List var6 = var38;
            qM var65 = var7.a;
            qM var8 = null;
            double var31 = -1.0;
            Iterator var63 = var6.iterator();

            while (var63.hasNext()) {
               qM var9;
               double var35;
               if ((var35 = (var9 = (qM)var63.next()).a(var65)) > var31) {
                  var8 = var9;
                  var31 = var35;
               }
            }

            qM var64 = var8;
            if (var54.a(var64, var46.a)) {
               var69 = var54;
               break;
            }

            var61.clear();
            var61.add(var64);
            var7 = var54;
            if ((var54 = var46.a(var58, var58.size(), var61)).b < var7.b) {
               throw new oY();
            }

            var58.add(0, var64);
            var58.subList(var54.c.length, var58.size()).clear();
         }
      } else {
         var69 = var45.b.a(new ArrayList());
      }

      qR var59 = var69;
      rs[] var62 = (rs[])var69.c();
      double var67 = var59.b;
      double var10;
      if ((var10 = ((rs)var59.a).e()) < this.c()) {
         qR var40 = new qR<>(sp.a, Double.POSITIVE_INFINITY);
         var2 = this;
         sr var55 = (sr)new rZ<sq>().a(var2);
         so var49 = new so(var2.c());
         var55.a(true).a(var49);

         for (rs var56 : var49.c) {
            sp var57 = new sp(var56);
            rQ var60 = this.a(var57);
            if (Math.PI - var60.b < var40.b) {
               var40 = new qR<>(var57.d(), Math.PI - var60.b, (sp)var60.a);
            }
         }

         return var40;
      } else {
         sp[] var39 = new sp[var62.length];

         for (int var47 = 0; var47 < var62.length; var47++) {
            var39[var47] = new sp(var62[var47]);
         }

         return new qR<>(new sp((rs)var59.a), BY.s((1.0 + var10 * var10 - var67 * var67) / (var10 * 2.0)), var39);
      }
   }

   private List<rs> k() {
      so var1 = new so(this.c());
      this.a(true).a(var1);
      return var1.c;
   }

   private List<rs> l() {
      sr var1 = (sr)new rZ<sq>().a(this);
      so var2 = new so(this.c());
      var1.a(true).a(var2);
      return var2.c;
   }

   // $VF: synthetic method
   @Override
   public final rK a(rM var1) {
      return this.e(var1);
   }

   // $VF: synthetic method
   @Override
   public final rK b(rM var1) {
      return this.e(var1);
   }
}
