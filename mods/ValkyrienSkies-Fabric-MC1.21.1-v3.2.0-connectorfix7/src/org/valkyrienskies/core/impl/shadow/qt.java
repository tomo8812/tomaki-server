package org.valkyrienskies.core.impl.shadow;

import java.util.Collections;
import java.util.List;

public final class qt extends qz {
   private double b = 0.9;

   private qt(List<qp> var1, int var2, double var3) {
      super(var1, var2);
      this.a(var3);
   }

   private qt(int var1, double var2) {
      super(var1);
      this.a(var2);
   }

   @Override
   public final qt a() {
      qt var1 = new qt(this.e(), this.b);
      List var2;
      Collections.sort(var2 = this.c());

      for (int var3 = (int)BY.z((1.0 - this.b) * var2.size()); var3 < var2.size(); var3++) {
         var1.a((qp)var2.get(var3));
      }

      return var1;
   }

   private void a(double var1) {
      if (!(var1 < 0.0) && !(var1 > 1.0)) {
         this.b = var1;
      } else {
         throw new po(px.ELITISM_RATE, var1, 0, 1);
      }
   }

   private double g() {
      return this.b;
   }
}
