package net.satisfy.herbalbrews.core.compat.jei.category;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.satisfy.herbalbrews.core.recipe.CauldronRecipe;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;
import net.satisfy.herbalbrews.core.util.HerbalBrewsIdentifier;
import org.jetbrains.annotations.NotNull;

public class CauldronCategory implements IRecipeCategory<CauldronRecipe> {
   public static final RecipeType<CauldronRecipe> CAULDRON_TYPE = RecipeType.create("herbalbrews", "cauldron_brewing", CauldronRecipe.class);
   private static final int BACKGROUND_WIDTH = 147;
   private static final int BACKGROUND_HEIGHT = 64;
   private static final int X_OFFSET = 26;
   private static final int Y_OFFSET = 13;
   private final IDrawable background;
   private final IDrawable icon;
   private final Component title;

   public CauldronCategory(IGuiHelper helper) {
      ResourceLocation texture = HerbalBrewsIdentifier.identifier("textures/gui/cauldron.png");
      this.background = helper.createDrawable(texture, 26, 13, 147, 64);
      ItemStack cauldronStack = new ItemStack((ItemLike)ObjectRegistry.CAULDRON.get());
      this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, cauldronStack);
      this.title = ((Block)ObjectRegistry.TEA_KETTLE.get()).getName();
   }

   @NotNull
   public RecipeType<CauldronRecipe> getRecipeType() {
      return CAULDRON_TYPE;
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

   public void setRecipe(IRecipeLayoutBuilder builder, CauldronRecipe recipe, IFocusGroup focuses) {
      ItemStack potion1 = new ItemStack(Items.POTION);
      potion1.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder((Potion)Potions.SWIFTNESS.value())));
      ItemStack potion2 = new ItemStack(Items.POTION);
      potion2.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder((Potion)Potions.HEALING.value())));
      ItemStack potion3 = new ItemStack(Items.POTION);
      potion3.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder((Potion)Potions.STRENGTH.value())));
      builder.addSlot(RecipeIngredientRole.INPUT, 53, 9).addItemStack(potion1);
      builder.addSlot(RecipeIngredientRole.INPUT, 31, 3).addItemStack(potion2);
      builder.addSlot(RecipeIngredientRole.INPUT, 75, 3).addItemStack(potion3);
      ItemStack outputFlask = new ItemStack((ItemLike)ObjectRegistry.FLASK.get());
      outputFlask.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder((Potion)Potions.REGENERATION.value())));
      builder.addSlot(RecipeIngredientRole.OUTPUT, 53, 45).addItemStack(outputFlask);
      ItemStack herbalInfusion = new ItemStack((ItemLike)ObjectRegistry.HERBAL_INFUSION.get());
      builder.addSlot(RecipeIngredientRole.INPUT, 122, 29).addItemStack(herbalInfusion);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void setRecipe(IRecipeLayoutBuilder param1, Object nullx, IFocusGroup nullxx) {
      this.setRecipe(var1, (CauldronRecipe)nullx, nullxx);
   }
}
