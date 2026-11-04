package net.satisfy.farm_and_charm.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.satisfy.farm_and_charm.core.block.ScarecrowBlock;
import net.satisfy.farm_and_charm.core.block.ScarecrowMode;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public class ScarecrowBlockEntity extends BlockEntity {
   private static final double WATCH_RANGE = 16.0;
   private static final double STARTLE_RANGE = 3.0;
   private static final float MAX_HEAD_YAW = 75.0F;
   private long nextGrowthTime;
   private float headYaw;
   private float headYawPrev;
   private float armRaise;
   private float armRaisePrev;

   public ScarecrowBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get(), pos, state);
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.nextGrowthTime = tag.getLong("NextGrowthTime");
   }

   protected void saveAdditional(CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putLong("NextGrowthTime", this.nextGrowthTime);
   }

   public void clientTick(Level level, BlockPos pos, BlockState state) {
      this.headYawPrev = this.headYaw;
      this.armRaisePrev = this.armRaise;
      float targetYaw = 0.0F;
      float targetRaise = 0.0F;
      if (state.getValue(ScarecrowBlock.MODE) == ScarecrowMode.WATCHFUL) {
         double x = pos.getX() + 0.5;
         double y = pos.getY() + 1.5;
         double z = pos.getZ() + 0.5;
         Player player = level.getNearestPlayer(x, y, z, 16.0, EntitySelector.NO_SPECTATORS);
         if (player != null) {
            float yawToPlayer = (float)Math.toDegrees(Math.atan2(-(player.getX() - x), player.getZ() - z));
            targetYaw = Mth.clamp(Mth.wrapDegrees(yawToPlayer - ((Direction)state.getValue(ScarecrowBlock.FACING)).toYRot()), -75.0F, 75.0F);
            if (player.distanceToSqr(x, y, z) < 9.0) {
               targetRaise = 1.0F;
            }
         } else {
            double time = level.getGameTime() + (Mth.murmurHash3Mixer((int)pos.asLong()) & 1023);
            targetYaw = (float)(Math.sin(time * 0.017) * 45.0 + Math.sin(time * 0.043) * 15.0);
         }
      }

      this.headYaw = this.headYaw + (targetYaw - this.headYaw) * 0.08F;
      this.armRaise = this.armRaise + (targetRaise - this.armRaise) * (targetRaise > this.armRaise ? 0.5F : 0.1F);
   }

   public float getHeadYaw(float partialTick) {
      return Mth.lerp(partialTick, this.headYawPrev, this.headYaw);
   }

   public float getArmRaise(float partialTick) {
      return Mth.lerp(partialTick, this.armRaisePrev, this.armRaise);
   }

   public static <T extends BlockEntity> void tick(Level level, T be) {
      if (be instanceof ScarecrowBlockEntity self) {
         if (level instanceof ServerLevel serverLevel) {
            long currentTime = serverLevel.getGameTime();
            if (self.nextGrowthTime == 0L) {
               self.nextGrowthTime = currentTime + PlatformHelper.getScarecrowGrowthInterval() * 20L;
               self.setChanged();
            } else if (currentTime >= self.nextGrowthTime) {
               self.nextGrowthTime = currentTime + PlatformHelper.getScarecrowGrowthInterval() * 20L;
               self.setChanged();
               int range = PlatformHelper.getScarecrowRange();
               BlockPos.betweenClosedStream(self.worldPosition.offset(-range, -1, -range), self.worldPosition.offset(range, 1, range)).forEach(targetPos -> {
                  BlockState targetState = serverLevel.getBlockState(targetPos);
                  if (targetState.getBlock() instanceof CropBlock crop && !crop.isMaxAge(targetState)) {
                     crop.randomTick(targetState, serverLevel, targetPos, serverLevel.random);
                     serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, Context.of(targetState));
                  } else {
                     if (targetState.getBlock() instanceof ClimbingCropBlock climbingCropBlock) {
                        climbingCropBlock.randomTick(targetState, serverLevel, targetPos, serverLevel.random);
                        serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, targetPos, Context.of(targetState));
                     }
                  }
               });
            }
         }
      }
   }
}
