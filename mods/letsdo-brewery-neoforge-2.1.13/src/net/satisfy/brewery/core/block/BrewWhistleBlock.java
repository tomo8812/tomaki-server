package net.satisfy.brewery.core.block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.brewery.core.block.entity.BrewWhistleBlockEntity;
import net.satisfy.brewery.core.block.property.BrewMaterial;
import net.satisfy.brewery.core.registry.BlockStateRegistry;
import net.satisfy.brewery.core.registry.SoundEventRegistry;
import net.satisfy.foundation.registry.FoundationParticles;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BrewWhistleBlock extends BrewingstationBlock implements EntityBlock {
   public static final BooleanProperty WHISTLE = BlockStateRegistry.WHISTLE;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   public static final Map<Direction, VoxelShape> BOTTOM_SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, BrewWhistleBlock.bottomVoxelShapeSupplier.get()));
      }
   });
   public static final Map<Direction, VoxelShape> TOP_SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, BrewWhistleBlock.topVoxelShapeSupplier.get()));
      }
   });
   private static final Supplier<VoxelShape> bottomVoxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.0, 0.125, 0.0, 1.0, 1.0, 0.125));
      shape = Shapes.or(shape, Shapes.box(0.0, 0.125, 0.125, 0.125, 1.0, 1.0));
      shape = Shapes.or(shape, Shapes.box(0.125, 0.0, 0.125, 1.0, 0.125, 1.0));
      return Shapes.or(shape, Shapes.box(0.125, 0.9375, 0.125, 1.0, 1.0, 1.0));
   };
   private static final Supplier<VoxelShape> topVoxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.or(shape, Shapes.box(0.1875, 0.0, 0.25, 0.4375, 1.0, 0.5));
      shape = Shapes.or(shape, Shapes.box(0.125, 0.5, 0.1875, 0.5, 0.5625, 0.5625));
      shape = Shapes.or(shape, Shapes.box(0.125, 0.875, 0.1875, 0.5, 0.9375, 0.5625));
      shape = Shapes.or(shape, Shapes.box(0.125, 0.5, 0.5625, 0.5, 0.75, 0.625));
      return Shapes.or(shape, Shapes.box(0.15625, 0.59375, 0.21875, 0.46875, 0.84375, 0.53125));
   };

   public BrewWhistleBlock(Properties properties) {
      super(properties);
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(MATERIAL, BrewMaterial.WOOD)).setValue(WHISTLE, false))
            .setValue(HALF, DoubleBlockHalf.LOWER)
      );
   }

   public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
      if (blockState.getValue(HALF) == DoubleBlockHalf.LOWER) {
         level.setBlockAndUpdate(blockPos.above(), (BlockState)blockState.setValue(HALF, DoubleBlockHalf.UPPER));
      }
   }

   @NotNull
   public BlockState updateShape(
      BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2
   ) {
      DoubleBlockHalf doubleBlockHalf = (DoubleBlockHalf)blockState.getValue(HALF);
      if (direction.getAxis() == Axis.Y && doubleBlockHalf == DoubleBlockHalf.LOWER == (direction == Direction.UP)) {
         return blockState2.is(this) && blockState2.getValue(HALF) != doubleBlockHalf
            ? (BlockState)blockState.setValue(FACING, (Direction)blockState2.getValue(FACING))
            : Blocks.AIR.defaultBlockState();
      } else {
         return doubleBlockHalf == DoubleBlockHalf.LOWER && direction == Direction.DOWN && !blockState.canSurvive(levelAccessor, blockPos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(blockState, direction, blockState2, levelAccessor, blockPos, blockPos2);
      }
   }

   @NotNull
   @Override
   public BlockState playerWillDestroy(Level level, BlockPos blockPos, BlockState blockState, Player player) {
      if (((DoubleBlockHalf)blockState.getValue(HALF)).equals(DoubleBlockHalf.UPPER)) {
         blockPos = blockPos.below();
      }

      return super.playerWillDestroy(level, blockPos, blockState, player);
   }

   @NotNull
   @Override
   public ItemStack getCloneItemStack(LevelReader blockGetter, BlockPos blockPos, BlockState blockState) {
      if (((DoubleBlockHalf)blockState.getValue(HALF)).equals(DoubleBlockHalf.UPPER)) {
         blockPos = blockPos.below();
      }

      return super.getCloneItemStack(blockGetter, blockPos, blockState);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      Map<Direction, VoxelShape> shapeMap = state.getValue(HALF) == DoubleBlockHalf.LOWER ? BOTTOM_SHAPE : TOP_SHAPE;
      return shapeMap.get(state.getValue(FACING));
   }

   public static boolean isVibrating(BlockState state) {
      return (Boolean)state.getValue(WHISTLE) && state.getValue(HALF) == DoubleBlockHalf.UPPER;
   }

   @NotNull
   protected RenderShape getRenderShape(BlockState state) {
      return isVibrating(state) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(HALF) == DoubleBlockHalf.UPPER ? new BrewWhistleBlockEntity(pos, state) : null;
   }

   @Override
   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{WHISTLE, HALF});
   }

   public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
      if ((Boolean)state.getValue(WHISTLE)) {
         if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            Direction direction = (Direction)state.getValue(FACING);
            double offsetX = 0.5 + direction.getStepX() * 0.6;
            double offsetY = 0.8;
            double offsetZ = 0.5 + direction.getStepZ() * 0.6;
            double x = pos.getX() + offsetX;
            double y = pos.getY() + offsetY;
            double z = pos.getZ() + offsetZ;
            double speedX = direction.getStepX() * 0.1 + (rand.nextFloat() - 0.5) * 0.05;
            double speedY = 0.5;
            double speedZ = direction.getStepZ() * 0.1 + (rand.nextFloat() - 0.5) * 0.05;

            for (int i = 0; i < 3; i++) {
               world.addParticle(
                  (ParticleOptions)FoundationParticles.SOUP_STEAM.get(),
                  x + (rand.nextDouble() - 0.5) * 0.1,
                  y + rand.nextDouble() * 0.1,
                  z + (rand.nextDouble() - 0.5) * 0.1,
                  speedX * 4.0,
                  speedY,
                  speedZ * 4.0
               );
            }
         }
      }
   }

   public static void puff(ServerLevel level, BlockPos pos) {
      BlockState state = level.getBlockState(pos);
      if (state.getBlock() instanceof BrewWhistleBlock) {
         BlockPos top = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos.above() : pos;
         Direction direction = (Direction)state.getValue(FACING);
         double x = top.getX() + 0.5 + direction.getStepX() * 0.6;
         double y = top.getY() + 0.8;
         double z = top.getZ() + 0.5 + direction.getStepZ() * 0.6;
         level.sendParticles((SimpleParticleType)FoundationParticles.SOUP_STEAM.get(), x, y, z, 14, 0.08, 0.1, 0.08, 0.02);
         level.playSound(null, top, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.6F, 1.4F);
      }
   }

   public static void playSounds(Level level, BlockPos pos) {
      long time = level.getGameTime();
      if (time % 60L == 0L) {
         level.playSound(null, pos, (SoundEvent)SoundEventRegistry.BREWSTATION_WHISTLE.get(), SoundSource.BLOCKS, 2.5F, 1.0F);
      }

      if (time % 20L == 0L) {
         level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.3F, 0.6F + level.random.nextFloat() * 0.2F);
      }
   }
}
