package net.satisfy.farm_and_charm.core.compat.rei.stove;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.SimpleGridMenuDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay.Serializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay.Serializer.RecipeLessConstructor;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.nbt.CompoundTag;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;

public class StoveDisplay extends BasicDisplay implements SimpleGridMenuDisplay {
   public static final CategoryIdentifier<StoveDisplay> STOVE_DISPLAY = CategoryIdentifier.of("farm_and_charm", "stove_display");
   private final float xp;

   public StoveDisplay(StoveRecipe recipe) {
      this(
         EntryIngredients.ofIngredients(recipe.getIngredients()),
         Collections.singletonList(EntryIngredients.of(recipe.getResultItem(BasicDisplay.registryAccess()))),
         recipe.getExperience()
      );
   }

   public StoveDisplay(List<EntryIngredient> input, List<EntryIngredient> output, CompoundTag tag) {
      this(input, output, tag.getFloat("experience"));
   }

   public StoveDisplay(List<EntryIngredient> input, List<EntryIngredient> output, float xp) {
      super(input, output, Optional.empty());
      this.xp = xp;
   }

   public static <R extends StoveDisplay> Serializer<R> serializer(RecipeLessConstructor<R> constructor) {
      return Serializer.ofRecipeLess(constructor, (display, tag) -> tag.putFloat("experience", display.getXp()));
   }

   public float getXp() {
      return this.xp;
   }

   public int getWidth() {
      return 3;
   }

   public int getHeight() {
      return 1;
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return STOVE_DISPLAY;
   }
}
