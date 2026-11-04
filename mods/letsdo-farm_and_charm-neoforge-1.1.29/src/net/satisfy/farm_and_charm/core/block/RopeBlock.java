package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import org.jetbrains.annotations.NotNull;

public class RopeBlock extends Block {
   public static final MapCodec<RopeBlock> CODEC = simpleCodec(RopeBlock::new);
   public static final BooleanProperty NORTH = BooleanProperty.create("north");
   public static final BooleanProperty SOUTH = BooleanProperty.create("south");
   public static final BooleanProperty EAST = BooleanProperty.create("east");
   public static final BooleanProperty WEST = BooleanProperty.create("west");
   public static final BooleanProperty CENTER_PIECE = BooleanProperty.create("center_piece");
   public static final BooleanProperty UP = BooleanProperty.create("up");
   public static final BooleanProperty DOWN = BooleanProperty.create("down");
   public static final BooleanProperty SUPPORTING_ROPE_KNOT = BooleanProperty.create("supporting_rope_knot");
   public static final BooleanProperty ROPE_KNOT = BooleanProperty.create("rope_knot");
   private static final VoxelShape CONNECTION_NORTH = Block.box(7.0, 11.0, 0.0, 9.0, 13.0, 7.0);
   private static final VoxelShape CONNECTION_SOUTH = Block.box(7.0, 11.0, 9.0, 9.0, 13.0, 16.0);
   private static final VoxelShape CONNECTION_WEST = Block.box(0.0, 11.0, 7.0, 7.0, 13.0, 9.0);
   private static final VoxelShape CONNECTION_EAST = Block.box(9.0, 11.0, 7.0, 16.0, 13.0, 9.0);
   private static final VoxelShape CONNECTION_KNOT = Block.box(6.0, 10.0, 6.0, 10.0, 14.0, 10.0);
   private static final VoxelShape CONNECTION_CENTER = Block.box(7.0, 11.0, 7.0, 9.0, 13.0, 9.0);
   private static final VoxelShape CONNECTION_DOWN = Block.box(7.0, 0.0, 7.0, 9.0, 11.0, 9.0);
   private static final VoxelShape CONNECTION_UP = Block.box(7.0, 13.0, 7.0, 9.0, 16.0, 9.0);
   private static final VoxelShape CONNECTION_SUPPORTING_KNOT = Block.box(5.0, 8.0, 5.0, 11.0, 16.0, 11.0);
   private static final VoxelShape BOTTOM_SUPPORT = Block.box(7.0, 0.0, 7.0, 9.0, 1.0, 9.0);

