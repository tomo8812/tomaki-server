package org.valkyrienskies.core.impl.shadow;

import java.util.ArrayList;
import java.util.Collection;

final class va implements uZ {
   private final uM a;

   private va(uM var1) {
      this.a = var1;
   }

   private int c() {
      return this.a.a();
   }

   private void a(double var1, double[] var3, double[] var4) {
      this.a.a(var1, var3, var4);
   }

   @Override
   public final Collection<String> a() {
      return new ArrayList<>();
   }

   @Override
   public final boolean a(String var1) {
      return false;
   }

   @Override
   public final double b(String var1) {
      throw new ve(var1);
   }

   @Override
   public final void b() {
   }
}
