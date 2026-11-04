package com.kishku7.chunksmith.worldenter;

import java.util.Locale;

public final class WorldEnterCenter {
   public static final String ORIGIN = "origin";
   public static final String SPAWN = "spawn";
   public static final String AUTO = "auto";
   public static final String DEFAULT = "auto";
   private static final WorldEnterCenter AT_ORIGIN = new WorldEnterCenter(false, false, 0.0, 0.0);
   private static final WorldEnterCenter AT_SPAWN = new WorldEnterCenter(true, false, 0.0, 0.0);
   private static final WorldEnterCenter AUTOMATIC = new WorldEnterCenter(true, true, 0.0, 0.0);
   private final boolean followsSpawn;
   private final boolean auto;
   private final double x;
   private final double z;

   private WorldEnterCenter(boolean followsSpawn, boolean auto, double x, double z) {
      this.followsSpawn = followsSpawn;
      this.auto = auto;
      this.x = x;
      this.z = z;
   }

   public static WorldEnterCenter origin() {
      return AT_ORIGIN;
   }

   public static WorldEnterCenter spawn() {
      return AT_SPAWN;
   }

   public static WorldEnterCenter auto() {
      return AUTOMATIC;
   }

   public static WorldEnterCenter at(double x, double z) {
      return new WorldEnterCenter(false, false, x, z);
   }

   public boolean isAuto() {
      return this.auto;
   }

   public static WorldEnterCenter parse(String spec) {
      if (spec == null) {
         return null;
      }

      String trimmed = spec.trim();
      if (trimmed.isEmpty()) {
         return null;
      }

      String lower = trimmed.toLowerCase(Locale.ROOT);
      if ("origin".equals(lower)) {
         return origin();
      }

      if ("spawn".equals(lower)) {
         return spawn();
      }

      if ("auto".equals(lower)) {
         return auto();
      }

      int comma = trimmed.indexOf(44);
      if (comma <= 0 || comma == trimmed.length() - 1) {
         return null;
      }

      if (trimmed.indexOf(44, comma + 1) >= 0) {
         return null;
      }

      try {
         double px = Double.parseDouble(trimmed.substring(0, comma).trim());
         double pz = Double.parseDouble(trimmed.substring(comma + 1).trim());
         return isFinite(px) && isFinite(pz) ? at(px, pz) : null;
      } catch (NumberFormatException notCoordinates) {
         return null;
      }
   }

   public boolean followsSpawn() {
      return this.followsSpawn;
   }

   public double x() {
      return this.x;
   }

   public double z() {
      return this.z;
   }

   public String spec() {
      if (this.auto) {
         return "auto";
      } else if (this.followsSpawn) {
         return "spawn";
      } else {
         return this.x == 0.0 && this.z == 0.0 ? "origin" : trim(this.x) + "," + trim(this.z);
      }
   }

   @Override
   public String toString() {
      return this.spec();
   }

   private static boolean isFinite(double value) {
      return !Double.isNaN(value) && !Double.isInfinite(value);
   }

   private static String trim(double value) {
      return value == Math.rint(value) && Math.abs(value) < 1.0E15 ? Long.toString((long)value) : Double.toString(value);
   }
}
