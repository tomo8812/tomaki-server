package org.millenaire.test;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.millenaire.Millenaire;
import org.millenaire.building.BuildingPlanSet;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillagerType;
import org.millenaire.goal.gathering.GatheringGoal;
import org.millenaire.goal.gathering.GatheringType;
import org.slf4j.Logger;

@GameTestHolder("millenaire")
@PrefixGameTestTemplate(false)
public class TownhallLimitAuditGameTests {
   private static final Logger LOGGER = LogUtils.getLogger();

   @GameTest(template = "empty_platform", setupTicks = 1L, timeoutTicks = 400, required = false)
   public static void townhallLimitCoversEveryReachableConstructionRequirement(GameTestHelper helper) {
      Map<ResourceLocation, Map<ResourceLocation, List<TownhallLimitAuditGameTests.CapSource>>> capsByCultureThenItem = collectReachableCaps();
      Map<ResourceLocation, Map<ResourceLocation, Integer>> requiredByCultureThenItem = new HashMap<>();
      Map<ResourceLocation, Map<ResourceLocation, TownhallLimitAuditGameTests.RequirementSource>> requirementSourceByCultureThenItem = new HashMap<>();
      collectConstructionRequirements(requiredByCultureThenItem, requirementSourceByCultureThenItem);
      List<String> insufficientFindings = new ArrayList<>();
      List<String> inconsistentFindings = new ArrayList<>();

      for (Entry<ResourceLocation, Map<ResourceLocation, Integer>> cultureEntry : requiredByCultureThenItem.entrySet()) {
         ResourceLocation culture = cultureEntry.getKey();
         Map<ResourceLocation, List<TownhallLimitAuditGameTests.CapSource>> capsByItem = capsByCultureThenItem.getOrDefault(culture, Map.of());

         for (Entry<ResourceLocation, Integer> itemEntry : cultureEntry.getValue().entrySet()) {
            ResourceLocation item = itemEntry.getKey();
            int required = itemEntry.getValue();
            List<TownhallLimitAuditGameTests.CapSource> rawCaps = capsByItem.get(item);
            if (rawCaps != null && !rawCaps.isEmpty()) {
               List<TownhallLimitAuditGameTests.CapSource> caps = dedupeCaps(rawCaps);
               int effectiveCap = caps.stream().mapToInt(TownhallLimitAuditGameTests.CapSource::limit).max().orElseThrow();
               if (required > effectiveCap) {
                  TownhallLimitAuditGameTests.RequirementSource src = requirementSourceByCultureThenItem.get(culture).get(item);
                  insufficientFindings.add(
                     String.format(
                        "[%s] %s: requires %d at %s (variant %s, level %d), but the highest reachable townhallLimit is %d (%s)",
                        culture,
                        item,
                        required,
                        src.buildingPlanId(),
                        src.variant(),
                        src.level(),
                        effectiveCap,
                        describeCaps(caps)
                     )
                  );
               }

               long distinctLimits = caps.stream().mapToInt(TownhallLimitAuditGameTests.CapSource::limit).distinct().count();
               if (distinctLimits > 1L) {
                  inconsistentFindings.add(String.format("[%s] %s: inconsistent townhallLimit across producers — %s", culture, item, describeCaps(caps)));
               }
            }
         }
      }

      if (insufficientFindings.isEmpty() && inconsistentFindings.isEmpty()) {
         helper.succeed();
      } else {
         StringBuilder sb = new StringBuilder();
         if (!insufficientFindings.isEmpty()) {
            sb.append(insufficientFindings.size()).append(" material(s) with townhallLimit lower than a reachable building requires:\n");
            insufficientFindings.forEach(f -> sb.append("  - ").append(f).append('\n'));
         }

         if (!inconsistentFindings.isEmpty()) {
            sb.append(inconsistentFindings.size()).append(" material(s) with inconsistent townhallLimit across producers:\n");
            inconsistentFindings.forEach(f -> sb.append("  - ").append(f).append('\n'));
         }

         String report = sb.toString();
         LOGGER.warn("[Millenaire] TownhallLimitAudit findings:\n{}", report);
         helper.fail(
            String.format(
               "TownhallLimitAudit: %d insufficient, %d inconsistent — see server log '[Millenaire] TownhallLimitAudit findings' for the full report",
               insufficientFindings.size(),
               inconsistentFindings.size()
            )
         );
      }
   }

