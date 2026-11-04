package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class sn implements rN<sq> {
   private final rM<sq> a;
   private final double b;
   private final Map<sm, rM<sq>> c;
   private final Map<rM<sq>, List<sm>> d;

   sn(rM<sq> var1, double var2) {
      this.a = var1;
      this.b = var2;
      this.c = new IdentityHashMap<>();
      this.d = new IdentityHashMap<>();
   }

   @Override
   public final int a(rM<sq> var1) {
      return rN.a.MINUS_SUB_PLUS$672d2271;
   }

   @Override
   public final void b(rM<sq> var1) {
      this.d.put(var1, new ArrayList<>());
      rO var2;
      if ((var2 = (rO)var1.e).a != null) {
         this.a((ss)var2.a, false, var1);
      }

      if (var2.b != null) {
         this.a((ss)var2.b, true, var1);
      }
   }

   @Override
   public final void c(rM<sq> var1) {
   }

   private void a(ss var1, boolean var2, rM<sq> var3) {
      sl var4 = (sl)var1.a;

      for (sf var5 : ((sg)var1.b).b()) {
         st var6 = new st(var4.e(new si(var5.a)));
         st var7 = new st(var4.e(new si(var5.b)));
         var6.a(var4);
         var7.a(var4);
         sm var9;
         if (var2) {
            var9 = new sm(var7, var6, var5.c(), var4.c());
         } else {
            var9 = new sm(var6, var7, var5.c(), var4);
         }

         this.c.put(var9, var3);
         this.d.get(var3).add(var9);
      }
   }

   private sm a(sm var1) {
      st var10 = var1.b;
      sp var2 = var1.b.a;
      double var16 = this.b;
      sp var3 = var2;
      rM var9 = this.a;
      ArrayList var4 = new ArrayList();
      var9.a(var3, var16, var4);
      ArrayList var18 = var4;
      double var21 = this.b;
      sm var6 = null;

      for (rM var7 : var18) {
         Iterator var22 = this.d.get(var7).iterator();

         while (var22.hasNext()) {
            sm var8;
            if ((var8 = (sm)var22.next()) != var1) {
               st var23 = var8.a;
               if (var8.a.b == null) {
                  var10 = var8.a;
                  sp var26 = var8.a.a;
                  rs var24 = var8.a.a.c;
                  double var12;
                  if ((var12 = rs.a(var2.c, var24)) <= var21) {
                     var21 = var12;
                     var6 = var8;
                  }
               }
            }
         }
      }

      if (var6 == null) {
         var10 = var1.a;
         sp var28 = var1.a.a;
         rs var20 = var1.a.a.c;
         if (rs.a(var2.c, var20) <= this.b) {
            return var1;
         } else {
            throw new oX(px.OUTLINE_BOUNDARY_LOOP_OPEN);
         }
      } else {
         return var6;
      }
   }

   public final List<sm> a() {
      Iterator var1 = this.c.keySet().iterator();

      while (var1.hasNext()) {
         sm var2;
         sm var10000 = var2 = (sm)var1.next();
         sm var3 = var2;
         sn var21 = this;
         st var12 = var3.b;
         sp var4 = var3.b.a;
         double var19 = var21.b;
         sp var5 = var4;
         rM var11 = var21.a;
         ArrayList var6 = new ArrayList();
         var11.a(var5, var19, var6);
         ArrayList var24 = var6;
         double var7 = var21.b;
         sm var27 = null;

         for (rM var9 : var24) {
            Iterator var28 = var21.d.get(var9).iterator();

            while (var28.hasNext()) {
               sm var10;
               if ((var10 = (sm)var28.next()) != var3) {
                  st var29 = var10.a;
                  if (var10.a.b == null) {
                     var12 = var10.a;
                     sp var32 = var10.a.a;
                     rs var30 = var10.a.a.c;
                     double var15;
                     if ((var15 = rs.a(var4.c, var30)) <= var7) {
                        var7 = var15;
                        var27 = var10;
                     }
                  }
               }
            }
         }

         sm var10001;
         if (var27 == null) {
            var12 = var3.a;
            sp var34 = var3.a.a;
            rs var26 = var3.a.a.c;
            if (!(rs.a(var4.c, var26) <= var21.b)) {
               throw new oX(px.OUTLINE_BOUNDARY_LOOP_OPEN);
            }

            var10001 = var3;
         } else {
            var10001 = var27;
         }

         var3 = var10001;
         var2 = var10000;
         var10000.b = var3.a;
         var2.b.a(var2);
         var2.b.a(var2.d);
      }

      return new ArrayList<>(this.c.keySet());
   }
}
