package net.satisfy.herbalbrews.core.compat.rei.display;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.satisfy.herbalbrews.core.recipe.CauldronRecipe;
import net.satisfy.herbalbrews.core.registry.ObjectRegistry;

public class CauldronDisplay extends BasicDisplay {
   public static final CategoryIdentifier<CauldronDisplay> CAULDRON_DISPLAY = CategoryIdentifier.of("herbalbrews", "cauldron_display");

   public CauldronDisplay(CauldronRecipe recipe) {
      super(createInputs(), createOutputs(), Optional.of(recipe.getId()));
   }

   private static List<EntryIngredient> createInputs() {
      List<EntryIngredient> inputs = new ArrayList<>();
      inputs.add(EntryIngredients.of(createPotionStack((Potion)Potions.SWIFTNESS.value())));
      inputs.add(EntryIngredients.of(createPotionStack((Potion)Potions.HEALING.value())));
      inputs.add(EntryIngredients.of(createPotionStack((Potion)Potions.STRENGTH.value())));
      inputs.add(EntryIngredients.of(new ItemStack((ItemLike)ObjectRegistry.HERBAL_INFUSION.get())));
      return inputs;
   }

   private static List<EntryIngredient> createOutputs() {
      List<EntryIngredient> outputs = new ArrayList<>();
      outputs.add(EntryIngredients.of(new ItemStack((ItemLike)ObjectRegistry.FLASK.get())));
      return outputs;
   }

   private static ItemStack createPotionStack(Potion potionType) {
      ItemStack potion = new ItemStack(Items.POTION);
      potion.set(DataComponents.POTION_CONTENTS, new PotionContents(BuiltInRegistries.POTION.wrapAsHolder(potionType)));
      return potion;
   }

   public CategoryIdentifier<?> getCategoryIdentifier() {
      return CAULDRON_DISPLAY;
   }
}
