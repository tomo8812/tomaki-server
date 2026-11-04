package org.valkyrienskies.core.api.ships

import org.joml.Vector3dc
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.util.GameTickOnly

@VsBeta
@GameTickOnly
public interface DragController {
   public abstract fun enableDrag() {
   }

   public abstract fun disableDrag() {
   }

   public abstract fun enableLift() {
   }

   public abstract fun disableLift() {
   }

   public abstract fun enableRotDrag() {
   }

   public abstract fun disableRotDrag() {
   }

   public abstract fun getDragForce(): Vector3dc {
   }

   public abstract fun getLiftForce(): Vector3dc {
   }

   public abstract fun setWindDirection(x: Double, y: Double, z: Double, identifier: String = "default") {
   }

   public open fun setWindDirection(direction: Vector3dc, identifier: String = "default") {
      this.setWindDirection(direction.x(), direction.y(), direction.z(), identifier)
   }

   public abstract fun setWindSpeed(speed: Double, identifier: String = "default") {
   }

   public abstract fun applyWindImpulse(direction: Vector3dc, speed: Double) {
   }

   public abstract fun getWindVector(): Vector3dc {
   }
}
