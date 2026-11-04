package net.satisfy.farm_and_charm.core.compat.jei.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.satisfy.farm_and_charm.client.gui.handler.CookingPotGuiHandler;
import net.satisfy.farm_and_charm.core.compat.jei.category.CookingPotCategory;
import net.satisfy.farm_and_charm.core.recipe.CookingPotRecipe;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class CookingPotTransferInfo implements IRecipeTransferInfo<CookingPotGuiHandler, CookingPotRecipe> {
   @NotNull
   public Class<? extends CookingPotGuiHandler> getContainerClass() {
      return CookingPotGuiHandler.class;
   }

   @NotNull
   public Optional<MenuType<CookingPotGuiHandler>> getMenuType() {
      return Optional.of((MenuType<CookingPotGuiHandler>)ScreenhandlerTypeRegistry.COOKING_POT_SCREEN_HANDLER.get());
   }

   @NotNull
   public RecipeType<CookingPotRecipe> getRecipeType() {
      return CookingPotCategory.COOKING_POT;
   }

   public boolean canHandle(CookingPotGuiHandler container, CookingPotRecipe recipe) {
      return true;
   }

   @NotNull
   public List<Slot> getRecipeSlots(CookingPotGuiHandler container, CookingPotRecipe recipe) {
      List<Slot> slots = new ArrayList<>();
      slots.add(container.getSlot(7));

      for (int i = 1; i <= recipe.getIngredients().size() && i < 7; i++) {
         slots.add(container.getSlot(i));
      }

      return slots;
   }

   @NotNull
   public List<Slot> getInventorySlots(CookingPotGuiHandler container, CookingPotRecipe recipe) {
      List<Slot> slots = new ArrayList<>();

      for (int i = 8; i < 44; i++) {
         Slot slot = container.getSlot(i);
         slots.add(slot);
      }

      return slots;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public List getInventorySlots(AbstractContainerMenu param1, Object nullx) {
      return this.getInventorySlots((CookingPotGuiHandler)var1, (CookingPotRecipe)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public List getRecipeSlots(AbstractContainerMenu param1, Object nullx) {
      return this.getRecipeSlots((CookingPotGuiHandler)var1, (CookingPotRecipe)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public boolean canHandle(AbstractContainerMenu param1, Object nullx) {
      return this.canHandle((CookingPotGuiHandler)var1, (CookingPotRecipe)nullx);
   }
}
