package org.valkyrienskies.core.impl.shadow

import org.joml.Matrix4dc
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.api.LoadedShipInternal
import org.valkyrienskies.core.impl.api.ShipInternal
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon

public open class Eq(p0: ShipDataCommon) : LoadedShipInternal, ShipInternal {
   @JvmSynthetic
   private ShipDataCommon b;
   public open val l: ShipDataCommon

   init {
      this.b = var1
      this.c = var1
   }

   public open val a: IShipActiveChunksSet

   public open val b: Vector3dc
      public open get() {
         return this.b.getAngularVelocity()
      }


   public open val c: ChunkClaim

   public open var d: String

   public open val e: Long

   public open val f: BodyKinematics

   public open val g: Vector3dc
      public open get() {
         return this.b.getOmega()
      }


   public open val h: ShipTransform
      public open get() {
         return this.b.getPrevTickShipTransform()
      }


   public open val i: ShipTransform

   public open val j: AABBic?

   public open val k: IShipActiveChunksSet
      public open get() {
         return this.b.getShipActiveChunksSet()
      }


   public open val m: Matrix4dc
      public open get() {
         return this.b.getShipToWorld()
      }


   public open val n: ShipTransform
      public open get() {
         return this.b.getShipTransform()
      }


   public open val o: AABBic?
      public open get() {
         return this.b.getShipVoxelAABB()
      }


   public open val p: String?

   public open val q: ShipTransform
      public open get() {
         return this.b.getTransform()
      }


   public open val r: Vector3dc
      public open get() {
         return this.b.getVelocity()
      }


   public open val s: AABBdc

   public open val t: Matrix4dc
      public open get() {
         return this.b.getWorldToShip()
      }


   public override fun onSetBlock(p0: Int, p1: Int, p2: Int, p3: Boolean, p4: Double, p5: Double, p6: Boolean) {
      this.b.onSetBlock(posX, posY, posZ, newBlockIsAir, oldBlockMass, newBlockMass, isRunningOnServer)
   }

   public override fun setFromTransform(p0: BodyTransform) {
      this.b.setFromTransform(transform)
   }

   public override fun unsafeSetKinematics(p0: BodyKinematics) {
      this.b.unsafeSetKinematics(kinematics)
   }

   @JvmStatic
   fun d(): Ft {
      a
   }

   public companion object a {
      @JvmStatic
      public final val a: Ft
   }
}
