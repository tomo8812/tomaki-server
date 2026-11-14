package org.millenaire.content.validate.semantic;

import com.google.gson.JsonObject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import org.millenaire.building.MockBlockExtractor;
import org.millenaire.building.NbtPaletteHelper;
import org.millenaire.building.SpecialPoint;
import org.millenaire.building.SpecialPointsLoader;
import org.millenaire.building.TemplateLoader;
import org.millenaire.content.ContentFs;
import org.millenaire.content.Resource;
import org.millenaire.content.validate.ContentIndex;
import org.millenaire.content.validate.Json;

final class TemplateCache {
   private final ContentIndex index;
   private final Map<String, List<TemplateCache.Level>> levelsByPlanSet = new HashMap<>();

   TemplateCache(ContentIndex index) {
      this.index = index;
   }

   List<TemplateCache.Level> levels(ContentIndex.Entry planSet) {
      return this.levelsByPlanSet.computeIfAbsent(planSet.identity(), k -> this.read(planSet));
   }

   private List<TemplateCache.Level> read(ContentIndex.Entry planSet) {
      List<TemplateCache.Level> out = new ArrayList<>();
      JsonObject json = planSet.json();
      if (json == null) {
         return out;
      }

      String buildingId = Json.string(json, "building_id");
      if (buildingId == null) {
         return out;
      }

      String culture = ContentIndex.culturePath(Json.string(json, "culture", planSet.culture()));
      ContentFs cultureFs = this.index.root().sub("cultures/" + culture);
      String dir = planSet.dir();
      String culturePrefix = "cultures/" + culture.toLowerCase(Locale.ROOT) + "/";
      String dirForCulture = dir.startsWith(culturePrefix) ? dir.substring(culturePrefix.length()) : dir;

      for (JsonObject variant : Json.objects(json, "variants")) {
         String letter = Json.string(variant, "variant");
         if (letter != null) {
            for (JsonObject levelObj : Json.objects(variant, "levels")) {
               Integer level = Json.integer(levelObj, "level");
               if (level != null) {
                  String stem = (buildingId + "_" + letter + "_" + level).toLowerCase(Locale.ROOT);
                  String nbtPath = dirForCulture.isEmpty() ? stem : dirForCulture + "/" + stem;
                  String rel = (dir.isEmpty() ? "" : dir + "/") + stem + ".nbt";
                  Optional<Resource> res = this.index.find(rel);
                  if (res.isEmpty()) {
                     out.add(new TemplateCache.Level(planSet, letter, level, rel, null, List.of(), "NBT missing"));
                  } else {
                     CompoundTag nbt;
                     try (InputStream in = res.get().open()) {
                        nbt = NbtIo.readCompressed(in, TemplateLoader.accounterFor(res.get().kind()));
                     } catch (Exception e) {
                        out.add(new TemplateCache.Level(planSet, letter, level, rel, null, List.of(), "NBT unreadable: " + e.getMessage()));
                        continue;
                     }

                     ResourceLocation key = ResourceLocation.fromNamespaceAndPath("millenaire", culture + "/" + stem);
                     MockBlockExtractor.Extraction extraction = SpecialPointsLoader.load(key, nbt, rel);
                     List<SpecialPoint> points = extraction.points();
                     if (!extraction.hasMarkers()) {
                        points = SpecialPointsLoader.load(key, nbtPath, cultureFs);
                     }

                     out.add(new TemplateCache.Level(planSet, letter, level, rel, palette(nbt), points, null));
                  }
               }
            }
         }
      }

      return out;
   }

   private static List<TemplateCache.PaletteEntry> palette(CompoundTag nbt) {
      ListTag paletteTag = NbtPaletteHelper.resolvePaletteTag(nbt);
      if (paletteTag == null) {
         return List.of();
      }

      List<TemplateCache.PaletteEntry> out = new ArrayList<>(paletteTag.size());

      for (int i = 0; i < paletteTag.size(); i++) {
         CompoundTag entry = paletteTag.getCompound(i);
         Map<String, String> props = new LinkedHashMap<>();
         if (entry.contains("Properties", 10)) {
            CompoundTag pt = entry.getCompound("Properties");

            for (String k : pt.getAllKeys()) {
               props.put(k, pt.getString(k));
            }
         }

         out.add(new TemplateCache.PaletteEntry(entry.getString("Name"), props));
      }

      return out;
   }

   record Level(
      ContentIndex.Entry planSet,
      String variant,
      int level,
      String nbtRelPath,
      List<TemplateCache.PaletteEntry> palette,
      List<SpecialPoint> points,
      String readError
   ) {
      boolean readable() {
         return this.palette != null;
      }
   }

   record PaletteEntry(String block, Map<String, String> properties) {
   }
}
