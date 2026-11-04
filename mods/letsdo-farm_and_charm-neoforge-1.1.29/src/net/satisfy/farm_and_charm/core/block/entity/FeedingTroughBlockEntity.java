package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FeedingTroughBlock;
import net.satisfy.farm_and_charm.core.network.PacketHandler;
import net.satisfy.farm_and_charm.core.network.packet.SyncSaturationPacket;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.util.SaturationTracker;
import org.jetbrains.annotations.NotNull;

public class FeedingTroughBlockEntity extends BlockEntity implements WorldlyContainer {
   private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

   public FeedingTroughBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.FEEDING_TROUGH_BLOCK_ENTITY.get(), pos, state);
   }

   public boolean addOne(Item item) {
      if (!new ItemStack(item).is(ItemTags.VILLAGER_PLANTABLE_SEEDS)) {
         return false;
      } else {
         ItemStack slot = (ItemStack)this.items.get(0);
         if (slot.isEmpty()) {
            this.items.set(0, new ItemStack(item, 1));
            this.pushSizeToState();
            return true;
         } else if (slot.is(item) && slot.getCount() < 4) {
            slot.grow(1);
            this.items.set(0, slot);
            this.pushSizeToState();
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean consumeOne() {
      ItemStack slot = (ItemStack)this.items.get(0);
      if (slot.isEmpty()) {
         return false;
      }

      slot.shrink(1);
      if (slot.getCount() <= 0) {
         this.items.set(0, ItemStack.EMPTY);
      }

      this.pushSizeToState();
      return true;
   }

   private void pushSizeToState() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         BlockState state = this.level.getBlockState(this.worldPosition);
         if (state.getBlock() instanceof FeedingTroughBlock) {
            int size = ((ItemStack)this.items.get(0)).isEmpty() ? 0 : ((ItemStack)this.items.get(0)).getCount();
            if ((Integer)state.getValue(FeedingTroughBlock.SIZE) != size) {
               this.level.setBlockAndUpdate(this.worldPosition, (BlockState)state.setValue(FeedingTroughBlock.SIZE, size));
            } else {
               this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
            }
         }
      }
   }

   public int @NotNull [] getSlotsForFace(Direction side) {
      return new int[]{0};
   }

   public boolean canPlaceItem(int slot, ItemStack stack) {
      ItemStack cur = this.getItem(slot);
      return stack.is(ItemTags.VILLAGER_PLANTABLE_SEEDS) && (cur.isEmpty() || cur.is(stack.getItem())) && cur.getCount() < 4;
   }

   public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
      return this.canPlaceItem(slot, stack);
   }

   public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
      return false;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      return ((ItemStack)this.items.get(0)).isEmpty();
   }

   @NotNull
   public ItemStack getItem(int slot) {
      return (ItemStack)this.items.get(slot);
   }

   @NotNull
   public ItemStack removeItem(int slot, int amount) {
      ItemStack stack = ContainerHelper.removeItem(this.items, slot, amount);
      if (((ItemStack)this.items.get(0)).getCount() <= 0) {
         this.items.set(0, ItemStack.EMPTY);
      }

      this.pushSizeToState();
      return stack;
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int slot) {
      ItemStack stack = ContainerHelper.takeItem(this.items, slot);
      if (((ItemStack)this.items.get(0)).getCount() <= 0) {
         this.items.set(0, ItemStack.EMPTY);
      }

      this.pushSizeToState();
      return stack;
   }

   public void setItem(int slot, @NotNull ItemStack stack) {
      if (stack.isEmpty()) {
         this.items.set(slot, ItemStack.EMPTY);
      } else {
         int capped = Math.min(4, stack.getCount());
         ItemStack copy = stack.copy();
         copy.setCount(capped);
         this.items.set(slot, copy);
      }

      if (((ItemStack)this.items.get(0)).getCount() <= 0) {
         this.items.set(0, ItemStack.EMPTY);
      }

      this.pushSizeToState();
   }

   public int getMaxStackSize() {
      return 4;
   }

   public boolean stillValid(@NotNull Player player) {
      return true;
   }

   public void clearContent() {
      this.items.set(0, ItemStack.EMPTY);
      this.pushSizeToState();
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      ContainerHelper.loadAllItems(compoundTag, this.items, provider);
      if (((ItemStack)this.items.get(0)).getCount() <= 0) {
         this.items.set(0, ItemStack.EMPTY);
      }

      this.pushSizeToState();
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      ContainerHelper.saveAllItems(compoundTag, this.items, provider);
   }

   public void setChanged() {
      super.setChanged();
   }

   public void onAnimalFed(Animal animal) {
      if (animal instanceof SaturationTracker.SaturatedAnimal saturated) {
         SaturationTracker tracker = saturated.farm_and_charm$getSaturationTracker();
         tracker.feedDirectly(animal, 5);
         if (!animal.level().isClientSide) {
            PacketHandler.sendSaturationSync(new SyncSaturationPacket(animal.getId(), tracker.level(), tracker.foodCounter()), animal);
         }

         this.consumeOne();
      }
   }
}
