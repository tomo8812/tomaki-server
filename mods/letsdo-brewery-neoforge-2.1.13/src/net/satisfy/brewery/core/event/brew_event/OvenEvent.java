package net.satisfy.brewery.core.event.brew_event;

import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.brewery.core.block.property.Heat;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.core.registry.ObjectRegistry;

public class OvenEvent extends BrewEvent {
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
         BlockPos ovenPos = BrewHelper.getBlock((Block)ObjectRegistry.BREW_OVEN.get(), components, level);
         if (ovenPos != null) {
            BlockState ovenState = level.getBlockState(ovenPos);
            level.setBlock(ovenPos, (BlockState)ovenState.setValue(BlockStateRegistry.HEAT, Heat.WEAK), 3);
         }
      }
   }

   @Override
   public boolean isFinish(Set<BlockPos> components, Level level) {
      if (components != null && level != null) {
         BlockPos ovenPos = BrewHelper.getBlock((Block)ObjectRegistry.BREW_OVEN.get(), components, level);
         if (ovenPos != null) {
            BlockState ovenState = level.getBlockState(ovenPos);
            return ovenState.getValue(BlockStateRegistry.HEAT) != Heat.WEAK;
         } else {
            return true;
         }
      } else {
         return true;
      }
   }
}
