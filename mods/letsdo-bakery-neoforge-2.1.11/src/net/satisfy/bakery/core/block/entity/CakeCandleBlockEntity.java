package net.satisfy.bakery.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.bakery.core.block.PieBlock;
import net.satisfy.bakery.core.registry.EntityTypeRegistry;

public class CakeCandleBlockEntity extends BlockEntity {
   private Item candle = Items.CANDLE;
   private int pieces = -1;
   private long wobbleStart = -4611686018427387904L;

   public CakeCandleBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.CAKE_CANDLE_BLOCK_ENTITY.get(), pos, state);
   }

   public Item getCandle() {
      return this.candle;
   }

   public Block getCandleBlock() {
      Block block = Block.byItem(this.candle);
      return block == Blocks.AIR ? Blocks.CANDLE : block;
   }

   public void setCandle(Item candle) {
      this.candle = candle;
      this.sync();
   }

   public int getPieces() {
      return this.pieces < 0 ? PieBlock.piecesFromCuts(this.getBlockState()) : this.pieces;
   }

   public void setPieces(int pieces) {
      this.pieces = pieces;
      this.sync();
   }

   public void setBlockState(BlockState state) {
      BlockState previous = this.getBlockState();
      super.setBlockState(state);
      if (this.level != null
         && this.level.isClientSide()
         && previous.hasProperty(PieBlock.CUTS)
         && state.hasProperty(PieBlock.CUTS)
         && !((Integer)previous.getValue(PieBlock.CUTS)).equals(state.getValue(PieBlock.CUTS))) {
         this.wobbleStart = this.level.getGameTime();
      }
   }

   public long getWobbleStart() {
      return this.wobbleStart;
   }

   private void sync() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      ResourceLocation id = ResourceLocation.tryParse(tag.getString("Candle"));
      this.candle = id != null && BuiltInRegistries.ITEM.containsKey(id) ? (Item)BuiltInRegistries.ITEM.get(id) : Items.CANDLE;
      this.pieces = tag.contains("Pieces") ? tag.getInt("Pieces") : -1;
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putString("Candle", BuiltInRegistries.ITEM.getKey(this.candle).toString());
      if (this.pieces >= 0) {
         tag.putInt("Pieces", this.pieces);
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
