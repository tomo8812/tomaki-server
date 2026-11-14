package org.millenaire.content.validate.rules;

import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import org.millenaire.content.SourceKind;
import org.millenaire.content.validate.ContentIndex;
import org.millenaire.content.validate.Finding;
import org.millenaire.content.validate.Json;
import org.millenaire.content.validate.Severity;
import org.millenaire.content.validate.ValidationContext;
import org.millenaire.content.validate.ValidationRule;

public final class CrossReferenceRules {
   private static final Pattern ITEM_ID = Pattern.compile("^(#)?([a-z0-9_.-]+:)?[a-z0-9_./-]+$");

   private CrossReferenceRules() {
   }

   public static List<ValidationRule> all() {
      return List.of(
         new CrossReferenceRules.X01(),
         new CrossReferenceRules.X02(),
         new CrossReferenceRules.X03(),
         new CrossReferenceRules.X04(),
         new CrossReferenceRules.X05(),
         new CrossReferenceRules.X07(),
         new CrossReferenceRules.X08(),
         new CrossReferenceRules.X09(),
         new CrossReferenceRules.X10(),
         new CrossReferenceRules.X11()
      );
   }

   static String path(String ref) {
      if (ref == null) {
         return null;
      }

      int colon = ref.indexOf(58);
      return colon < 0 ? ref : ref.substring(colon + 1);
   }

   static String declaredCulture(ContentIndex.Entry e) {
      String c = ContentIndex.culturePath(Json.string(e.json(), "culture"));
      return c != null ? c : e.culture();
   }

   static Set<String> goalCatalogue(ContentIndex index, ValidationContext ctx) {
      Set<String> ids = new HashSet<>(ctx.engineGoalIds());

      for (ContentIndex.Entry e : index.entries(ContentIndex.Family.GATHERING_TYPE)) {
         ids.add(e.id().toLowerCase(Locale.ROOT));
      }

      for (ContentIndex.Entry e : index.entries(ContentIndex.Family.VISIT_GOAL)) {
         ids.add(e.id());
      }

      for (ContentIndex.Entry e : index.entries(ContentIndex.Family.CULTURE_GOAL)) {
         ids.add(e.id());
      }

      return ids;
   }

   static Map<String, Set<String>> goalsByCulture(ContentIndex index) {
      Map<String, Set<String>> out = new HashMap<>();

      for (ContentIndex.Entry v : index.entries(ContentIndex.Family.VILLAGER_TYPE)) {
         if (v.json() != null) {
            Set<String> set = out.computeIfAbsent(declaredCulture(v), k -> new HashSet<>());

            for (String g : Json.strings(v.json(), "goals")) {
               set.add(path(g));
            }
         }
      }

      return out;
   }

