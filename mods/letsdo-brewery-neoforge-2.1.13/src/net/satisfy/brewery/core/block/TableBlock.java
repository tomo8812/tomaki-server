package net.satisfy.brewery.core.block;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.foundation.block.LineConnectingBlock;
import net.satisfy.foundation.block.LineConnectingType;
import net.satisfy.foundation.util.DyeHelper;
import org.jetbrains.annotations.NotNull;

public class TableBlock extends LineConnectingBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   public static final BooleanProperty HAS_TABLECLOTH = BooleanProperty.create("has_tablecloth");
   public static final EnumProperty<DyeColor> COLOR = PatternedWoolBlock.COLOR;
   public static final VoxelShape TOP_SHAPE = Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0);
   public static final VoxelShape[] LEG_SHAPES = new VoxelShape[]{
      Block.box(7.0, 0.0, 7.0, 9.0, 13.0, 9.0),
      Block.box(7.0, 0.0, 7.0, 9.0, 13.0, 9.0),
      Block.box(7.0, 0.0, 7.0, 9.0, 13.0, 9.0),
      Block.box(7.0, 0.0, 7.0, 9.0, 13.0, 9.0)
   };

   public TableBlock(Properties settings) {
      super(settings);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(WATERLOGGED, false)).setValue(HAS_TABLECLOTH, false))
            .setValue(COLOR, DyeColor.LIGHT_BLUE)
      );
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if ((Boolean)state.getValue(HAS_TABLECLOTH) && itemStack.getItem() instanceof DyeItem dyeItem) {
         return DyeHelper.dye(itemStack, dyeItem.getDyeColor(), state, COLOR, world, pos, player);
      }

      if (itemStack.getItem().equals(ObjectRegistry.PATTERNED_CARPET.get()) && !(Boolean)state.getValue(HAS_TABLECLOTH)) {
         world.setBlock(pos, (BlockState)((BlockState)state.setValue(HAS_TABLECLOTH, true)).setValue(COLOR, PatternedWoolBlock.getColor(itemStack)), 3);
         if (!player.isCreative()) {
            itemStack.shrink(1);
         }

         return ItemInteractionResult.sidedSuccess(world.isClientSide);
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
      if (!(Boolean)state.getValue(HAS_TABLECLOTH)) {
         return super.useWithoutItem(state, level, pos, player, hitResult);
      }

      level.setBlock(pos, (BlockState)state.setValue(HAS_TABLECLOTH, false), 3);
      if (!level.isClientSide) {
         ItemStack returnedCarpet = PatternedWoolBlock.withColor(
            new ItemStack((ItemLike)ObjectRegistry.PATTERNED_CARPET.get()), (DyeColor)state.getValue(COLOR)
         );
         if (!player.addItem(returnedCarpet)) {
            player.drop(returnedCarpet, false);
         }
      }

      return InteractionResult.sidedSuccess(level.isClientSide);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      Direction direction = (Direction)state.getValue(FACING);
      LineConnectingType type = (LineConnectingType)state.getValue(TYPE);
      if (type == LineConnectingType.MIDDLE) {
         return TOP_SHAPE;
      }

      if ((direction != Direction.NORTH || type != LineConnectingType.LEFT) && (direction != Direction.SOUTH || type != LineConnectingType.RIGHT)) {
         if ((direction != Direction.NORTH || type != LineConnectingType.RIGHT) && (direction != Direction.SOUTH || type != LineConnectingType.LEFT)) {
            if ((direction != Direction.EAST || type != LineConnectingType.LEFT) && (direction != Direction.WEST || type != LineConnectingType.RIGHT)) {
               return (direction != Direction.EAST || type != LineConnectingType.RIGHT) && (direction != Direction.WEST || type != LineConnectingType.LEFT)
                  ? Shapes.or(TOP_SHAPE, LEG_SHAPES)
                  : Shapes.or(TOP_SHAPE, new VoxelShape[]{LEG_SHAPES[2], LEG_SHAPES[3]});
            } else {
               return Shapes.or(TOP_SHAPE, new VoxelShape[]{LEG_SHAPES[0], LEG_SHAPES[1]});
            }
         } else {
            return Shapes.or(TOP_SHAPE, new VoxelShape[]{LEG_SHAPES[1], LEG_SHAPES[2]});
         }
      } else {
         return Shapes.or(TOP_SHAPE, new VoxelShape[]{LEG_SHAPES[0], LEG_SHAPES[3]});
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Level world = context.getLevel();
      BlockPos clickedPos = context.getClickedPos();
      return (BlockState)Objects.requireNonNull(super.getStateForPlacement(context))
         .setValue(WATERLOGGED, world.getFluidState(clickedPos).getType() == Fluids.WATER);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{WATERLOGGED, HAS_TABLECLOTH, COLOR});
   }

   @NotNull
   public FluidState getFluidState(BlockState state) {
      return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
   }
}
