package org.millenaire.content.validate.semantic;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.Level;
import org.millenaire.Millenaire;
import org.millenaire.building.SpecialPoint;
import org.millenaire.content.BuiltInCultures;
import org.millenaire.content.ContentIssueLog;
import org.millenaire.content.validate.ContentIndex;
import org.millenaire.content.validate.Finding;
import org.millenaire.content.validate.Json;
import org.millenaire.content.validate.LoadCyclePreconditions;
import org.millenaire.content.validate.LoaderDiagnosticNormaliser;
import org.millenaire.content.validate.Severity;
import org.millenaire.content.validate.ValidationContext;
import org.millenaire.content.validate.ValidationRule;
import org.millenaire.culture.ModCultures;

public final class SemanticRules {
   private SemanticRules() {
   }

   public static List<ValidationRule> registryRules(ContentIndex index) {
      TemplateCache cache = new TemplateCache(index);
      return List.of(new SemanticRules.M01(), new SemanticRules.M02(cache), new SemanticRules.M03(cache), new SemanticRules.M06(cache));
   }

   public static ValidationRule loadCycleRule(List<Finding> staticFindings) {
      return new SemanticRules.M04(staticFindings);
   }

   public static List<ValidationRule> all(ContentIndex index, List<Finding> staticFindings) {
      List<ValidationRule> out = new ArrayList<>(registryRules(index));
      out.add(loadCycleRule(staticFindings));
      return out;
   }

   private static boolean itemResolves(ContentIndex index, String culture, String ref, RegistryProbe probe) {
      if (ref != null && !ref.isEmpty()) {
         if (culture != null) {
            ContentIndex.Entry good = index.tradedGoods(culture).get(ref);
            if (good != null) {
               String item = Json.string(good.json(), "item");
               return item != null && (item.startsWith("#") ? probe.itemTagExists(item) : probe.itemExists(item));
            }
         }

         return ref.startsWith("#") ? probe.itemTagExists(ref) : probe.itemExists(ref);
      } else {
         return false;
      }
   }

   private static String cultureOf(ContentIndex.Entry e) {
      String c = ContentIndex.culturePath(Json.string(e.json(), "culture"));
      return c != null ? c : e.culture();
   }

   static final class M01 implements ValidationRule {
      @Override
      public String id() {
         return "M-01";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         RegistryProbe probe = RegistryProbe.INSTANCE;

         for (String culture : index.cultures()) {
            for (ContentIndex.Entry good : index.tradedGoods(culture).values()) {
               String item = Json.string(good.json(), "item");
               if (item != null) {
                  boolean ok = item.startsWith("#") ? probe.itemTagExists(item) : probe.itemExists(item);
                  if (!ok) {
                     report(good, "item", item, out);
                  }
               }
            }
         }

         for (ContentIndex.Entry v : index.entries(ContentIndex.Family.VILLAGER_TYPE)) {
            if (v.json() != null) {
               String culture = SemanticRules.cultureOf(v);

               for (String field : List.of("bring_back_home_goods", "collect_goods", "items_needed")) {
                  for (String ref : Json.strings(v.json(), field)) {
                     if (!SemanticRules.itemResolves(index, culture, ref, probe)) {
                        report(v, field, ref, out);
                     }
                  }
               }

               for (String field : List.of("required_goods", "foreign_merchant_stock", "initial_inventory")) {
                  for (String ref : Json.keys(v.json(), field)) {
                     if (!SemanticRules.itemResolves(index, culture, ref, probe)) {
                        report(v, field, ref, out);
                     }
                  }
               }

               for (String field : List.of("default_weapon", "travelbook_held_item", "travelbook_held_item_off_hand")) {
                  String ref = Json.string(v.json(), field);
                  if (ref != null && !SemanticRules.itemResolves(index, culture, ref, probe)) {
                     report(v, field, ref, out);
                  }
               }
            }
         }

         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (p.json() != null) {
               String culture = SemanticRules.cultureOf(p);

               for (JsonObject sg : Json.objects(p.json(), "starting_goods")) {
                  String ref = Json.string(sg, "item");
                  if (ref != null && !SemanticRules.itemResolves(index, culture, ref, probe)) {
                     report(p, "starting_goods[].item", ref, out);
                  }
               }
            }
         }

