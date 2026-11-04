package org.valkyrienskies.core.impl.shadow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class pN implements Serializable {
   private static final long a = 20130813L;
   private final List<pM> b = new ArrayList<>();

   private void a(double var1, double var3) {
      double var10 = var3;
      double var8 = var1;
      double var6 = 1.0;
      this.b.add(new pM(1.0, var8, var10));
   }

   private void a(double var1, double var3, double var5) {
      this.b.add(new pM(1.0, var3, var5));
   }

   private void a(pM var1) {
      this.b.add(var1);
   }

   private List<pM> a() {
      return new ArrayList<>(this.b);
   }

   private void b() {
      this.b.clear();
   }
}
