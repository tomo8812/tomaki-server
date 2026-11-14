package dev.diorama;

import dev.diorama.render.Radar;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public final class RadarCategories {
   public static final int SAME_SIZE = 0;
   public static final String POKEMON = "pokemon";
   private static final List<String> POKEMON_MOBS = List.of("cobblemon:pokemon", "pixelmon:pixelmon");
   private static final RadarCategories.Entry[] KINDS = new RadarCategories.Entry[Radar.Kind.values().length];
   private static final Radar.Kind[] LISTED = new Radar.Kind[]{Radar.Kind.PLAYER, Radar.Kind.HOSTILE, Radar.Kind.NEUTRAL, Radar.Kind.PASSIVE, Radar.Kind.ITEM};
   private static final Map<EntityType<?>, Object> BY_TYPE;
   private static @Nullable List<RadarCategories.Category> cachedFor;

   private RadarCategories() {
   }

   public static RadarCategories.Entry kind(Radar.Kind kind) {
      return KINDS[kind.ordinal()];
   }

   public static List<RadarCategories.Entry> all() {
      List<RadarCategories.Entry> all = new ArrayList<>(Settings.get().radarCategories);

      for (Radar.Kind kind : LISTED) {
         all.add(kind(kind));
      }

      return all;
   }

   public static RadarCategories.Entry of(Entity entity, Radar.Kind kind) {
      if (kind != Radar.Kind.PLAYER && kind != Radar.Kind.ITEM) {
         List<RadarCategories.Category> categories = Settings.get().radarCategories;
         if (categories.isEmpty()) {
            return kind(kind);
         }

         if (cachedFor != categories) {
            BY_TYPE.clear();
            cachedFor = categories;
         }

         Object found = BY_TYPE.get(entity.getType());
         if (found == null) {
            String id = EntityType.getKey(entity.getType()).toString();
            found = categories;

            for (RadarCategories.Category category : categories) {
               if (category.has(id)) {
                  found = category;
                  break;
               }
            }

            BY_TYPE.put(entity.getType(), found);
         }

         return found instanceof RadarCategories.Category category ? category : kind(kind);
      } else {
         return kind(kind);
      }
   }

   public static void changed() {
      BY_TYPE.clear();
   }

   public static boolean matches(String pattern, String id) {
      int star = pattern.indexOf(42);
      if (star < 0) {
         return pattern.equals(id);
      }

      if (!id.startsWith(pattern.substring(0, star))) {
         return false;
      }

      int at = star;
      int from = star;

      while (true) {
         int next = pattern.indexOf(42, at + 1);
         String piece = pattern.substring(at + 1, next < 0 ? pattern.length() : next);
         if (next < 0) {
            return id.length() - from >= piece.length() && id.endsWith(piece);
         }

         int found = id.indexOf(piece, from);
         if (found < 0) {
            return false;
         }

         from = found + piece.length();
         at = next;
      }
   }

   static @Nullable String cleanMob(@Nullable String mob) {
      String trimmed = mob == null ? "" : mob.trim().toLowerCase(Locale.ROOT);
      if (trimmed.isEmpty()) {
         return null;
      } else {
         return trimmed.indexOf(58) < 0 ? "minecraft:" + trimmed : trimmed;
      }
   }

   static void loaded(Settings settings) {
      List<RadarCategories.Category> categories = new ArrayList<>();
      if (settings.radarCategories != null) {
         for (RadarCategories.Category category : settings.radarCategories) {
            if (category != null) {
               List<String> mobs = new ArrayList<>();
               if (category.mobs != null) {
                  for (String mob : category.mobs) {
                     String clean = cleanMob(mob);
                     if (clean != null && !mobs.contains(clean)) {
                        mobs.add(clean);
                     }
                  }
               }

               category.mobs = mobs;
               category.iconScale = category.iconScale <= 0 ? 0 : Settings.snapScale(category.iconScale);
               if (category.names == null) {
                  category.names = Settings.NameTags.NEVER;
               }

               categories.add(category);
            }
         }
      }

      settings.radarCategories = categories;
      Map<String, RadarCategories.KindLook> kinds = new TreeMap<>();
      if (settings.radarKinds != null) {
         for (Radar.Kind kind : Radar.Kind.values()) {
            RadarCategories.KindLook look = settings.radarKinds.get(kind.name());
            if (look != null) {
               look.iconScale = look.iconScale <= 0 ? 0 : Settings.snapScale(look.iconScale);
               if (look.names == null) {
                  look.names = Settings.NameTags.NEVER;
               }

               kinds.put(kind.name(), look);
            }
         }
      }

      settings.radarKinds = kinds;
      if (!settings.radarPokemonAdded && (installed("cobblemon") || installed("pixelmon"))) {
         settings.radarPokemonAdded = true;
         RadarCategories.Category pokemon = new RadarCategories.Category();
         pokemon.builtIn = "pokemon";
         pokemon.mobs = new ArrayList<>(POKEMON_MOBS);
         categories.add(0, pokemon);
      }

      changed();
   }

   public static List<RadarCategories.Mob> mobs() {
      List<RadarCategories.Mob> mobs = new ArrayList<>();

      for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE) {
         String id = EntityType.getKey(type).toString();
         if (hasAttributes(type) && !id.equals("minecraft:player") && !id.equals("minecraft:armor_stand")) {
            mobs.add(new RadarCategories.Mob(id, type.getDescription()));
         }
      }

      mobs.sort(
         Comparator.<RadarCategories.Mob, String>comparing(mob -> mob.name().getString().toLowerCase(Locale.ROOT)).thenComparing(RadarCategories.Mob::id)
      );
      return mobs;
   }

   private static boolean hasAttributes(EntityType<?> type) {
      return DefaultAttributes.hasSupplier(type);
   }

   public static Set<String> mobMods(List<RadarCategories.Mob> mobs) {
      Set<String> mods = new TreeSet<>();

      for (RadarCategories.Mob mob : mobs) {
         String namespace = mob.id().substring(0, mob.id().indexOf(58));
         if (!namespace.equals("minecraft")) {
            mods.add(namespace);
         }
      }

      return mods;
   }

   public static String modName(String id) {
      return ModList.get().getModContainerById(id).map(mod -> mod.getModInfo().getDisplayName()).orElse(id);
   }

   private static boolean installed(String mod) {
      return ModList.get().isLoaded(mod);
   }

   static {
      for (Radar.Kind kind : Radar.Kind.values()) {
         KINDS[kind.ordinal()] = new RadarCategories.KindEntry(kind);
      }

      BY_TYPE = new IdentityHashMap<>();
   }

   public static final class Category implements RadarCategories.Entry {
      public @Nullable String name;
      public @Nullable String builtIn;
      public List<String> mobs = new ArrayList<>();
      public boolean shown = true;
      public int iconScale = 0;
      public Settings.NameTags names = Settings.NameTags.NEVER;

      @Override
      public Component name() {
         return this.name != null && !this.name.isBlank()
            ? Component.literal(this.name)
            : Component.translatable("pokemon".equals(this.builtIn) ? "diorama.radar_categories.pokemon" : "diorama.radar_categories.unnamed");
      }

      @Override
      public boolean shown() {
         return this.shown;
      }

      @Override
      public void shown(boolean shown) {
         this.shown = shown;
      }

      @Override
      public int iconScale() {
         return this.iconScale;
      }

      @Override
      public void iconScale(int percent) {
         this.iconScale = percent;
      }

      @Override
      public boolean hasNames() {
         return true;
      }

      @Override
      public Settings.NameTags names() {
         return this.names;
      }

      @Override
      public void names(Settings.NameTags names) {
         this.names = names;
      }

      public boolean has(String id) {
         for (String mob : this.mobs) {
            if (RadarCategories.matches(mob, id)) {
               return true;
            }
         }

         return false;
      }
   }

   public interface Entry {
      Component name();

      boolean shown();

      void shown(boolean var1);

      int iconScale();

      void iconScale(int var1);

      boolean hasNames();

      Settings.NameTags names();

      void names(Settings.NameTags var1);
   }

   private record KindEntry(Radar.Kind kind) implements RadarCategories.Entry {
      @Override
      public Component name() {
         return Component.translatable("diorama.settings." + switch (this.kind) {
            case PLAYER -> "players";
            case HOSTILE -> "hostile";
            case NEUTRAL -> "neutral";
            case PASSIVE -> "passive";
            case ITEM -> "items";
         });
      }

      @Override
      public boolean shown() {
         Settings s = Settings.get();

         return switch (this.kind) {
            case PLAYER -> s.radarPlayers;
            case HOSTILE -> s.radarHostile;
            case NEUTRAL -> s.radarNeutral;
            case PASSIVE -> s.radarPassive;
            case ITEM -> s.radarItems;
         };
      }

      @Override
      public void shown(boolean shown) {
         Settings s = Settings.get();
         switch (this.kind) {
            case PLAYER:
               s.radarPlayers = shown;
               break;
            case HOSTILE:
               s.radarHostile = shown;
               break;
            case NEUTRAL:
               s.radarNeutral = shown;
               break;
            case PASSIVE:
               s.radarPassive = shown;
               break;
            case ITEM:
               s.radarItems = shown;
         }
      }

      private RadarCategories.@Nullable KindLook look() {
         return Settings.get().radarKinds.get(this.kind.name());
      }

      private RadarCategories.KindLook lookToChange() {
         return Settings.get().radarKinds.computeIfAbsent(this.kind.name(), k -> new RadarCategories.KindLook());
      }

      @Override
      public int iconScale() {
         RadarCategories.KindLook look = this.look();
         return look == null ? 0 : look.iconScale;
      }

      @Override
      public void iconScale(int percent) {
         this.lookToChange().iconScale = percent;
      }

      @Override
      public boolean hasNames() {
         return this.kind != Radar.Kind.ITEM;
      }

      @Override
      public Settings.NameTags names() {
         if (this.kind == Radar.Kind.PLAYER) {
            return Settings.get().playerNames;
         }

         RadarCategories.KindLook look = this.look();
         return look == null ? Settings.NameTags.NEVER : look.names;
      }

      @Override
      public void names(Settings.NameTags names) {
         if (this.kind == Radar.Kind.PLAYER) {
            Settings.get().playerNames = names;
         } else {
            this.lookToChange().names = names;
         }
      }
   }

   public static final class KindLook {
      public int iconScale = 0;
      public Settings.NameTags names = Settings.NameTags.NEVER;
   }

   public record Mob(String id, Component name) {
   }
}
