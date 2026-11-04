package org.valkyrienskies.core.impl.game.ships

import java.util.ArrayDeque
import java.util.Queue
import kotlin.jvm.internal.MutablePropertyReference0
import kotlin.jvm.internal.MutablePropertyReference0Impl
import kotlin.jvm.internal.Reflection
import org.joml.Matrix3dc
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.api.bodies.properties.BodyTransform
import org.valkyrienskies.core.api.ships.DragController
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ShipPhysicsListener
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.api.ships.properties.PhysPose
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.impl.bodies.properties.BodyKinematicsFactory
import org.valkyrienskies.core.impl.shadow.Em
import org.valkyrienskies.core.impl.shadow.Ey
import org.valkyrienskies.core.impl.shadow.FV
import org.valkyrienskies.core.impl.shadow.Ge
import org.valkyrienskies.core.impl.shadow.Hc
import org.valkyrienskies.core.impl.shadow.Hf
import org.valkyrienskies.core.impl.shadow.Hg
import org.valkyrienskies.core.internal.ships.VsiPhysShip

public data class PhysShipImpl(id: Long,
      rigidBodyReference: Hc<*>,
      physicsListeners: List<ShipPhysicsListener>,
      inertia: Em,
      _poseVel: Hg,
      lastShipTeleportId: Int,
      physicsThreadChecker: FV,
      wingManager: Ey = Ey()
   ) :
   VsiPhysShip {
   public open val id: Long
   internal final val rigidBodyReference: Hc<*>
   internal final var physicsListeners: List<ShipPhysicsListener>
   internal final var inertia: Em
   private final var _poseVel: Hg
   internal final var lastShipTeleportId: Int
   private final val physicsThreadChecker: FV
   internal final val wingManager: Ey

   public open lateinit var kinematics: BodyKinematics
      private set

   private final val invForces: ArrayDeque<Vector3dc>
   private final val invTorques: ArrayDeque<Vector3dc>
   private final val rotForces: ArrayDeque<Vector3dc>
   private final val rotTorques: ArrayDeque<Vector3dc>
   private final val invPosForces: ArrayDeque<Vector3dc>
   private final val invPosPositions: ArrayDeque<Vector3dc>

   init {
      this.id = id
      this.rigidBodyReference = rigidBodyReference
      this.physicsListeners = physicsListeners
      this.inertia = inertia
      this._poseVel = _poseVel
      this.lastShipTeleportId = lastShipTeleportId
      this.physicsThreadChecker = physicsThreadChecker
      this.wingManager = wingManager
      this.updatePhysTransform()
      this.invForces = ArrayDeque<>()
      this.invTorques = ArrayDeque<>()
      this.rotForces = ArrayDeque<>()
      this.rotTorques = ArrayDeque<>()
      this.invPosForces = ArrayDeque<>()
      this.invPosPositions = ArrayDeque<>()
   }

   public final var poseVel: Hg
      public final get() {
         return this._poseVel
      }

      public final set(poseVel) {
         this._poseVel = poseVel
         this.updatePhysTransform()
      }


   public open var isStatic: Boolean
      public open get() {
         return this.rigidBodyReference.f()
      }

      public open set(<set-?>) {
         this.rigidBodyReference.a(`<set-?>`)
      }


   @JvmStatic
   fun `isStatic$delegate`(`<this>`: PhysShipImpl) {
      Reflection.mutableProperty0(
         MutablePropertyReference0Impl(`<this>`.rigidBodyReference, Hc.class, "isStatic", "isStatic()Z", 0) as MutablePropertyReference0
      )
   }

   public open var kinematicTarget: PhysPose?
      public open get() {
         val var10000: Hf = this.rigidBodyReference.o()
         val var3: PhysPoseImpl
         if (var10000 != null) {
            var3 = PhysPoseImpl(var10000.a, var10000.b)
         } else {
            var3 = null
         }

         return var3
      }

      public open set(value) {
         val var3: Hf
         if (value != null) {
            var3 = Hf(value.pos, value.rot)
         } else {
            var3 = null
         }

         this.rigidBodyReference.a(var3)
      }


   public open val dragController: DragController?
      public open get() {
         val var4: java.util.Iterator = this.physicsListeners.iterator()

         var var10000: Any
         while (true) {
            if (var4.hasNext()) {
               val var2: Any
               if (((var2 = var4.next()) as ShipPhysicsListener) !is DragController) {
                  continue
               }

               var10000 = var2
               break
            }

            var10000 = null
            break
         }

         return var10000 as? DragController
      }


   @VsBeta
   public open var buoyantFactor: Double
      public open get() {
         return this.rigidBodyReference.l()
      }

      public open set(<set-?>) {
         this.rigidBodyReference.e(`<set-?>`)
      }


   @JvmStatic
   fun `getBuoyantFactor$delegate`(`<this>`: PhysShipImpl) {
      Reflection.mutableProperty0(
         MutablePropertyReference0Impl(`<this>`.rigidBodyReference, Hc.class, "buoyantFactor", "getBuoyantFactor()D", 0) as MutablePropertyReference0
      )
   }

   @VsBeta
   public open var doFluidDrag: Boolean
      public open get() {
         return this.rigidBodyReference.m()
      }

      public open set(<set-?>) {
         this.rigidBodyReference.b(`<set-?>`)
      }


   @JvmStatic
   fun `getDoFluidDrag$delegate`(`<this>`: PhysShipImpl) {
      Reflection.mutableProperty0(
         MutablePropertyReference0Impl(`<this>`.rigidBodyReference, Hc.class, "doFluidDrag", "getDoFluidDrag()Z", 0) as MutablePropertyReference0
      )
   }

   public open val momentOfInertia: Matrix3dc
      public open get() {
         return this.inertia.f
      }


   @VsBeta
   public open val liquidOverlap: Double
      public open get() {
         return this.rigidBodyReference.s()
      }


   @VsBeta
   public open val isSleeping: Boolean
      public open get() {
         return this.rigidBodyReference.r()
      }


   public override fun applyWorldForce(forceInWorld: Vector3dc, posInWorld: Vector3dc) {
      if (this.canApplyWrenches(this.isStatic, forceInWorld, posInWorld)) {
         posInWorld = this.kinematics.transform.toModel.transformPosition(posInWorld, Vector3d())
         this.applyWorldForceToModelPos(forceInWorld, posInWorld)
      }
   }

   public override fun applyWorldForceToModelPos(forceInWorld: Vector3dc, posInShip: Vector3dc) {
      if (this.canApplyWrenches(this.isStatic, forceInWorld, posInShip)) {
         posInShip = Vector3d(posInShip).sub(this.kinematics.positionInModel)
         this.applyWorldForceToBodyPos(forceInWorld, posInShip)
      }
   }

   public override fun applyWorldForceToBodyPos(forceInWorld: Vector3dc, posInBody: Vector3dc) {
      if (this.canApplyWrenches(this.isStatic, forceInWorld, posInBody)) {
         this.invPosPositions.add(posInBody)
         this.invPosForces.add(forceInWorld)
      }
   }

   public override fun applyWorldTorque(torqueInWorld: Vector3dc) {
      if (canApplyWrenches$default(this, this.isStatic, torqueInWorld, null, 4, null)) {
         this.invTorques.add(torqueInWorld)
      }
   }

   public override fun applyModelForce(forceInShip: Vector3dc, posInShip: Vector3dc) {
      if (this.canApplyWrenches(this.isStatic, forceInShip, posInShip)) {
         posInShip = Vector3d(posInShip).sub(this.kinematics.positionInModel)
         this.applyBodyForce(forceInShip, posInShip)
      }
   }

   public override fun applyModelTorque(torqueInShip: Vector3dc) {
      if (canApplyWrenches$default(this, this.isStatic, torqueInShip, null, 4, null)) {
         this.applyBodyTorque(torqueInShip)
      }
   }

   public override fun applyBodyForce(forceInBody: Vector3dc, posInBody: Vector3dc) {
      if (this.canApplyWrenches(this.isStatic, forceInBody, posInBody)) {
         val var10000: ArrayDeque = this.invPosForces
         val var10002: Vector3d = this.scaleForceToWorld(forceInBody)
         var10000.add(this.rotateForceToWorld(var10002))
         this.invPosPositions.add(posInBody)
      }
   }

   public override fun applyBodyTorque(torqueInBody: Vector3dc) {
      if (canApplyWrenches$default(this, this.isStatic, torqueInBody, null, 4, null)) {
         this.rotTorques.add(this.scaleTorqueToWorld(torqueInBody))
      }
   }

   private fun Vector3dc.rotateForceToWorld(): Vector3d {
      return this.poseVel.c.transform(`$this$rotateForceToWorld`, Vector3d())
   }

   private fun Vector3dc.scaleForceToWorld(): Vector3d {
      val var10000: Vector3d = Vector3d(`$this$scaleForceToWorld`)
      `$this$scaleForceToWorld` = this.kinematics.scaling
      return var10000.mul(`$this$scaleForceToWorld`.x() * `$this$scaleForceToWorld`.y() * `$this$scaleForceToWorld`.z())
   }

   private fun Vector3dc.scaleTorqueToWorld(): Vector3d {
      val var10000: Vector3d = Vector3d(`$this$scaleTorqueToWorld`)
      `$this$scaleTorqueToWorld` = this.kinematics.scaling
      return var10000.mul(
         `$this$scaleTorqueToWorld`.x()
            * `$this$scaleTorqueToWorld`.x()
            * `$this$scaleTorqueToWorld`.x()
            * `$this$scaleTorqueToWorld`.x()
            * `$this$scaleTorqueToWorld`.x()
      )
   }

   private fun canApplyWrenches(isStatic: Boolean, vec0: Vector3dc, vec1: Vector3dc? = null): Boolean {
      Ge.a(vec0)
      if (vec1 != null) {
         Ge.a(vec1)
      }

      this.physicsThreadChecker.assertIsPhysicsThread()
      return !isStatic
   }

   public open val centerOfMass: Vector3dc
      public open get() {
         return this.inertia.b
      }


   public open val mass: Double
      public open get() {
         return this.inertia.e
      }


   public open val velocity: Vector3dc
      public open get() {
         return this.poseVel.d
      }


   public open val angularVelocity: Vector3dc
      public open get() {
         return this.poseVel.e
      }


   public fun applyQueuedForces() {
      if (!this.isStatic) {
         var var1: Queue = this.invForces
         var var2: Hc = this.rigidBodyReference

         while (true) {
            var var10000: Any = var1.poll()
            if (var10000 == null) {
               var1 = this.invTorques
               var2 = this.rigidBodyReference

               while (true) {
                  var10000 = var1.poll()
                  if (var10000 == null) {
                     var1 = this.rotForces
                     var2 = this.rigidBodyReference

                     while (true) {
                        var10000 = var1.poll()
                        if (var10000 == null) {
                           var1 = this.rotTorques
                           var2 = this.rigidBodyReference

                           while (true) {
                              var10000 = var1.poll()
                              if (var10000 == null) {
                                 while (!this.invPosForces.isEmpty()) {
                                    val var7: Vector3dc = this.invPosPositions.removeFirst()
                                    val var11: Vector3dc = this.invPosForces.removeFirst()
                                    val var18: Hc = this.rigidBodyReference
                                    var18.a(var7, var11)
                                 }

                                 if (!this.invPosPositions.isEmpty()) {
                                    throw IllegalStateException("Check failed.")
                                 } else if (!this.invPosForces.isEmpty()) {
                                    throw IllegalStateException("Check failed.")
                                 } else {
                                    return
                                 }
                              }

                              var2.b(var10000 as Vector3dc)
                           }
                        }

                        var2.d(var10000 as Vector3dc)
                     }
                  }

                  var2.c(var10000 as Vector3dc)
               }
            }

            var2.e(var10000 as Vector3dc)
         }
      }
   }

   @Deprecated(message = "Use applyWorldForce instead", replaceWith = @ReplaceWith(expression = "applyWorldForce(force, posInWorld)", imports = []))
   public override fun applyInvariantForce(force: Vector3dc) {
      PhysShip.applyWorldForceToBodyPos$default(this, force, null, 2, null)
   }

   @Deprecated(message = "Use applyWorldTorque instead", replaceWith = @ReplaceWith(expression = "applyWorldTorque(torque)", imports = []))
   public override fun applyInvariantTorque(torque: Vector3dc) {
      this.applyWorldTorque(torque)
   }

   @Deprecated(message = "Use applyModelForce or applyBodyForce instead", replaceWith = @ReplaceWith(expression = "applyBodyForce(force, posInBody)", imports = []))
   public override fun applyRotDependentForce(force: Vector3dc) {
      PhysShip.applyBodyForce$default(this, force, null, 2, null)
   }

   @Deprecated(message = "Use applyModelTorque or applyBodyTorque instead", replaceWith = @ReplaceWith(expression = "applyBodyTorque(torque)", imports = []))
   public override fun applyRotDependentTorque(torque: Vector3dc) {
      this.applyBodyTorque(torque)
   }

   @Deprecated(message = "Use applyWorldForce instead", replaceWith = @ReplaceWith(expression = "applyWorldForce(force, pos)", imports = []))
   public override fun applyInvariantForceToPos(force: Vector3dc, pos: Vector3dc) {
      this.applyWorldForceToBodyPos(force, pos)
   }

   @Deprecated(message = "Use applyModelForce or applyBodyForce instead", replaceWith = @ReplaceWith(expression = "applyBodyForce(force, pos)", imports = []))
   public override fun applyRotDependentForceToPos(force: Vector3dc, pos: Vector3dc) {
      this.applyBodyForce(force, pos)
   }

   private fun updatePhysTransform() {
      val var10001: BodyKinematicsFactory = BodyKinematicsFactory.INSTANCE
      val var10002: Vector3dc = this.poseVel.d
      val var10003: Vector3dc = this.poseVel.e
      val var10004: Vector3dc = this.poseVel.b
      val var10005: Quaterniondc = this.poseVel.c
      val var10006: Vector3dc = Vector3d(this.rigidBodyReference.j())
      val var10007: Vector3d = Vector3d(this.rigidBodyReference.k()).mul(-1.0)
      this.kinematics = var10001.create(var10002, var10003, var10004, var10005, var10006, var10007)
   }

   public open val prevTickTransform: ShipTransform
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }


   public open val chunkClaim: ChunkClaim
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }


   public open var chunkClaimDimension: String
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }

      public open set(value) {
      }


   public open val worldAABB: AABBdc
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }


   public open val shipAABB: AABBic?
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }


   public open val slug: String?
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }


   public open val activeChunksSet: IShipActiveChunksSet
      public open get() {
         throw NotImplementedError("An operation is not implemented: Not yet implemented")
      }


   public override fun setFromTransform(transform: BodyTransform) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public override fun unsafeSetKinematics(kinematics: BodyKinematics) {
      throw NotImplementedError("An operation is not implemented: Not yet implemented")
   }

   public operator fun component1(): Long {
      return this.id
   }

   internal operator fun `component2$impl`(): Hc<*> {
      return this.rigidBodyReference
   }

   internal operator fun `component3$impl`(): List<ShipPhysicsListener> {
      return this.physicsListeners
   }

   internal operator fun `component4$impl`(): Em {
      return this.inertia
   }

   private operator fun component5(): Hg {
      return this._poseVel
   }

   internal operator fun `component6$impl`(): Int {
      return this.lastShipTeleportId
   }

   private operator fun component7(): FV {
      return this.physicsThreadChecker
   }

   internal operator fun `component8$impl`(): Ey {
      return this.wingManager
   }

   public fun copy(
      id: Long = this.id,
      rigidBodyReference: Hc<*> = this.rigidBodyReference,
      physicsListeners: List<ShipPhysicsListener> = this.physicsListeners,
      inertia: Em = this.inertia,
      _poseVel: Hg = this._poseVel,
      lastShipTeleportId: Int = this.lastShipTeleportId,
      physicsThreadChecker: FV = this.physicsThreadChecker,
      wingManager: Ey = this.wingManager
   ): PhysShipImpl {
      return PhysShipImpl(id, rigidBodyReference, physicsListeners, inertia, _poseVel, lastShipTeleportId, physicsThreadChecker, wingManager)
   }

   public override fun toString(): String {
      return "PhysShipImpl(id=${this.id}, rigidBodyReference=${this.rigidBodyReference}, physicsListeners=${this.physicsListeners}, inertia=${this.inertia}, _poseVel=${this._poseVel}, lastShipTeleportId=${this.lastShipTeleportId}, physicsThreadChecker=${this.physicsThreadChecker}, wingManager=${this.wingManager})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (
                                          ((java.lang.Long.hashCode(this.id) * 31 + this.rigidBodyReference.hashCode()) * 31 + this.physicsListeners.hashCode())
                                                * 31
                                             + this.inertia.hashCode()
                                       )
                                       * 31
                                    + this._poseVel.hashCode()
                              )
                              * 31
                           + Integer.hashCode(this.lastShipTeleportId)
                     )
                     * 31
                  + this.physicsThreadChecker.hashCode()
            )
            * 31
         + this.wingManager.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      label64@
      if (this === other) {
         return true
      } else {
         return other is PhysShipImpl
            && this.id == (other as PhysShipImpl).id
            && this.rigidBodyReference == (other as PhysShipImpl).rigidBodyReference
            && this.physicsListeners == (other as PhysShipImpl).physicsListeners
            && this.inertia == (other as PhysShipImpl).inertia
            && this._poseVel == (other as PhysShipImpl)._poseVel
            && this.lastShipTeleportId == (other as PhysShipImpl).lastShipTeleportId
            && this.physicsThreadChecker == (other as PhysShipImpl).physicsThreadChecker
            && this.wingManager == (other as PhysShipImpl).wingManager
         }
   }
}
