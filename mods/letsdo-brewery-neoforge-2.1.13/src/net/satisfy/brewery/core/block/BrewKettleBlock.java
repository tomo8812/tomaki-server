package net.satisfy.brewery.core.block;

import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.core.block.entity.BrewstationBlockEntity;
import net.satisfy.brewery.core.block.property.BrewMaterial;
import net.satisfy.brewery.core.block.property.Liquid;
import net.satisfy.brewery.core.item.DrinkBlockItem;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrewKettleBlock extends BrewingstationBlock implements EntityBlock {
   public static final EnumProperty<Liquid> LIQUID = BlockStateRegistry.LIQUID;
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, BrewKettleBlock.voxelShapeSupplier.get()));
      }
   });
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.0, 0.0, 0.125, 0.875, 0.125, 1.0));
      shape = Shapes.or(shape, Shapes.box(0.0, 0.125, 0.0, 1.0, 1.0, 0.125));
      shape = Shapes.or(shape, Shapes.box(0.875, 0.125, 0.125, 1.0, 1.0, 1.0));
      shape = Shapes.or(shape, Shapes.box(0.0, 0.125, 0.125, 0.125, 1.0, 1.0));
      shape = Shapes.or(shape, Shapes.box(0.125, 0.125, 0.9375, 0.875, 1.0, 1.0));
      return Shapes.or(shape, Shapes.box(0.125, 0.5625, 0.09375, 0.875, 0.5625, 0.96875));
   };
   private final BrewMaterial brewMaterial;
   public static final MapCodec<BrewKettleBlock> CODEC = simpleCodec(BrewKettleBlock::new);

   public BrewKettleBlock(Properties properties) {
      this(BrewMaterial.WOOD, properties);
   }

   public BrewKettleBlock(BrewMaterial brewMaterial, Properties properties) {
      super(properties);
      this.brewMaterial = brewMaterial;
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(MATERIAL, brewMaterial)).setValue(LIQUID, Liquid.EMPTY));
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if (interactionHand == InteractionHand.OFF_HAND) {
         return ItemInteractionResult.CONSUME;
      }

      if (level.isClientSide) {
         return ItemInteractionResult.CONSUME;
      }

      if (level.getBlockEntity(blockPos) instanceof BrewstationBlockEntity brewKettleEntity) {
         if (itemStack.isEmpty()) {
            ItemStack returnStack = brewKettleEntity.removeIngredient();
            if (returnStack != null) {
               player.addItem(returnStack);
               level.playSound(null, blockPos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1.0F, 1.0F);
               level.sendBlockUpdated(blockPos, blockState, blockState, 2);
               return ItemInteractionResult.SUCCESS;
            }

            return ItemInteractionResult.CONSUME;
         }

         if (itemStack.getItem() == ((Block)ObjectRegistry.BEER_MUG.get()).asItem() || itemStack.is(Items.GLASS_BOTTLE)) {
            if (blockState.getValue(LIQUID) == Liquid.BEER) {
               ItemStack preview = brewKettleEntity.peekBeer();
               if (preview != null && DrinkBlockItem.getContainer(preview) == itemStack.getItem()) {
                  ItemStack beerStack = brewKettleEntity.getBeer();
                  if (!player.addItem(beerStack)) {
                     player.drop(beerStack, false);
                  }

                  if (!player.isCreative()) {
                     itemStack.shrink(1);
                  }

                  level.playSound(null, blockPos, SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                  level.sendBlockUpdated(blockPos, blockState, blockState, 2);
                  return ItemInteractionResult.SUCCESS;
               }

               return ItemInteractionResult.CONSUME;
            }

            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
         }

         if (itemStack.getItem() == Items.WATER_BUCKET) {
            if (blockState.getValue(LIQUID) != Liquid.EMPTY && blockState.getValue(LIQUID) != Liquid.DRAINED) {
               return ItemInteractionResult.CONSUME;
            }

            level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(LIQUID, Liquid.FILLED));
            level.playSound(null, blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.isCreative()) {
               player.setItemInHand(interactionHand, new ItemStack(Items.BUCKET));
            }

            return ItemInteractionResult.SUCCESS;
         }

         if (itemStack.getItem() == Items.BUCKET) {
            Liquid liquid = (Liquid)blockState.getValue(LIQUID);
            if (liquid != Liquid.FILLED && liquid != Liquid.OVERFLOWING) {
               return ItemInteractionResult.CONSUME;
            }

            level.setBlockAndUpdate(blockPos, (BlockState)blockState.setValue(LIQUID, liquid == Liquid.OVERFLOWING ? Liquid.FILLED : Liquid.EMPTY));
            level.playSound(null, blockPos, SoundEvents.BUCKET_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
            if (!player.isCreative()) {
               itemStack.shrink(1);
               ItemStack filledBucket = new ItemStack(Items.WATER_BUCKET);
               if (!player.addItem(filledBucket)) {
                  player.drop(filledBucket, false);
               }
            }

            return ItemInteractionResult.SUCCESS;
         }

         if (blockState.getValue(LIQUID) != Liquid.BEER) {
            ItemStack insertStack = player.isCreative() ? itemStack.copy() : itemStack;
            if (player.isCreative()) {
               insertStack.setCount(1);
            }

            ItemInteractionResult interactionResult = brewKettleEntity.addIngredient(insertStack);
            if (interactionResult == ItemInteractionResult.SUCCESS) {
               level.playSound(null, blockPos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.PLAYERS, 1.0F, 1.0F);
               level.sendBlockUpdated(blockPos, blockState, blockState, 2);
            }

            return interactionResult;
         }
      }

      return super.useItemOn(itemStack, blockState, level, blockPos, player, interactionHand, blockHitResult);
   }

   public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
      if (blockState.getValue(LIQUID) == Liquid.OVERFLOWING) {
         double x = blockPos.getX() + 0.5;
         double y = blockPos.getY() + 0.95;
         double z = blockPos.getZ() + 0.5;
         if (randomSource.nextDouble() < 0.02) {
            level.playLocalSound(x, y, z, (SoundEvent)SoundEventRegistry.BREWSTATION_KETTLE.get(), SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         double offset = 0.2;
         double offsetX = randomSource.nextDouble() * offset - offset / 2.0;
         double offsetY = randomSource.nextDouble() * 0.1;
         double offsetZ = randomSource.nextDouble() * offset - offset / 2.0;
         level.addParticle(ParticleTypes.BUBBLE, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0);
         level.addParticle(ParticleTypes.BUBBLE_POP, x + offsetX, y + offsetY, z + offsetZ, 0.0, 0.0, 0.0);
      }
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
      if (state.getBlock() != newState.getBlock()) {
         if (world.getBlockEntity(pos) instanceof BrewstationBlockEntity brewstationEntity) {
            Containers.dropContents(world, pos, brewstationEntity);
            world.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, world, pos, newState, moved);
      }
   }

   @NotNull
   @Override
   public ItemStack getCloneItemStack(LevelReader getter, BlockPos pos, BlockState state) {
      BrewMaterial material = (BrewMaterial)state.getValue(MATERIAL);

      return switch (material) {
         case COPPER -> new ItemStack((ItemLike)ObjectRegistry.COPPER_BREWINGSTATION.get());
         case WOOD -> new ItemStack((ItemLike)ObjectRegistry.WOODEN_BREWINGSTATION.get());
         case NETHERITE -> new ItemStack((ItemLike)ObjectRegistry.NETHERITE_BREWINGSTATION.get());
      };
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      Level level = blockPlaceContext.getLevel();
      BlockPos mainPos = blockPlaceContext.getClickedPos();
      BlockState blockState = super.getStateForPlacement(blockPlaceContext);
      if (blockState == null) {
         return null;
      }

      Direction facing = (Direction)blockState.getValue(FACING);
      BlockPos backPos = mainPos.relative(facing.getOpposite());
      BlockPos sidePos = mainPos.relative(facing.getCounterClockWise());
      BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
      BlockPos topPos = diagonalPos.above();
      boolean placeable = this.canPlace(level, backPos, sidePos, diagonalPos, topPos, mainPos.above());
      return placeable ? blockState : null;
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
      if (!level.isClientSide) {
         Direction facing = (Direction)blockState.getValue(FACING);
         BlockPos backPos = blockPos.relative(facing.getOpposite());
         BlockPos sidePos = blockPos.relative(facing.getCounterClockWise());
         BlockPos diagonalPos = sidePos.relative(facing.getOpposite());
         BlockPos topPos = diagonalPos.above();
         BlockPos kettleTopPos = blockPos.above();
         if (this.canPlace(level, backPos, sidePos, diagonalPos, topPos, kettleTopPos)) {
            level.setBlock(
               backPos,
               (BlockState)((BlockState)((Block)ObjectRegistry.BREW_TIMER.get()).defaultBlockState().setValue(FACING, facing))
                  .setValue(MATERIAL, this.brewMaterial),
               3
            );
            level.setBlock(
               sidePos,
               (BlockState)((BlockState)((Block)ObjectRegistry.BREW_WHISTLE.get()).defaultBlockState().setValue(FACING, facing))
                  .setValue(MATERIAL, this.brewMaterial),
               3
            );
            level.setBlock(
               diagonalPos,
               (BlockState)((BlockState)((Block)ObjectRegistry.BREW_OVEN.get()).defaultBlockState().setValue(FACING, facing))
                  .setValue(MATERIAL, this.brewMaterial),
               3
            );
            level.setBlock(
               kettleTopPos,
               (BlockState)((BlockState)((Block)ObjectRegistry.BREW_KETTLE_TOP.get()).defaultBlockState().setValue(FACING, facing))
                  .setValue(MATERIAL, this.brewMaterial),
               3
            );
            if (level.getBlockEntity(blockPos) instanceof BrewstationBlockEntity brewKettleEntity) {
               brewKettleEntity.setComponents(blockPos, backPos, sidePos, diagonalPos, kettleTopPos);
            }
         }
      }
   }

   private boolean canPlace(Level level, BlockPos... blockPoses) {
      for (BlockPos blockPos : blockPoses) {
         if (!level.getBlockState(blockPos).isAir()) {
            return false;
         }
      }

      return true;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
      return new BrewstationBlockEntity(blockPos, blockState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return (lvl, pos, st, be) -> {
         if (be instanceof BrewstationBlockEntity kettle) {
            kettle.tick(lvl, pos, st, kettle);
         }
      };
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LIQUID});
   }

   @Override
   protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
      return CODEC;
   }
}
