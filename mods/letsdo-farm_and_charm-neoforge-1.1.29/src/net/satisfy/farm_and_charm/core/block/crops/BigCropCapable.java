package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;

public interface BigCropCapable {
   int getMaxAge();

   IntegerProperty getAgeProperty();

   BooleanProperty getBigProperty();

   BooleanProperty getGiantProperty();

   default void tryTransformToBigCrop(Level level, BlockPos pos, BlockState state, boolean usedCompost) {
      if (level instanceof ServerLevel serverLevel) {
         IntegerProperty age = this.getAgeProperty();
         BooleanProperty big = this.getBigProperty();
         BooleanProperty giant = this.getGiantProperty();
         if (state.hasProperty(age) && state.hasProperty(big) && state.hasProperty(giant)) {
            int currentAge = (Integer)state.getValue(age);
            boolean isBig = (Boolean)state.getValue(big);
            boolean isGiant = (Boolean)state.getValue(giant);
            if (currentAge == this.getMaxAge()) {
               boolean hasWater = this.hasWaterSourceNearby(level, pos);
               boolean isRaining = level.isRainingAt(pos.above());
               int lightLevel = level.getMaxLocalRawBrightness(pos.above());
               RandomSource random = level.getRandom();
               if (usedCompost) {
                  if (!hasWater) {
                     return;
                  }

                  if (!isBig && random.nextFloat() < 0.144F) {
                     level.setBlock(pos, (BlockState)state.setValue(big, true), 2);
                     this.spawnHappyParticles(serverLevel, pos);
                  } else if (isBig && !isGiant && random.nextFloat() < 0.1F) {
                     level.setBlock(pos, (BlockState)state.setValue(giant, true), 2);
                     this.spawnHappyParticles(serverLevel, pos);
                  }
               } else {
                  long seed = pos.asLong();
                  RandomSource staticRandom = RandomSource.create(seed);
                  if (staticRandom.nextFloat() >= 0.25F) {
                     return;
                  }

                  if (!isBig && isRaining && lightLevel > 12 && random.nextInt(100) < PlatformHelper.getBigCropChance()) {
                     level.setBlock(pos, (BlockState)state.setValue(big, true), 2);
                     this.spawnHappyParticles(serverLevel, pos);
                  } else if (!isBig && hasWater && random.nextFloat() < 0.144F) {
                     level.setBlock(pos, (BlockState)state.setValue(big, true), 2);
                     this.spawnHappyParticles(serverLevel, pos);
                  }
               }
            }
         }
      }
   }

   private boolean hasWaterSourceNearby(Level level, BlockPos pos) {
      for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-4, 0, -4), pos.offset(4, 1, 4))) {
         if (level.getFluidState(checkPos).is(FluidTags.WATER)) {
            return true;
         }
      }

      for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-2, 0, -2), pos.offset(2, 2, 2))) {
         if (level.getBlockState(checkPos).is((Block)ObjectRegistry.WATER_SPRINKLER.get())) {
            return true;
         }
      }

      return false;
   }

   private void spawnHappyParticles(ServerLevel level, BlockPos pos) {
      double x = pos.getX() + 0.5;
      double y = pos.getY() + 0.75;
      double z = pos.getZ() + 0.5;
      level.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 8, 0.25, 0.25, 0.25, 0.0);
   }
}
