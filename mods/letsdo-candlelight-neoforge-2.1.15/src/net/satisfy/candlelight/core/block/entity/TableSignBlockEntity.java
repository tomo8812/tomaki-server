package net.satisfy.candlelight.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentMap.Builder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BlockEntity.DataComponentInput;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.candlelight.core.registry.DataComponentRegistry;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class TableSignBlockEntity extends BlockEntity {
   private static final String TEXT_KEY = "Text";
   private String text = "";

   public TableSignBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.TABLE_SIGN_BLOCK_ENTITY.get(), pos, state);
   }

   public String getText() {
      return this.text;
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.text = tag.getString("Text");
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      if (!this.text.isEmpty()) {
         tag.putString("Text", this.text);
      }
   }

   protected void applyImplicitComponents(DataComponentInput input) {
      super.applyImplicitComponents(input);
      this.text = (String)input.getOrDefault((DataComponentType)DataComponentRegistry.TABLE_SIGN_TEXT.get(), "");
   }

   protected void collectImplicitComponents(Builder builder) {
      super.collectImplicitComponents(builder);
      if (!this.text.isEmpty()) {
         builder.set((DataComponentType)DataComponentRegistry.TABLE_SIGN_TEXT.get(), this.text);
      }
   }

   public void removeComponentsFromTag(CompoundTag tag) {
      tag.remove("Text");
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      return this.saveWithoutMetadata(provider);
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
