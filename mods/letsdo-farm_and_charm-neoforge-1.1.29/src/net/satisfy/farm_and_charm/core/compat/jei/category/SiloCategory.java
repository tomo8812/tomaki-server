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
import net.satisfy.farm_and_charm.core.recipe.SiloRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import org.jetbrains.annotations.NotNull;

public class SiloCategory implements IRecipeCategory<SiloRecipe> {
   public static final RecipeType<SiloRecipe> DRYING_TYPE = RecipeType.create("farm_and_charm", "drying", SiloRecipe.class);
   private static final int WIDTH = 150;
   private static final int HEIGHT = 50;
   private static final int INPUT_X = 39;
   private static final int ARROW_X = 63;
   private static final int OUTPUT_X = 93;
   private static final int ROW_Y = 16;
   private final IDrawable icon;
   private final IDrawable slot;

   public SiloCategory(IGuiHelper helper) {
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ObjectRegistry.SILO_WOOD.get()));
      this.slot = helper.getSlotDrawable();
   }

   @NotNull
   public RecipeType<SiloRecipe> getRecipeType() {
      return DRYING_TYPE;
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("rei.farm_and_charm.silo_category");
   }

   public int getWidth() {
      return 150;
   }

   public int getHeight() {
      return 50;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, SiloRecipe recipe, IFocusGroup focuses) {
      List<Ingredient> ingredients = recipe.getIngredients();
      IRecipeSlotBuilder input = builder.addSlot(RecipeIngredientRole.INPUT, 40, 17).setBackground(this.slot, -1, -1);
      if (!ingredients.isEmpty()) {
         input.addIngredients(ingredients.get(0));
      }

      builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 17).setBackground(this.slot, -1, -1).addItemStack(recipe.getResultItem(null));
   }

   public void draw(SiloRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
      RecipeViewerLayout.drawRightArrow(graphics, 63, 17);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((SiloRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (SiloRecipe)nullx, nullxx);
   }
}
