package com.kishku7.chunksmith.worldenter;

import java.util.List;
import java.util.Locale;

public final class WorldEnterShape {
   public static final String DEFAULT = "circle";
   public static final List<String> ALL = List.of("circle", "square", "diamond", "hexagon", "pentagon", "triangle", "star");

   private WorldEnterShape() {
   }

   public static String parse(String raw) {
      if (raw == null) {
         return null;
      }

      String wanted = raw.trim().toLowerCase(Locale.ROOT);
      return ALL.contains(wanted) ? wanted : null;
   }

   public static long estimateChunks(String shape, long radiusBlocks) {
      double r = radiusBlocks / 16.0;

      return (long)Math.ceil(switch (shape == null ? "circle" : shape) {
         case "square" -> 4.0 * r * r;
         case "diamond" -> 2.0 * r * r;
         case "hexagon" -> 1.5 * Math.sqrt(3.0) * r * r;
         case "pentagon" -> 2.5 * Math.sin(Math.PI * 2.0 / 5.0) * r * r;
         case "triangle" -> 0.75 * Math.sqrt(3.0) * r * r;
         case "star" -> 1.12 * r * r;
         default -> Math.PI * r * r;
      });
   }

   public static boolean covers(String done, String wanted) {
      String d = done == null ? "circle" : done;
      String w = wanted == null ? "circle" : wanted;
      return d.equals(w) || "square".equals(d);
   }
}
