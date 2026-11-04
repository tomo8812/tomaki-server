package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class qK implements qI {
   private int a;

   private qK(int var1) {
      this.a = var1;
   }

   @Override
   public final qq a(qF var1) {
      return new qq(this.a((qz)var1), this.a((qz)var1));
   }

   private qp a(qz var1) {
      if (var1.f() < this.a) {
         throw new oV(px.TOO_LARGE_TOURNAMENT_ARITY, this.a, var1.f());
      }

      qz var2 = new qz(this.a) {
         @Override
         public final qt a() {
            return null;
         }
      };
      ArrayList var5 = new ArrayList<>(Collections.unmodifiableList(var1.a));

      for (int var3 = 0; var3 < this.a; var3++) {
         int var4 = qx.a().nextInt(var5.size());
         var2.a((qp)var5.get(var4));
         var5.remove(var4);
      }

      var1 = var2;
      qp var8 = var2.a.get(0);
      Iterator var7 = var1.a.iterator();

      while (var7.hasNext()) {
         qp var9;
         if ((var9 = (qp)var7.next()).b(var8) > 0) {
            var8 = var9;
         }
      }

      return var8;
   }

   private int a() {
      return this.a;
   }

   private void a(int var1) {
      this.a = var1;
   }
}
