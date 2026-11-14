package org.millenaire.content.validate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.bind.TypeAdapters;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import org.millenaire.content.BuiltInCultures;
import org.millenaire.content.ContentFs;
import org.millenaire.content.CustomContentIndex;
import org.millenaire.content.Resource;
import org.millenaire.content.SourceKind;
import org.millenaire.culture.CultureContentScanner;

public final class ContentIndex {
   private static final String RESOURCEPACK_EN_US = "resourcepack/assets/millenaire/lang/en_us.json";
   private final ContentFs root;
   private final List<String> cultures;
   private final List<Resource> physical = new ArrayList<>();
   private final Map<String, JsonElement> parsed = new HashMap<>();
   private final Map<ContentIndex.LayerKey, JsonElement> parsedLayers = new HashMap<>();
   private final List<Resource> shadowedLayers = new ArrayList<>();
   private final List<ContentIndex.ParseFailure> parseFailures = new ArrayList<>();
   private final Map<ContentIndex.Family, List<ContentIndex.Entry>> entries = new LinkedHashMap<>();
   private final Map<String, Map<String, ContentIndex.Entry>> tradedGoodsByCulture = new LinkedHashMap<>();
   private final Map<String, List<ContentIndex.Entry>> tradedGoodsFilesByCulture = new LinkedHashMap<>();
   private final List<ContentIndex.DuplicateId> duplicates = new ArrayList<>();
   private final List<ContentIndex.RejectedFragment> rejectedFragments = new ArrayList<>();
   private final Map<String, List<Resource>> orphanFragments = new LinkedHashMap<>();
   private final List<Resource> shadowed = new ArrayList<>();
   private final List<Resource> wrongDepth = new ArrayList<>();
   private final Map<String, Set<String>> disabledIds = new LinkedHashMap<>();
   private final Map<String, Set<String>> namelistsByCulture = new LinkedHashMap<>();
   private final Set<String> globalDisabledQuests = new TreeSet<>();
   private final List<String> questManifest = new ArrayList<>();
   private boolean questManifestPresent;
   private final List<String> languagesManifest = new ArrayList<>();
   private boolean languagesManifestPresent;
   private final Set<String> languageLocalesOnDisk = new TreeSet<>();
   private final Map<String, Set<String>> cultureLangKeys = new LinkedHashMap<>();
   private final List<ContentIndex.ToolCategoryLayer> toolCategoryLayers = new ArrayList<>();

   private ContentIndex(ContentFs root, List<String> cultures) {
      this.root = root;
      this.cultures = List.copyOf(cultures);

      for (ContentIndex.Family f : ContentIndex.Family.values()) {
         this.entries.put(f, new ArrayList<>());
      }
   }

   public static ContentIndex build(ContentFs root, List<String> cultures) {
      ContentIndex index = new ContentIndex(root, cultures);
      index.walkPhysical();
      index.globalDisabledQuests.addAll(index.readDisabledList(root.sub("quests"), "_disabled.json"));

      for (String culture : cultures) {
         index.indexCulture(culture);
      }

      index.indexGlobal();
      index.arbitrateQuestKeys();
      return index;
   }

   public static ContentIndex build(CustomContentIndex snapshot) {
      List<String> cultures = new ArrayList<>(BuiltInCultures.IDS);
      cultures.addAll(new TreeSet<>(snapshot.customCultureIds()));
      return build(snapshot.root(), cultures);
   }

   private void walkPhysical() {
      this.root.walk("", Integer.MAX_VALUE).forEach(this.physical::add);
      this.physical.sort(Comparator.comparing(Resource::relPath));

      for (Resource res : this.physical) {
         if (res.relPath().endsWith(".json")) {
            this.parseJson(res, true);
         }
      }

      for (Resource winner : List.copyOf(this.physical)) {
         if (parsesEveryLayer(winner.relPath())) {
            List<Resource> layers = this.root.findAll(winner.relPath());

            for (int i = 1; i < layers.size(); i++) {
               this.shadowedLayers.add(layers.get(i));
               this.parseJson(layers.get(i), false);
            }
         }
      }
   }

   private static boolean parsesEveryLayer(String relPath) {
      String leaf = leafName(relPath);
      if ("_disabled.json".equals(leaf)) {
         return true;
      } else if ("quests/_manifest.json".equals(relPath) || isToolCategoriesFile(relPath)) {
         return true;
      } else if (relPath.startsWith("gathering_type/") || relPath.startsWith("visit_goal/")) {
         return leaf.endsWith(".json");
      } else if (relPath.startsWith("cultures/") && relPath.endsWith("/resourcepack/assets/millenaire/lang/en_us.json")) {
         return relPath.split("/").length == 2 + "resourcepack/assets/millenaire/lang/en_us.json".split("/").length;
      } else {
         return !"culture.json".equals(leaf) && !"traded_goods.json".equals(leaf) ? false : relPath.startsWith("cultures/") && relPath.split("/").length == 3;
      }
   }

