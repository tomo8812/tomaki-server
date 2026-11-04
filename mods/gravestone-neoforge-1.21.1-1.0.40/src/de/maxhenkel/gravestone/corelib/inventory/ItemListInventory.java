package de.maxhenkel.gravestone.corelib.inventory;

import java.util.function.Function;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemListInventory implements Container {
   protected NonNullList<ItemStack> items;
   private Runnable onMarkDirty;
   private Function<Player, Boolean> onIsUsableByPlayer;

   public ItemListInventory(NonNullList<ItemStack> items, Runnable onMarkDirty, Function<Player, Boolean> onIsUsableByPlayer) {
      this.items = items;
      this.onMarkDirty = onMarkDirty;
      this.onIsUsableByPlayer = onIsUsableByPlayer;
   }

   public ItemListInventory(NonNullList<ItemStack> items, Runnable onMarkDirty) {
      this(items, onMarkDirty, null);
   }

   public ItemListInventory(NonNullList<ItemStack> items) {
      this(items, null);
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      return this.items.stream().allMatch(ItemStack::isEmpty);
   }

   public ItemStack getItem(int index) {
      return (ItemStack)this.items.get(index);
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack itemstack = ContainerHelper.removeItem(this.items, index, count);
      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int index) {
      return ContainerHelper.takeItem(this.items, index);
   }

   public void setItem(int index, ItemStack stack) {
      this.items.set(index, stack);
      if (stack.getCount() > this.getMaxStackSize()) {
         stack.setCount(this.getMaxStackSize());
      }

      this.setChanged();
   }

   public void setChanged() {
      if (this.onMarkDirty != null) {
         this.onMarkDirty.run();
      }
   }

   public boolean stillValid(Player player) {
      return this.onIsUsableByPlayer != null ? this.onIsUsableByPlayer.apply(player) : true;
   }

   public void clearContent() {
      this.items.clear();
   }
}
