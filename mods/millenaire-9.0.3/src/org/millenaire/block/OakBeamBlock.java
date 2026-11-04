package org.millenaire.block;

import com.mojang.serialization.MapCodec;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class OakBeamBlock extends DirectionalBlock {
   public static final MapCodec<OakBeamBlock> CODEC = simpleCodec(OakBeamBlock::new);
   public static final BooleanProperty CONNECTED = BooleanProperty.create("connected");
   private static final VoxelShape CENTER_SHAPE = Block.box(6.0, 6.0, 6.0, 10.0, 10.0, 10.0);
   private static final Map<Direction, VoxelShape> ARM_SHAPES = new EnumMap<>(Direction.class);
   private static final Map<Direction, VoxelShape> FULL_SHAPES = new EnumMap<>(Direction.class);

   public OakBeamBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.SOUTH)).setValue(CONNECTED, false));
   }

   protected MapCodec<? extends DirectionalBlock> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, CONNECTED});
   }

   private boolean canAttachTo(BlockGetter level, BlockPos pos, Direction facing) {
      BlockPos supportPos = pos.relative(facing);
      return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, facing.getOpposite());
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction facing = context.getClickedFace().getOpposite();
      boolean connected = this.canAttachTo(context.getLevel(), context.getClickedPos(), facing);
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, facing)).setValue(CONNECTED, connected);
   }

   protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      return reorientToBestNeighbor(state, level, pos);
   }

   protected BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)((BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)))).setValue(CONNECTED, false);
   }

   protected BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   private static BlockState reorientToBestNeighbor(BlockState state, LevelAccessor level, BlockPos pos) {
      Direction currentFacing = (Direction)state.getValue(FACING);
      BlockPos currentSupportPos = pos.relative(currentFacing);
      if (level.getBlockState(currentSupportPos).isFaceSturdy(level, currentSupportPos, currentFacing.getOpposite())) {
         return (BlockState)state.setValue(CONNECTED, true);
      }

      for (Direction dir : Direction.values()) {
         if (dir != currentFacing) {
            BlockPos supportPos = pos.relative(dir);
            if (level.getBlockState(supportPos).isFaceSturdy(level, supportPos, dir.getOpposite())) {
               return (BlockState)((BlockState)state.setValue(FACING, dir)).setValue(CONNECTED, true);
            }
         }
      }

      return (BlockState)state.setValue(CONNECTED, false);
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return state.getValue(CONNECTED) ? FULL_SHAPES.get(state.getValue(FACING)) : CENTER_SHAPE;
   }

   protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
      return false;
   }

   static {
      ARM_SHAPES.put(Direction.NORTH, Block.box(6.0, 6.0, 0.0, 10.0, 10.0, 6.0));
      ARM_SHAPES.put(Direction.SOUTH, Block.box(6.0, 6.0, 10.0, 10.0, 10.0, 16.0));
      ARM_SHAPES.put(Direction.WEST, Block.box(0.0, 6.0, 6.0, 6.0, 10.0, 10.0));
      ARM_SHAPES.put(Direction.EAST, Block.box(10.0, 6.0, 6.0, 16.0, 10.0, 10.0));
      ARM_SHAPES.put(Direction.UP, Block.box(6.0, 10.0, 6.0, 10.0, 16.0, 10.0));
      ARM_SHAPES.put(Direction.DOWN, Block.box(6.0, 0.0, 6.0, 10.0, 6.0, 10.0));

      for (Direction dir : Direction.values()) {
         FULL_SHAPES.put(dir, Shapes.or(CENTER_SHAPE, ARM_SHAPES.get(dir)));
      }
   }
}
