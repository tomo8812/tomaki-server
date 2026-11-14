package dev.diorama.compat;

import dev.diorama.Diorama;
import java.lang.reflect.Method;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.fml.ModList;
import org.jspecify.annotations.Nullable;

public final class Pixelmon {
   private static boolean started;
   private static @Nullable Class<?> pokemon;
   private static @Nullable Method sprite;

   private Pixelmon() {
   }

   public static @Nullable ResourceLocation sprite(Entity entity) {
      if (!started) {
         started = true;
         if (installed()) {
            try {
               pokemon = Class.forName("com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity");
               sprite = pokemon.getMethod("getSprite");
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
               Diorama.LOGGER.warn("Pixelmon is installed, but its sprites couldn't be found; Pokémon will be dots on the radar", e);
               pokemon = null;
               sprite = null;
            }
         }
      }

      if (sprite != null && pokemon.isInstance(entity)) {
         try {
            return sprite.invoke(entity) instanceof ResourceLocation location ? location : null;
         } catch (ReflectiveOperationException | RuntimeException e) {
            Diorama.LOGGER.warn("Couldn't read a Pokémon's sprite; Pokémon will be dots on the radar", e);
            sprite = null;
            return null;
         }
      } else {
         return null;
      }
   }

   private static boolean installed() {
      return ModList.get().isLoaded("pixelmon");
   }
}
