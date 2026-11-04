package org.valkyrienskies.core.impl.shadow

import io.netty.buffer.ByteBuf
import java.nio.file.Path
import javax.inject.Inject
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.internal.hooks.VsiCoreHooks
import org.valkyrienskies.core.internal.hooks.VsiCoreHooksOut
import org.valkyrienskies.core.internal.hooks.VsiPlayState
import org.valkyrienskies.core.internal.world.VsiPlayer

public class EX @Inject  public constructor(p0: VsiCoreHooksOut, p1: Fb) : VsiCoreHooks, VsiCoreHooksOut {
   public final val o: Fb
   @JvmSynthetic
   private VsiCoreHooksOut b;
   public open var h: Boolean
   public open var l: Boolean
   public open var g: Boolean
   public open var f: Boolean
   public open var k: Boolean
   public open var i: Boolean
   public open var j: Boolean
   public open var d: Double
   public open var e: Double
   public open var n: Double

   init {
      this.a = var2
      this.b = var1
      this.c = VSCoreConfig.SERVER.sp.enableConnectivity
      this.d = VSCoreConfig.SERVER.sp.enableWorldConnectivity
      this.e = VSCoreConfig.SERVER.sp.enableBlockEdgeConnectivity
      this.f = VSCoreConfig.SERVER.sp.enableBlockCornerConnectivity
      this.g = VSCoreConfig.SERVER.sp.enableSplitting
      this.h = VSCoreConfig.SERVER.ds.enableDrag
      this.i = VSCoreConfig.SERVER.ds.enableLift
      this.j = VSCoreConfig.SERVER.ds.dragCoefficient
      this.k = VSCoreConfig.SERVER.ds.dragMultiplier
      this.l = VSCoreConfig.SERVER.ds.liftMultiplier
   }

   public open val a: Path

   public open val b: ShipWorld

   public open val c: ShipWorld?

   public open val m: Boolean

   public open val p: VsiPlayState

   public override fun sendToClient(p0: ByteBuf, p1: VsiPlayer) {
      this.b.sendToClient(buf, player)
   }

   public override fun sendToServer(p0: ByteBuf) {
      this.b.sendToServer(buf)
   }

   public override fun onReceiveServer(p0: ByteBuf, p1: VsiPlayer) {
      this.a.a(buf, sender)
   }

   public override fun onReceiveClient(p0: ByteBuf) {
      this.a.a(buf)
   }

   public override fun afterDisconnect() {
   }

   public override fun afterClientJoinServer(p0: VsiPlayer) {
   }
}
