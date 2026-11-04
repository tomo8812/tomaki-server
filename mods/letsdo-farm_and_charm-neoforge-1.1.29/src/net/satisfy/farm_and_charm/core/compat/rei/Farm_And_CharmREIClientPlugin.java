package net.satisfy.farm_and_charm.core.compat.rei;

import dev.architectury.event.EventResult;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.core.compat.rei.cooking.CookingPotCategory;
import net.satisfy.farm_and_charm.core.compat.rei.cooking.CookingPotDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.cutting.AssemblyCategory;
import net.satisfy.farm_and_charm.core.compat.rei.cutting.AssemblyDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.cutting.CuttingBoardCategory;
import net.satisfy.farm_and_charm.core.compat.rei.cutting.CuttingBoardDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.cutting.StrippingCategory;
import net.satisfy.farm_and_charm.core.compat.rei.cutting.StrippingDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.doughing.CraftingBowlCategory;
import net.satisfy.farm_and_charm.core.compat.rei.doughing.CraftingBowlDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.mincing.MincingCategory;
import net.satisfy.farm_and_charm.core.compat.rei.mincing.MincingDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.roasting.RoasterCategory;
import net.satisfy.farm_and_charm.core.compat.rei.roasting.RoasterDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.silo.SiloCategory;
import net.satisfy.farm_and_charm.core.compat.rei.silo.SiloDisplay;
import net.satisfy.farm_and_charm.core.compat.rei.stove.StoveCategory;
import net.satisfy.farm_and_charm.core.compat.rei.stove.StoveDisplay;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.core.util.Strippables;

public class Farm_And_CharmREIClientPlugin implements REIClientPlugin {
   public void registerCategories(CategoryRegistry registry) {
      registry.add(new CookingPotCategory());
      registry.add(new StoveCategory());
      registry.add(new CraftingBowlCategory());
      registry.add(new RoasterCategory());
      registry.add(new SiloCategory());
      registry.add(new MincingCategory());
      registry.add(new DisplayCategory[]{new CuttingBoardCategory(), new AssemblyCategory(), new StrippingCategory()});
      registry.addWorkstations(
         CuttingBoardDisplay.ID,
         new EntryStack[]{
            EntryStacks.of((ItemLike)ObjectRegistry.CUTTING_BOARD.get()),
            EntryStacks.of((ItemLike)ObjectRegistry.IRON_CLEAVER.get()),
            EntryStacks.of((ItemLike)ObjectRegistry.DIAMOND_CLEAVER.get()),
            EntryStacks.of((ItemLike)ObjectRegistry.NETHERITE_CLEAVER.get())
         }
      );
      registry.addWorkstations(AssemblyDisplay.ID, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.CUTTING_BOARD.get())});
      registry.addWorkstations(
         StrippingDisplay.ID, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.CUTTING_BOARD.get()), EntryStacks.of(Items.IRON_AXE)}
      );
      registry.addWorkstations(MincingCategory.MINCING_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.MINCER.get())});
      registry.addWorkstations(CraftingBowlCategory.CRAFTING_BOWL_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.CRAFTING_BOWL.get())});
      registry.addWorkstations(CookingPotDisplay.COOKING_POT_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.COOKING_POT.get())});
      registry.addWorkstations(StoveDisplay.STOVE_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.STOVE.get())});
      registry.addWorkstations(RoasterDisplay.ROASTER_DISPLAY, new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.ROASTER.get())});
      registry.addWorkstations(
         SiloCategory.SILO_DISPLAY,
         new EntryStack[]{EntryStacks.of((ItemLike)ObjectRegistry.SILO_WOOD.get()), EntryStacks.of((ItemLike)ObjectRegistry.SILO_COPPER.get())}
      );
   }

   public void registerEntries(EntryRegistry registry) {
      registry.removeEntryIf(entry -> entry.getValue() instanceof ItemStack stack && stack.is(TagRegistry.CAMPFIRE_MEATS));
   }

   public void registerDisplays(DisplayRegistry registry) {
      registry.registerVisibilityPredicate(
         (category, display) -> display.getOutputEntries()
               .stream()
               .flatMap(Collection::stream)
               .anyMatch(entry -> entry.getValue() instanceof ItemStack stack && stack.is(TagRegistry.CAMPFIRE_MEATS))
            ? EventResult.interruptFalse()
            : EventResult.pass()
      );
      registry.registerRecipeFiller(
         CookingPotRecipe.class,
         (RecipeType)RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get(),
         holder -> new CookingPotDisplay((CookingPotRecipe)holder.value())
      );
      registry.registerRecipeFiller(
         MincerRecipe.class, (RecipeType)RecipeTypeRegistry.MINCER_RECIPE_TYPE.get(), holder -> new MincingDisplay((MincerRecipe)holder.value())
      );
      registry.registerRecipeFiller(
         StoveRecipe.class, (RecipeType)RecipeTypeRegistry.STOVE_RECIPE_TYPE.get(), holder -> new StoveDisplay((StoveRecipe)holder.value())
      );
      registry.registerRecipeFiller(
         CraftingBowlRecipe.class,
         (RecipeType)RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get(),
         holder -> new CraftingBowlDisplay((CraftingBowlRecipe)holder.value())
      );
      registry.registerRecipeFiller(
         RoasterRecipe.class, (RecipeType)RecipeTypeRegistry.ROASTER_RECIPE_TYPE.get(), holder -> new RoasterDisplay((RoasterRecipe)holder.value())
      );
      registry.registerRecipeFiller(
         SiloRecipe.class, (RecipeType)RecipeTypeRegistry.SILO_RECIPE_TYPE.get(), holder -> new SiloDisplay((SiloRecipe)holder.value())
      );
      registry.registerRecipeFiller(
         CuttingBoardRecipe.class,
         (RecipeType)RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get(),
         holder -> new CuttingBoardDisplay((CuttingBoardRecipe)holder.value())
      );
      registry.registerRecipeFiller(
         CuttingBoardAssemblyRecipe.class,
         (RecipeType)RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get(),
         holder -> new AssemblyDisplay((CuttingBoardAssemblyRecipe)holder.value())
      );
      Strippables.all().forEach(entry -> registry.add(new StrippingDisplay(entry)));
   }

   public static List<Ingredient> ingredients(Recipe<RecipeInput> recipe, ItemStack stack) {
      List<Ingredient> l = new ArrayList<>(recipe.getIngredients());
      if (!stack.isEmpty()) {
         l.add(0, Ingredient.of(new ItemLike[]{stack.getItem()}));
      }

      return l;
   }
}
