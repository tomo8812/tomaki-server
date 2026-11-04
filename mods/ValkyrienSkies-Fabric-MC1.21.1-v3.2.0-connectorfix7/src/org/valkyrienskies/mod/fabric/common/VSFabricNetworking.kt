package org.valkyrienskies.mod.fabric.common

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.Context
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.class_1657
import net.minecraft.class_3222
import org.valkyrienskies.core.internal.hooks.VsiCoreHooksIn
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.mod.common.networking.VSPacketFragmenter
import org.valkyrienskies.mod.common.util.MinecraftPlayer

public class VSFabricNetworking(isClient: Boolean) {
   private final val isClient: Boolean

   init {
      this.isClient = isClient
   }

   private fun registerClientNetworking(hooks: VsiCoreHooksIn) {
      ClientPlayNetworking.registerGlobalReceiver(VSPacket.Companion.VS_PACKET_TYPE, { packet: VSPacket, context: Context ->
         context.client().execute({ 
            val var10001: ByteBuf = Unpooled.wrappedBuffer(`$packet`.data)
            `$hooks`.onReceiveClient(var10001)
         })
      })
      ClientPlayNetworking.registerGlobalReceiver(VSFragmentPacket.Companion.VS_FRAGMENT_TYPE, { packet: VSFragmentPacket, context: Context ->
         context.client().execute({ 
            val var10000: ByteBuf = Unpooled.wrappedBuffer(`$packet`.data)
            val assembled: ByteBuf = VSPacketFragmenter.onReceiveFragment(var10000)
            if (assembled != null) {
               `$hooks`.onReceiveClient(assembled)
            }
         })
      })
   }

   public fun register(hooks: VsiCoreHooksIn) {
      PayloadTypeRegistry.playC2S().register(VSPacket.Companion.VS_PACKET_TYPE, VSPacket.Companion.VS_PACKET_CODEC)
      PayloadTypeRegistry.playS2C().register(VSPacket.Companion.VS_PACKET_TYPE, VSPacket.Companion.VS_PACKET_CODEC)
      PayloadTypeRegistry.playC2S().register(VSFragmentPacket.Companion.VS_FRAGMENT_TYPE, VSFragmentPacket.Companion.VS_FRAGMENT_CODEC)
      PayloadTypeRegistry.playS2C().register(VSFragmentPacket.Companion.VS_FRAGMENT_TYPE, VSFragmentPacket.Companion.VS_FRAGMENT_CODEC)
      if (this.isClient) {
         this.registerClientNetworking(hooks)
      }

      ServerPlayNetworking.registerGlobalReceiver(
         VSPacket.Companion.VS_PACKET_TYPE, { packet: VSPacket, context: net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context ->
            context.server().execute({ 
               val var10001: ByteBuf = Unpooled.wrappedBuffer(`$packet`.data)
               val var10002: class_3222 = `$context`.player()
               val var3: MinecraftPlayer = playerWrapper
               `$hooks`.onReceiveServer(var10001, var3)
            })
         }
      )
      ServerPlayNetworking.registerGlobalReceiver(
         VSFragmentPacket.Companion.VS_FRAGMENT_TYPE,
         { packet: VSFragmentPacket, context: net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.Context ->
            context.server().execute({ 
               val var10000: ByteBuf = Unpooled.wrappedBuffer(`$packet`.data)
               val assembled: ByteBuf = VSPacketFragmenter.onReceiveFragment(var10000)
               if (assembled != null) {
                  val var10002: class_3222 = `$context`.player()
                  val var4: MinecraftPlayer = playerWrapper
                  `$hooks`.onReceiveServer(assembled, var4)
               }
            })
         }
      )
   }

   public fun sendToClient(data: ByteBuf, player: VsiPlayer) {
      val fragment: class_1657 = if ((player as? MinecraftPlayer) != null) (player as? MinecraftPlayer).player else null
      val var10000: class_3222 = fragment as? class_3222
      if ((fragment as? class_3222) != null) {
         val serverPlayer: class_3222 = var10000
         if (VSPacketFragmenter.needsSplitting(data)) {
            val var7: ByteBuf = data.copy()

            for (var6 in VSPacketFragmenter.split(var7)) {
               ServerPlayNetworking.send(serverPlayer, VSFragmentPacket(this.copyToByteArray(var6)))
            }
         } else {
            ServerPlayNetworking.send(var10000, VSPacket(this.copyToByteArray(data)))
         }
      }
   }

   public fun sendToServer(data: ByteBuf) {
      if (VSPacketFragmenter.needsSplitting(data)) {
         val var10000: ByteBuf = data.copy()

         for (fragment in VSPacketFragmenter.split(var10000)) {
            ClientPlayNetworking.send(VSFragmentPacket(this.copyToByteArray(fragment)))
         }
      } else {
         ClientPlayNetworking.send(VSPacket(this.copyToByteArray(data)))
      }
   }

   private fun ByteBuf.copyToByteArray(): ByteArray {
      val byteArray: ByteArray = ByteArray(`$this$copyToByteArray`.readableBytes())
      `$this$copyToByteArray`.getBytes(`$this$copyToByteArray`.readerIndex(), byteArray)
      return byteArray
   }
}
