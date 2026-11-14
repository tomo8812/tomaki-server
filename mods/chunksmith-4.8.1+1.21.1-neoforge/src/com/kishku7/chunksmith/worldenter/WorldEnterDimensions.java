package com.kishku7.chunksmith.worldenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class WorldEnterDimensions {
   public static final String DEFAULT = "overworld";
   public static final String OVERWORLD_ID = "minecraft:overworld";
   public static final String NETHER_ID = "minecraft:the_nether";
   public static final String END_ID = "minecraft:the_end";

   private WorldEnterDimensions() {
   }

   public static List<String> parse(String spec) {
      if (spec != null && !spec.isBlank()) {
         boolean overworld = false;
         boolean nether = false;
         boolean end = false;

         for (String part : spec.split(",")) {
            switch (part.trim().toLowerCase(Locale.ROOT)) {
               case "overworld":
                  overworld = true;
                  break;
               case "nether":
                  nether = true;
                  break;
               case "end":
                  end = true;
                  break;
               case "all":
                  overworld = true;
                  nether = true;
                  end = true;
                  break;
               default:
                  return null;
            }
         }

         List<String> ids = new ArrayList<>(3);
         if (overworld) {
            ids.add("minecraft:overworld");
         }

         if (nether) {
            ids.add("minecraft:the_nether");
         }

         if (end) {
            ids.add("minecraft:the_end");
         }

         return ids;
      } else {
         return null;
      }
   }

   public static String spec(List<String> ids) {
      if (ids.size() == 3) {
         return "all";
      }

      List<String> names = new ArrayList<>(ids.size());

      for (String id : ids) {
         names.add(shortName(id));
      }

      return String.join(",", names);
   }

   public static String shortName(String id) {
      if ("minecraft:the_nether".equals(id)) {
         return "nether";
      } else {
         return "minecraft:the_end".equals(id) ? "end" : "overworld";
      }
   }

   public static String displayName(String id) {
      if ("minecraft:the_nether".equals(id)) {
         return "the Nether";
      } else {
         return "minecraft:the_end".equals(id) ? "the End" : "the Overworld";
      }
   }

   public static String recordFileName(String id) {
      if ("minecraft:the_nether".equals(id)) {
         return "worldenter-done-nether.json";
      } else {
         return "minecraft:the_end".equals(id) ? "worldenter-done-end.json" : "worldenter-done.json";
      }
   }
}
