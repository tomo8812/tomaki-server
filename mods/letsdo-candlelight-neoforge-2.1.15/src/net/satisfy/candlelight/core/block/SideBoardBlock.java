package net.satisfy.candlelight.core.block;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.candlelight.core.block.entity.SideBoardBlockEntity;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SideBoardBlock extends ChestBlock {
   public static final Map<Direction, VoxelShape> SHAPES = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      map.put(Direction.NORTH, Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0));
      map.put(Direction.SOUTH, Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0));
      map.put(Direction.WEST, Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0));
      map.put(Direction.EAST, Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0));
   });

   public SideBoardBlock(Properties settings) {
      super(settings, EntityTypeRegistry.SIDEBOARD_BLOCK_ENTITY::get);
   }

   @NotNull
   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new SideBoardBlockEntity(pos, state);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPES.get(state.getValue(FACING));
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return null;
   }
}
