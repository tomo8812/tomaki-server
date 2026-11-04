package org.millenaire.content.validate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class RuleCatalog {
   private static final Map<String, RuleCatalog.Rule> RULES = new LinkedHashMap<>();

   private static void add(String id, String tier, String title, boolean promoted) {
      RULES.put(id, new RuleCatalog.Rule(id, tier, title, promoted));
   }

   private RuleCatalog() {
   }

   public static List<RuleCatalog.Rule> rules() {
      return List.copyOf(RULES.values());
   }

   public static RuleCatalog.Rule get(String ruleId) {
      RuleCatalog.Rule rule = RULES.get(ruleId);
      if (rule == null) {
         int slash = ruleId.indexOf(47);
         if (slash > 0) {
            rule = RULES.get(ruleId.substring(0, slash));
         }
      }

      return rule;
   }

   public static boolean isPromoted(String ruleId) {
      RuleCatalog.Rule rule = get(ruleId);
      return rule != null && rule.promoted();
   }

   public static Finding normalise(Finding finding) {
      return isPromoted(finding.ruleId()) ? finding : finding.withSeverity(finding.severity().capAt(Severity.WARN));
   }

   static {
      add("S-01", "S", "Every *.json parses, is UTF-8 and carries no BOM", true);
      add("S-02", "S", "Identity matches the file (building_id == stem, wall type key == stem)", true);
      add("S-03", "S", "Depth: buildings/villagers one folder below the root, villages at the root", true);
      add("S-04", "S", "Required fields present per the field contract", true);
      add("S-05", "S", "No unknown field at any nesting level declared by the contract", true);
      add("S-06", "S", "Every declared NBT exists; every NBT is claimed by a plan set", true);
      add("S-07", "S", "Manifests match the files (languages, quests)", true);
      add("S-08", "S", "No duplicate identity within one source", true);
      add("S-09", "S", "Variant fragments name an existing base in the same directory", true);
      add("X-01", "X", "Villager goals resolve in the goal catalogue", true);
      add("X-02", "X", "Plan residents / visitors resolve to villager types of the culture", true);
      add("X-03", "X", "Village layouts, player buildings, hamlets, never and wall types resolve", true);
      add("X-04", "X", "Plan shops resolve; shop goods resolve; unreferenced shops reported", true);
      add("X-05", "X", "Gathering handlers exist; handlerParams building tags are declared", true);
      add("X-07", "X", "Item references are traded-good ids or syntactically valid item ids", true);
      add("X-08", "X", "Quest villager keys, villager types, cultures and village types resolve", true);
      add("X-09", "X", "Content ids have their en_us name key", true);
      add("X-10", "X", "Sub-building parent_building triplets resolve", true);
      add("X-11", "X", "Tag references are declared somewhere; wall gateway plan sets exist", true);
      add("M-01", "M", "Every item id referenced by content resolves in the item registry", false);
      add("M-02", "M", "Every NBT palette block resolves; every mock block is a known special point", false);
      add("M-03", "M", "Effective special points cover the building's role", false);
      add("M-04", "M", "Full load cycle observed clean", false);
      add("M-05", "M", "Natural village types can start, place and grow", false);
      add("M-06", "M", "Mining sourceSubtype reachable through the village's buildings", false);
      add("ALLOWLIST", "V", "Allowlist entries match a finding", true);
      add("CONTRACT", "V", "Field contracts load", true);
      add("VALIDATOR", "V", "Rules run without throwing", true);
   }

   public record Rule(String id, String tier, String title, boolean promoted) {
   }
}
