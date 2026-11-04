package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.PetBowlBlock;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.util.GeneralUtil;
import net.satisfy.foundation.text.TextEditableBlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PetBowlBlockEntity extends BlockEntity implements WorldlyContainer, TextEditableBlockEntity {
   private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
   private boolean updatingState = false;
   private boolean fedDog;
   private boolean fedCat;
   private Component text = Component.literal("");

   public PetBowlBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.PET_BOWL_BLOCK_ENTITY.get(), pos, state);
   }

   public int @NotNull [] getSlotsForFace(Direction side) {
      return new int[]{0};
   }

   public boolean canPlaceItem(int slot, ItemStack stack) {
      return (stack.is((Item)ObjectRegistry.DOG_FOOD.get()) || stack.is((Item)ObjectRegistry.CAT_FOOD.get())) && this.getItem(slot).getCount() < 4;
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
      this.setChanged();
      return stack;
   }

   @NotNull
   public ItemStack removeItemNoUpdate(int slot) {
      ItemStack stack = ContainerHelper.takeItem(this.items, slot);
      this.setChanged();
      return stack;
   }

   public void setItem(int slot, @NotNull ItemStack stack) {
      if (stack.isEmpty()) {
         this.items.set(slot, ItemStack.EMPTY);
      } else {
         ItemStack current = (ItemStack)this.items.get(slot);
         if (current.isEmpty()) {
            this.items.set(slot, stack.copy());
         } else if (ItemStack.isSameItemSameComponents(current, stack)) {
            int newCount = Math.min(4, current.getCount() + stack.getCount());
            current.setCount(newCount);
            this.items.set(slot, current);
         }
      }

      this.setChanged();
   }

   public boolean stillValid(@NotNull Player player) {
      return true;
   }

   public void clearContent() {
      this.items.clear();
      this.setChanged();
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      ContainerHelper.loadAllItems(tag, this.items, provider);
      this.fedDog = tag.getBoolean("FedDog");
      this.fedCat = tag.getBoolean("FedCat");
      if (tag.contains("Text0")) {
         this.text = Serializer.fromJson(tag.getString("Text0"), provider);
      }
   }

   protected void saveAdditional(@NotNull CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      ContainerHelper.saveAllItems(tag, this.items, provider);
      tag.putBoolean("FedDog", this.fedDog);
      tag.putBoolean("FedCat", this.fedCat);
      tag.putString("Text0", Serializer.toJson(this.text, provider));
   }

   public void setChanged() {
      super.setChanged();
      if (!this.updatingState) {
         this.updatingState = true;
         this.updateBlockState();
         this.updatingState = false;
      }
   }

   private void updateBlockState() {
      if (this.level != null && !this.level.isClientSide) {
         BlockState state = this.level.getBlockState(this.worldPosition);
         if (state.getBlock() instanceof PetBowlBlock) {
            GeneralUtil.FoodType newType = GeneralUtil.FoodType.NONE;
            ItemStack stack = (ItemStack)this.items.get(0);
            if (!stack.isEmpty()) {
               if (stack.is((Item)ObjectRegistry.CAT_FOOD.get())) {
                  newType = GeneralUtil.FoodType.CAT;
               } else if (stack.is((Item)ObjectRegistry.DOG_FOOD.get())) {
                  newType = GeneralUtil.FoodType.DOG;
               }
            }

            if (state.getValue(PetBowlBlock.FOOD_TYPE) != newType) {
               BlockState newState = (BlockState)state.setValue(PetBowlBlock.FOOD_TYPE, newType);
               this.level.setBlock(this.worldPosition, newState, 3);
               this.level.sendBlockUpdated(this.worldPosition, state, newState, 3);
            }
         }
      }
   }

   public void onFed(ItemStack stack) {
      if (stack.is((Item)ObjectRegistry.DOG_FOOD.get())) {
         this.fedDog = true;
      } else if (stack.is((Item)ObjectRegistry.CAT_FOOD.get())) {
         this.fedCat = true;
      }
   }

   public void decreaseFood() {
      ItemStack stack = (ItemStack)this.items.get(0);
      if (!stack.isEmpty()) {
         stack.shrink(1);
         if (stack.isEmpty()) {
            this.items.set(0, ItemStack.EMPTY);
         }

         this.setChanged();
      }
   }

   public boolean wasCatFed() {
      return this.fedCat;
   }

   public void resetFedFlags() {
      this.fedDog = false;
      this.fedCat = false;
      this.setChanged();
   }

   public Component getText() {
      return this.text;
   }

   public void setText(int line, Component text) {
      if (line == 0) {
         this.text = text;
         this.setChanged();
         if (this.level != null && !this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         }
      }
   }

   public int getTextLineCount() {
      return 1;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = super.getUpdateTag(provider);
      this.saveAdditional(tag, provider);
      return tag;
   }

   public boolean canBeUsedBy(Animal animal) {
      return this.text.getString().isBlank() ? true : animal.hasCustomName() && animal.getName().getString().equals(this.text.getString());
   }
}