   public RopeBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition
                                       .any())
                                    .setValue(NORTH, false))
                                 .setValue(SOUTH, false))
                              .setValue(EAST, false))
                           .setValue(WEST, false))
                        .setValue(UP, false))
                     .setValue(DOWN, false))
                  .setValue(CENTER_PIECE, false))
               .setValue(SUPPORTING_ROPE_KNOT, false))
            .setValue(ROPE_KNOT, false)
      );
   }

   @NotNull
   protected MapCodec<? extends Block> codec() {
      return CODEC;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{NORTH, SOUTH, EAST, WEST, UP, DOWN, CENTER_PIECE, SUPPORTING_ROPE_KNOT, ROPE_KNOT});
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      Level level = ctx.getLevel();
      BlockPos pos = ctx.getClickedPos();
      boolean north = this.canConnectTo(level, pos.north(), Direction.SOUTH);
      boolean south = this.canConnectTo(level, pos.south(), Direction.NORTH);
      boolean east = this.canConnectTo(level, pos.east(), Direction.WEST);
      boolean west = this.canConnectTo(level, pos.west(), Direction.EAST);
      boolean up = this.canConnectTo(level, pos.above(), Direction.DOWN);
      boolean down = this.canConnectTo(level, pos.below(), Direction.UP);
      boolean belowClimbingCrop = level.getBlockState(pos.below()).getBlock() instanceof ClimbingCropBlock;
      if (belowClimbingCrop) {
         down = true;
         up = true;
      }

      boolean hasHorizontal = north || south || east || west;
      boolean isCorner = (north || south) && (east || west);
      boolean hasVertical = up || down;
      boolean ropeKnot = hasHorizontal && (isCorner || hasVertical);
      boolean topSupport = level.getBlockState(pos.above()).isFaceSturdy(level, pos.above(), Direction.DOWN);
      BlockState state = (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState()
                                 .setValue(NORTH, north))
                              .setValue(SOUTH, south))
                           .setValue(EAST, east))
                        .setValue(WEST, west))
                     .setValue(UP, up))
                  .setValue(DOWN, down))
               .setValue(CENTER_PIECE, true))
            .setValue(ROPE_KNOT, ropeKnot))
         .setValue(SUPPORTING_ROPE_KNOT, topSupport);
      if (this.hasNoConnection(state)) {
         return null;
      } else {
         return !this.hasFixedAnchor(ctx.getLevel(), pos) ? null : state;
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if (direction.getAxis().isHorizontal() || direction.getAxis().isVertical()) {
         boolean conn = this.canConnectTo(level, neighborPos, direction.getOpposite());
         state = (BlockState)state.setValue(this.prop(direction), conn);
         if (direction == Direction.UP) {
            boolean topSupport = neighbor.isFaceSturdy(level, neighborPos, Direction.DOWN);
            state = (BlockState)state.setValue(SUPPORTING_ROPE_KNOT, topSupport);
         }

         if (direction == Direction.DOWN && neighbor.getBlock() instanceof ClimbingCropBlock) {
            state = (BlockState)((BlockState)state.setValue(DOWN, true)).setValue(UP, true);
         }

         boolean north = (Boolean)state.getValue(NORTH);
         boolean south = (Boolean)state.getValue(SOUTH);
         boolean east = (Boolean)state.getValue(EAST);
         boolean west = (Boolean)state.getValue(WEST);
         boolean up = (Boolean)state.getValue(UP);
         boolean down = (Boolean)state.getValue(DOWN);
         boolean hasHorizontal = north || south || east || west;
         boolean isCorner = (north || south) && (east || west);
         boolean hasVertical = up || down;
         boolean ropeKnot = hasHorizontal && (isCorner || hasVertical);
         state = (BlockState)state.setValue(ROPE_KNOT, ropeKnot);
         state = (BlockState)state.setValue(CENTER_PIECE, true);
         if (this.hasNoConnection(state) || !this.canSurvive(state, level, pos)) {
            level.scheduleTick(pos, this, 1);
         }
      }

      return state;
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (this.hasNoConnection(state) || !this.canSurvive(state, level, pos)) {
         level.destroyBlock(pos, true);
      }
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      return this.hasFixedAnchor(level, pos);
   }

   private boolean hasFixedAnchor(LevelReader level, BlockPos start) {
      ArrayDeque<BlockPos> queue = new ArrayDeque<>();
      Set<BlockPos> seen = new HashSet<>();
      queue.add(start);
      int steps = 0;

      while (!queue.isEmpty() && steps < 256) {
         BlockPos p = queue.poll();
         if (seen.add(p)) {
            for (Direction d : Direction.values()) {
               BlockPos n = p.relative(d);
               BlockState s = level.getBlockState(n);
               if (s.getBlock() instanceof RopeKnotBlock) {
                  return true;
               }

               if (s.getBlock() instanceof RopeBlock) {
                  if (!seen.contains(n)) {
                     queue.add(n);
                  }
               } else if (d == Direction.UP) {
                  if (s.isFaceSturdy(level, n, Direction.DOWN)) {
                     return true;
                  }
               } else if (d != Direction.DOWN && s.isFaceSturdy(level, n, d.getOpposite())) {
                  return true;
               }
            }

            steps++;
         }
      }

      return false;
   }

   private boolean hasNoConnection(BlockState state) {
      return !(Boolean)state.getValue(NORTH)
         && !(Boolean)state.getValue(SOUTH)
         && !(Boolean)state.getValue(EAST)
         && !(Boolean)state.getValue(WEST)
         && !(Boolean)state.getValue(UP)
         && !(Boolean)state.getValue(DOWN);
   }

   private BooleanProperty prop(Direction direction) {
      return switch (direction) {
         case SOUTH -> SOUTH;
         case EAST -> EAST;
         case WEST -> WEST;
         case UP -> UP;
         case DOWN -> DOWN;
         default -> NORTH;
      };
   }

   private boolean canConnectTo(LevelAccessor level, BlockPos neighborPos, Direction dirTowardNeighbor) {
      BlockState blockState = level.getBlockState(neighborPos);
      if (blockState.getBlock() instanceof RopeBlock) {
         return true;
      }

      if (blockState.getBlock() instanceof RopeKnotBlock) {
         return true;
      }

      if (!(blockState.getBlock() instanceof ClimbingCropBlock)) {
         if (dirTowardNeighbor == Direction.UP) {
            if (blockState.getBlock() instanceof LanternBlock) {
               return true;
            } else {
               return blockState.getBlock() instanceof BellBlock ? true : blockState.isFaceSturdy(level, neighborPos, Direction.DOWN);
            }
         } else {
            return blockState.isFaceSturdy(level, neighborPos, dirTowardNeighbor);
         }
      } else {
         return dirTowardNeighbor == Direction.DOWN || dirTowardNeighbor == Direction.UP;
      }
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
      VoxelShape shape = CONNECTION_CENTER;
      if ((Boolean)state.getValue(NORTH)) {
         shape = Shapes.or(shape, CONNECTION_NORTH);
      }

      if ((Boolean)state.getValue(SOUTH)) {
         shape = Shapes.or(shape, CONNECTION_SOUTH);
      }

      if ((Boolean)state.getValue(WEST)) {
         shape = Shapes.or(shape, CONNECTION_WEST);
      }

      if ((Boolean)state.getValue(EAST)) {
         shape = Shapes.or(shape, CONNECTION_EAST);
      }

      if ((Boolean)state.getValue(UP)) {
         shape = Shapes.or(shape, CONNECTION_UP);
      }

      if ((Boolean)state.getValue(DOWN)) {
         shape = Shapes.or(shape, CONNECTION_DOWN);
      }

      if ((Boolean)state.getValue(ROPE_KNOT)) {
         shape = Shapes.or(shape, CONNECTION_KNOT);
      }

      if ((Boolean)state.getValue(SUPPORTING_ROPE_KNOT)) {
         shape = Shapes.or(shape, CONNECTION_SUPPORTING_KNOT);
      }

      return shape;
   }

   @NotNull
   protected VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
      return Shapes.or(this.getShape(state, level, pos, CollisionContext.empty()), BOTTOM_SUPPORT);
   }

   @NotNull
   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos blockPos, CollisionContext ctx) {
      VoxelShape shape = CONNECTION_CENTER;
      if ((Boolean)state.getValue(NORTH)) {
         shape = Shapes.or(shape, CONNECTION_NORTH);
      }

      if ((Boolean)state.getValue(SOUTH)) {
         shape = Shapes.or(shape, CONNECTION_SOUTH);
      }

      if ((Boolean)state.getValue(WEST)) {
         shape = Shapes.or(shape, CONNECTION_WEST);
      }

      if ((Boolean)state.getValue(EAST)) {
         shape = Shapes.or(shape, CONNECTION_EAST);
      }

      if ((Boolean)state.getValue(UP)) {
         shape = Shapes.or(shape, CONNECTION_UP);
      }

      if ((Boolean)state.getValue(DOWN)) {
         shape = Shapes.or(shape, CONNECTION_DOWN);
      }

      if ((Boolean)state.getValue(ROPE_KNOT)) {
         shape = Shapes.or(shape, CONNECTION_KNOT);
      }

      if ((Boolean)state.getValue(SUPPORTING_ROPE_KNOT)) {
         shape = Shapes.or(shape, CONNECTION_SUPPORTING_KNOT);
      }

      return shape;
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
   }
}
