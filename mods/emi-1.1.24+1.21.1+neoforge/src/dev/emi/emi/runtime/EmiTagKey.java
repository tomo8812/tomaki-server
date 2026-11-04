package dev.emi.emi.runtime;

import com.google.common.collect.Maps;
import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.registry.EmiTags;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

public class EmiTagKey<T> {
   public static final Map<TagKey<?>, EmiTagKey<?>> CACHE = Maps.newHashMap();
   private final TagKey<T> raw;
   private List<T> cached;

   private EmiTagKey(TagKey<T> raw) {
      this.raw = raw;
      this.recalculate();
   }

   public void recalculate() {
      this.cached = this.stream().toList();
   }

   public TagKey<T> raw() {
      return this.raw;
   }

   public boolean isOf(Registry<?> registry) {
      return this.raw.isFor(registry.key());
   }

   public ResourceLocation id() {
      return this.raw.location();
   }

   public Registry<T> registry() {
      Minecraft client = Minecraft.getInstance();
      return (Registry<T>)client.level.registryAccess().registry(this.raw.registry()).orElse(null);
   }

   public Stream<T> stream() {
      Registry<T> registry = this.registry();
      Optional<Named<T>> opt = registry.getTag(this.raw);
      if (opt.isEmpty()) {
         return Stream.of((T[])(new Object[0]));
      } else {
         return registry == EmiPort.getFluidRegistry() ? opt.get().stream().filter(o -> {
            Fluid f = (Fluid)o.value();
            return f.isSource(f.defaultFluidState());
         }).map(Holder::value) : opt.get().stream().map(Holder::value);
      }
   }

   public List<T> getList() {
      return this.cached;
   }

   public Set<T> getSet() {
      return this.stream().collect(Collectors.toSet());
   }

   public Component getTagName() {
      String s = this.getTagTranslationKey();
      return s == null ? EmiPort.literal("#" + this.id()) : EmiPort.translatable(s);
   }

   public boolean hasTranslation() {
      return this.getTagTranslationKey() != null;
   }

   @Nullable
   private String getTagTranslationKey() {
      ResourceLocation registry = this.raw.registry().location();
      if (registry.getNamespace().equals("minecraft")) {
         String s = translatePrefix("tag." + registry.getPath().replace("/", ".") + ".", this.id());
         if (s != null) {
            return s;
         }
      } else {
         String s = translatePrefix("tag." + registry.getNamespace() + "." + registry.getPath().replace("/", ".") + ".", this.id());
         if (s != null) {
            return s;
         }
      }

      return translatePrefix("tag.", this.id());
   }

   @Nullable
   private static String translatePrefix(String prefix, ResourceLocation id) {
      String s = EmiUtil.translateId(prefix, id);
      if (I18n.exists(s)) {
         return s;
      }

      if (id.getNamespace().equals("forge")) {
         s = EmiUtil.translateId(prefix, EmiPort.id("c", id.getPath()));
         if (I18n.exists(s)) {
            return s;
         }
      }

      return null;
   }

   @Nullable
   public ResourceLocation getCustomModel() {
      ResourceLocation rid = this.id();
      return rid.getNamespace().equals("forge") && !EmiTags.MODELED_TAGS.containsKey(this.raw())
         ? of(this.registry(), EmiPort.id("c", rid.getPath())).getCustomModel()
         : EmiTags.MODELED_TAGS.get(this.raw());
   }

   public boolean hasCustomModel() {
      return this.getCustomModel() != null;
   }

   @Override
   public int hashCode() {
      return this.raw().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof EmiTagKey other && this.raw().equals(other.raw());
   }

   public static <T> EmiTagKey<T> of(TagKey<T> raw) {
      return (EmiTagKey<T>)CACHE.computeIfAbsent(raw, k -> new EmiTagKey((TagKey<T>)k));
   }

   public static <T> EmiTagKey<T> of(Registry<T> registry, ResourceLocation id) {
      return of(TagKey.create(registry.key(), id));
   }

   public static <T> Stream<EmiTagKey<T>> fromRegistry(Registry<T> registry) {
      return registry.getTagNames().map(EmiTagKey::of);
   }

   public static void reload() {
      for (EmiTagKey<?> key : CACHE.values()) {
         key.recalculate();
      }
   }
}
