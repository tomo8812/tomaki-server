package org.valkyrienskies.core.impl.shadow

import io.netty.buffer.ByteBuf
import it.unimi.dsi.fastutil.booleans.BooleanConsumer
import java.lang.annotation.Documented
import java.lang.annotation.RetentionPolicy
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.net.SocketException
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.impl.config.VSCoreConfig
import org.valkyrienskies.core.impl.networking.impl.PacketRequestUdp
import org.valkyrienskies.core.impl.networking.impl.PacketUdpState
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.internal.hooks.VsiCoreHooks
import org.valkyrienskies.core.internal.world.VsiPlayer

@Singleton
public class Fl @Inject  public constructor(p0: Fb, p1: Fb, p2: Fe, p3: SimplePacketNetworking) {
   @JvmStatic
   @JvmSynthetic
   private KProperty<Object>[] g;
   public final val c: Fb
   public final val a: Fb
   private final val h: SimplePacketNetworking
   private final val b: Fd
   public final var d: Boolean
   public final var g: Boolean
   private final var f: RegisteredListener?

   private final val e: Logger
      private final get() {
         val var2: KProperty = g[0];
         val var1: Logger = this.i;
         return var1;
      }


   init {
      this.a = var1;
      this.b = var2;
      this.c = var4;
      this.h = this.b.a("UDP fallback");
      this.i = Ga.a(this, g[0]);
   }

   public fun e() {
      this.b.a(this.h, org/valkyrienskies/core/impl/shadow/Fl##Lambda_0_137(this));
      this.b.a(this.h, org/valkyrienskies/core/impl/shadow/Fl##Lambda_1_149(this));
   }

   public fun f(): Fj? {
      if (VSCoreConfig.SERVER.enableUdp) {
         try {
            val var1: DatagramSocket = DatagramSocket(VSCoreConfig.SERVER.udpPort);
            val var2: Fj = Fj(var1, this.a, this.h);
            this.e = true;
            val var3: SimplePacketNetworking;
            (var3 = this.c).registerServerHandler1(PacketRequestUdp::class, { p0: PacketRequestUdp, p1: VsiPlayer ->
               val var10000: java.lang.Long = var2.a(var2x, var1x);
               if (var10000 != null) {
                  val var5: Fl = Fl.this;
                  var3.sendToClient1(PacketUdpState(var1.getLocalPort(), var5.e, var10000.longValue()), var2x);
               }
            } as Function2);
            return var2;
         } catch (var4: SocketException) {
            this.e.error("Tried to bind to ${VSCoreConfig.SERVER.udpPort} but failed!", var4);
         } catch (var5: Exception) {
            this.e.error("Tried to setup udp with port: ${VSCoreConfig.SERVER.udpPort} but failed!", var5);
         }
      }

      this.g();
      return null;
   }

   public fun a(p0: SocketAddress, p1: SecretKey, p2: BooleanConsumer) {
      if (this.f != null) {
         this.f.unregister();
      }

      val var4: SimplePacketNetworking = this.c;
      this.f = this.c.registerClientHandler1(PacketUdpState::class, { p0: PacketUdpState ->
         var3.accept(var1x.state);
         if (var1x.state) {
            var var2: SocketAddress = var1;
            if (var1 is InetSocketAddress && (var1 as InetSocketAddress).getPort() != var1x.port) {
               var2 = InetSocketAddress((var2 as InetSocketAddress).getAddress(), var1x.port);
            }

            if (!Fl.this.a(var2, var1x.id)) {
               Fl.this.g();
            }
         }
      } as Function1);
      val var10004: ByteArray = var2.getEncoded();
      var4.sendToServer1(PacketRequestUdp(0, var10004));
   }

   private fun a(p0: SocketAddress, p1: Long): Boolean {
      try {
         Fi(DatagramSocket(), this.a, var1, var2, {
            val var1: Fl = Fl.this;
            Fl.this.d = true;
         } as () -> Unit);
         return true;
      } catch (var5: Exception) {
         this.e.error("Tried to setup udp client with socket address: $var1 but failed!", var5);
         return false;
      }
   }

   private fun g() {
      this.e.info("We are not using UDP, falling back to TCP");
      this.d = false;
      this.e = false;
      val var1: SimplePacketNetworking;
      (var1 = this.c).registerServerHandler1(PacketRequestUdp::class, { p0: PacketRequestUdp, p1: VsiPlayer ->
         val var3: Fl = Fl.this;
         var1.sendToClient1(PacketUdpState(-1, Fl.this.e, -1L), var2);
      } as Function2);
      this.a.h = { p0: ByteBuf, p1: VsiPlayer ->
         Fl.this.h.a(var1, var2);
      } as Function2;
      this.a.i = { p0: ByteBuf ->
         Fl.this.h.b(var1);
      } as Function1;
   }

   private fun h() {
      this.b.a(this.h, org/valkyrienskies/core/impl/shadow/Fl##Lambda_0_137(this));
      this.b.a(this.h, org/valkyrienskies/core/impl/shadow/Fl##Lambda_1_149(this));
   }

   @JvmStatic
   fun a(var0: Fl, var1: Fc) {
      var0.a.a(var1.b);
   }

   @JvmStatic
   fun a(var0: Fl, var1: Fc, var2: VsiPlayer) {
      var0.a.a(var1.b, var2);
   }

   @JvmStatic
   fun {
      val var0: Array<KProperty>;
      (var0 = arrayOfNulls(1))[0] = Reflection.property1(
         PropertyReference1Impl(Fl.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
      );
      g = var0;
   }

   @dW
   public abstract class a {
      @dP
      @Singleton
      public abstract fun a(p0: Fu): SimplePacketNetworking {
      }

      public companion object a {
         @dX
         @Singleton
         @JvmStatic
         public fun a(p0: Provider<VsiCoreHooks>): Fb {
            return Fb(var0);
         }

         @dX
         @Singleton
         @JvmStatic
         public fun b(p0: Provider<VsiCoreHooks>): Fb {
            return Fb(var0);
         }
      }

      // $VF: Class flags could not be determined
      @Qualifier
      @MustBeDocumented
      @Retention(AnnotationRetention.BINARY)
      @Documented
      @java.lang.annotation.Retention(RetentionPolicy.CLASS)
      annotation class b(

      )

      // $VF: Class flags could not be determined
      @Qualifier
      @MustBeDocumented
      @Retention(AnnotationRetention.BINARY)
      @Documented
      @java.lang.annotation.Retention(RetentionPolicy.CLASS)
      annotation class c(

      )
   }
}
