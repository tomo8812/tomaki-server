package org.valkyrienskies.core.api.ships

import org.joml.Matrix4dc
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.util.Identified

public interface Ship : Identified {
   public val id: Long

   public val slug: String?

   @VsBeta
   public val kinematics: BodyKinematics

   public open val transform: ShipTransform
      public open get() {
         val var10000: BodyTransform = this.kinematics.transform
         return var10000 as ShipTransform
      }


   public val prevTickTransform: ShipTransform

   public val chunkClaim: ChunkClaim

   public val chunkClaimDimension: String

   public val worldAABB: AABBdc

   public val shipAABB: AABBic?

   public open val velocity: Vector3dc
      public open get() {
         return this.kinematics.velocity
      }


   public open val angularVelocity: Vector3dc
      public open get() {
         return this.kinematics.angularVelocity
      }


   public val activeChunksSet: IShipActiveChunksSet

   public open val shipToWorld: Matrix4dc
      public open get() {
         return this.transform.getToWorld()
      }


   public open val worldToShip: Matrix4dc
      public open get() {
         return this.transform.getToModel()
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "prevTickTransform", imports = {}))
   public open val prevTickShipTransform: ShipTransform
      public open get() {
         return this.prevTickTransform
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "angularVelocity", imports = {}))
   public open val omega: Vector3dc
      public open get() {
         return this.angularVelocity
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "transform", imports = {}))
   public open val shipTransform: ShipTransform
      public open get() {
         return this.transform
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "shipAABB", imports = {}))
   public open val shipVoxelAABB: AABBic?
      public open get() {
         return this.shipAABB
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "activeChunksSet", imports = {}))
   public open val shipActiveChunksSet: IShipActiveChunksSet
      public open get() {
         return this.activeChunksSet
      }

}
