package net.satisfy.brewery.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.registry.EntityTypeRegistry;
import net.satisfy.foundation.util.LibUtil;

public class BeerMugBlockEntity extends BlockEntity {
   public static final String FLOWER_KEY = "flower";
   private Item flower;

   public BeerMugBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.BEER_MUG_BLOCK_ENTITY.get(), pos, state);
   }

   public Item getFlower() {
      return this.flower;
   }

   public void setFlower(Item flower) {
      this.flower = flower;
      this.setChanged();
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      this.writeFlower(compoundTag, this.flower, provider);
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.flower = this.readFlower(compoundTag, provider);
   }

   public void writeFlower(CompoundTag nbt, Item flower, Provider provider) {
      CompoundTag nbtCompound = new CompoundTag();
      if (flower != null) {
         flower.getDefaultInstance().save(provider, nbtCompound);
      }

      nbt.put("flower", nbtCompound);
   }

   public Item readFlower(CompoundTag nbt, Provider provider) {
      if (nbt.contains("flower")) {
         CompoundTag nbtCompound = nbt.getCompound("flower");
         if (!nbtCompound.isEmpty()) {
            return ItemStack.parseOptional(provider, nbtCompound).getItem();
         }
      }

      return null;
   }

   public ClientboundBlockEntityDataPacket getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   public CompoundTag getUpdateTag(Provider provider) {
      return this.saveWithoutMetadata(provider);
   }

   public void setChanged() {
      if (this.level instanceof ServerLevel serverLevel) {
         Packet<ClientGamePacketListener> updatePacket = this.getUpdatePacket();

         for (ServerPlayer player : LibUtil.tracking(serverLevel, this.getBlockPos())) {
            player.connection.send(updatePacket);
         }
      }

      super.setChanged();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Packet getUpdatePacket() {
      return this.getUpdatePacket();
   }
}
