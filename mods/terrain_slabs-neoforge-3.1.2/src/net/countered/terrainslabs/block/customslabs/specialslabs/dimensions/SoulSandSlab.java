package net.countered.terrainslabs.block.customslabs.specialslabs.dimensions;

import net.countered.terrainslabs.block.customslabs.specialslabs.CustomSlab;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulSandSlab extends CustomSlab {
   protected static final VoxelShape BOTTOM_SHAPE_COL = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
   protected static final VoxelShape DOUBLE_SHAPE_COL = Block.box(0.0, 0.0, 0.0, 16.0, 14.0, 16.0);
   protected static final VoxelShape TOP_SHAPE_COL = Block.box(0.0, 8.0, 0.0, 16.0, 14.0, 16.0);
   protected static final VoxelShape BOTTOM_SHAPE_OUT = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   protected static final VoxelShape TOP_SHAPE_OUT = Block.box(0.0, 8.0, 0.0, 16.0, 16.0, 16.0);

   public SoulSandSlab(Properties properties) {
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

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      SlabType slabType = (SlabType)state.getValue(TYPE);
      switch (slabType) {
         case DOUBLE:
            return Shapes.block();
         case TOP:
            return TOP_SHAPE_OUT;
         default:
            return BOTTOM_SHAPE_OUT;
      }
   }

   public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos) {
      SlabType slabType = (SlabType)state.getValue(TYPE);
      switch (slabType) {
         case DOUBLE:
            return Shapes.block();
         case TOP:
            return TOP_SHAPE_OUT;
         default:
            return BOTTOM_SHAPE_OUT;
      }
   }

   public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
      SlabType slabType = (SlabType)state.getValue(TYPE);
      switch (slabType) {
         case DOUBLE:
            return 0.2F;
         default:
            return 1.0F;
      }
   }
}
