package net.satisfy.bakery.core.compat.rei;

import dev.architectury.event.EventResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.satisfy.bakery.core.compat.rei.caking.BakerStationCategory;
import net.satisfy.bakery.core.compat.rei.caking.BakerStationDisplay;
import net.satisfy.bakery.core.compat.rei.jam.JamPotCategory;
import net.satisfy.bakery.core.compat.rei.jam.JamPotDisplay;
import net.satisfy.bakery.core.recipe.BakingStationRecipe;
import net.satisfy.bakery.core.recipe.JamRecipe;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;

public class BakeryREIClientPlugin {
   public static void registerCategories(CategoryRegistry registry) {
      registry.add(new BakerStationCategory());
      registry.addWorkstations(BakerStationCategory.BAKER_STATION_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.BAKER_STATION.get())});
      registry.add(new JamPotCategory());
      registry.addWorkstations(JamPotDisplay.JAM_POT_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.SMALL_COOKING_POT_ITEM.get())});
   }

   public static void registerDisplays(DisplayRegistry registry) {
      registry.registerRecipeFiller(
         BakingStationRecipe.class,
         (RecipeType)RecipeTypeRegistry.BAKING_STATION_RECIPE_TYPE.get(),
         holder -> new BakerStationDisplay((BakingStationRecipe)holder.value())
      );
      registry.registerRecipeFiller(
         JamRecipe.class, (RecipeType)RecipeTypeRegistry.JAM_COOKING_TYPE.get(), holder -> new JamPotDisplay((JamRecipe)holder.value())
      );
      registry.registerVisibilityPredicate(
         (category, display) -> display.getOutputEntries()
               .stream()
               .flatMap(Collection::stream)
               .anyMatch(entry -> entry.getValue() instanceof ItemStack stack && stack.is((Item)ObjectRegistry.BAKED_SWEET_DOUGH.get()))
            ? EventResult.interruptFalse()
            : EventResult.pass()
      );
   }

   public static void registerEntries(EntryRegistry registry) {
      registry.removeEntry(EntryStacks.of((ItemLike)ObjectRegistry.BAKED_SWEET_DOUGH.get()));
   }

   public static List<Ingredient> ingredients(Recipe<RecipeInput> recipe, ItemStack stack) {
      List<Ingredient> l = new ArrayList<>(recipe.getIngredients());
      l.add(0, Ingredient.of(new ItemLike[]{stack.getItem()}));
      return l;
   }
}
