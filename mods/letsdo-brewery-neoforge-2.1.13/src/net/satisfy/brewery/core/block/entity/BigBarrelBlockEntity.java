package net.satisfy.brewery.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.item.DrinkBlockItem;
import net.satisfy.brewery.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BigBarrelBlockEntity extends BlockEntity {
   public static final int SLOTS = 9;
   public static final long TICKS_PER_QUALITY = 168000L;
   private final NonNullList<ItemStack> drinks = NonNullList.withSize(9, ItemStack.EMPTY);
   private final long[] storedAt = new long[9];

   public BigBarrelBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.BIG_BARREL_BLOCK_ENTITY.get(), pos, state);
   }

   public static boolean canAge(ItemStack stack) {
      return stack.getItem() instanceof DrinkBlockItem;
   }

   public ItemStack getDrink(int slot) {
      return (ItemStack)this.drinks.get(slot);
   }

   public boolean isEmpty() {
      return this.drinks.stream().allMatch(ItemStack::isEmpty);
   }

   public boolean insert(ItemStack stack) {
      if (this.level != null && canAge(stack)) {
         long now = this.level.getGameTime();

         for (int slot = 0; slot < 9 && !stack.isEmpty(); slot++) {
            ItemStack stored = (ItemStack)this.drinks.get(slot);
            int room = stored.getMaxStackSize() - stored.getCount();
            if (!stored.isEmpty() && room > 0 && ItemStack.isSameItemSameComponents(stored, stack)) {
               int moved = Math.min(room, stack.getCount());
               this.storedAt[slot] = (this.storedAt[slot] * stored.getCount() + now * moved) / (stored.getCount() + moved);
               stored.grow(moved);
               stack.shrink(moved);
            }
         }

         if (stack.isEmpty()) {
            this.sync();
            return true;
         }

         for (int slot = 0; slot < 9; slot++) {
            if (((ItemStack)this.drinks.get(slot)).isEmpty()) {
               this.drinks.set(slot, stack.split(stack.getCount()));
               this.storedAt[slot] = this.level.getGameTime();
               this.sync();
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public ItemStack takeLast() {
      for (int slot = 8; slot >= 0; slot--) {
         if (!((ItemStack)this.drinks.get(slot)).isEmpty()) {
            ItemStack aged = this.aged(slot);
            this.drinks.set(slot, ItemStack.EMPTY);
            this.storedAt[slot] = 0L;
            this.sync();
            return aged;
         }
      }

      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> takeAll() {
      NonNullList<ItemStack> all = NonNullList.create();

      for (int slot = 0; slot < 9; slot++) {
         if (!((ItemStack)this.drinks.get(slot)).isEmpty()) {
            all.add(this.aged(slot));
            this.drinks.set(slot, ItemStack.EMPTY);
         }
      }

      return all;
   }

   public int getQuality(int slot) {
      return Math.min(4, DrinkBlockItem.getQuality((ItemStack)this.drinks.get(slot)) + (int)(this.elapsed(slot) / 168000L));
   }

   public int getAgingPercent(int slot) {
      return this.getQuality(slot) >= 4 ? 100 : (int)(this.elapsed(slot) % 168000L * 100L / 168000L);
   }

   private long elapsed(int slot) {
      return this.level == null ? 0L : Math.max(0L, this.level.getGameTime() - this.storedAt[slot]);
   }

   private ItemStack aged(int slot) {
      ItemStack stack = ((ItemStack)this.drinks.get(slot)).copy();
      DrinkBlockItem.addQuality(stack, this.getQuality(slot));
      return stack;
   }

   private void sync() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);

      for (int slot = 0; slot < 9; slot++) {
         this.drinks.set(slot, ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(tag, this.drinks, provider);
      long[] times = tag.getLongArray("StoredAt");

      for (int slot = 0; slot < 9; slot++) {
         this.storedAt[slot] = slot < times.length ? times[slot] : 0L;
      }
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      ContainerHelper.saveAllItems(tag, this.drinks, true, provider);
      tag.putLongArray("StoredAt", this.storedAt);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, provider);
      return tag;
   }
}