   private void parseJson(Resource res, boolean overlayWinner) {
      byte[] bytes;
      try (InputStream in = res.open()) {
         bytes = in.readAllBytes();
      } catch (IOException e) {
         this.parseFailures.add(new ContentIndex.ParseFailure(ContentIndex.Origin.of(res), "unreadable: " + e.getMessage()));
         return;
      }

      if (bytes.length >= 3 && (bytes[0] & 255) == 239 && (bytes[1] & 255) == 187 && (bytes[2] & 255) == 191) {
         this.parseFailures.add(new ContentIndex.ParseFailure(ContentIndex.Origin.of(res), "file starts with a UTF-8 BOM, which the loaders do not strip"));
      } else {
         try {
            StandardCharsets.UTF_8
               .newDecoder()
               .onMalformedInput(CodingErrorAction.REPORT)
               .onUnmappableCharacter(CodingErrorAction.REPORT)
               .decode(ByteBuffer.wrap(bytes));
         } catch (CharacterCodingException e) {
            this.parseFailures.add(new ContentIndex.ParseFailure(ContentIndex.Origin.of(res), "not valid UTF-8"));
            return;
         }

         try {
            JsonReader reader = new JsonReader(new InputStreamReader(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8));

            label76: {
               try {
                  reader.setLenient(false);
                  JsonElement element = (JsonElement)TypeAdapters.JSON_ELEMENT.read(reader);
                  if (reader.peek() == JsonToken.END_DOCUMENT) {
                     this.parsedLayers.put(keyOf(res), element);
                     if (overlayWinner) {
                        this.parsed.put(res.relPath(), element);
                     }
                     break label76;
                  }

                  this.parseFailures.add(new ContentIndex.ParseFailure(ContentIndex.Origin.of(res), "trailing content after the JSON document"));
               } catch (Throwable var12) {
                  try {
                     reader.close();
                  } catch (Throwable var8) {
                     var12.addSuppressed(var8);
                  }

                  throw var12;
               }

               reader.close();
               return;
            }

            reader.close();
         } catch (Exception e) {
            this.parseFailures.add(new ContentIndex.ParseFailure(ContentIndex.Origin.of(res), "does not parse: " + firstLine(e.getMessage())));
         }
      }
   }

   private static ContentIndex.LayerKey keyOf(Resource res) {
      return new ContentIndex.LayerKey(res.source().displayName(), res.relPath());
   }

   private JsonElement elementOf(Resource res) {
      return this.parsedLayers.get(keyOf(res));
   }

   private JsonObject objectOf(Resource res) {
      JsonElement el = this.elementOf(res);
      return el != null && el.isJsonObject() ? el.getAsJsonObject() : null;
   }

   private static String firstLine(String s) {
      if (s == null) {
         return "";
      }

      int nl = s.indexOf(10);
      return nl < 0 ? s : s.substring(0, nl);
   }

