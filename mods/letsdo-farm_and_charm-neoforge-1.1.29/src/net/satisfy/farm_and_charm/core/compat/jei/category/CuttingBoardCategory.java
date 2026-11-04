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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import org.jetbrains.annotations.NotNull;

public class CuttingBoardCategory implements IRecipeCategory<CuttingBoardRecipe> {
   public static final RecipeType<CuttingBoardRecipe> TYPE = RecipeType.create("farm_and_charm", "cutting_board", CuttingBoardRecipe.class);
   private static final int INPUT_X = 10;
   private static final int ARROW_X = 34;
   private static final int OUTPUT_X = 64;
   private static final int ROW_Y = 6;
   private final IDrawable icon;
   private final IDrawable slot;

   public CuttingBoardCategory(IGuiHelper helper) {
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ObjectRegistry.CUTTING_BOARD.get()));
      this.slot = helper.getSlotDrawable();
   }

   @NotNull
   public RecipeType<CuttingBoardRecipe> getRecipeType() {
      return TYPE;
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("category.farm_and_charm.cutting_board");
   }

   public int getWidth() {
      return 130;
   }

   public int getHeight() {
      return 36;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, CuttingBoardRecipe recipe, IFocusGroup focuses) {
      int offset = offset(recipe);
      builder.addSlot(RecipeIngredientRole.INPUT, offset + 10 + 1, 7).setBackground(this.slot, -1, -1).addIngredients(recipe.getIngredient());
      builder.addSlot(RecipeIngredientRole.OUTPUT, offset + 64 + 1, 7).setBackground(this.slot, -1, -1).addItemStack(recipe.getResult());
      int x = offset + 64 + 18;

      for (ItemStack byproduct : recipe.getByproducts()) {
         builder.addSlot(RecipeIngredientRole.OUTPUT, x + 1, 7).setBackground(this.slot, -1, -1).addItemStack(byproduct);
         x += 18;
      }
   }

   public void draw(CuttingBoardRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
      RecipeViewerLayout.drawRightArrow(graphics, offset(recipe) + 34, 7);
   }

   private static int offset(CuttingBoardRecipe recipe) {
      int width = 54 + (1 + recipe.getByproducts().size()) * 18;
      return (130 - width) / 2 - 10;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((CuttingBoardRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (CuttingBoardRecipe)nullx, nullxx);
   }
}
