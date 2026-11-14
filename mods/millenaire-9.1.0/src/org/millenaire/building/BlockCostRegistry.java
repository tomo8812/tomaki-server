package org.millenaire.building;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.millenaire.content.CustomContentIndex;
import org.millenaire.content.Resource;
import org.millenaire.culture.JsonLoaderUtils;
import org.millenaire.item.ItemHelper;
import org.slf4j.Logger;

public final class BlockCostRegistry {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Gson GSON = JsonLoaderUtils.GSON;
   public static final String FILE_NAME = "blocklist.json";
   private static final String ANYWOOD_LITERAL = "anywood";
   private static final String ANYWOOD_PLANKS = "millenaire:anywood_planks";
   private static final Set<String> PROTECTED_NAMESPACES = Set.of("millenaire");
   private static final Set<String> GAP_ONLY_NAMESPACES = Set.of("minecraft");
   static final String CORE_USED_BLOCKS_RESOURCE = "/millenaire/reference/core_used_blocks.json";
   private static volatile Set<String> coreUsedBlocks;
   private static volatile boolean coreUsedBlocksLoaded;
   static final int MAX_COST_QUANTITY = 9999;
   private static final Map<String, BlockCostRegistry.BlockCost> COSTS = new ConcurrentHashMap<>();
   private static final Set<String> WARNED_DUPLICATE_IDS = ConcurrentHashMap.newKeySet();

   private BlockCostRegistry() {
   }

   static boolean isUsedByCoreContent(String blockId) {
      Set<String> used = coreUsedBlocks();
      return used == null || used.contains(blockId);
   }

   @Nullable
   private static Set<String> coreUsedBlocks() {
      if (!coreUsedBlocksLoaded) {
         synchronized (BlockCostRegistry.class) {
            if (!coreUsedBlocksLoaded) {
               coreUsedBlocks = readCoreUsedBlocks();
               coreUsedBlocksLoaded = true;
            }
         }
      }

      return coreUsedBlocks;
   }

