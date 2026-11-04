package org.valkyrienskies.core.impl.bodies.properties

import java.io.DataInput
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.bodies.properties.BodyTransform.Builder
import org.valkyrienskies.core.api.ships.properties.ShipTransform

public object BodyTransformFactory : BodyTransform.Factory {
   public override fun newBuilder(): Builder {
      return BodyTransformBuilderImpl(null, null, null, null, 15, null)
   }

   public override fun newBuilder(from: BodyTransform): Builder {
      return this.newBuilder().from(from)
   }

   public open fun readFrom(from: DataInput): ShipTransform {
      return this.create(
         Vector3d(from.readDouble(), from.readDouble(), from.readDouble()),
         Quaterniond(from.readDouble(), from.readDouble(), from.readDouble(), from.readDouble()),
         Vector3d(from.readDouble(), from.readDouble(), from.readDouble()),
         Vector3d(from.readDouble(), from.readDouble(), from.readDouble())
      )
   }

   public open fun create(position: Vector3dc, rotation: Quaterniondc, scaling: Vector3dc, positionInModel: Vector3dc): ShipTransform {
      return BodyTransformImpl(position, rotation, scaling, positionInModel)
   }

   public open fun createFromSlerp(previous: BodyTransform, current: BodyTransform, alpha: Double): ShipTransform {
      val var5: Vector3dc = current.positionInModel
      val var9: Vector3d = previous.toWorld
         .transformDirection(current.positionInModel.sub(previous.positionInModel, Vector3d()), Vector3d())
         .add(previous.position)
         .lerp(current.position, alpha, Vector3d())
         val var7: Quaterniond = previous.rotation.slerp(current.rotation, alpha, Quaterniond()).normalize()
      val var8: Vector3d = previous.scaling.lerp(current.scaling, alpha, Vector3d())
      val var10001: Vector3dc = var9
      val var10002: Quaterniondc = var7
      return this.create(var10001, var10002, var8, var5)
   }

   public override fun build(builder: (Builder) -> Unit): BodyTransform {
      val var2: BodyTransform.Builder = this.newBuilder()
      builder(var2)
      return var2.build()
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun readFrom(from: DataInput): BodyTransform {
      this.readFrom(from) as BodyTransform
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun create(position: Vector3dc, rotation: Quaterniondc, scaling: Vector3dc, positionInModel: Vector3dc): BodyTransform {
      this.create(position, rotation, scaling, positionInModel) as BodyTransform
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override final fun createFromSlerp(previous: BodyTransform, current: BodyTransform, alpha: Double): BodyTransform {
      this.createFromSlerp(previous, current, alpha) as BodyTransform
   }
}
