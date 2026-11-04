package org.embeddedt.modernfix.common.mixin.bugfix.recipe_book_type_desync;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.stats.RecipeBookSettings;
import net.minecraft.world.inventory.RecipeBookType;
import org.embeddedt.modernfix.annotation.ClientOnlyMixin;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(RecipeBookSettings.class)
@ClientOnlyMixin
public class RecipeBookSettingsMixin {
   private static int mfix$maxVanillaOrdinal;

   static {
      int ord = 0;

      for (Field f : RecipeBookType.class.getDeclaredFields()) {
         if (RecipeBookType.class.isAssignableFrom(f.getType()) && Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
            try {
               f.setAccessible(true);
               RecipeBookType type = (RecipeBookType)f.get(null);
               ord = Math.max(type.ordinal(), ord);
            } catch (Exception e) {
               e.printStackTrace();
               ord = 2147483646;
               break;
            }
         }
      }

      mfix$maxVanillaOrdinal = ord;
   }
}
