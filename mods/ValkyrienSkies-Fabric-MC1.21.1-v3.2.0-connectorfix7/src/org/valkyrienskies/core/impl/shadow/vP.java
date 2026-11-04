package org.valkyrienskies.core.impl.shadow;

final class vP extends wm {
   private static final long n = 20111120L;

   public vP() {
   }

   private vP(vP var1) {
      super(var1);
   }

   @Override
   public final ws a() {
      return new vP(this);
   }

   @Override
   public final void a(double var1, double var3) {
      if (super.a != null && var1 <= 0.5) {
         for (int var6 = 0; var6 < super.g.length; var6++) {
            super.g[var6] = super.a[var6] + var1 * super.d * super.b[0][var6];
         }

         System.arraycopy(super.b[0], 0, super.h, 0, super.h.length);
      } else {
         for (int var5 = 0; var5 < super.g.length; var5++) {
            super.g[var5] = super.e[var5] - var3 * super.b[0][var5];
         }

         System.arraycopy(super.b[0], 0, super.h, 0, super.h.length);
      }
   }
}
