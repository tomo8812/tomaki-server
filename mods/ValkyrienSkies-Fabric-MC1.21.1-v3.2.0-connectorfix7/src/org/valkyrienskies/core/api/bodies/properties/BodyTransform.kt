package org.valkyrienskies.core.api.bodies.properties

import java.io.DataInput
import java.io.DataOutput
import org.joml.Matrix4dc
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta

public interface BodyTransform {
   public val position: Vector3dc

   public val rotation: Quaterniondc

   public val scaling: Vector3dc

   public val positionInModel: Vector3dc

   public val toWorld: Matrix4dc

   public val toModel: Matrix4dc

   @VsBeta
   public abstract fun withVelocity(velocity: Vector3dc, angularVelocity: Vector3dc): BodyKinematics {
   }

   @VsBeta
   public abstract fun toBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
   }

   @VsBeta
   public abstract fun writeTransform(output: DataOutput) {
   }

   @VsBeta
   public interface Builder {
      public val position: Vector3d

      public val positionInModel: Vector3d

      public val rotation: Quaterniond

      public val scaling: Vector3d

      public open fun position(position: Vector3dc): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
         val it: BodyTransform.Builder = this
         this.position.set(position)
         return this
      }

      public open fun positionInModel(positionInModel: Vector3dc): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
         val it: BodyTransform.Builder = this
         this.positionInModel.set(positionInModel)
         return this
      }

      public open fun rotation(rotation: Quaterniondc): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
         val it: BodyTransform.Builder = this
         this.rotation.set(rotation)
         return this
      }

      public open fun scaling(scaling: Vector3dc): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
         val it: BodyTransform.Builder = this
         this.scaling.set(scaling)
         return this
      }

      public abstract fun from(transform: BodyTransform): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
      }

      public abstract fun build(): BodyTransform {
      }
   }

   @VsBeta
   public interface Factory {
      public abstract fun newBuilder(): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
      }

      public abstract fun newBuilder(from: BodyTransform): org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder {
      }

      public abstract fun readFrom(from: DataInput): BodyTransform {
      }

      public abstract fun build(builder: (org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder) -> Unit): BodyTransform {
      }

      public abstract fun create(position: Vector3dc, rotation: Quaterniondc, scaling: Vector3dc, positionInModel: Vector3dc): BodyTransform {
      }

      public abstract fun createFromSlerp(previous: BodyTransform, current: BodyTransform, alpha: Double): BodyTransform {
      }
   }
}
