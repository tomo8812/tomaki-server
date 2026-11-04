package net.satisfy.farm_and_charm.core.entity.ai;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.farm_and_charm.core.block.TimberWellBlock;
import net.satisfy.farm_and_charm.core.block.WaterTroughBlock;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import net.satisfy.foundation.registry.FoundationParticles;

public class ApproachWaterTroughGoal extends MoveToBlockGoal {
   protected final Animal animal;
   private int drinkCooldownTicks;

   public ApproachWaterTroughGoal(Animal animal, double speed) {
      super(animal, speed, PlatformHelper.getFeedingTroughRange());
      this.animal = animal;
   }

   public void tick() {
      Level level = this.animal.level();
      if (!level.isClientSide && this.drinkCooldownTicks > 0) {
         this.drinkCooldownTicks--;
      }

      if (!level.isClientSide) {
         BlockState state = level.getBlockState(this.blockPos);
         if (this.drinkCooldownTicks <= 0 && this.isValidTarget(level, this.blockPos)) {
            this.animal
               .getLookControl()
               .setLookAt(this.blockPos.getX() + 0.5, this.blockPos.getY(), this.blockPos.getZ() + 0.5, 10.0F, this.animal.getMaxHeadXRot());
            if (this.isReachedTarget()) {
               boolean drank = state.getBlock() instanceof TimberWellBlock
                  ? this.drinkFromWell(level, state)
                  : this.drainConnected(level, this.blockPos, state);
               if (drank) {
                  this.drinkCooldownTicks = 200 + level.random.nextInt(200);
               } else {
                  this.drinkCooldownTicks = 60 + level.random.nextInt(80);
               }
            }
         }
      }

      super.tick();
   }

   public boolean canUse() {
      return this.drinkCooldownTicks <= 0 && super.canUse();
   }

   public boolean canContinueToUse() {
      return this.drinkCooldownTicks <= 0 && super.canContinueToUse();
   }

   public double acceptedDistance() {
      return 2.25;
   }

   protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
      BlockState state = levelReader.getBlockState(blockPos);
      return state.getBlock() instanceof TimberWellBlock
         ? state.getValue(TimberWellBlock.PART) != TimberWellBlock.TimberWellPart.TOP && TimberWellBlock.hasWater(state)
         : state.getBlock() instanceof WaterTroughBlock && (Integer)state.getValue(WaterTroughBlock.LEVEL) > 0;
   }

   private boolean drinkFromWell(Level level, BlockState state) {
      if (!TimberWellBlock.drink(level, this.blockPos, state)) {
         return false;
      }

      this.applyDrinkEffects(level);
      return true;
   }

   private boolean drainConnected(Level level, BlockPos startPos, BlockState startState) {
      Direction facing = (Direction)startState.getValue(WaterTroughBlock.FACING);
      List<BlockPos> component = this.getComponent(level, startPos, facing);
      boolean anyDrained = false;

      for (BlockPos componentPos : component) {
         BlockState state = level.getBlockState(componentPos);
         if (state.getBlock() instanceof WaterTroughBlock && state.getValue(WaterTroughBlock.FACING) == facing) {
            int currentLevel = (Integer)state.getValue(WaterTroughBlock.LEVEL);
            if (currentLevel > 0 && !this.isInfiniteSource(component, componentPos, facing)) {
               level.setBlock(componentPos, (BlockState)state.setValue(WaterTroughBlock.LEVEL, currentLevel - 1), 3);
               anyDrained = true;
            }
         }
      }

      if (anyDrained) {
         this.applyDrinkEffects(level);
      }

      return anyDrained;
   }

   private void applyDrinkEffects(Level level) {
      int age = this.animal.getAge();
      if (age > 0) {
         this.animal.setAge(Math.max(0, age - 400));
      }

      double x = this.blockPos.getX() + 0.5;
      double y = this.blockPos.getY() + 0.25;
      double z = this.blockPos.getZ() + 0.5;
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles((SimpleParticleType)FoundationParticles.WATER_SPLASH.get(), x, y, z, 8, 0.2, 0.05, 0.2, 0.02);
      }

      level.playSound(null, x, y, z, SoundEvents.GENERIC_SPLASH, SoundSource.NEUTRAL, 0.4F, 0.9F + level.random.nextFloat() * 0.2F);
   }

   private List<BlockPos> getComponent(Level level, BlockPos start, Direction facing) {
      Direction left = facing.getCounterClockWise();
      Direction right = facing.getClockWise();
      HashSet<BlockPos> visited = new HashSet<>();
      ArrayDeque<BlockPos> queue = new ArrayDeque<>();
      ArrayList<BlockPos> result = new ArrayList<>();
      visited.add(start);
      queue.add(start);

      while (!queue.isEmpty()) {
         BlockPos current = queue.poll();
         result.add(current);

         for (Direction step : new Direction[]{left, right}) {
            BlockPos next = current.relative(step);
            if (visited.add(next)) {
               BlockState nextState = level.getBlockState(next);
               if (nextState.getBlock() instanceof WaterTroughBlock && nextState.getValue(WaterTroughBlock.FACING) == facing) {
                  queue.add(next);
               }
            }
         }
      }

      return result;
   }

   private boolean isInfiniteSource(List<BlockPos> component, BlockPos pos, Direction facing) {
      if (component.size() < 3) {
         return false;
      }

      boolean usesX = facing.getAxis() == Axis.Z;
      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;

      for (BlockPos componentPos : component) {
         int value = usesX ? componentPos.getX() : componentPos.getZ();
         if (value < min) {
            min = value;
         }

         if (value > max) {
            max = value;
         }
      }

      int self = usesX ? pos.getX() : pos.getZ();
      return self != min && self != max;
   }
}
