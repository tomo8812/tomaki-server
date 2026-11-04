package net.satisfy.farm_and_charm.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FoodShapes {
   public static final VoxelShape[] POTATO_WITH_ROAST_MEAT = new VoxelShape[]{
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(2.0, 1.95, 5.0, 5.0, 4.95, 8.0),
         Block.box(3.0, 1.95, 2.0, 6.0, 4.95, 5.0),
         Block.box(4.0, 1.95, 4.0, 14.0, 6.95, 6.0),
         Block.box(4.0, 1.95, 6.0, 14.0, 6.95, 9.0),
         Block.box(4.0, 1.95, 9.0, 14.0, 6.95, 11.0),
         Block.box(4.0, 1.95, 11.0, 14.0, 6.95, 14.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(3.0, 1.95, 2.0, 6.0, 4.95, 5.0),
         Block.box(4.0, 1.95, 6.0, 14.0, 6.95, 9.0),
         Block.box(4.0, 1.95, 9.0, 14.0, 6.95, 11.0),
         Block.box(4.0, 1.95, 11.0, 14.0, 6.95, 14.0)
      ),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(4.0, 1.95, 9.0, 14.0, 6.95, 11.0), Block.box(4.0, 1.95, 11.0, 14.0, 6.95, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(4.0, 1.95, 11.0, 14.0, 6.95, 14.0))
   };
   public static final VoxelShape[] BAKED_LAMB_HAM = new VoxelShape[]{
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(3.0, 2.0, 3.0, 13.0, 10.0, 13.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(3.0, 2.0, 6.0, 13.0, 10.0, 13.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(3.0, 2.0, 9.0, 13.0, 10.0, 13.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(3.0, 2.0, 11.0, 13.0, 10.0, 13.0))
   };
   public static final VoxelShape[] FARMERS_BREAKFAST = new VoxelShape[]{
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(1.9, 1.9, 3.9, 4.1, 4.1, 11.1),
         Block.box(2.0, 2.0, 4.0, 4.0, 4.0, 11.0),
         Block.box(2.9, 1.9, 11.9, 10.1, 4.1, 14.1),
         Block.box(3.0, 2.0, 12.0, 10.0, 4.0, 14.0),
         Block.box(6.0, 2.05, 6.0, 9.0, 3.05, 9.0),
         Block.box(10.0, 2.0, 6.0, 14.0, 6.0, 13.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(2.9, 1.9, 11.9, 10.1, 4.1, 14.1),
         Block.box(3.0, 2.0, 12.0, 10.0, 4.0, 14.0),
         Block.box(6.0, 2.05, 6.0, 9.0, 3.05, 9.0),
         Block.box(10.0, 2.0, 6.0, 14.0, 6.0, 13.0)
      ),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.9, 1.9, 11.9, 10.1, 4.1, 14.1), Block.box(3.0, 2.0, 12.0, 10.0, 4.0, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0))
   };
   public static final VoxelShape[] STUFFED_CHICKEN = new VoxelShape[]{
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(2.0, 2.0, 2.0, 4.0, 4.0, 5.0),
         Block.box(2.0, 4.0, 10.0, 4.0, 7.0, 14.0),
         Block.box(3.0, 5.0, 14.0, 4.0, 6.0, 16.0),
         Block.box(4.0, 2.0, 3.0, 12.0, 8.0, 13.0),
         Block.box(5.0, 8.0, 4.0, 11.0, 9.0, 12.0),
         Block.box(12.0, 2.0, 2.0, 14.0, 4.0, 5.0),
         Block.box(12.0, 4.0, 10.0, 14.0, 7.0, 14.0),
         Block.box(12.0, 5.0, 14.0, 13.0, 6.0, 16.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(2.0, 2.0, 2.0, 4.0, 4.0, 5.0),
         Block.box(4.0, 2.0, 3.0, 12.0, 8.0, 13.0),
         Block.box(12.0, 4.0, 10.0, 14.0, 7.0, 14.0),
         Block.box(12.0, 5.0, 14.0, 13.0, 6.0, 16.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(4.0, 2.0, 6.0, 12.0, 8.0, 13.0),
         Block.box(5.0, 2.05, 3.0, 11.0, 8.05, 6.0),
         Block.box(12.0, 4.0, 10.0, 14.0, 7.0, 14.0),
         Block.box(12.0, 5.0, 14.0, 13.0, 6.0, 16.0)
      ),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(4.0, 2.0, 9.0, 12.0, 8.0, 13.0), Block.box(5.0, 2.05, 3.0, 11.0, 8.05, 9.0))
   };
   public static final VoxelShape[] STUFFED_RABBIT = new VoxelShape[]{
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(4.0, 2.0, 2.0, 6.0, 4.0, 6.0),
         Block.box(4.0, 2.0, 8.0, 5.0, 4.0, 11.0),
         Block.box(4.0, 2.0, 11.0, 6.0, 6.0, 15.0),
         Block.box(5.0, 2.0, 4.0, 11.0, 7.0, 14.0),
         Block.box(10.0, 2.0, 2.0, 12.0, 4.0, 6.0),
         Block.box(10.0, 2.0, 11.0, 12.0, 6.0, 15.0),
         Block.box(11.0, 2.0, 8.0, 12.0, 4.0, 11.0),
         Block.box(5.5, 7.0, 2.0, 10.5, 11.0, 7.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(4.0, 2.0, 2.0, 6.0, 4.0, 6.0),
         Block.box(5.0, 2.0, 4.0, 11.0, 7.0, 14.0),
         Block.box(10.0, 2.0, 2.0, 12.0, 4.0, 6.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(4.0, 2.0, 2.0, 6.0, 4.0, 6.0),
         Block.box(5.0, 2.0, 4.0, 11.0, 7.0, 10.0),
         Block.box(5.0, 2.0, 10.0, 11.0, 7.0, 14.0),
         Block.box(10.0, 2.0, 2.0, 12.0, 4.0, 6.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(5.0, 2.0, 4.0, 11.0, 7.0, 7.0),
         Block.box(5.0, 2.0, 7.0, 11.0, 7.0, 14.0),
         Block.box(10.0, 2.0, 2.0, 12.0, 4.0, 6.0)
      )
   };
   public static final VoxelShape[] FARMERS_BREAD = new VoxelShape[]{
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(2.0, 2.0, 2.0, 14.0, 8.0, 5.0),
         Block.box(2.0, 2.0, 5.0, 14.0, 8.0, 8.0),
         Block.box(2.0, 2.0, 8.0, 14.0, 8.0, 11.0),
         Block.box(2.0, 2.0, 11.0, 14.0, 8.0, 14.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(2.0, 2.0, 5.0, 14.0, 8.0, 8.0),
         Block.box(2.0, 2.0, 8.0, 14.0, 8.0, 11.0),
         Block.box(2.0, 2.0, 11.0, 14.0, 8.0, 14.0)
      ),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 8.0, 14.0, 8.0, 11.0), Block.box(2.0, 2.0, 11.0, 14.0, 8.0, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 11.0, 14.0, 8.0, 14.0))
   };
   public static final VoxelShape[] GRANDMOTHERS_STRAWBERRY_CAKE = new VoxelShape[]{
      shape(
         Block.box(2.0, 4.0, 2.0, 8.0, 7.0, 8.0),
         Block.box(2.0, 4.0, 8.0, 8.0, 7.0, 14.0),
         Block.box(3.0, 0.0, 3.0, 8.0, 4.0, 8.0),
         Block.box(3.0, 0.0, 8.0, 8.0, 4.0, 13.0),
         Block.box(8.0, 0.0, 3.0, 13.0, 4.0, 8.0),
         Block.box(8.0, 0.0, 8.0, 13.0, 4.0, 13.0),
         Block.box(8.0, 4.0, 2.0, 14.0, 7.0, 8.0),
         Block.box(8.0, 4.0, 8.0, 14.0, 7.0, 14.0)
      ),
      shape(
         Block.box(2.0, 4.0, 8.0, 8.0, 7.0, 14.0),
         Block.box(3.0, 0.0, 8.0, 8.0, 4.0, 13.0),
         Block.box(8.0, 0.0, 3.0, 13.0, 4.0, 8.0),
         Block.box(8.0, 0.0, 8.0, 13.0, 4.0, 13.0),
         Block.box(8.0, 4.0, 2.0, 14.0, 7.0, 8.0),
         Block.box(8.0, 4.0, 8.0, 14.0, 7.0, 14.0)
      ),
      shape(
         Block.box(2.0, 4.0, 8.0, 8.0, 7.0, 14.0),
         Block.box(3.0, 0.0, 8.0, 8.0, 4.0, 13.0),
         Block.box(8.0, 0.0, 8.0, 13.0, 4.0, 13.0),
         Block.box(8.0, 4.0, 8.0, 14.0, 7.0, 14.0)
      ),
      shape(Block.box(8.0, 0.0, 8.0, 13.0, 4.0, 13.0), Block.box(8.0, 4.0, 8.0, 14.0, 7.0, 14.0))
   };
   public static final VoxelShape[] OAT_PANCAKE = new VoxelShape[]{
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 2.0, 14.0, 3.0, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 2.0, 14.0, 4.0, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 2.0, 14.0, 5.0, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 2.0, 14.0, 6.0, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 2.0, 14.0, 7.0, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 2.0, 14.0, 8.0, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 2.0, 14.0, 9.0, 14.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0), Block.box(2.0, 2.0, 2.0, 14.0, 10.0, 14.0))
   };
   public static final VoxelShape[] ROASTED_CORN = new VoxelShape[]{
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(6.9, 1.9, 3.9, 9.1, 4.1, 11.1),
         Block.box(7.0, 2.0, 4.0, 9.0, 4.0, 11.0),
         Block.box(9.9, 1.9, 3.9, 12.1, 4.1, 11.1),
         Block.box(10.0, 2.0, 4.0, 12.0, 4.0, 11.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(2.9, 1.9, 3.9, 5.1, 4.1, 11.1),
         Block.box(3.0, 2.0, 4.0, 5.0, 4.0, 11.0),
         Block.box(6.9, 1.9, 3.9, 9.1, 4.1, 11.1),
         Block.box(7.0, 2.0, 4.0, 9.0, 4.0, 11.0),
         Block.box(8.9, 3.9, 3.9, 11.1, 6.1, 11.1),
         Block.box(9.0, 4.0, 4.0, 11.0, 6.0, 11.0),
         Block.box(9.9, 1.9, 3.9, 12.1, 4.1, 11.1),
         Block.box(10.0, 2.0, 4.0, 12.0, 4.0, 11.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(1.706, 3.9, 5.209, 8.906, 6.1, 7.409),
         Block.box(1.706, 3.9, 8.209, 8.906, 6.1, 10.409),
         Block.box(1.806, 4.0, 5.309, 8.806, 6.0, 7.309),
         Block.box(1.806, 4.0, 8.309, 8.806, 6.0, 10.309),
         Block.box(2.9, 1.9, 3.9, 5.1, 4.1, 11.1),
         Block.box(3.0, 2.0, 4.0, 5.0, 4.0, 11.0),
         Block.box(6.9, 1.9, 3.9, 9.1, 4.1, 11.1),
         Block.box(7.0, 2.0, 4.0, 9.0, 4.0, 11.0),
         Block.box(8.9, 3.9, 3.9, 11.1, 6.1, 11.1),
         Block.box(9.0, 4.0, 4.0, 11.0, 6.0, 11.0),
         Block.box(9.9, 1.9, 3.9, 12.1, 4.1, 11.1),
         Block.box(10.0, 2.0, 4.0, 12.0, 4.0, 11.0)
      ),
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
         Block.box(1.706, 3.9, 5.209, 8.906, 6.1, 7.409),
         Block.box(1.706, 3.9, 8.209, 8.906, 6.1, 10.409),
         Block.box(1.806, 4.0, 5.309, 8.806, 6.0, 7.309),
         Block.box(1.806, 4.0, 8.309, 8.806, 6.0, 10.309),
         Block.box(2.9, 1.9, 3.9, 5.1, 4.1, 11.1),
         Block.box(3.0, 2.0, 4.0, 5.0, 4.0, 11.0),
         Block.box(3.9, 5.9, 3.9, 6.1, 8.1, 11.1),
         Block.box(4.0, 6.0, 4.0, 6.0, 8.0, 11.0),
         Block.box(6.9, 1.9, 3.9, 9.1, 4.1, 11.1),
         Block.box(7.0, 2.0, 4.0, 9.0, 4.0, 11.0),
         Block.box(7.9, 5.9, 3.9, 10.1, 8.1, 11.1),
         Block.box(8.0, 6.0, 4.0, 10.0, 8.0, 11.0),
         Block.box(8.9, 3.9, 3.9, 11.1, 6.1, 11.1),
         Block.box(9.0, 4.0, 4.0, 11.0, 6.0, 11.0),
         Block.box(9.9, 1.9, 3.9, 12.1, 4.1, 11.1),
         Block.box(10.0, 2.0, 4.0, 12.0, 4.0, 11.0)
      )
   };

   private FoodShapes() {
   }

   private static VoxelShape shape(VoxelShape first, VoxelShape... rest) {
      return Shapes.or(first, rest);
   }
}
