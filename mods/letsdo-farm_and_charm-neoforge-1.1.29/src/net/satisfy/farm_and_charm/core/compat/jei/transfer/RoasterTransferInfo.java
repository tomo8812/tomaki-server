package net.satisfy.farm_and_charm.core.compat.jei.transfer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.satisfy.farm_and_charm.client.gui.handler.RoasterGuiHandler;
import net.satisfy.farm_and_charm.core.compat.jei.category.RoasterCategory;
import net.satisfy.farm_and_charm.core.recipe.RoasterRecipe;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class RoasterTransferInfo implements IRecipeTransferInfo<RoasterGuiHandler, RoasterRecipe> {
   @NotNull
   public Class<? extends RoasterGuiHandler> getContainerClass() {
      return RoasterGuiHandler.class;
   }

   @NotNull
   public Optional<MenuType<RoasterGuiHandler>> getMenuType() {
      return Optional.of((MenuType<RoasterGuiHandler>)ScreenhandlerTypeRegistry.ROASTER_SCREEN_HANDLER.get());
   }

   @NotNull
   public RecipeType<RoasterRecipe> getRecipeType() {
      return RoasterCategory.ROASTER;
   }

   public boolean canHandle(RoasterGuiHandler container, RoasterRecipe recipe) {
      return true;
   }

   @NotNull
   public List<Slot> getRecipeSlots(RoasterGuiHandler container, RoasterRecipe recipe) {
      List<Slot> slots = new ArrayList<>();
      slots.add(container.getSlot(7));

      for (int i = 1; i <= recipe.getIngredients().size() && i < 7; i++) {
         slots.add(container.getSlot(i));
      }

      return slots;
   }

   @NotNull
   public List<Slot> getInventorySlots(RoasterGuiHandler container, RoasterRecipe recipe) {
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
      return this.getInventorySlots((RoasterGuiHandler)var1, (RoasterRecipe)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public List getRecipeSlots(AbstractContainerMenu param1, Object nullx) {
      return this.getRecipeSlots((RoasterGuiHandler)var1, (RoasterRecipe)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public boolean canHandle(AbstractContainerMenu param1, Object nullx) {
      return this.canHandle((RoasterGuiHandler)var1, (RoasterRecipe)nullx);
   }
}
