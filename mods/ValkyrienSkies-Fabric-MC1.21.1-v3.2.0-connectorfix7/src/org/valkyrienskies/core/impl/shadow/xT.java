package org.valkyrienskies.core.impl.shadow;

@Deprecated
public abstract class xT<PAIR> implements ya<PAIR> {
   @Deprecated
   private static final double a = 100.0 * Cq.a;
   @Deprecated
   private static final double b = 100.0 * Cq.b;
   private final double c;
   private final double d;

   @Deprecated
   public xT() {
      this.c = a;
      this.d = b;
   }

   public xT(double var1, double var3) {
      this.c = var1;
      this.d = var3;
   }

   public final double a() {
      return this.c;
   }

   public final double b() {
      return this.d;
   }

   @Override
   public abstract boolean a(int var1, PAIR var2, PAIR var3);
}
