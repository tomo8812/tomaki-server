package com.kishku7.chunksmith.shape;

import com.kishku7.chunksmith.platform.util.Vector2;
import java.util.Optional;

public final class ShapeUtil {
   private ShapeUtil() {
   }

   public static boolean insideLine(double ax, double az, double bx, double bz, double cx, double cz) {
      return (bx - ax) * (cz - az) > (bz - az) * (cx - ax);
   }

   public static Optional<Vector2> intersection(double l1x1, double l1z1, double l1x2, double l1z2, double l2x1, double l2z1, double l2x2, double l2z2) {
      double a1 = l1z2 - l1z1;
      double a2 = l2z2 - l2z1;
      double b1 = l1x1 - l1x2;
      double b2 = l2x1 - l2x2;
      double determinant = a1 * b2 - a2 * b1;
      if (determinant == 0.0) {
         return Optional.empty();
      }

      double c1 = a1 * l1x1 + b1 * l1z1;
      double c2 = a2 * l2x1 + b2 * l2z1;
      double x = (b2 * c1 - b1 * c2) / determinant;
      double z = (a1 * c2 - a2 * c1) / determinant;
      return Optional.of(Vector2.of(x, z));
   }

   public static Vector2 pointOnEllipse(double centerX, double centerZ, double radiusX, double radiusZ, double angle) {
      double x = centerX + radiusX * Math.cos(angle);
      double z = centerZ + radiusZ * Math.sin(angle);
      return Vector2.of(x, z);
   }

   public static Vector2 closestPointOnLine(double posX, double posZ, double p1x, double p1z, double p2x, double p2z) {
      double dx = p2x - p1x;
      double dz = p2z - p1z;
      double perpendicularSlope = -dx / dz;
      double p3x;
      double p3z;
      if (Double.isInfinite(perpendicularSlope)) {
         p3x = posX;
         p3z = posZ + 1.0;
      } else {
         p3x = posX + 1.0;
         p3z = posZ + perpendicularSlope;
      }

      return intersection(p1x, p1z, p2x, p2z, posX, posZ, p3x, p3z).orElseThrow(IllegalStateException::new);
   }

   public static double distanceBetweenPoints(double p1x, double p1z, double p2x, double p2z) {
      return Math.sqrt(Math.pow(p1x - p2x, 2.0) + Math.pow(p1z - p2z, 2.0));
   }
}
