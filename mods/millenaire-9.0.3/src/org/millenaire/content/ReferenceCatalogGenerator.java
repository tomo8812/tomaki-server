package org.millenaire.content;

import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import org.millenaire.goal.GoalRegistry;
import org.millenaire.goal.gathering.GatheringHandlerRegistry;
import org.slf4j.Logger;

public final class ReferenceCatalogGenerator {
   private static final Logger LOGGER = LogUtils.getLogger();

   private ReferenceCatalogGenerator() {
   }

   public static void generate(GoalRegistry goalRegistry) {
      if (ContentDirectoryManager.isInitialized()) {
         try {
            Path dir = ContentDirectoryManager.getStandardDir().resolve("_reference");
            Files.createDirectories(dir);
            writeGoals(dir, goalRegistry);
            writeGatheringHandlers(dir);
            writeSpecialPoints(dir);
            writeIndex(dir);
            LOGGER.info("Reference catalogs written to {}", dir);
         } catch (Exception e) {
            LOGGER.warn("Could not generate reference catalogs: {}", e.getMessage(), e);
         }
      }
   }

   private static void writeGoals(Path dir, GoalRegistry goalRegistry) throws IOException {
      StringBuilder sb = new StringBuilder();
      sb.append("# Registered goals\n");
      sb.append("# Use these ids in villager_type JSONs under the \"goals\" field.\n");
      sb.append("# Generated automatically — do not edit.\n\n");
      List<ResourceLocation> ids = goalRegistry.getAllIds();

      for (ResourceLocation id : ids) {
         sb.append(id).append('\n');
      }

      sb.append("\n# Total: ").append(ids.size()).append(" goals.\n");
      Files.writeString(dir.resolve("goals.txt"), sb.toString(), StandardCharsets.UTF_8);
   }

   private static void writeGatheringHandlers(Path dir) throws IOException {
      StringBuilder sb = new StringBuilder();
      sb.append("# Registered gathering handlers\n");
      sb.append("# Use these ids in gathering_type/<id>.json under the \"handler\" field.\n");
      sb.append("# Generated automatically — do not edit.\n\n");
      List<String> ids = GatheringHandlerRegistry.getAllIds();

      for (String id : ids) {
         sb.append(id).append('\n');
      }

      sb.append("\n# Total: ").append(ids.size()).append(" handlers.\n");
      Files.writeString(dir.resolve("gathering_handlers.txt"), sb.toString(), StandardCharsets.UTF_8);
   }