   private void indexCulture(String culture) {
      ContentFs cultureFs = this.root.sub("cultures/" + culture);
      String prefix = "cultures/" + culture + "/";
      List<Resource> cultureJsons = cultureFs.findAll("culture.json");
      if (!cultureJsons.isEmpty()) {
         JsonObject merged = null;

         for (int i = cultureJsons.size() - 1; i >= 0; i--) {
            JsonObject layer = this.objectOf(cultureJsons.get(i));
            if (layer != null) {
               if (merged == null) {
                  merged = layer.deepCopy();
               } else {
                  for (Map.Entry<String, JsonElement> e : layer.entrySet()) {
                     merged.add(e.getKey(), e.getValue());
                  }
               }
            }
         }

         this.entries
            .get(ContentIndex.Family.CULTURE)
            .add(
               new ContentIndex.Entry(
                  ContentIndex.Family.CULTURE, culture, culture, culture + "/culture", merged, ContentIndex.Origin.of(cultureJsons.get(0)), null, List.of()
               )
            );
      }

      this.singleFile(ContentIndex.Family.REPUTATION, culture, cultureFs, "reputation.json", culture + "/reputation");
      this.singleFile(ContentIndex.Family.CULTURE_REPUTATION, culture, cultureFs, "culture_reputation.json", culture + "/culture_reputation");
      this.scanFamily(ContentIndex.Family.PLAN_SET, "building_plan", "buildings", culture, cultureFs);
      this.scanFamily(ContentIndex.Family.VILLAGER_TYPE, "villager_type", "villagers", culture, cultureFs);
      this.scanFamily(ContentIndex.Family.VILLAGE_TYPE, "village_type", "villages", culture, cultureFs);

      for (Resource res : cultureFs.walk("shops", 0).sorted(Comparator.comparing(Resource::relPath)).toList()) {
         String leaf = leafName(res.relPath());
         if (leaf.endsWith(".json") && !leaf.startsWith("_")) {
            String id = stemOf(res.relPath());
            this.entries
               .get(ContentIndex.Family.SHOP)
               .add(
                  new ContentIndex.Entry(
                     ContentIndex.Family.SHOP,
                     culture,
                     id,
                     culture + "/shops/" + id,
                     this.objectAt(res.relPath()),
                     ContentIndex.Origin.of(res),
                     null,
                     List.of()
                  )
               );
         }
      }

      List<Resource> tgFiles = cultureFs.findAll("traded_goods.json");
      Map<String, ContentIndex.Entry> goods = new LinkedHashMap<>();
      List<ContentIndex.Entry> files = new ArrayList<>();

      for (int i = tgFiles.size() - 1; i >= 0; i--) {
         Resource res = tgFiles.get(i);
         ContentIndex.Origin origin = ContentIndex.Origin.of(res);
         JsonObject rootObj = this.objectOf(res);
         files.add(
            new ContentIndex.Entry(ContentIndex.Family.TRADED_GOODS, culture, "traded_goods", culture + "/traded_goods", rootObj, origin, null, List.of())
         );
         if (rootObj != null) {
            for (JsonObject good : Json.objects(rootObj, "goods")) {
               String id = Json.string(good, "id");
               if (id != null) {
                  ContentIndex.Entry entry = new ContentIndex.Entry(
                     ContentIndex.Family.TRADED_GOODS, culture, id, culture + "/traded_goods:" + id, good, origin, null, List.of()
                  );
                  if (Json.bool(good, "disabled", false)) {
                     goods.remove(id);
                  } else if (goods.containsKey(id)) {
                     ContentIndex.Entry previous = goods.put(id, entry);
                     boolean sameSource = previous.origin().source().equals(origin.source());
                     if (sameSource || !good.equals(previous.json())) {
                        this.duplicates.add(new ContentIndex.DuplicateId(ContentIndex.Family.TRADED_GOODS, culture, id, previous.origin(), origin, sameSource));
                     }
                  } else {
                     goods.put(id, entry);
                  }
               }
            }
         }
      }

      this.tradedGoodsByCulture.put(culture, goods);
      this.tradedGoodsFilesByCulture.put(culture, files);
      Set<String> names = new TreeSet<>();
      cultureFs.walk("namelists", 0).forEach(resx -> {
         String leafx = leafName(resx.relPath());
         if (leafx.endsWith(".txt") && !leafx.startsWith("_")) {
            names.add(leafx.substring(0, leafx.length() - 4));
         }
      });
      this.namelistsByCulture.put(culture, names);
      Set<String> disabledGoals = this.readDisabledList(cultureFs, "goal/_disabled.json", SourceKind.SUBMOD);
      this.disabledIds.put(culture + "/goal", disabledGoals);

      for (Resource res : cultureFs.walk("goal", 0).sorted(Comparator.comparing(Resource::relPath)).toList()) {
         String leaf = leafName(res.relPath());
         if (leaf.endsWith(".json") && !leaf.startsWith("_")) {
            String id = stemOf(res.relPath());
            if (res.kind() == SourceKind.CLASSPATH || !disabledGoals.contains(id)) {
               this.entries
                  .get(ContentIndex.Family.CULTURE_GOAL)
                  .add(
                     new ContentIndex.Entry(
                        ContentIndex.Family.CULTURE_GOAL,
                        culture,
                        culture + "/" + id,
                        culture + "/goal/" + id,
                        this.objectAt(res.relPath()),
                        ContentIndex.Origin.of(res),
                        null,
                        List.of()
                     )
                  );
            }
         }
      }

      Set<String> disabledQuests = this.readDisabledList(cultureFs, "quests/_disabled.json");
      this.disabledIds.put(culture + "/quests", disabledQuests);

      for (Resource res : cultureFs.walk("quests", Integer.MAX_VALUE).sorted(Comparator.comparing(Resource::relPath)).toList()) {
         String leaf = leafName(res.relPath());
         if (leaf.endsWith(".json") && !leaf.startsWith("_")) {
            String rel = res.relPath().substring((prefix + "quests/").length());
            String id = "cultures/" + culture + "/" + rel.substring(0, rel.length() - 5);
            if (!disabledQuests.contains(rel.substring(0, rel.length() - 5))
               && !this.globalDisabledQuests.contains(id)
               && !isWorldQuestPath(rel.substring(0, rel.length() - 5))) {
               this.entries
                  .get(ContentIndex.Family.QUEST)
                  .add(
                     new ContentIndex.Entry(
                        ContentIndex.Family.QUEST, culture, id, "quests/" + id, this.objectAt(res.relPath()), ContentIndex.Origin.of(res), null, List.of()
                     )
                  );
            }
         }
      }

      this.indexCultureLangKeys(culture, cultureFs);
   }

