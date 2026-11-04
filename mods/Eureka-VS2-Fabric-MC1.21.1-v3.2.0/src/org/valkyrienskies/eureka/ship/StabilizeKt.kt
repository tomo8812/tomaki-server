package org.valkyrienskies.eureka.ship

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.eureka.EurekaConfig

private final val WORLD_UP: Vector3dc = Vector3d(0.0, 1.0, 0.0) as Vector3dc

public fun stabilize(ship: PhysShip, omega: Vector3dc, vel: Vector3dc, forces: PhysShip, linear: Boolean, yaw: Boolean): Double {
   val shipUp: Vector3d = Vector3d(0.0, 1.0, 0.0)
   ship.getTransform().getShipToWorldRotation().transform(shipUp)
   val angleBetween: Double = shipUp.angle(WORLD_UP)
   val idealAngularAcceleration: Vector3d = Vector3d()
   if (angleBetween > 0.01) {
      val stabilizationTorque: Vector3d = shipUp.cross(WORLD_UP, Vector3d()).normalize()
      idealAngularAcceleration.add(stabilizationTorque.mul(angleBetween, stabilizationTorque) as Vector3dc)
   }

   idealAngularAcceleration.sub(omega.x(), if (!yaw) 0.0 else omega.y(), omega.z())
   val var16: Vector3d = ship.getTransform()
      .getShipToWorldRotation()
      .transform(ship.getMomentOfInertia().transform(ship.getTransform().getShipToWorldRotation().transformInverse(idealAngularAcceleration)))
      val speed: Double = ship.getVelocity().length()
   var16.mul(
      EurekaConfig.SERVER.stabilizationTorqueConstant
         / Math.max(1.0, speed * speed * EurekaConfig.SERVER.scaledInstability / ship.getMass() + speed * EurekaConfig.SERVER.unscaledInstability)
   )
   forces.applyInvariantTorque(var16 as Vector3dc)
   if (linear) {
      val idealVelocity: Vector3d = Vector3d(vel).negate()
      idealVelocity.y = 0.0
      val s: Double = EurekaConfig.SERVER.linearStabilizeMaxAntiVelocity
         * (1 - 1 / smoothingATanMax(EurekaConfig.SERVER.linearMaxMass, ship.getMass() * EurekaConfig.SERVER.linearMassScaling + 1.0))
         / 10.0
         if (idealVelocity.lengthSquared() > s * s) {
         idealVelocity.normalize(s)
      }

      idealVelocity.mul(ship.getMass() * ((double)10 - EurekaConfig.SERVER.antiVelocityMassRelevance))
      forces.applyInvariantForce(idealVelocity as Vector3dc)
      return idealVelocity.length()
   } else {
      return 0.0
   }
}

private fun smoothingATan(smoothing: Double, x: Double): Double {
   return Math.atan(x * smoothing) / smoothing
}

private fun smoothingATanMax(max: Double, x: Double): Double {
   return smoothingATan((double)1 / (max * 0.638), x)
}
