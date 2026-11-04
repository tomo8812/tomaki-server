package org.valkyrienskies.core.impl.config

import org.jetbrains.annotations.NotNull
import org.valkyrienskies.core.impl.api_impl.config.ConfigPhysicsBackendType
import org.valkyrienskies.core.internal.config.ConfigEntry

public class PhysicsConfig {
   @ConfigEntry(
      description = "The detail of the collision LOD of ships, higher values are more detailed but heavier to compute. Setting this to 0 disables Lod."
   )
   public final var lodDetail: Int = 8192

   @ConfigEntry(description = "Sets number of sub-steps used by Krunch")
   public final var physicsSubSteps: Int = 20

   @ConfigEntry(
      description = "Sets number of iterations per sub-steps used by Krunch Classic. Typically 1 is enough, increasing substeps is more substantial."
   )
   public final var krunchIterations: Int = 1

   @ConfigEntry(
      description = "The maximum number of iterations used by the PhysX to resolve positions. Higher values are more accurate but heavier to compute",
      min = 1.0,
      max = 30.0
   )
   public final var positionIterations: Int = 8

   @ConfigEntry(
      description = "The maximum number of iterations used by PhysX to resolve velocity. Higher values are more accurate but heavier to compute",
      min = 1.0,
      max = 30.0
   )
   public final var velocityIterations: Int = 2

   @ConfigEntry(description = "Limit the max collision de-penetration speed so that rigid bodies don't go flying apart when they overlap")
   public final var maxDePenetrationSpeed: Double = 10.0

   @ConfigEntry(description = "How fast the physics is running. 1.0 is full speed, 0.5 is 50% speed, etc.")
   public final var physicsSpeed: Double = 1.0

   @ConfigEntry(description = "How long ships are held in place after loading so physics and joints can stabilize. Set to 0 to disable.")
   public final var shipLoadFreezeSeconds: Double = 5.0

   @ConfigEntry(description = "Which backend to use.")
   @NotNull
   public final var physicsBackend: ConfigPhysicsBackendType = ConfigPhysicsBackendType.KRUNCH_CLASSIC

   @ConfigEntry(
      description = "How many threads are used by Krunch physics. When set to 0 Krunch will run everything on 1 thread, the same thread that executed krunch"
   )
   public final var physicsThreads: Int = Math.max(Runtime.getRuntime().availableProcessors() - 4, 0)
}
