package com.kishku7.chunksmith.worldenter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public final class WorldEnterDone {
   public static final String FILE_NAME = "worldenter-done.json";
   private final String dimension;
   private final long radiusBlocks;
   private final double centerX;
   private final double centerZ;
   private final long completedAtMillis;

   public WorldEnterDone(String dimension, long radiusBlocks, long completedAtMillis) {
      this(dimension, radiusBlocks, 0.0, 0.0, completedAtMillis);
   }

   public WorldEnterDone(String dimension, long radiusBlocks, double centerX, double centerZ, long completedAtMillis) {
      this.dimension = dimension;
      this.radiusBlocks = radiusBlocks;
      this.centerX = centerX;
      this.centerZ = centerZ;
      this.completedAtMillis = completedAtMillis;
   }

   public double centerX() {
      return this.centerX;
   }

   public double centerZ() {
      return this.centerZ;
   }

   public String dimension() {
      return this.dimension;
   }

   public long radiusBlocks() {
      return this.radiusBlocks;
   }

   public long completedAtMillis() {
      return this.completedAtMillis;
   }

   public boolean satisfies(String wantedDimension, long wantedRadius) {
      return this.satisfies(wantedDimension, wantedRadius, 0.0, 0.0);
   }

   public boolean satisfies(String wantedDimension, long wantedRadius, double wantedX, double wantedZ) {
      if (this.dimension != null && this.dimension.equals(wantedDimension)) {
         double dx = this.centerX - wantedX;
         double dz = this.centerZ - wantedZ;
         double distance = Math.sqrt(dx * dx + dz * dz);
         return distance + wantedRadius <= this.radiusBlocks;
      } else {
         return false;
      }
   }

   public boolean write(Path worldDir) {
      Path dir = worldDir.resolve("chunksmith");
      Path target = dir.resolve("worldenter-done.json");
      Path temp = dir.resolve("worldenter-done.json.tmp");
      String json = "{\n  \"dimension\": \""
         + this.dimension
         + "\",\n  \"radiusBlocks\": "
         + this.radiusBlocks
         + ",\n  \"centerX\": "
         + this.centerX
         + ",\n  \"centerZ\": "
         + this.centerZ
         + ",\n  \"completedAtMillis\": "
         + this.completedAtMillis
         + "\n}\n";

      try {
         Files.createDirectories(dir);
         Files.write(temp, json.getBytes(StandardCharsets.UTF_8));

         try {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
         } catch (IOException atomicUnsupported) {
            Files.move(temp, target, StandardCopyOption.REPLACE_EXISTING);
         }

         return true;
      } catch (IOException e) {
         return false;
      }
   }

   public static Optional<WorldEnterDone> read(Path worldDir) {
      Path target = worldDir.resolve("chunksmith").resolve("worldenter-done.json");
      if (!Files.isRegularFile(target)) {
         return Optional.empty();
      }

      try {
         String json = new String(Files.readAllBytes(target), StandardCharsets.UTF_8);
         String dim = field(json, "dimension", "");
         long radius = Long.parseLong(field(json, "radiusBlocks", "0"));
         return !dim.isEmpty() && radius > 0L
            ? Optional.of(
               new WorldEnterDone(
                  dim,
                  radius,
                  Double.parseDouble(field(json, "centerX", "0")),
                  Double.parseDouble(field(json, "centerZ", "0")),
                  Long.parseLong(field(json, "completedAtMillis", "0"))
               )
            )
            : Optional.empty();
      } catch (IOException | NumberFormatException e) {
         return Optional.empty();
      }
   }

   public static void clear(Path worldDir) {
      try {
         Files.deleteIfExists(worldDir.resolve("chunksmith").resolve("worldenter-done.json"));
      } catch (IOException var2) {
      }
   }

   private static String field(String json, String name, String fallback) {
      String key = "\"" + name + "\"";
      int at = json.indexOf(key);
      if (at < 0) {
         return fallback;
      }

      int colon = json.indexOf(58, at + key.length());
      if (colon < 0) {
         return fallback;
      }

      int end = colon + 1;

      while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}' && json.charAt(end) != '\n') {
         end++;
      }

      String value = json.substring(colon + 1, end).trim();
      if (value.length() >= 2 && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"') {
         value = value.substring(1, value.length() - 1);
      }

      return value.isEmpty() ? fallback : value;
   }
}
