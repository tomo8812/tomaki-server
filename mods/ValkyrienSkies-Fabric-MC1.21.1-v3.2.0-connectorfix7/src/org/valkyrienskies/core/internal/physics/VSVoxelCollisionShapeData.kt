package org.valkyrienskies.core.internal.physics

import org.joml.Vector3ic
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet

public data class VSVoxelCollisionShapeData(minDefined: Vector3ic,
      maxDefined: Vector3ic,
      totalVoxelRegion: AABBic,
      shipVoxelsFullyLoaded: Boolean,
      chunksToLoad: IShipActiveChunksSet?
   ) :
   VSCollisionShapeData {
   public final val minDefined: Vector3ic
   public final val maxDefined: Vector3ic
   public final val totalVoxelRegion: AABBic
   public final val shipVoxelsFullyLoaded: Boolean
   public final val chunksToLoad: IShipActiveChunksSet?

   init {
      this.minDefined = minDefined
      this.maxDefined = maxDefined
      this.totalVoxelRegion = totalVoxelRegion
      this.shipVoxelsFullyLoaded = shipVoxelsFullyLoaded
      this.chunksToLoad = chunksToLoad
   }

   public open val shapeType: VSCollisionShapes
      public open get() {
         return VSCollisionShapes.VOXEL
      }


   public operator fun component1(): Vector3ic {
      return this.minDefined
   }

   public operator fun component2(): Vector3ic {
      return this.maxDefined
   }

   public operator fun component3(): AABBic {
      return this.totalVoxelRegion
   }

   public operator fun component4(): Boolean {
      return this.shipVoxelsFullyLoaded
   }

   public operator fun component5(): IShipActiveChunksSet? {
      return this.chunksToLoad
   }

   public fun copy(
      minDefined: Vector3ic = this.minDefined,
      maxDefined: Vector3ic = this.maxDefined,
      totalVoxelRegion: AABBic = this.totalVoxelRegion,
      shipVoxelsFullyLoaded: Boolean = this.shipVoxelsFullyLoaded,
      chunksToLoad: IShipActiveChunksSet? = this.chunksToLoad
   ): VSVoxelCollisionShapeData {
      return VSVoxelCollisionShapeData(minDefined, maxDefined, totalVoxelRegion, shipVoxelsFullyLoaded, chunksToLoad)
   }

   public override fun toString(): String {
      return "VSVoxelCollisionShapeData(minDefined=${this.minDefined}, maxDefined=${this.maxDefined}, totalVoxelRegion=${this.totalVoxelRegion}, shipVoxelsFullyLoaded=${this.shipVoxelsFullyLoaded}, chunksToLoad=${this.chunksToLoad})"
   }

   public override fun hashCode(): Int {
      return (
               ((this.minDefined.hashCode() * 31 + this.maxDefined.hashCode()) * 31 + this.totalVoxelRegion.hashCode()) * 31
                  + java.lang.Boolean.hashCode(this.shipVoxelsFullyLoaded)
            )
            * 31
         + (if (this.chunksToLoad == null) 0 else this.chunksToLoad.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label46@
      if (this === other) {
         return true
      } else {
         return other is VSVoxelCollisionShapeData
            && this.minDefined == (other as VSVoxelCollisionShapeData).minDefined
            && this.maxDefined == (other as VSVoxelCollisionShapeData).maxDefined
            && this.totalVoxelRegion == (other as VSVoxelCollisionShapeData).totalVoxelRegion
            && this.shipVoxelsFullyLoaded == (other as VSVoxelCollisionShapeData).shipVoxelsFullyLoaded
            && this.chunksToLoad == (other as VSVoxelCollisionShapeData).chunksToLoad
         }
   }
}
