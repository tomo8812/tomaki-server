package net.countered.terrainslabs.block.customslabs.specialslabs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class GravityAffectedSlab extends CustomSlab implements Fallable {
   public GravityAffectedSlab(Properties properties) {
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

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      level.scheduleTick(pos, this, this.getDelayAfterPlace());
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (isFree(level.getBlockState(pos.below())) && pos.getY() >= level.getMinBuildHeight()) {
         FallingBlockEntity fallingBlockEntity = FallingBlockEntity.fall(level, pos, state);
         this.falling(fallingBlockEntity);
      }
   }

   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      level.scheduleTick(pos, this, this.getDelayAfterPlace());
      return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
   }

   protected void falling(FallingBlockEntity entity) {
   }

   protected int getDelayAfterPlace() {
      return 2;
   }

   public static boolean isFree(BlockState state) {
      return state.isAir() || state.is(BlockTags.FIRE) || state.liquid() || state.canBeReplaced();
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      if (random.nextInt(16) == 0) {
         BlockPos blockPos = pos.below();
         if (isFree(level.getBlockState(blockPos))) {
            ParticleUtils.spawnParticleBelow(level, pos, random, new BlockParticleOption(ParticleTypes.FALLING_DUST, state));
         }
      }
   }

   public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
      return -16777216;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos blockPos = context.getClickedPos();
      BlockState blockState = context.getLevel().getBlockState(blockPos);
      if (blockState.is(this)) {
         return (BlockState)((BlockState)blockState.setValue(TYPE, SlabType.DOUBLE)).setValue(WATERLOGGED, false);
      }

      FluidState fluidState = context.getLevel().getFluidState(blockPos);
      BlockState blockState2 = (BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM))
         .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
      Direction direction = context.getClickedFace();
      return direction == Direction.DOWN || direction != Direction.UP && context.getClickLocation().y - blockPos.getY() > 0.5
         ? (BlockState)blockState2.setValue(TYPE, SlabType.TOP)
         : blockState2;
   }

   public void onBrokenAfterFall(Level level, BlockPos pos, FallingBlockEntity fallingBlock) {
      if (fallingBlock.getBlockState().getValue(TYPE) == SlabType.DOUBLE) {
         popResource(level, pos, new ItemStack(this.asItem()));
      }
   }

   public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
      if (state.getValue(TYPE) == SlabType.TOP) {
         level.setBlockAndUpdate(pos, (BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM));
      }
   }
}
