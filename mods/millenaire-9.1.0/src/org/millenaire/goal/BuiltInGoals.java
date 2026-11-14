package org.millenaire.goal;

import java.util.List;
import java.util.Set;

public final class BuiltInGoals {
   public static final List<String> IDS = List.of(
      "idle",
      "rest",
      "build",
      "socialise",
      "chat",
      "be_seller",
      "child_become_adult",
      "bring_back_home",
      "deliver_resources_shop",
      "get_resources_for_shops",
      "get_goods_for_household",
      "get_tool",
      "gather_goods",
      "light_hearth",
      "hunt_monster",
      "raid_village",
      "defend_village",
      "hide",
      "hostile_villager",
      "engage_target",
      "hired_escort",
      "gather_silk",
      "gather_snails",
      "gather_brick",
      "dry_brick",
      "harvest_sugar_cane",
      "plant_sugar_cane",
      "clear_old_path",
      "build_path",
      "terraform",
      "keep_stall",
      "visit_inn",
      "visit_building"
   );
   private static final Set<String> ID_SET = Set.copyOf(IDS);

   private BuiltInGoals() {
   }

   public static boolean contains(String id) {
      return ID_SET.contains(id);
   }
}
