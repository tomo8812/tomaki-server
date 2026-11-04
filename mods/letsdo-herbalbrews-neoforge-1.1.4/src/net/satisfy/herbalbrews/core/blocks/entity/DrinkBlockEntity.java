package net.satisfy.herbalbrews.core.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;

public class DrinkBlockEntity extends BlockEntity implements Clearable {
   private CompoundTag storedNbt;

   public DrinkBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.DRINK_BLOCK_ENTITY.get(), pos, state);
   }

   public void setStoredNbt(CompoundTag tag) {
      this.storedNbt = tag;
   }

   public CompoundTag getStoredNbt() {
      return this.storedNbt;
   }

   public void clearContent() {
      this.storedNbt = null;
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      if (compoundTag.contains("StoredNbt")) {
         this.storedNbt = compoundTag.getCompound("StoredNbt");
      }
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      if (this.storedNbt != null) {
         compoundTag.put("StoredNbt", this.storedNbt);
      }
   }
}
