package org.valkyrienskies.core.impl.bodies.properties

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonIncludeProperties
import java.io.DataOutput
import java.io.Externalizable
import java.io.InvalidObjectException
import java.io.ObjectInput
import java.io.ObjectInputStream
import java.io.ObjectOutput
import java.io.Serializable
import org.joml.Matrix4dc
import org.joml.Quaterniondc
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.util.DoNotObfuscate

@JsonIncludeProperties(["position", "positionInModel", "rotation", "scaling"])
@JsonIgnoreProperties(ignoreUnknown = true)
@DoNotObfuscate
public class BodyTransformImpl @JsonCreator  public constructor(position: Vector3dc, rotation: Quaterniondc, scaling: Vector3dc, positionInModel: Vector3dc) :
   Serializable,
   BodyTransform,
   ShipTransform {
   public open val position: Vector3dc
   public open val positionInModel: Vector3dc
   public open val rotation: Quaterniondc
   public open val scaling: Vector3dc
   public open val toWorld: Matrix4dc
   public open val toModel: Matrix4dc

   @VsBeta
   public override fun withVelocity(velocity: Vector3dc, angularVelocity: Vector3dc): BodyKinematics {
      return BodyKinematicsFactory.INSTANCE.create(velocity, angularVelocity, this)
   }

   public override fun toBuilder(): Builder {
      return BodyTransformFactory.INSTANCE.newBuilder(this)
   }

   @VsBeta
   public override fun writeTransform(output: DataOutput) {
      val var2: DataOutput
      (var2 = output).writeDouble(this.position.x())
      var2.writeDouble(this.position.y())
      var2.writeDouble(this.position.z())
      var2.writeDouble(this.rotation.x())
      var2.writeDouble(this.rotation.y())
      var2.writeDouble(this.rotation.z())
      var2.writeDouble(this.rotation.w())
      var2.writeDouble(this.scaling.x())
      var2.writeDouble(this.scaling.y())
      var2.writeDouble(this.scaling.z())
      var2.writeDouble(this.positionInModel.x())
      var2.writeDouble(this.positionInModel.y())
      var2.writeDouble(this.positionInModel.z())
   }

   public override fun hashCode(): Int {
      return ((this.position.hashCode() * 31 + this.positionInModel.hashCode()) * 31 + this.rotation.hashCode()) * 31 + this.scaling.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      return this === other
         || other is BodyTransform
            && this.position == (other as BodyTransform).position
            && this.positionInModel == (other as BodyTransform).positionInModel
            && this.rotation == (other as BodyTransform).rotation
            && this.scaling == (other as BodyTransform).scaling
         }

   private fun writeReplace(): Any {
      return BodyTransformImpl.SerializationDelegate(this)
   }

   private fun readObject(s: ObjectInputStream) {
      throw InvalidObjectException("Deserialization via serialization delegate")
   }

   public override fun toString(): String {
      return "BodyTransform(position=${this.position}, rotation=${this.rotation}, scaling=${this.scaling}, positionInModel=${this.positionInModel})"
   }

   private class SerializationDelegate(transform: BodyTransform) : Externalizable {
      private final var transform: BodyTransform

      init {
         this.transform = transform
      }

      public override fun writeExternal(out: ObjectOutput) {
         this.transform.writeTransform(out)
      }

      public override fun readExternal(input: ObjectInput) {
         this.transform = BodyTransformFactory.INSTANCE.readFrom(input)
      }

      private fun readResolve(): Any {
         return this.transform
      }
   }
}
