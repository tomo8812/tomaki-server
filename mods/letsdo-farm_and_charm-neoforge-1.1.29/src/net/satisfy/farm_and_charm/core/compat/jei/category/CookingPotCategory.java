package net.satisfy.farm_and_charm.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableAnimated.StartDirection;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.client.gui.CookingPotGui;
import net.satisfy.farm_and_charm.core.block.entity.CookingPotBlockEntity;
import net.satisfy.farm_and_charm.core.compat.jei.FarmAndCharmJEIPlugin;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class CookingPotCategory implements IRecipeCategory<CookingPotRecipe> {
   public static final RecipeType<CookingPotRecipe> COOKING_POT = RecipeType.create("farm_and_charm", "cooking_pot", CookingPotRecipe.class);
   public static final int WIDTH = 124;
   public static final int HEIGHT = 60;
   public static final int WIDTH_OF = 26;
   public static final int HEIGHT_OF = 13;
   private final IDrawable background;
   private final IDrawable icon;
   private final IDrawableAnimated arrow;
   private final Component localizedName;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !CookingPotCategory.class.desiredAssertionStatus();

   public CookingPotCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(CookingPotGui.BACKGROUND, 26, 13, 124, 60);
      this.arrow = helper.drawableBuilder(CookingPotGui.BACKGROUND, 178, 15, 18, 30)
         .buildAnimated(CookingPotBlockEntity.getMaxCookingTime(), StartDirection.LEFT, false);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ((Block)ObjectRegistry.COOKING_POT.get()).asItem().getDefaultInstance());
      this.localizedName = Component.translatable("rei.farm_and_charm.cooking_pot_category");
   }

   public void setRecipe(IRecipeLayoutBuilder builder, CookingPotRecipe recipe, IFocusGroup focuses) {
      NonNullList<Ingredient> ingredients = recipe.getIngredients();
      int s = ingredients.size();
      if (recipe.isContainerRequired()) {
         builder.addSlot(RecipeIngredientRole.INPUT, 69, 42).addItemStack(recipe.getContainerItem());
      }

      for (int row = 0; row < 2; row++) {
         for (int slot = 0; slot < 3; slot++) {
            int current = slot + row * 3;
            if (current >= s) {
               break;
            }

            FarmAndCharmJEIPlugin.addSlot(builder, 30 + slot * 18 - 26, 17 + row * 18 - 13, (Ingredient)ingredients.get(current));
         }
      }

      assert Minecraft.getInstance().level != null;
      builder.addSlot(RecipeIngredientRole.OUTPUT, 98, 15).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
   }

   public void draw(CookingPotRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.arrow.draw(guiGraphics, 69, 1);
   }

   @NotNull
   public RecipeType<CookingPotRecipe> getRecipeType() {
      return COOKING_POT;
   }

   @NotNull
   public Component getTitle() {
      return this.localizedName;
   }

   @NotNull
   public IDrawable getBackground() {
      return this.background;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((CookingPotRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (CookingPotRecipe)nullx, nullxx);
   }
}
