package org.valkyrienskies.core.impl.shadow;

public class pn extends oW {
   private static final long c = -6100997100383932834L;
   private final Number d;
   private final boolean e;

   public pn(Number var1, Number var2, boolean var3) {
      this(var3 ? px.NUMBER_TOO_SMALL : px.NUMBER_TOO_SMALL_BOUND_EXCLUDED, var1, var2, var3);
   }

   public pn(pw var1, Number var2, Number var3, boolean var4) {
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
