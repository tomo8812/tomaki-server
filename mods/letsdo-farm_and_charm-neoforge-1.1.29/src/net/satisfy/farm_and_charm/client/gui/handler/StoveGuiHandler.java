package net.satisfy.farm_and_charm.client.gui.handler;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.satisfy.farm_and_charm.core.registry.RecipeTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ScreenhandlerTypeRegistry;
import net.satisfy.foundation.menu.ExtendedSlot;
import net.satisfy.foundation.menu.OutputSlot;
import net.satisfy.foundation.recipe.book.StationRecipeBookMenu;
import org.jetbrains.annotations.NotNull;

public class StoveGuiHandler extends AbstractContainerMenu implements StationRecipeBookMenu {
   private static final int OUTPUT_SLOT = 0;
   private static final int[] INPUT_SLOTS = new int[]{1, 2, 3};
   private final Container inventory;
   public final ContainerData propertyDelegate;

   public StoveGuiHandler(int syncId, Inventory playerInventory) {
      this(syncId, playerInventory, new SimpleContainer(5), new SimpleContainerData(4));
   }

   public StoveGuiHandler(int syncId, Inventory playerInventory, Container inventory, ContainerData propertyDelegate) {
      super((MenuType)ScreenhandlerTypeRegistry.STOVE_SCREEN_HANDLER.get(), syncId);
      this.inventory = inventory;
      this.propertyDelegate = propertyDelegate;
      this.addDataSlots(propertyDelegate);
      this.buildBlockEntityContainer(playerInventory);
      this.buildPlayerContainer(playerInventory);
   }

   private static boolean isFuel(ItemStack stack) {
      return AbstractFurnaceBlockEntity.isFuel(stack);
   }

   private void buildBlockEntityContainer(Inventory playerInventory) {
      this.addSlot(new OutputSlot(playerInventory.player, this.inventory, 0, 126, 42));
      this.addSlot(new ExtendedSlot(this.inventory, 1, 29, 18));
      this.addSlot(new ExtendedSlot(this.inventory, 2, 47, 18));
      this.addSlot(new ExtendedSlot(this.inventory, 3, 65, 18));
      this.addSlot(new ExtendedSlot(this.inventory, 4, 42, 48, StoveGuiHandler::isFuel));
   }

   private void buildPlayerContainer(Inventory playerInventory) {
      for (int row = 0; row < 3; row++) {
         for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
         }
      }

      for (int col = 0; col < 9; col++) {
         this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
      }
   }

   @NotNull
   public ItemStack quickMoveStack(Player player, int index) {
      Slot slot = (Slot)this.slots.get(index);
      if (!slot.hasItem()) {
         return ItemStack.EMPTY;
      }

      ItemStack stack = slot.getItem();
      ItemStack copy = stack.copy();
      if (index == 0) {
         if (!this.moveItemStackTo(stack, 5, 41, true)) {
            return ItemStack.EMPTY;
         }

         slot.onQuickCraft(stack, copy);
      } else if (index >= 1 && index <= 3) {
         if (!this.moveItemStackTo(stack, 5, 41, true)) {
            return ItemStack.EMPTY;
         }
      } else if (index == 4) {
         if (!this.moveItemStackTo(stack, 5, 41, true)) {
            return ItemStack.EMPTY;
         }
      } else if (isFuel(stack)) {
         if (!this.moveItemStackTo(stack, 4, 5, false)) {
            return ItemStack.EMPTY;
         }
      } else if (!this.moveItemStackTo(stack, 1, 4, false)) {
         return ItemStack.EMPTY;
      }

      if (stack.isEmpty()) {
         slot.set(ItemStack.EMPTY);
      } else {
         slot.setChanged();
      }

      if (stack.getCount() == copy.getCount()) {
         return ItemStack.EMPTY;
      }

      slot.onTake(player, stack);
      return copy;
   }

   public RecipeType<?> recipeBookType() {
      return (RecipeType<?>)RecipeTypeRegistry.STOVE_RECIPE_TYPE.get();
   }

   public int[] recipeBookInputSlots() {
      return INPUT_SLOTS;
   }

   public int recipeBookResultSlot(RecipeType<?> type) {
      return 0;
   }

   public boolean stillValid(Player player) {
      return this.inventory.stillValid(player);
   }

   public int getScaledProgress(int arrowWidth) {
      int progress = this.propertyDelegate.get(2);
      int totalProgress = this.propertyDelegate.get(3);
      return progress > 0 && totalProgress > 0 ? progress * arrowWidth / totalProgress : 0;
   }

   public boolean isBeingBurned() {
      return this.propertyDelegate.get(0) > 0;
   }
}
