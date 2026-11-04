package de.maxhenkel.gravestone.corelib.item;

import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ItemUtils {
   public static ItemStack itemStackAmount(int amount, ItemStack stack, Player player) {
      if (stack == null || stack.isEmpty()) {
         return ItemStack.EMPTY;
      }

      if (player != null && player.getAbilities().instabuild) {
         return stack;
      }

      stack.setCount(stack.getCount() + amount);
      if (stack.getCount() <= 0) {
         stack.setCount(0);
         return ItemStack.EMPTY;
      }

      if (stack.getCount() > stack.getMaxStackSize()) {
         stack.setCount(stack.getMaxStackSize());
      }

      return stack;
   }

   public static ItemStack decrItemStack(ItemStack stack, Player player) {
      return itemStackAmount(-1, stack, player);
   }

   public static ItemStack incrItemStack(ItemStack stack, Player player) {
      return itemStackAmount(1, stack, player);
   }

   public static boolean areItemsEqual(ItemStack stack1, ItemStack stack2) {
      if (stack1 != null && stack2 != null) {
         return stack1.getItem() == stack2.getItem() ? stack1.getDamageValue() == stack2.getDamageValue() : false;
      } else {
         return false;
      }
   }

   public static void saveInventory(Provider provider, CompoundTag compound, String name, Container inv) {
      ListTag tagList = new ListTag();

      for (int i = 0; i < inv.getContainerSize(); i++) {
         ItemStack item = inv.getItem(i);
         if (!item.isEmpty()) {
            CompoundTag slot = new CompoundTag();
            slot.putInt("Slot", i);
            tagList.add(item.save(provider, slot));
         }
      }

      compound.put(name, tagList);
   }

   public static void saveInventory(Provider provider, CompoundTag compound, String name, NonNullList<ItemStack> inv) {
      ListTag tagList = new ListTag();

      for (int i = 0; i < inv.size(); i++) {
         ItemStack item = (ItemStack)inv.get(i);
         if (!item.isEmpty()) {
            CompoundTag slot = new CompoundTag();
            slot.putInt("Slot", i);
            tagList.add(item.save(provider, slot));
         }
      }

      compound.put(name, tagList);
   }

   public static void saveItemList(Provider provider, CompoundTag compound, String name, NonNullList<ItemStack> list) {
      ListTag itemList = new ListTag();

      for (ItemStack stack : list) {
         if (!stack.isEmpty()) {
            itemList.add(stack.save(provider));
         }
      }

      compound.put(name, itemList);
   }

   public static void readInventory(Provider provider, CompoundTag compound, String name, Container inv) {
      if (compound.contains(name)) {
         ListTag tagList = compound.getList(name, 10);

         for (int i = 0; i < tagList.size(); i++) {
            CompoundTag slot = tagList.getCompound(i);
            int j = slot.getInt("Slot");
            if (j >= 0 && j < inv.getContainerSize()) {
               inv.setItem(j, ItemStack.parseOptional(provider, slot));
            }
         }
      }
   }

   public static void readInventory(Provider provider, CompoundTag compound, String name, NonNullList<ItemStack> inv) {
      if (compound.contains(name)) {
         ListTag tagList = compound.getList(name, 10);

         for (int i = 0; i < tagList.size(); i++) {
            CompoundTag slot = tagList.getCompound(i);
            int j = slot.getInt("Slot");
            if (j >= 0 && j < inv.size()) {
               inv.set(j, ItemStack.parseOptional(provider, slot));
            }
         }
      }
   }

   public static NonNullList<ItemStack> readItemList(Provider provider, CompoundTag compound, String name, boolean includeEmpty) {
      NonNullList<ItemStack> items = NonNullList.create();
      if (!compound.contains(name)) {
         return items;
      }

      ListTag itemList = compound.getList(name, 10);

      for (int i = 0; i < itemList.size(); i++) {
         ItemStack item = ItemStack.parseOptional(provider, itemList.getCompound(i));
         if (!includeEmpty) {
            if (!item.isEmpty()) {
               items.add(item);
            }
         } else {
            items.add(item);
         }
      }

      return items;
   }

   public static NonNullList<ItemStack> readItemList(Provider provider, CompoundTag compound, String name) {
      return readItemList(provider, compound, name, true);
   }

   public static void readItemList(Provider provider, CompoundTag compound, String name, NonNullList<ItemStack> list) {
      if (compound.contains(name)) {
         ListTag itemList = compound.getList(name, 10);

         for (int i = 0; i < itemList.size() && i < list.size(); i++) {
            list.set(i, ItemStack.parseOptional(provider, itemList.getCompound(i)));
         }
      }
   }

   public static void removeStackFromSlot(Container inventory, int index) {
      inventory.setItem(index, ItemStack.EMPTY);
   }

   public static boolean isStackable(ItemStack stack1, ItemStack stack2) {
      return ItemStack.isSameItemSameComponents(stack1, stack2);
   }

   public static CompoundTag writeOverstackedItem(Provider provider, CompoundTag compound, ItemStack stack) {
      stack.save(provider, compound);
      compound.remove("Count");
      compound.putInt("Count", stack.getCount());
      return compound;
   }

   public static ItemStack readOverstackedItem(Provider provider, CompoundTag compound) {
      CompoundTag data = compound.copy();
      int count = data.getInt("Count");
      data.remove("Count");
      data.putByte("Count", (byte)1);
      ItemStack stack = ItemStack.parseOptional(provider, data);
      stack.setCount(count);
      return stack;
   }
}
