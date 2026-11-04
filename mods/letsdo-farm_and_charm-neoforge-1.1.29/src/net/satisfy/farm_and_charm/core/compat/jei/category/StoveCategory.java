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
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.satisfy.farm_and_charm.client.gui.StoveGui;
import net.satisfy.farm_and_charm.core.compat.jei.FarmAndCharmJEIPlugin;
import net.satisfy.farm_and_charm.core.recipe.StoveRecipe;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class StoveCategory implements IRecipeCategory<StoveRecipe> {
   public static final RecipeType<StoveRecipe> STOVE = RecipeType.create("farm_and_charm", "stove", StoveRecipe.class);
   public static final int WIDTH = 124;
   public static final int HEIGHT = 60;
   public static final int WIDTH_OF = 26;
   public static final int HEIGHT_OF = 13;
   private final IDrawable background;
   private final IDrawable icon;
   private final IDrawable burnIcon;
   private final IDrawableAnimated arrow;
   private final Component localizedName;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !StoveCategory.class.desiredAssertionStatus();

   public StoveCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(StoveGui.BACKGROUND, 26, 13, 124, 60);
      this.arrow = helper.drawableBuilder(StoveGui.BACKGROUND, 178, 20, 18, 25).buildAnimated(240, StartDirection.LEFT, false);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ((Block)ObjectRegistry.STOVE.get()).asItem().getDefaultInstance());
      this.burnIcon = helper.createDrawable(StoveGui.BACKGROUND, 176, 0, 17, 15);
      this.localizedName = Component.translatable("rei.farm_and_charm.stove_category");
   }

   public void setRecipe(IRecipeLayoutBuilder builder, StoveRecipe recipe, IFocusGroup focuses) {
      NonNullList<Ingredient> ingredients = recipe.getIngredients();
      FarmAndCharmJEIPlugin.addSlot(builder, 3, 5, (Ingredient)ingredients.get(0));
      if (ingredients.size() > 1) {
         FarmAndCharmJEIPlugin.addSlot(builder, 21, 5, (Ingredient)ingredients.get(1));
      }

      if (ingredients.size() > 2) {
         FarmAndCharmJEIPlugin.addSlot(builder, 39, 5, (Ingredient)ingredients.get(2));
      }

      assert Minecraft.getInstance().level != null;
      builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 29).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
   }

   public void draw(StoveRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
      this.arrow.draw(guiGraphics, 67, 19);
      this.burnIcon.draw(guiGraphics, 36, 36);
      this.drawExperience(recipe, guiGraphics);
   }

   protected void drawExperience(StoveRecipe recipe, GuiGraphics guiGraphics) {
      float experience = recipe.getExperience();
      if (experience > 0.0F) {
         Component experienceString = Component.translatable("gui.jei.category.smelting.experience", new Object[]{experience});
         Minecraft minecraft = Minecraft.getInstance();
         Font fontRenderer = minecraft.font;
         int stringWidth = fontRenderer.width(experienceString);
         guiGraphics.drawString(fontRenderer, experienceString, this.getWidth() - stringWidth, 0, -8355712, false);
      }
   }

   public int getWidth() {
      return 124;
   }

   public int getHeight() {
      return 60;
   }

   @NotNull
   public RecipeType<StoveRecipe> getRecipeType() {
      return STOVE;
   }

   @NotNull
   public Component getTitle() {
      return this.localizedName;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((StoveRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (StoveRecipe)nullx, nullxx);
   }
}
