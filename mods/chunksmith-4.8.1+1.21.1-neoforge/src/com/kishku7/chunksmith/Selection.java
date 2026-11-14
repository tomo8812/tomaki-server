package com.kishku7.chunksmith;

import com.kishku7.chunksmith.platform.Border;
import com.kishku7.chunksmith.platform.World;
import com.kishku7.chunksmith.platform.util.Location;
import com.kishku7.chunksmith.platform.util.Vector2;
import com.kishku7.chunksmith.util.Parameter;

public final class Selection {
   public static final double DEFAULT_CENTER_X = 0.0;
   public static final double DEFAULT_CENTER_Z = 0.0;
   public static final double DEFAULT_RADIUS = 500.0;
   private final Chunksmith chunky;
   private final World world;
   private final double centerX;
   private final double centerZ;
   private final double radiusX;
   private final double radiusZ;
   private final Parameter pattern;
   private final String shape;
   private final double horizon;
   private final int centerChunkX;
   private final int centerChunkZ;
   private final int radiusChunksX;
   private final int radiusChunksZ;
   private final int diameterChunksX;
   private final int diameterChunksZ;
   private final int centerRegionX;
   private final int centerRegionZ;
   private final int radiusRegionsX;
   private final int radiusRegionsZ;
   private final int diameterRegionsX;
   private final int diameterRegionsZ;

   private Selection(
      Chunksmith chunky, World world, double centerX, double centerZ, double radiusX, double radiusZ, Parameter pattern, String shape, double horizon
   ) {
      this.chunky = chunky;
      this.world = world;
      this.centerX = centerX;
      this.centerZ = centerZ;
      this.radiusX = radiusX;
      this.radiusZ = radiusZ;
      this.pattern = pattern;
      this.shape = shape;
      this.horizon = horizon;
      this.centerChunkX = (int)centerX >> 4;
      this.centerChunkZ = (int)centerZ >> 4;
      this.radiusChunksX = (int)Math.ceil(radiusX / 16.0);
      this.radiusChunksZ = (int)Math.ceil(radiusZ / 16.0);
      this.diameterChunksX = 2 * this.radiusChunksX + 1;
      this.diameterChunksZ = 2 * this.radiusChunksZ + 1;
      this.centerRegionX = this.centerChunkX >> 5;
      this.centerRegionZ = this.centerChunkZ >> 5;
      this.radiusRegionsX = (int)Math.ceil(this.radiusChunksX / 32.0F);
      this.radiusRegionsZ = (int)Math.ceil(this.radiusChunksZ / 32.0F);
      this.diameterRegionsX = 2 * this.radiusRegionsX + 1;
      this.diameterRegionsZ = 2 * this.radiusRegionsZ + 1;
   }

   public static Selection.Builder builder(Chunksmith chunky, World world) {
      return new Selection.Builder(chunky, world);
   }

   public Chunksmith chunky() {
      return this.chunky;
   }

   public World world() {
      return this.world;
   }

   public double centerX() {
      return this.centerX;
   }

   public double centerZ() {
      return this.centerZ;
   }

   public double radiusX() {
      return this.radiusX;
   }

   public double radiusZ() {
      return this.radiusZ;
   }

   public Parameter pattern() {
      return this.pattern;
   }

   public String shape() {
      return this.shape;
   }

   public double horizon() {
      return this.horizon;
   }

   public int centerChunkX() {
      return this.centerChunkX;
   }

   public int centerChunkZ() {
      return this.centerChunkZ;
   }

   public int radiusChunksX() {
      return this.radiusChunksX;
   }

   public int radiusChunksZ() {
      return this.radiusChunksZ;
   }

   public int diameterChunksX() {
      return this.diameterChunksX;
   }

   public int diameterChunksZ() {
      return this.diameterChunksZ;
   }

   public int centerRegionX() {
      return this.centerRegionX;
   }

   public int centerRegionZ() {
      return this.centerRegionZ;
   }

   public int radiusRegionsX() {
      return this.radiusRegionsX;
   }

   public int radiusRegionsZ() {
      return this.radiusRegionsZ;
   }

   public int diameterRegionsX() {
      return this.diameterRegionsX;
   }

   public int diameterRegionsZ() {
      return this.diameterRegionsZ;
   }

   public static final class Builder {
      private final Chunksmith chunky;
      private World world;
      private double centerX = 0.0;
      private double centerZ = 0.0;
      private double radiusX = 500.0;
      private double radiusZ = 500.0;
      private Parameter pattern = Parameter.of("region");
      private String shape = "square";
      private double horizon;

      private Builder(Chunksmith chunky, World world) {
         this.chunky = chunky;
         this.world = world;
      }

      public Selection.Builder world(World world) {
         this.world = world;
         return this;
      }

      public Selection.Builder center(double centerX, double centerZ) {
         this.centerX = centerX;
         this.centerZ = centerZ;
         return this;
      }

      public Selection.Builder centerX(double centerX) {
         this.centerX = centerX;
         return this;
      }

      public Selection.Builder centerZ(double centerZ) {
         this.centerZ = centerZ;
         return this;
      }

      public Selection.Builder radius(double radius) {
         this.radiusX = radius;
         this.radiusZ = radius;
         return this;
      }

      public Selection.Builder radiusX(double radiusX) {
         this.radiusX = radiusX;
         return this;
      }

      public Selection.Builder radiusZ(double radiusZ) {
         this.radiusZ = radiusZ;
         return this;
      }

      public Selection.Builder pattern(Parameter pattern) {
         this.pattern = pattern;
         return this;
      }

      public Selection.Builder shape(String shape) {
         this.shape = shape;
         return this;
      }

      public Selection.Builder horizon(double horizon) {
         this.horizon = horizon;
         return this;
      }

      public Selection.Builder spawn() {
         Location spawn = this.world.getSpawn();
         this.centerX = spawn.getX();
         this.centerZ = spawn.getZ();
         return this;
      }

      public Selection.Builder worldborder() {
         Border border = this.world.getWorldBorder();
         Vector2 center = border.getCenter();
         this.centerX = center.getX();
         this.centerZ = center.getZ();
         this.radiusX = border.getRadiusX();
         this.radiusZ = border.getRadiusZ();
         return this;
      }

      public Selection build() {
         return new Selection(this.chunky, this.world, this.centerX, this.centerZ, this.radiusX, this.radiusZ, this.pattern, this.shape, this.horizon);
      }
   }
}
