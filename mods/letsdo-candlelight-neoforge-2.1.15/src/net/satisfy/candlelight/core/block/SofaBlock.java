package net.satisfy.candlelight.core.block;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.block.LineConnectingType;
import net.satisfy.foundation.util.ShapeUtil;

public class SofaBlock extends net.satisfy.foundation.block.SofaBlock {
   private static final VoxelShape SINGLE = Shapes.or(
      Shapes.empty(),
      new VoxelShape[]{
         Block.box(12.0, 0.0, 2.0, 15.0, 3.0, 5.0),
         Block.box(12.0, 0.0, 11.0, 15.0, 3.0, 14.0),
         Block.box(1.0, 0.0, 2.0, 4.0, 3.0, 5.0),
         Block.box(1.0, 0.0, 11.0, 4.0, 3.0, 14.0),
         Block.box(0.0, 3.0, 15.0, 16.0, 16.0, 16.0),
         Block.box(2.0, 3.0, 0.0, 14.0, 4.0, 15.0),
         Block.box(0.0, 3.001, 1.0, 2.0, 13.001, 15.0),
         Block.box(2.0, 4.0, 0.0, 14.0, 7.0, 15.0),
         Block.box(14.0, 3.001, 1.0, 16.0, 13.001, 15.0),
         Block.box(0.0, 13.0, 12.0, 16.0, 16.0, 15.0),
         Block.box(2.0, 7.0, 12.0, 14.0, 13.0, 15.0)
      }
   );
   private static final VoxelShape END = Shapes.or(
      Shapes.empty(),
      new VoxelShape[]{
         Block.box(12.0, 0.0, 2.0, 15.0, 3.0, 5.0),
         Block.box(12.0, 0.0, 11.0, 15.0, 3.0, 14.0),
         Block.box(0.0, 3.0, 15.0, 16.0, 16.0, 16.0),
         Block.box(0.0, 3.0, 0.0, 14.0, 4.0, 15.0),
         Block.box(0.0, 4.0, 0.0, 14.0, 7.0, 15.0),
         Block.box(14.0, 3.001, 1.0, 16.0, 13.001, 15.0),
         Block.box(0.0, 13.0, 12.0, 16.0, 16.0, 15.0),
         Block.box(0.0, 7.0, 12.0, 14.0, 13.0, 15.0)
      }
   );
   private static final VoxelShape END_MIRRORED = Shapes.or(
      Shapes.empty(),
      new VoxelShape[]{
         Block.box(1.0, 0.0, 2.0, 4.0, 3.0, 5.0),
         Block.box(1.0, 0.0, 11.0, 4.0, 3.0, 14.0),
         Block.box(0.0, 3.0, 15.0, 16.0, 16.0, 16.0),
         Block.box(2.0, 3.0, 0.0, 16.0, 4.0, 15.0),
         Block.box(2.0, 4.0, 0.0, 16.0, 7.0, 15.0),
         Block.box(0.0, 3.001, 1.0, 2.0, 13.001, 15.0),
         Block.box(0.0, 13.0, 12.0, 16.0, 16.0, 15.0),
         Block.box(2.0, 7.0, 12.0, 16.0, 13.0, 15.0)
      }
   );
   private static final VoxelShape MIDDLE = Shapes.or(
      Shapes.empty(),
      new VoxelShape[]{
         Block.box(0.0, 3.0, 15.0, 16.0, 16.0, 16.0),
         Block.box(0.0, 3.0, 0.0, 16.0, 4.0, 15.0),
         Block.box(0.0, 4.0, 0.0, 16.0, 7.0, 15.0),
         Block.box(0.0, 7.0, 12.0, 16.0, 16.0, 15.0)
      }
   );
   private static final Map<LineConnectingType, Map<Direction, VoxelShape>> SHAPES = (Map<LineConnectingType, Map<Direction, VoxelShape>>)Util.make(
      new EnumMap(LineConnectingType.class), map -> {
         for (LineConnectingType type : LineConnectingType.values()) {
            VoxelShape base = switch (type) {
               case MIDDLE -> MIDDLE;
               case LEFT -> END_MIRRORED;
               case RIGHT -> END;
               default -> SINGLE;
            };
            Map<Direction, VoxelShape> rotated = new EnumMap<>(Direction.class);

            for (Direction direction : Plane.HORIZONTAL) {
               rotated.put(direction, ShapeUtil.rotateShape(Direction.NORTH, direction, base));
            }

            map.put(type, rotated);
         }
      }
   );

   public SofaBlock(Properties settings) {
      super(settings);
   }

   protected VoxelShape getSofaShape(LineConnectingType type, Direction facing) {
      return SHAPES.get(type).get(facing);
   }
}
