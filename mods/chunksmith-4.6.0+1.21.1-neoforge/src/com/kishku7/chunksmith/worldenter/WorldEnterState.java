package com.kishku7.chunksmith.worldenter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public final class WorldEnterState {
   public static final String FILE_NAME = "worldenter.json";
   private final boolean timeAdvanceWasOn;
   private final long dayTime;
   private final long tickBudgetMillis;
   private final long playerReserveMillis;
   private final double targetMspt;

   public WorldEnterState(boolean timeAdvanceWasOn, long dayTime, long tickBudgetMillis, long playerReserveMillis, double targetMspt) {
      this.timeAdvanceWasOn = timeAdvanceWasOn;
      this.dayTime = dayTime;
      this.tickBudgetMillis = tickBudgetMillis;
      this.playerReserveMillis = playerReserveMillis;
      this.targetMspt = targetMspt;
   }

   public boolean timeAdvanceWasOn() {
      return this.timeAdvanceWasOn;
   }

   public long dayTime() {
      return this.dayTime;
   }

   public long tickBudgetMillis() {
      return this.tickBudgetMillis;
   }

   public long playerReserveMillis() {
      return this.playerReserveMillis;
   }

   public double targetMspt() {
      return this.targetMspt;
   }

   public boolean write(Path worldDir) {
      Path dir = worldDir.resolve("chunksmith");
      Path target = dir.resolve("worldenter.json");
      Path temp = dir.resolve("worldenter.json.tmp");
      String json = "{\n  \"timeAdvanceWasOn\": "
         + this.timeAdvanceWasOn
         + ",\n  \"dayTime\": "
         + this.dayTime
         + ",\n  \"tickBudgetMillis\": "
         + this.tickBudgetMillis
         + ",\n  \"playerReserveMillis\": "
         + this.playerReserveMillis
         + ",\n  \"targetMspt\": "
         + this.targetMspt
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

   public static Optional<WorldEnterState> read(Path worldDir) {
      Path target = worldDir.resolve("chunksmith").resolve("worldenter.json");
      if (!Files.isRegularFile(target)) {
         return Optional.empty();
      }

      try {
         String json = new String(Files.readAllBytes(target), StandardCharsets.UTF_8);
         return Optional.of(
            new WorldEnterState(
               Boolean.parseBoolean(field(json, "timeAdvanceWasOn", "true")),
               Long.parseLong(field(json, "dayTime", "0")),
               Long.parseLong(field(json, "tickBudgetMillis", "25")),
               Long.parseLong(field(json, "playerReserveMillis", "20")),
               Double.parseDouble(field(json, "targetMspt", "150.0"))
            )
         );
      } catch (IOException | NumberFormatException e) {
         return Optional.empty();
      }
   }

   public static void clear(Path worldDir) {
      try {
         Files.deleteIfExists(worldDir.resolve("chunksmith").resolve("worldenter.json"));
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
      return value.isEmpty() ? fallback : value;
   }
}
