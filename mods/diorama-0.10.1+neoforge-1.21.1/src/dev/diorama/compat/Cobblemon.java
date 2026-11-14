package dev.diorama.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.diorama.Diorama;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public final class Cobblemon {
   private static boolean started;
   private static Object client;
   private static Method battle;
   private static boolean subjectsStarted;
   private static @Nullable Class<?> pokemonEntity;
   private static Method pokemonOf;
   private static Method speciesOf;
   private static Method speciesId;
   private static Method pokemonAspects;
   private static @Nullable Class<?> npcEntity;
   private static Method npcId;
   private static Method npcAspects;
   private static boolean portraitsStarted;
   private static @Nullable Method posablePortrait;
   private static Constructor<?> floatingState;
   private static Method setAspects;
   private static @Nullable Method pokemonPortrait;
   private static Object allSpecies;
   private static Method speciesById;

   private Cobblemon() {
   }

   public static boolean inBattle() {
      if (!started) {
         started = true;
         if (installed()) {
            try {
               Class<?> type = Class.forName("com.cobblemon.mod.common.client.CobblemonClient");
               client = type.getField("INSTANCE").get(null);
               battle = type.getMethod("getBattle");
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
               Diorama.LOGGER.warn("Cobblemon is installed, but its battles couldn't be found; the minimap won't step aside for them", e);
               battle = null;
            }
         }
      }

      if (battle == null) {
         return false;
      }

      try {
         return battle.invoke(client) != null;
      } catch (ReflectiveOperationException | RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read Cobblemon's battle; the minimap won't step aside for battles", e);
         battle = null;
         return false;
      }
   }

   public static Cobblemon.@Nullable Subject subject(Entity entity) {
      if (!has(entity)) {
         return null;
      }

      try {
         return pokemonEntity != null && pokemonEntity.isInstance(entity)
            ? subject(speciesId.invoke(speciesOf.invoke(pokemonOf.invoke(entity))), pokemonAspects.invoke(entity), false)
            : subject(npcId.invoke(entity), npcAspects.invoke(entity), true);
      } catch (ReflectiveOperationException | RuntimeException e) {
         Diorama.LOGGER.warn("Couldn't read which Pokémon or NPC an entity is; Cobblemon's will be dots on the radar", e);
         pokemonEntity = null;
         npcEntity = null;
         return null;
      }
   }

   public static boolean has(Entity entity) {
      if (!subjectsStarted) {
         subjectsStarted = true;
         if (installed()) {
            try {
               Class<?> type = Class.forName("com.cobblemon.mod.common.entity.pokemon.PokemonEntity");
               pokemonOf = type.getMethod("getPokemon");
               speciesOf = pokemonOf.getReturnType().getMethod("getSpecies");
               speciesId = speciesOf.getReturnType().getMethod("getResourceIdentifier");
               pokemonAspects = type.getMethod("getAspects");
               pokemonEntity = type;
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
               Diorama.LOGGER.warn("Cobblemon is installed, but its Pokémon couldn't be read; they will be dots on the radar", e);
            }

            try {
               Class<?> type = Class.forName("com.cobblemon.mod.common.entity.npc.NPCEntity");
               npcId = type.getMethod("getResourceIdentifier");
               npcAspects = type.getMethod("getAspects");
               npcEntity = type;
            } catch (ReflectiveOperationException | RuntimeException | LinkageError var2) {
            }
         }
      }

      return pokemonEntity != null && pokemonEntity.isInstance(entity) || npcEntity != null && npcEntity.isInstance(entity);
   }

   private static Cobblemon.@Nullable Subject subject(Object id, Object aspects, boolean npc) {
      if (id instanceof ResourceLocation identifier && aspects instanceof Set<?> set) {
         TreeSet names = new TreeSet();

         for (Object aspect : set) {
            names.add(String.valueOf(aspect));
         }

         return new Cobblemon.Subject(identifier, Collections.unmodifiableSet(names), npc);
      } else {
         return null;
      }
   }

   public static boolean drawPortrait(Cobblemon.Subject subject, PoseStack pose) throws ReflectiveOperationException {
      if (!portraitsStarted) {
         portraitsStarted = true;

         try {
            Class<?> gui = Class.forName("com.cobblemon.mod.common.api.gui.GuiUtilsKt");
            Class<?> state = stateClass();
            if (state != null) {
               floatingState = Class.forName("com.cobblemon.mod.common.client.render.models.blockbench.FloatingState").getConstructor();
               setAspects = state.getMethod("setCurrentAspects", Set.class);
               posablePortrait = gui.getMethod("drawPosablePortrait", ResourceLocation.class, PoseStack.class, state, float.class);
            } else {
               Class<?> species = Class.forName("com.cobblemon.mod.common.pokemon.Species");
               Class<?> all = Class.forName("com.cobblemon.mod.common.api.pokemon.PokemonSpecies");
               allSpecies = all.getField("INSTANCE").get(null);
               speciesById = all.getMethod("getByIdentifier", ResourceLocation.class);
               pokemonPortrait = gui.getMethod(
                  "drawPortraitPokemon",
                  species,
                  Set.class,
                  PoseStack.class,
                  float.class,
                  boolean.class,
                  Class.forName("com.cobblemon.mod.common.client.render.models.blockbench.PoseableEntityState"),
                  float.class
               );
            }
         } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
            Diorama.LOGGER.warn("Cobblemon is installed, but its portraits couldn't be found; Pokémon will be dots on the radar", e);
            posablePortrait = null;
            pokemonPortrait = null;
         }
      }

      pose.translate(9.5F, -12.0F, 0.0F);
      if (posablePortrait != null) {
         Object state = floatingState.newInstance();
         setAspects.invoke(state, subject.aspects());
         posablePortrait.invoke(null, subject.id(), pose, state, 0.0F);
         return true;
      }

      if (pokemonPortrait != null && !subject.npc()) {
         Object species = speciesById.invoke(allSpecies, subject.id());
         if (species == null) {
            return false;
         }

         pokemonPortrait.invoke(null, species, subject.aspects(), pose, 13.0F, false, null, 0.0F);
         return true;
      } else {
         return false;
      }
   }

   private static @Nullable Class<?> stateClass() {
      try {
         return Class.forName("com.cobblemon.mod.common.client.render.models.blockbench.PosableState");
      } catch (ClassNotFoundException e) {
         return null;
      }
   }

   private static boolean installed() {
      return ModList.get().isLoaded("cobblemon");
   }

   public record Subject(ResourceLocation id, Set<String> aspects, boolean npc) {
      public boolean shiny() {
         return !this.npc && this.aspects.contains("shiny");
      }
   }
}
