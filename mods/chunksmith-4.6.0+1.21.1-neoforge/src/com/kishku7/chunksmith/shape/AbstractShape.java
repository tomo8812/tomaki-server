package com.kishku7.chunksmith.shape;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.platform.util.Vector2;

public abstract class AbstractShape implements Shape {
   protected final double centerX;
   protected final double centerZ;
   protected final double diameterX;
   protected final double diameterZ;
   protected final double radiusX;
   protected final double radiusZ;

   protected AbstractShape(Selection selection, boolean chunkAligned) {
      if (chunkAligned) {
         this.centerX = (selection.centerChunkX() << 4) + 8.0;
         this.centerZ = (selection.centerChunkZ() << 4) + 8.0;
         this.diameterX = selection.diameterChunksX() << 4;
         this.diameterZ = selection.diameterChunksZ() << 4;
         this.radiusX = this.diameterX / 2.0;
         this.radiusZ = this.diameterZ / 2.0;
      } else {
         this.centerX = selection.centerX();
         this.centerZ = selection.centerZ();
         this.radiusX = selection.radiusX();
         this.radiusZ = selection.radiusZ();
         this.diameterX = 2.0 * this.radiusX;
         this.diameterZ = 2.0 * this.radiusZ;
      }
   }

   public Vector2 center() {
      return Vector2.of(this.centerX, this.centerZ);
   }
}
