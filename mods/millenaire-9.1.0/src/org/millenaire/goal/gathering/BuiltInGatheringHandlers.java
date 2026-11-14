package org.millenaire.goal.gathering;

import java.util.List;
import java.util.Set;

public final class BuiltInGatheringHandlers {
   public static final List<String> IDS = List.of(
      "harvesting",
      "planting",
      "chopping",
      "sapling_planting",
      "breeding",
      "mining",
      "slaughter",
      "crafting",
      "shearing",
      "fishing",
      "fishing_inuit",
      "smelting",
      "flower_planting",
      "fruit_harvesting",
      "cocoa_harvesting",
      "cocoa_planting",
      "paddy_planting",
      "paddy_harvesting",
      "take_from_building"
   );
   private static final Set<String> ID_SET = Set.copyOf(IDS);

   private BuiltInGatheringHandlers() {
   }

   public static boolean contains(String id) {
      return ID_SET.contains(id);
   }
}
