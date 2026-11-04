package org.valkyrienskies.core.impl.shadow;

public final class pg extends oW {
   private static final long c = 3596849179428944575L;
   private final int d;
   private final boolean e;
   private final int f;
   private final Number g;

   private pg(Number var1, Number var2, int var3) {
      this(var1, var2, var3, Ch.b.INCREASING$37efe540, true);
   }

   public pg(Number var1, Number var2, int var3, int var4, boolean var5) {
      super(
         var4 == Ch.b.INCREASING$37efe540
            ? (var5 ? px.NOT_STRICTLY_INCREASING_SEQUENCE : px.NOT_INCREASING_SEQUENCE)
            : (var5 ? px.NOT_STRICTLY_DECREASING_SEQUENCE : px.NOT_DECREASING_SEQUENCE),
         var1,
         var2,
         var3,
         var3 - 1
      );
      this.d = var4;
      this.e = var5;
      this.f = var3;
      this.g = var2;
   }

   private int b() {
      return this.d;
   }

   private boolean c() {
      return this.e;
   }

   private int d() {
      return this.f;
   }

   private Number e() {
      return this.g;
   }
}