         for (ContentIndex.Entry s : index.entries(ContentIndex.Family.SHOP)) {
            if (s.json() != null) {
               for (String ref : Json.strings(s.json(), "deliver_to")) {
                  if (!SemanticRules.itemResolves(index, s.culture(), ref, probe)) {
                     report(s, "deliver_to", ref, out);
                  }
               }
            }
         }

         for (ContentIndex.Entry q : index.entries(ContentIndex.Family.QUEST)) {
            if (q.json() != null) {
               for (JsonObject step : Json.objects(q.json(), "steps")) {
                  for (String field : List.of("requiredGoods", "rewardGoods")) {
                     for (String key : Json.keys(step, field)) {
                        String ref = key.contains("|") ? key.substring(0, key.indexOf(124)) : key;
                        if (!SemanticRules.itemResolves(index, null, ref, probe)) {
                           report(q, "steps[]." + field, ref, out);
                        }
                     }
                  }
               }
            }
         }

         for (ContentIndex.ToolCategoryLayer layer : index.toolCategoryLayers()) {
            JsonObject tools = layer.json();

            for (String category : tools.keySet()) {
               if (!category.startsWith("_")) {
                  for (JsonObject entry : Json.objects(tools, category)) {
                     String ref = Json.string(entry, "item");
                     if (ref != null && !probe.itemExists(ref)) {
                        out.add(
                           Finding.of(
                              "M-01",
                              Severity.ERROR,
                              "tool_categories",
                              "tool_categories:" + category,
                              layer.origin().label(),
                              "item '" + ref + "' is not a registered item — ToolCategoryRegistry ignores it at runtime.",
                              "fix the item id."
                           )
                        );
                     }
                  }
               }
            }
         }
      }

