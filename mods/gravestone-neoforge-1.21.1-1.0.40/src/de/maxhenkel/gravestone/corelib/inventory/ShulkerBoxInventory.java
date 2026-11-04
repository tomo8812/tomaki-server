package de.maxhenkel.gravestone.corelib.inventory;

import de.maxhenkel.gravestone.corelib.sound.SoundUtils;
import javax.annotation.Nullable;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.SeededContainerLoot;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootParams.Builder;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public abstract class ShulkerBoxInventory implements Container, MenuProvider {
   protected NonNullList<ItemStack> items;
   protected ItemStack shulkerBox;
   protected int invSize;

   public ShulkerBoxInventory(Player player, ItemStack shulkerBox, int invSize) {
      this.shulkerBox = shulkerBox;
      this.invSize = invSize;
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      this.startOpen(player);
      ItemContainerContents contents = (ItemContainerContents)shulkerBox.get(DataComponents.CONTAINER);
      if (contents != null) {
         contents.copyInto(this.items);
      }

      SeededContainerLoot loot = (SeededContainerLoot)shulkerBox.get(DataComponents.CONTAINER_LOOT);
      if (loot != null) {
         this.fillWithLoot(player, loot);
         shulkerBox.remove(DataComponents.CONTAINER_LOOT);
      }
   }

   public ShulkerBoxInventory(Player player, ItemStack shulkerBox) {
      this(player, shulkerBox, 27);
   }

   public void fillWithLoot(@Nullable Player player, SeededContainerLoot loot) {
      if (player != null) {
         LootTable loottable = player.getServer().reloadableRegistries().getLootTable(loot.lootTable());
         Builder builder = new Builder((ServerLevel)player.level());
         builder.withLuck(player.getLuck()).withParameter(LootContextParams.THIS_ENTITY, player);
         loottable.fill(this, builder.create(LootContextParamSets.CHEST), loot.seed());
         this.setChanged();
      }
   }

   public int getContainerSize() {
      return this.invSize;
   }

   public ItemStack getItem(int index) {
      return (ItemStack)this.items.get(index);
   }

   public ItemStack removeItem(int index, int count) {
      ItemStack itemstack = ContainerHelper.removeItem(this.items, index, count);
      this.setChanged();
      return itemstack;
   }

   public ItemStack removeItemNoUpdate(int index) {
      ItemStack stack = ContainerHelper.takeItem(this.items, index);
      this.setChanged();
      return stack;
   }

   public void setItem(int index, ItemStack stack) {
      this.items.set(index, stack);
      this.setChanged();
   }

   public int getMaxStackSize() {
      return 64;
   }

   public void setChanged() {
      this.shulkerBox.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(this.items));
   }

   public void startOpen(Player player) {
      player.level()
         .playSound(
            null, player.getX(), player.getY(), player.getZ(), this.getOpenSound(), SoundSource.BLOCKS, 0.5F, SoundUtils.getVariatedPitch(player.level())
         );
   }

   public void stopOpen(Player player) {
      this.setChanged();
      player.level()
         .playSound(
            null, player.getX(), player.getY(), player.getZ(), this.getCloseSound(), SoundSource.BLOCKS, 0.5F, player.level().random.nextFloat() * 0.1F + 0.9F
         );
   }

   protected SoundEvent getOpenSound() {
      return SoundEvents.SHULKER_BOX_OPEN;
   }

   protected SoundEvent getCloseSound() {
      return SoundEvents.SHULKER_BOX_CLOSE;
   }

   public void clearContent() {
      this.items.clear();
      this.setChanged();
   }

   public boolean isEmpty() {
      return this.items.isEmpty();
   }

   public boolean stillValid(Player player) {
      for (InteractionHand hand : InteractionHand.values()) {
         if (player.getItemInHand(hand).equals(this.shulkerBox)) {
            return true;
         }
      }

      return false;
   }

   public Component getDisplayName() {
      return this.shulkerBox.getHoverName();
   }

   @Nullable
   public abstract AbstractContainerMenu createMenu(int var1, Inventory var2, Player var3);
}
