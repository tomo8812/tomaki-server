package org.valkyrienskies.core.impl.shadow;

public abstract class wF<PAIR> implements wK<PAIR> {
   private final double a;
   private final double b;

   public wF(double var1, double var3) {
      this.a = var1;
      this.b = var3;
   }

   public final double a() {
      return this.a;
   }

   public final double b() {
      return this.b;
   }

   @Override
   public abstract boolean a(int var1, PAIR var2, PAIR var3);
}
