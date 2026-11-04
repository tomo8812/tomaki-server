package org.valkyrienskies.core.impl.networking.simple

import java.util.Arrays
import kotlin.reflect.KClass
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.internal.world.VsiPlayer

public interface SimplePacketNetworking {
   public abstract fun <T : SimplePacket> registerServerHandler(klass: KClass<Any>, handler: (Any, VsiPlayer) -> Unit): RegisteredListener {
   }

   public abstract fun <T : SimplePacket> registerClientHandler(klass: KClass<Any>, handler: (Any) -> Unit): RegisteredListener {
   }

   public abstract fun sendToServer(packet: SimplePacket) {
   }

   public abstract fun sendToClient(packet: SimplePacket, player: VsiPlayer) {
   }

   public abstract fun sendToClients(packet: SimplePacket, vararg players: VsiPlayer) {
   }

   public abstract fun sendToAllClients(packet: SimplePacket) {
   }

   public abstract fun register(klass: KClass<out SimplePacket>) {
   }

   @JvmName(name = "registerServerHandler1")
   @JvmSynthetic
   public open fun <T : SimplePacket> KClass<Any>.registerServerHandler(handler: (Any, VsiPlayer) -> Unit): RegisteredListener {
      return this.registerServerHandler(`$this$registerServerHandler`, handler)
   }

   @JvmName(name = "registerClientHandler1")
   @JvmSynthetic
   public open fun <T : SimplePacket> KClass<Any>.registerClientHandler(handler: (Any) -> Unit): RegisteredListener {
      return this.registerClientHandler(`$this$registerClientHandler`, handler)
   }

   @JvmName(name = "sendToServer1")
   @JvmSynthetic
   public open fun SimplePacket.sendToServer() {
      this.sendToServer(`$this$sendToServer`)
   }

   @JvmName(name = "sendToClient1")
   @JvmSynthetic
   public open fun SimplePacket.sendToClient(player: VsiPlayer) {
      this.sendToClient(`$this$sendToClient`, player)
   }

   @JvmName(name = "sendToClients1")
   @JvmSynthetic
   public open fun SimplePacket.sendToClients(vararg players: VsiPlayer) {
      this.sendToClients(`$this$sendToClients`, Arrays.copyOf(players, players.length))
   }

   @JvmName(name = "sendToAllClients1")
   @JvmSynthetic
   public open fun SimplePacket.sendToAllClients() {
      this.sendToAllClients(`$this$sendToAllClients`)
   }

   @JvmName(name = "register1")
   @JvmSynthetic
   public open fun KClass<out SimplePacket>.register() {
      this.register(`$this$register`)
   }
}
