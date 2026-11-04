package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class qn extends qm<Integer> {
   private qn(List<Integer> var1) {
      super(var1);
   }

   private qn(Integer[] var1) {
      super(var1);
   }

   @Override
   protected final void a(List<Integer> var1) {
      Iterator var3 = var1.iterator();

      while (var3.hasNext()) {
         int var2;
         if ((var2 = (Integer)var3.next()) < 0 || var2 > 1) {
            throw new qy(px.INVALID_BINARY_DIGIT, var2);
         }
      }
   }

   private static List<Integer> a(int var0) {
      ArrayList var1 = new ArrayList(var0);

      for (int var2 = 0; var2 < var0; var2++) {
         var1.add(qx.a().nextInt(2));
      }

      return var1;
   }

   @Override
   protected final boolean a(qp var1) {
      if (!(var1 instanceof qn)) {
         return false;
      }

      var1 = var1;
      if (this.b() != var1.b()) {
         return false;
      }

      for (int var2 = 0; var2 < this.a().size(); var2++) {
         if (!this.a().get(var2).equals(var1.a().get(var2))) {
            return false;
         }
      }

      return true;
   }
}
