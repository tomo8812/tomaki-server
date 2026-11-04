package de.maxhenkel.gravestone.corelib.block;

import java.util.EnumMap;
import java.util.Map;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class DirectionalVoxelShape {
   private Map<Direction, VoxelShape> shapes;

   private DirectionalVoxelShape(Map<Direction, VoxelShape> shapes) {
      this.shapes = shapes;
   }

   public VoxelShape get(Direction direction) {
      return this.shapes.getOrDefault(direction, Shapes.empty());
   }

   public static class Builder {
      private Map<Direction, VoxelShape> shapeMap = new EnumMap<>(Direction.class);

      public DirectionalVoxelShape.Builder direction(Direction direction, VoxelShape... shapes) {
         this.shapeMap.put(direction, VoxelUtils.combine(shapes));
         return this;
      }

      public DirectionalVoxelShape build() {
         return new DirectionalVoxelShape(this.shapeMap);
      }
   }
}
