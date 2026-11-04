package net.countered.terrainslabs.block.customslabs.soilslabs;

import net.countered.terrainslabs.block.customslabs.specialslabs.CustomSlab;
import net.countered.terrainslabs.registries.ModBlocksRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PathSlab extends CustomSlab {
   protected static final VoxelShape BOTTOM_SHAPE_COL = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
   protected static final VoxelShape TOP_SHAPE_COL = Block.box(0.0, 8.0, 0.0, 16.0, 15.0, 16.0);
   protected static final VoxelShape DOUBLE_SHAPE_COL = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

   public PathSlab(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(TYPE, SlabType.BOTTOM)).setValue(WATERLOGGED, false))
            .setValue(GENERATED, false)
      );
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{TYPE, WATERLOGGED, GENERATED});
   }

   public boolean useShapeForLightOcclusion(BlockState state) {
      return true;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      SlabType slabType = (SlabType)state.getValue(TYPE);
      switch (slabType) {
         case DOUBLE:
            return DOUBLE_SHAPE_COL;
         case TOP:
            return TOP_SHAPE_COL;
         default:
            return BOTTOM_SHAPE_COL;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      SlabType slabType = (SlabType)state.getValue(TYPE);
      switch (slabType) {
         case DOUBLE:
            return DOUBLE_SHAPE_COL;
         case TOP:
            return TOP_SHAPE_COL;
         default:
            return BOTTOM_SHAPE_COL;
      }
   }

   public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      SlabType slabType = (SlabType)state.getValue(TYPE);
      switch (slabType) {
         case DOUBLE:
            level.setBlockAndUpdate(pos, (BlockState)((Block)ModBlocksRegistry.DIRT_SLAB.get()).defaultBlockState().setValue(TYPE, SlabType.DOUBLE));
            break;
         case TOP:
            level.setBlockAndUpdate(pos, (BlockState)((Block)ModBlocksRegistry.DIRT_SLAB.get()).defaultBlockState().setValue(TYPE, SlabType.TOP));
            break;
         default:
            level.setBlockAndUpdate(pos, (BlockState)((Block)ModBlocksRegistry.DIRT_SLAB.get()).defaultBlockState().setValue(TYPE, SlabType.BOTTOM));
      }
   }

   public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
      BlockState blockState = level.getBlockState(pos.above());
      return !blockState.isSolid() || blockState.getBlock() instanceof FenceGateBlock;
   }
}
