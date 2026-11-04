package net.satisfy.brewery.core.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.compat.jei.category.BrewingStationCategory;
import net.satisfy.brewery.core.recipe.BrewingRecipe;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class BreweryJEIClientPlugin implements IModPlugin {
   @NotNull
   public ResourceLocation getPluginUid() {
      return Brewery.identifier("jei_plugin");
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new BrewingStationCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
      List<RecipeHolder<BrewingRecipe>> recipeHolders = rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.BREWING_RECIPE_TYPE.get());
      List<BrewingRecipe> recipes = new ArrayList<>();
      recipeHolders.forEach(rh -> recipes.add((BrewingRecipe)rh.value()));
      registration.addRecipes(BrewingStationCategory.TYPE, recipes);
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.WOODEN_BREWINGSTATION.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{BrewingStationCategory.TYPE}
      );
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.COPPER_BREWINGSTATION.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{BrewingStationCategory.TYPE}
      );
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.NETHERITE_BREWINGSTATION.get()).asItem().getDefaultInstance(),
         new mezz.jei.api.recipe.RecipeType[]{BrewingStationCategory.TYPE}
      );
   }
}
