package org.valkyrienskies.core.impl.shadow;

public final class pm extends oW {
   private static final long c = 4330003017885151975L;
   private final Number d;
   private final boolean e;

   public pm(Number var1, Number var2, boolean var3) {
      this(var3 ? px.NUMBER_TOO_LARGE : px.NUMBER_TOO_LARGE_BOUND_EXCLUDED, var1, var2, var3);
   }

   public pm(px var1, Number var2, Number var3, boolean var4) {
      super(var1, var2, var3);
      this.d = var3;
      this.e = var4;
   }

   private boolean b() {
      return this.e;
   }

   private Number c() {
      return this.d;
   }
}
