package dev.diorama;

public final class Projection {
   private static volatile double sin = Math.sin(Math.toRadians(MapStyle.DEFAULT.pitchDegrees()));
   private static volatile double cos = Math.cos(Math.toRadians(MapStyle.DEFAULT.pitchDegrees()));
   private static volatile Facing facing = Facing.NORTH;
   public static final int TILE_UNITS = 128;
   public static final int PIXELS_PER_UNIT = 4;
   public static final int TILE_PIXELS = 512;
   public static final int COVERAGE_CELLS = 64;

   private Projection() {
   }

   static void setPitch(double degrees) {
      sin = Math.sin(Math.toRadians(degrees));
      cos = Math.cos(Math.toRadians(degrees));
   }

   public static void setFacing(Facing value) {
      facing = value;
   }

   public static Facing facing() {
      return facing;
   }

   public static double sin() {
      return sin;
   }

   public static double cos() {
      return cos;
   }

   public static double v(double y, double z) {
      return z * sin - y * cos;
   }

   public static double u(double x, double z) {
      return facing.x(x, z);
   }

   public static double v(double x, double y, double z) {
      return v(y, facing.z(x, z));
   }

   public static double zAt(double v, double y) {
      return (v + y * cos) / sin;
   }
}
