package net.satisfy.candlelight.core.block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.food.FoodBlock;
import net.satisfy.foundation.util.ShapeUtil;
import org.jetbrains.annotations.NotNull;

public class EffectFoodTrayBlock extends FoodBlock {
   private static final Supplier<VoxelShape> voxelShapeSupplier = () -> {
      VoxelShape shape = Shapes.empty();
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.0, 0.1875, 0.75, 0.0625, 0.8125), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.0625, 0.125, 0.8125, 0.1875, 0.875), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.0625, 0.125, 0.25, 0.1875, 0.875), BooleanOp.OR);
      shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.0625, 0.8125, 0.75, 0.1875, 0.875), BooleanOp.OR);
      return Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.0625, 0.125, 0.75, 0.1875, 0.1875), BooleanOp.OR);
   };
   public static final Map<Direction, VoxelShape> SHAPE = (Map<Direction, VoxelShape>)Util.make(new HashMap(), map -> {
      for (Direction direction : Plane.HORIZONTAL.stream().toList()) {
         map.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, voxelShapeSupplier.get()));
      }
   });

   public EffectFoodTrayBlock(Properties settings, int maxBites, FoodProperties foodComponent) {
      super(settings, maxBites, foodComponent);
   }

   @NotNull
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE.get(state.getValue(FACING));
   }
}
