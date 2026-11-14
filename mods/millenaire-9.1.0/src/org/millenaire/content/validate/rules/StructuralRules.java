package org.millenaire.content.validate.rules;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import org.millenaire.content.Resource;
import org.millenaire.content.validate.ContentIndex;
import org.millenaire.content.validate.FieldContract;
import org.millenaire.content.validate.Finding;
import org.millenaire.content.validate.Severity;
import org.millenaire.content.validate.ValidationContext;
import org.millenaire.content.validate.ValidationRule;

public final class StructuralRules {
   private StructuralRules() {
   }

   public static List<ValidationRule> all() {
      return List.of(
         new StructuralRules.S01(),
         new StructuralRules.S02(),
         new StructuralRules.S03(),
         new StructuralRules.S04S05(),
         new StructuralRules.S06(),
         new StructuralRules.S07(),
         new StructuralRules.S08(),
         new StructuralRules.S09()
      );
   }

   static String identityOf(String relPath) {
      String id = relPath.startsWith("cultures/") ? relPath.substring("cultures/".length()) : relPath;
      if (id.endsWith(".json")) {
         id = id.substring(0, id.length() - 5);
      }

      return id;
   }

   private static String originLabel(Resource res) {
      return res.relPath() + " (" + res.source().displayName() + ")";
   }

   private static String notAGzipStream(Resource res) {
      try (
         InputStream in = res.open();
         GZIPInputStream gz = new GZIPInputStream(in);
      ) {
         gz.read();
         return null;
      } catch (IOException | RuntimeException e) {
         String message = e.getMessage();
         return message != null && !message.isEmpty() ? message : e.getClass().getSimpleName();
      }
   }

   private static String familyOf(String plural) {
      return switch (plural) {
         case "buildings" -> "plan_set";
         case "villagers" -> "villager_type";
         case "villages" -> "village_type";
         default -> plural;
      };
   }

   static String str(JsonObject obj, String key) {
      JsonElement e = obj.get(key);
      return e != null && e.isJsonPrimitive() ? e.getAsString() : null;
   }

   static Integer integer(JsonObject obj, String key) {
      JsonElement e = obj.get(key);
      return e != null && e.isJsonPrimitive() && e.getAsJsonPrimitive().isNumber() ? e.getAsInt() : null;
   }

   static List<JsonObject> objects(JsonObject obj, String key) {
      ArrayList<JsonObject> out = new ArrayList<>();
      JsonElement e = obj.get(key);
      if (e != null && e.isJsonArray()) {
         for (JsonElement el : e.getAsJsonArray()) {
            if (el.isJsonObject()) {
               out.add(el.getAsJsonObject());
            }
         }

         return out;
      } else {
         return out;
      }
   }

   static final class S01 implements ValidationRule {
      @Override
      public String id() {
         return "S-01";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (ContentIndex.ParseFailure f : index.parseFailures()) {
            out.add(
               Finding.of(
                  "S-01",
                  Severity.ERROR,
                  "json",
                  StructuralRules.identityOf(f.origin().relPath()),
                  f.origin().label(),
                  f.message(),
                  "make the file strict JSON encoded as UTF-8 without a byte-order mark."
               )
            );
         }
      }
   }

   static final class S02 implements ValidationRule {
      @Override
      public String id() {
         return "S-02";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (ContentIndex.Entry e : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (e.json() != null) {
               String buildingId = StructuralRules.str(e.json(), "building_id");
               if (buildingId != null && !buildingId.equalsIgnoreCase(e.stem())) {
                  out.add(
                     Finding.of(
                        "S-02",
                        Severity.ERROR,
                        "plan_set",
                        e.identity(),
                        e.origin().label(),
                        "building_id '" + buildingId + "' does not match the filename stem '" + e.stem() + "' — the loader drops the plan set.",
                        "rename the file to '" + buildingId + ".json' (and its NBTs) or change building_id."
                     )
                  );
               }
            }
         }

         for (ContentIndex.Entry e : index.entries(ContentIndex.Family.WALL_TYPE)) {
            if (e.json() != null) {
               String key = StructuralRules.str(e.json(), "key");
               if (key != null && !key.equalsIgnoreCase(e.stem())) {
                  out.add(
                     Finding.of(
                        "S-02",
                        Severity.ERROR,
                        "wall_type",
                        e.identity(),
                        e.origin().label(),
                        "key '" + key + "' does not match the filename stem '" + e.stem() + "'.",
                        "rename the file or change 'key' so the two agree."
                     )
                  );
               }
            }
         }
      }
   }

