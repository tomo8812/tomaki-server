package maki.airship;

public final class ShipShiftDecision {
   public static boolean send(boolean var0, boolean var1, boolean var2, boolean var3) {
      return var0 ? var2 != var3 : var1;
   }
}
