package net.satisfy.farm_and_charm.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.core.recipe.CraftingBowlRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class CraftingBowlCategory implements IRecipeCategory<CraftingBowlRecipe> {
   public static final RecipeType<CraftingBowlRecipe> DOUGHING = RecipeType.create("farm_and_charm", "doughing", CraftingBowlRecipe.class);
   public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("farm_and_charm", "textures/gui/crafting_bowl.png");
   private final IDrawable background;
   private final IDrawable icon;
   private static final int WIDTH = 176;
   private static final int HEIGHT = 85;

   public CraftingBowlCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ((Block)ObjectRegistry.CRAFTING_BOWL.get()).asItem().getDefaultInstance());
   }

   @NotNull
   public RecipeType<CraftingBowlRecipe> getRecipeType() {
      return DOUGHING;
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("rei.farm_and_charm.bowl_category");
   }

   public int getWidth() {
      return 176;
   }

   public int getHeight() {
      return 85;
   }

   public void draw(CraftingBowlRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, CraftingBowlRecipe recipe, IFocusGroup focuses) {
      if (!recipe.getIngredients().isEmpty()) {
         builder.addSlot(RecipeIngredientRole.INPUT, 50, 25).addIngredients((Ingredient)recipe.getIngredients().get(0));
      }

      if (recipe.getIngredients().size() > 1) {
         builder.addSlot(RecipeIngredientRole.INPUT, 50, 43).addIngredients((Ingredient)recipe.getIngredients().get(1));
      }

      if (recipe.getIngredients().size() > 2) {
         builder.addSlot(RecipeIngredientRole.INPUT, 32, 25).addIngredients((Ingredient)recipe.getIngredients().get(2));
      }

      if (recipe.getIngredients().size() > 3) {
         builder.addSlot(RecipeIngredientRole.INPUT, 32, 43).addIngredients((Ingredient)recipe.getIngredients().get(3));
      }

      builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 35).addItemStack(recipe.getResultItem(null));
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((CraftingBowlRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (CraftingBowlRecipe)nullx, nullxx);
   }
}
