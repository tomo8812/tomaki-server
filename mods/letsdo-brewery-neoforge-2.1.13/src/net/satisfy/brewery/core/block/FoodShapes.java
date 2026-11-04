package net.satisfy.brewery.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class FoodShapes {
   public static final VoxelShape[] PORK_KNUCKLE = new VoxelShape[]{
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0), Block.box(1.0, 1.0, 3.0, 13.0, 9.0, 13.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0), Block.box(3.0, 1.0, 3.0, 13.0, 5.0, 13.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0), Block.box(4.0, 1.0, 4.0, 12.0, 4.0, 12.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0))
   };
   public static final VoxelShape[] FRIED_CHICKEN = new VoxelShape[]{
      shape(
         Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0),
         Block.box(3.0, 1.0, 3.0, 7.0, 7.0, 7.0),
         Block.box(4.0, 1.0, 8.0, 8.0, 7.0, 12.0),
         Block.box(4.0, 7.0, 4.0, 6.0, 11.0, 6.0),
         Block.box(5.0, 7.0, 9.0, 7.0, 11.0, 11.0)
      ),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0), Block.box(4.0, 1.0, 8.0, 8.0, 7.0, 12.0), Block.box(5.0, 7.0, 9.0, 7.0, 11.0, 11.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0), Block.box(6.0, 1.0, 6.0, 10.0, 5.0, 12.0), Block.box(7.0, 2.0, 2.0, 9.0, 4.0, 6.0)),
      shape(Block.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0))
   };
   public static final VoxelShape[] HALF_CHICKEN = new VoxelShape[]{
      shape(Block.box(1.0, 0.0, 0.0, 15.0, 1.0, 16.0), Block.box(5.0, 1.0, 1.0, 10.0, 7.0, 15.0), Block.box(6.0, 7.0, 2.0, 10.0, 9.0, 14.0)),
      shape(Block.box(1.0, 0.0, 0.0, 15.0, 1.0, 16.0), Block.box(5.0, 1.0, 1.0, 10.0, 7.0, 15.0), Block.box(6.0, 7.0, 2.0, 10.0, 9.0, 14.0)),
      shape(Block.box(1.0, 0.0, 0.0, 15.0, 1.0, 16.0), Block.box(5.0, 1.0, 5.0, 10.0, 7.0, 15.0), Block.box(6.0, 7.0, 5.0, 10.0, 9.0, 14.0)),
      shape(Block.box(1.0, 0.0, 0.0, 15.0, 1.0, 16.0), Block.box(5.0, 1.0, 9.0, 10.0, 7.0, 15.0), Block.box(6.0, 7.0, 9.0, 10.0, 9.0, 14.0))
   };
   public static final VoxelShape[] MASHED_POTATOES = new VoxelShape[]{
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(5.0, 1.0, 5.0, 8.0, 4.0, 11.0),
         Block.box(8.0, 1.0, 5.0, 11.0, 4.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      ),
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 8.0, 3.0, 11.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(8.0, 1.0, 5.0, 11.0, 4.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      ),
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(8.0, 1.0, 5.0, 11.0, 4.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      ),
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(8.0, 1.0, 5.0, 11.0, 2.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      )
   };
   public static final VoxelShape[] POTATO_SALAD = new VoxelShape[]{
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(5.0, 1.0, 5.0, 8.0, 4.0, 11.0),
         Block.box(8.0, 1.0, 5.0, 11.0, 4.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      ),
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 8.0, 3.0, 11.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(8.0, 1.0, 5.0, 11.0, 4.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      ),
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(8.0, 1.0, 5.0, 11.0, 4.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      ),
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(8.0, 1.0, 5.0, 11.0, 2.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      )
   };
   public static final VoxelShape[] DUMPLINGS = new VoxelShape[]{
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(5.0, 1.0, 5.0, 8.0, 4.0, 8.0),
         Block.box(6.0, 4.0, 6.0, 9.0, 7.0, 9.0),
         Block.box(8.0, 1.0, 8.0, 11.0, 4.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      ),
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(5.0, 1.0, 5.0, 8.0, 4.0, 8.0),
         Block.box(8.0, 1.0, 8.0, 11.0, 4.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      ),
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(5.0, 1.0, 5.0, 8.0, 4.0, 8.0),
         Block.box(8.0, 1.0, 8.0, 11.0, 2.0, 11.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      ),
      shape(
         Block.box(4.0, 0.0, 4.0, 5.0, 3.0, 12.0),
         Block.box(5.0, 0.0, 4.0, 11.0, 3.0, 5.0),
         Block.box(5.0, 0.0, 5.0, 11.0, 1.0, 11.0),
         Block.box(5.0, 0.0, 11.0, 11.0, 3.0, 12.0),
         Block.box(5.0, 1.0, 5.0, 8.0, 4.0, 8.0),
         Block.box(11.0, 0.0, 4.0, 12.0, 3.0, 12.0)
      )
   };

   private FoodShapes() {
   }

   private static VoxelShape shape(VoxelShape first, VoxelShape... rest) {
      return Shapes.or(first, rest);
   }
}
