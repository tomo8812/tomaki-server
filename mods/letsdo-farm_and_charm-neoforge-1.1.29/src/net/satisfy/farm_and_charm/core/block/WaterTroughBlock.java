package net.satisfy.farm_and_charm.core.block;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.block.LineConnectingBlock;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class WaterTroughBlock extends LineConnectingBlock {
   public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 3);
   private static final VoxelShape SHAPE_MIDDLE_NORTH = createShape(false, false);
   private static final VoxelShape SHAPE_LEFT_NORTH = createShape(true, false);
   private static final VoxelShape SHAPE_RIGHT_NORTH = createShape(false, true);
   private static final VoxelShape SHAPE_SINGLE_NORTH = createShape(true, true);
   private static final EnumMap<Direction, VoxelShape> SHAPE_MIDDLE = createRotations(SHAPE_MIDDLE_NORTH);
   private static final EnumMap<Direction, VoxelShape> SHAPE_LEFT = createRotations(SHAPE_LEFT_NORTH);
   private static final EnumMap<Direction, VoxelShape> SHAPE_RIGHT = createRotations(SHAPE_RIGHT_NORTH);
   private static final EnumMap<Direction, VoxelShape> SHAPE_SINGLE = createRotations(SHAPE_SINGLE_NORTH);

   public WaterTroughBlock(Properties properties) {
      super(properties.randomTicks());
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(LEVEL, 0));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LEVEL});
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (!stack.is(Items.WATER_BUCKET)) {
         if (stack.is(Items.BUCKET)) {
            int currentLevel = (Integer)state.getValue(LEVEL);
            if (currentLevel <= 0) {
               return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }

            if (level.isClientSide) {
               return ItemInteractionResult.SUCCESS;
            }

            boolean infinite = this.isInfiniteSource(level, pos, (Direction)state.getValue(FACING));
            if (!player.getAbilities().instabuild) {
               player.setItemInHand(hand, new ItemStack(Items.WATER_BUCKET));
            }

            if (!infinite) {
               level.setBlock(pos, (BlockState)state.setValue(LEVEL, Math.max(0, currentLevel - 1)), 3);
            }

            return ItemInteractionResult.SUCCESS;
         } else {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }
      } else {
         if (level.isClientSide) {
            return ItemInteractionResult.SUCCESS;
         }

         List<BlockPos> component = this.getComponent(level, pos, (Direction)state.getValue(FACING));
         int baseFill = this.getFillLevelForComponentSize(component.size());
         boolean hasAnyZero = false;

         for (BlockPos componentPos : component) {
            BlockState componentState = level.getBlockState(componentPos);
            if (componentState.getBlock() == this && (Integer)componentState.getValue(LEVEL) == 0) {
               hasAnyZero = true;
               break;
            }
         }

         boolean changed = false;
         if (hasAnyZero) {
            for (BlockPos componentPos : component) {
               BlockState componentState = level.getBlockState(componentPos);
               if (componentState.getBlock() == this && (Integer)componentState.getValue(LEVEL) != baseFill) {
                  level.setBlock(componentPos, (BlockState)componentState.setValue(LEVEL, baseFill), 3);
                  changed = true;
               }
            }
         } else {
            for (BlockPos componentPos : component) {
               BlockState componentState = level.getBlockState(componentPos);
               if (componentState.getBlock() == this) {
                  int currentLevel = (Integer)componentState.getValue(LEVEL);
                  if (currentLevel < 3) {
                     level.setBlock(componentPos, (BlockState)componentState.setValue(LEVEL, currentLevel + 1), 3);
                     changed = true;
                  }
               }
            }
         }

         if (!player.getAbilities().instabuild && changed) {
            player.setItemInHand(hand, new ItemStack(Items.BUCKET));
         }

         return ItemInteractionResult.SUCCESS;
      }
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      int currentLevel = (Integer)state.getValue(LEVEL);
      if (currentLevel < 3) {
         if (level.isRainingAt(pos.above())) {
            if (random.nextInt(18) == 0) {
               level.setBlock(pos, (BlockState)state.setValue(LEVEL, currentLevel + 1), 3);
            }
         }
      }
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      Direction facing = (Direction)state.getValue(FACING);
      Direction left = facing.getCounterClockWise();
      Direction right = facing.getClockWise();
      boolean hasLeft = this.isSameTrough(world, pos.relative(left), facing);
      boolean hasRight = this.isSameTrough(world, pos.relative(right), facing);
      if (hasLeft && hasRight) {
         return SHAPE_MIDDLE.get(facing);
      } else if (!hasLeft && !hasRight) {
         return SHAPE_SINGLE.get(facing);
      } else {
         return !hasLeft ? SHAPE_LEFT.get(facing) : SHAPE_RIGHT.get(facing);
      }
   }

   public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
      super.onPlace(state, level, pos, oldState, movedByPiston);
      if (!level.isClientSide) {
         level.scheduleTick(pos, this, 1);
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      BlockState updated = super.updateShape(state, direction, neighborState, level, pos, neighborPos);
      if (level instanceof Level realLevel && !realLevel.isClientSide) {
         realLevel.scheduleTick(pos, this, 1);
      }

      return updated;
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      this.normalizeConnectedLevels(level, pos, state);
   }

   private void normalizeConnectedLevels(Level level, BlockPos pos, BlockState state) {
      if (state.getBlock() == this) {
         Direction facing = (Direction)state.getValue(FACING);
         List<BlockPos> component = this.getComponent(level, pos, facing);
         if (component.size() >= 2) {
            int minLevel = 3;
            int maxLevel = 0;
            boolean hasZero = false;

            for (BlockPos componentPos : component) {
               BlockState componentState = level.getBlockState(componentPos);
               if (componentState.getBlock() == this) {
                  int current = (Integer)componentState.getValue(LEVEL);
                  if (current == 0) {
                     hasZero = true;
                  }

                  if (current < minLevel) {
                     minLevel = current;
                  }

                  if (current > maxLevel) {
                     maxLevel = current;
                  }
               }
            }

            int target;
            if (maxLevel > 0 && hasZero) {
               target = Math.max(0, maxLevel - 1);
            } else {
               target = minLevel;
            }

            for (BlockPos componentPos : component) {
               BlockState componentState = level.getBlockState(componentPos);
               if (componentState.getBlock() == this && (Integer)componentState.getValue(LEVEL) != target) {
                  level.setBlock(componentPos, (BlockState)componentState.setValue(LEVEL, target), 3);
               }
            }
         }
      }
   }

   private static VoxelShape createShape(boolean leftLeg, boolean rightLeg) {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.0, 0.25, 0.0, 1.0, 0.75, 1.0));
      if (leftLeg) {
         shape = Shapes.or(shape, Shapes.box(0.0625, 0.0, 0.0, 0.3125, 0.25, 1.0));
      }

      if (rightLeg) {
         shape = Shapes.or(shape, Shapes.box(0.6875, 0.0, 0.0, 0.9375, 0.25, 1.0));
      }

      return shape;
   }

   private static EnumMap<Direction, VoxelShape> createRotations(VoxelShape northShape) {
      EnumMap<Direction, VoxelShape> map = new EnumMap<>(Direction.class);

      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, northShape));
      }

      return map;
   }

   private int getFillLevelForComponentSize(int size) {
      if (size <= 1) {
         return 3;
      } else {
         return size == 2 ? 2 : 1;
      }
   }

   private boolean isInfiniteSource(Level level, BlockPos pos, Direction facing) {
      List<BlockPos> component = this.getComponent(level, pos, facing);
      if (component.size() < 3) {
         return false;
      }

      boolean usesX = facing.getAxis() == Axis.Z;
      int min = Integer.MAX_VALUE;
      int max = Integer.MIN_VALUE;

      for (BlockPos componentPos : component) {
         int value = usesX ? componentPos.getX() : componentPos.getZ();
         if (value < min) {
            min = value;
         }

         if (value > max) {
            max = value;
         }
      }

      int self = usesX ? pos.getX() : pos.getZ();
      return self != min && self != max;
   }

   private List<BlockPos> getComponent(Level level, BlockPos start, Direction facing) {
      Direction left = facing.getCounterClockWise();
      Direction right = facing.getClockWise();
      HashSet<BlockPos> visited = new HashSet<>();
      ArrayDeque<BlockPos> queue = new ArrayDeque<>();
      List<BlockPos> result = new ArrayList<>();
      visited.add(start);
      queue.add(start);

      while (!queue.isEmpty()) {
         BlockPos current = queue.poll();
         result.add(current);

         for (Direction step : new Direction[]{left, right}) {
            BlockPos next = current.relative(step);
            if (visited.add(next)) {
               BlockState nextState = level.getBlockState(next);
               if (nextState.getBlock() == this && nextState.getValue(FACING) == facing) {
                  queue.add(next);
               }
            }
         }
      }

      return result;
   }

   private boolean isSameTrough(BlockGetter world, BlockPos pos, Direction facing) {
      BlockState otherState = world.getBlockState(pos);
      return otherState.getBlock() != this ? false : otherState.getValue(FACING) == facing;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      int earthy = 16767334;
      int gold = 16766720;
      if (Screen.hasShiftDown()) {
         tooltip.add(Component.translatable("tooltip.farm_and_charm.water_trough.info_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
      } else {
         tooltip.add(
            Component.translatable(
                  "tooltip.farm_and_charm.tooltip_information.hold",
                  new Object[]{Component.literal("[SHIFT]").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(gold)))}
               )
               .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy)))
         );
      }
   }
}
