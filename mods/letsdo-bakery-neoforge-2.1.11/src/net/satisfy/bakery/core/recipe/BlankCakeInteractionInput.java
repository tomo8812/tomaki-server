package net.satisfy.bakery.core.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import org.jetbrains.annotations.NotNull;

public record BlankCakeInteractionInput(ItemStack stack) implements RecipeInput {
   @NotNull
   public ItemStack getItem(int index) {
      return index == 0 ? this.stack : ItemStack.EMPTY;
   }

   public int size() {
      return 1;
   }
}