   private void indexCultureLangKeys(String culture, ContentFs cultureFs) {
      Set<String> keys = new TreeSet<>();

      for (Resource res : cultureFs.findAll("resourcepack/assets/millenaire/lang/en_us.json")) {
         JsonObject obj = this.objectOf(res);
         if (obj != null) {
            keys.addAll(obj.keySet());
         }
      }

      if (!keys.isEmpty()) {
         this.cultureLangKeys.put(culture, keys);
      }
   }

   private void singleFile(ContentIndex.Family family, String culture, ContentFs cultureFs, String name, String identity) {
      Optional<Resource> res = cultureFs.findFirst(name);
      if (!res.isEmpty()) {
         this.entries
            .get(family)
            .add(
               new ContentIndex.Entry(
                  family, culture, culture, identity, this.objectAt(res.get().relPath()), ContentIndex.Origin.of(res.get()), null, List.of()
               )
            );
      }
   }

   private void scanFamily(ContentIndex.Family family, String contentType, String plural, String culture, ContentFs cultureFs) {
      Set<String> disabled = CultureContentScanner.readDisabledIds(cultureFs, plural);
      this.disabledIds.put(culture + "/" + plural, disabled);
      CultureContentScanner.ScanResult scan = CultureContentScanner.discover(contentType, culture, cultureFs, disabled, false);
      if (scan != null) {
         this.shadowed.addAll(scan.shadowed());
         this.wrongDepth.addAll(scan.wrongDepth());
         this.orphanFragments.putAll(scan.orphanFragments());
         Map<String, CultureContentScanner.ScannedEntry> seen = new HashMap<>();

         for (CultureContentScanner.ScannedEntry e : scan.entries()) {
            String key = e.residualId().toLowerCase(Locale.ROOT);
            CultureContentScanner.ScannedEntry first = seen.putIfAbsent(key, e);
            if (first != null) {
               this.duplicates
                  .add(
                     new ContentIndex.DuplicateId(
                        family, culture, e.residualId(), ContentIndex.Origin.of(e.resource()), ContentIndex.Origin.of(first.resource()), true
                     )
                  );
            }
         }

         for (Resource loser : scan.shadowed()) {
            String id = stemOf(loser.relPath());
            CultureContentScanner.ScannedEntry winner = seen.get(id.toLowerCase(Locale.ROOT));
            if (winner != null) {
               this.duplicates
                  .add(new ContentIndex.DuplicateId(family, culture, id, ContentIndex.Origin.of(loser), ContentIndex.Origin.of(winner.resource()), false));
            }
         }

         for (CultureContentScanner.ScannedEntry e : scan.entries()) {
            JsonObject json = this.objectAt(e.relPath());
            List<ContentIndex.Origin> fragmentOrigins = new ArrayList<>();
            if (family == ContentIndex.Family.PLAN_SET && json != null && !e.variantFragments().isEmpty()) {
               json = json.deepCopy();
               this.mergeFragments(json, e, fragmentOrigins);
            }

            String identity = e.relPath().startsWith("cultures/") ? e.relPath().substring("cultures/".length(), e.relPath().length() - 5) : e.relPath();
            this.entries
               .get(family)
               .add(
                  new ContentIndex.Entry(
                     family, culture, e.residualId(), identity, json, ContentIndex.Origin.of(e.resource()), e.pathCategory(), fragmentOrigins
                  )
               );
         }
      }
   }

   private void mergeFragments(JsonObject json, CultureContentScanner.ScannedEntry base, List<ContentIndex.Origin> applied) {
      String buildingId = Json.string(json, "building_id", base.residualId());
      String culture = Json.string(json, "culture");
      List<Resource> ordered = new ArrayList<>(base.variantFragments());
      ordered.sort(Comparator.<Resource>comparingInt(r -> r.kind().ordinal()).thenComparing(Resource::relPath));
      Map<String, JsonObject> byLetter = new LinkedHashMap<>();

      for (JsonObject v : Json.objects(json, "variants")) {
         String letter = Json.string(v, "variant");
         if (letter != null) {
            byLetter.put(letter, v);
         }
      }

      boolean any = false;

      for (Resource fragment : ordered) {
         ContentIndex.Origin origin = ContentIndex.Origin.of(fragment);
         JsonObject fj = this.objectAt(fragment.relPath());
         if (fj == null) {
            this.rejectedFragments.add(new ContentIndex.RejectedFragment(origin, buildingId, "does not parse"));
         } else if (!Json.bool(fj, "fragment", false)) {
            this.rejectedFragments.add(new ContentIndex.RejectedFragment(origin, buildingId, "does not declare \"fragment\": true"));
         } else {
            String declaredId = Json.string(fj, "building_id");
            if (declaredId != null && declaredId.equalsIgnoreCase(buildingId)) {
               String declaredCulture = Json.string(fj, "culture");
               if (declaredCulture != null && declaredCulture.equals(namespaced(culture))) {
                  if (Json.array(fj, "variants") == null) {
                     this.rejectedFragments.add(new ContentIndex.RejectedFragment(origin, buildingId, "has no 'variants' array"));
                  } else {
                     Iterator var15 = Json.objects(fj, "variants").iterator();

                     while (true) {
                        if (var15.hasNext()) {
                           JsonObject v = (JsonObject)var15.next();
                           String letter = Json.string(v, "variant");
                           if (letter != null) {
                              byLetter.put(letter, v);
                              any = true;
                              continue;
                           }

                           this.rejectedFragments.add(new ContentIndex.RejectedFragment(origin, buildingId, "has a variant entry with no 'variant' letter"));
                        }

                        applied.add(origin);
                        break;
                     }
                  }
               } else {
                  this.rejectedFragments
                     .add(
                        new ContentIndex.RejectedFragment(
                           origin, buildingId, "declares culture '" + declaredCulture + "' but extends a '" + culture + "' plan set"
                        )
                     );
               }
            } else {
               this.rejectedFragments
                  .add(new ContentIndex.RejectedFragment(origin, buildingId, "declares building_id '" + declaredId + "' but sits next to '" + buildingId + "'"));
            }
         }
      }

      if (any) {
         JsonArray merged = new JsonArray();

         for (JsonObject v : byLetter.values()) {
            merged.add(v);
         }

         json.add("variants", merged);
      }
   }

