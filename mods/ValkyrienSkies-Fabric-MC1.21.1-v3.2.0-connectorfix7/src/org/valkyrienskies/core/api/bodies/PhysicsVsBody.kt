package org.valkyrienskies.core.api.bodies

import org.joml.Vector3dc

public interface PhysicsVsBody : ServerBaseVsBody {
   @JvmStatic
   PhysicsVsBody.Companion Companion = PhysicsVsBody.Companion.$$INSTANCE;

   public open fun applyForceInWorld(force: Vector3dc) {
      this.applyForceInWorld("unspecified", force)
   }

   public open fun applyForceInLocal(force: Vector3dc) {
      this.applyForceInLocal("unspecified", force)
   }

   public open fun applyForceInWorld(force: Vector3dc, pos: Vector3dc) {
      this.applyForceInWorld("unspecified", force)
   }

   public open fun applyForceInLocal(force: Vector3dc, pos: Vector3dc) {
      this.applyForceInLocal("unspecified", force)
   }

   public open fun applyRotatingForceInWorld(force: Vector3dc) {
      this.applyRotatingForceInWorld("unspecified", force)
   }

   public open fun applyInvariantForceInLocal(force: Vector3dc) {
      this.applyInvariantForceInLocal("unspecified", force)
   }

   public open fun applyRotatingForceInWorld(force: Vector3dc, pos: Vector3dc) {
      this.applyRotatingForceInWorld("unspecified", force)
   }

   public open fun applyInvariantForceInLocal(force: Vector3dc, pos: Vector3dc) {
      this.applyInvariantForceInLocal("unspecified", force)
   }

   public open fun applyTorqueInWorld(torque: Vector3dc) {
      this.applyTorqueInWorld("unspecified", torque)
   }

   public open fun applyTorqueInLocal(torque: Vector3dc) {
      this.applyTorqueInLocal("unspecified", torque)
   }

   public open fun applyRotatingTorqueInWorld(torque: Vector3dc) {
      this.applyRotatingTorqueInWorld("unspecified", torque)
   }

   public open fun applyInvariantTorqueInLocal(torque: Vector3dc) {
      this.applyInvariantTorqueInLocal("unspecified", torque)
   }

   public abstract fun applyForceInWorld(reason: String, force: Vector3dc) {
   }

   public abstract fun applyForceInLocal(reason: String, force: Vector3dc) {
   }

   public abstract fun applyForceInWorld(reason: String, force: Vector3dc, pos: Vector3dc) {
   }

   public abstract fun applyForceInLocal(reason: String, force: Vector3dc, pos: Vector3dc) {
   }

   public abstract fun applyRotatingForceInWorld(reason: String, force: Vector3dc) {
   }

   public abstract fun applyInvariantForceInLocal(reason: String, force: Vector3dc) {
   }

   public abstract fun applyRotatingForceInWorld(reason: String, force: Vector3dc, pos: Vector3dc) {
   }

   public abstract fun applyInvariantForceInLocal(reason: String, force: Vector3dc, pos: Vector3dc) {
   }

   public abstract fun applyTorqueInWorld(reason: String, torque: Vector3dc) {
   }

   public abstract fun applyTorqueInLocal(reason: String, torque: Vector3dc) {
   }

   public abstract fun applyRotatingTorqueInWorld(reason: String, torque: Vector3dc) {
   }

   public abstract fun applyInvariantTorqueInLocal(reason: String, torque: Vector3dc) {
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      PhysicsVsBody.Companion $$INSTANCE = PhysicsVsBody.Companion();
      public const val REASON_UNKNOWN: String = "unspecified"
   }
}
