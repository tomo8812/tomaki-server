package net.satisfy.brewery.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.satisfy.brewery.core.block.entity.BigBarrelBlockEntity;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public class BigBarrelBlock extends HorizontalDirectionalBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   public static final MapCodec<BigBarrelBlock> CODEC = simpleCodec(BigBarrelBlock::new);

   public BigBarrelBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(this.defaultBlockState());
   }

   @NotNull
   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }

   @NotNull
   public ItemStack getCloneItemStack(LevelReader levelReader, BlockPos blockPos, BlockState blockState) {
      return !(this instanceof BigBarrelMainBlock)
         ? ((Block)ObjectRegistry.BARREL_MAIN.get()).getCloneItemStack(levelReader, blockPos, blockState)
         : super.getCloneItemStack(levelReader, blockPos, blockState);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public static BlockPos mainPos(BlockState state, BlockPos pos) {
      BlockPos lower = state.hasProperty(HALF) && state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
      Direction facing = (Direction)state.getValue(FACING);
      Block block = state.getBlock();
      if (block instanceof BigBarrelMainHeadBlock) {
         return lower.relative(facing);
      } else if (block instanceof BigBarrelRightBlock) {
         return lower.relative(facing.getClockWise());
      } else {
         return block instanceof BigBarrelRightHeadBlock ? lower.relative(facing).relative(facing.getClockWise()) : lower;
      }
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (!BigBarrelBlockEntity.canAge(stack)) {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }

      if (!level.isClientSide && level.getBlockEntity(mainPos(state, pos)) instanceof BigBarrelBlockEntity barrel) {
         ItemStack inserted = player.isCreative() ? stack.copy() : stack;
         if (barrel.insert(inserted)) {
            level.playSound(null, pos, SoundEvents.BARREL_CLOSE, SoundSource.BLOCKS, 0.7F, 1.2F);
         }
      }

      return ItemInteractionResult.sidedSuccess(level.isClientSide);
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (player.isShiftKeyDown() && level.getBlockEntity(mainPos(state, pos)) instanceof BigBarrelBlockEntity barrel && !barrel.isEmpty()) {
         if (!level.isClientSide) {
            ItemStack taken = barrel.takeLast();
            if (!player.addItem(taken)) {
               player.drop(taken, false);
            }

            level.playSound(null, pos, SoundEvents.BARREL_OPEN, SoundSource.BLOCKS, 0.7F, 1.2F);
         }

         return InteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return InteractionResult.PASS;
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{FACING});
   }
}
