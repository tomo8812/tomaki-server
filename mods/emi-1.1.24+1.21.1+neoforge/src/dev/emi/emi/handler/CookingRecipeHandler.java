package dev.emi.emi.handler;

import com.google.common.collect.Lists;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import java.util.List;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.Slot;

public class CookingRecipeHandler<T extends AbstractFurnaceMenu> implements StandardRecipeHandler<T> {
   private final EmiRecipeCategory category;

   public CookingRecipeHandler(EmiRecipeCategory category) {
      this.category = category;
   }

   public List<Slot> getInputSources(T handler) {
      List<Slot> list = Lists.newArrayList();
      list.add(handler.getSlot(0));
      int invStart = 3;

      for (int i = invStart; i < invStart + 36; i++) {
         list.add(handler.getSlot(i));
      }

      return list;
   }

   public List<Slot> getCraftingSlots(T handler) {
      return List.of((Slot)handler.slots.get(0));
   }

   @Override
   public boolean supportsRecipe(EmiRecipe recipe) {
      return recipe.getCategory() == this.category && recipe.supportsRecipeTree();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public List getCraftingSlots(AbstractContainerMenu param1) {
      return this.getCraftingSlots((T)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public List getInputSources(AbstractContainerMenu param1) {
      return this.getInputSources((T)var1);
   }
}
