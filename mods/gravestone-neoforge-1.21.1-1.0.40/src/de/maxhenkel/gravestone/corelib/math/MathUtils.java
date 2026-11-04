package de.maxhenkel.gravestone.corelib.math;

public class MathUtils {
   public static boolean isInBounds(float number, float bound, float tolerance) {
      return number > bound - tolerance && number < bound + tolerance;
   }

   public static float subtractToZero(float num, float sub) {
      float erg;
      if (num < 0.0F) {
         erg = num + sub;
         if (erg > 0.0F) {
            erg = 0.0F;
         }
      } else {
         erg = num - sub;
         if (erg < 0.0F) {
            erg = 0.0F;
         }
      }

      return erg;
   }

   public static double round(double value, int scale) {
      return Math.round(value * Math.pow(10.0, scale)) / Math.pow(10.0, scale);
   }

   public static float round(float value, int scale) {
      return (float)(Math.round(value * Math.pow(10.0, scale)) / Math.pow(10.0, scale));
   }
}
