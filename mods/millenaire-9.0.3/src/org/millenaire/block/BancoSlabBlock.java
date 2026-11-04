package org.millenaire.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class BancoSlabBlock extends SlabBlock {
   public static final IntegerProperty PROGRESS = IntegerProperty.create("progress", 0, 3);

   public BancoSlabBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(TYPE, SlabType.BOTTOM)).setValue(WATERLOGGED, false))
            .setValue(PROGRESS, 0)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{PROGRESS});
   }

   public boolean isRandomlyTicking(BlockState state) {
      return true;
   }

   protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
      if (random.nextInt(4) == 0) {
         int progress = (Integer)state.getValue(PROGRESS);
         if (progress < 3) {
            level.setBlock(pos, (BlockState)state.setValue(PROGRESS, progress + 1), 2);
         }
      }
   }

   public boolean canPlaceLiquid(@Nullable Player player, BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
      return false;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      return state == null ? null : (BlockState)state.setValue(WATERLOGGED, false);
   }

   protected FluidState getFluidState(BlockState state) {
      return Fluids.EMPTY.defaultFluidState();
   }
}
