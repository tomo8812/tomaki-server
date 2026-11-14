package com.kishku7.chunksmith.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RendererNames {
   public static final String DISTANT_HORIZONS = "distanthorizons";
   public static final String VOXY = "voxy";
   public static final String NEOVOXY = "neovoxy";
   private static final Map<String, String> NAMES = new LinkedHashMap<>();

   private RendererNames() {
   }

   public static List<String> ids() {
      return List.copyOf(NAMES.keySet());
   }

   public static String display(String id) {
      String name = NAMES.get(id);
      return name != null ? name : id;
   }

   public static List<String> displayNames() {
      return new ArrayList<>(NAMES.values());
   }

   static {
      NAMES.put("distanthorizons", "Distant Horizons");
      NAMES.put("voxy", "Voxy");
      NAMES.put("neovoxy", "neo-voxy");
   }
}
