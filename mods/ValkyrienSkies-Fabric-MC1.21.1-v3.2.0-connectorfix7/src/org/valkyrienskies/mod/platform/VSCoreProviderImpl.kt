package org.valkyrienskies.mod.platform

import com.mojang.logging.LogUtils
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.core.internal.VsiCoreFactory
import org.valkyrienskies.mod.common.VSCoreProvider
import org.valkyrienskies.mod.fabric.common.FabricHooksImpl
import org.valkyrienskies.mod.fabric.common.VSFabricNetworking

public class VSCoreProviderImpl : VSCoreProvider {
   public override fun newVSCore(): VsiCore {
      val isClient: Boolean = FabricLoader.getInstance().getEnvironmentType() === EnvType.CLIENT
      val networking: VSFabricNetworking = VSFabricNetworking(isClient)
      val hooks: FabricHooksImpl = FabricHooksImpl(networking)
      val vsCore: VsiCore = if (isClient)
         VsiCoreFactory.Companion.instance.newVsCoreClient(hooks)
         else
         VsiCoreFactory.Companion.instance.newVsCoreServer(hooks)
         networking.register(vsCore.hooks)
      LogUtils.getLogger().info("Initialized VSCore on Fabric!")
      return vsCore
   }
}
