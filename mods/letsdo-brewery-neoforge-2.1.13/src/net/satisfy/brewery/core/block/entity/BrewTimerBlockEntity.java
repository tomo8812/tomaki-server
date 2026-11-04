package net.satisfy.brewery.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrewTimerBlockEntity extends BlockEntity {
   private long pressTime = -100L;
   private float needleAngle = -60.0F;
   @Nullable
   private BlockPos stationPos;
   private long nextStationSearch;

   public BrewTimerBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.BREW_TIMER_BLOCK_ENTITY.get(), pos, state);
   }

   public void press() {
      if (this.level != null) {
         this.pressTime = this.level.getGameTime();
         this.setChanged();
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public long getPressTime() {
      return this.pressTime;
   }

   public float updateNeedle(float target, float speed) {
      this.needleAngle = this.needleAngle + (target - this.needleAngle) * speed;
      return this.needleAngle;
   }

   @Nullable
   public BrewstationBlockEntity findStation() {
      if (this.level == null) {
         return null;
      }

      if (this.stationPos != null
         && this.level.getBlockEntity(this.stationPos) instanceof BrewstationBlockEntity station
         && station.isPartOf(this.worldPosition)) {
         return station;
      }

      this.stationPos = null;
      if (this.level.getGameTime() < this.nextStationSearch) {
         return null;
      }

      this.nextStationSearch = this.level.getGameTime() + 40L;

      for (BlockPos pos : BlockPos.betweenClosed(this.worldPosition.offset(-2, -2, -2), this.worldPosition.offset(2, 2, 2))) {
         if (this.level.getBlockEntity(pos) instanceof BrewstationBlockEntity station && station.isPartOf(this.worldPosition)) {
            this.stationPos = pos.immutable();
            return station;
         }
      }

      return null;
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.pressTime = tag.contains("PressTime") ? tag.getLong("PressTime") : -100L;
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putLong("PressTime", this.pressTime);
   }

   @Nullable
   public Packet<ClientGamePacketListener> getUpdatePacket() {
      return ClientboundBlockEntityDataPacket.create(this);
   }

   @NotNull
   public CompoundTag getUpdateTag(Provider provider) {
      CompoundTag tag = new CompoundTag();
      this.saveAdditional(tag, provider);
      return tag;
   }
}
