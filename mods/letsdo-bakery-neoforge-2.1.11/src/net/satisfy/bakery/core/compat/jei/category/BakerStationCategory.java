package net.satisfy.bakery.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;
import net.satisfy.bakery.Bakery;
import net.satisfy.bakery.core.recipe.BakingStationRecipe;
import net.satisfy.bakery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class BakerStationCategory implements IRecipeCategory<BakingStationRecipe> {
   public static final RecipeType<BakingStationRecipe> CAKING = RecipeType.create("bakery", "caking", BakingStationRecipe.class);
   public static final ResourceLocation TEXTURE = Bakery.identifier("textures/gui/baking_station.png");
   private static final int WIDTH = 176;
   private static final int HEIGHT = 85;
   private final IDrawable background;
   private final IDrawable icon;

   public BakerStationCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, ((Block)ObjectRegistry.BAKER_STATION.get()).asItem().getDefaultInstance());
   }

   @NotNull
   public RecipeType<BakingStationRecipe> getRecipeType() {
      return CAKING;
   }

   @NotNull
   public Component getTitle() {
      return ((Block)ObjectRegistry.BAKER_STATION.get()).getName();
   }

   public int getWidth() {
      return 176;
   }

   public int getHeight() {
      return 85;
   }

   public void draw(BakingStationRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.background.draw(guiGraphics);
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, BakingStationRecipe recipe, IFocusGroup focuses) {
      if (!recipe.getIngredients().isEmpty()) {
         builder.addSlot(RecipeIngredientRole.INPUT, 50, 25).addIngredients((Ingredient)recipe.getIngredients().get(0));
      }

      if (recipe.getIngredients().size() > 1) {
         builder.addSlot(RecipeIngredientRole.INPUT, 50, 43).addIngredients((Ingredient)recipe.getIngredients().get(1));
      }

      if (Minecraft.getInstance().level != null) {
         builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 35).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((BakingStationRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (BakingStationRecipe)nullx, nullxx);
   }
}
