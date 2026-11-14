package maki.chunksmithcontroller;

public final class FollowPolicy {
   public static int center(double coordinate) {
      if (Double.isFinite(coordinate) && !(Math.abs(coordinate) > 2.999E7)) {
         return Math.floorDiv((int)Math.floor(coordinate), 16) * 16 + 8;
      } else {
         throw new IllegalArgumentException("Invalid world coordinate");
      }
   }

   public static boolean moved(int x, int z, int oldX, int oldZ) {
      return Math.floorDiv(x, 16) != Math.floorDiv(oldX, 16) || Math.floorDiv(z, 16) != Math.floorDiv(oldZ, 16);
   }

   public static boolean due(long now, long last, int seconds) {
      return last == 0L || now - last >= seconds * 1000L;
   }
}
