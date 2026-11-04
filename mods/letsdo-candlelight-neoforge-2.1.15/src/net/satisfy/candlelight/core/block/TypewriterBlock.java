package net.satisfy.candlelight.core.block;

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
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.candlelight.core.block.entity.TypewriterEntity;
import net.satisfy.candlelight.core.registry.ObjectRegistry;
import net.satisfy.candlelight.core.registry.SoundEventRegistry;
import net.satisfy.candlelight.core.util.CandlelightUtil;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TypewriterBlock extends BaseEntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final IntegerProperty FULL = IntegerProperty.create("full", 0, 2);
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, TypewriterBlock.voxelShapeSupplier.get()));
      }
   });
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.join(shape, Shapes.box(0.0625, 0.0, 0.0625, 0.9375, 0.125, 0.9375), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.3125, 0.125, 0.0625, 0.6875, 0.1875, 0.125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.1875, 0.125, 0.1875, 0.3125, 0.1875, 0.3125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.375, 0.125, 0.1875, 0.5, 0.1875, 0.3125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.5625, 0.125, 0.1875, 0.6875, 0.1875, 0.3125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.75, 0.125, 0.1875, 0.875, 0.1875, 0.3125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.53125, 0.125, 0.3125, 0.65625, 0.25, 0.4375), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.71875, 0.125, 0.3125, 0.84375, 0.25, 0.4375), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.34375, 0.125, 0.3125, 0.46875, 0.25, 0.4375), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.15625, 0.125, 0.3125, 0.28125, 0.25, 0.4375), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.0625, 0.125, 0.4375, 0.9375, 0.4375, 0.9375), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.8125, 0.4375, 0.75, 0.9375, 0.6875, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.0625, 0.4375, 0.75, 0.1875, 0.6875, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.1875, 0.5, 0.75, 0.8125, 0.5625, 0.8125), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.1875, 0.53125, 0.875, 0.8125, 0.65625, 0.875), BooleanOp.OR);
      shape = Shapes.join(shape, Shapes.box(0.25, 0.375, 0.84375, 0.75, 0.75, 0.84375), BooleanOp.OR);
      return Shapes.join(shape, Shapes.box(0.125, 0.125, 0.1875, 0.1875, 0.1875, 0.3125), BooleanOp.OR);
   };
   public static final MapCodec<TypewriterBlock> CODEC = simpleCodec(TypewriterBlock::new);

   public TypewriterBlock(Properties settings) {
      super(settings);
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      ItemStack stack = player.getItemInHand(hand);
      if (stack.getItem() == ObjectRegistry.NOTE_PAPER.get() && (Integer)state.getValue(FULL) == 0) {
         world.setBlock(pos, (BlockState)state.setValue(FULL, 1), 2);
         if (world.getBlockEntity(pos) instanceof TypewriterEntity typeWriterEntity) {
            typeWriterEntity.addPaper(new ItemStack((ItemLike)ObjectRegistry.NOTE_PAPER_WRITEABLE.get()));
            stack.setCount(stack.getCount() - 1);
         }

         return ItemInteractionResult.SUCCESS;
      } else if ((Integer)state.getValue(FULL) == 1) {
         if (world.getBlockEntity(pos) instanceof TypewriterEntity typeWriterEntity && world.isClientSide) {
            CandlelightUtil.setTypeWriterScreen(player, typeWriterEntity);
         }

         return ItemInteractionResult.SUCCESS;
      } else if ((Integer)state.getValue(FULL) == 2) {
         world.setBlock(pos, (BlockState)state.setValue(FULL, 0), 2);
         if (world.getBlockEntity(pos) instanceof TypewriterEntity typeWriterEntity) {
            ItemStack paper = typeWriterEntity.getPaper();
            ItemStack result = new ItemStack((ItemLike)ObjectRegistry.NOTE_PAPER_WRITTEN.get());
            if (paper.has(DataComponents.CUSTOM_DATA)) {
               result.set(DataComponents.CUSTOM_DATA, (CustomData)paper.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY));
            }

            ((CustomData)result.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY))
               .copyTag()
               .put("author", StringTag.valueOf(player.getName().getString()));
            player.addItem(result);
            typeWriterEntity.removePaper();
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return super.useItemOn(itemStack, state, world, pos, player, hand, hit);
      }
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if ((Integer)state.getValue(FULL) != 0 || !player.getMainHandItem().isEmpty() || !player.getOffhandItem().isEmpty()) {
         return super.useWithoutItem(state, level, pos, player, hit);
      } else if (level.isClientSide) {
         return InteractionResult.SUCCESS;
      } else if (!(level.getBlockEntity(pos) instanceof TypewriterEntity typewriter)) {
         return InteractionResult.PASS;
      } else {
         RandomSource var13 = level.getRandom();
         int keys = 0;
         int presses = 2 + var13.nextInt(3);

         for (int param = 0; param < presses; param++) {
            keys |= 1 << var13.nextInt(7);
         }

         int param = keys;
         if (var13.nextFloat() < 0.3F) {
            param |= 128;
         }

         int column = typewriter.advanceMashColumn(presses);
         int id;
         if (column < 0) {
            id = 25;
            level.playSound(null, pos, (SoundEvent)SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.BLOCKS, 0.8F, 1.0F);
         } else {
            id = 1 + column;
         }

         level.blockEvent(pos, this, id, param);
         level.playSound(null, pos, (SoundEvent)SoundEventRegistry.TYPEWRITER.get(), SoundSource.BLOCKS, 1.0F, 0.85F + var13.nextFloat() * 0.3F);
         return InteractionResult.CONSUME;
      }
   }

   protected boolean triggerEvent(BlockState state, Level level, BlockPos pos, int id, int param) {
      BlockEntity blockEntity = level.getBlockEntity(pos);
      return blockEntity != null && blockEntity.triggerEvent(id, param);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite())).setValue(FULL, 0);
   }

   public void onRemove(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
      if (!blockState.is(blockState2.getBlock()) && level.getBlockEntity(blockPos) instanceof TypewriterEntity typeWriterEntity) {
         ItemStack dropStack = typeWriterEntity.getPaper();
         Containers.dropItemStack(level, blockPos.getX(), blockPos.getY(), blockPos.getZ(), dropStack);
      }

      super.onRemove(blockState, level, blockPos, blockState2, bl);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING}).add(new Property[]{FULL});
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      tooltip.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TypewriterEntity(pos, state);
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }
}
