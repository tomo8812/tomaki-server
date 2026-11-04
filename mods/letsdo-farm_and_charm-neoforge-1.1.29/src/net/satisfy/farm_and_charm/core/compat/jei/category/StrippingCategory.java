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
import net.minecraft.world.item.Items;
import net.satisfy.farm_and_charm.core.util.Strippables;
import net.satisfy.foundation.compat.RecipeViewerLayout;
import org.jetbrains.annotations.NotNull;

public class StrippingCategory implements IRecipeCategory<Strippables.Entry> {
   public static final RecipeType<Strippables.Entry> TYPE = RecipeType.create("farm_and_charm", "log_stripping", Strippables.Entry.class);
   private static final int INPUT_X = 10;
   private static final int ARROW_X = 34;
   private static final int OUTPUT_X = 64;
   private static final int ROW_Y = 6;
   private final IDrawable icon;
   private final IDrawable slot;

   public StrippingCategory(IGuiHelper helper) {
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.IRON_AXE));
      this.slot = helper.getSlotDrawable();
   }

   @NotNull
   public RecipeType<Strippables.Entry> getRecipeType() {
      return TYPE;
   }

   @NotNull
   public Component getTitle() {
      return Component.translatable("category.farm_and_charm.log_stripping");
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

   public void setRecipe(IRecipeLayoutBuilder builder, Strippables.Entry recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 11, 7).setBackground(this.slot, -1, -1).addItemStack(recipe.log());
      builder.addSlot(RecipeIngredientRole.OUTPUT, 65, 7).setBackground(this.slot, -1, -1).addItemStack(recipe.stripped());
   }

   public void draw(Strippables.Entry recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
      RecipeViewerLayout.drawRightArrow(graphics, 34, 7);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((Strippables.Entry)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (Strippables.Entry)nullx, nullxx);
   }
}
