package org.valkyrienskies.core.impl.shadow

import com.fasterxml.jackson.databind.JsonNode
import org.joml.Matrix4dc
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.ClientShipTransformProvider
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.api.ClientShipInternal
import org.valkyrienskies.core.impl.api.ShipInternal
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.util.serialization.VSJacksonUtil
import org.valkyrienskies.core.util.AABBdUtilKt

public class Er(p0: ShipDataCommon, p1: JsonNode = VSJacksonUtil.INSTANCE.defaultMapper.valueToTree(var1)) : Eq(var1), ClientShipInternal, ShipInternal {
   @JvmSynthetic
   private ShipDataCommon h;
   public final var i: BodyKinematics

   public open lateinit var n: ShipTransform
      private set

   public open lateinit var m: AABBdc
      private set

   public open var w: ClientShipTransformProvider?
   public final var h: BodyKinematics
   public final var g: Int
   public final val q: Fr<JsonNode>

   init {
      this.h = var1
      this.e = var1.kinematics
      this.f = Integer.MIN_VALUE
      this.g = Fr<>(a, var2)
      this.b = var1.kinematics
      this.c = var1.getTransform()
      this.d = var1.getTransform().createEmptyAABB()
   }

   public open val a: IShipActiveChunksSet

   public open val b: Vector3dc
      public open get() {
         return this.h.getAngularVelocity()
      }


   public open val c: ChunkClaim

   public open var d: String

   public open val e: Long

   public open val f: BodyKinematics

   public open val j: Vector3dc
      public open get() {
         return this.h.getOmega()
      }


   public open val k: ShipTransform
      public open get() {
         return this.h.getPrevTickShipTransform()
      }


   public open val l: ShipTransform

   public open val o: AABBic?

   public open val p: IShipActiveChunksSet
      public open get() {
         return this.h.getShipActiveChunksSet()
      }


   public open val r: Matrix4dc
      public open get() {
         return this.h.getShipToWorld()
      }


   public open val s: ShipTransform
      public open get() {
         return this.h.getShipTransform()
      }


   public open val t: AABBic?
      public open get() {
         return this.h.getShipVoxelAABB()
      }


   public open val u: String?

   public open val v: ShipTransform
      public open get() {
         return this.h.getTransform()
      }


   public open val x: Vector3dc
      public open get() {
         return this.h.getVelocity()
      }


   public open val y: AABBdc

   public open val z: Matrix4dc
      public open get() {
         return this.h.getWorldToShip()
      }


   public override fun onSetBlock(p0: Int, p1: Int, p2: Int, p3: Boolean, p4: Double, p5: Double, p6: Boolean) {
      this.h.onSetBlock(posX, posY, posZ, newBlockIsAir, oldBlockMass, newBlockMass, isRunningOnServer)
   }

   public override fun setFromTransform(p0: BodyTransform) {
      this.h.setFromTransform(transform)
   }

   public override fun unsafeSetKinematics(p0: BodyKinematics) {
      this.h.unsafeSetKinematics(kinematics)
   }

   public fun e() {
      this.b = this.e
      val var10000: ClientShipTransformProvider = this.w
      var var2: BodyTransform
      if (var10000 != null) {
         val var10001: ShipTransform = this.i
         val var10002: ShipTransform = this.l.getTransform()
         val var10003: BodyTransform = this.b.transform
         var2 = var10000.provideNextTransform(var10001, var10002, var10003 as ShipTransform)
      } else {
         var2 = null
      }

      if (var2 == null) {
         val var3: ShipTransform = this.i
         val var4: ShipTransform = this.l.getTransform()
         val var5: BodyTransform = this.b.transform
         var2 = Eg.a(var3, var4, var5 as ShipTransform)
      }

      this.l.updatePrevTickShipTransform()
      this.l.kinematics = this.e.withTransform(var2)
   }

   public fun a(p0: Double) {
      val var10000: ClientShipTransformProvider = this.w
      val var3: BodyTransform = if (var10000 != null) var10000.provideNextRenderTransform(this.l.prevTickTransform, this.l.getTransform(), var1) else null
      var var10001: BodyTransform = var3
      if (var3 == null) {
         var10001 = Eg.a(this.l.prevTickTransform, this.l.getTransform(), var1)
      }

      run label34@{
         this.c = var10001 as ShipTransform
         val var5: AABBic = this.l.shipAABB
         if (var5 != null) {
            val var6: AABBd = AABBdUtilKt.toAABBd(var5, AABBd())
            if (var6 != null) {
               var7 = var6.transform(this.n.shipToWorld, AABBd())
               return@label34
            }
         }

         var7 = null
      }

      this.d = if (var7 == null) this.n.createEmptyAABB() else var7
   }

   public companion object a_ {
      public const val a: Double = 0.7
   }
}
