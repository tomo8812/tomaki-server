package net.satisfy.farm_and_charm.core.entity.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.FeedingTroughBlock;
import net.satisfy.farm_and_charm.core.block.entity.FeedingTroughBlockEntity;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public class ApproachFeedingTroughGoal extends MoveToBlockGoal {
   protected final Animal animal;

   public ApproachFeedingTroughGoal(Animal animal, double speed) {
      super(animal, speed, PlatformHelper.getFeedingTroughRange());
      this.animal = animal;
   }

   public void tick() {
      Level world = this.animal.getCommandSenderWorld();
      if (!world.isClientSide() && this.animal.canFallInLove()) {
         BlockState blockState = world.getBlockState(this.blockPos);
         if (blockState.getBlock() instanceof FeedingTroughBlock && (Integer)blockState.getValue(FeedingTroughBlock.SIZE) > 0) {
            this.animal
               .getLookControl()
               .setLookAt(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5, 10.0F, this.animal.getMaxHeadXRot());
            if (this.isReachedTarget()) {
               world.setBlock(
                  this.blockPos, (BlockState)blockState.setValue(FeedingTroughBlock.SIZE, (Integer)blockState.getValue(FeedingTroughBlock.SIZE) - 1), 3
               );
               this.animal.setInLove(null);
               if (world instanceof ServerLevel serverLevel) {
                  BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.HAY_BLOCK.defaultBlockState());
                  double x = this.blockPos.getX() + 0.5;
                  double y = this.blockPos.getY() + 0.35;
                  double z = this.blockPos.getZ() + 0.5;
                  serverLevel.sendParticles(particle, x, y, z, 10, 0.25, 0.12, 0.25, 0.02);
               }

               if (world.getBlockEntity(this.blockPos) instanceof FeedingTroughBlockEntity trough) {
                  trough.onAnimalFed(this.animal);
               }
            }
         }
      }

      super.tick();
   }

   public boolean canUse() {
      return this.animal.canFallInLove() && this.animal.getAge() == 0 && super.canUse();
   }

   public boolean canContinueToUse() {
      return super.canContinueToUse() && this.animal.canFallInLove() && this.animal.getAge() == 0;
   }

   public double acceptedDistance() {
      return 2.25;
   }

   protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
      BlockState blockState = levelReader.getBlockState(blockPos);
      return blockState.getBlock() instanceof FeedingTroughBlock && (Integer)blockState.getValue(FeedingTroughBlock.SIZE) > 0;
   }
}
