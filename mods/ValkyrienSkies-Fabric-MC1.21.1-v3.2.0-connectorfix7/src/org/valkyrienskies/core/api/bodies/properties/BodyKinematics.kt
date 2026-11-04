package org.valkyrienskies.core.api.bodies.properties

import org.joml.Matrix4dc
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta

@VsBeta
public interface BodyKinematics {
   public val transform: BodyTransform

   public val velocity: Vector3dc

   public val angularVelocity: Vector3dc

   public open val position: Vector3dc
      public open get() {
         return this.transform.position
      }


   public open val rotation: Quaterniondc
      public open get() {
         return this.transform.rotation
      }


   public open val scaling: Vector3dc
      public open get() {
         return this.transform.scaling
      }


   public open val positionInModel: Vector3dc
      public open get() {
         return this.transform.positionInModel
      }


   public open val toWorld: Matrix4dc
      public open get() {
         return this.transform.toWorld
      }


   public open val toModel: Matrix4dc
      public open get() {
         return this.transform.toModel
      }


   public abstract fun withTransform(transform: BodyTransform): BodyKinematics {
   }

   @VsBeta
   public abstract fun toBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
   }

   @VsBeta
   public interface Builder {
      public val velocity: Vector3d

      public val angularVelocity: Vector3d

      public val position: Vector3d

      public val positionInModel: Vector3d

      public val rotation: Quaterniond

      public val scaling: Vector3d

      public open fun position(position: Vector3dc): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
         val it: BodyKinematics.Builder = this
         this.position.set(position)
         return this
      }

      public open fun positionInModel(positionInModel: Vector3dc): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
         val it: BodyKinematics.Builder = this
         this.positionInModel.set(positionInModel)
         return this
      }

      public open fun rotation(rotation: Quaterniondc): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
         val it: BodyKinematics.Builder = this
         this.rotation.set(rotation)
         return this
      }

      public open fun scaling(scaling: Vector3dc): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
         val it: BodyKinematics.Builder = this
         this.scaling.set(scaling)
         return this
      }

      public open fun velocity(velocity: Vector3dc): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
         val it: BodyKinematics.Builder = this
         this.velocity.set(velocity)
         return this
      }

      public open fun angularVelocity(angularVelocity: Vector3dc): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
         val it: BodyKinematics.Builder = this
         this.angularVelocity.set(angularVelocity)
         return this
      }

      public open fun fromTransform(transform: BodyTransform): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
         val it: BodyKinematics.Builder = this
         this.position.set(transform.position)
         this.positionInModel.set(transform.positionInModel)
         this.rotation.set(transform.rotation)
         this.scaling.set(transform.scaling)
         return this
      }

      public open fun from(kinematics: BodyKinematics): org.valkyrienskies.core.api.bodies.properties.BodyKinematics.Builder {
         val it: BodyKinematics.Builder = this
         this.position.set(kinematics.position)
         this.positionInModel.set(kinematics.positionInModel)
         this.rotation.set(kinematics.rotation)
         this.scaling.set(kinematics.scaling)
         this.velocity.set(kinematics.velocity)
         this.angularVelocity.set(kinematics.angularVelocity)
         return this
      }

      public abstract fun build(): BodyKinematics {
      }
   }
}
