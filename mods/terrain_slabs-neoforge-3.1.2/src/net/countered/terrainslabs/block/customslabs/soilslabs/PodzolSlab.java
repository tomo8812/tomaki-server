package net.countered.terrainslabs.block.customslabs.soilslabs;

import net.countered.terrainslabs.block.customslabs.specialslabs.CustomSlab;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class PodzolSlab extends CustomSlab {
   public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

   public PodzolSlab(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM)).setValue(SNOWY, false))
               .setValue(WATERLOGGED, false))
            .setValue(GENERATED, false)
      );
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if (direction == Direction.UP) {
         state = (BlockState)state.setValue(SNOWY, isSnow(neighborState));
      }

      if ((Boolean)state.getValue(WATERLOGGED)) {
         level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
      }

      return state;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos blockPos = context.getClickedPos();
      BlockState blockState = context.getLevel().getBlockState(blockPos);
      if (blockState.is(this)) {
         return (BlockState)((BlockState)blockState.setValue(TYPE, SlabType.DOUBLE)).setValue(WATERLOGGED, false);
      }

      BlockState blockAbove = context.getLevel().getBlockState(blockPos.above());
      BlockState defaultState = (BlockState)this.defaultBlockState().setValue(SNOWY, isSnow(blockAbove));
      FluidState fluidState = context.getLevel().getFluidState(blockPos);
      BlockState blockState2 = (BlockState)((BlockState)defaultState.setValue(TYPE, SlabType.BOTTOM))
         .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
      Direction direction = context.getClickedFace();
      return direction == Direction.DOWN || direction != Direction.UP && context.getClickLocation().y - blockPos.getY() > 0.5
         ? (BlockState)blockState2.setValue(TYPE, SlabType.TOP)
         : blockState2;
   }

   private static boolean isSnow(BlockState state) {
      return state.is(BlockTags.SNOW);
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{SNOWY, TYPE, WATERLOGGED, GENERATED});
   }
}
