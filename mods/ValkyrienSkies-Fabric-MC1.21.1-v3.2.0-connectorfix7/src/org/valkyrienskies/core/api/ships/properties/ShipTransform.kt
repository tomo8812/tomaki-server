package org.valkyrienskies.core.api.ships.properties

import org.jetbrains.annotations.ApiStatus
import org.joml.Matrix4dc
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.bodies.properties.BodyTransform

@ApiStatus.NonExtendable
public interface ShipTransform : BodyTransform {
   public open val positionInWorld: Vector3dc
      public open get() {
         return this.getPosition()
      }


   public open val positionInShip: Vector3dc
      public open get() {
         return this.getPositionInModel()
      }


   public open val shipToWorldRotation: Quaterniondc
      public open get() {
         return this.getRotation()
      }


   public open val shipToWorldScaling: Vector3dc
      public open get() {
         return this.getScaling()
      }


   public open val shipToWorld: Matrix4dc
      public open get() {
         return this.getToWorld()
      }


   public open val worldToShip: Matrix4dc
      public open get() {
         return this.getToModel()
      }


   @Deprecated(message = "not sure why this exists", replaceWith = @ReplaceWith(expression = "AABBd(position, position)", imports = ["org.joml.primitives.AABBd"]))
   public open fun createEmptyAABB(): AABBdc {
      return AABBd(this.getPosition(), this.getPosition())
   }

   @Deprecated(message = "use toBuilder or rebuild instead - you can cast the result back to ShipTransform if needed")
   public open fun copy(
      positionInWorld: Vector3dc = this.positionInWorld,
      positionInShip: Vector3dc = this.positionInShip,
      shipToWorldRotation: Quaterniondc = this.shipToWorldRotation,
      shipToWorldScaling: Vector3dc = this.shipToWorldScaling
   ): ShipTransform {
      val var5: BodyTransform = this.toBuilder()
         .position(positionInWorld)
         .rotation(shipToWorldRotation)
         .scaling(shipToWorldScaling)
         .positionInModel(positionInShip)
         .build()
         return var5 as ShipTransform
   }

   @Deprecated(message = "redundant", replaceWith = @ReplaceWith(expression = "rotation.transform(directionInShip, dest)", imports = []))
   public open fun transformDirectionNoScalingFromShipToWorld(directionInShip: Vector3dc, dest: Vector3d): Vector3d {
      val var10000: Vector3d = this.getRotation().transform(directionInShip, dest)
      return var10000
   }

   @Deprecated(message = "redundant", replaceWith = @ReplaceWith(expression = "rotation.transformInverse(directionInWorld, dest)", imports = []))
   public open fun transformDirectionNoScalingFromWorldToShip(directionInWorld: Vector3dc, dest: Vector3d): Vector3d {
      val var10000: Vector3d = this.getRotation().transformInverse(directionInWorld, dest)
      return var10000
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "position", imports = {}))
   public open val shipPositionInWorldCoordinates: Vector3dc
      public open get() {
         return this.getPosition()
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "positionInModel", imports = {}))
   public open val shipPositionInShipCoordinates: Vector3dc
      public open get() {
         return this.getPositionInModel()
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "rotation", imports = {}))
   public open val shipCoordinatesToWorldCoordinatesRotation: Quaterniondc
      public open get() {
         return this.getRotation()
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "scaling", imports = {}))
   public open val shipCoordinatesToWorldCoordinatesScaling: Vector3dc
      public open get() {
         return this.getScaling()
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "toWorld", imports = {}))
   public open val shipToWorldMatrix: Matrix4dc
      public open get() {
         return this.getToWorld()
      }


   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "toModel", imports = {}))
   public open val worldToShipMatrix: Matrix4dc
      public open get() {
         return this.getToModel()
      }

}