   private static boolean isWorldQuestPath(String questPath) {
      return questPath.startsWith("worldquest-") || questPath.contains("/worldquest-");
   }

   private void arbitrateQuestKeys() {
      Map<String, ContentIndex.Entry> byKey = new LinkedHashMap<>();

      for (ContentIndex.Entry quest : this.entries.get(ContentIndex.Family.QUEST)) {
         if (quest.json() != null) {
            String key = Json.string(quest.json(), "key");
            if (key != null && !key.isEmpty()) {
               ContentIndex.Entry first = byKey.putIfAbsent(key, quest);
               if (first != null) {
                  this.duplicates.add(new ContentIndex.DuplicateId(ContentIndex.Family.QUEST, null, key, quest.origin(), first.origin(), true));
               }
            }
         }
      }
   }

   private void indexGlobal() {
      ContentFs wallFs = this.root.sub("wall_type");

      for (Resource res : wallFs.walk("", 1).sorted(Comparator.comparing(Resource::relPath)).toList()) {
         String full = res.relPath();
         String leaf = leafName(full);
         if (leaf.endsWith(".json") && !leaf.startsWith("_") && full.startsWith("wall_type/")) {
            String rel = full.substring("wall_type/".length());
            if (rel.indexOf(47) >= 0) {
               String id = rel.substring(0, rel.length() - 5);
               this.entries
                  .get(ContentIndex.Family.WALL_TYPE)
                  .add(
                     new ContentIndex.Entry(
                        ContentIndex.Family.WALL_TYPE,
                        id.substring(0, id.indexOf(47)),
                        id,
                        "wall_type/" + id,
                        this.objectAt(full),
                        ContentIndex.Origin.of(res),
                        null,
                        List.of()
                     )
                  );
            }
         }
      }

      ContentFs gtFs = this.root.sub("gathering_type");
      Set<String> disabledGt = this.readDisabledList(gtFs, "_disabled.json", false);
      this.disabledIds.put("gathering_type", disabledGt);
      this.indexFlatIdFamily(ContentIndex.Family.GATHERING_TYPE, gtFs, "gathering_type/", this.readDisabledList(gtFs, "_disabled.json", true), disabledGt);
      ContentFs vgFs = this.root.sub("visit_goal");
      Set<String> disabledVg = this.readDisabledList(vgFs, "_disabled.json", false);
      this.disabledIds.put("visit_goal", disabledVg);
      this.indexFlatIdFamily(ContentIndex.Family.VISIT_GOAL, vgFs, "visit_goal/", Set.of(), disabledVg);
      Resource manifest = this.classpathLayerOf("quests/_manifest.json");
      if (manifest == null) {
         manifest = this.root.findFirst("quests/_manifest.json").orElse(null);
      }

      if (manifest != null) {
         this.questManifestPresent = true;
         JsonElement el = this.elementOf(manifest);
         if (el != null && el.isJsonArray()) {
            for (JsonElement e : el.getAsJsonArray()) {
               if (e.isJsonPrimitive()) {
                  this.questManifest.add(e.getAsString());
               }
            }
         }
      }

      ContentFs questsFs = this.root.sub("quests");
      Set<String> disabledQuests = this.globalDisabledQuests;
      this.disabledIds.put("quests", disabledQuests);

      for (Resource res : questsFs.walk("", Integer.MAX_VALUE).sorted(Comparator.comparing(Resource::relPath)).toList()) {
         String full = res.relPath();
         String leaf = leafName(full);
         if (leaf.endsWith(".json") && !leaf.startsWith("_") && !full.startsWith("quests/lang/")) {
            String rel = full.substring("quests/".length());
            String id = rel.substring(0, rel.length() - 5);
            if (!disabledQuests.contains(id) && !isWorldQuestPath(id)) {
               this.entries
                  .get(ContentIndex.Family.QUEST)
                  .add(
                     new ContentIndex.Entry(
                        ContentIndex.Family.QUEST, null, id, "quests/" + id, this.objectAt(full), ContentIndex.Origin.of(res), null, List.of()
                     )
                  );
            }
         }
      }

      Optional<Resource> langManifest = this.root.findFirst("languages/_manifest.json");
      if (langManifest.isPresent()) {
         this.languagesManifestPresent = true;
         JsonObject obj = this.objectAt(langManifest.get().relPath());
         if (obj != null) {
            this.languagesManifest.addAll(Json.strings(obj, "languages"));
         }
      }

      this.root.sub("languages").walk("", Integer.MAX_VALUE).forEach(resx -> {
         String relx = resx.relPath();
         if (relx.startsWith("languages/")) {
            String tail = relx.substring("languages/".length());
            int slash = tail.indexOf(47);
            if (slash > 0) {
               String leaf = tail.substring(slash + 1);
               if (leaf.indexOf(47) < 0 && leaf.endsWith(".txt")) {
                  this.languageLocalesOnDisk.add(tail.substring(0, slash));
               }
            }
         }
      });
      TreeSet<String> toolFiles = new TreeSet<>();
      this.root.sub("tool_categories").walk("", Integer.MAX_VALUE).forEach(resx -> {
         String relx = resx.relPath();
         if (isToolCategoriesFile(relx)) {
            toolFiles.add(relx);
         }
      });

      for (String rel : toolFiles) {
         for (Resource res : this.root.findAll(rel)) {
            JsonObject obj = this.objectOf(res);
            if (obj != null) {
               this.toolCategoryLayers.add(new ContentIndex.ToolCategoryLayer(ContentIndex.Origin.of(res), obj));
            }
         }
      }
   }

