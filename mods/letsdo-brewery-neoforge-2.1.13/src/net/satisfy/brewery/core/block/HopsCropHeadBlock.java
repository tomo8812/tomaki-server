package net.satisfy.brewery.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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

public class HopsCropHeadBlock extends ClimbingCropBlock implements BonemealableBlock {
   protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 16.0, 15.0);
   public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

   public HopsCropHeadBlock(Properties properties) {
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

   public static int getMaxHeight(LevelAccessor level, BlockPos pos) {
      BlockState self = level.getBlockState(pos);
      boolean supportedFlag = self.hasProperty(SUPPORTED) && (Boolean)self.getValue(SUPPORTED);
      boolean ropeAbove = level.getBlockState(pos.above()).getBlock() instanceof RopeBlock || level.getBlockState(pos.above(2)).getBlock() instanceof RopeBlock;
      return !supportedFlag && !ropeAbove ? 2 : 4;
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      super.tick(state, level, pos, random);
      if (getHeight(pos, level) > getMaxHeight(level, pos)) {
         level.destroyBlock(pos, true);
      }
   }

   public boolean isRandomlyTicking(BlockState state) {
      return true;
   }

   public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      super.randomTick(state, level, pos, random);
      if (level.getRawBrightness(pos, 0) >= 9 && random.nextFloat() < 0.2F) {
         BlockPos up = pos.above();
         BlockState rope = level.getBlockState(up);
         boolean ropeOk = rope.getBlock() instanceof RopeBlock
            && (!rope.hasProperty(RopeBlock.DOWN) || (Boolean)rope.getValue(RopeBlock.DOWN))
            && (!rope.hasProperty(RopeBlock.CENTER_PIECE) || (Boolean)rope.getValue(RopeBlock.CENTER_PIECE))
            && (!rope.hasProperty(RopeBlock.ROPE_KNOT) || !(Boolean)rope.getValue(RopeBlock.ROPE_KNOT))
            && (!rope.hasProperty(RopeBlock.SUPPORTING_ROPE_KNOT) || !(Boolean)rope.getValue(RopeBlock.SUPPORTING_ROPE_KNOT));
         if ((Boolean)state.getValue(SUPPORTED) && ropeOk && getHeight(pos, level) < getMaxHeight(level, pos)) {
            boolean supported = (Boolean)state.getValue(SUPPORTED);
            BlockState body = (BlockState)((BlockState)((Block)ObjectRegistry.HOPS_CROP_BODY.get())
                  .defaultBlockState()
                  .setValue(HopsCropBodyBlock.AGE, (Integer)state.getValue(AGE)))
               .setValue(SUPPORTED, supported);
            level.setBlock(pos, body, 2);
            level.setBlockAndUpdate(up, (BlockState)((BlockState)this.defaultBlockState().setValue(AGE, 0)).setValue(SUPPORTED, supported));
            return;
         }

         if ((Integer)state.getValue(AGE) < this.getMaxAge()) {
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(AGE, (Integer)state.getValue(AGE) + 1));
         }
      }
   }

   public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
      BlockPos up = pos.above();
      BlockState rope = level.getBlockState(up);
      boolean ropeOk = rope.getBlock() instanceof RopeBlock
         && (!rope.hasProperty(RopeBlock.DOWN) || (Boolean)rope.getValue(RopeBlock.DOWN))
         && (!rope.hasProperty(RopeBlock.CENTER_PIECE) || (Boolean)rope.getValue(RopeBlock.CENTER_PIECE))
         && (!rope.hasProperty(RopeBlock.ROPE_KNOT) || !(Boolean)rope.getValue(RopeBlock.ROPE_KNOT))
         && (!rope.hasProperty(RopeBlock.SUPPORTING_ROPE_KNOT) || !(Boolean)rope.getValue(RopeBlock.SUPPORTING_ROPE_KNOT));
      if ((Boolean)state.getValue(SUPPORTED) && ropeOk && getHeight(pos, level) < getMaxHeight(level, pos)) {
         boolean supported = (Boolean)state.getValue(SUPPORTED);
         BlockState body = (BlockState)((BlockState)((Block)ObjectRegistry.HOPS_CROP_BODY.get())
               .defaultBlockState()
               .setValue(HopsCropBodyBlock.AGE, (Integer)state.getValue(AGE)))
            .setValue(SUPPORTED, supported);
         level.setBlock(pos, body, 2);
         level.setBlockAndUpdate(up, (BlockState)((BlockState)this.defaultBlockState().setValue(AGE, 0)).setValue(SUPPORTED, supported));
      } else {
         if ((Integer)state.getValue(AGE) < this.getMaxAge()) {
            level.setBlockAndUpdate(pos, (BlockState)state.setValue(AGE, (Integer)state.getValue(AGE) + 1));
         } else {
            this.dropFruits(level, pos, state);
         }
      }
   }

   @NotNull
   public BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
      if (dir == Direction.DOWN && !state.canSurvive(level, pos)) {
         level.scheduleTick(pos, this, 1);
      }

      return state;
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockState belowState = level.getBlockState(pos.below());
      return belowState.is(this) ? false : super.canSurvive(state, level, pos);
   }

   public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
      return true;
   }

   public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
      return true;
   }
}
