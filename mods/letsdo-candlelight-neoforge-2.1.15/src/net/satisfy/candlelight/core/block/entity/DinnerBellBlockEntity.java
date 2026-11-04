package net.satisfy.candlelight.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;

public class DinnerBellBlockEntity extends BlockEntity {
   public static final int RING_EVENT = 1;
   private long ringTime = Long.MIN_VALUE;

   public DinnerBellBlockEntity(BlockPos blockPos, BlockState blockState) {
      super((BlockEntityType)EntityTypeRegistry.DINNER_BELL_BLOCK_ENTITY.get(), blockPos, blockState);
   }

   public boolean triggerEvent(int id, int param) {
      if (id == 1) {
         if (this.level != null) {
            this.ringTime = this.level.getGameTime();
         }

         return true;
      } else {
         return super.triggerEvent(id, param);
      }
   }

   public float getRingProgress(float partialTicks) {
      return this.level != null && this.ringTime != Long.MIN_VALUE ? (float)(this.level.getGameTime() - this.ringTime) + partialTicks : -1.0F;
   }
}
