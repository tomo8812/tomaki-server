package net.satisfy.herbalbrews.core.compat.jei.category;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.satisfy.herbalbrews.core.recipe.TeaKettleRecipe;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import net.satisfy.herbalbrews.core.registry.TagsRegistry;
import net.satisfy.herbalbrews.core.util.HerbalBrewsIdentifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

public class TeaKettleCategory implements IRecipeCategory<TeaKettleRecipe> {
   public static final RecipeType<TeaKettleRecipe> TEA_KETTLE_TYPE = RecipeType.create("herbalbrews", "tea_kettle_brewing", TeaKettleRecipe.class);
   private static final int BACKGROUND_WIDTH = 160;
   private static final int BACKGROUND_HEIGHT = 70;
   private static final int X_OFFSET = 10;
   private static final int Y_OFFSET = 10;
   private static final Vector2i ARROW_POS = new Vector2i(54, 22);
   private static final int ARROW_U = 176;
   private static final int ARROW_V = 14;
   private static final int ARROW_WIDTH = 24;
   private static final int ARROW_HEIGHT = 17;
   private static final int MAX_TIME = 200;
   private final IDrawable background;
   private final IDrawable icon;
   private final IDrawable burnIcon;
   private final IDrawableAnimated arrow;
   private final Component title;
   private final ResourceLocation texture = HerbalBrewsIdentifier.identifier("textures/gui/tea_kettle.png");
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !TeaKettleCategory.class.desiredAssertionStatus();

   public TeaKettleCategory(IGuiHelper helper) {
      this.background = helper.createDrawable(this.texture, 10, 10, 160, 70);
      this.arrow = helper.drawableBuilder(this.texture, 176, 14, 24, 17).buildAnimated(200, StartDirection.LEFT, false);
      ItemStack kettleStack = new ItemStack((ItemLike)ObjectRegistry.TEA_KETTLE.get());
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, kettleStack);
      this.burnIcon = helper.createDrawable(this.texture, 176, 0, 14, 14);
      this.title = ((Block)ObjectRegistry.TEA_KETTLE.get()).getName();
   }

   @NotNull
   public RecipeType<TeaKettleRecipe> getRecipeType() {
      return TEA_KETTLE_TYPE;
   }

   @NotNull
   public Component getTitle() {
      return this.title;
   }

   @NotNull
   public IDrawable getBackground() {
      return this.background;
   }

   @NotNull
   public IDrawable getIcon() {
      return this.icon;
   }

   public void setRecipe(IRecipeLayoutBuilder builder, TeaKettleRecipe recipe, IFocusGroup focuses) {
      NonNullList<Ingredient> ingredients = recipe.getIngredients();
      int s = Math.min(ingredients.size(), 4);
      assert Minecraft.getInstance().level != null;
      builder.addSlot(RecipeIngredientRole.OUTPUT, 81, 12).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
      ItemStack waterBottle = new ItemStack(Items.POTION);
      waterBottle.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder((Potion)Potions.WATER.value())));
      builder.addSlot(RecipeIngredientRole.INPUT, 108, 33).addIngredients(Ingredient.of(new ItemStack[]{new ItemStack(Items.WATER_BUCKET), waterBottle}));
      int idx = 0;

      for (int row = 0; row < 2; row++) {
         for (int col = 0; col < 2 && idx < s; col++) {
            Ingredient ingredient = (Ingredient)ingredients.get(idx);
            Ingredient filteredIngredient = this.filterOutTag(ingredient);
            if (!filteredIngredient.isEmpty()) {
               int xPos = 13 + col * 18;
               int yPos = 12 + row * 18;
               builder.addSlot(RecipeIngredientRole.INPUT, xPos - 10, yPos - 10).addIngredients(filteredIngredient);
            }

            idx++;
         }
      }

      if (this.containsTagIngredient(ingredients)) {
         builder.addSlot(RecipeIngredientRole.INPUT, 21, 42).addIngredients(Ingredient.of(TagsRegistry.CONTAINER_ITEMS));
      }

      if (this.containsTagIngredient(ingredients)) {
         builder.addSlot(RecipeIngredientRole.INPUT, 85, 48).addIngredients(Ingredient.of(TagsRegistry.HEAT_ITEMS));
      }
   }

   private Ingredient filterOutTag(Ingredient ingredient) {
      List<ItemStack> filteredStacks = new ArrayList<>();

      for (ItemStack stack : ingredient.getItems()) {
         if (!stack.is(TagsRegistry.CONTAINER_ITEMS)) {
            filteredStacks.add(stack);
         }
      }

      return filteredStacks.isEmpty() ? Ingredient.EMPTY : Ingredient.of(filteredStacks.toArray(new ItemStack[0]));
   }

   private boolean containsTagIngredient(NonNullList<Ingredient> ingredients) {
      for (Ingredient ingredient : ingredients) {
         for (ItemStack stack : ingredient.getItems()) {
            if (stack.is(TagsRegistry.CONTAINER_ITEMS)) {
               return true;
            }
         }
      }

      return false;
   }

   public void draw(TeaKettleRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
      this.arrow.draw(guiGraphics, ARROW_POS.x() - 10, ARROW_POS.y() - 10);
      this.burnIcon.draw(guiGraphics, 142, 52);
      int requiredWater = recipe.getRequiredWater();
      int fluidFillHeight = Math.min(requiredWater * 43 / 100, 43);
      guiGraphics.blit(this.texture, 131, 6 + (43 - fluidFillHeight), 183, 31 + (43 - fluidFillHeight), 8, fluidFillHeight);
      int requiredHeat = recipe.getRequiredHeat();
      int heatFillHeight = Math.min(requiredHeat * 43 / 100, 43);
      guiGraphics.blit(this.texture, 146, 6 + (43 - heatFillHeight), 176, 31 + (43 - heatFillHeight), 5, heatFillHeight);
      new Rectangle(131, 6, 8, 43);
      new Rectangle(146, 6, 5, 43);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void draw(Object param1, IRecipeSlotsView nullx, GuiGraphics nullxx, double nullxxx, double nullxxxx) {
      this.draw((TeaKettleRecipe)var1, nullx, nullxx, nullxxx, nullxxxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (TeaKettleRecipe)nullx, nullxx);
   }
}