   static Set<String> buildingTags(ContentIndex index, String culture) {
      Set<String> tags = new HashSet<>();

      for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET, culture)) {
         if (p.json() != null) {
            tags.addAll(Json.strings(p.json(), "tags"));

            for (JsonObject v : Json.objects(p.json(), "variants")) {
               tags.addAll(Json.strings(v, "tags"));

               for (JsonObject l : Json.objects(v, "levels")) {
                  tags.addAll(Json.strings(l, "tags"));
               }
            }
         }
      }

      return tags;
   }

   static boolean itemRefIsValid(ContentIndex index, String culture, String ref) {
      if (ref != null && !ref.isEmpty()) {
         return culture != null && index.tradedGoods(culture).containsKey(ref) ? true : ITEM_ID.matcher(ref).matches();
      } else {
         return false;
      }
   }

   static final class X01 implements ValidationRule {
      @Override
      public String id() {
         return "X-01";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         Set<String> catalogue = CrossReferenceRules.goalCatalogue(index, ctx);

         for (ContentIndex.Entry v : index.entries(ContentIndex.Family.VILLAGER_TYPE)) {
            if (v.json() != null) {
               for (String goal : Json.strings(v.json(), "goals")) {
                  String id = CrossReferenceRules.path(goal);
                  if (!catalogue.contains(id) && !catalogue.contains(id.toLowerCase(Locale.ROOT))) {
                     out.add(
                        Finding.of(
                           "X-01",
                           Severity.ERROR,
                           "villager_type",
                           v.identity(),
                           v.origin().label(),
                           "goal '" + goal + "' is neither an engine goal nor a gathering type, visit goal or culture goal — the villager never runs it.",
                           "fix the id, or add gathering_type/<id>.json / visit_goal/<id>.json."
                        )
                     );
                  }
               }
            }
         }
      }
   }

   static final class X02 implements ValidationRule {
      @Override
      public String id() {
         return "X-02";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (p.json() != null) {
               String culture = CrossReferenceRules.declaredCulture(p);
               Set<String> refs = new LinkedHashSet<>();
               refs.addAll(residents(p.json(), "male_residents", "male"));
               refs.addAll(residents(p.json(), "female_residents", "female"));
               refs.addAll(Json.strings(p.json(), "visitors"));

               for (JsonObject v : Json.objects(p.json(), "variants")) {
                  for (JsonObject l : Json.objects(v, "levels")) {
                     refs.addAll(Json.stringOrArray(l, "male"));
                     refs.addAll(Json.stringOrArray(l, "female"));
                  }
               }

               for (String ref : refs) {
                  if (index.villagerType(culture, ref) == null) {
                     out.add(
                        Finding.of(
                           "X-02",
                           Severity.ERROR,
                           "plan_set",
                           p.identity(),
                           p.origin().label(),
                           "resident/visitor '" + ref + "' is not a villager type of culture '" + culture + "' — the slot never spawns.",
                           "fix the id or add cultures/" + culture + "/villagers/<category>/" + ref + ".json."
                        )
                     );
                  }
               }
            }
         }
      }

      private static List<String> residents(JsonObject json, String canonical, String alias) {
         return json.has(canonical) ? Json.strings(json, canonical) : Json.stringOrArray(json, alias);
      }
   }

   static final class X03 implements ValidationRule {
      @Override
      public String id() {
         return "X-03";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (ContentIndex.Entry vt : index.entries(ContentIndex.Family.VILLAGE_TYPE)) {
            if (vt.json() != null) {
               String culture = CrossReferenceRules.declaredCulture(vt);

               for (JsonObject slot : Json.objects(vt.json(), "layout")) {
                  checkPlanRef(index, vt, Json.string(slot, "plan"), "layout slot", out);
               }

               for (String pb : Json.strings(vt.json(), "player_buildings")) {
                  checkPlanRef(index, vt, pb, "player building", out);
               }

               for (String h : Json.strings(vt.json(), "hamlets")) {
                  String p = CrossReferenceRules.path(h);
                  int slash = p.indexOf(47);
                  ContentIndex.Entry target = slash > 0 ? index.villageType(p.substring(0, slash), p.substring(slash + 1)) : null;
                  if (target == null) {
                     out.add(
                        Finding.of(
                           "X-03",
                           Severity.ERROR,
                           "village_type",
                           vt.identity(),
                           vt.origin().label(),
                           "hamlet '" + h + "' is not a village type.",
                           "fix the id or add cultures/<c>/villages/<id>.json."
                        )
                     );
                  }
               }

               for (String never : Json.strings(vt.json(), "never")) {
                  if (index.planSet(culture, never) == null) {
                     out.add(
                        Finding.of(
                           "X-03",
                           Severity.ERROR,
                           "village_type",
                           vt.identity(),
                           vt.origin().label(),
                           "'never' entry '" + never + "' is not a plan set of culture '" + culture + "' — the exclusion has no effect.",
                           "fix the building id."
                        )
                     );
                  }
               }

               for (String field : List.of("outer_wall_type", "inner_wall_type")) {
                  String ref = Json.string(vt.json(), field);
                  if (ref != null) {
                     String id = ref.contains(":") ? CrossReferenceRules.path(ref) : culture + "/" + ref.toLowerCase(Locale.ROOT);
                     boolean found = index.entries(ContentIndex.Family.WALL_TYPE).stream().anyMatch(w -> w.id().equalsIgnoreCase(id));
                     if (!found) {
                        out.add(
                           Finding.of(
                              "X-03",
                              Severity.ERROR,
                              "village_type",
                              vt.identity(),
                              vt.origin().label(),
                              field + " '" + ref + "' is not a wall type (expected wall_type/" + id + ".json).",
                              "fix the reference or add the wall type."
                           )
                        );
                     }
                  }
               }
            }
         }
      }

      private static void checkPlanRef(ContentIndex index, ContentIndex.Entry vt, String ref, String what, List<Finding> out) {
         if (ref != null) {
            String p = CrossReferenceRules.path(ref);
            int slash = p.indexOf(47);
            ContentIndex.Entry target = slash > 0 ? index.planSet(p.substring(0, slash), p.substring(slash + 1)) : null;
            if (target == null) {
               out.add(
                  Finding.of(
                     "X-03",
                     Severity.ERROR,
                     "village_type",
                     vt.identity(),
                     vt.origin().label(),
                     what + " '" + ref + "' is not a plan set — the village type is disabled at load.",
                     "fix the id or add the plan set."
                  )
               );
            }
         }
      }
   }

   static final class X04 implements ValidationRule {
      @Override
      public String id() {
         return "X-04";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         Set<String> referenced = new HashSet<>();

         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (p.json() != null) {
               String shop = Json.string(p.json(), "shop");
               if (shop != null) {
                  String culture = CrossReferenceRules.declaredCulture(p);
                  referenced.add(culture + "/" + shop);
                  if (index.shop(culture, shop) == null) {
                     out.add(
                        Finding.of(
                           "X-04",
                           Severity.ERROR,
                           "plan_set",
                           p.identity(),
                           p.origin().label(),
                           "shop '" + shop + "' has no profile at cultures/" + culture + "/shops/" + shop + ".json — the building trades nothing.",
                           "add the shop profile or fix the reference."
                        )
                     );
                  }
               }
            }
         }

         for (ContentIndex.Entry s : index.entries(ContentIndex.Family.SHOP)) {
            if (!referenced.contains(s.culture() + "/" + s.id())) {
               out.add(
                  Finding.of(
                     "X-04",
                     Severity.WARN,
                     "shop",
                     s.identity(),
                     s.origin().label(),
                     "shop profile is referenced by no plan set of the culture — dead file.",
                     "reference it from a plan set's 'shop' field or delete it."
                  )
               );
            }

            if (s.json() != null) {
               Map<String, ContentIndex.Entry> goods = index.tradedGoods(s.culture());

               for (String field : List.of("sells", "buys", "buys_optional")) {
                  for (String good : Json.strings(s.json(), field)) {
                     if (!goods.containsKey(good)) {
                        out.add(
                           Finding.of(
                              "X-04",
                              Severity.ERROR,
                              "shop",
                              s.identity(),
                              s.origin().label(),
                              field + " entry '" + good + "' is not a traded good of culture '" + s.culture() + "'.",
                              "fix the good id or add it to traded_goods.json."
                           )
                        );
                     }
                  }
               }

               for (String item : Json.strings(s.json(), "deliver_to")) {
                  if (!CrossReferenceRules.itemRefIsValid(index, s.culture(), item)) {
                     out.add(
                        Finding.of(
                           "X-04",
                           Severity.ERROR,
                           "shop",
                           s.identity(),
                           s.origin().label(),
                           "deliver_to entry '" + item + "' is not a valid item id.",
                           "use a namespace:path item id."
                        )
                     );
                  }
               }
            }
         }
      }
   }

   static final class X05 implements ValidationRule {
      @Override
      public String id() {
         return "X-05";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         Map<String, Set<String>> goalsByCulture = CrossReferenceRules.goalsByCulture(index);
         Map<String, Set<String>> tagCache = new HashMap<>();

         for (ContentIndex.Entry g : index.entries(ContentIndex.Family.GATHERING_TYPE)) {
            if (g.json() != null) {
               String handler = Json.string(g.json(), "handler");
               if (handler != null) {
                  if (!ctx.isHandler(handler)) {
                     out.add(
                        Finding.of(
                           "X-05",
                           Severity.ERROR,
                           "gathering_type",
                           g.identity(),
                           g.origin().label(),
                           "handler '" + handler + "' is not a registered gathering handler — the type is skipped.",
                           "use one of the ids in BuiltInGatheringHandlers."
                        )
                     );
                  }

                  Set<String> cultures = new TreeSet<>();

                  for (Entry<String, Set<String>> e : goalsByCulture.entrySet()) {
                     if (e.getValue().contains(g.id())) {
                        cultures.add(e.getKey());
                     }
                  }

                  if (!cultures.isEmpty()) {
                     Set<String> vocabulary = new HashSet<>();

                     for (String c : cultures) {
                        vocabulary.addAll(tagCache.computeIfAbsent(c, k -> CrossReferenceRules.buildingTags(index, k)));
                     }

                     JsonObject params = Json.object(g.json(), "handlerParams");
                     checkTag(g, Json.string(params, "buildingTag"), "handlerParams.buildingTag", vocabulary, cultures, out);
                     checkTag(g, Json.string(params, "requiredTag"), "handlerParams.requiredTag", vocabulary, cultures, out);
                     checkTag(g, Json.string(g.json(), "destinationBuilding"), "destinationBuilding", vocabulary, cultures, out);
                  }
               }
            }
         }
      }

      private static void checkTag(ContentIndex.Entry g, String tag, String field, Set<String> vocabulary, Set<String> cultures, List<Finding> out) {
         if (tag != null && !vocabulary.contains(tag)) {
            out.add(
               Finding.of(
                  "X-05",
                  Severity.ERROR,
                  "gathering_type",
                  g.identity(),
                  g.origin().label(),
                  field
                     + " '"
                     + tag
                     + "' is declared by no plan set of "
                     + cultures
                     + " (the cultures whose villagers run this goal) — the goal never finds a building.",
                  "fix the tag or add it to a plan set's tags."
               )
            );
         }
      }
   }

   static final class X07 implements ValidationRule {
      @Override
      public String id() {
         return "X-07";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (ContentIndex.Entry v : index.entries(ContentIndex.Family.VILLAGER_TYPE)) {
            if (v.json() != null) {
               String culture = CrossReferenceRules.declaredCulture(v);

               for (String field : List.of("bring_back_home_goods", "collect_goods", "items_needed")) {
                  for (String ref : Json.strings(v.json(), field)) {
                     report(index, v, culture, field, ref, out);
                  }
               }

               for (String field : List.of("required_goods", "foreign_merchant_stock", "initial_inventory")) {
                  for (String ref : Json.keys(v.json(), field)) {
                     report(index, v, culture, field, ref, out);
                  }
               }

               for (String field : List.of("default_weapon", "travelbook_held_item", "travelbook_held_item_off_hand")) {
                  String ref = Json.string(v.json(), field);
                  if (ref != null) {
                     report(index, v, culture, field, ref, out);
                  }
               }
            }
         }

         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (p.json() != null) {
               String culture = CrossReferenceRules.declaredCulture(p);

               for (JsonObject sg : Json.objects(p.json(), "starting_goods")) {
                  String ref = Json.string(sg, "item");
                  if (ref != null) {
                     report(index, p, culture, "starting_goods[].item", ref, out);
                  }
               }

               for (JsonObject v : Json.objects(p.json(), "variants")) {
                  for (JsonObject l : Json.objects(v, "levels")) {
                     for (String ap : Json.strings(l, "abstracted_production")) {
                        int comma = ap.indexOf(44);
                        String good = comma > 0 ? ap.substring(0, comma).trim() : ap.trim();
                        if (!index.tradedGoods(culture).containsKey(good)) {
                           out.add(
                              Finding.of(
                                 "X-07",
                                 Severity.ERROR,
                                 "plan_set",
                                 p.identity(),
                                 p.origin().label(),
                                 "abstracted_production '" + ap + "' names no traded good of culture '" + culture + "' (matched by good id at donation time).",
                                 "use a traded_goods.json id."
                              )
                           );
                        }
                     }
                  }
               }
            }
         }
      }

      private static void report(ContentIndex index, ContentIndex.Entry e, String culture, String field, String ref, List<Finding> out) {
         if (!CrossReferenceRules.itemRefIsValid(index, culture, ref)) {
            out.add(
               Finding.of(
                  "X-07",
                  Severity.ERROR,
                  e.family().label(),
                  e.identity(),
                  e.origin().label(),
                  field + " '" + ref + "' is neither a traded good of '" + culture + "' nor a syntactically valid item id (lowercase namespace:path).",
                  "use a namespace:path item id or a traded good id."
               )
            );
         }
      }
   }

   static final class X08 implements ValidationRule {
      @Override
      public String id() {
         return "X-08";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         Set<String> cultures = new HashSet<>(index.cultures());

         for (ContentIndex.Entry q : index.entries(ContentIndex.Family.QUEST)) {
            if (q.json() != null) {
               Set<String> keys = new HashSet<>();

               for (JsonObject def : Json.objects(q.json(), "villagerDefs")) {
                  String key = Json.string(def, "key");
                  if (key != null) {
                     keys.add(key);
                  }
               }

               for (JsonObject def : Json.objects(q.json(), "villagerDefs")) {
                  for (String type : Json.strings(def, "villagerTypes")) {
                     String p = CrossReferenceRules.path(type);
                     int slash = p.indexOf(47);
                     ContentIndex.Entry target = slash > 0 ? index.villagerType(p.substring(0, slash), p.substring(slash + 1)) : null;
                     if (target == null) {
                        out.add(
                           Finding.of(
                              "X-08",
                              Severity.ERROR,
                              "quest",
                              q.identity(),
                              q.origin().label(),
                              "villagerDefs[" + Json.string(def, "key") + "].villagerTypes '" + type + "' is not a villager type.",
                              "fix the id (culture/id)."
                           )
                        );
                     }
                  }

                  String relatedTo = Json.string(def, "relatedTo");
                  if (relatedTo != null && !keys.contains(relatedTo)) {
                     keyMissing(q, "villagerDefs[].relatedTo", relatedTo, keys, out);
                  }
               }

               for (JsonObject step : Json.objects(q.json(), "steps")) {
                  String vk = Json.string(step, "villagerKey");
                  if (vk != null && !keys.contains(vk)) {
                     keyMissing(q, "steps[].villagerKey", vk, keys, out);
                  }

                  for (String field : List.of("villagerTagsSuccess", "villagerTagsFailure", "clearTagsSuccess", "clearTagsFailure")) {
                     for (JsonObject action : Json.objects(step, field)) {
                        String k = Json.string(action, "villagerKey");
                        if (k != null && !keys.contains(k)) {
                           keyMissing(q, "steps[]." + field + "[].villagerKey", k, keys, out);
                        }
                     }
                  }

                  for (JsonObject rc : Json.objects(step, "relationChanges")) {
                     for (String field : List.of("firstVillager", "secondVillager")) {
                        String k = Json.string(rc, field);
                        if (k != null && !keys.contains(k)) {
                           keyMissing(q, "steps[].relationChanges[]." + field, k, keys, out);
                        }
                     }
                  }

                  for (JsonObject bb : Json.objects(step, "bedrockBuildings")) {
                     String culture = ContentIndex.culturePath(Json.string(bb, "culture"));
                     String villageType = Json.string(bb, "villageType");
                     if (culture != null && !cultures.contains(culture)) {
                        out.add(
                           Finding.of(
                              "X-08",
                              Severity.ERROR,
                              "quest",
                              q.identity(),
                              q.origin().label(),
                              "bedrockBuildings culture '" + culture + "' is not a culture.",
                              "fix the culture id."
                           )
                        );
                     } else if (culture != null
                        && villageType != null
                        && index.villageType(culture, CrossReferenceRules.path(villageType).replace(culture + "/", "")) == null) {
                        out.add(
                           Finding.of(
                              "X-08",
                              Severity.ERROR,
                              "quest",
                              q.identity(),
                              q.origin().label(),
                              "bedrockBuildings villageType '" + villageType + "' is not a village type of '" + culture + "'.",
                              "fix the village type id."
                           )
                        );
                     }
                  }
               }
            }
         }
      }

      private static void keyMissing(ContentIndex.Entry q, String field, String key, Set<String> keys, List<Finding> out) {
         out.add(
            Finding.of(
               "X-08",
               Severity.ERROR,
               "quest",
               q.identity(),
               q.origin().label(),
               field + " '" + key + "' is not a villagerDefs key of this quest (" + new TreeSet<String>(keys) + ").",
               "use one of the declared keys."
            )
         );
      }
   }

   static final class X09 implements ValidationRule {
      @Override
      public String id() {
         return "X-09";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         if (!ctx.hasEnUs()) {
            out.add(
               Finding.of(
                  "X-09",
                  Severity.ERROR,
                  "languages",
                  "assets/millenaire/lang/en_us.json",
                  "/assets/millenaire/lang/en_us.json",
                  "en_us.json is not on the classpath — key existence cannot be checked.",
                  "ship assets/millenaire/lang/en_us.json."
               )
            );
         } else {
            List<CrossReferenceRules.X09.Need> needs = new ArrayList<>();
            Set<String> seenGoals = new HashSet<>();

            for (ContentIndex.Entry c : index.entries(ContentIndex.Family.CULTURE)) {
               needs.add(new CrossReferenceRules.X09.Need(c, "culture.millenaire." + c.culture()));
            }

            for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
               if (p.json() != null) {
                  String buildingId = Json.string(p.json(), "building_id");
                  if (buildingId != null) {
                     needs.add(new CrossReferenceRules.X09.Need(p, "building.millenaire." + CrossReferenceRules.declaredCulture(p) + "." + buildingId));
                  }
               }
            }

            for (ContentIndex.Entry v : index.entries(ContentIndex.Family.VILLAGER_TYPE)) {
               if (v.json() != null) {
                  needs.add(new CrossReferenceRules.X09.Need(v, "role.millenaire." + v.culture() + "_" + v.id()));

                  for (String goal : Json.strings(v.json(), "goals")) {
                     String key = "goal.millenaire." + CrossReferenceRules.path(goal);
                     if (seenGoals.add(key)) {
                        needs.add(new CrossReferenceRules.X09.Need(v, key));
                     }
                  }
               }
            }

            for (ContentIndex.Entry vg : index.entries(ContentIndex.Family.VISIT_GOAL)) {
               if (vg.json() != null) {
                  String goalKey = Json.string(vg.json(), "goalKey");
                  if (goalKey != null) {
                     String key = "goal.millenaire." + goalKey;
                     if (seenGoals.add(key)) {
                        needs.add(new CrossReferenceRules.X09.Need(vg, key));
                     }
                  }
               }
            }

            for (ContentIndex.Family f : List.of(ContentIndex.Family.REPUTATION, ContentIndex.Family.CULTURE_REPUTATION)) {
               for (ContentIndex.Entry r : index.entries(f)) {
                  if (r.json() != null) {
                     for (JsonObject label : Json.objects(r.json(), "labels")) {
                        String key = Json.string(label, "key");
                        if (key != null) {
                           needs.add(new CrossReferenceRules.X09.Need(r, key));
                        }
                     }
                  }
               }
            }

            report(index, ctx, needs, out);
         }
      }

      private static boolean resolves(ContentIndex index, ValidationContext ctx, CrossReferenceRules.X09.Need need) {
         if (ctx.hasKey(need.key())) {
            return true;
         }

         String culture = need.subject().culture();
         return culture == null ? index.allCultureLangKeys().contains(need.key()) : index.cultureLangKeys(culture).contains(need.key());
      }

      private static void report(ContentIndex index, ValidationContext ctx, List<CrossReferenceRules.X09.Need> needs, List<Finding> out) {
         Set<String> culturesWithKeys = new HashSet<>();

         for (CrossReferenceRules.X09.Need need : needs) {
            if (resolves(index, ctx, need)) {
               culturesWithKeys.add(String.valueOf(need.subject().culture()));
            }
         }

         for (CrossReferenceRules.X09.Need need : needs) {
            if (!resolves(index, ctx, need)) {
               ContentIndex.Entry e = need.subject();
               boolean translated = culturesWithKeys.contains(String.valueOf(e.culture()));
               out.add(
                  Finding.of(
                     "X-09",
                     translated ? Severity.ERROR : Severity.WARN,
                     e.family().label(),
                     e.identity(),
                     e.origin().label(),
                     "en_us.json has no '" + need.key() + "' — players see the raw key.",
                     fixFor(e, translated)
                  )
               );
            }
         }
      }

      private static String fixFor(ContentIndex.Entry e, boolean translated) {
         if (e.origin().kind() == SourceKind.CLASSPATH) {
            return "add the key to assets/millenaire/lang/en_us.json (and fr_fr.json).";
         }

         String culture = e.culture() == null ? "<culture>" : e.culture();
         String path = "cultures/" + culture + "/resourcepack/assets/millenaire/lang/en_us.json";
         return translated ? "add the key to " + path + " in your pack." : "ship " + path + " in your pack with the name keys of this culture.";
      }

      private record Need(ContentIndex.Entry subject, String key) {
      }
   }

   static final class X10 implements ValidationRule {
      @Override
      public String id() {
         return "X-10";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (p.json() != null) {
               JsonObject ref = Json.object(p.json(), "parent_building");
               boolean isSub = Json.bool(p.json(), "is_sub_building", false);
               if (ref != null) {
                  String id = Json.string(ref, "id");
                  String variant = Json.string(ref, "variant");
                  Integer level = Json.integer(ref, "level");
                  if (id != null && !id.isEmpty() && variant != null && !variant.isEmpty() && level != null) {
                     String culture = CrossReferenceRules.declaredCulture(p);
                     ContentIndex.Entry parent = index.planSet(culture, id);
                     if (parent != null && parent.json() != null) {
                        boolean levelFound = false;

                        for (JsonObject v : Json.objects(parent.json(), "variants")) {
                           if (variant.equalsIgnoreCase(Json.string(v, "variant"))) {
                              for (JsonObject l : Json.objects(v, "levels")) {
                                 if (level.equals(Json.integer(l, "level"))) {
                                    levelFound = true;
                                 }
                              }
                           }
                        }

                        if (!levelFound) {
                           out.add(
                              Finding.of(
                                 "X-10",
                                 Severity.ERROR,
                                 "plan_set",
                                 p.identity(),
                                 p.origin().label(),
                                 "parent_building " + id + " has no variant '" + variant + "' level " + level + ".",
                                 "point at an existing (variant, level) of the parent."
                              )
                           );
                        }
                     } else {
                        out.add(
                           Finding.of(
                              "X-10",
                              Severity.ERROR,
                              "plan_set",
                              p.identity(),
                              p.origin().label(),
                              "parent_building '" + id + "' is not a plan set of culture '" + culture + "'.",
                              "fix the parent id."
                           )
                        );
                     }
                  } else {
                     out.add(
                        Finding.of(
                           "X-10",
                           Severity.ERROR,
                           "plan_set",
                           p.identity(),
                           p.origin().label(),
                           "parent_building is malformed (needs non-empty id, variant and an integer level) — the loader replaces it by null.",
                           "complete the triplet."
                        )
                     );
                  }
               } else if (isSub && !p.json().has("parent_building")) {
                  out.add(
                     Finding.of(
                        "X-10",
                        Severity.WARN,
                        "plan_set",
                        p.identity(),
                        p.origin().label(),
                        "sub-building has no parent_building reference — Import Table export/import cannot place it.",
                        "add {\"id\", \"variant\", \"level\"} of the parent plan set."
                     )
                  );
               } else if (p.json().has("parent_building") && !p.json().get("parent_building").isJsonNull()) {
                  out.add(
                     Finding.of(
                        "X-10",
                        Severity.ERROR,
                        "plan_set",
                        p.identity(),
                        p.origin().label(),
                        "parent_building is not an object — the loader replaces it by null.",
                        "use {\"id\", \"variant\", \"level\"}."
                     )
                  );
               }
            }
         }
      }
   }

   static final class X11 implements ValidationRule {
      private static final Set<String> RUNTIME_TAGS = Set.of(
         "chief",
         "despawnallmobs",
         "foreignmerchant",
         "grove",
         "helpInAttacks",
         "hof",
         "hostile",
         "inn",
         "isRaider",
         "leisure",
         "localmerchant",
         "marvel",
         "noble",
         "noleafclearing",
         "noteleport",
         "seller",
         "townhalls",
         "visitor"
      );

      @Override
      public String id() {
         return "X-11";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         Set<String> vocabulary = new HashSet<>(RUNTIME_TAGS);

         for (String c : index.cultures()) {
            vocabulary.addAll(CrossReferenceRules.buildingTags(index, c));
         }

         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (p.json() != null) {
               for (JsonObject v : Json.objects(p.json(), "variants")) {
                  for (JsonObject l : Json.objects(v, "levels")) {
                     vocabulary.addAll(Json.strings(l, "village_tags"));
                     vocabulary.addAll(Json.strings(l, "parent_tags"));
                  }
               }
            }
         }

         for (ContentIndex.Entry vt : index.entries(ContentIndex.Family.VILLAGE_TYPE)) {
            if (vt.json() != null) {
               String t = Json.string(vt.json(), "key_lone_building_generate_tag");
               if (t != null) {
                  vocabulary.add(t);
               }
            }
         }

         List<String> fields = List.of("required_tags", "required_village_tags", "required_parent_tags", "clear_tags", "forbidden_tags_in_village");

         for (ContentIndex.Entry p : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (p.json() != null) {
               for (JsonObject v : Json.objects(p.json(), "variants")) {
                  for (JsonObject l : Json.objects(v, "levels")) {
                     for (String field : fields) {
                        for (String tag : Json.strings(l, field)) {
                           if (!vocabulary.contains(tag)) {
                              out.add(
                                 Finding.of(
                                    "X-11",
                                    Severity.WARN,
                                    "plan_set",
                                    p.identity(),
                                    p.origin().label(),
                                    field
                                       + " '"
                                       + tag
                                       + "' is declared nowhere (no plan tags/village_tags/parent_tags, no runtime tag) — the condition can never be met.",
                                    "fix the tag name or declare it on the plan that should carry it."
                                 )
                              );
                           }
                        }
                     }
                  }
               }
            }
         }

         for (ContentIndex.Entry w : index.entries(ContentIndex.Family.WALL_TYPE)) {
            if (w.json() != null) {
               for (String field : List.of(
                  "gateway_plan_set",
                  "wall_plan_set",
                  "tower_plan_set",
                  "corner_plan_set",
                  "cap_right_plan_set",
                  "cap_left_plan_set",
                  "cap_both_plan_set",
                  "slope1_left_plan_set",
                  "slope1_right_plan_set",
                  "slope2_left_plan_set",
                  "slope2_right_plan_set",
                  "slope3_left_plan_set",
                  "slope3_right_plan_set"
               )) {
                  String ref = Json.string(w.json(), field);
                  if (ref != null) {
                     String p = CrossReferenceRules.path(ref);
                     int slash = p.indexOf(47);
                     ContentIndex.Entry target = slash > 0 ? index.planSet(p.substring(0, slash), p.substring(slash + 1)) : null;
                     if (target == null) {
                        out.add(
                           Finding.of(
                              "X-11",
                              Severity.ERROR,
                              "wall_type",
                              w.identity(),
                              w.origin().label(),
                              field + " '" + ref + "' is not a plan set.",
                              "fix the plan set id."
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
