package org.valkyrienskies.core.api.ships

import org.joml.Matrix3dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.util.PhysTickOnly

@PhysTickOnly
public interface PhysShip : Ship {
   public var isStatic: Boolean

   public var buoyantFactor: Double
      public abstract get() {
      }

      public abstract set(<set-?>) {
      }


   public var doFluidDrag: Boolean
      public abstract get() {
      }

      public abstract set(<set-?>) {
      }


   public val mass: Double

   public val centerOfMass: Vector3dc
      public abstract get() {
      }


   public val momentOfInertia: Matrix3dc

   public val liquidOverlap: Double
      public abstract get() {
      }


   public val isSleeping: Boolean
      public abstract get() {
      }


   public abstract fun applyWorldForce(forceInWorld: Vector3dc, posInWorld: Vector3dc = var0.getKinematics().position) {
   }

   public abstract fun applyWorldTorque(torqueInWorld: Vector3dc) {
   }

   public abstract fun applyModelForce(forceInShip: Vector3dc, posInShip: Vector3dc = var0.getKinematics().positionInModel) {
   }

   public abstract fun applyModelTorque(torqueInShip: Vector3dc) {
   }

   public abstract fun applyWorldForceToModelPos(forceInWorld: Vector3dc, posInShip: Vector3dc = var0.getKinematics().positionInModel) {
   }

   public abstract fun applyBodyForce(forceInBody: Vector3dc, posInBody: Vector3dc = Vector3d() as Vector3dc) {
   }

   public abstract fun applyBodyTorque(torqueInBody: Vector3dc) {
   }

   public abstract fun applyWorldForceToBodyPos(forceInWorld: Vector3dc, posInBody: Vector3dc = Vector3d() as Vector3dc) {
   }

   @Deprecated(message = "Use applyModelForce or applyBodyForce instead", replaceWith = @ReplaceWith(expression = "applyBodyForce(force, posInBody)", imports = []))
   public abstract fun applyRotDependentForce(force: Vector3dc) {
   }

   @Deprecated(message = "Use applyWorldForce instead", replaceWith = @ReplaceWith(expression = "applyWorldForce(force, posInWorld)", imports = []))
   public abstract fun applyInvariantForce(force: Vector3dc) {
   }

   @Deprecated(message = "Use applyModelForce or applyBodyForce instead", replaceWith = @ReplaceWith(expression = "applyBodyForce(force, pos)", imports = []))
   public abstract fun applyRotDependentForceToPos(force: Vector3dc, pos: Vector3dc) {
   }

   @Deprecated(message = "Use applyWorldForce instead", replaceWith = @ReplaceWith(expression = "applyWorldForce(force, pos)", imports = []))
   public abstract fun applyInvariantForceToPos(force: Vector3dc, pos: Vector3dc) {
   }

   @Deprecated(message = "Use applyModelTorque or applyBodyTorque instead", replaceWith = @ReplaceWith(expression = "applyBodyTorque(torque)", imports = []))
   public abstract fun applyRotDependentTorque(torque: Vector3dc) {
   }

   @Deprecated(message = "Use applyWorldTorque instead", replaceWith = @ReplaceWith(expression = "applyWorldTorque(torque)", imports = []))
   public abstract fun applyInvariantTorque(torque: Vector3dc) {
   }
}
