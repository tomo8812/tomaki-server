package net.satisfy.brewery.core.block;

import java.util.Optional;
import net.minecraft.BlockUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.core.block.RopeBlock;
import net.satisfy.farm_and_charm.core.block.crops.ClimbingCropBlock;
import org.jetbrains.annotations.NotNull;

public class HopsCropBodyBlock extends ClimbingCropBlock implements BonemealableBlock {
   public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
   public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

   public HopsCropBodyBlock(Properties properties) {
      super(properties, SHAPE);
      this.registerDefaultState((BlockState)((BlockState)this.defaultBlockState().setValue(AGE, 0)).setValue(SUPPORTED, false));
   }

   protected IntegerProperty getAgeProperty() {
      return AGE;
   }

   protected int getMaxAge() {
      return 4;
   }

   protected ItemLike getRipeItem() {
      return (ItemLike)ObjectRegistry.HOPS.get();
   }

   protected ItemLike getRottenItem() {
      return (ItemLike)ObjectRegistry.HOPS.get();
   }

   protected int getHarvestResetAge(Level level, BlockPos pos, BlockState state) {
      return level.getBlockState(pos.above()).is((Block)ObjectRegistry.HOPS_CROP.get()) ? 2 : 1;
   }

   @NotNull
   public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
      return new ItemStack((ItemLike)ObjectRegistry.HOPS_CROP.get());
   }

   public boolean canBeReplaced(BlockState state, BlockPlaceContext blockPlaceContext) {
      return blockPlaceContext.getPlayer() != null ? false : super.canBeReplaced(state, blockPlaceContext);
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if (dir == Direction.DOWN && !state.canSurvive(level, pos)) {
         level.scheduleTick(pos, this, 1);
      }

      return state;
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      Optional<BlockPos> headOpt = BlockUtil.getTopConnectedBlock(level, pos, state.getBlock(), Direction.UP, (Block)ObjectRegistry.HOPS_CROP.get());
      if (headOpt.isPresent()) {
         BlockPos headPos = headOpt.get();
         BlockState headState = level.getBlockState(headPos);
         BlockPos up = headPos.above();
         BlockState rope = level.getBlockState(up);
         boolean ropeOk = rope.getBlock() instanceof RopeBlock
            && (!rope.hasProperty(RopeBlock.DOWN) || (Boolean)rope.getValue(RopeBlock.DOWN))
            && (!rope.hasProperty(RopeBlock.CENTER_PIECE) || (Boolean)rope.getValue(RopeBlock.CENTER_PIECE))
            && (!rope.hasProperty(RopeBlock.ROPE_KNOT) || !(Boolean)rope.getValue(RopeBlock.ROPE_KNOT))
            && (!rope.hasProperty(RopeBlock.SUPPORTING_ROPE_KNOT) || !(Boolean)rope.getValue(RopeBlock.SUPPORTING_ROPE_KNOT));
         if (headState.hasProperty(SUPPORTED)
            && (Boolean)headState.getValue(SUPPORTED)
            && ropeOk
            && HopsCropHeadBlock.getHeight(headPos, level) < HopsCropHeadBlock.getMaxHeight(level, headPos)) {
            boolean supported = (Boolean)headState.getValue(SUPPORTED);
            BlockState bodyAtHead = (BlockState)((BlockState)((Block)ObjectRegistry.HOPS_CROP_BODY.get())
                  .defaultBlockState()
                  .setValue(AGE, (Integer)headState.getValue(HopsCropHeadBlock.AGE)))
               .setValue(SUPPORTED, supported);
            level.setBlock(headPos, bodyAtHead, 2);
            level.setBlockAndUpdate(
               up,
               (BlockState)((BlockState)((Block)ObjectRegistry.HOPS_CROP.get()).defaultBlockState().setValue(HopsCropHeadBlock.AGE, 0))
                  .setValue(SUPPORTED, supported)
            );
            return;
         }
      }

      if ((Integer)state.getValue(AGE) < this.getMaxAge()) {
         boolean supported = (Boolean)state.getValue(SUPPORTED);
         level.setBlockAndUpdate(pos, (BlockState)((BlockState)state.setValue(AGE, (Integer)state.getValue(AGE) + 1)).setValue(SUPPORTED, supported));
      } else {
         this.dropFruits(level, pos, state);
      }
   }
}
