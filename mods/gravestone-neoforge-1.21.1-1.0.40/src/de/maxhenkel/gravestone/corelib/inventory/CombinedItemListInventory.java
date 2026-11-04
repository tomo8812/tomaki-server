package de.maxhenkel.gravestone.corelib.inventory;

import java.util.Arrays;
import java.util.function.Function;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Deprecated
public class CombinedItemListInventory implements Container {
   protected NonNullList<ItemStack>[] items;
   private Runnable onMarkDirty;
   private Function<Player, Boolean> onIsUsableByPlayer;

   public CombinedItemListInventory(Runnable onMarkDirty, Function<Player, Boolean> onIsUsableByPlayer, NonNullList<ItemStack>... items) {
      this.items = items;
      this.onMarkDirty = onMarkDirty;
      this.onIsUsableByPlayer = onIsUsableByPlayer;
   }

   public CombinedItemListInventory(Runnable onMarkDirty, NonNullList<ItemStack>... items) {
      this(onMarkDirty, null, items);
   }

   public CombinedItemListInventory(NonNullList<ItemStack>... items) {
      this(null, items);
   }

   private int getListIndex(int slot) {
      int index = 0;

      for (int i = 0; i < this.items.length; i++) {
         NonNullList<ItemStack> list = this.items[i];
         if (slot >= index && slot < index + list.size()) {
            return i;
         }

         index += list.size();
      }

      return -1;
   }

   private int getLocalIndex(int slot) {
      int index = 0;

      for (int i = 0; i < this.items.length; i++) {
         NonNullList<ItemStack> list = this.items[i];
         if (slot >= index && slot < index + list.size()) {
            return slot - index;
         }

         index += list.size();
      }

      return -1;
   }

   public int getContainerSize() {
      return Arrays.stream(this.items).<Integer>map(NonNullList::size).reduce(0, Integer::sum);
   }

   public boolean isEmpty() {
      return Arrays.stream(this.items).allMatch(itemStacks -> itemStacks.stream().allMatch(ItemStack::isEmpty));
   }

   public ItemStack getItem(int index) {
      return (ItemStack)this.items[this.getListIndex(index)].get(this.getLocalIndex(index));
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack itemstack = ContainerHelper.removeItem(this.items[this.getListIndex(index)], this.getLocalIndex(index), count);
      if (!itemstack.isEmpty()) {
         this.setChanged();
      }

      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int index) {
      return ContainerHelper.takeItem(this.items[this.getListIndex(index)], this.getLocalIndex(index));
   }

   public void setItem(int index, ItemStack stack) {
      this.items[this.getListIndex(index)].set(this.getLocalIndex(index), stack);
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
      Arrays.stream(this.items).forEach(NonNullList::clear);
   }
}
