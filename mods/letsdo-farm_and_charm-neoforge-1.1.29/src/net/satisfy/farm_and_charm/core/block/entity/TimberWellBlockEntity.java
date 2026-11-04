package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;

public class TimberWellBlockEntity extends BlockEntity {
   public static final int PUMP_EVENT = 1;
   public static final int PUMP_DURATION_TICKS = 12;
   private long pumpStartTick = -12L;

   public TimberWellBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.TIMBER_WELL_BLOCK_ENTITY.get(), pos, state);
   }

   public boolean isPumping(long gameTime) {
      return gameTime - this.pumpStartTick < 12L;
   }

   public long getPumpStartTick() {
      return this.pumpStartTick;
   }

   public boolean triggerEvent(int id, int type) {
      if (id == 1 && this.level != null) {
         this.pumpStartTick = this.level.getGameTime();
         return true;
      } else {
         return super.triggerEvent(id, type);
      }
   }
}
