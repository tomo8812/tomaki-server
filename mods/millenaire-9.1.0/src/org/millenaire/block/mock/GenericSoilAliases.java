package org.millenaire.block.mock;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public final class GenericSoilAliases {
   private static final Map<String, String> FIXED = Map.of("millenaire:rice", "rice", "minecraft:sugar_cane", "sugarcane", "minecraft:cocoa_beans", "cacao");
   private static volatile Map<Block, String> cropBlockToSubtype = Map.of();
   private static volatile Set<String> exactSubtypes = Set.of();
   private static final Map<String, Optional<String>> CACHE = new ConcurrentHashMap<>();

   private GenericSoilAliases() {
   }

   public static void rebuild(Map<Block, String> cropBlockToSubtypeTable, Set<String> exactSoilSubtypes) {
      cropBlockToSubtype = Map.copyOf(cropBlockToSubtypeTable);
      exactSubtypes = Set.copyOf(exactSoilSubtypes);
      CACHE.clear();
   }

   public static <K> Map<K, String> unambiguous(Map<K, ? extends Set<String>> candidates, BiConsumer<K, Set<String>> onAmbiguous) {
      Map<K, String> result = new LinkedHashMap<>();

      for (Entry<K, ? extends Set<String>> entry : candidates.entrySet()) {
         if (entry.getValue().size() == 1) {
            result.put(entry.getKey(), entry.getValue().iterator().next());
         } else {
            onAmbiguous.accept(entry.getKey(), (Set<String>)entry.getValue());
         }
      }

      return result;
   }

   @Nullable
   public static String resolve(@Nullable String payload) {
      return payload != null && !payload.isEmpty() ? CACHE.computeIfAbsent(payload, p -> Optional.ofNullable(compute(p))).orElse(null) : null;
   }

   @Nullable
   private static String compute(String payload) {
      if (exactSubtypes.contains(payload)) {
         return null;
      }

      String fixed = FIXED.get(payload);
      if (fixed != null) {
         return fixed;
      }

      ResourceLocation id = ResourceLocation.tryParse(payload);
      if (id == null) {
         return null;
      }

      Item item = (Item)BuiltInRegistries.ITEM.getOptional(id).orElse(null);
      if (item == null) {
         return null;
      }

      Block crop = MockGenericSoilBlock.cropBlockOf(item);
      return crop != null ? cropBlockToSubtype.get(crop) : null;
   }

   public static boolean matches(String wantedSubtype, @Nullable String pointSubtype) {
      return wantedSubtype.equals(pointSubtype) ? true : pointSubtype != null && pointSubtype.indexOf(58) >= 0 && wantedSubtype.equals(resolve(pointSubtype));
   }
}
