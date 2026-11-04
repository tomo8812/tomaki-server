package maki.lodcpu;

public final class FollowPolicy {
   public static int center(double var0) {
      if (Double.isFinite(var0) && !(Math.abs(var0) > 2.999E7)) {
         return Math.floorDiv((int)Math.floor(var0), 16) * 16 + 8;
      } else {
         throw new IllegalArgumentException("Invalid world coordinate");
      }
   }

   public static boolean moved(int var0, int var1, int var2, int var3) {
      return Math.floorDiv(var0, 16) != Math.floorDiv(var2, 16) || Math.floorDiv(var1, 16) != Math.floorDiv(var3, 16);
   }

   public static boolean due(long var0, long var2, int var4) {
      return var2 == 0L || var0 - var2 >= var4 * 1000L;
   }
}
