package com.kishku7.chunksmith.platform;

import com.kishku7.chunksmith.platform.util.Vector2;
import net.minecraft.world.level.border.WorldBorder;

public class NeoForgeBorder implements Border {
   private final WorldBorder worldBorder;

   public NeoForgeBorder(WorldBorder worldBorder) {
      this.worldBorder = worldBorder;
   }

   @Override
   public Vector2 getCenter() {
      return Vector2.of(this.worldBorder.getCenterX(), this.worldBorder.getCenterZ());
   }

   @Override
   public double getRadiusX() {
      return this.worldBorder.getSize() / 2.0;
   }

   @Override
   public double getRadiusZ() {
      return this.worldBorder.getSize() / 2.0;
   }

   @Override
   public String getShape() {
      return "square";
   }
}
