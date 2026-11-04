package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.entity.ScarecrowBlockEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScarecrowBlock extends BaseEntityBlock {
   public static final MapCodec<ScarecrowBlock> CODEC = simpleCodec(ScarecrowBlock::new);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.4375, 0.0, 0.4375, 0.5625, 0.75, 0.5625), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.625, 0.375, 0.75, 1.4375, 0.6875), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 1.4375, 0.3125, 0.75, 1.9375, 0.8125), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.6875, 1.125, 0.375, 1.0, 1.5, 0.6875), BooleanOp.OR);
      return Shapes.joinUnoptimized(shape, Shapes.box(0.0, 1.125, 0.375, 0.3125, 1.5, 0.6875), BooleanOp.OR);
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), m -> {
      for (Direction d : Plane.HORIZONTAL) {
         m.put(d, ShapeUtil.rotateShape(Direction.NORTH, d, voxelShapeSupplier.get()));
      }
   });
   private static final Map<Direction, VoxelShape> LOWER_SHAPE = (Map<Direction, VoxelShape>)Util.make(
      new HashMap(), m -> SHAPE.forEach((d, shape) -> m.put(d, Shapes.join(shape, Shapes.block(), BooleanOp.AND)))
   );
   private static final Map<Direction, VoxelShape> UPPER_SHAPE = (Map<Direction, VoxelShape>)Util.make(
      new HashMap(), m -> SHAPE.forEach((d, shape) -> m.put(d, Shapes.join(shape.move(0.0, -1.0, 0.0), Shapes.block(), BooleanOp.AND)))
   );
   public static final BooleanProperty HAS_DUNGAREES = BooleanProperty.create("has_dungarees");
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   public static final EnumProperty<ScarecrowMode> MODE = EnumProperty.create("mode", ScarecrowMode.class);

   public ScarecrowBlock(Properties props) {
      super(props);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH)).setValue(HAS_DUNGAREES, true))
               .setValue(HALF, DoubleBlockHalf.LOWER))
            .setValue(MODE, ScarecrowMode.CALM)
      );
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      BlockPos pos = ctx.getClickedPos();
      Level level = ctx.getLevel();
      return pos.getY() < level.getMaxBuildHeight() - 1 && level.getBlockState(pos.above()).canBeReplaced(ctx)
         ? (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite())).setValue(HAS_DUNGAREES, true)
         : null;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, HAS_DUNGAREES, HALF, MODE});
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      level.setBlock(pos.above(), (BlockState)state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   @NotNull
   public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
      if (!level.isClientSide && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockPos below = pos.below();
         BlockState lower = level.getBlockState(below);
         if (lower.is(this) && lower.getValue(HALF) == DoubleBlockHalf.LOWER) {
            if (player.isCreative()) {
               level.setBlock(below, Blocks.AIR.defaultBlockState(), 35);
               level.levelEvent(player, 2001, below, Block.getId(lower));
            } else {
               level.destroyBlock(below, true, player);
            }
         }
      }

      return super.playerWillDestroy(level, pos, state, player);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return (state.getValue(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE : LOWER_SHAPE).get(state.getValue(FACING));
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
         VoxelShape below = world.getBlockState(pos.below()).getShape(world, pos.below());
         return Block.isFaceFull(below, Direction.UP);
      } else {
         BlockState lower = world.getBlockState(pos.below());
         return lower.is(this) && lower.getValue(HALF) == DoubleBlockHalf.LOWER;
      }
   }

   public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(world, pos)) {
         world.destroyBlock(pos, true);
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
         if (!state.canSurvive(world, pos)) {
            world.scheduleTick(pos, this, 1);
         }

         return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
      } else if (direction != Direction.DOWN || neighborState.is(this) && neighborState.getValue(HALF) == DoubleBlockHalf.LOWER) {
         return direction == Direction.DOWN
            ? (BlockState)((BlockState)((BlockState)state.setValue(FACING, (Direction)neighborState.getValue(FACING)))
                  .setValue(HAS_DUNGAREES, (Boolean)neighborState.getValue(HAS_DUNGAREES)))
               .setValue(MODE, (ScarecrowMode)neighborState.getValue(MODE))
            : state;
      } else {
         return Blocks.AIR.defaultBlockState();
      }
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, @Nullable Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (level.isClientSide) {
         return ItemInteractionResult.SUCCESS;
      }

      if (player == null) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         pos = pos.below();
         state = level.getBlockState(pos);
         if (!state.is(this)) {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }
      }

      if ((Boolean)state.getValue(HAS_DUNGAREES)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      if (!stack.is((Item)ObjectRegistry.DUNGAREES.get())) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      level.setBlock(pos, (BlockState)state.setValue(HAS_DUNGAREES, true), 3);
      if (!player.isCreative()) {
         stack.shrink(1);
      }

      return ItemInteractionResult.SUCCESS;
   }

   @NotNull
   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      }

      if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         pos = pos.below();
         state = level.getBlockState(pos);
         if (!state.is(this)) {
            return InteractionResult.PASS;
         }
      }

      if (player.isShiftKeyDown()) {
         level.setBlock(pos, (BlockState)state.cycle(MODE), 3);
         level.playSound(null, pos, SoundEvents.GRASS_HIT, SoundSource.BLOCKS, 0.8F, 0.9F + level.random.nextFloat() * 0.2F);
         return InteractionResult.SUCCESS;
      }

      if (!(Boolean)state.getValue(HAS_DUNGAREES)) {
         return InteractionResult.PASS;
      }

      ItemStack dungarees = new ItemStack((ItemLike)ObjectRegistry.DUNGAREES.get());
      if (!player.addItem(dungarees)) {
         player.drop(dungarees, false);
      }

      level.setBlock(pos, (BlockState)state.setValue(HAS_DUNGAREES, false), 3);
      return InteractionResult.SUCCESS;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState st) {
      return st.getValue(HALF) == DoubleBlockHalf.LOWER ? new ScarecrowBlockEntity(pos, st) : null;
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level lvl, BlockState st, BlockEntityType<T> type) {
      return type == EntityTypeRegistry.SCARECROW_BLOCK_ENTITY.get() ? (level, pos, state, be) -> {
         if (level.isClientSide) {
            ((ScarecrowBlockEntity)be).clientTick(level, pos, state);
         } else {
            ScarecrowBlockEntity.tick(level, be);
         }
      } : null;
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return state.getValue(HALF) == DoubleBlockHalf.UPPER ? RenderShape.INVISIBLE : RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      tooltip.add(Component.translatable("tooltip.farm_and_charm.thankyou_1").withStyle(new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY}));
      tooltip.add(Component.empty());
      tooltip.add(Component.translatable("tooltip.farm_and_charm.thankyou_2").withStyle(ChatFormatting.DARK_PURPLE));
      tooltip.add(Component.translatable("tooltip.farm_and_charm.thankyou_4").withStyle(ChatFormatting.BLUE));
      tooltip.add(Component.empty());
      tooltip.add(Component.translatable("tooltip.farm_and_charm.thankyou_3").withStyle(ChatFormatting.GOLD));
   }
}
