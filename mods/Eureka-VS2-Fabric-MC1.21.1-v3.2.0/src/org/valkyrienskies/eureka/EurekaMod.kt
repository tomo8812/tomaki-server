package org.valkyrienskies.eureka

import org.valkyrienskies.core.api.attachment.AttachmentRegistration.Builder
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

public object EurekaMod {
   public const val MOD_ID: String = "vs_eureka"

   @JvmStatic
   public fun init() {
      EurekaConfigLoader.loadOrCreate()
      EurekaBlocks.INSTANCE.register()
      EurekaBlockEntities.INSTANCE.register()
      EurekaItems.INSTANCE.register()
      EurekaScreens.INSTANCE.register()
      EurekaEntities.INSTANCE.register()
      EurekaWeights.INSTANCE.register()
      ValkyrienSkiesMod.getVsCore().registerAttachment(EurekaShipControl::class.java, { $this$registerAttachment: Builder ->
         `$this$registerAttachment`.useLegacySerializer()
         Unit.INSTANCE
      })
   }

   @JvmStatic
   public fun initClient() {
      EurekaClientScreens.INSTANCE.register()
   }
}
