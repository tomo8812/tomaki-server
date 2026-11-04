package de.maxhenkel.gravestone.corelib.inventory;

import javax.annotation.Nonnull;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

public class RestrictedItemStackHandler extends ItemStackHandler {
   private RestrictedItemStackHandler.ItemValidator itemValidator;

   public RestrictedItemStackHandler(NonNullList<ItemStack> stacks, RestrictedItemStackHandler.ItemValidator itemValidator) {
      super(stacks);
      this.itemValidator = itemValidator;
   }

   public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
      return this.itemValidator.isValid(slot, stack);
   }

   public interface ItemValidator {
      boolean isValid(int var1, ItemStack var2);
   }
}
