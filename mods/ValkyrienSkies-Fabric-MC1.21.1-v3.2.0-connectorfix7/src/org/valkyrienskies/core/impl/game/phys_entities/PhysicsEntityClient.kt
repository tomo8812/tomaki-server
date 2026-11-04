package org.valkyrienskies.core.impl.game.phys_entities

import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.core.internal.physics.VSCollisionShapeData

public class PhysicsEntityClient(id: Long,
   dimensionId: String,
   shipTransform: ShipTransform,
   prevTickShipTransform: ShipTransform,
   collisionShapeData: VSCollisionShapeData
) {
   public final val id: Long
   public final val dimensionId: String
   public final var shipTransform: ShipTransform
   public final var prevTickShipTransform: ShipTransform
   public final var collisionShapeData: VSCollisionShapeData
   public final var nextShipTransform: ShipTransform

   public final var renderTransform: ShipTransform
      private set

   public final var renderAABB: AABBdc
      private set

   public final var latestNetworkTransform: ShipTransform
   public final var latestNetworkTTick: Int

   init {
      this.id = id
      this.dimensionId = dimensionId
      this.shipTransform = shipTransform
      this.prevTickShipTransform = prevTickShipTransform
      this.collisionShapeData = collisionShapeData
      this.nextShipTransform = this.shipTransform
      this.renderTransform = this.shipTransform
      this.renderAABB = this.shipTransform.createEmptyAABB()
      this.latestNetworkTransform = this.shipTransform
      this.latestNetworkTTick = Integer.MIN_VALUE
   }

   public fun tickUpdateShipTransform() {
      this.nextShipTransform = this.latestNetworkTransform
      this.prevTickShipTransform = this.shipTransform
      this.shipTransform = ShipTransformImpl.Companion.createFromSlerp(this.shipTransform, this.nextShipTransform, 0.7)
   }

   public fun updateRenderShipTransform(partialTicks: Double) {
      this.renderTransform = ShipTransformImpl.Companion.createFromSlerp(this.prevTickShipTransform, this.shipTransform, partialTicks)
      this.renderAABB = this.renderTransform.createEmptyAABB()
   }
}