   private static void writeSpecialPoints(Path dir) throws IOException {
      String content = "# Special-point mock blocks\n# Place these blocks inside an NBT template to mark semantic positions.\n# The mod extracts them at load time (MockBlockExtractor) and processes\n# them at placement time (MockBlockProcessor). See docs/systems/ for\n# per-type processing semantics.\n#\n# Generated automatically — do not edit.\n\nmillenaire:mock_marker          Generic marker (spawn point, waypoint, etc.)\nmillenaire:mock_facing_marker   Directional marker (door facing, etc.)\nmillenaire:mock_chest           Chest spawn location\nmillenaire:mock_soil            Farmable soil zone\nmillenaire:mock_source          Quarry source: placed as its material, mined by\n                                villagers (mining \"sourceSubtype\")\nmillenaire:mock_free            Cost-free block: placed as its material, not\n                                counted in the building cost\nmillenaire:mock_tree_spawn      Tree planting location\nmillenaire:mock_animal_spawn    Animal spawn zone\nmillenaire:mock_decor           Decorative item placement\n\n# Generic markers (data-extensible) — the subtype is a registry id held by\n# the block's block entity (\"payload\"), not a blockstate. In creative mode,\n# right-click the placed marker with an item to set it (sneaking or not);\n# with an empty hand, right-click shows the current payload. Pick-block\n# (middle click) copies the payload.\n\nmillenaire:mock_generic_soil         Soil for any farmland crop. Payload: the\n                                     seed item id (right-click with a seed whose\n                                     block is a crop or a stem), e.g.\n                                     mymod:blueberry_seeds. The builder places\n                                     dirt.\nmillenaire:mock_generic_tree_spawn   Tree planting location for any sapling.\n                                     Payload: the sapling item id, e.g.\n                                     minecraft:cherry_sapling.\nmillenaire:mock_generic_animal_spawn Pen spawn for any mob. Payload: the entity\n                                     type id from any spawn egg, e.g. minecraft:goat.\n                                     Monsters are accepted: they are persistent,\n                                     tagged millenaire_pen and exempt from the\n                                     village mob sweeps (they still attack).\nmillenaire:mock_generic_source       Quarry source of any full block. Payload: the\n                                     blockstate placed, e.g. minecraft:tuff (right-\n                                     click with the block; its placement orientation\n                                     is kept). Special point subtype: the block id.\nmillenaire:mock_generic_free         Cost-free block of any single-cell block.\n                                     Payload: the blockstate placed, e.g.\n                                     minecraft:oak_log[axis=x]. Both refuse blocks\n                                     with a block entity (chests...) and two-part\n                                     blocks (doors, beds, tall plants). Sneak to\n                                     place a block against them without changing\n                                     their payload.\n\n# Authoring rules for generic content:\n#  - Crops: the gathering type JSON matches the payload through\n#    \"soilSubtype\": \"<seed id>\", with \"cropBlock\" / \"seedItem\". Farmland crops\n#    only (CropBlock / StemBlock, Millenaire seeds and grapes included; no\n#    rice or sugar cane). Provision the first seeds (starting goods) or\n#    omit \"seedItem\"; harvests supply more. Subtype-less flower types never\n#    plant on generic soil. A generic soil subtype that no gathering type\n#    references is logged at server start (typo check).\n#  - Trees: \"subtypeFilter\": \"<sapling id>\" (optional). The ground under a tree\n#    spawn is grass; a sapling needing other ground will not survive. Modded logs\n#    outside minecraft:logs are never chopped.\n#  - Animals: breeding / slaughter JSONs take the full \"animalType\" id. The pen\n#    cap is per entity type (\"cow\" and \"minecraft:cow\" share it); breeding and\n#    slaughter cap on all animal spawn points of the building together.\n#  - Sources: a \"mining\" JSON names the block id in \"sourceSubtype\"\n#    (e.g. \"minecraft:tuff\"; \"stone\" does not match \"minecraft:stone\"). A\n#    generic source that no mining type names is logged at server start.\n#    Sources and free blocks cost nothing; a payload naming an absent mod\n#    places nothing (warned once).\n#  - Upgrade levels: at the same position, a later level's generic point\n#    replaces the earlier one; removing a marker does not remove the point.\n#  - An empty or malformed payload is skipped with a warning; the template\n#    does not fall back to its _special_points sidecar.\n#  - Unknown ids (missing mod) still load; the points are skipped at runtime\n#    with one warning per id.\n\n# Total: 12 mock block types.\n";
      Files.writeString(dir.resolve("special_points.txt"), content, StandardCharsets.UTF_8);
   }

   private static void writeIndex(Path dir) throws IOException {
      String content = "Millénaire custom content — reference catalogs\n==============================================\n\nThis directory is regenerated on every server start. Do not edit\nthese files — edits are overwritten. Use them as read-only\nreferences when authoring custom content.\n\nFiles:\n  goals.txt                All registered goal ids (for villager_type.goals)\n  gathering_handlers.txt   All registered gathering handler ids\n                           (for gathering_type/<id>.json \"handler\" field)\n  special_points.txt       The 12 mock block types usable inside NBT templates\n\nNot yet generated (pending):\n  tags.txt                 Building + villager tags. Tags are crowd-sourced\n                           from existing JSON files — inspect\n                           cultures/norman/ for real examples.\n\nFor JSON schema references, inspect the deployed built-in cultures\n(cultures/norman/ is the most complete). See docs/feat/custom-content.md\nfor authoring workflows.\n";
      Files.writeString(dir.resolve("README.txt"), content, StandardCharsets.UTF_8);
   }
}