   static final class S03 implements ValidationRule {
      @Override
      public String id() {
         return "S-03";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (String culture : index.cultures()) {
            String base = "cultures/" + culture.toLowerCase(Locale.ROOT) + "/";

            for (Resource res : index.physicalFiles()) {
               String rel = res.relPath();
               if (rel.startsWith(base) && rel.endsWith(".json")) {
                  String inside = rel.substring(base.length());
                  int slash = inside.indexOf(47);
                  if (slash >= 0) {
                     String plural = inside.substring(0, slash);
                     String tail = inside.substring(slash + 1);
                     String leaf = ContentIndex.leafName(tail);
                     if (!leaf.startsWith("_")) {
                        int depth = tail.split("/").length - 1;

                        int expected = switch (plural) {
                           case "buildings", "villagers" -> 1;
                           case "villages" -> 0;
                           default -> -1;
                        };
                        if (expected >= 0 && depth != expected) {
                           out.add(
                              Finding.of(
                                 "S-03",
                                 Severity.ERROR,
                                 StructuralRules.familyOf(plural),
                                 StructuralRules.identityOf(rel),
                                 res.relPath() + " (" + res.source().displayName() + ")",
                                 "sits "
                                    + depth
                                    + " folder(s) below '"
                                    + plural
                                    + "/' but the loader only reads "
                                    + (expected == 0 ? "files directly under it" : "'" + plural + "/<category>/<id>.json'")
                                    + " — the file is never loaded.",
                                 expected == 0
                                    ? "move the file directly under 'cultures/" + culture + "/" + plural + "/'."
                                    : "move the file to 'cultures/" + culture + "/" + plural + "/<category>/'."
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

   static final class S04S05 implements ValidationRule {
      @Override
      public String id() {
         return "S-04";
      }

      @Override
      public List<String> ids() {
         return List.of("S-04", "S-05");
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (Entry<String, String> err : ctx.contractErrors().entrySet()) {
            out.add(
               Finding.of(
                  "CONTRACT",
                  Severity.ERROR,
                  err.getKey(),
                  "contract/" + err.getKey(),
                  "/content/validate/contract/" + err.getKey() + ".json",
                  err.getValue(),
                  "fix the contract file."
               )
            );
         }

         checkFamily(index, ctx, ContentIndex.Family.PLAN_SET, index.entries(ContentIndex.Family.PLAN_SET), out);
         checkFamily(index, ctx, ContentIndex.Family.VILLAGER_TYPE, index.entries(ContentIndex.Family.VILLAGER_TYPE), out);
         checkFamily(index, ctx, ContentIndex.Family.VILLAGE_TYPE, index.entries(ContentIndex.Family.VILLAGE_TYPE), out);
         checkFamily(index, ctx, ContentIndex.Family.SHOP, index.entries(ContentIndex.Family.SHOP), out);
         checkFamily(index, ctx, ContentIndex.Family.WALL_TYPE, index.entries(ContentIndex.Family.WALL_TYPE), out);
         FieldContract tg = ctx.contract(ContentIndex.Family.TRADED_GOODS.label());

         for (String culture : index.cultures()) {
            for (ContentIndex.Entry file : index.tradedGoodsFiles(culture)) {
               checkEntry(index, tg, "traded_goods", file, out);
            }
         }
      }

      private static void checkFamily(
         ContentIndex index, ValidationContext ctx, ContentIndex.Family family, List<ContentIndex.Entry> entries, List<Finding> out
      ) {
         FieldContract contract = ctx.contract(family.label());

         for (ContentIndex.Entry e : entries) {
            checkEntry(index, contract, family.label(), e, out);
         }
      }

      private static void checkEntry(ContentIndex index, FieldContract contract, String family, ContentIndex.Entry e, List<Finding> out) {
         if (e.json() == null) {
            JsonElement parsedEl = index.parsedJson(e.origin());
            if (parsedEl != null) {
               out.add(
                  Finding.of(
                     "S-04",
                     Severity.ERROR,
                     family,
                     e.identity(),
                     e.origin().label(),
                     "root of the document is not a JSON object.",
                     "wrap the content in an object."
                  )
               );
            }
         } else if (contract != null) {
            for (FieldContract.Problem p : contract.check(e.json())) {
               boolean unknown = "S-05".equals(p.ruleId());
               out.add(
                  Finding.of(
                     p.ruleId(),
                     Severity.ERROR,
                     family,
                     e.identity(),
                     e.origin().label(),
                     p.message(),
                     unknown ? "remove the field or rename it to the key the loader reads." : "add the field with a value of the declared type."
                  )
               );
            }
         }
      }
   }

   static final class S06 implements ValidationRule {
      @Override
      public String id() {
         return "S-06";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         Set<String> claimed = new HashSet<>();

         for (ContentIndex.Entry e : index.entries(ContentIndex.Family.PLAN_SET)) {
            if (e.json() != null) {
               String buildingId = StructuralRules.str(e.json(), "building_id");
               if (buildingId != null) {
                  String dir = e.dir();

                  for (JsonObject variant : StructuralRules.objects(e.json(), "variants")) {
                     String letter = StructuralRules.str(variant, "variant");
                     if (letter != null) {
                        for (JsonObject level : StructuralRules.objects(variant, "levels")) {
                           Integer lvl = StructuralRules.integer(level, "level");
                           if (lvl != null) {
                              String nbt = (dir.isEmpty() ? "" : dir + "/") + (buildingId + "_" + letter + "_" + lvl + ".nbt").toLowerCase(Locale.ROOT);
                              claimed.add(nbt);
                              Optional<Resource> res = index.find(nbt);
                              if (res.isEmpty()) {
                                 out.add(
                                    Finding.of(
                                       "S-06",
                                       Severity.ERROR,
                                       "plan_set",
                                       e.identity(),
                                       e.origin().label(),
                                       "NBT missing for variant '" + letter + "' level " + lvl + " (expected at " + nbt + ") — the whole plan set is dropped.",
                                       "ship the template or remove the level from the JSON."
                                    )
                                 );
                              } else {
                                 String unreadable = StructuralRules.notAGzipStream(res.get());
                                 if (unreadable != null) {
                                    out.add(
                                       Finding.of(
                                          "S-06",
                                          Severity.ERROR,
                                          "plan_set",
                                          e.identity(),
                                          StructuralRules.originLabel(res.get()),
                                          "NBT for variant '"
                                             + letter
                                             + "' level "
                                             + lvl
                                             + " is not a readable gzip stream ("
                                             + unreadable
                                             + ") — TemplateLoader fails on it and the whole plan set is dropped.",
                                          "re-export the template: the file is truncated or was not written compressed."
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

         for (Resource res : index.physicalFiles()) {
            String rel = res.relPath();
            if (rel.endsWith(".nbt") && rel.startsWith("cultures/") && rel.contains("/buildings/") && !claimed.contains(rel)) {
               out.add(
                  Finding.of(
                     "S-06",
                     Severity.WARN,
                     "plan_set",
                     StructuralRules.identityOf(rel),
                     rel + " (" + res.source().displayName() + ")",
                     "NBT is not claimed by any plan set variant/level (orphan).",
                     "delete the file or declare the level in its plan set."
                  )
               );
            }
         }
      }
   }

   static final class S07 implements ValidationRule {
      @Override
      public String id() {
         return "S-07";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         if (index.languagesManifestPresent()) {
            for (String locale : index.languagesManifest()) {
               if (!index.languageLocalesOnDisk().contains(locale.toLowerCase(Locale.ROOT))) {
                  out.add(
                     Finding.of(
                        "S-07",
                        Severity.ERROR,
                        "languages",
                        "languages/_manifest.json",
                        "languages/_manifest.json",
                        "locale '" + locale + "' is listed but has no 'languages/" + locale + "/' folder.",
                        "add the folder or drop the locale from the manifest."
                     )
                  );
               }
            }

            for (String locale : index.languageLocalesOnDisk()) {
               if (!"native".equals(locale) && !locale.startsWith("_") && !index.languagesManifest().contains(locale)) {
                  out.add(
                     Finding.of(
                        "S-07",
                        Severity.INFO,
                        "languages",
                        "languages/" + locale,
                        "languages/" + locale,
                        "locale folder is not listed in the manifest; it is only discovered through the directory walk.",
                        "list it in languages/_manifest.json if it is a supported locale."
                     )
                  );
               }
            }
         }

         if (index.questManifestPresent()) {
            Set<String> manifest = new HashSet<>(index.questManifest());

            for (String entry : index.questManifest()) {
               String rel = entry.startsWith("cultures/") ? questRelForCulture(entry) : "quests/" + entry + ".json";
               if (index.find(rel).isEmpty()) {
                  out.add(
                     Finding.of(
                        "S-07",
                        Severity.ERROR,
                        "quest",
                        "quests/" + entry,
                        "quests/_manifest.json",
                        "manifest entry has no file at " + rel + ".",
                        "add the quest file or drop the entry."
                     )
                  );
               }
            }

            for (ContentIndex.Entry q : index.entries(ContentIndex.Family.QUEST)) {
               if (q.culture() == null && !manifest.contains(q.id())) {
                  out.add(
                     Finding.of(
                        "S-07",
                        Severity.WARN,
                        "quest",
                        q.identity(),
                        q.origin().label(),
                        "quest file is not listed in quests/_manifest.json (loaded only through the directory walk).",
                        "add '" + q.id() + "' to the manifest."
                     )
                  );
               }
            }
         }
      }

      private static String questRelForCulture(String entry) {
         String tail = entry.substring("cultures/".length());
         int slash = tail.indexOf(47);
         return slash <= 0 ? "quests/" + entry + ".json" : "cultures/" + tail.substring(0, slash) + "/quests/" + tail.substring(slash + 1) + ".json";
      }
   }

   static final class S08 implements ValidationRule {
      @Override
      public String id() {
         return "S-08";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (ContentIndex.DuplicateId d : index.duplicates()) {
            String identity = d.culture() == null ? d.family().label() + "/" + d.id() : d.culture() + "/" + d.family().label() + ":" + d.id();
            if (d.blocking()) {
               String scope = d.loser().source().equals(d.winner().source())
                  ? "is declared twice in the same source"
                  : "is declared by two sources at different paths";

               String consequence = switch (d.family()) {
                  case TRADED_GOODS -> "the second occurrence replaces the first.";
                  case GATHERING_TYPE, VISIT_GOAL -> "the second occurrence is ignored.";
                  case QUEST -> "only one of the two files is ever registered, and which one depends on the load pass.";
                  default -> "the second occurrence is fatal for the culture.";
               };
               out.add(
                  new Finding(
                     "S-08",
                     Severity.ERROR,
                     d.family().label(),
                     identity,
                     List.of(d.loser().label(), d.winner().label()),
                     "identity '" + d.id() + "' " + scope + "; " + consequence,
                     "rename or remove one of the two files/entries.",
                     false
                  )
               );
            } else {
               out.add(
                  new Finding(
                     "S-08",
                     Severity.INFO,
                     d.family().label(),
                     identity,
                     List.of(d.loser().label(), d.winner().label()),
                     "'" + d.id() + "' is overridden across sources (" + d.winner().source() + " wins).",
                     "",
                     false
                  )
               );
            }
         }
      }
   }

   static final class S09 implements ValidationRule {
      @Override
      public String id() {
         return "S-09";
      }

      @Override
      public void check(ContentIndex index, ValidationContext ctx, List<Finding> out) {
         for (Entry<String, List<Resource>> e : index.orphanFragments().entrySet()) {
            String baseId = e.getKey().substring(e.getKey().lastIndexOf(47) + 1);

            for (Resource fragment : e.getValue()) {
               out.add(
                  Finding.of(
                     "S-09",
                     Severity.ERROR,
                     "plan_set",
                     StructuralRules.identityOf(fragment.relPath()),
                     fragment.relPath() + " (" + fragment.source().displayName() + ")",
                     "variant fragment targets building '" + baseId + "', which is not defined in the same folder — the fragment is never applied.",
                     "move the fragment next to its base plan set or fix the base id in its name."
                  )
               );
            }
         }

         for (ContentIndex.RejectedFragment r : index.rejectedFragments()) {
            out.add(
               Finding.of(
                  "S-09",
                  Severity.ERROR,
                  "plan_set",
                  StructuralRules.identityOf(r.fragment().relPath()),
                  r.fragment().label(),
                  "variant fragment " + r.reason() + " — ignored by the loader.",
                  "fix the fragment header (culture, building_id, \"fragment\": true, variants)."
               )
            );
         }

         Set<String> honoured = Set.of("culture", "building_id", "variants", "fragment");

         for (ContentIndex.Entry plan : index.entries(ContentIndex.Family.PLAN_SET)) {
            for (ContentIndex.Origin frag : plan.fragments()) {
               JsonObject fj = index.objectAt(frag.relPath());
               if (fj != null) {
                  for (String key : fj.keySet()) {
                     if (!honoured.contains(key)) {
                        out.add(
                           Finding.of(
                              "S-09",
                              Severity.WARN,
                              "plan_set",
                              StructuralRules.identityOf(frag.relPath()),
                              frag.label(),
                              "fragment declares plan-set field '" + key + "', which fragments cannot override — ignored.",
                              "remove the field from the fragment."
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
