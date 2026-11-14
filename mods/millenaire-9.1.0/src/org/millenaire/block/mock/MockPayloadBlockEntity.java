package org.millenaire.block.mock;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.millenaire.block.ModBlockEntities;

public class MockPayloadBlockEntity extends BlockEntity {
   private String payload = "";
   @Nullable
   private ItemStack displayStack;

   public MockPayloadBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)ModBlockEntities.MOCK_PAYLOAD.get(), pos, state);
   }

   public String getPayload() {
      return this.payload;
   }

   public void setPayload(String payload) {
      this.payload = payload == null ? "" : payload;
      this.displayStack = null;
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
      }
   }

   public ItemStack getDisplayStack() {
      if (this.displayStack == null) {
         this.displayStack = !this.payload.isEmpty() && this.getBlockState().getBlock() instanceof MockPayloadBlock block
            ? block.displayStack(this.payload)
            : ItemStack.EMPTY;
      }

      return this.displayStack;
   }

   protected void saveAdditional(CompoundTag tag, Provider registries) {
      super.saveAdditional(tag, registries);
      if (!this.payload.isEmpty()) {
         tag.putString("payload", this.payload);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider registries) {
      super.loadAdditional(tag, registries);
      this.payload = tag.getString("payload");
      this.displayStack = null;
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider registries) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, registries);
      return tag;
   }
}
