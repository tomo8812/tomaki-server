package org.valkyrienskies.core.impl.config

import org.jetbrains.annotations.NotNull
import org.valkyrienskies.core.internal.config.ConfigCategory
import org.valkyrienskies.core.internal.config.ConfigEntry

public object VSCoreConfig {
   @JvmField
   @NotNull
   public final val SERVER: org.valkyrienskies.core.impl.config.VSCoreConfig.Server = VSCoreConfig.Server()

   public class Server {
      @ConfigCategory(title = "Physics Thread Settings")
      @NotNull
      public final val pt: org.valkyrienskies.core.impl.config.VSCoreConfig.Server.PhysicsThreadSettings = VSCoreConfig.Server.PhysicsThreadSettings()

      @ConfigCategory(title = "Connectivity Settings (Experimental)")
      @NotNull
      public final val sp: org.valkyrienskies.core.impl.config.VSCoreConfig.Server.ConnectivitySettings = VSCoreConfig.Server.ConnectivitySettings()

      @ConfigCategory(title = "Drag Settings")
      @NotNull
      public final val ds: org.valkyrienskies.core.impl.config.VSCoreConfig.Server.DragSettings = VSCoreConfig.Server.DragSettings()

      @ConfigEntry(description = "Port to attempt to establish UDP connections on")
      public final var udpPort: Int = 25565

      @ConfigEntry(description = "Is udp enabled? If not, the server will only use TCP")
      public final var enableUdp: Boolean

      @ConfigEntry(description = "Ship load distance in blocks")
      public final var shipLoadDistance: Double = 128.0

      @ConfigEntry(description = "Ship unload distance in blocks")
      public final var shipUnloadDistance: Double = 196.0

      @ConfigCategory(title = "Krunch Konfig")
      @NotNull
      public final var physics: PhysicsConfig = PhysicsConfig()

      public class ConnectivitySettings {
         @ConfigEntry(
            description = "Enable/Disable Connectivity (May reduce memory usage. Use [enableSplitting] instead if you just want to disable ship splitting.)"
         )
         public final var enableConnectivity: Boolean

         @ConfigEntry(description = "Enable/Disable World Connectivity (Experimental!)")
         public final var enableWorldConnectivity: Boolean

         @ConfigEntry(description = "Enable/Disable Block Edge Connectivity (WARNING! May significantly increase memory usage)")
         public final var enableBlockEdgeConnectivity: Boolean = true

         @ConfigEntry(description = "Enable/Disable Block Corner Connectivity (WARNING! May significantly increase memory usage)")
         public final var enableBlockCornerConnectivity: Boolean

         @ConfigEntry(description = "Enable/Disable Splitting (Disabling this setting will not prevent the use of Air Pockets.)")
         public final var enableSplitting: Boolean

         @ConfigEntry(description = "Enable verbose logging of connectivity (May result in very large log files)")
         public final var verboseConnectivityLogging: Boolean
      }

      public class DragSettings {
         @ConfigEntry(description = "Enable/Disable Drag on all bodies.")
         public final var enableDrag: Boolean = true

         @ConfigEntry(description = "Enable/Disable Lift on all bodies.")
         public final var enableLift: Boolean = true

         @ConfigEntry(
            description = "The drag coefficient used to represent a solid 1x1x1 cube. This is an exaggerated value by default due to the scale of builds in MC."
         )
         public final var dragCoefficient: Double = 2.18

         @ConfigEntry(description = "A static multiplier applied to drag forces. Useful for when your configured masses are higher/lower than the default.")
         public final var dragMultiplier: Double = 10.0

         @ConfigEntry(description = "A static multiplier applied to lift forces. Useful for when your configured masses are higher/lower than the default.")
         public final var liftMultiplier: Double = 10.0

         @ConfigEntry(
            description = "Bias towards up/gravity for lift. Makes things act more arcade-y, but can cause loop-de-looping. Range: 0.0 (no bias) to 1.0 (full bias), but best between 0.3 and 0.7."
         )
         public final var biasFactor: Double = 0.3
      }

      public class PhysicsThreadSettings {
         @ConfigEntry(description = "Synchronize the physics thread and the game thread")
         public final var synchronizePhysics: Boolean

         @ConfigEntry(description = "default is 3, for 60 phys ticks per second")
         public final var physicsTicksPerGameTick: Int = 3

         @ConfigEntry(
            description = "Number of terrain baking threads, more is good for preventing stutters when flying fast ships. However, too many will overload the cpu which will lag the game."
         )
         public final var terrainBakingThreads: Int = Runtime.getRuntime().availableProcessors() - 4

         @ConfigEntry(description = "Thread priority of terrain baking threads. 5 is default priority, higher is higher priority, lower is lower priority.")
         public final var terrainBakingThreadPriority: Int = 5

         @ConfigEntry(description = "Number of terrain deletion threads. Typically just 1 is fine.")
         public final var terrainDeletionThreads: Int = 1

         @ConfigEntry(description = "Thread priority of terrain deletion threads. These are used to clean up memory, so they can be lower priority than.")
         public final var terrainDeletionThreadPriority: Int = 4
      }
   }
}