      private static void report(ContentIndex.Entry e, String field, String ref, List<Finding> out) {
         out.add(
            Finding.of(
               "M-01",
               Severity.ERROR,
               e.family().label(),
               e.identity(),
               e.origin().label(),
               field + " '" + ref + "' resolves to no registered item, item tag or traded good.",
               "fix the id (check the namespace and spelling)."
            )
         );
      }
   }

   static final class M02 implements ValidationRule {
      private final TemplateCache cache;

      M02(TemplateCache cache) {
         this.cache = cache;
      }

      @Override
      public String id() {
         return "M-02";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         RegistryProbe probe = RegistryProbe.INSTANCE;

         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            for (TemplateCache.Level level : this.cache.levels(p)) {
               if (level.readable()) {
                  Set<String> reported = new HashSet<>();

                  for (TemplateCache.PaletteEntry entry : level.palette()) {
                     String name = entry.block();
                     if (!name.isEmpty() && reported.add(name)) {
                        if (!probe.blockExists(name)) {
                           out.add(
                              Finding.of(
                                 "M-02",
                                 Severity.ERROR,
                                 "plan_set",
                                 p.identity(),
                                 level.nbtRelPath(),
                                 "palette block '" + name + "' is not a registered block (variant " + level.variant() + " level " + level.level() + ").",
                                 "re-export the template or fix the block id."
                              )
                           );
                        } else if (!entry.properties().isEmpty()) {
                           String bad = probe.invalidBlockProperty(name, entry.properties());
                           if (bad != null) {
                              out.add(
                                 Finding.of(
                                    "M-02",
                                    Severity.ERROR,
                                    "plan_set",
                                    p.identity(),
                                    level.nbtRelPath(),
                                    "palette block '"
                                       + name
                                       + "' has an invalid property "
                                       + bad
                                       + " (variant "
                                       + level.variant()
                                       + " level "
                                       + level.level()
                                       + ").",
                                    "fix the property in the template (a mock block subtype must be one the extractor knows)."
                                 )
                              );
                           }
                        }
                     }
                  }
               } else if (!"NBT missing".equals(level.readError())) {
                  out.add(
                     Finding.of(
                        "M-02",
                        Severity.ERROR,
                        "plan_set",
                        p.identity(),
                        level.nbtRelPath(),
                        "template cannot be read (" + level.readError() + ") — BuildingPlanSetLoader drops the whole plan set at load.",
                        "re-export the template (variant " + level.variant() + " level " + level.level() + ")."
                     )
                  );
               }
            }
         }
      }
   }

   static final class M03 implements ValidationRule {
      private final TemplateCache cache;

      M03(TemplateCache cache) {
         this.cache = cache;
      }

      @Override
      public String id() {
         return "M-03";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            JsonObject json = p.json();
            if (json != null) {
               boolean isSub = Json.bool(json, "is_sub_building", false);
               boolean hasShop = Json.string(json, "shop") != null;
               boolean rootResidents = !residents(json).isEmpty();

               for (JsonObject variant : Json.objects(json, "variants")) {
                  String letter = Json.string(variant, "variant");
                  boolean noPaths = false;
                  Set<String> types = new HashSet<>();
                  List<Integer> noSleeping = new ArrayList<>();
                  List<Integer> noSelling = new ArrayList<>();
                  List<Integer> noSellingNoFallback = new ArrayList<>();
                  List<Integer> noAnchor = new ArrayList<>();
                  List<Integer> noPathStart = new ArrayList<>();
                  String firstNbt = null;

                  for (JsonObject levelObj : Json.objects(variant, "levels")) {
                     if (levelObj.has("no_paths_to_building")) {
                        noPaths = Json.bool(levelObj, "no_paths_to_building", false);
                     }

                     Integer lvl = Json.integer(levelObj, "level");
                     TemplateCache.Level level = find(this.cache.levels(p), letter, lvl);
                     if (level != null && level.readable()) {
                        if (firstNbt == null) {
                           firstNbt = level.nbtRelPath();
                        }

                        for (SpecialPoint sp : level.points()) {
                           types.add(sp.type());
                        }

                        boolean residents = rootResidents || !residents(levelObj).isEmpty();
                        if (residents && !types.contains("sleepingPos")) {
                           noSleeping.add(lvl);
                        }

                        if (hasShop && !types.contains("sellingPos")) {
                           if (types.contains("sleepingPos")) {
                              noSelling.add(lvl);
                           } else {
                              noSellingNoFallback.add(lvl);
                           }
                        }

                        if (!noPaths && !isSub) {
                           boolean anchored = types.contains("pathStartPos") || types.contains("sellingPos") || types.contains("sleepingPos");
                           if (!anchored) {
                              noAnchor.add(lvl);
                           } else if (!types.contains("pathStartPos")) {
                              noPathStart.add(lvl);
                           }
                        }
                     }
                  }

                  if (firstNbt != null) {
                     if (!noSleeping.isEmpty()) {
                        out.add(
                           Finding.of(
                              "M-03",
                              Severity.ERROR,
                              "plan_set",
                              p.identity(),
                              firstNbt,
                              "variant "
                                 + letter
                                 + " houses residents but has no sleeping position at level(s) "
                                 + noSleeping
                                 + " (no mock marker, no sidecar entry).",
                              "add a sleepingPos marker to the template."
                           )
                        );
                     }

                     if (!noSelling.isEmpty()) {
                        out.add(
                           Finding.of(
                              "M-03",
                              Severity.INFO,
                              "plan_set",
                              p.identity(),
                              firstNbt,
                              "variant "
                                 + letter
                                 + " declares a shop but has no selling position at level(s) "
                                 + noSelling
                                 + " — the seller falls back to the sleeping position.",
                              "add a sellingPos marker to the template for an explicit counter."
                           )
                        );
                     }

                     if (!noSellingNoFallback.isEmpty()) {
                        out.add(
                           Finding.of(
                              "M-03",
                              Severity.WARN,
                              "plan_set",
                              p.identity(),
                              firstNbt,
                              "variant "
                                 + letter
                                 + " declares a shop but has neither a selling nor a sleeping position at level(s) "
                                 + noSellingNoFallback
                                 + " — the seller has no position to sell from.",
                              "add a sellingPos marker to the template."
                           )
                        );
                     }

                     if (!noAnchor.isEmpty()) {
                        out.add(
                           Finding.of(
                              "M-03",
                              Severity.ERROR,
                              "plan_set",
                              p.identity(),
                              firstNbt,
                              "variant "
                                 + letter
                                 + " has no path anchor at level(s) "
                                 + noAnchor
                                 + " (no path start, selling or sleeping position) and does not declare no_paths_to_building — the path system can only probe the perimeter.",
                              "add a pathStartPos marker or set \"no_paths_to_building\": true."
                           )
                        );
                     } else if (!noPathStart.isEmpty()) {
                        out.add(
                           Finding.of(
                              "M-03",
                              Severity.INFO,
                              "plan_set",
                              p.identity(),
                              firstNbt,
                              "variant "
                                 + letter
                                 + " has no path start position at level(s) "
                                 + noPathStart
                                 + "; the village path anchors on the selling/sleeping position instead.",
                              "add a pathStartPos marker for an explicit path end."
                           )
                        );
                     }
                  }
               }
            }
         }
      }

      private static List<String> residents(JsonObject obj) {
         List<String> out = new ArrayList<>();
         if (obj.has("male_residents")) {
            out.addAll(Json.strings(obj, "male_residents"));
         } else {
            out.addAll(Json.stringOrArray(obj, "male"));
         }

         if (obj.has("female_residents")) {
            out.addAll(Json.strings(obj, "female_residents"));
         } else {
            out.addAll(Json.stringOrArray(obj, "female"));
         }

         return out;
      }

      private static TemplateCache.Level find(List<TemplateCache.Level> levels, String variant, Integer level) {
         if (variant != null && level != null) {
            for (TemplateCache.Level l : levels) {
               if (l.variant().equals(variant) && l.level() == level) {
                  return l;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   static final class M04 implements ValidationRule {
      private final List<Finding> staticFindings;

      M04(List<Finding> staticFindings) {
         this.staticFindings = staticFindings;
      }

      @Override
      public String id() {
         return "M-04";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         LoadCyclePreconditions.check(Millenaire.getContentLoadFailure(), ContentIssueLog.captureWasInstalled(), ContentIssueLog.captureWasCompleted(), out);

         for (String culture : BuiltInCultures.IDS) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("millenaire", culture);
            if (ModCultures.getCulture(id) == null) {
               out.add(
                  Finding.of(
                     "M-04",
                     Severity.ERROR,
                     "culture",
                     culture + "/culture",
                     null,
                     "built-in culture '" + culture + "' is not registered after load.",
                     "see the loader errors for the culture."
                  )
               );
            }
         }

         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (p.json() != null) {
               String buildingId = Json.string(p.json(), "building_id");
               if (buildingId != null) {
                  ResourceLocation id = ResourceLocation.tryBuild("millenaire", SemanticRules.cultureOf(p) + "/" + buildingId);
                  if (id == null || ModCultures.getBuildingPlanSet(id) == null) {
                     unloaded(p, "plan set", out);
                  }
               }
            }
         }

         for (ContentIndex.Entry v : index.entries(ContentIndex.Family.VILLAGER_TYPE)) {
            if (v.json() != null) {
               ResourceLocation id = ResourceLocation.tryBuild("millenaire", v.culture() + "/" + v.id());
               if (id == null || ModCultures.getVillagerType(id) == null) {
                  unloaded(v, "villager type", out);
               }
            }
         }

         for (ContentIndex.Entry vt : index.entries(ContentIndex.Family.VILLAGE_TYPE)) {
            if (vt.json() != null) {
               ResourceLocation id = ResourceLocation.tryBuild("millenaire", vt.culture() + "/" + vt.id());
               if (id == null || ModCultures.getVillageType(id) == null) {
                  unloaded(vt, "village type", out);
               }
            }
         }

         Map<String, Integer> seen = new HashMap<>();

         for (ContentIssueLog.Entry e : ContentIssueLog.snapshot()) {
            LoaderDiagnosticNormaliser.Normalised n = LoaderDiagnosticNormaliser.normalise(e.message());
            boolean known = !n.ruleId().startsWith("M-04");
            if (!known || !LoaderDiagnosticNormaliser.covers(this.staticFindings, n.ruleId(), n.subject(), index.cultures())) {
               String ruleId = known ? "M-04/" + n.ruleId() : n.ruleId();
               String identity = n.subject() != null ? n.subject() : e.shortLogger();
               Severity severity = e.level().isMoreSpecificThan(Level.ERROR) ? Severity.ERROR : Severity.WARN;
               String key = ruleId + "\u0000" + identity + "\u0000" + e.message();
               if (seen.merge(key, 1, Integer::sum) <= 1) {
                  out.add(
                     Finding.of(
                        ruleId,
                        severity,
                        "load",
                        identity,
                        e.shortLogger(),
                        e.message() + (e.count() > 1 ? " (x" + e.count() + ")" : ""),
                        "see the loader message; if it is expected, allowlist it with a reason."
                     )
                  );
               }
            }
         }
      }

      private static void unloaded(ContentIndex.Entry e, String what, List<Finding> out) {
         out.add(
            Finding.of(
               "M-04",
               Severity.ERROR,
               e.family().label(),
               e.identity(),
               e.origin().label(),
               what + " is in the effective index but was not registered by the loaders.",
               "see the loader error for the file."
            )
         );
      }
   }

   static final class M06 implements ValidationRule {
      private final TemplateCache cache;

      M06(TemplateCache cache) {
         this.cache = cache;
      }

      @Override
      public String id() {
         return "M-06";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         Map<String, Set<String>> carriers = new HashMap<>();

         for (ContentIndex.Entry v : index.entries(ContentIndex.Family.VILLAGER_TYPE)) {
            if (v.json() != null) {
               for (String g : Json.strings(v.json(), "goals")) {
                  String id = ContentIndex.culturePath(g);
                  carriers.computeIfAbsent(id, k -> new HashSet<>()).add(SemanticRules.cultureOf(v) + "/" + v.id());
               }
            }
         }

         Map<String, Set<String>> subtypeCache = new HashMap<>();

         for (ContentIndex.Entry g : index.entries(ContentIndex.Family.GATHERING_TYPE)) {
            if (g.json() != null && "mining".equals(Json.string(g.json(), "handler"))) {
               JsonObject params = Json.object(g.json(), "handlerParams");
               String subtype = Json.string(params, "sourceSubtype");
               if (subtype != null) {
                  String buildingTag = Json.string(params, "buildingTag");
                  Set<String> villagers = carriers.getOrDefault(g.id(), Set.of());
                  if (!villagers.isEmpty()) {
                     for (ContentIndex.Entry vt : index.entries(ContentIndex.Family.VILLAGE_TYPE)) {
                        if (vt.json() != null) {
                           String culture = SemanticRules.cultureOf(vt);
                           List<ContentIndex.Entry> pool = operationalPool(index, vt, culture);
                           boolean carried = false;

                           for (ContentIndex.Entry plan : pool) {
                              for (String r : residentsOf(plan.json())) {
                                 if (villagers.contains(culture + "/" + r)) {
                                    carried = true;
                                 }
                              }
                           }

                           if (carried) {
                              Set<String> reachable = new TreeSet<>();

                              for (ContentIndex.Entry plan : pool) {
                                 if (buildingTag == null || planHasTag(plan.json(), buildingTag)) {
                                    reachable.addAll(subtypeCache.computeIfAbsent(plan.identity(), k -> this.sourceSubtypes(plan)));
                                 }
                              }

                              if (!reachable.contains(subtype)) {
                                 out.add(
                                    Finding.of(
                                       "M-06",
                                       Severity.ERROR,
                                       "gathering_type",
                                       "gathering_type/" + g.id() + "@" + vt.identity(),
                                       g.origin().label(),
                                       "sourceSubtype '"
                                          + subtype
                                          + "' is provided by no mock_source of the buildings village type "
                                          + vt.identity()
                                          + " can raise"
                                          + (buildingTag != null ? " (tag '" + buildingTag + "')" : "")
                                          + " — its villagers carry the goal but can never mine; reachable: "
                                          + reachable
                                          + ".",
                                       "add a mock_source of that subtype to a building of the village, or drop the goal from its villagers (or allowlist the divergence with a reason)."
                                    )
                                 );
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      private static List<ContentIndex.Entry> operationalPool(ContentIndex index, ContentIndex.Entry vt, String culture) {
         Map<String, ContentIndex.Entry> pool = new LinkedHashMap<>();

         for (JsonObject slot : Json.objects(vt.json(), "layout")) {
            String ref = ContentIndex.culturePath(Json.string(slot, "plan"));
            if (ref != null) {
               int slash = ref.indexOf(47);
               ContentIndex.Entry plan = slash > 0 ? index.planSet(ref.substring(0, slash), ref.substring(slash + 1)) : null;
               if (plan != null && plan.json() != null) {
                  pool.put(plan.identity(), plan);
               }

               if (plan != null && plan.json() != null) {
                  addSubs(index, plan, culture, pool);
               }
            }
         }

         boolean lone = Json.bool(vt.json(), "lone_building", false);
         String special = Json.string(vt.json(), "special_type");
         boolean extras = vt.json().has("allow_extra_buildings") ? Json.bool(vt.json(), "allow_extra_buildings", false) : !lone && special == null;
         if (Json.bool(vt.json(), "player_controlled", false)) {
            extras = false;
         }

         if (extras) {
            Set<String> excluded = Set.of("townhalls", "lone", "loneold", "player", "walls");
            Set<String> never = new HashSet<>(Json.strings(vt.json(), "never"));

            for (ContentIndex.Entry plan : index.entries(ContentIndex.Family.PLAN_SET, culture)) {
               if (plan.json() != null
                  && !excluded.contains(Json.string(plan.json(), "category", "houses"))
                  && !Json.bool(plan.json(), "is_sub_building", false)
                  && !Json.bool(plan.json(), "is_town_hall", false)
                  && Json.integer(plan.json(), "max_count", 1) != 0
                  && !never.contains(plan.id())) {
                  pool.putIfAbsent(plan.identity(), plan);
                  addSubs(index, plan, culture, pool);
               }
            }
         }

         return new ArrayList<>(pool.values());
      }

      private static void addSubs(ContentIndex index, ContentIndex.Entry plan, String culture, Map<String, ContentIndex.Entry> pool) {
         Set<String> subs = new HashSet<>(Json.strings(plan.json(), "starting_sub_buildings"));

         for (JsonObject v : Json.objects(plan.json(), "variants")) {
            for (JsonObject l : Json.objects(v, "levels")) {
               subs.addAll(Json.strings(l, "sub_buildings"));
            }
         }

         for (String sub : subs) {
            ContentIndex.Entry s = index.planSet(culture, sub);
            if (s != null && s.json() != null) {
               pool.putIfAbsent(s.identity(), s);
            }
         }
      }

      private static List<String> residentsOf(JsonObject json) {
         List<String> out = new ArrayList<>();
         out.addAll(json.has("male_residents") ? Json.strings(json, "male_residents") : Json.stringOrArray(json, "male"));
         out.addAll(json.has("female_residents") ? Json.strings(json, "female_residents") : Json.stringOrArray(json, "female"));

         for (JsonObject v : Json.objects(json, "variants")) {
            for (JsonObject l : Json.objects(v, "levels")) {
               out.addAll(Json.stringOrArray(l, "male"));
               out.addAll(Json.stringOrArray(l, "female"));
            }
         }

         return out;
      }

      private static boolean planHasTag(JsonObject json, String tag) {
         if (Json.strings(json, "tags").contains(tag)) {
            return true;
         }

         for (JsonObject v : Json.objects(json, "variants")) {
            if (Json.strings(v, "tags").contains(tag)) {
               return true;
            }

            for (JsonObject l : Json.objects(v, "levels")) {
               if (Json.strings(l, "tags").contains(tag)) {
                  return true;
               }
            }
         }

         return false;
      }

      private Set<String> sourceSubtypes(ContentIndex.Entry plan) {
         Set<String> out = new HashSet<>();

         for (TemplateCache.Level level : this.cache.levels(plan)) {
            for (SpecialPoint sp : level.points()) {
               if ("source".equals(sp.type()) && sp.subtype() != null) {
                  out.add(sp.subtype());
               }
            }
         }

         return out;
      }
   }
}
