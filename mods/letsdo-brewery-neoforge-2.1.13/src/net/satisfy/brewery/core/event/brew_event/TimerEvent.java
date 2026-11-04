package net.satisfy.brewery.core.event.brew_event;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.core.registry.ObjectRegistry;

public class TimerEvent extends BrewEvent {
   @Override
   public CompoundTag save(CompoundTag compoundTag) {
      return compoundTag;
   }

   @Override
   public void load(CompoundTag compoundTag) {
   }

   @Override
   public void start(Set<BlockPos> components, Level level) {
      if (components != null && level != null) {
         BlockPos timerPos = BrewHelper.getBlock((Block)ObjectRegistry.BREW_TIMER.get(), components, level);
         if (timerPos != null) {
            BlockState timerState = level.getBlockState(timerPos);
            level.setBlock(timerPos, (BlockState)timerState.setValue(BlockStateRegistry.TIME, true), 3);
         }
      }
   }

   @Override
   public boolean isFinish(Set<BlockPos> components, Level level) {
      if (components != null && level != null) {
         BlockPos timerPos = BrewHelper.getBlock((Block)ObjectRegistry.BREW_TIMER.get(), components, level);
         if (timerPos != null) {
            BlockState timerState = level.getBlockState(timerPos);
            return !(Boolean)timerState.getValue(BlockStateRegistry.TIME);
         } else {
            return true;
         }
      } else {
         return true;
      }
   }
}
