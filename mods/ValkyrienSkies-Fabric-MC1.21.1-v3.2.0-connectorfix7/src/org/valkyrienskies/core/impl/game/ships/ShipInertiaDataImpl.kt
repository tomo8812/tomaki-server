package org.valkyrienskies.core.impl.game.ships

import org.joml.Matrix3d
import org.joml.Matrix3dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.impl.shadow.Em

public data class ShipInertiaDataImpl(_centerOfMassInShip: Vector3d, _mass: Double, _momentOfInertiaTensor: Matrix3d) : ShipInertiaData {
   private final val _centerOfMassInShip: Vector3d
   private final var _mass: Double
   private final val _momentOfInertiaTensor: Matrix3d
   private final var momentOfInertiaSpherical: Matrix3d
   public final var cmChangedThisTick: Boolean

   init {
      this._centerOfMassInShip = _centerOfMassInShip
      this._mass = _mass
      this._momentOfInertiaTensor = _momentOfInertiaTensor
      this.momentOfInertiaSpherical = Matrix3d(this._momentOfInertiaTensor)
      this.updateSphericalMOI()
   }

   public open val inertiaTensor: Matrix3dc
      public open get() {
         return this.momentOfInertiaSpherical
      }


   public final val momentOfInertiaTensorToSave: Matrix3dc
      public final get() {
         return this._momentOfInertiaTensor
      }


   public open val centerOfMass: Vector3dc
      public open get() {
         return this._centerOfMassInShip
      }


   public open val mass: Double
      public open get() {
         return this._mass
      }


   public fun onSetBlock(posX: Int, posY: Int, posZ: Int, oldBlockMass: Double, newBlockMass: Double) {
      val var8: Double
      if (!(Math.abs(var8 = newBlockMass - oldBlockMass) < 1.0E-6)) {
         val var10: Double = var8 / 8.0
         val var12: Double = posX + 0.5
         val var14: Double = posY + 0.5
         val var16: Double = posZ + 0.5
         this.addMassAt(var12 + 0.4, var14 + 0.4, (double)posZ + 0.5 + 0.4, var10)
         this.addMassAt(var12 + 0.4, var14 + 0.4, var16 - 0.4, var10)
         this.addMassAt(var12 + 0.4, var14 - 0.4, var16 + 0.4, var10)
         this.addMassAt(var12 + 0.4, var14 - 0.4, var16 - 0.4, var10)
         this.addMassAt(var12 - 0.4, var14 + 0.4, var16 + 0.4, var10)
         this.addMassAt(var12 - 0.4, var14 + 0.4, var16 - 0.4, var10)
         this.addMassAt(var12 - 0.4, var14 - 0.4, var16 + 0.4, var10)
         this.addMassAt(var12 - 0.4, var14 - 0.4, var16 - 0.4, var10)
      }
   }

   public fun onSetBlockUseSphereMOI(posX: Int, posY: Int, posZ: Int, oldBlockMass: Double, newBlockMass: Double) {
      this.onSetBlock(posX, posY, posZ, oldBlockMass, newBlockMass)
      this.updateSphericalMOI()
   }

   private fun updateSphericalMOI() {
      this.momentOfInertiaSpherical
         .identity()
         .scale(
            (
                  this._momentOfInertiaTensor.transform(Vector3d(1.0, 0.0, 0.0)).length()
                     + this._momentOfInertiaTensor.transform(Vector3d(0.0, 1.0, 0.0)).length()
                     + this._momentOfInertiaTensor.transform(Vector3d(0.0, 0.0, 1.0)).length()
               )
               / 3.0
         )
      }

