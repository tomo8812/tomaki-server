package dev.emi.emi.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import dev.emi.emi.EmiPort;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiRegistryAdapter;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.ListEmiIngredient;
import dev.emi.emi.api.stack.TagEmiIngredient;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.data.TagExclusions;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.runtime.EmiHidden;
import dev.emi.emi.runtime.EmiReloadLog;
import dev.emi.emi.runtime.EmiTagKey;
import dev.emi.emi.util.InheritanceMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class EmiTags {
   public static final InheritanceMap<EmiRegistryAdapter<?>> ADAPTERS_BY_CLASS = new InheritanceMap<>(Maps.newHashMap());
   public static final Map<Registry<?>, EmiRegistryAdapter<?>> ADAPTERS_BY_REGISTRY = Maps.newHashMap();
   public static final ResourceLocation HIDDEN_FROM_RECIPE_VIEWERS = EmiPort.id("c", "hidden_from_recipe_viewers");
   public static final Map<TagKey<?>, ResourceLocation> MODELED_TAGS = Maps.newHashMap();
   private static final Map<Set<?>, List<EmiTagKey<?>>> CACHED_TAGS = Maps.newHashMap();
   private static final Map<EmiTagKey<?>, List<?>> TAG_VALUES = Maps.newHashMap();
   private static final Map<ResourceLocation, List<EmiTagKey<?>>> SORTED_TAGS = Maps.newHashMap();
   public static final List<EmiTagKey<?>> TAGS = Lists.newArrayList();
   public static TagExclusions exclusions = new TagExclusions();

   public static <T> List<EmiStack> getValues(EmiTagKey<T> key) {
      if (TAG_VALUES.containsKey(key)) {
         EmiRegistryAdapter adapter = ADAPTERS_BY_REGISTRY.get(key.registry());
         if (adapter != null) {
            List<T> values = (List<T>)TAG_VALUES.getOrDefault(key, List.of());
            return values.stream().map(t -> adapter.of((T)t, EmiPort.emptyExtraData(), 1L)).toList();
         }
      }

      return List.of();
   }

   public static <T> List<EmiStack> getRawValues(EmiTagKey<T> key) {
      if (key.isOf(EmiPort.getBlockRegistry())) {
         return key.stream().map(e -> EmiStack.of((Block)e)).toList();
      }

      EmiRegistryAdapter adapter = ADAPTERS_BY_REGISTRY.get(key.registry());
      return adapter != null ? key.stream().map(t -> adapter.of((T)t, EmiPort.emptyExtraData(), 1L)).toList() : List.of();
   }

   public static <T> EmiIngredient getIngredient(Class<T> clazz, List<EmiStack> stacks, long amount) {
      Map<T, EmiStack> map = Maps.newHashMap();

      for (EmiStack stack : stacks) {
         if (!stack.isEmpty()) {
            EmiStack existing = map.getOrDefault(stack.getKey(), null);
            if (existing != null && !stack.equals(existing)) {
               return new ListEmiIngredient(stacks, amount);
            }

            map.put((T)stack.getKey(), stack);
         }
      }

      if (map.size() == 0) {
         return EmiStack.EMPTY;
      }

      if (map.size() == 1) {
         return map.values().stream().toList().get(0).copy().setAmount(amount);
      }

      EmiRegistryAdapter<T> adapter = (EmiRegistryAdapter<T>)ADAPTERS_BY_CLASS.get(clazz);
      if (adapter == null) {
         return new ListEmiIngredient(stacks, amount);
      }

      Registry<T> registry = adapter.getRegistry();
      List<EmiTagKey<T>> keys = (List<EmiTagKey<T>>)CACHED_TAGS.get(map.keySet());
      if (keys != null) {
         for (EmiTagKey<T> key : keys) {
            List<T> values = key.getList();
            values.forEach(map::remove);
         }
      } else {
         keys = Lists.newArrayList();
         Set<T> original = new HashSet<>(map.keySet());

         for (EmiTagKey<T> key : getTags(registry)) {
            List<T> values = key.getList();
            if (values.size() >= 2) {
               if (map.keySet().containsAll(values)) {
                  values.forEach(map::remove);
                  keys.add(key);
               }

               if (map.isEmpty()) {
                  break;
               }
            }
         }

         CACHED_TAGS.put(original, keys);
      }

      if (keys == null || keys.isEmpty()) {
         return new ListEmiIngredient(stacks.stream().toList(), amount);
      } else if (map.isEmpty()) {
         return keys.size() == 1
            ? tagIngredient(keys.get(0), amount)
            : new ListEmiIngredient(keys.stream().map(k -> tagIngredient((EmiTagKey<?>)k, 1L)).toList(), amount);
      } else {
         return new ListEmiIngredient(
            List.of(map.values().stream().map(i -> i.copy().setAmount(1L)).toList(), keys.stream().map(k -> tagIngredient((EmiTagKey<?>)k, 1L)).toList())
               .stream()
               .flatMap(a -> a.stream())
               .toList(),
            amount
         );
      }
   }

   private static EmiIngredient tagIngredient(EmiTagKey<?> key, long amount) {
      List<?> list = TAG_VALUES.get(key);
      if (list != null && !list.isEmpty()) {
         return list.size() == 1 ? new TagEmiIngredient(key, amount).getEmiStacks().get(0).copy().setAmount(amount) : new TagEmiIngredient(key, amount);
      } else {
         return EmiStack.EMPTY;
      }
   }

   public static <T> List<EmiTagKey<T>> getTags(Registry<T> registry) {
      return (List<EmiTagKey<T>>)SORTED_TAGS.getOrDefault(registry.key().location(), List.of());
   }

   public static void registerTagModels(ResourceManager manager, Consumer<ModelResourceLocation> consumer, String variant) {
      MODELED_TAGS.clear();

      for (ResourceLocation id : EmiPort.findResources(manager, "models/tag", s -> s.endsWith(".json"))) {
         String path = id.getPath();
         path = path.substring(11, path.length() - 5);
         String[] parts = path.split("/");
         if (parts.length > 1) {
            TagKey<?> key = TagKey.create(
               ResourceKey.createRegistryKey(EmiPort.id("minecraft", parts[0])), EmiPort.id(id.getNamespace(), path.substring(1 + parts[0].length()))
            );
            ResourceLocation mid = EmiPort.id(id.getNamespace(), "tag/" + path);
            MODELED_TAGS.put(key, mid);
            consumer.accept(new ModelResourceLocation(mid, variant));
         }
      }
   }

   public static void reload() {
      EmiTagKey.reload();
      TAGS.clear();
      SORTED_TAGS.clear();
      TAG_VALUES.clear();
      CACHED_TAGS.clear();

      for (Registry<?> registry : ADAPTERS_BY_REGISTRY.keySet()) {
         reloadTags(registry);
      }
   }

   private static <T> void reloadTags(Registry<T> registry) {
      Set<T> hidden = EmiTagKey.<T>of(registry, HIDDEN_FROM_RECIPE_VIEWERS).getSet();
      ResourceLocation rid = registry.key().location();
      List<EmiTagKey<T>> tags = EmiTagKey.<T>fromRegistry(registry)
         .filter(keyx -> !exclusions.contains(rid, keyx.id()) && !hidden.containsAll(keyx.getList()))
         .toList();
      logUntranslatedTags(tags);
      tags = consolodateTags(tags);

      for (EmiTagKey<T> key : tags) {
         List<T> values = key.stream().filter(s -> !EmiHidden.isDisabled(stackFromKey(key, (T)s))).toList();
         if (values.isEmpty()) {
            TAG_VALUES.put(key, key.getList());
         } else {
            TAG_VALUES.put(key, values);
         }
      }

      TAGS.addAll(tags.stream().sorted((a, b) -> a.id().toString().compareTo(b.id().toString())).toList());
      tags = tags.stream().sorted((a, b) -> Long.compare(b.stream().count(), a.stream().count())).toList();
      SORTED_TAGS.put(registry.key().location(), tags);
   }

   private static <T> EmiStack stackFromKey(EmiTagKey<T> key, T t) {
      EmiRegistryAdapter<T> adapter = (EmiRegistryAdapter<T>)ADAPTERS_BY_REGISTRY.get(key.registry());
      if (adapter != null) {
         return adapter.of(t, EmiPort.emptyExtraData(), 1L);
      } else {
         throw new UnsupportedOperationException("Unsupported tag registry " + key);
      }
   }

   private static <T> void logUntranslatedTags(List<EmiTagKey<T>> tags) {
      if (EmiConfig.logUntranslatedTags) {
         List<String> untranslated = Lists.newArrayList();

         for (EmiTagKey<T> tag : tags) {
            if (!tag.hasTranslation()) {
               untranslated.add(tag.id().toString());
            }
         }

         if (!untranslated.isEmpty()) {
            for (String tag : untranslated.stream().sorted().toList()) {
               EmiReloadLog.warn("Untranslated tag #" + tag);
            }

            EmiReloadLog.info(" Tag warning can be disabled in the config, EMI docs describe how to add a translation or exclude tags.");
         }
      }
   }

   private static <T> List<EmiTagKey<T>> consolodateTags(List<EmiTagKey<T>> tags) {
      Map<Set<T>, EmiTagKey<T>> map = Maps.newHashMap();

      for (int i = 0; i < tags.size(); i++) {
         EmiTagKey<T> key = tags.get(i);
         Set<T> values = key.getSet();
         EmiTagKey<T> original = map.get(values);
         if (original != null) {
            map.put(values, betterTag(key, original));
         } else {
            map.put(values, key);
         }
      }

      return map.values().stream().toList();
   }

   private static <T> EmiTagKey<T> betterTag(EmiTagKey<T> a, EmiTagKey<T> b) {
      if (a.hasTranslation() != b.hasTranslation()) {
         return a.hasTranslation() ? a : b;
      }

      if (a.hasCustomModel() != b.hasCustomModel()) {
         return a.hasCustomModel() ? a : b;
      }

      String an = a.id().getNamespace();
      String bn = b.id().getNamespace();
      if (!an.equals(bn)) {
         if (an.equals("minecraft")) {
            return a;
         }

         if (bn.equals("minecraft")) {
            return b;
         }

         if (an.equals("c")) {
            return a;
         }

         if (bn.equals("c")) {
            return b;
         }

         if (an.equals("fabric")) {
            return EmiAgnos.isModLoaded("forge") ? b : a;
         }

         if (bn.equals("fabric")) {
            return EmiAgnos.isModLoaded("forge") ? a : b;
         }

         if (an.equals("forge")) {
            return EmiAgnos.isModLoaded("forge") ? a : b;
         }

         if (bn.equals("forge")) {
            return EmiAgnos.isModLoaded("forge") ? b : a;
         }
      }

      return a.id().toString().length() <= b.id().toString().length() ? a : b;
   }
}
