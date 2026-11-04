package org.valkyrienskies.mod.common.config

import org.jetbrains.annotations.NotNull

public object VSClientConfig {
   @JvmField
   @NotNull
   public final val CLIENT: org.valkyrienskies.mod.common.config.VSClientConfig.Client = VSClientConfig.Client()

   public class Client {
      public final var shipCameraZoomMin: Double = 0.125
      public final var shipCameraZoomMax: Double = 2.0
      public final var influenceExtendLeft: Double = 2.0
      public final var influenceExtendRight: Double = 2.0
      public final var influenceExtendBottom: Double = 2.0
      public final var influenceExtendTop: Double = 2.0
      public final var influenceExtendBack: Double = 2.0
      public final var influenceExtendFront: Double = 2.0
   }
}
