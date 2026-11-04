package net.satisfy.farm_and_charm.core.block;

import com.mojang.serialization.MapCodec;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.FarmAndCharm;
import net.satisfy.farm_and_charm.core.block.entity.MincerBlockEntity;
import net.satisfy.farm_and_charm.core.registry.EntityTypeRegistry;
import net.satisfy.farm_and_charm.core.registry.SoundEventRegistry;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MincerBlock extends BaseEntityBlock {
   public static final MapCodec<MincerBlock> CODEC = simpleCodec(MincerBlock::new);
   public static final int CRANKS_NEEDED = 20;
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.0, 0.1875, 0.9375, 0.0625, 0.8125), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.0625, 0.3125, 0.5625, 0.375, 0.6875), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.375, 0.25, 0.625, 0.8125, 0.75), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.625, 0.4375, 0.3125, 0.9375, 0.75, 0.6875), BooleanOp.OR);
      return Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.8125, 0.3125, 0.5625, 1.0, 0.6875), BooleanOp.OR);
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public MincerBlock(Properties settings) {
      super(settings);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   @NotNull
   protected MapCodec<? extends BaseEntityBlock> codec() {
      return CODEC;
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return true;
   }

   public int getLightBlock(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 0;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection());
   }

   @NotNull
   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootParams.Builder builder) {
      List<ItemStack> dropsOriginal = super.getDrops(state, builder);
      return !dropsOriginal.isEmpty() ? dropsOriginal : Collections.singletonList(new ItemStack(this, 1));
   }

   @NotNull
   public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      if (!(level.getBlockEntity(pos) instanceof MincerBlockEntity mincer)) {
         return InteractionResult.PASS;
      } else {
         ItemStack playerStack = player.getItemInHand(InteractionHand.MAIN_HAND);
         if (player.isShiftKeyDown()) {
            if (!level.isClientSide) {
               boolean removed = false;

               for (int slot = 0; slot < mincer.getContainerSize(); slot++) {
                  ItemStack removedStack = mincer.removeItem(slot, mincer.getItem(slot).getCount());
                  if (!removedStack.isEmpty()) {
                     Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), removedStack);
                     removed = true;
                  }
               }

               if (removed) {
                  level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5F, 0.9F);
               }
            }

            return InteractionResult.SUCCESS;
         } else {
            ItemStack inputStack = mincer.getItem(0);
            int crank = mincer.getCrankTicks();
            if (!playerStack.isEmpty() && crank == 0 && mincer.hasValidRecipe(level, playerStack)) {
               if (!mincer.canPlaceItem(0, playerStack)) {
                  return InteractionResult.sidedSuccess(level.isClientSide());
               }

               if (player.isCreative()) {
                  ItemStack playerStackCopy = playerStack.copy();
                  playerStackCopy.setCount(playerStackCopy.getMaxStackSize());
                  mincer.setItem(0, playerStackCopy);
                  level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 0.8F + level.random.nextFloat() * 0.2F);
                  return InteractionResult.SUCCESS;
               }

               if (inputStack.is(playerStack.getItem())) {
                  int countInPlayerHand = playerStack.getCount();
                  int insertableCount = inputStack.getMaxStackSize() - inputStack.getCount();
                  int countToTakeFromPlayer = Math.min(insertableCount, countInPlayerHand);
                  if (countToTakeFromPlayer > 0) {
                     inputStack.setCount(inputStack.getCount() + countToTakeFromPlayer);
                     mincer.setItem(0, inputStack);
                     playerStack.shrink(countToTakeFromPlayer);
                     if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && playerStack.is(Items.BEEF)) {
                        AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(FarmAndCharm.identifier("main/introduction_mincing"));
                        if (advancement != null) {
                           AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);

                           for (String criterion : progress.getRemainingCriteria()) {
                              serverPlayer.getAdvancements().award(advancement, criterion);
                           }
                        }
                     }
                  }
               } else if (inputStack.isEmpty()) {
                  ItemStack insertedStack = playerStack.copy();
                  mincer.setItem(0, insertedStack);
                  playerStack.shrink(playerStack.getCount());
                  if (!level.isClientSide && player instanceof ServerPlayer serverPlayer && insertedStack.is(Items.BEEF)) {
                     AdvancementHolder advancement = serverPlayer.server.getAdvancements().get(FarmAndCharm.identifier("main/introduction_mincing"));
                     if (advancement != null) {
                        AdvancementProgress progress = serverPlayer.getAdvancements().getOrStartProgress(advancement);

                        for (String criterion : progress.getRemainingCriteria()) {
                           serverPlayer.getAdvancements().award(advancement, criterion);
                        }
                     }
                  }
               }

               level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 0.6F, 0.8F + level.random.nextFloat() * 0.2F);
               return InteractionResult.SUCCESS;
            } else {
               if (level instanceof ServerLevel serverWorld) {
                  for (ItemStack stack : mincer.getItems()) {
                     if (!stack.isEmpty() && mincer.getItem(1) != stack) {
                        ItemParticleOption particleOption = new ItemParticleOption(ParticleTypes.ITEM, stack);
                        serverWorld.sendParticles(particleOption, pos.getX() + 0.5, pos.getY() + 1.1, pos.getZ() + 0.4, 3, 0.2, 0.1, 0.0, 0.1);
                     }
                  }
               }

               if (crank <= 6 && !level.isClientSide) {
                  mincer.crank();
                  level.playSound(
                     null, pos, (SoundEvent)SoundEventRegistry.MINCER_CRANKING.get(), SoundSource.BLOCKS, 0.4F, 0.9F + level.random.nextFloat() * 0.2F
                  );
                  return InteractionResult.SUCCESS;
               } else {
                  return InteractionResult.SUCCESS;
               }
            }
         }
      }
   }

   public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
      VoxelShape shape = world.getBlockState(pos.below()).getShape(world, pos.below());
      Direction direction = Direction.UP;
      return Block.isFaceFull(shape, direction);
   }

   public void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(world, pos)) {
         world.destroyBlock(pos, true);
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
      if (!state.canSurvive(world, pos)) {
         world.scheduleTick(pos, this, 1);
      }

      return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
   }

   public void onPlace(BlockState blockstate, Level world, BlockPos pos, BlockState oldState, boolean moving) {
      super.onPlace(blockstate, world, pos, oldState, moving);
   }

   public MenuProvider getMenuProvider(BlockState state, Level worldIn, BlockPos pos) {
      BlockEntity tileEntity = worldIn.getBlockEntity(pos);
      return tileEntity instanceof MenuProvider ? (MenuProvider)tileEntity : null;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new MincerBlockEntity(pos, state);
   }

   public boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
      super.triggerEvent(state, world, pos, eventID, eventParam);
      BlockEntity blockEntity = world.getBlockEntity(pos);
      return blockEntity != null && blockEntity.triggerEvent(eventID, eventParam);
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (world.getBlockEntity(pos) instanceof MincerBlockEntity be) {
            for (int slot = 0; slot < be.getContainerSize(); slot++) {
               ItemStack stack = be.removeItem(slot, be.getItem(slot).getCount());
               if (!stack.isEmpty()) {
                  Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
               }
            }

            world.updateNeighbourForOutputSignal(pos, this);
         }

         super.onRemove(state, world, pos, newState, isMoving);
      }
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return createTickerHelper(
         type, (BlockEntityType)EntityTypeRegistry.MINCER_BLOCK_ENTITY.get(), (world1, pos, state1, be) -> be.tick(world1, pos, state1, be)
      );
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> list, TooltipFlag tooltipFlag) {
      list.add(Component.translatable("tooltip.farm_and_charm.canbeplaced").withStyle(ChatFormatting.GRAY));
   }
}
