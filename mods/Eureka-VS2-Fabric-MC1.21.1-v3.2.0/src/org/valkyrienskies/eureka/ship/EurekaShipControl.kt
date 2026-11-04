package org.valkyrienskies.eureka.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com/fasterxml/jackson/annotation/JsonAutoDetect.Visibility
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.math.MathKt
import net.minecraft.class_1657
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_2561
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.joml.AxisAngle4d
import org.joml.Matrix3dc
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.attachment.AttachmentHolder
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerTickListener
import org.valkyrienskies.core.api.ships.ShipPhysicsListener
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaConfig.Server
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt

@JsonAutoDetect(getterVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE, isGetterVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE, setterVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE, fieldVisibility = com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
@SourceDebugExtension(["SMAP\nEurekaShipControl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EurekaShipControl.kt\norg/valkyrienskies/eureka/ship/EurekaShipControl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1175:1\n1#2:1176\n*E\n"])
public class EurekaShipControl : ShipPhysicsListener, ServerTickListener {
   @JsonIgnore
   @Nullable
   internal final var ship: LoadedServerShip?

   private final var extraForceLinear: Double
   private final var extraForceAngular: Double
   public final var aligning: Boolean
   public final var disassembling: Boolean
   private final var physConsumption: Float
   private final var angleUntilAligned: Double
   private final var positionUntilAligned: Vector3d = Vector3d()

   public final var consumed: Float
      private set

   @JsonIgnore
   private final var wasCruisePressed: Boolean

   @JsonProperty("cruise")
   public final var isCruising: Boolean

   @JsonProperty("vanillaControls")
   public final var vanillaControls: Boolean

   @JsonIgnore
   @Nullable
   private final var controlData: org.valkyrienskies.eureka.ship.EurekaShipControl.ControlData?

   @JsonProperty("cruiseSeatDir")
   private final var cruiseSeatDir: Int = -1

   @JsonProperty("cruiseFwd")
   private final var cruiseFwd: Float

   @JsonProperty("cruiseLeft")
   private final var cruiseLeft: Float

   @JsonProperty("cruiseUp")
   private final var cruiseUp: Float

   @JsonProperty("cruiseSprint")
   private final var cruiseSprint: Boolean

   @JsonIgnore
   @Nullable
   public final var seatedPlayer: class_1657?

   @JsonProperty("cruiseSpeed")
   public final var oldSpeed: Double

   @JsonProperty("cruiseTargetSpeed")
   @Nullable
   private final var cruiseTargetSpeedMps: Double?

   @JsonIgnore
   @Nullable
   private final var cruiseTurnOmega: Double?

   @JsonProperty("cruiseTurning")
   private final var cruiseTurning: Boolean

   @JsonProperty("cruiseTurnRate")
   private final var cruiseTurnOmegaSaved: Double

   @JsonProperty("cruiseHorizontal")
   private final var cruiseHorizontalActive: Boolean

   @JsonProperty("cruiseVertical")
   private final var cruiseVerticalActive: Boolean

   @JsonProperty("cruiseVerticalRate")
   private final var cruiseVerticalRate: Float

   @JsonIgnore
   @NotNull
   private final val fwdCancel: org.valkyrienskies.eureka.ship.EurekaShipControl.CancelState = EurekaShipControl.CancelState()

   @JsonIgnore
   @NotNull
   private final val vertCancel: org.valkyrienskies.eureka.ship.EurekaShipControl.CancelState = EurekaShipControl.CancelState()

   @JsonIgnore
   @NotNull
   private final val turnCancel: org.valkyrienskies.eureka.ship.EurekaShipControl.CancelState = EurekaShipControl.CancelState()

   @JsonIgnore
   @NotNull
   private final val scratchInvRotation: Quaterniond = Quaterniond()

   @JsonIgnore
   @NotNull
   private final val scratchAxisAngle: AxisAngle4d = AxisAngle4d()

   @JsonIgnore
   @Nullable
   private final var holdTargetY: Double?

   @JsonIgnore
   private final var holdEngaged: Boolean

   @JsonIgnore
   public final var keelInWater: Boolean

   @JsonIgnore
   public final var turnHold: Double

   @JsonIgnore
   public final var fwdHold: Double

   @JsonIgnore
   public final var vertHold: Double

   @JsonIgnore
   private final var turnHoldLastGameTick: Long = -1L

   @JsonIgnore
   private final var fwdIntentSign: Int

   @JsonIgnore
   private final var vertIntentSign: Int

   @JsonIgnore
   private final var turnIntentSign: Int

   public final var powerLinear: Double
   public final var powerAngular: Double

   public final var anchors: Int
      public final set(v) {
         this.anchors = v
         this.deleteIfEmpty()
      }


   public final var anchorsActive: Int

   public final var balloons: Int
      public final set(v) {
         this.balloons = v
         this.deleteIfEmpty()
      }


   public final var helms: Int
      public final set(v) {
         this.helms = v
         this.deleteIfEmpty()
      }


   public final var floaters: Int
      public final set(v) {
         this.floaters = v
         this.deleteIfEmpty()
      }


   public final var engines: Int
   public final var assembledBlocks: Int

   @JsonIgnore
   private final var engineFuelAccum: Double

   @JsonIgnore
   private final var engineFuelCount: Int

   @JsonIgnore
   private final var engineFuelAccumNext: Double

   @JsonIgnore
   private final var engineFuelCountNext: Int

   @JsonIgnore
   public final var engineFuelPercent: Int
      private set

   private final val anchored: Boolean
      private final get() {
         return this.anchorsActive > 0
      }


   public final val canDisassemble: Boolean
      public final get() {
         if (this.ship != null && this.disassembling && Math.abs(this.angleUntilAligned) < 0.02) {
            val var10000: Vector3d = this.positionUntilAligned
            val var10001: LoadedServerShip = this.ship
            if (var10000.distanceSquared(var10001.getTransform().getPositionInWorld()) < 4.0) {
               return true
            }
         }

         return false
      }


   private final val cfg: Server
      private final get() {
         return if (this.vanillaControls) EurekaConfig.VANILLA else EurekaConfig.ADVANCED
      }


   public final val engineCfg: Server
      public final get() {
         return this.cfg
      }


   public open fun physTick(physShip: PhysShip, physLevel: PhysLevel) {
      if (this.helms < 1) {
         physShip.setDoFluidDrag(true)
      } else {
         physShip.setDoFluidDrag(EurekaConfig.SERVER.doFluidDrag)
         if (this.ship != null) {
            val ship: LoadedServerShip = this.ship
            val mass: Double = physShip.getMass()
            val moiTensor: Matrix3dc = physShip.getMomentOfInertia()
            val omega: Vector3dc = physShip.getOmega()
            val vel: Vector3dc = physShip.getVelocity()
            var balloonForceProvided: Double = this.balloons * Companion.forcePerBalloon
            if (EurekaConfig.SERVER.maxBalloonsPerEngine > 0) {
               balloonForceProvided *= Math.min(
                  1.0, this.extraForceLinear * (double)EurekaConfig.SERVER.maxBalloonsPerEngine / (double)(this.cfg.enginePowerLinear * (float)this.balloons)
               )
            }

            val inWater: Boolean = this.keelInWater || physShip.getLiquidOverlap() > EurekaConfig.SERVER.waterAltitudeHoldMinOverlap
            val stillInWater: Boolean = this.keelInWater || physShip.getLiquidOverlap() > 0.0
            this.holdEngaged = EurekaConfig.SERVER.enableWaterAltitudeHold
               && !this.disassembling
               && !this.anchored
               && this.floaters > 0
               && this.balloons > 0
               && (inWater || this.holdEngaged && stillInWater)
               if (!this.holdEngaged) {
               this.holdTargetY = null
            }

            physShip.setBuoyantFactor(
               if (this.holdEngaged)
                  1.0
                  else
                  1.0
                     + (double)this.floaters
                        * Math.min(EurekaConfig.SERVER.floaterBuoyantFactorPerKg / 15.0 / mass, EurekaConfig.SERVER.maxFloaterBuoyantFactor)
            )
            val invRotationAxisAngle: AxisAngle4d = this.scratchAxisAngle
               .set(physShip.getTransform().getShipToWorldRotation().invert(this.scratchInvRotation) as Quaterniondc)
               this.angleUntilAligned = (int)Math.floor(invRotationAxisAngle.angle / (Math.PI / 2) + 4.5) % 4 * (Math.PI / 2) - invRotationAxisAngle.angle
            if (this.disassembling) {
               val controllingPlayer: Vector3dc = ship.getTransform().getPositionInWorld()
               this.positionUntilAligned = controllingPlayer.floor(Vector3d())
               val validPlayer: Vector3d = controllingPlayer.sub(this.positionUntilAligned as Vector3dc, Vector3d())
               physShip.applyInvariantForce(validPlayer as Vector3dc)
            }

            if (this.aligning && Math.abs(this.angleUntilAligned) > 0.01) {
               if (this.angleUntilAligned < 0.3 && this.angleUntilAligned > 0.0) {
                  this.angleUntilAligned = 0.3
               }

               if (this.angleUntilAligned > -0.3 && this.angleUntilAligned < 0.0) {
                  this.angleUntilAligned = -0.3
               }

               val var32: Vector3d = moiTensor.transform(
                  Vector3d(invRotationAxisAngle.x, invRotationAxisAngle.y, invRotationAxisAngle.z)
                     .mul(-this.angleUntilAligned)
                     .mul(EurekaConfig.SERVER.stabilizationSpeed)
               )
               physShip.applyInvariantTorque(var32 as Vector3dc)
            }

            val var31: SeatedControllingPlayer = ship.getAttachment(SeatedControllingPlayer.class) as SeatedControllingPlayer
            val var33: Boolean = var31 != null && !this.anchored
            if (this.anchored) {
               if (this.isCruising) {
                  this.isCruising = false
                  this.showCruiseStatus()
               }

               this.clearCruiseLatches()
               physShip.setStatic(true)
            } else {
               StabilizeKt.stabilize(
                  physShip,
                  omega,
                  vel,
                  physShip,
                  !var33 && !this.aligning,
                  !var33 && (!this.isCruising || !this.cruiseTurning || !EurekaConfig.SERVER.enableTurnCruise)
               )
               var var34: Vector3d = Vector3d(0.0, 0.0, 0.0)
               if (this.isCruising && this.controlData == null) {
                  if (0 <= this.cruiseSeatDir && this.cruiseSeatDir < 6) {
                     this.controlData = EurekaShipControl.ControlData(
                        class_2350.values()[this.cruiseSeatDir], this.cruiseFwd, this.cruiseLeft, this.cruiseUp, this.cruiseSprint
                     )
                     this.cruiseTurnOmega = if (!this.vanillaControls && this.cruiseTurning && EurekaConfig.SERVER.enableTurnCruise)
                        this.cruiseTurnOmegaSaved
                        else
                        null
                     } else {
                     this.cruiseTurnOmega = if (!this.vanillaControls && this.cruiseTurning && EurekaConfig.SERVER.enableTurnCruise)
                        this.cruiseTurnOmegaSaved
                        else
                        null
                     }
               }

               var var35: Any = null
               if (var33) {
                  val live: EurekaShipControl.ControlData = this.getControlData(var31)
                  var35 = live
                  if (!this.isCruising) {
                     this.controlData = live
                  }

                  this.wasCruisePressed = var31.getCruise()
               } else if (!this.isCruising) {
                  this.controlData = null
                  this.oldSpeed = 0.0
                  this.clearCruiseLatches()
               }

               if (this.isCruising) {
                  if (this.controlData != null) {
                     val effective: EurekaShipControl.ControlData = this.controlData
                     this.cruiseSeatDir = this.controlData.seatInDirection.ordinal()
                     this.cruiseFwd = effective.forwardImpulse
                     this.cruiseLeft = effective.leftImpulse
                     this.cruiseUp = effective.upImpulse
                     this.cruiseSprint = effective.sprintOn
                  }

                  if (!this.vanillaControls) {
                     this.cruiseTurnOmegaSaved = if (this.cruiseTurnOmega != null) this.cruiseTurnOmega else 0.0
                  }
               }

               val var36: Double = if (balloonForceProvided >= mass * 10.0)
                  1.0
                  else
                  (if (physShip.getLiquidOverlap() > 0.0) EurekaConfig.SERVER.waterThrustAssist else EurekaConfig.SERVER.landThrustAssist)
                  if (!this.vanillaControls && this.cruiseVerticalActive) {
                  val var37: Float = if (var35 != null) var35.upImpulse else 0.0F
                  if (var37 != 0.0F) {
                     this.cruiseVerticalRate = RangesKt.coerceIn(this.cruiseVerticalRate * 0.94F + var37 * 0.06F, -1.0F, 1.0F)
                  }
               }

               val var10000: EurekaShipControl.ControlData
               if (this.vanillaControls || !this.isCruising) {
                  var10000 = this.controlData
               } else if (this.controlData != null) {
                  val control: EurekaShipControl.ControlData = this.controlData
                  var10000 = EurekaShipControl.ControlData(
                     this.controlData.seatInDirection,
                     if (var35 != null) var35.forwardImpulse else 0.0F,
                     if (var35 != null) var35.leftImpulse else 0.0F,
                     if (this.cruiseVerticalActive) this.cruiseVerticalRate else (if (var35 != null) var35.upImpulse else 0.0F),
                     control.sprintOn
                  )
               } else {
                  var10000 = null
               }

               val var39: Boolean = (if (var10000 != null) var10000.upImpulse else 0.0F) != 0.0F && (!this.vanillaControls || !this.isCruising)
               if (var10000 != null) {
                  this.applyPlayerControl(var10000, physShip, var36)
                  var34 = this.getPlayerUpwardVel(var10000, mass)
               }

               if (this.holdEngaged) {
                  this.applyWaterAltitudeHold(physShip, var34 as Vector3dc, vel, mass, var39)
               } else {
                  physShip.applyInvariantForce(
                     Vector3d(
                        0.0,
                        Math.min(
                              balloonForceProvided,
                              Math.max(
                                 (var34.y() - vel.y() - -10.0 / EurekaConfig.SERVER.elevationSnappiness) * mass * EurekaConfig.SERVER.elevationSnappiness, 0.0
                              )
                           )
                           + vel.y() * -mass,
                        0.0
                     ) as Vector3dc
                  )
               }

               if (!this.vanillaControls && this.isCruising) {
                  val var40: ArrayList = ArrayList(3)
                  if (this.cruiseHorizontalActive) {
                     val var43: Int = this.signOfD(this.oldSpeed, 0.05)
                     if (var43 != 0) {
                        this.fwdIntentSign = var43
                     }

                     if (this.fwdCancel
                        .update(Math.abs(this.fwdHold), this.signOfD(this.fwdHold, 0.0), this.fwdIntentSign, EurekaConfig.SERVER.horizontalCancelHold)) {
                        this.cruiseHorizontalActive = false
                        var40.add("Horizontal Disabled")
                     }
                  }

                  if (this.cruiseVerticalActive) {
                     val var44: Int = this.signOfD((double)this.cruiseVerticalRate, 0.05)
                     if (var44 != 0) {
                        this.vertIntentSign = var44
                     }

                     if (this.vertCancel
                        .update(Math.abs(this.vertHold), this.signOfD(this.vertHold, 0.0), this.vertIntentSign, EurekaConfig.SERVER.verticalCancelHold)) {
                        this.cruiseVerticalActive = false
                        var40.add("Vertical Disabled")
                     }
                  }

                  if (this.cruiseTurning) {
                     val var45: Int = this.signOfD(if (this.cruiseTurnOmega != null) this.cruiseTurnOmega else 0.0, 0.02)
                     if (var45 != 0) {
                        this.turnIntentSign = var45
                     }

                     if (this.turnCancel
                        .update(Math.abs(this.turnHold), this.signOfD(this.turnHold, 0.0), this.turnIntentSign, EurekaConfig.SERVER.turnCancelHold)) {
                        this.clearCruiseTurn()
                        var40.add("Turn Disabled")
                     }
                  }

                  if (EurekaConfig.SERVER.debugCruiseCancel && !var40.isEmpty()) {
                     if (this.seatedPlayer != null) {
                        this.seatedPlayer
                           .method_7353(
                              class_2561.method_43470(
                                 "Cruise Control: ${CollectionsKt.joinToString$default(var40, " | ", null, null, 0, null, null, 62, null)}"
                              ) as class_2561,
                              true
                           )
                        }
                  }
               }

               physShip.setStatic(this.anchored)
            }
         }
      }
   }

   private fun applyWaterAltitudeHold(physShip: PhysShip, idealUpwardVel: Vector3dc, vel: Vector3dc, mass: Double, verticalInputActive: Boolean) {
      val currentY: Double = physShip.getTransform().getPositionInWorld().y()
      var var10000: Double
      if (verticalInputActive) {
         this.holdTargetY = currentY
         var10000 = ((idealUpwardVel.y() - vel.y()) * EurekaConfig.SERVER.elevationSnappiness - -10.0) * mass
      } else {
         if (this.holdTargetY != null) {
            var10000 = this.holdTargetY
         } else {
            this.holdTargetY = currentY
            var10000 = currentY
         }

         val k: Double = EurekaConfig.SERVER.waterAltitudeHoldStiffness
         var10000 = ((var10000 - currentY) * k - vel.y() * (2.0 * Math.sqrt(k)) - -10.0) * mass
      }

      physShip.applyInvariantForce(Vector3d(0.0, var10000, 0.0) as Vector3dc)
   }

   private fun getControlData(player: SeatedControllingPlayer): org.valkyrienskies.eureka.ship.EurekaShipControl.ControlData {
      val currentControlData: EurekaShipControl.ControlData = EurekaShipControl.ControlData.Companion.create(player)
      if (this.vanillaControls) {
         if (!this.wasCruisePressed && player.getCruise()) {
            this.isCruising = !this.isCruising
            this.showCruiseStatus()
         } else if (!player.getCruise()
            && this.isCruising
            && (player.getLeftImpulse() != 0.0F || player.getSprintOn() || player.getUpImpulse() != 0.0F || player.getForwardImpulse() != 0.0F)
            && !(currentControlData == this.controlData)) {
            this.isCruising = false
            this.showCruiseStatus()
         }

         return currentControlData
      } else {
         if (!this.wasCruisePressed && player.getCruise()) {
            if (!this.isCruising) {
               val latchH: Boolean = currentControlData.forwardImpulse != 0.0F
               val latchV: Boolean = currentControlData.upImpulse != 0.0F
               val latchT: Boolean = currentControlData.leftImpulse != 0.0F && EurekaConfig.SERVER.enableTurnCruise
               this.isCruising = true
               this.controlData = currentControlData
               this.cruiseHorizontalActive = latchH
               this.cruiseVerticalActive = latchV
               this.cruiseVerticalRate = if (latchV) currentControlData.upImpulse else 0.0F
               this.cruiseTurning = latchT
               if (!latchT) {
                  this.cruiseTurnOmega = null
               }

               this.fwdIntentSign = if (latchH) this.signOf(currentControlData.forwardImpulse) else 0
               this.vertIntentSign = if (latchV) this.signOf(currentControlData.upImpulse) else 0
               this.turnIntentSign = if (latchT) this.signOf(currentControlData.leftImpulse) else 0
               this.showCruiseStatus()
            } else {
               this.isCruising = false
               this.clearCruiseLatches()
               this.showCruiseStatus()
            }
         }

         return currentControlData
      }
   }

   private fun applyPlayerControl(control: org.valkyrienskies.eureka.ship.EurekaShipControl.ControlData, physShip: PhysShip, thrustMultiplier: Double) {
      if (this.ship != null) {
         val ship: LoadedServerShip = this.ship
         val transform: ShipTransform = physShip.getTransform()
         val aabb: AABBdc = ship.getWorldAABB()
         val center: Vector3dc = transform.getPositionInWorld()
         val moiTensor: Matrix3dc = physShip.getMomentOfInertia()
         val omega: Vector3dc = physShip.getOmega()
         val `$this$applyPlayerControl_u24lambda_u247`: EurekaShipControl = this
         val largestDistance: Double = RangesKt.coerceIn(
            Math.max(
               Math.max(
                  Math.max(center.distance(aabb.minX(), center.y(), aabb.minZ()), center.distance(aabb.minX(), center.y(), aabb.maxZ())),
                  center.distance(aabb.maxX(), center.y(), aabb.minZ())
               ),
               center.distance(aabb.maxX(), center.y(), aabb.maxZ())
            ),
            0.5,
            EurekaConfig.SERVER.maxSizeForTurnSpeedPenalty
         )
         var idealAlphaY: Double = 0.0
         if (this.vanillaControls) {
            val delay: Double = (this.cfg.turnSpeed + this.extraForceAngular) / largestDistance
            idealAlphaY = (if (Math.abs(omega.y()) < delay && control.leftImpulse != 0.0F) control.leftImpulse else -RangesKt.coerceIn(omega.y(), -1.0, 1.0))
               * (this.cfg.turnAcceleration / largestDistance)
            } else {
            val var41: Double = this.cfg.turnSpeed / largestDistance
            val var42: Double = RangesKt.coerceAtLeast(EurekaConfig.SERVER.turnAccelDelay, 0.05)
            val var43: Double = Math.abs(this.turnHold)
            val var44: Boolean = control.leftImpulse != 0.0F
            val var45: Double = if (control.leftImpulse > 0.0F) 1.0 else (if (control.leftImpulse < 0.0F) -1.0 else 0.0)
            val turnLatched: Boolean = this.isCruising && !this.disassembling && !this.aligning && EurekaConfig.SERVER.enableTurnCruise && this.cruiseTurning
            val effCap: Double = RangesKt.coerceAtMost(
               if (var44 && var43 > var42) var41 + this.cfg.turnAcceleration / largestDistance * (var43 - var42) else var41, 24.0 / largestDistance
            )
            val chase: Double = if (var44 && var43 > var42) Math.max(var41 / var42, this.cfg.turnAcceleration / largestDistance) else var41 / var42
            val maintainChase: Double = Math.max(chase, this.cfg.turnAcceleration / largestDistance)
            idealAlphaY = if (var44)
               RangesKt.coerceIn(effCap * var45 - omega.y(), -chase, chase)
               else
               (
                  if (turnLatched)
                     RangesKt.coerceIn((if (this.cruiseTurnOmega != null) this.cruiseTurnOmega else 0.0) - omega.y(), -maintainChase, maintainChase)
                     else
                     RangesKt.coerceIn(0.0 - omega.y(), -chase, chase)
               )
               if (turnLatched && var44) {
               this.cruiseTurnOmega = omega.y()
            }
         }

         var var10001: Vector3d = moiTensor.transform(Vector3d(0.0, idealAlphaY, 0.0))
         physShip.applyInvariantTorque(var10001 as Vector3dc)
         physShip.applyInvariantTorque(this.getPlayerControlledBanking(control, physShip, moiTensor, -idealAlphaY) as Vector3dc)
         var10001 = this.getPlayerForwardVel(control, physShip).mul(thrustMultiplier)
         physShip.applyInvariantForce(var10001 as Vector3dc)
      }
   }

   private fun getPlayerControlledBanking(
      control: org.valkyrienskies.eureka.ship.EurekaShipControl.ControlData,
      physShip: PhysShip,
      moiTensor: Matrix3dc,
      strength: Double
   ): Vector3d {
      val var10000: class_2382 = control.seatInDirection.method_10163()
      val rotationVector: Vector3d = VectorConversionsMCKt.toJOMLD(var10000)
      physShip.getTransform().getShipToWorldRotation().transform(rotationVector)
      rotationVector.y = 0.0
      rotationVector.mul(strength * 1.5)
      physShip.getTransform()
         .getShipToWorldRotation()
         .transform(moiTensor.transform(physShip.getTransform().getShipToWorldRotation().transformInverse(rotationVector)))
         return rotationVector
   }

   private fun getPlayerForwardVel(control: org.valkyrienskies.eureka.ship.EurekaShipControl.ControlData, physShip: PhysShip): Vector3d {
      val scaledMass: Double = physShip.getMass() * EurekaConfig.SERVER.speedMassScale
      val vel: Vector3dc = physShip.getVelocity()
      val var10000: class_2382 = control.seatInDirection.method_10163()
      val forwardVector: Vector3d = VectorConversionsMCKt.toJOMLD(var10000)
      physShip.getTransform().getShipToWorldRotation().transform(forwardVector)
      forwardVector.normalize()
      val s: Double = 1
         / this.smoothingATanMax(
            EurekaConfig.SERVER.linearMaxMass, physShip.getMass() * EurekaConfig.SERVER.linearMassScaling + EurekaConfig.SERVER.linearBaseMass
         )
         if (!this.cruiseHorizontalActive || control.forwardImpulse != 0.0F) {
         this.oldSpeed = this.oldSpeed * (1 - s) + control.forwardImpulse * s
         if (control.forwardImpulse != 0.0F) {
            this.cruiseTargetSpeedMps = null
         }
      } else if (this.cruiseTargetSpeedMps != null) {
         this.oldSpeed = RangesKt.coerceIn(
            this.oldSpeed + (this.cruiseTargetSpeedMps.doubleValue() - vel.dot(forwardVector as Vector3dc)) / Math.max(this.estimateTopSpeed(), 1.0) * 0.15,
            -1.0,
            1.0
         )
      }

      var speed: Double = this.oldSpeed * EurekaConfig.SERVER.linearCasualSpeed / 3
      if (this.extraForceLinear != 0.0) {
         val var18: Double = Math.max((this.extraForceLinear - (double)this.cfg.enginePowerLinear * this.cfg.engineBoostOffset) * this.cfg.engineBoost, 0.0)
         this.extraForceLinear = this.extraForceLinear + (var18 + var18 * var18 * EurekaConfig.SERVER.engineBoostExponentialPower)
         this.extraForceLinear /= scaledMass
         speed += if (speed < 0.0)
            this.smoothingATanMax(this.cfg.maxReverseSpeedFromEngines, this.extraForceLinear * this.oldSpeed)
            else
            this.smoothingATanMax(EurekaConfig.SERVER.maxSpeedFromEngines, this.extraForceLinear * this.oldSpeed)
            this.physConsumption = this.physConsumption + (if (control.sprintOn) 1.0F else (float)Math.min(Math.abs(this.oldSpeed), 1.0))
      }

      forwardVector.mul(speed)
      val var19: Vector3d = physShip.getTransform().getShipToWorldRotation().transform(Vector3d(0.0, 1.0, 0.0))
      val var21: Vector3d = forwardVector.mul(EurekaConfig.SERVER.baseSpeed)
         .sub(vel.sub(var19.mul(var19.dot(vel)) as Vector3dc, Vector3d()) as Vector3dc)
         .mul(scaledMass)
         return var21
   }

   private fun getPlayerUpwardVel(control: org.valkyrienskies.eureka.ship.EurekaShipControl.ControlData, mass: Double): Vector3d {
      if (control.upImpulse != 0.0F) {
         val var10000: Vector3d = Vector3d(0.0, 1.0, 0.0)
            .mul((double)control.upImpulse)
            .mul(
               if (control.upImpulse < 0.0F)
                  this.cfg.baseImpulseDescendRate
                  else
                  this.cfg.baseImpulseElevationRate
                     + this.smoothing(2.0, EurekaConfig.SERVER.balloonElevationMaxSpeed, (double)this.balloons * Companion.forcePerBalloon / mass)
            )
            return var10000
      } else {
         return Vector3d(0.0, 0.0, 0.0)
      }
   }

   private fun showCruiseStatus() {
      val cruiseKey: java.lang.String = if (this.isCruising) "hud.vs_eureka.start_cruising" else "hud.vs_eureka.stop_cruising"
      if (this.seatedPlayer != null) {
         this.seatedPlayer.method_7353(class_2561.method_43471(cruiseKey) as class_2561, true)
      }
   }

   private fun clearCruiseTurn() {
      this.cruiseTurnOmega = null
      this.cruiseTurning = false
      this.turnIntentSign = 0
      this.turnCancel.reset()
   }

   public fun cancelCruiseForModeSwitch() {
      if (this.isCruising) {
         this.isCruising = false
         this.showCruiseStatus()
      }

      this.clearCruiseLatches()
   }

   private fun clearCruiseLatches() {
      this.cruiseHorizontalActive = false
      this.cruiseVerticalActive = false
      this.cruiseVerticalRate = 0.0F
      this.cruiseTargetSpeedMps = null
      this.fwdIntentSign = 0
      this.vertIntentSign = 0
      this.fwdCancel.reset()
      this.vertCancel.reset()
      this.clearCruiseTurn()
   }

   private fun signOf(v: Float): Int {
      return if (v > 0.0F) 1 else (if (v < 0.0F) -1 else 0)
   }

   private fun signOfD(v: Double, eps: Double): Int {
      return if (v > eps) 1 else (if (v < -eps) -1 else 0)
   }

   private fun accumSigned(impulse: Float, prev: Double): Double {
      val sign: Int = this.signOf(impulse)
      if (sign == 0) {
         return 0.0
      } else {
         return if (sign == this.signOfD(prev, 0.0)) prev + sign * 0.05 else sign * 0.05
      }
   }

   public fun updateInputHolds(gameTime: Long, forwardImpulse: Float, leftImpulse: Float, upImpulse: Float) {
      if (gameTime != this.turnHoldLastGameTick) {
         this.turnHoldLastGameTick = gameTime
         this.turnHold = this.accumSigned(leftImpulse, this.turnHold)
         this.fwdHold = this.accumSigned(forwardImpulse, this.fwdHold)
         this.vertHold = this.accumSigned(upImpulse, this.vertHold)
      }
   }

   public fun reportEngineFuel(fraction: Double) {
      this.engineFuelAccumNext = this.engineFuelAccumNext + RangesKt.coerceIn(fraction, 0.0, 1.0)
      val var3: Int = this.engineFuelCountNext++
   }

   private fun deleteIfEmpty() {
      if (this.helms <= 0 && this.floaters <= 0 && this.anchors <= 0 && this.balloons <= 0) {
         if (this.ship != null) {
            val var10000: EurekaShipControl = this.ship.removeAttachment(EurekaShipControl.class) as EurekaShipControl
         }
      }
   }

   private fun smoothing(smoothing: Double, max: Double, x: Double): Double {
      return max - smoothing / (x + smoothing / max)
   }

   private fun smoothingATan(smoothing: Double, x: Double): Double {
      return Math.atan(x * smoothing) / smoothing
   }

   private fun smoothingATanMax(max: Double, x: Double): Double {
      return this.smoothingATan((double)1 / (max * 0.638), x)
   }

   public final val cruiseHorizontalArmed: Boolean
      public final get() {
         return this.cruiseHorizontalActive
      }


   public final val cruiseVerticalArmed: Boolean
      public final get() {
         return this.cruiseVerticalActive
      }


   public final val cruiseTurnArmed: Boolean
      public final get() {
         return this.cruiseTurning
      }


   public fun cruiseSpeedMps(): Double {
      var var3: Double
      run label23@{
         if (this.ship != null) {
            val var10000: Vector3dc = this.ship.getVelocity()
            if (var10000 != null) {
               var3 = var10000.length()
               return@label23
            }
         }

         var3 = 0.0
      }

      return if (this.oldSpeed < 0.0) -var3 else var3
   }

   public fun cruiseVerticalMps(): Double {
      return if (this.cruiseVerticalRate >= 0.0)
         this.cruiseVerticalRate * this.cfg.baseImpulseElevationRate
         else
         this.cruiseVerticalRate * this.cfg.baseImpulseDescendRate
      }

   public fun cruiseTurnDegPerSec(): Double {
      return (if (this.cruiseTurnOmega != null) this.cruiseTurnOmega else 0.0) * 180.0 / Math.PI
   }

   private fun ensureMenuCourse(seatDir: class_2350) {
      if (this.controlData == null) {
         this.controlData = EurekaShipControl.ControlData(seatDir, 0.0F, 0.0F, 0.0F, false, 30, null)
         this.cruiseSeatDir = seatDir.ordinal()
      }
   }

   public fun setCruiseFromMenu(enable: Boolean, seatDir: class_2350) {
      if (enable != this.isCruising) {
         this.isCruising = enable
         if (enable) {
            this.ensureMenuCourse(seatDir)
         } else {
            this.clearCruiseLatches()
         }

         this.showCruiseStatus()
      }
   }

   public fun setCruiseAxisArmed(axis: Int, armed: Boolean, seatDir: class_2350) {
      when (axis) {
         0 -> {
            this.cruiseHorizontalActive = armed
            if (!armed) {
               this.fwdIntentSign = 0
               this.fwdCancel.reset()
               this.cruiseTargetSpeedMps = null
            }
         }
         1 -> {
            if (armed) {
               this.cruiseTurning = true
            } else {
               this.clearCruiseTurn()
            }
         }
         2 -> {
            this.cruiseVerticalActive = armed
            if (!armed) {
               this.cruiseVerticalRate = 0.0F
               this.vertIntentSign = 0
               this.vertCancel.reset()
            }
         }
         else -> {}
      }

      if (armed) {
         this.isCruising = true
         this.ensureMenuCourse(seatDir)
      }
   }

   public fun setCruiseValueMenu(axis: Int, value: Double, seatDir: class_2350) {
      this.isCruising = true
this.ensureMenuCourse(seatDir)
      when (axis) {
         0 -> {
            val var12: Double = this.estimateTopSpeed()
            val var14: Double = this.cfg.maxReverseSpeedFromEngines
            val var16: Double = RangesKt.coerceIn(value, -var14, var12)
            this.cruiseTargetSpeedMps = var16
            this.oldSpeed = RangesKt.coerceIn(
               if (var16 >= 0.0) (if (var12 > 0.0) var16 / var12 else 0.0) else (if (var14 > 0.0) var16 / var14 else 0.0), -1.0, 1.0
            )
            this.cruiseHorizontalActive = true
            this.fwdIntentSign = this.signOfD(this.oldSpeed, 0.05)
         }
         1 -> {
            val var13: Double = Math.min(24.0 / this.turnRadiusEstimate(), 0.2792526803190927)
            val var15: Double = RangesKt.coerceIn(value * Math.PI / 180.0, -var13, var13)
            this.cruiseTurnOmega = var15
            this.cruiseTurnOmegaSaved = var15
            this.cruiseTurning = true
            this.turnIntentSign = this.signOfD(var15, 0.02)
         }
         2 -> {
            val upMax: Double = this.cfg.baseImpulseElevationRate
            val downMax: Double = this.cfg.baseImpulseDescendRate
            val clamped: Double = RangesKt.coerceIn(value, -downMax, upMax)
            this.cruiseVerticalRate = (float)RangesKt.coerceIn(
               if (clamped >= 0.0) (if (upMax > 0.0) clamped / upMax else 0.0) else (if (downMax > 0.0) clamped / downMax else 0.0), -1.0, 1.0
            )
            this.cruiseVerticalActive = true
            this.vertIntentSign = this.signOfD((double)this.cruiseVerticalRate, 0.05)
         }
         else -> {}
      }
   }

   private fun turnRadiusEstimate(): Double {
      if (this.ship != null) {
         val var10000: AABBic = this.ship.getShipAABB()
         if (var10000 != null) {
            val dx: Double = (var10000.maxX() - var10000.minX()) / 2.0
            val dz: Double = (var10000.maxZ() - var10000.minZ()) / 2.0
            return RangesKt.coerceIn(Math.sqrt(dx * dx + dz * dz), 0.5, EurekaConfig.SERVER.maxSizeForTurnSpeedPenalty)
         }
      }

      return EurekaConfig.SERVER.maxSizeForTurnSpeedPenalty
   }

   public fun estimateTopSpeed(): Double {
      if (this.ship != null) {
         val var10000: ShipInertiaData = this.ship.getInertiaData()
         if (var10000 != null) {
            val scaledMass: Double = var10000.getMass() * EurekaConfig.SERVER.speedMassScale
            var speed: Double = EurekaConfig.SERVER.linearCasualSpeed / 3.0
            val fullPower: Double = (double)this.engines * this.cfg.enginePowerLinear
            if (fullPower > 0.0 && scaledMass > 0.0) {
               val boost: Double = Math.max((fullPower - (double)this.cfg.enginePowerLinear * this.cfg.engineBoostOffset) * this.cfg.engineBoost, 0.0)
               speed += this.smoothingATanMax(
                  EurekaConfig.SERVER.maxSpeedFromEngines, (fullPower + (boost + boost * boost * EurekaConfig.SERVER.engineBoostExponentialPower)) / scaledMass
               )
            }

            return Math.max(EurekaConfig.SERVER.baseSpeed, speed)
         }
      }

      return EurekaConfig.SERVER.baseSpeed
   }

   public open fun onServerTick() {
      this.extraForceLinear = this.powerLinear
      this.powerLinear = 0.0
      this.extraForceAngular = this.powerAngular
      this.powerAngular = 0.0
      this.consumed = this.physConsumption * 0.1F
      this.physConsumption = 0.0F
      this.engineFuelPercent = if (this.engineFuelCount > 0)
         RangesKt.coerceIn(MathKt.roundToInt(this.engineFuelAccum / (double)this.engineFuelCount * 100.0), 0, 100)
         else
         0
         this.engineFuelAccum = this.engineFuelAccumNext
      this.engineFuelCount = this.engineFuelCountNext
      this.engineFuelAccumNext = 0.0
      this.engineFuelCountNext = 0
   }

   private class CancelState {
      private final var prevHold: Double
      private final var armed: Boolean

      public fun reset() {
         this.prevHold = 0.0
         this.armed = false
      }

      public fun update(held: Double, heldSign: Int, infSign: Int, duration: Double): Boolean {
         if (held > 0.0 && (this.prevHold <= 0.0 || held < this.prevHold)) {
            this.armed = heldSign != 0 && infSign != 0 && heldSign != infSign
         }

         if (held <= 0.0) {
            this.armed = false
         }

         this.prevHold = held
         return this.armed && held >= duration
      }
   }

   @SourceDebugExtension(["SMAP\nEurekaShipControl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 EurekaShipControl.kt\norg/valkyrienskies/eureka/ship/EurekaShipControl$Companion\n+ 2 AttachmentHolder.kt\norg/valkyrienskies/core/api/attachment/AttachmentHolderKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,1175:1\n66#2:1176\n1#3:1177\n*S KotlinDebug\n*F\n+ 1 EurekaShipControl.kt\norg/valkyrienskies/eureka/ship/EurekaShipControl$Companion\n*L\n1121#1:1176\n*E\n"])
   public companion object {
      private const val ALIGN_THRESHOLD: Double = 0.01
      private const val DISASSEMBLE_THRESHOLD: Double = 0.02
      private const val TURN_OMEGA_MAX_LINEAR: Double = 24.0
      private const val CRUISE_TURN_DEG_MAX: Double = 16.0
      private const val CRUISE_SPEED_TRIM: Double = 0.15
      private const val VERTICAL_TRIM: Float = 0.06F
      private const val GRAVITY: Double = -10.0

      public fun getOrCreate(ship: LoadedServerShip): EurekaShipControl {
         var var10000: EurekaShipControl = (ship as AttachmentHolder).getAttachment(EurekaShipControl.class) as EurekaShipControl
         if (var10000 == null) {
            val var5: EurekaShipControl = EurekaShipControl()
            ship.setAttachment(var5)
            var10000 = var5
         }

         return var10000
      }

      private final val forcePerBalloon: Double
         private final get() {
            return EurekaConfig.SERVER.massPerBalloon * 10.0 * EurekaConfig.SERVER.balloonLiftMultiplier
         }

   }

   private data class ControlData(seatInDirection: class_2350,
      forwardImpulse: Float = 0.0F,
      leftImpulse: Float = 0.0F,
      upImpulse: Float = 0.0F,
      sprintOn: Boolean = false
   ) {
      public final val seatInDirection: class_2350
      public final var forwardImpulse: Float
      public final var leftImpulse: Float
      public final var upImpulse: Float
      public final var sprintOn: Boolean

      init {
         this.seatInDirection = seatInDirection
         this.forwardImpulse = forwardImpulse
         this.leftImpulse = leftImpulse
         this.upImpulse = upImpulse
         this.sprintOn = sprintOn
      }

      public operator fun component1(): class_2350 {
         return this.seatInDirection
      }

      public operator fun component2(): Float {
         return this.forwardImpulse
      }

      public operator fun component3(): Float {
         return this.leftImpulse
      }

      public operator fun component4(): Float {
         return this.upImpulse
      }

      public operator fun component5(): Boolean {
         return this.sprintOn
      }

      public fun copy(
         seatInDirection: class_2350 = this.seatInDirection,
         forwardImpulse: Float = this.forwardImpulse,
         leftImpulse: Float = this.leftImpulse,
         upImpulse: Float = this.upImpulse,
         sprintOn: Boolean = this.sprintOn
      ): org.valkyrienskies.eureka.ship.EurekaShipControl.ControlData {
         return EurekaShipControl.ControlData(seatInDirection, forwardImpulse, leftImpulse, upImpulse, sprintOn)
      }

      public override fun toString(): String {
         return "ControlData(seatInDirection=${this.seatInDirection}, forwardImpulse=${this.forwardImpulse}, leftImpulse=${this.leftImpulse}, upImpulse=${this.upImpulse}, sprintOn=${this.sprintOn})"
      }

      public override fun hashCode(): Int {
         return (
                  ((this.seatInDirection.hashCode() * 31 + java.lang.Float.hashCode(this.forwardImpulse)) * 31 + java.lang.Float.hashCode(this.leftImpulse))
                        * 31
                     + java.lang.Float.hashCode(this.upImpulse)
               )
               * 31
            + java.lang.Boolean.hashCode(this.sprintOn)
         }

      public override operator fun equals(other: Any?): Boolean {
         label46@
         if (this === other) {
            return true
         } else {
            return other is EurekaShipControl.ControlData
               && this.seatInDirection === (other as EurekaShipControl.ControlData).seatInDirection
               && java.lang.Float.compare(this.forwardImpulse, (other as EurekaShipControl.ControlData).forwardImpulse) == 0
               && java.lang.Float.compare(this.leftImpulse, (other as EurekaShipControl.ControlData).leftImpulse) == 0
               && java.lang.Float.compare(this.upImpulse, (other as EurekaShipControl.ControlData).upImpulse) == 0
               && this.sprintOn == (other as EurekaShipControl.ControlData).sprintOn
            }
      }

      public companion object {
         public fun create(player: SeatedControllingPlayer): org.valkyrienskies.eureka.ship.EurekaShipControl.ControlData {
            return EurekaShipControl.ControlData(
               player.getSeatInDirection(), player.getForwardImpulse(), player.getLeftImpulse(), player.getUpImpulse(), player.getSprintOn()
            )
         }
      }
   }
}
