package net.satisfy.bakery.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Component.Serializer;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;

public class StreetSignBlockEntity extends BlockEntity {
   private final Component[] text = new Component[]{Component.literal(""), Component.literal(""), Component.literal("")};
   private boolean glowing = false;

   public StreetSignBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.STREET_SIGN_BLOCK_ENTITY.get(), pos, state);
   }

   public Component getText(int line) {
      return this.text[line];
   }

   public void setText(int line, Component component) {
      this.text[line] = component;
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public boolean isGlowing() {
      return this.glowing;
   }

   public void setGlowing(boolean glowing) {
      this.glowing = glowing;
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.glowing = compoundTag.getBoolean("Glowing");

      for (int i = 0; i < 3; i++) {
         if (compoundTag.contains("Text" + i)) {
            this.text[i] = Serializer.fromJson(compoundTag.getString("Text" + i), provider);
         }
      }
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      compoundTag.putBoolean("Glowing", this.glowing);

      for (int i = 0; i < 3; i++) {
         compoundTag.putString("Text" + i, Serializer.toJson(this.text[i], provider));
      }
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
