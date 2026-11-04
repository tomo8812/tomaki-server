package net.countered.terrainslabs.block.customslabs.specialslabs.dimensions;

import net.countered.terrainslabs.block.customslabs.specialslabs.CustomSlab;
import net.countered.terrainslabs.registries.ModBlocksRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.lighting.LightEngine;

public class NyliumSlab extends CustomSlab implements BonemealableBlock {
   public NyliumSlab(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM)).setValue(WATERLOGGED, false))
            .setValue(GENERATED, false)
      );
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{TYPE, WATERLOGGED, GENERATED});
   }

   private static boolean canBeNylium(BlockState state, LevelReader reader, BlockPos pos) {
      BlockPos blockPos = pos.above();
      BlockState blockState = reader.getBlockState(blockPos);
      int i = LightEngine.getLightBlockInto(
         reader, Blocks.WARPED_NYLIUM.defaultBlockState(), pos, blockState, blockPos, Direction.UP, blockState.getLightBlock(reader, blockPos)
      );
      return i < reader.getMaxLightLevel();
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!canBeNylium(state, level, pos)) {
         if (state.getValue(TYPE) == SlabType.TOP) {
            level.setBlockAndUpdate(pos, (BlockState)((Block)ModBlocksRegistry.NETHERRACK_SLAB.get()).defaultBlockState().setValue(TYPE, SlabType.TOP));
         } else if (state.getValue(TYPE) == SlabType.DOUBLE) {
            level.setBlockAndUpdate(pos, (BlockState)((Block)ModBlocksRegistry.NETHERRACK_SLAB.get()).defaultBlockState().setValue(TYPE, SlabType.DOUBLE));
         } else if (state.getValue(TYPE) == SlabType.BOTTOM) {
            level.setBlockAndUpdate(pos, (BlockState)((Block)ModBlocksRegistry.NETHERRACK_SLAB.get()).defaultBlockState().setValue(TYPE, SlabType.BOTTOM));
         }
      }
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      if (!level.getBlockState(pos.above()).propagatesSkylightDown(level, pos)) {
         return false;
      }

      for (BlockPos blockPos : BlockPos.betweenClosed(pos.offset(-1, -1, -1), pos.offset(1, 1, 1))) {
         if (level.getBlockState(blockPos).is(BlockTags.NYLIUM)) {
            return true;
         }
      }

      return false;
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
   }
}
