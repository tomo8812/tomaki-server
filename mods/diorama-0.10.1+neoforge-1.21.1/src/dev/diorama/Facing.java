package dev.diorama;

import java.util.Locale;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public enum Facing {
   NORTH(0, Rotation.NONE),
   EAST(1, Rotation.COUNTERCLOCKWISE_90),
   SOUTH(2, Rotation.CLOCKWISE_180),
   WEST(3, Rotation.CLOCKWISE_90);

   public final int turns;
   private final Rotation rotation;

   Facing(int turns, Rotation rotation) {
      this.turns = turns;
      this.rotation = rotation;
   }

   public String id() {
      return this.name().toLowerCase(Locale.ROOT);
   }

   public static Facing of(String id) {
      for (Facing facing : values()) {
         if (facing.id().equals(id)) {
            return facing;
         }
      }

      return NORTH;
   }

   public Facing next(int steps) {
      return values()[Math.floorMod(this.turns + steps, 4)];
   }

   public int blockX(int x, int z) {
      return switch (this) {
         case EAST -> z;
         case SOUTH -> -x - 1;
         case WEST -> -z - 1;
         default -> x;
      };
   }

   public int blockZ(int x, int z) {
      return switch (this) {
         case EAST -> -x - 1;
         case SOUTH -> -z - 1;
         case WEST -> x;
         default -> z;
      };
   }

   public int worldBlockX(int x, int z) {
      return switch (this) {
         case EAST -> -z - 1;
         case SOUTH -> -x - 1;
         case WEST -> z;
         default -> x;
      };
   }

   public int worldBlockZ(int x, int z) {
      return switch (this) {
         case EAST -> x;
         case SOUTH -> -z - 1;
         case WEST -> -x - 1;
         default -> z;
      };
   }

   public double x(double x, double z) {
      return switch (this) {
         case EAST -> z;
         case SOUTH -> -x;
         case WEST -> -z;
         default -> x;
      };
   }

   public double z(double x, double z) {
      return switch (this) {
         case EAST -> -x;
         case SOUTH -> -z;
         case WEST -> x;
         default -> z;
      };
   }

   public double worldX(double x, double z) {
      return switch (this) {
         case EAST -> -z;
         case SOUTH -> -x;
         case WEST -> z;
         default -> x;
      };
   }

   public double worldZ(double x, double z) {
      return switch (this) {
         case EAST -> x;
         case SOUTH -> -z;
         case WEST -> -x;
         default -> z;
      };
   }

   public BlockPos worldPos(BlockPos turned) {
      return new BlockPos(this.worldBlockX(turned.getX(), turned.getZ()), turned.getY(), this.worldBlockZ(turned.getX(), turned.getZ()));
   }

   public BlockState turned(BlockState state) {
      if (this == NORTH) {
         return state;
      }

      try {
         return state.rotate(this.rotation);
      } catch (RuntimeException e) {
         return state;
      }
   }

   // $VF: synthetic method
   private static Facing[] $values() {
      return new Facing[]{NORTH, EAST, SOUTH, WEST};
   }
}
