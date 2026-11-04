package net.satisfy.brewery.core.compat.jei.category;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.satisfy.brewery.Brewery;
import net.satisfy.brewery.core.recipe.BrewingRecipe;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class BrewingStationCategory implements IRecipeCategory<BrewingRecipe> {
   public static final ResourceLocation UID = Brewery.identifier("brewing");
   public static final ResourceLocation TEXTURE = Brewery.identifier("textures/gui/brewingstation.png");
   public static final RecipeType<BrewingRecipe> TYPE = new RecipeType(UID, BrewingRecipe.class);
   private final IDrawable background;
   private final IDrawable icon;
   private final int width;
   private final int height;

   public BrewingStationCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack((ItemLike)ObjectRegistry.COPPER_BREWINGSTATION.get()));
      this.width = 176;
      this.height = 85;
   }

   @NotNull
   public RecipeType<BrewingRecipe> getRecipeType() {
      return TYPE;
   }

   @NotNull
   public Component getTitle() {
      return ((Block)ObjectRegistry.WOODEN_BREWINGSTATION.get()).getName();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void draw(BrewingRecipe recipe, IRecipeSlotsView slots, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
   }

   public void setRecipe(IRecipeLayoutBuilder builder, BrewingRecipe recipe, IFocusGroup focuses) {
      builder.addSlot(RecipeIngredientRole.INPUT, 50, 17).addIngredients((Ingredient)recipe.getIngredients().get(0));
      builder.addSlot(RecipeIngredientRole.INPUT, 50, 35).addIngredients((Ingredient)recipe.getIngredients().get(1));
      builder.addSlot(RecipeIngredientRole.INPUT, 50, 53).addIngredients((Ingredient)recipe.getIngredients().get(2));
      builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 35).addItemStack(recipe.getResultItem(null));
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((BrewingRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (BrewingRecipe)nullx, nullxx);
   }
}
