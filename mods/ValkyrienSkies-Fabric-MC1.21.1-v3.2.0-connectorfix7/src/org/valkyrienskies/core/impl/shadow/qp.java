package org.valkyrienskies.core.impl.shadow;

import java.util.Iterator;

public abstract class qp implements Comparable<qp>, qu {
   private static final double a = Double.NEGATIVE_INFINITY;
   private double b = Double.NEGATIVE_INFINITY;

   public final double d() {
      if (this.b == Double.NEGATIVE_INFINITY) {
         this.b = this.e();
      }

      return this.b;
   }

   public final int b(qp var1) {
      return Double.compare(this.d(), var1.d());
   }

   protected boolean a(qp var1) {
      return false;
   }

   private qp a(qF var1) {
      for (qp var2 : var1) {
         if (this.a(var2)) {
            return var2;
         }
      }

      return null;
   }

   private void b(qF var1) {
      qF var2 = var1;
      qp var4 = this;
      Iterator var6 = var2.iterator();

      qp var10000;
      while (true) {
         if (var6.hasNext()) {
            qp var3 = (qp)var6.next();
            if (!var4.a(var3)) {
               continue;
            }

            var10000 = var3;
            break;
         }

         var10000 = null;
         break;
      }

      qp var5 = var10000;
      if (var10000 != null) {
         this.b = var5.d();
      }
   }

   // $VF: synthetic method
   @Override
   public int compareTo(Object var1) {
      return this.b((qp)var1);
   }
}