   private static boolean isToolCategoriesFile(String relPath) {
      if (!relPath.startsWith("tool_categories/")) {
         return false;
      }

      String leaf = relPath.substring("tool_categories/".length());
      return leaf.endsWith(".json") && !leaf.startsWith("_");
   }

   private Resource classpathLayerOf(String relPath) {
      for (Resource res : this.root.findAll(relPath)) {
         if (res.kind() == SourceKind.CLASSPATH) {
            return res;
         }
      }

      return null;
   }

   private void indexFlatIdFamily(ContentIndex.Family family, ContentFs fs, String prefix, Set<String> classpathDisabled, Set<String> externalDisabled) {
      List<Resource> jarPhase = new ArrayList<>();
      List<Resource> externalPhase = new ArrayList<>();
      Set<String> seenPaths = new LinkedHashSet<>();

      for (Resource res : fs.walk("", Integer.MAX_VALUE).toList()) {
         String leaf = leafName(res.relPath());
         if (leaf.endsWith(".json") && !leaf.startsWith("_")) {
            if (res.kind() != SourceKind.CLASSPATH) {
               externalPhase.add(res);
            }

            if (seenPaths.add(res.relPath())) {
               for (Resource layer : this.root.findAll(res.relPath())) {
                  if (layer.kind() == SourceKind.CLASSPATH) {
                     jarPhase.add(layer);
                  }
               }
            }
         }
      }

      jarPhase.sort(Comparator.comparing(Resource::relPath));
      externalPhase.sort(Comparator.comparing(Resource::relPath));
      Set<String> submodIds = new LinkedHashSet<>();

      for (Resource res : externalPhase) {
         if (res.kind() == SourceKind.SUBMOD) {
            submodIds.add(stemOf(res.relPath()));
         }
      }

      externalPhase.removeIf(resx -> resx.kind() == SourceKind.STANDARD && submodIds.contains(stemOf(resx.relPath())));
      Map<String, ContentIndex.Entry> registered = new LinkedHashMap<>();

      for (Map.Entry<List<Resource>, Set<String>> phase : List.of(Map.entry(jarPhase, classpathDisabled), Map.entry(externalPhase, externalDisabled))) {
         Map<String, ContentIndex.Entry> phaseWinners = new LinkedHashMap<>();

         for (Resource res : phase.getKey()) {
            String id = stemOf(res.relPath());
            if (!phase.getValue().contains(id)) {
               ContentIndex.Entry entry = new ContentIndex.Entry(
                  family, null, id, prefix + id, this.objectOf(res), ContentIndex.Origin.of(res), null, List.of()
               );
               ContentIndex.Entry first = phaseWinners.putIfAbsent(id, entry);
               if (first != null) {
                  this.duplicates.add(new ContentIndex.DuplicateId(family, null, id, entry.origin(), first.origin(), true));
               } else {
                  ContentIndex.Entry previous = registered.put(id, entry);
                  if (previous != null) {
                     this.duplicates.add(new ContentIndex.DuplicateId(family, null, id, previous.origin(), entry.origin(), false));
                  }
               }
            }
         }
      }

      this.entries.get(family).addAll(registered.values());
   }

