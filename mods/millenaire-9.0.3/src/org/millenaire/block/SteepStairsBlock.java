package org.millenaire.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SteepStairsBlock extends StairBlock {
   public static final EnumProperty<Half> HALF_SHAPE = EnumProperty.create("half_shape", Half.class);
   private static final VoxelShape SLAB_BOTTOM = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   private static final VoxelShape SLAB_TOP = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape QTR_BOT_WEST = Block.box(0.0, 0.0, 0.0, 8.0, 8.0, 16.0);
   private static final VoxelShape QTR_BOT_EAST = Block.box(8.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   private static final VoxelShape QTR_BOT_NORTH = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 8.0);
   private static final VoxelShape QTR_BOT_SOUTH = Block.box(0.0, 0.0, 8.0, 16.0, 8.0, 16.0);
   private static final VoxelShape QTR_TOP_WEST = Block.box(0.0, 8.0, 0.0, 8.0, 16.0, 16.0);
   private static final VoxelShape QTR_TOP_EAST = Block.box(8.0, 8.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape QTR_TOP_NORTH = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 8.0);
   private static final VoxelShape QTR_TOP_SOUTH = Block.box(0.0, 8.0, 8.0, 16.0, 16.0, 16.0);
   private static final VoxelShape OCT_BOT_NW = Block.box(0.0, 0.0, 0.0, 8.0, 8.0, 8.0);
   private static final VoxelShape OCT_BOT_NE = Block.box(8.0, 0.0, 0.0, 16.0, 8.0, 8.0);
   private static final VoxelShape OCT_BOT_SW = Block.box(0.0, 0.0, 8.0, 8.0, 8.0, 16.0);
   private static final VoxelShape OCT_BOT_SE = Block.box(8.0, 0.0, 8.0, 16.0, 8.0, 16.0);
   private static final VoxelShape OCT_TOP_NW = Block.box(0.0, 8.0, 0.0, 8.0, 16.0, 8.0);
   private static final VoxelShape OCT_TOP_NE = Block.box(8.0, 8.0, 0.0, 16.0, 16.0, 8.0);
   private static final VoxelShape OCT_TOP_SW = Block.box(0.0, 8.0, 8.0, 8.0, 16.0, 16.0);
   private static final VoxelShape OCT_TOP_SE = Block.box(8.0, 8.0, 8.0, 16.0, 16.0, 16.0);
   private static final VoxelShape QTR_TOP_WEST_FAT = Block.box(0.0, 8.0, 0.0, 12.0, 16.0, 16.0);
   private static final VoxelShape QTR_TOP_WEST_THIN = Block.box(0.0, 8.0, 0.0, 4.0, 16.0, 16.0);
   private static final VoxelShape QTR_TOP_EAST_FAT = Block.box(12.0, 8.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape QTR_TOP_EAST_THIN = Block.box(4.0, 8.0, 0.0, 16.0, 16.0, 16.0);
   private static final VoxelShape QTR_TOP_NORTH_FAT = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 12.0);
   private static final VoxelShape QTR_TOP_NORTH_THIN = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 4.0);
   private static final VoxelShape QTR_TOP_SOUTH_FAT = Block.box(0.0, 8.0, 12.0, 16.0, 16.0, 16.0);
   private static final VoxelShape QTR_TOP_SOUTH_THIN = Block.box(0.0, 8.0, 4.0, 16.0, 16.0, 16.0);
   private static final VoxelShape OCT_TOP_NW_FAT = Block.box(0.0, 8.0, 0.0, 12.0, 16.0, 12.0);
   private static final VoxelShape OCT_TOP_NW_THIN = Block.box(0.0, 8.0, 0.0, 4.0, 16.0, 4.0);
   private static final VoxelShape OCT_TOP_NE_FAT = Block.box(4.0, 8.0, 0.0, 16.0, 16.0, 12.0);
   private static final VoxelShape OCT_TOP_NE_THIN = Block.box(12.0, 8.0, 0.0, 16.0, 16.0, 4.0);
   private static final VoxelShape OCT_TOP_SW_FAT = Block.box(0.0, 8.0, 4.0, 12.0, 16.0, 16.0);
   private static final VoxelShape OCT_TOP_SW_THIN = Block.box(0.0, 8.0, 12.0, 4.0, 16.0, 16.0);
   private static final VoxelShape OCT_TOP_SE_FAT = Block.box(12.0, 8.0, 12.0, 16.0, 16.0, 16.0);
   private static final VoxelShape OCT_TOP_SE_THIN = Block.box(4.0, 8.0, 4.0, 16.0, 16.0, 16.0);
   private static final VoxelShape QTR_BOT_WEST_FAT = Block.box(0.0, 0.0, 0.0, 12.0, 8.0, 16.0);
   private static final VoxelShape QTR_BOT_WEST_THIN = Block.box(0.0, 0.0, 0.0, 4.0, 8.0, 16.0);
   private static final VoxelShape QTR_BOT_EAST_FAT = Block.box(12.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   private static final VoxelShape QTR_BOT_EAST_THIN = Block.box(4.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   private static final VoxelShape QTR_BOT_NORTH_FAT = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 12.0);
   private static final VoxelShape QTR_BOT_NORTH_THIN = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 4.0);
   private static final VoxelShape QTR_BOT_SOUTH_FAT = Block.box(0.0, 0.0, 12.0, 16.0, 8.0, 16.0);
   private static final VoxelShape QTR_BOT_SOUTH_THIN = Block.box(0.0, 0.0, 4.0, 16.0, 8.0, 16.0);
   private static final VoxelShape OCT_BOT_NW_FAT = Block.box(0.0, 0.0, 0.0, 12.0, 8.0, 12.0);
   private static final VoxelShape OCT_BOT_NW_THIN = Block.box(0.0, 0.0, 0.0, 4.0, 8.0, 4.0);
   private static final VoxelShape OCT_BOT_NE_FAT = Block.box(4.0, 0.0, 0.0, 16.0, 8.0, 12.0);
   private static final VoxelShape OCT_BOT_NE_THIN = Block.box(12.0, 0.0, 0.0, 16.0, 8.0, 4.0);
   private static final VoxelShape OCT_BOT_SW_FAT = Block.box(0.0, 0.0, 4.0, 12.0, 8.0, 16.0);
   private static final VoxelShape OCT_BOT_SW_THIN = Block.box(0.0, 0.0, 12.0, 4.0, 8.0, 16.0);
   private static final VoxelShape OCT_BOT_SE_FAT = Block.box(12.0, 0.0, 12.0, 16.0, 8.0, 16.0);
   private static final VoxelShape OCT_BOT_SE_THIN = Block.box(4.0, 0.0, 4.0, 16.0, 8.0, 16.0);

   public SteepStairsBlock(BlockState baseState, Properties properties) {
      super(baseState, properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
                     .setValue(HALF, Half.BOTTOM))
                  .setValue(SHAPE, StairsShape.STRAIGHT))
               .setValue(WATERLOGGED, false))
            .setValue(HALF_SHAPE, Half.BOTTOM)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{HALF_SHAPE});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      if (state == null) {
         return null;
      }

      Level level = context.getLevel();
      BlockPos pos = context.getClickedPos();
      Direction facing = context.getClickedFace();
      boolean orientBottom = state.getValue(HALF) == Half.BOTTOM;
      BlockState nextTo = level.getBlockState(pos.relative(facing.getOpposite()));
      boolean nextToThin = nextTo.getBlock() == this && nextTo.getValue(HALF_SHAPE) == Half.TOP;
      nextTo = level.getBlockState(pos.below());
      nextToThin |= nextTo.getBlock() == this && nextTo.getValue(HALF_SHAPE) == Half.BOTTOM && nextTo.getValue(HALF) == Half.BOTTOM && orientBottom;
      nextTo = level.getBlockState(pos.above());
      nextToThin |= nextTo.getBlock() == this && nextTo.getValue(HALF_SHAPE) == Half.BOTTOM && nextTo.getValue(HALF) == Half.TOP && !orientBottom;
      Player placer = context.getPlayer();
      boolean sneaking = placer != null && placer.isShiftKeyDown();
      if (sneaking || nextToThin) {
         state = (BlockState)state.setValue(HALF_SHAPE, Half.TOP);
      }

      return state;
   }

   protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return computeSteepShape(state);
   }

   protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return computeSteepShape(state);
   }

   protected boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   private static VoxelShape computeSteepShape(BlockState state) {
      boolean isTop = state.getValue(HALF) == Half.TOP;
      boolean isUpper = state.getValue(HALF_SHAPE) == Half.TOP;
      VoxelShape shape = Shapes.empty();
      if (!isUpper) {
         shape = Shapes.or(shape, isTop ? SLAB_TOP : SLAB_BOTTOM);
      }

      StairsShape blockShape = (StairsShape)state.getValue(SHAPE);
      if (blockShape == StairsShape.STRAIGHT || blockShape == StairsShape.INNER_LEFT || blockShape == StairsShape.INNER_RIGHT) {
         shape = Shapes.or(shape, getCollQuarterBlockSteep(state));
         if (isUpper) {
            shape = Shapes.or(shape, getCollQuarterBlock(state));
         }
      }

      if (blockShape != StairsShape.STRAIGHT) {
         shape = Shapes.or(shape, getCollEighthBlockSteep(state));
         if (isUpper) {
            shape = Shapes.or(shape, getCollEighthBlock(state));
         }
      }

      return shape.isEmpty() ? Shapes.block() : shape;
   }

   private static VoxelShape getCollQuarterBlock(BlockState state) {
      boolean isBottom = state.getValue(HALF) == Half.BOTTOM;

      return switch ((Direction)state.getValue(FACING)) {
         case SOUTH -> isBottom ? QTR_BOT_SOUTH : QTR_TOP_SOUTH;
         case WEST -> isBottom ? QTR_BOT_WEST : QTR_TOP_WEST;
         case EAST -> isBottom ? QTR_BOT_EAST : QTR_TOP_EAST;
         default -> isBottom ? QTR_BOT_NORTH : QTR_TOP_NORTH;
      };
   }

   private static VoxelShape getCollQuarterBlockSteep(BlockState state) {
      boolean isTop = state.getValue(HALF) == Half.TOP;
      boolean isUpper = state.getValue(HALF_SHAPE) == Half.TOP;
      if (isUpper) {
         return switch ((Direction)state.getValue(FACING)) {
            case SOUTH -> isTop ? QTR_BOT_SOUTH_FAT : QTR_TOP_SOUTH_FAT;
            case WEST -> isTop ? QTR_BOT_WEST_THIN : QTR_TOP_WEST_THIN;
            case EAST -> isTop ? QTR_BOT_EAST_FAT : QTR_TOP_EAST_FAT;
            default -> isTop ? QTR_BOT_NORTH_THIN : QTR_TOP_NORTH_THIN;
         };
      } else {
         return switch ((Direction)state.getValue(FACING)) {
            case SOUTH -> isTop ? QTR_BOT_SOUTH_THIN : QTR_TOP_SOUTH_THIN;
            case WEST -> isTop ? QTR_BOT_WEST_FAT : QTR_TOP_WEST_FAT;
            case EAST -> isTop ? QTR_BOT_EAST_THIN : QTR_TOP_EAST_THIN;
            default -> isTop ? QTR_BOT_NORTH_FAT : QTR_TOP_NORTH_FAT;
         };
      }
   }

   private static Direction shapeFacing(BlockState state) {
      Direction facing = (Direction)state.getValue(FACING);

      return switch ((StairsShape)state.getValue(SHAPE)) {
         case OUTER_RIGHT -> facing.getClockWise();
         case INNER_RIGHT -> facing.getOpposite();
         case INNER_LEFT -> facing.getCounterClockWise();
         default -> facing;
      };
   }

   private static VoxelShape getCollEighthBlock(BlockState state) {
      Direction shapeFacing = shapeFacing(state);
      boolean isBottom = state.getValue(HALF) == Half.BOTTOM;

      return switch (shapeFacing) {
         case SOUTH -> isBottom ? OCT_BOT_SE : OCT_TOP_SE;
         case WEST -> isBottom ? OCT_BOT_SW : OCT_TOP_SW;
         case EAST -> isBottom ? OCT_BOT_NE : OCT_TOP_NE;
         default -> isBottom ? OCT_BOT_NW : OCT_TOP_NW;
      };
   }

   private static VoxelShape getCollEighthBlockSteep(BlockState state) {
      Direction shapeFacing = shapeFacing(state);
      boolean isTop = state.getValue(HALF) == Half.TOP;
      boolean isUpper = state.getValue(HALF_SHAPE) == Half.TOP;
      if (isUpper) {
         return switch (shapeFacing) {
            case SOUTH -> isTop ? OCT_BOT_SE_FAT : OCT_TOP_SE_FAT;
            case WEST -> isTop ? OCT_BOT_SW_THIN : OCT_TOP_SW_THIN;
            case EAST -> isTop ? OCT_BOT_NE_THIN : OCT_TOP_NE_THIN;
            default -> isTop ? OCT_BOT_NW_THIN : OCT_TOP_NW_THIN;
         };
      } else {
         return switch (shapeFacing) {
            case SOUTH -> isTop ? OCT_BOT_SE_THIN : OCT_TOP_SE_THIN;
            case WEST -> isTop ? OCT_BOT_SW_FAT : OCT_TOP_SW_FAT;
            case EAST -> isTop ? OCT_BOT_NE_FAT : OCT_TOP_NE_FAT;
            default -> isTop ? OCT_BOT_NW_FAT : OCT_TOP_NW_FAT;
         };
      }
   }
}
