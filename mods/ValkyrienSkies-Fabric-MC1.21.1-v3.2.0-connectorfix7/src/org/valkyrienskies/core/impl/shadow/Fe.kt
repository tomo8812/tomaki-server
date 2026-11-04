package org.valkyrienskies.core.impl.shadow

import javax.inject.Inject
import javax.inject.Singleton
import org.valkyrienskies.core.impl.networking.impl.PacketCommonConfigUpdate
import org.valkyrienskies.core.impl.networking.impl.PacketPhysEntityCreate
import org.valkyrienskies.core.impl.networking.impl.PacketPhysEntityRemove
import org.valkyrienskies.core.impl.networking.impl.PacketRequestUdp
import org.valkyrienskies.core.impl.networking.impl.PacketServerConfigUpdate
import org.valkyrienskies.core.impl.networking.impl.PacketShipDataCreate
import org.valkyrienskies.core.impl.networking.impl.PacketShipRemove
import org.valkyrienskies.core.impl.networking.impl.PacketUdpState
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking

@Singleton
public class Fe @Inject  public constructor(p0: Fb, p1: Fb, p2: SimplePacketNetworking) {
   public final val d: SimplePacketNetworking
   public final val a: Fd
   public final val c: Fd
   public final val b: Fd

   init {
      this.b = var3
      this.c = var1.a("Ship data delta update")
      this.d = var2.a("Ship transform update")
      this.e = var2.a("Physics Entity transform update")
      a = this
      val var4: SimplePacketNetworking
      (var4 = this.b).register1(PacketRequestUdp::class)
      var4.register1(PacketUdpState::class)
      var4.register1(PacketShipDataCreate::class)
      var4.register1(PacketShipRemove::class)
      var4.register1(PacketCommonConfigUpdate::class)
      var4.register1(PacketServerConfigUpdate::class)
      var4.register1(PacketPhysEntityCreate::class)
      var4.register1(PacketPhysEntityRemove::class)
   }

   public companion object a {
      @Deprecated(message = "global state")
      public final lateinit var a: Fe
   }
}
