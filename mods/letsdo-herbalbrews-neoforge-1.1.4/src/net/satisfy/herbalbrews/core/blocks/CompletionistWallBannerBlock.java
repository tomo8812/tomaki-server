package net.satisfy.herbalbrews.core.blocks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class CompletionistWallBannerBlock extends CompletionistBannerBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(
      ImmutableMap.of(
         Direction.SOUTH,
         box(0.0, 0.0, 14.0, 16.0, 12.5, 16.0),
         Direction.NORTH,
         box(0.0, 0.0, 0.0, 16.0, 12.5, 2.0),
         Direction.EAST,
         box(14.0, 0.0, 0.0, 16.0, 12.5, 16.0),
         Direction.WEST,
         box(0.0, 0.0, 0.0, 2.0, 12.5, 16.0)
      )
   );

   public CompletionistWallBannerBlock(Properties properties) {
      super(properties);
   }

   @Override
   protected void makeDefaultState() {
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   @NotNull
   public String getDescriptionId() {
      return this.asItem().getDescriptionId();
   }

   @Override
   public boolean canSurvive(@NotNull BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
      return levelReader.getBlockState(blockPos.relative((Direction)blockState.getValue(FACING))).isSolid();
   }

   @NotNull
   @Override
   public BlockState updateShape(
      @NotNull BlockState blockState,
      @NotNull Direction direction,
      @NotNull BlockState blockState2,
      @NotNull LevelAccessor levelAccessor,
      @NotNull BlockPos blockPos,
      @NotNull BlockPos blockPos2
   ) {
      return direction == blockState.getValue(FACING) && !blockState.canSurvive(levelAccessor, blockPos)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
   }

   @NotNull
   @Override
   public VoxelShape getShape(
      @NotNull BlockState blockState, @NotNull BlockGetter blockGetter, @NotNull BlockPos blockPos, @NotNull CollisionContext collisionContext
   ) {
      return SHAPES.get(blockState.getValue(FACING));
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockState = this.defaultBlockState();
      Level level = context.getLevel();
      BlockPos blockPos = context.getClickedPos();

      for (Direction direction : context.getNearestLookingDirections()) {
         if (direction.getAxis().isHorizontal()) {
            Direction oppositeDirection = direction.getOpposite();
            BlockState oppositeState = (BlockState)blockState.setValue(FACING, oppositeDirection);
            if (oppositeState.canSurvive(level, blockPos)) {
               return oppositeState;
            }
         }
      }

      return null;
   }

   @NotNull
   @Override
   public BlockState rotate(BlockState blockState, Rotation rotation) {
      return (BlockState)blockState.setValue(FACING, rotation.rotate((Direction)blockState.getValue(FACING)));
   }

   @NotNull
   @Override
   public BlockState mirror(BlockState blockState, Mirror mirror) {
      return blockState.rotate(mirror.getRotation((Direction)blockState.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }
}
