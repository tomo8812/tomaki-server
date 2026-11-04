package org.valkyrienskies.core.internal.world

import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.util.PhysTickOnly
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.core.internal.joints.VSJoint

@VsBeta
@PhysTickOnly
public interface VsiPhysLevel : PhysLevel {
   public abstract fun getGravity(): Vector3dc {
   }

   public abstract fun getAllPhysShips(): Set<PhysShip> {
   }

   public abstract fun getJointById(jointId: Int): VSJoint? {
   }

   public abstract fun getJointsFromShip(shipId: Long): Set<Int> {
   }

   public abstract fun getAllJoints(): Map<Int, VSJoint> {
   }

   public abstract fun getJointsByShipIds(): Map<Long, Set<Int>> {
   }

   public abstract fun addJoint(newJoint: VSJoint): Int {
   }

   public abstract fun updateJoint(jointId: Int, updatedJoint: VSJoint): Boolean {
   }

   public abstract fun removeJoint(jointId: Int): Boolean {
   }
}