   private fun addMassAt(x: Double, y: Double, z: Double, addedMass: Double) {
      val var9: DoubleArray = DoubleArray(9)
      val var10000: Matrix3d = this._momentOfInertiaTensor.transpose(Matrix3d())
      var10000.get(var9)
      val var11: Double = this.mass
      val var10: Vector3d = Vector3d(this.getCenterOfMassInShip())
      if (var11 + addedMass > 1.0E-6) {
         val var27: Vector3d = this.getCenterOfMassInShip().mul(var11, Vector3d())
         var27.add(x * addedMass, y * addedMass, z * addedMass)
         var27.mul(1.0 / (var11 + addedMass))
         this._centerOfMassInShip.set(var27)
         val var15: Double = var10.x - this.getCenterOfMassInShip().x()
         val var17: Double = var10.y - this.getCenterOfMassInShip().y()
         val var19: Double = var10.z - this.getCenterOfMassInShip().z()
         val var21: Double = x - this.getCenterOfMassInShip().x()
         val var23: Double = y - this.getCenterOfMassInShip().y()
         val var25: Double = z - this.getCenterOfMassInShip().z()
         var9[0] = var9[0] + (var17 * var17 + var19 * var19) * var11 + (var23 * var23 + var25 * var25) * addedMass
         var9[1] = var9[1] - var15 * var17 * var11 - var21 * var23 * addedMass
         var9[2] = var9[2] - var15 * var19 * var11 - var21 * var25 * addedMass
         var9[3] = var9[1]
         var9[4] = var9[4] + (var15 * var15 + var19 * var19) * var11 + (var21 * var21 + var25 * var25) * addedMass
         var9[5] = var9[5] - var17 * var19 * var11 - var23 * var25 * addedMass
         var9[6] = var9[2]
         var9[7] = var9[5]
         var9[8] = var9[8] + (var15 * var15 + var17 * var17) * var11 + (var21 * var21 + var23 * var23) * addedMass
         this._momentOfInertiaTensor.set(var9).transpose()
         this._mass += addedMass
      } else {
         this._centerOfMassInShip.set(x, y, z)
         this._momentOfInertiaTensor.zero()
         this._mass = 0.0
      }

      this.cmChangedThisTick = true
   }

   public fun copyToPhyInertia(scaling: Double = 1.0): Em {
      val var10002: Double = this.mass * scaling * scaling * scaling
      val var10003: Matrix3d = Matrix3d(this.momentOfInertiaSpherical).scale(scaling * scaling * scaling * scaling * scaling)
      return Em(var10002, var10003, Vector3d(this.getCenterOfMassInShip()), this.cmChangedThisTick)
   }

   private operator fun component1(): Vector3d {
      return this._centerOfMassInShip
   }

   private operator fun component2(): Double {
      return this._mass
   }

   private operator fun component3(): Matrix3d {
      return this._momentOfInertiaTensor
   }

   public fun copy(
      _centerOfMassInShip: Vector3d = this._centerOfMassInShip,
      _mass: Double = this._mass,
      _momentOfInertiaTensor: Matrix3d = this._momentOfInertiaTensor
   ): ShipInertiaDataImpl {
      return ShipInertiaDataImpl(_centerOfMassInShip, _mass, _momentOfInertiaTensor)
   }

   public override fun toString(): String {
      return "ShipInertiaDataImpl(_centerOfMassInShip=${this._centerOfMassInShip}, _mass=${this._mass}, _momentOfInertiaTensor=${this._momentOfInertiaTensor})"
   }

   public override fun hashCode(): Int {
      return (this._centerOfMassInShip.hashCode() * 31 + java.lang.Double.hashCode(this._mass)) * 31 + this._momentOfInertiaTensor.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is ShipInertiaDataImpl
            && this._centerOfMassInShip == (other as ShipInertiaDataImpl)._centerOfMassInShip
            && java.lang.Double.compare(this._mass, (other as ShipInertiaDataImpl)._mass) == 0
            && this._momentOfInertiaTensor == (other as ShipInertiaDataImpl)._momentOfInertiaTensor
         }
   }

   public companion object {
      private const val EPSILON: Double = 1.0E-6
      private const val INERTIA_OFFSET: Double = 0.4

      public fun newEmptyShipInertiaData(): ShipInertiaDataImpl {
         return ShipInertiaDataImpl(Vector3d(), 0.0, Matrix3d())
      }
   }
}
