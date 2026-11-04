package net.satisfy.candlelight.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;

public class SideTableBlockEntity extends BlockEntity {
   public static final int MAX_ITEMS = 4;
   public static final int MAX_BOOKS = 2;
   private static final String ITEMS_KEY = "Items";
   private static final String LIGHT_KEY = "Light";
   private ItemStack items = ItemStack.EMPTY;
   private ItemStack light = ItemStack.EMPTY;

   public static int maxFor(ItemStack stack) {
      return stack.is(ItemTags.BOOKSHELF_BOOKS) ? 2 : 4;
   }

   public SideTableBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.SIDE_TABLE_BLOCK_ENTITY.get(), pos, state);
   }

   public ItemStack getItems() {
      return this.items;
   }

   public void setItems(ItemStack items) {
      this.items = items;
      this.sync();
   }

   public ItemStack getLight() {
      return this.light;
   }

   public void setLight(ItemStack light) {
      this.light = light;
      this.sync();
   }

   private void sync() {
      this.setChanged();
      if (this.level != null && !this.level.isClientSide()) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.items = ItemStack.parseOptional(provider, tag.getCompound("Items"));
      this.light = ItemStack.parseOptional(provider, tag.getCompound("Light"));
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      tag.put("Items", (Tag)(this.items.isEmpty() ? new CompoundTag() : this.items.save(provider, new CompoundTag())));
      tag.put("Light", (Tag)(this.light.isEmpty() ? new CompoundTag() : this.light.save(provider, new CompoundTag())));
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      return this.saveWithoutMetadata(provider);
   }

   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }
}
