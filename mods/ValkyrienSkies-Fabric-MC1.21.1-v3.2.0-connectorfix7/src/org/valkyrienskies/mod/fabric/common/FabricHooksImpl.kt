package org.valkyrienskies.mod.fabric.common

import io.netty.buffer.ByteBuf
import java.nio.file.Path
import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.mod.common.hooks.CommonHooksImpl

public class FabricHooksImpl(networking: VSFabricNetworking) : CommonHooksImpl {
   private final val networking: VSFabricNetworking

   init {
      this.networking = networking
   }

   public open val isPhysicalClient: Boolean
      public open get() {
         return FabricLoader.getInstance().getEnvironmentType() === EnvType.CLIENT
      }


   public open val configDir: Path
      public open get() {
         val var10000: Path = FabricLoader.getInstance().getConfigDir().resolve("valkyrienskies")
         return var10000
      }


   public override fun sendToServer(buf: ByteBuf) {
      this.networking.sendToServer(buf)
   }

   public override fun sendToClient(buf: ByteBuf, player: VsiPlayer) {
      this.networking.sendToClient(buf, player)
   }
}
