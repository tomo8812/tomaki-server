package net.satisfy.herbalbrews.core.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.satisfy.herbalbrews.core.blocks.entity.TeaLeafBlockEntity;

public class TeaLeafBlock extends Block implements EntityBlock {
   public static final IntegerProperty DRYING = IntegerProperty.create("drying", 0, 4);

   public TeaLeafBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(DRYING, 0));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{DRYING});
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new TeaLeafBlockEntity(pos, state);
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return !level.isClientSide ? (level1, blockPos, blockState, blockEntity) -> {
         if (blockEntity instanceof TeaLeafBlockEntity) {
            ((TeaLeafBlockEntity)blockEntity).tick(level1, blockPos, blockState, (TeaLeafBlockEntity)blockEntity);
         }
      } : null;
   }

   private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> checkType(
      BlockEntityType<A> givenType, BlockEntityType<E> expectedType, BlockEntityTicker<? super E> ticker
   ) {
      return expectedType == givenType ? ticker : null;
   }

   public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
      super.animateTick(state, level, pos, random);
      if (random.nextInt(10) == 0) {
         level.addParticle(ParticleTypes.COMPOSTER, pos.getX() + random.nextFloat(), pos.getY() + 1.1, pos.getZ() + random.nextFloat(), 0.0, 0.0, 0.0);
      }
   }
}
