package net.satisfy.farm_and_charm.core.block;

import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.registry.TagRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.jetbrains.annotations.Nullable;

public class FertilizedFarmlandBlock extends FarmBlock implements SimpleWaterloggedBlock {
   public static final BooleanProperty LOWERED = BooleanProperty.create("lowered");
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape LOWERED_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);

   public FertilizedFarmlandBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(LOWERED, false)).setValue(WATERLOGGED, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{LOWERED, WATERLOGGED});
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return blockState.getValue(LOWERED) ? LOWERED_SHAPE : super.getShape(blockState, blockGetter, blockPos, collisionContext);
   }

   protected ItemInteractionResult useItemOn(
      ItemStack itemStack, BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult
   ) {
      if (blockHitResult.getDirection() == Direction.UP && isPlantable(itemStack) && !canPlantOn(blockState, itemStack)) {
         return ItemInteractionResult.FAIL;
      }

      if (itemStack.is((Item)ObjectRegistry.PITCHFORK.get()) && !(Boolean)blockState.getValue(LOWERED) && level.getBlockState(blockPos.above()).isAir()) {
         if (!level.isClientSide) {
            BlockState lowered = (BlockState)blockState.setValue(LOWERED, true);
            level.setBlock(blockPos, lowered, 3);
            level.playSound(null, blockPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 0.8F);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, Context.of(player, lowered));
            spawnDirtParticles(level, blockPos, blockState);
            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(interactionHand));
            if (hasAdjacentWater(level, blockPos)) {
               flood(level, blockPos, lowered);
            }
         }

         return ItemInteractionResult.sidedSuccess(level.isClientSide);
      } else {
         return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
      }
   }

   public boolean canPlaceLiquid(@Nullable Player player, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, Fluid fluid) {
      return (Boolean)blockState.getValue(LOWERED) && super.canPlaceLiquid(player, blockGetter, blockPos, blockState, fluid);
   }

   public FluidState getFluidState(BlockState blockState) {
      return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
   }

   public BlockState updateShape(
      BlockState blockState, Direction direction, BlockState neighborState, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos neighborPos
   ) {
      if ((Boolean)blockState.getValue(WATERLOGGED)) {
         levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
      }

      return super.updateShape(blockState, direction, neighborState, levelAccessor, blockPos, neighborPos);
   }

   public static boolean canPlantOn(BlockState soilState, ItemStack seedStack) {
      return !soilState.hasProperty(LOWERED) || !(Boolean)soilState.getValue(LOWERED) || seedStack.is(TagRegistry.NEEDS_LOWERED_FARMLAND);
   }

   private static boolean isPlantable(ItemStack itemStack) {
      return itemStack.is(TagRegistry.SEEDS)
         || itemStack.is(TagRegistry.NEEDS_LOWERED_FARMLAND)
         || itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BushBlock;
   }

   public static void spawnDirtParticles(Level level, BlockPos blockPos, BlockState blockState) {
      if (level instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            new BlockParticleOption(ParticleTypes.BLOCK, blockState),
            blockPos.getX() + 0.5,
            blockPos.getY() + 1.0,
            blockPos.getZ() + 0.5,
            20,
            0.3,
            0.1,
            0.3,
            0.1
         );
      }
   }

   private static boolean hasAdjacentWater(Level level, BlockPos blockPos) {
      for (Direction direction : Plane.HORIZONTAL) {
         if (level.getBlockState(blockPos.relative(direction)).is(Blocks.WATER)) {
            return true;
         }
      }

      return false;
   }

   private static void flood(Level level, BlockPos blockPos, BlockState blockState) {
      level.setBlock(blockPos, (BlockState)blockState.setValue(WATERLOGGED, true), 3);
      level.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
   }

   public static void turnToSoil(@Nullable Entity entity, BlockState blockState, Level level, BlockPos blockPos) {
      BlockState blockState2 = pushEntitiesUp(blockState, ((Block)ObjectRegistry.FERTILIZED_SOIL_BLOCK.get()).defaultBlockState(), level, blockPos);
      level.setBlockAndUpdate(blockPos, blockState2);
      level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, Context.of(entity, blockState2));
   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return !this.defaultBlockState().canSurvive(blockPlaceContext.getLevel(), blockPlaceContext.getClickedPos())
         ? ((Block)ObjectRegistry.FERTILIZED_SOIL_BLOCK.get()).defaultBlockState()
         : super.getStateForPlacement(blockPlaceContext);
   }

   public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
   }

   public void randomTick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
      if ((Boolean)blockState.getValue(LOWERED) && !(Boolean)blockState.getValue(WATERLOGGED) && hasAdjacentWater(serverLevel, blockPos)) {
         flood(serverLevel, blockPos, blockState);
         blockState = serverLevel.getBlockState(blockPos);
      }

      if (randomSource.nextFloat() < this.getGrowthChance(serverLevel, blockPos)) {
         this.applyBonemealEffect(serverLevel, blockPos, randomSource);
      }
   }

   public void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
      super.neighborChanged(blockState, level, blockPos, neighborBlock, neighborPos, movedByPiston);
      if (level instanceof ServerLevel serverLevel) {
         if ((Boolean)blockState.getValue(LOWERED) && !(Boolean)blockState.getValue(WATERLOGGED) && hasAdjacentWater(serverLevel, blockPos)) {
            flood(serverLevel, blockPos, blockState);
         } else if (neighborPos.equals(blockPos.above())) {
            BlockState stateAbove = serverLevel.getBlockState(neighborPos);
            if (stateAbove.getBlock() instanceof CropBlock) {
               RandomSource randomSource = serverLevel.random;

               for (int i = 0; i < 10; i++) {
                  double offsetX = blockPos.getX() + 0.5 + (randomSource.nextDouble() - 0.5) * 0.8;
                  double offsetY = blockPos.getY() + 1.0;
                  double offsetZ = blockPos.getZ() + 0.5 + (randomSource.nextDouble() - 0.5) * 0.8;
                  serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), offsetX, offsetY, offsetZ, 1, 0.0, 0.0, 0.0, 0.0);
               }
            }
         }
      }
   }

   private float getGrowthChance(ServerLevel serverLevel, BlockPos blockPos) {
      int lightLevel = serverLevel.getMaxLocalRawBrightness(blockPos.above());
      return lightLevel >= 10 ? 0.055F : 0.05F;
   }

   private void applyBonemealEffect(ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
      if (PlatformHelper.isBonemealEffectEnabled()) {
         BlockPos posAbove = blockPos.above();
         BlockState stateAbove = serverLevel.getBlockState(posAbove);
         if (stateAbove.getBlock() instanceof BonemealableBlock bonemealableBlock && bonemealableBlock.isValidBonemealTarget(serverLevel, posAbove, stateAbove)
            )
          {
            bonemealableBlock.performBonemeal(serverLevel, randomSource, posAbove, stateAbove);
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, posAbove.getX() + 0.5, posAbove.getY() + 1.0, posAbove.getZ() + 0.5, 5, 0.5, 0.5, 0.5, 0.5);
         }

         this.checkAndTurnToSoil(serverLevel, blockPos, serverLevel.getBlockState(blockPos));
      }
   }

   private void checkAndTurnToSoil(ServerLevel serverLevel, BlockPos blockPos, BlockState currentBlockState) {
      if (currentBlockState.is((Block)ObjectRegistry.FERTILIZED_SOIL_BLOCK.get())) {
         turnToSoil(null, currentBlockState, serverLevel, blockPos);
      }
   }

   public void fallOn(Level level, BlockState blockState, BlockPos blockPos, Entity entity, float f) {
   }

   public void appendHoverText(ItemStack itemStack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag tooltipFlag) {
      int earthy = 16767334;
      int gold = 16766720;
      if (Screen.hasShiftDown()) {
         tooltip.add(Component.translatable("tooltip.farm_and_charm.fertilized_farmland.info_0").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(earthy))));
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
