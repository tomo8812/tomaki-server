package net.satisfy.farm_and_charm.core.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet.ListBacked;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.client.gui.handler.RoasterGuiHandler;
import net.satisfy.farm_and_charm.client.gui.handler.StoveGuiHandler;
import net.satisfy.farm_and_charm.core.compat.jei.category.AssemblyCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.CookingPotCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.CraftingBowlCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.CuttingBoardCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.MincerCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.RoasterCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.SiloCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.StoveCategory;
import net.satisfy.farm_and_charm.core.compat.jei.category.StrippingCategory;
import net.satisfy.farm_and_charm.core.compat.jei.transfer.CookingPotTransferInfo;
import net.satisfy.farm_and_charm.core.compat.jei.transfer.RoasterTransferInfo;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.recipe.MincerRecipe;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.core.util.Strippables;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class FarmAndCharmJEIPlugin implements IModPlugin {
   public static void addSlot(IRecipeLayoutBuilder builder, int x, int y, Ingredient ingredient) {
      builder.addSlot(RecipeIngredientRole.INPUT, x, y).addIngredients(ingredient);
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new CookingPotCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new StoveCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new CraftingBowlCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new RoasterCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new SiloCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(new IRecipeCategory[]{new MincerCategory(registration.getJeiHelpers().getGuiHelper())});
      registration.addRecipeCategories(
         new IRecipeCategory[]{
            new CuttingBoardCategory(registration.getJeiHelpers().getGuiHelper()),
            new AssemblyCategory(registration.getJeiHelpers().getGuiHelper()),
            new StrippingCategory(registration.getJeiHelpers().getGuiHelper())
         }
      );
   }

   public void registerRecipes(IRecipeRegistration registration) {
      RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
      List<RecipeHolder<CookingPotRecipe>> cookingHoldersRecipes = rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.COOKING_POT_RECIPE_TYPE.get());
      List<CookingPotRecipe> cookingPotRecipes = new ArrayList<>();
      cookingHoldersRecipes.iterator().forEachRemaining(recipeHolder -> cookingPotRecipes.add((CookingPotRecipe)recipeHolder.value()));
      registration.addRecipes(CookingPotCategory.COOKING_POT, cookingPotRecipes);
      List<RecipeHolder<StoveRecipe>> stoveHolderRecipes = rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.STOVE_RECIPE_TYPE.get());
      List<StoveRecipe> stoveRecipes = new ArrayList<>();
      stoveHolderRecipes.iterator().forEachRemaining(recipeHolder -> stoveRecipes.add((StoveRecipe)recipeHolder.value()));
      registration.addRecipes(StoveCategory.STOVE, stoveRecipes);
      List<RecipeHolder<CraftingBowlRecipe>> doughingHolderRecipes = rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.CRAFTING_BOWL_RECIPE_TYPE.get());
      List<CraftingBowlRecipe> doughingRecipes = new ArrayList<>();
      doughingHolderRecipes.iterator().forEachRemaining(recipeHolder -> doughingRecipes.add((CraftingBowlRecipe)recipeHolder.value()));
      registration.addRecipes(CraftingBowlCategory.DOUGHING, doughingRecipes);
      List<RecipeHolder<RoasterRecipe>> roasterHolderRecipes = rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.ROASTER_RECIPE_TYPE.get());
      List<RoasterRecipe> roasterRecipes = new ArrayList<>();
      roasterHolderRecipes.iterator().forEachRemaining(recipeHolder -> roasterRecipes.add((RoasterRecipe)recipeHolder.value()));
      registration.addRecipes(RoasterCategory.ROASTER, roasterRecipes);
      List<RecipeHolder<SiloRecipe>> siloHolderRecipes = rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.SILO_RECIPE_TYPE.get());
      List<SiloRecipe> siloRecipes = new ArrayList<>();
      siloHolderRecipes.iterator().forEachRemaining(recipeHolder -> siloRecipes.add((SiloRecipe)recipeHolder.value()));
      registration.addRecipes(SiloCategory.DRYING_TYPE, siloRecipes);
      List<RecipeHolder<MincerRecipe>> mincerHolderRecipes = rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.MINCER_RECIPE_TYPE.get());
      List<MincerRecipe> mincerRecipes = new ArrayList<>();
      mincerHolderRecipes.iterator().forEachRemaining(recipeHolder -> mincerRecipes.add((MincerRecipe)recipeHolder.value()));
      registration.addRecipes(MincerCategory.MINCING_TYPE, mincerRecipes);
      registration.addRecipes(
         CuttingBoardCategory.TYPE,
         rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(
         AssemblyCategory.TYPE,
         rm.getAllRecipesFor((RecipeType)RecipeTypeRegistry.CUTTING_BOARD_ASSEMBLY_RECIPE_TYPE.get()).stream().map(RecipeHolder::value).toList()
      );
      registration.addRecipes(StrippingCategory.TYPE, Strippables.all());
   }

   @NotNull
   public ResourceLocation getPluginUid() {
      return FarmAndCharm.identifier("jei_plugin");
   }

   public void onRuntimeAvailable(IJeiRuntime runtime) {
      List<ItemStack> hidden = BuiltInRegistries.ITEM
         .getTag(TagRegistry.CAMPFIRE_MEATS)
         .stream()
         .<Holder>flatMap(ListBacked::stream)
         .map(holder -> new ItemStack((ItemLike)holder.value()))
         .toList();
      if (!hidden.isEmpty()) {
         runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM_STACK, hidden);
      }

      RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();
      List<RecipeHolder<CampfireCookingRecipe>> campfire = rm.getAllRecipesFor(RecipeType.CAMPFIRE_COOKING)
         .stream()
         .filter(holder -> ((CampfireCookingRecipe)holder.value()).getResultItem(Minecraft.getInstance().level.registryAccess()).is(TagRegistry.CAMPFIRE_MEATS))
         .toList();
      runtime.getRecipeManager().hideRecipes(RecipeTypes.CAMPFIRE_COOKING, campfire);
   }

   public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
      registration.addRecipeTransferHandler(new RoasterTransferInfo());
      registration.addRecipeTransferHandler(
         RoasterGuiHandler.class, (MenuType)ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get(), RoasterCategory.ROASTER, 1, 3, 5, 36
      );
      registration.addRecipeTransferHandler(new CookingPotTransferInfo());
      registration.addRecipeTransferHandler(
         StoveGuiHandler.class, (MenuType)ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), StoveCategory.STOVE, 1, 3, 5, 36
      );
   }

   public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.COOKING_POT.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{CookingPotCategory.COOKING_POT}
      );
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.CRAFTING_BOWL.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{CraftingBowlCategory.DOUGHING}
      );
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.STOVE.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{StoveCategory.STOVE}
      );
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.ROASTER.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{RoasterCategory.ROASTER}
      );
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.MINCER.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{MincerCategory.MINCING_TYPE}
      );
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.SILO_WOOD.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{SiloCategory.DRYING_TYPE}
      );
      registration.addRecipeCatalyst(
         ((Block)ObjectRegistry.SILO_COPPER.get()).asItem().getDefaultInstance(), new mezz.jei.api.recipe.RecipeType[]{SiloCategory.DRYING_TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ObjectRegistry.CUTTING_BOARD.get()),
         new mezz.jei.api.recipe.RecipeType[]{CuttingBoardCategory.TYPE, AssemblyCategory.TYPE, StrippingCategory.TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ObjectRegistry.IRON_CLEAVER.get()), new mezz.jei.api.recipe.RecipeType[]{CuttingBoardCategory.TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ObjectRegistry.DIAMOND_CLEAVER.get()), new mezz.jei.api.recipe.RecipeType[]{CuttingBoardCategory.TYPE}
      );
      registration.addRecipeCatalyst(
         new ItemStack((ItemLike)ObjectRegistry.NETHERITE_CLEAVER.get()), new mezz.jei.api.recipe.RecipeType[]{CuttingBoardCategory.TYPE}
      );
      registration.addRecipeCatalyst(new ItemStack(Items.IRON_AXE), new mezz.jei.api.recipe.RecipeType[]{StrippingCategory.TYPE});
   }
}
