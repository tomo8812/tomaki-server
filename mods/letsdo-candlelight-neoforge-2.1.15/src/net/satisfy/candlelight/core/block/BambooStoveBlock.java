package net.satisfy.candlelight.core.block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.Direction.Plane;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class BambooStoveBlock extends StoveBlock {
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0, 0.0, 0.8125, 0.1875, 0.125, 1.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0, 0.0, 0.0, 0.1875, 0.125, 0.1875), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 0.0, 0.8125, 1.0, 0.125, 1.0), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 0.0, 0.0, 1.0, 0.125, 0.1875), BooleanOp.OR);
      return Shapes.joinUnoptimized(shape, Shapes.box(0.0, 0.125, 0.0, 1.0, 1.0, 1.0), BooleanOp.OR);
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public BambooStoveBlock(Properties settings) {
      super(settings);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }

   public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
      if ((Boolean)state.getValue(LIT)) {
         double d = pos.getX() + 0.5;
         double e = pos.getY() + 0.24;
         double f = pos.getZ() + 0.5;
         if (random.nextDouble() < 0.1) {
            world.playLocalSound(d, e, f, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         }

         world.playLocalSound(d, e, f, SoundEvents.SMOKER_SMOKE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
         world.playLocalSound(d, e, f, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);

         for (Direction direction : Direction.values()) {
            if (direction != Direction.DOWN && direction != state.getValue(FACING)) {
               Axis axis = direction.getAxis();
               double h = random.nextDouble() * 0.6 - 0.3;
               double i = axis == Axis.X ? direction.getStepX() * 0.52 : h;
               double j = random.nextDouble() * 6.0 / 16.0;
               double k = axis == Axis.Z ? direction.getStepZ() * 0.52 : h;
               world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0);
               world.addParticle(ParticleTypes.FLAME, d + i, e + j, f + k, 0.0, 0.0, 0.0);
            }
         }

         double particleHeight = pos.getY() + 0.5 + 1.0;
         world.addParticle(ParticleTypes.SMOKE, d, particleHeight, f, 0.0, 0.0, 0.0);
      }
   }
}
