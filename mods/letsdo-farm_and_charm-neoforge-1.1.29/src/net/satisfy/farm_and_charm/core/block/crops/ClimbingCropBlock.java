package net.satisfy.farm_and_charm.core.block.crops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.farm_and_charm.core.block.RopeBlock;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.NotNull;

public abstract class ClimbingCropBlock extends Block {
   public static final BooleanProperty SUPPORTED = BooleanProperty.create("supported");
   protected final VoxelShape shape;

   protected ClimbingCropBlock(Properties properties, VoxelShape shape) {
      super(properties);
      this.shape = shape;
   }

   protected abstract IntegerProperty getAgeProperty();

   protected abstract int getMaxAge();

   protected abstract ItemLike getRipeItem();

   protected abstract ItemLike getRottenItem();

   protected float getRottenChance() {
      return 0.05F;
   }

   protected int getHarvestResetAge(Level level, BlockPos pos, BlockState state) {
      return 1;
   }

   protected static boolean isRopeAbove(LevelAccessor level, BlockPos pos) {
      BlockPos up = pos.above();
      BlockState s = level.getBlockState(up);
      return s.getBlock() instanceof RopeBlock ? true : s.isFaceSturdy(level, up, Direction.DOWN);
   }

   protected static int getHeight(BlockPos pos, LevelAccessor level) {
      int h = 0;

      while (level.getBlockState(pos.below(h)).getBlock() instanceof ClimbingCropBlock) {
         h++;
      }

      return h;
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return this.shape;
   }

   public BlockState getStateForPlacement(BlockPlaceContext ctx) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), 0)).setValue(SUPPORTED, false);
   }

   @NotNull
   public BlockState getStateForAge(int age) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(this.getAgeProperty(), Math.min(age, this.getMaxAge()))).setValue(SUPPORTED, false);
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockPos below = pos.below();
      BlockState belowState = level.getBlockState(below);
      return this.mayPlaceOn(belowState) || belowState.getBlock() instanceof ClimbingCropBlock;
   }

   protected boolean mayPlaceOn(BlockState state) {
      return state.getBlock() instanceof FarmBlock;
   }

   protected boolean canGrow(BlockState state) {
      return (Integer)state.getValue(this.getAgeProperty()) < this.getMaxAge();
   }

   @NotNull
   protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
      InteractionHand hand = player.getUsedItemHand();
      if (player.getItemInHand(hand).is(Items.BONE_MEAL)) {
         return InteractionResult.PASS;
      } else {
         int age = (Integer)state.getValue(this.getAgeProperty());
         if (age == this.getMaxAge()) {
            this.dropFruits(level, pos, state);
            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return super.useWithoutItem(state, level, pos, player, hit);
         }
      }
   }

   @NotNull
   protected ItemInteractionResult useItemOn(
      ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit
   ) {
      if (stack.is(((Item)ObjectRegistry.ROPE.get()).asItem()) && !(Boolean)state.getValue(SUPPORTED)) {
         if (!level.isClientSide) {
            level.setBlock(pos, (BlockState)state.setValue(SUPPORTED, true), 2);
            if (!player.getAbilities().instabuild) {
               stack.shrink(1);
            }

            level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.8F, 1.0F);
         }

         return ItemInteractionResult.SUCCESS;
      } else {
         return super.useItemOn(stack, state, level, pos, player, hand, hit);
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock() && !level.isClientSide) {
         BlockPos p = pos.above();

         for (BlockState s = level.getBlockState(p); s.getBlock() instanceof ClimbingCropBlock; s = level.getBlockState(p)) {
            level.destroyBlock(p, true);
            p = p.above();
         }
      }

      super.onRemove(state, level, pos, newState, isMoving);
   }

   protected void dropFruits(Level level, BlockPos blockPos, BlockState blockState) {
      int age = (Integer)blockState.getValue(this.getAgeProperty());
      int amount = level.getRandom().nextInt(2) + (age >= this.getMaxAge() ? 1 : 0);
      ItemStack drop = level.getRandom().nextFloat() < this.getRottenChance()
         ? new ItemStack(this.getRottenItem(), 1)
         : new ItemStack(this.getRipeItem(), amount);
      popResource(level, blockPos, drop);
      level.playSound(null, blockPos, SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, SoundSource.BLOCKS, 1.0F, 0.8F + level.random.nextFloat() * 0.4F);
      int resetAge = this.getHarvestResetAge(level, blockPos, blockState);
      level.setBlock(blockPos, (BlockState)blockState.setValue(this.getAgeProperty(), resetAge), 2);
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (!state.canSurvive(level, pos)) {
         level.destroyBlock(pos, true);
      }
   }

   public boolean isRandomlyTicking(BlockState state) {
      return this.canGrow(state);
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (level.getRawBrightness(pos, 0) >= 9) {
         int age = (Integer)state.getValue(this.getAgeProperty());
         if (age < this.getMaxAge() && random.nextFloat() < 0.2F) {
            boolean supported = (Boolean)state.getValue(SUPPORTED);
            level.setBlock(pos, (BlockState)((BlockState)state.setValue(this.getAgeProperty(), age + 1)).setValue(SUPPORTED, supported), 2);
         }
      }
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
      return state.getFluidState().isEmpty();
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{this.getAgeProperty(), SUPPORTED});
   }
}
