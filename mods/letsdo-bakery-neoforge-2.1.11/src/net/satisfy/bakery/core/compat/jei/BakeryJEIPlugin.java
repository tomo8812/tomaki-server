package net.satisfy.bakery.core.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.compat.jei.category.BakerStationCategory;
import net.satisfy.bakery.core.compat.jei.category.JamPotCategory;
import net.satisfy.bakery.core.recipe.BakingStationRecipe;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import net.satisfy.bakery.core.registry.RecipeTypeRegistry;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class BakeryJEIPlugin implements IModPlugin {
   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new BakerStationCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new JamPotCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerRecipes(IRecipeRegistration registration) {
      RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
      List<RecipeHolder<BakingStationRecipe>> bakingRecipesHolders = rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.BAKING_STATION_RECIPE_TYPE.get());
      List<BakingStationRecipe> bakingRecipes = new ArrayList<>();
      bakingRecipesHolders.forEach(bakingStationRecipeRecipeHolder -> bakingRecipes.add((BakingStationRecipe)bakingStationRecipeRecipeHolder.value()));
      registration.addRecipes(BakerStationCategory.CAKING, bakingRecipes);
      registration.addRecipes(
         JamPotCategory.JAM_COOKING, rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.JAM_COOKING_TYPE.get()).stream().map(RecipeHolder::value).toList()
      );
   }

   @NotNull
   public ResourceLocation getPluginUid() {
      return Bakery.identifier("jei_plugin");
   }

   public void onRuntimeAvailable(IJeiRuntime runtime) {
      runtime.getIngredientManager()
         .removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, List.of(((Item)ObjectRegistry.BAKED_SWEET_DOUGH.get()).getDefaultInstance()));
      RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
      runtime.getRecipeManager().hideRecipes(RecipeTypes.CAMPFIRE_COOKING, hiddenRecipes(rm, RecipeType.CAMPFIRE_COOKING));
      runtime.getRecipeManager().hideRecipes(RecipeTypes.SMOKING, hiddenRecipes(rm, RecipeType.SMOKING));
   }

   private static <T extends AbstractCookingRecipe> List<RecipeHolder<T>> hiddenRecipes(RecipeManager rm, RecipeType<T> type) {
      return rm.getAllRecipesFor(type)
         .stream()
         .filter(
            holder -> ((AbstractCookingRecipe)holder.value())
               .getResultItem(Minecraft.getInstance().level.registryAccess())
               .is((Item)ObjectRegistry.BAKED_SWEET_DOUGH.get())
         )
         .toList();
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.BAKER_STATION.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{BakerStationCategory.CAKING}
      );
      registration.addRecipeCatalyst(
         ((Item)ObjectRegistry.SMALL_COOKING_POT_ITEM.get()).getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{JamPotCategory.JAM_COOKING}
      );
   }
}
