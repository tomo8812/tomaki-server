package net.satisfy.bakery.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.util.StoredExperience;

public class BakerStationBlockEntity extends BlockEntity {
   public static final int TOOL_SLOTS = 2;
   private final NonNullList<ItemStack> tools = NonNullList.withSize(2, ItemStack.EMPTY);
   private final StoredExperience experience = new StoredExperience();

   public BakerStationBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.BAKER_STATION_BLOCK_ENTITY.get(), pos, state);
   }

   public ItemStack getTool(int slot) {
      return (ItemStack)this.tools.get(slot);
   }

   public NonNullList<ItemStack> getTools() {
      return this.tools;
   }

   public boolean addTool(int slot, ItemStack stack) {
      if (!((ItemStack)this.tools.get(slot)).isEmpty()) {
         return false;
      }

      this.tools.set(slot, stack.split(1));
      this.sync();
      return true;
   }

   public ItemStack takeTool(int slot) {
      ItemStack tool = (ItemStack)this.tools.get(slot);
      this.tools.set(slot, ItemStack.EMPTY);
      this.sync();
      return tool;
   }

   public void awardExperience(ServerLevel level, float amount, Vec3 pos) {
      this.experience.add(amount);
      this.experience.award(level, pos);
      this.setChanged();
   }

   private void sync() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);

      for (int slot = 0; slot < 2; slot++) {
         this.tools.set(slot, ItemStack.EMPTY);
      }

      ContainerHelper.loadAllItems(tag, this.tools, provider);
      this.experience.load(tag);
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      ContainerHelper.saveAllItems(tag, this.tools, true, provider);
      this.experience.save(tag);
   }

   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = super.getUpdateTag(provider);
      this.saveAdditional(tag, provider);
      return tag;
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
