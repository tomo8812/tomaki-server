package net.satisfy.bakery.core.compat.jei.category;

import java.util.HashMap;
import java.util.Map;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.satisfy.bakery.core.recipe.JamRecipe;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class JamPotCategory implements IRecipeCategory<JamRecipe> {
   public static final RecipeType<JamRecipe> JAM_COOKING = RecipeType.create("bakery", "jam_cooking", JamRecipe.class);
   public static final int INPUTS = 3;
   private static final int WIDTH = 116;
   private static final int HEIGHT = 26;
   private static final int ARROW_X = 58;
   private static final int OUTPUT_X = 89;
   private final IDrawable slot;
   private final IDrawable outputSlot;
   private final IDrawable icon;
   private final IGuiHelper helper;
   private final Map<Integer, IDrawableAnimated> arrows = new HashMap<>();

   public JamPotCategory(IGuiHelper helper) {
      this.helper = helper;
      this.slot = helper.getSlotDrawable();
      this.outputSlot = helper.getOutputSlot();
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ((Item)ObjectRegistry.SMALL_COOKING_POT_ITEM.get()).getDefaultInstance());
   }

   @NotNull
   public RecipeType<JamRecipe> getRecipeType() {
      return JAM_COOKING;
   }

   @NotNull
   public Component getTitle() {
      return ((Item)ObjectRegistry.SMALL_COOKING_POT_ITEM.get()).getDescription();
   }

   public int getWidth() {
      return 116;
   }

   public int getHeight() {
      return 26;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, JamRecipe recipe, IFocusGroup focuses) {
      NonNullList<Ingredient> ingredients = recipe.getIngredients();

      for (int i = 0; i < 3 && i < ingredients.size(); i++) {
         builder.addSlot(RecipeIngredientRole.INPUT, 1 + i * 18, 5).addIngredients((Ingredient)ingredients.get(i));
      }

      if (Minecraft.getInstance().level != null) {
         builder.addSlot(RecipeIngredientRole.OUTPUT, 93, 5).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
      }
   }

   public void draw(JamRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      for (int i = 0; i < 3; i++) {
         this.slot.draw(guiGraphics, i * 18, 4);
      }

      this.outputSlot.draw(guiGraphics, 89, 0);
      this.arrows.computeIfAbsent(recipe.getCookingTime(), this.helper::createAnimatedRecipeArrow).draw(guiGraphics, 58, 5);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((JamRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (JamRecipe)nullx, nullxx);
   }
}