   @Nullable
   private static Set<String> readCoreUsedBlocks() {
      try (InputStream in = BlockCostRegistry.class.getResourceAsStream("/millenaire/reference/core_used_blocks.json")) {
         if (in == null) {
            LOGGER.error(
               "[Millenaire] {} missing from the jar — no minecraft: block can be declared in {}",
               "/millenaire/reference/core_used_blocks.json",
               "blocklist.json"
            );
            return null;
         }

         try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
            JsonArray blocks = ((JsonObject)GSON.fromJson(reader, JsonObject.class)).getAsJsonArray("blocks");
            Set<String> out = new HashSet<>();

            for (JsonElement e : blocks) {
               out.add(e.getAsString());
            }

            return Set.copyOf(out);
         }
      } catch (Exception e) {
         LOGGER.error(
            "[Millenaire] Error reading {} — no minecraft: block can be declared in {}",
            new Object[]{"/millenaire/reference/core_used_blocks.json", "blocklist.json", e}
         );
         return null;
      }
   }

   public static Optional<BlockCostRegistry.BlockCost> get(String blockId) {
      return Optional.ofNullable(COSTS.get(blockId));
   }

   public static int size() {
      return COSTS.size();
   }

   public static void clear() {
      COSTS.clear();
      WARNED_DUPLICATE_IDS.clear();
   }

   static void putForTesting(String blockId, BlockCostRegistry.BlockCost cost) {
      COSTS.put(blockId, cost);
   }

   public static void loadAll() {
      loadFromResources(CustomContentIndex.current().root().findAll("blocklist.json"));
   }

   static void loadFromResources(List<Resource> resources) {
      Map<String, BlockCostRegistry.BlockCost> byId = new LinkedHashMap<>();
      int removed = 0;
      int duplicatesIgnored = 0;
      if (resources != null && !resources.isEmpty()) {
         for (int i = resources.size() - 1; i >= 0; i--) {
            Resource res = resources.get(i);
            String layerLabel = res.source().displayName();

            try (InputStream stream = res.open()) {
               List<BlockCostRegistry.Entry> entries = parseEntries(stream, layerLabel);
               if (entries != null) {
                  for (BlockCostRegistry.Entry e : entries) {
                     if (e.disabled()) {
                        if (byId.remove(e.id()) != null) {
                           removed++;
                        }
                     } else if (byId.containsKey(e.id())) {
                        duplicatesIgnored++;
                        if (WARNED_DUPLICATE_IDS.add(e.id())) {
                           LOGGER.warn(
                              "[Millenaire] Block cost for '{}' already declared; ignoring duplicate from '{}' (use \"disabled\":true to override)",
                              e.id(),
                              layerLabel
                           );
                        }
                     } else {
                        byId.put(e.id(), e.cost());
                     }
                  }
               }
            } catch (Exception e) {
               LOGGER.error("[Millenaire] Error reading {} ({} layer): {}", new Object[]{"blocklist.json", layerLabel, e.getMessage(), e});
            }
         }

         COSTS.clear();
         COSTS.putAll(byId);
         LOGGER.info(
            "[Millenaire] Loaded {} block cost entries (sources={}, removed={}, duplicates ignored={})",
            new Object[]{byId.size(), resources.size(), removed, duplicatesIgnored}
         );
      } else {
         COSTS.clear();
         LOGGER.info("[Millenaire] Loaded 0 block cost entries (sources=0)");
      }
   }

   @Nullable
   private static List<BlockCostRegistry.Entry> parseEntries(InputStream stream, String layer) {
      try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
         JsonObject root = (JsonObject)GSON.fromJson(reader, JsonObject.class);
         JsonArray blocksArray = root.getAsJsonArray("blocks");
         if (blocksArray == null) {
            LOGGER.error("[Millenaire] {} ({} layer): no 'blocks' array — file skipped", "blocklist.json", layer);
            return null;
         }

         List<BlockCostRegistry.Entry> out = new ArrayList<>();

         for (JsonElement element : blocksArray) {
            BlockCostRegistry.Entry entry = parseEntry(element, layer);
            if (entry != null) {
               out.add(entry);
            }
         }

         return out;
      } catch (Exception e) {
         LOGGER.error("[Millenaire] Error parsing {} ({} layer)", new Object[]{"blocklist.json", layer, e});
         return null;
      }
   }

   @Nullable
   private static BlockCostRegistry.Entry parseEntry(JsonElement element, String layer) {
      try {
         JsonObject obj = element.getAsJsonObject();
         String idStr = GsonHelper.getAsString(obj, "id");
         ResourceLocation id = ResourceLocation.tryParse(idStr);
         if (id == null) {
            LOGGER.warn("[Millenaire] {} ({} layer): invalid block id '{}' — entry ignored", new Object[]{"blocklist.json", layer, idStr});
            return null;
         }

         if (PROTECTED_NAMESPACES.contains(id.getNamespace())) {
            LOGGER.warn(
               "[Millenaire] {} ({} layer): block '{}' is in a protected namespace — millenaire: costs are built-in and cannot be overridden (entry ignored)",
               new Object[]{"blocklist.json", layer, id}
            );
            return null;
         }

         if (GAP_ONLY_NAMESPACES.contains(id.getNamespace()) && BuildingCostCalculator.hasBuiltInCost(id.toString())) {
            LOGGER.warn(
               "[Millenaire] {} ({} layer): block '{}' already has a built-in cost — only minecraft: blocks Millénaire leaves unpriced can be declared (entry ignored)",
               new Object[]{"blocklist.json", layer, id}
            );
            return null;
         }

         if (GAP_ONLY_NAMESPACES.contains(id.getNamespace()) && isUsedByCoreContent(id.toString())) {
            LOGGER.warn(
               "[Millenaire] {} ({} layer): block '{}' is used by Millénaire's own buildings — its cost is part of core balance and cannot be declared (entry ignored)",
               new Object[]{"blocklist.json", layer, id}
            );
            return null;
         }

         boolean disabled = GsonHelper.getAsBoolean(obj, "disabled", false);
         if (disabled) {
            return new BlockCostRegistry.Entry(id.toString(), null, true);
         }

         int quantity = GsonHelper.getAsInt(obj, "cost_quantity");
         if (quantity >= 0 && quantity <= 9999) {
            ResourceLocation costItem = null;
            if (quantity > 0) {
               String itemStr = GsonHelper.getAsString(obj, "cost_item");
               if ("anywood".equals(itemStr)) {
                  costItem = ResourceLocation.parse("millenaire:anywood_planks");
               } else {
                  costItem = ResourceLocation.tryParse(itemStr);
                  if (costItem == null) {
                     LOGGER.warn(
                        "[Millenaire] {} ({} layer): invalid cost_item '{}' for '{}' — entry ignored", new Object[]{"blocklist.json", layer, itemStr, id}
                     );
                     return null;
                  }

                  if (ItemHelper.resolve(costItem) == null) {
                     LOGGER.warn(
                        "[Millenaire] {} ({} layer): cost_item '{}' for '{}' is not a registered item — villagers will not be able to source it",
                        new Object[]{"blocklist.json", layer, costItem, id}
                     );
                  }
               }
            }

            if (BuildingCostCalculator.matchesFreeNameHeuristic(id.toString())) {
               LOGGER.warn(
                  "[Millenaire] {} ({} layer): entry for '{}' is shadowed by the free-block heuristic (leaves/saplings/flower pots are always free) — it will never apply",
                  new Object[]{"blocklist.json", layer, id}
               );
            }

            return new BlockCostRegistry.Entry(id.toString(), new BlockCostRegistry.BlockCost(costItem, quantity), false);
         } else {
            LOGGER.warn(
               "[Millenaire] {} ({} layer): cost_quantity {} for '{}' out of range [0, {}] — entry ignored",
               new Object[]{"blocklist.json", layer, quantity, id, 9999}
            );
            return null;
         }
      } catch (Exception e) {
         LOGGER.warn("[Millenaire] {} ({} layer): malformed entry ({}) — entry ignored", new Object[]{"blocklist.json", layer, e.getMessage()});
         return null;
      }
   }

   public record BlockCost(ResourceLocation costItem, int quantity) {
   }

   private record Entry(String id, BlockCostRegistry.BlockCost cost, boolean disabled) {
   }
}
