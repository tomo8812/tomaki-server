package net.caffeinemc.mods.lithium.mixin.block.redstone_wire;

import net.caffeinemc.mods.lithium.common.util.DirectionConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin extends Block {
   private static final int MIN = 0;
   private static final int MAX = 15;
   private static final int MAX_WIRE = 14;

   public RedStoneWireBlockMixin(Properties settings) {
      super(settings);
   }

   @Inject(method = "calculateTargetStrength(Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)I", cancellable = true, at = @At("HEAD"))
   private void getReceivedPowerFaster(Level world, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
      cir.setReturnValue(this.getReceivedPower(world, pos));
   }

   private int getReceivedPower(Level world, BlockPos pos) {
      LevelChunk chunk = world.getChunkAt(pos);
      int power = 0;

      for (Direction dir : DirectionConstants.VERTICAL) {
         BlockPos side = pos.relative(dir);
         BlockState neighbor = chunk.getBlockState(side);
         if (!neighbor.isAir() && !neighbor.is(this)) {
            power = Math.max(power, this.getPowerFromVertical(world, side, neighbor, dir));
            if (power >= 15) {
               return 15;
            }
         }
      }

      BlockPos up = pos.above();
      boolean checkWiresAbove = !chunk.getBlockState(up).isRedstoneConductor(world, up);

      for (Direction dir : DirectionConstants.HORIZONTAL) {
         power = Math.max(power, this.getPowerFromSide(world, pos.relative(dir), dir, checkWiresAbove));
         if (power >= 15) {
            return 15;
         }
      }

      return power;
   }

   private int getPowerFromVertical(Level world, BlockPos pos, BlockState state, Direction toDir) {
      int power = state.getSignal(world, pos, toDir);
      if (power >= 15) {
         return 15;
      } else {
         return state.isRedstoneConductor(world, pos) ? Math.max(power, this.getStrongPowerTo(world, pos, toDir.getOpposite())) : power;
      }
   }

   private int getPowerFromSide(Level world, BlockPos pos, Direction toDir, boolean checkWiresAbove) {
      LevelChunk chunk = world.getChunkAt(pos);
      BlockState state = chunk.getBlockState(pos);
      if (state.is(this)) {
         return (Integer)state.getValue(BlockStateProperties.POWER) - 1;
      }

      int power = state.getSignal(world, pos, toDir);
      if (power >= 15) {
         return 15;
      }

      if (state.isRedstoneConductor(world, pos)) {
         power = Math.max(power, this.getStrongPowerTo(world, pos, toDir.getOpposite()));
         if (power >= 15) {
            return 15;
         }

         if (checkWiresAbove && power < 14) {
            BlockPos up = pos.above();
            BlockState aboveState = chunk.getBlockState(up);
            if (aboveState.is(this)) {
               power = Math.max(power, (Integer)aboveState.getValue(BlockStateProperties.POWER) - 1);
            }
         }
      } else if (power < 14) {
         BlockPos down = pos.below();
         BlockState belowState = chunk.getBlockState(down);
         if (belowState.is(this)) {
            power = Math.max(power, (Integer)belowState.getValue(BlockStateProperties.POWER) - 1);
         }
      }

      return power;
   }

   private int getStrongPowerTo(Level world, BlockPos pos, Direction ignore) {
      int power = 0;

      for (Direction dir : DirectionConstants.ALL) {
         if (dir != ignore) {
            BlockPos side = pos.relative(dir);
            BlockState neighbor = world.getBlockState(side);
            if (!neighbor.isAir() && !neighbor.is(this)) {
               power = Math.max(power, neighbor.getDirectSignal(world, side, dir));
               if (power >= 15) {
                  return 15;
               }
            }
         }
      }

      return power;
   }
}
