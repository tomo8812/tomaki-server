package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class qT<S extends qN, P extends qM<S>> implements qQ<S, P> {
   public final double a;
   public final qS<S, P> b;

   public qT(double var1, qS<S, P> var3) {
      this.a = var1;
      this.b = var3;
   }

   @Override
   public final qR<S, P> a(Iterable<P> var1) {
      if (var1 != null && var1.iterator().hasNext()) {
         Iterable var2 = var1;
         qT var20 = this;
         qM var3 = (qM)var2.iterator().next();
         ArrayList var4 = new ArrayList(var3.a().a() + 1);
         ArrayList var5 = new ArrayList(var3.a().a() + 1);
         var4.add(var3);
         qR var21 = var20.a(var4, var4.size(), var5);

         while (true) {
            qR var7 = var21;
            Iterable var6 = var2;
            qM var24 = var7.a;
            qM var8 = null;
            double var14 = -1.0;
            Iterator var22 = var6.iterator();

            while (var22.hasNext()) {
               qM var9;
               double var18;
               if ((var18 = (var9 = (qM)var22.next()).a(var24)) > var14) {
                  var8 = var9;
                  var14 = var18;
               }
            }

            qM var23 = var8;
            if (var21.a((P)var23, var20.a)) {
               return var21;
            }

            var5.clear();
            var5.add(var23);
            var7 = var21;
            if ((var21 = var20.a(var4, var4.size(), var5)).b < var7.b) {
               throw new oY();
            }

            var4.add(0, var23);
            var4.subList(var21.c.length, var4.size()).clear();
         }
      } else {
         return this.b.a(new ArrayList<>());
      }
   }

   private qR<S, P> b(Iterable<P> var1) {
      qM var2 = (qM)var1.iterator().next();
      ArrayList var3 = new ArrayList(var2.a().a() + 1);
      ArrayList var4 = new ArrayList(var2.a().a() + 1);
      var3.add(var2);
      qR var8 = this.a(var3, var3.size(), var4);

      while (true) {
         qM var5 = a(var1, var8);
         if (var8.a((P)var5, this.a)) {
            return var8;
         }

         var4.clear();
         var4.add(var5);
         qR var6 = var8;
         if ((var8 = this.a(var3, var3.size(), var4)).b < var6.b) {
            throw new oY();
         }

         var3.add(0, var5);
         var3.subList(var8.c.length, var3.size()).clear();
      }
   }

   public qR<S, P> a(List<P> var1, int var2, List<P> var3) {
      qR var4;
      if ((var4 = this.b.a(var3)).c.length <= var4.a.a().a()) {
         for (int var5 = 0; var5 < var2; var5++) {
            qM var6 = (qM)var1.get(var5);
            if (!var4.a((P)var6, this.a)) {
               var3.add(var6);
               var4 = this.a(var1, var5, var3);
               var3.remove(var3.size() - 1);

               for (int var7 = var5; var7 > 0; var7--) {
                  var1.set(var7, var1.get(var7 - 1));
               }

               var1.set(0, var6);
            }
         }
      }

      return var4;
   }

   private static P a(Iterable<P> var0, qR<S, P> var1) {
      qM var11 = var1.a;
      qM var2 = null;
      double var4 = -1.0;
      Iterator var10 = var0.iterator();

      while (var10.hasNext()) {
         qM var3;
         double var8;
         if ((var8 = (var3 = (qM)var10.next()).a(var11)) > var4) {
            var2 = var3;
            var4 = var8;
         }
      }

      return (P)var2;
   }
}
