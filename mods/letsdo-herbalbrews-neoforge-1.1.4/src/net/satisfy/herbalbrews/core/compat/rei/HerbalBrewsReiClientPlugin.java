package net.satisfy.herbalbrews.core.compat.rei;

import java.util.ArrayList;
import java.util.List;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.satisfy.herbalbrews.core.compat.rei.category.CauldronCategory;
import net.satisfy.herbalbrews.core.compat.rei.category.TeaKettleCategory;
import net.satisfy.herbalbrews.core.compat.rei.display.CauldronDisplay;
import net.satisfy.herbalbrews.core.compat.rei.display.TeaKettleDisplay;
import net.satisfy.herbalbrews.core.recipe.CauldronRecipe;
import net.satisfy.herbalbrews.core.recipe.TeaKettleRecipe;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import net.satisfy.herbalbrews.core.registry.RecipeTypeRegistry;

public class HerbalBrewsReiClientPlugin {
   public static void registerCategories(CategoryRegistry registry) {
      registry.add(new TeaKettleCategory());
      registry.add(new CauldronCategory());
      registry.addWorkstations(TeaKettleCategory.TEA_KETTLE_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.TEA_KETTLE.get())});
      registry.addWorkstations(TeaKettleCategory.TEA_KETTLE_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.COPPER_TEA_KETTLE.get())});
      registry.addWorkstations(CauldronDisplay.CAULDRON_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.CAULDRON.get())});
   }

   public static void registerDisplays(DisplayRegistry registry) {
      registry.registerRecipeFiller(
         TeaKettleRecipe.class, (RecipeType)RecipeTypeRegistry.TEA_KETTLE_RECIPE_TYPE.get(), holder -> new TeaKettleDisplay((TeaKettleRecipe)holder.value())
      );
      registry.registerRecipeFiller(
         CauldronRecipe.class, (RecipeType)RecipeTypeRegistry.CAULDRON_RECIPE_TYPE.get(), holder -> new CauldronDisplay((CauldronRecipe)holder.value())
      );
   }

   public static List<Ingredient> ingredients(Recipe<RecipeInput> recipe, ItemStack stack) {
      List<Ingredient> l = new ArrayList<>(recipe.getIngredients());
      l.add(0, Ingredient.of(new ItemLike[]{stack.getItem()}));
      return l;
   }
}
