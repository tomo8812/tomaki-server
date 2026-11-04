package net.satisfy.farm_and_charm.core.compat.jei.category;

import java.util.List;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.satisfy.farm_and_charm.core.recipe.CuttingBoardAssemblyRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import net.satisfy.foundation.compat.RecipeViewerLayout.AssemblyLayout;
import net.satisfy.foundation.compat.RecipeViewerLayout.Pos;
import org.jetbrains.annotations.NotNull;

public class AssemblyCategory implements IRecipeCategory<CuttingBoardAssemblyRecipe> {
   public static final RecipeType<CuttingBoardAssemblyRecipe> TYPE = RecipeType.create(
      "farm_and_charm", "cutting_board_assembly", CuttingBoardAssemblyRecipe.class
   );
   private final IDrawable icon;
   private final IDrawable slot;

   public AssemblyCategory(IGuiHelper helper) {
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ObjectRegistry.CUTTING_BOARD.get()));
      this.slot = helper.getSlotDrawable();
   }

   @NotNull
   public RecipeType<CuttingBoardAssemblyRecipe> getRecipeType() {
      return TYPE;
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("category.farm_and_charm.assembly");
   }

   public int getWidth() {
      return RecipeViewerLayout.assemblyWidth(3);
   }

   public int getHeight() {
      return 54;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, CuttingBoardAssemblyRecipe recipe, IFocusGroup focuses) {
      List<Ingredient> ingredients = recipe.getIngredients();
      AssemblyLayout layout = RecipeViewerLayout.assembly(ingredients.size(), recipe.isOrdered(), 3);

      for (int i = 0; i < layout.slots().size(); i++) {
         Pos pos = (Pos)layout.slots().get(i);
         IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, pos.x() + 1, pos.y() + 1).setBackground(this.slot, -1, -1);
         if (i < ingredients.size()) {
            input.addIngredients(ingredients.get(i));
         }
      }

      Pos output = layout.output();
      builder.addSlot(RecipeIngredientRole.OUTPUT, output.x() + 1, output.y() + 1).setBackground(this.slot, -1, -1).addItemStack(recipe.getResult());
   }

   public void draw(CuttingBoardAssemblyRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
      for (Pos pos : RecipeViewerLayout.assembly(recipe.getIngredients().size(), recipe.isOrdered(), 3).arrows()) {
         RecipeViewerLayout.drawRightArrow(graphics, pos.x(), pos.y());
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((CuttingBoardAssemblyRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (CuttingBoardAssemblyRecipe)nullx, nullxx);
   }
}