   private static List<TownhallLimitAuditGameTests.CapSource> dedupeCaps(List<TownhallLimitAuditGameTests.CapSource> caps) {
      Map<ResourceLocation, TownhallLimitAuditGameTests.CapSource> byProducer = new LinkedHashMap<>();

      for (TownhallLimitAuditGameTests.CapSource c : caps) {
         byProducer.putIfAbsent(c.gatheringTypeId(), c);
      }

      return new ArrayList<>(byProducer.values());
   }

   private static String describeCaps(List<TownhallLimitAuditGameTests.CapSource> caps) {
      return caps.stream().map(c -> c.gatheringTypeId() + "=" + c.limit()).collect(Collectors.joining(", "));
   }

   private static Map<ResourceLocation, Map<ResourceLocation, List<TownhallLimitAuditGameTests.CapSource>>> collectReachableCaps() {
      Map<ResourceLocation, Map<ResourceLocation, List<TownhallLimitAuditGameTests.CapSource>>> result = new HashMap<>();

      for (VillagerType villagerType : ModCultures.getAllVillagerTypes().values()) {
         ResourceLocation culture = villagerType.culture();

         for (ResourceLocation goalId : villagerType.goals()) {
            if (Millenaire.getGoalRegistry().get(goalId) instanceof GatheringGoal gatheringGoal) {
               GatheringType type = gatheringGoal.getGatheringType();
               if (type.townhallLimit() != null) {
                  for (Entry<String, Integer> e : type.townhallLimit().entrySet()) {
                     String key = e.getKey();
                     if (!key.startsWith("#")) {
                        ResourceLocation item = ResourceLocation.parse(key);
                        result.computeIfAbsent(culture, k -> new HashMap<>())
                           .computeIfAbsent(item, k -> new ArrayList<>())
                           .add(new TownhallLimitAuditGameTests.CapSource(type.id(), e.getValue()));
                     }
                  }
               }
            }
         }
      }

      return result;
   }

   private static void collectConstructionRequirements(
      Map<ResourceLocation, Map<ResourceLocation, Integer>> requiredOut,
      Map<ResourceLocation, Map<ResourceLocation, TownhallLimitAuditGameTests.RequirementSource>> sourceOut
   ) {
      for (BuildingPlanSet plan : ModCultures.getAllBuildingPlanSets().values()) {
         if (!plan.isWallSegment() && !"marvel".equals(plan.category())) {
            ResourceLocation culture = plan.culture();

            for (Entry<String, List<BuildingPlanSet.LevelDef>> variantEntry : plan.variants().entrySet()) {
               String variant = variantEntry.getKey();

               for (BuildingPlanSet.LevelDef level : variantEntry.getValue()) {
                  if (!"lone".equals(plan.category()) || level.level() != 0) {
                     for (Entry<ResourceLocation, Integer> req : level.requiredResources().entrySet()) {
                        ResourceLocation item = req.getKey();
                        int qty = req.getValue();
                        Map<ResourceLocation, Integer> perItem = requiredOut.computeIfAbsent(culture, k -> new HashMap<>());
                        Integer prevMax = perItem.get(item);
                        if (prevMax == null || qty > prevMax) {
                           perItem.put(item, qty);
                           sourceOut.computeIfAbsent(culture, k -> new HashMap<>())
                              .put(item, new TownhallLimitAuditGameTests.RequirementSource(plan.id(), variant, level.level()));
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private record CapSource(ResourceLocation gatheringTypeId, int limit) {
   }

   private record RequirementSource(ResourceLocation buildingPlanId, String variant, int level) {
   }
}