   private Set<String> readDisabledList(ContentFs fs, String relPath) {
      return this.readDisabledList(fs, relPath, false);
   }

   private Set<String> readDisabledList(ContentFs fs, String relPath, SourceKind only) {
      Set<String> out = new LinkedHashSet<>();

      for (Resource res : fs.findAll(relPath)) {
         if (res.kind() == only) {
            JsonElement el = this.elementOf(res);
            if (el != null && el.isJsonArray()) {
               for (JsonElement e : el.getAsJsonArray()) {
                  if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
                     out.add(e.getAsString());
                  }
               }
            }
         }
      }

      return out;
   }

   private Set<String> readDisabledList(ContentFs fs, String relPath, boolean classpathOnly) {
      List<Resource> manifests = fs.findAll(relPath);
      if (manifests.isEmpty()) {
         return Set.of();
      }

      Set<String> out = new LinkedHashSet<>();

      for (Resource res : manifests) {
         if (!classpathOnly || res.kind() == SourceKind.CLASSPATH) {
            JsonElement el = this.elementOf(res);
            if (el != null && el.isJsonArray()) {
               for (JsonElement e : el.getAsJsonArray()) {
                  if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
                     out.add(e.getAsString());
                  }
               }
            }
         }
      }

      return out;
   }

   public ContentFs root() {
      return this.root;
   }

   public List<String> cultures() {
      return this.cultures;
   }

   public List<Resource> physicalFiles() {
      return Collections.unmodifiableList(this.physical);
   }

   public List<ContentIndex.ParseFailure> parseFailures() {
      return Collections.unmodifiableList(this.parseFailures);
   }

   public JsonElement parsedJson(String relPath) {
      return this.parsed.get(relPath);
   }

   public JsonElement parsedJson(ContentIndex.Origin origin) {
      return this.parsedLayers.get(new ContentIndex.LayerKey(origin.source(), origin.relPath()));
   }

   public List<Resource> shadowedLayers() {
      return Collections.unmodifiableList(this.shadowedLayers);
   }

   public JsonObject objectAt(String relPath) {
      JsonElement el = this.parsed.get(relPath);
      return el != null && el.isJsonObject() ? el.getAsJsonObject() : null;
   }

   public Optional<Resource> find(String relPath) {
      return this.root.findFirst(relPath);
   }

   public List<ContentIndex.Entry> entries(ContentIndex.Family family) {
      return Collections.unmodifiableList(this.entries.get(family));
   }

   public List<ContentIndex.Entry> entries(ContentIndex.Family family, String culture) {
      List<ContentIndex.Entry> out = new ArrayList<>();

      for (ContentIndex.Entry e : this.entries.get(family)) {
         if (culture.equals(e.culture())) {
            out.add(e);
         }
      }

      return out;
   }

   public ContentIndex.Entry planSet(String culture, String buildingId) {
      return this.byId(ContentIndex.Family.PLAN_SET, culture, buildingId);
   }

   public ContentIndex.Entry villagerType(String culture, String id) {
      return this.byId(ContentIndex.Family.VILLAGER_TYPE, culture, id);
   }

   public ContentIndex.Entry villageType(String culture, String id) {
      return this.byId(ContentIndex.Family.VILLAGE_TYPE, culture, id);
   }

   public ContentIndex.Entry shop(String culture, String id) {
      return this.byId(ContentIndex.Family.SHOP, culture, id);
   }

   private ContentIndex.Entry byId(ContentIndex.Family family, String culture, String id) {
      if (culture != null && id != null) {
         for (ContentIndex.Entry e : this.entries.get(family)) {
            if (culture.equals(e.culture()) && e.id().equalsIgnoreCase(id)) {
               return e;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public Map<String, ContentIndex.Entry> tradedGoods(String culture) {
      return this.tradedGoodsByCulture.getOrDefault(culture, Map.of());
   }

   public List<ContentIndex.Entry> tradedGoodsFiles(String culture) {
      return this.tradedGoodsFilesByCulture.getOrDefault(culture, List.of());
   }

   public ContentIndex.Entry culture(String culture) {
      return this.byId(ContentIndex.Family.CULTURE, culture, culture);
   }

   public Set<String> namelists(String culture) {
      return this.namelistsByCulture.getOrDefault(culture, Set.of());
   }

   public Set<String> disabledIds(String scope) {
      return this.disabledIds.getOrDefault(scope, Set.of());
   }

   public List<ContentIndex.DuplicateId> duplicates() {
      return Collections.unmodifiableList(this.duplicates);
   }

   public List<ContentIndex.RejectedFragment> rejectedFragments() {
      return Collections.unmodifiableList(this.rejectedFragments);
   }

   public Map<String, List<Resource>> orphanFragments() {
      return Collections.unmodifiableMap(this.orphanFragments);
   }

   public List<Resource> shadowed() {
      return Collections.unmodifiableList(this.shadowed);
   }

   public List<Resource> wrongDepth() {
      return Collections.unmodifiableList(this.wrongDepth);
   }

   public boolean questManifestPresent() {
      return this.questManifestPresent;
   }

   public List<String> questManifest() {
      return Collections.unmodifiableList(this.questManifest);
   }

   public boolean languagesManifestPresent() {
      return this.languagesManifestPresent;
   }

   public List<String> languagesManifest() {
      return Collections.unmodifiableList(this.languagesManifest);
   }

   public Set<String> languageLocalesOnDisk() {
      return Collections.unmodifiableSet(this.languageLocalesOnDisk);
   }

   public Set<String> cultureLangKeys(String culture) {
      Set<String> keys = culture == null ? null : this.cultureLangKeys.get(culture);
      return keys == null ? Set.of() : Collections.unmodifiableSet(keys);
   }

   public Set<String> allCultureLangKeys() {
      Set<String> all = new TreeSet<>();

      for (Set<String> keys : this.cultureLangKeys.values()) {
         all.addAll(keys);
      }

      return all;
   }

   public List<ContentIndex.ToolCategoryLayer> toolCategoryLayers() {
      return Collections.unmodifiableList(this.toolCategoryLayers);
   }

   static String namespaced(String ref) {
      if (ref == null) {
         return null;
      } else {
         return ref.indexOf(58) < 0 ? "minecraft:" + ref : ref;
      }
   }

   public static String culturePath(String cultureRef) {
      if (cultureRef == null) {
         return null;
      }

      int colon = cultureRef.indexOf(58);
      return colon < 0 ? cultureRef : cultureRef.substring(colon + 1);
   }

   public static String leafName(String relPath) {
      int slash = relPath.lastIndexOf(47);
      return slash < 0 ? relPath : relPath.substring(slash + 1);
   }

   public static String stemOf(String relPath) {
      String leaf = leafName(relPath);
      int dot = leaf.lastIndexOf(46);
      return dot < 0 ? leaf : leaf.substring(0, dot);
   }

   public record DuplicateId(ContentIndex.Family family, String culture, String id, ContentIndex.Origin loser, ContentIndex.Origin winner, boolean blocking) {
   }

   public record Entry(
      ContentIndex.Family family,
      String culture,
      String id,
      String identity,
      JsonObject json,
      ContentIndex.Origin origin,
      String pathCategory,
      List<ContentIndex.Origin> fragments
   ) {
      public String relPath() {
         return this.origin.relPath();
      }

      public String stem() {
         return ContentIndex.stemOf(this.origin.relPath());
      }

      public String dir() {
         String p = this.origin.relPath();
         int slash = p.lastIndexOf(47);
         return slash < 0 ? "" : p.substring(0, slash);
      }
   }

   public enum Family {
      CULTURE("culture"),
      PLAN_SET("plan_set"),
      VILLAGER_TYPE("villager_type"),
      VILLAGE_TYPE("village_type"),
      SHOP("shop"),
      TRADED_GOODS("traded_goods"),
      WALL_TYPE("wall_type"),
      GATHERING_TYPE("gathering_type"),
      VISIT_GOAL("visit_goal"),
      CULTURE_GOAL("culture_goal"),
      QUEST("quest"),
      REPUTATION("reputation"),
      CULTURE_REPUTATION("culture_reputation");

      private final String label;

      Family(String label) {
         this.label = label;
      }

      public String label() {
         return this.label;
      }

      // $VF: synthetic method
      private static ContentIndex.Family[] $values() {
         return new ContentIndex.Family[]{
            CULTURE,
            PLAN_SET,
            VILLAGER_TYPE,
            VILLAGE_TYPE,
            SHOP,
            TRADED_GOODS,
            WALL_TYPE,
            GATHERING_TYPE,
            VISIT_GOAL,
            CULTURE_GOAL,
            QUEST,
            REPUTATION,
            CULTURE_REPUTATION
         };
      }
   }

   private record LayerKey(String source, String relPath) {
   }

   public record Origin(String relPath, String source, SourceKind kind) {
      public static ContentIndex.Origin of(Resource res) {
         return new ContentIndex.Origin(res.relPath(), res.source().displayName(), res.kind());
      }

      public String label() {
         return this.relPath + " (" + this.source + ")";
      }
   }

   public record ParseFailure(ContentIndex.Origin origin, String message) {
   }

   public record RejectedFragment(ContentIndex.Origin fragment, String baseId, String reason) {
   }

   public record ToolCategoryLayer(ContentIndex.Origin origin, JsonObject json) {
   }
}
