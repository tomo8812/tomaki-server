package org.valkyrienskies.mod.common.util

import com.fasterxml.jackson.annotation.JsonIgnore
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBic
import org.slf4j.Logger
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ShipPhysicsListener
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.mod.common.config.VSGameConfig

public class WaveBuoyancyAttachment : ShipPhysicsListener {
   @JsonIgnore
   @Nullable
   internal final var ship: LoadedServerShip?

   @JsonIgnore
   private final var disabled: Boolean

   @JsonIgnore
   @NotNull
   private final val scratchModelPos: Vector3d = Vector3d()

   @JsonIgnore
   @NotNull
   private final val scratchWorldPos: Vector3d = Vector3d()

   public override fun physTick(physShip: PhysShip, physLevel: PhysLevel) {
      if (!this.disabled) {
         val cfg: VSGameConfig.Server.OCEANWAVES = VSGameConfig.SERVER.OceanWaves
         if (cfg.enableWaveBuoyancy) {
            if (!(physShip.liquidOverlap <= 0.0)) {
               if (this.ship != null) {
                  val ship: LoadedServerShip = this.ship

                  try {
                     val t: Double = physShip.mass
                     if (t <= 0.0) {
                        return
                     }

                     val var10000: AABBic = ship.getShipAABB()
                     if (var10000 == null) {
                        return
                     }

                     val minX: Double = var10000.minX()
                     val maxX: Double = var10000.maxX()
                     val minZ: Double = var10000.minZ()
                     val maxZ: Double = var10000.maxZ()
                     val midY: Double = ((double)var10000.minY() + var10000.maxY()) * 0.5
                     val transform: ShipTransform = physShip.getTransform()
                     val shipToWorld: Matrix4dc = transform.shipToWorld
                     val center: Vector3dc = transform.positionInWorld
                     val vel: Vector3dc = physShip.getVelocity()
                     val omega: Vector3dc = physShip.getOmega()
                     val n: Int = RangesKt.coerceIn(cfg.sampleGrid, 1, 8)
                     val pointMass: Double = t / (n * n)
                     val stiffness: Double = cfg.stiffness
                     val damping: Double = cfg.damping

                     repeat(n) { ix ->
                        repeat(n) { iz ->
                           val modelPos: Vector3d = this.scratchModelPos
                              .set(
                                 minX + (maxX - minX) * (if (n == 1) 0.5 else (double)ix / (double)(n - 1)),
                                 midY,
                                 minZ + (maxZ - minZ) * (if (n == 1) 0.5 else (double)iz / (double)(n - 1))
                              )
                              val worldPos: Vector3d = shipToWorld.transformPosition(
                              minX + (maxX - minX) * (if (n == 1) 0.5 else (double)ix / (double)(n - 1)),
                              midY,
                              minZ + (maxZ - minZ) * (if (n == 1) 0.5 else (double)iz / (double)(n - 1)),
                              this.scratchWorldPos
                           )
                           val force: Double = (
                                 OceanWaveField.INSTANCE.height(worldPos.x, worldPos.z) * stiffness
                                    - (vel.y() + (omega.z() * (worldPos.x - center.x()) - omega.x() * (worldPos.z - center.z()))) * damping
                              )
                              * pointMass
                              if (!java.lang.Double.isInfinite(force) && !java.lang.Double.isNaN(force)) {
                              val var10001: Vector3dc = Vector3d(0.0, force, 0.0)
                              physShip.applyWorldForceToModelPos(var10001, modelPos)
                           }
                        }
                     }
                  } catch (var54: java.lang.Throwable) {
                     this.disabled = true
                     LOGGER.error("[vs wave-buoyancy] disabled for ship ${ship.getId()} after error in physTick", var54)
                  }
               }
            }
         }
      }
   }

   public companion object {
      private final val LOGGER: Logger
   }
}
