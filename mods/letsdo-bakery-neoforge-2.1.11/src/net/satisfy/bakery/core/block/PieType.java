package net.satisfy.bakery.core.block;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.satisfy.foundation.util.ShapeUtil;

public enum PieType {
   CAKE(Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0), 4, 8, 0.0F),
   CHOCOLATE_GATEAU(Block.box(1.0, 0.0, 1.0, 15.0, 8.0, 15.0), 1, 8, 0.0F),
   BUNDT_CAKE(Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0), 1, 8, 0.0F),
   PUDDING(Shapes.or(Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0), Block.box(5.0, 8.0, 5.0, 11.0, 9.0, 11.0)), 1, 9, 0.0F),
   APPLE_PIE(Shapes.or(Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0), Block.box(1.0, 4.0, 1.0, 15.0, 6.0, 15.0)), 4, 6, 0.0F),
   GLOWBERRY_TART(Block.box(1.0, 0.0, 1.0, 15.0, 5.0, 15.0), 4, 5, 0.0F),
   LINZER_TART(Block.box(1.0, 0.0, 1.0, 15.0, 3.0, 15.0), 4, 3, 0.0F),
   CHOCOLATE_TART(Block.box(3.0, 0.0, 3.0, 13.0, 4.0, 13.0), 4, 4, 1.0F);

   private final VoxelShape[][] shapes = new VoxelShape[PieType.Cuts.LEFT.length][4];
   private final int maxCandles;
   private final int candleHeight;
   private final float candleInset;

   PieType(VoxelShape full, int maxCandles, int candleHeight, float candleInset) {
      this.maxCandles = maxCandles;
      this.candleHeight = candleHeight;
      this.candleInset = candleInset;

      for (int cuts = 0; cuts < PieType.Cuts.LEFT.length; cuts++) {
         VoxelShape left = Shapes.join(full, PieType.Cuts.LEFT[cuts], BooleanOp.AND);

         for (Direction direction : Plane.HORIZONTAL) {
            this.shapes[cuts][direction.get2DDataValue()] = ShapeUtil.rotateShape(Direction.NORTH, direction, left);
         }
      }
   }

   public VoxelShape shape(int cuts, Direction facing) {
      return this.shapes[cuts][facing.get2DDataValue()];
   }

   public int maxCandles() {
      return this.maxCandles;
   }

   public int candleHeight() {
      return this.candleHeight;
   }

   public float candleInset() {
      return this.candleInset;
   }

   // $VF: synthetic method
   private static PieType[] $values() {
      return new PieType[]{CAKE, CHOCOLATE_GATEAU, BUNDT_CAKE, PUDDING, APPLE_PIE, GLOWBERRY_TART, LINZER_TART, CHOCOLATE_TART};
   }

   private static final class Cuts {
      private static final VoxelShape[] LEFT = new VoxelShape[]{
         Shapes.block(),
         Shapes.or(Block.box(0.0, 0.0, 0.0, 8.0, 16.0, 8.0), Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0)),
         Block.box(0.0, 0.0, 8.0, 16.0, 16.0, 16.0),
         Block.box(0.0, 0.0, 8.0, 8.0, 16.0, 16.0)
      };
   }
}
