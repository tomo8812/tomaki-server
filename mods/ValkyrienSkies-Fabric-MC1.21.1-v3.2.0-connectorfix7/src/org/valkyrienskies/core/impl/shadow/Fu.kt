package org.valkyrienskies.core.impl.shadow

import io.netty.buffer.ByteBuf
import java.util.ArrayList
import java.util.Arrays
import java.util.HashMap
import javax.inject.Inject
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.jvm.internal.TypeIntrinsics
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.full.KClasses
import org.apache.logging.log4j.Logger
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.impl.networking.simple.SimplePacket
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.internal.world.VsiPlayer

public open class Fu @Inject  public constructor(p0: dT<Fb>) : SimplePacketNetworking {
   private final val b: dT<Fb>
   private final val a: HashMap<Class<out SimplePacket>, org.valkyrienskies.core.impl.shadow.Fu.b>

   init {
      this.b = var1
      this.c = HashMap<>()
   }

   private fun Class<out SimplePacket>.a(): org.valkyrienskies.core.impl.shadow.Fu.b {
      val var10000: Any = this.c.get(var1)
      if (var10000 == null) {
         throw IllegalArgumentException(("SimplePacket ($var1) not registered").toString())
      } else {
         return var10000 as Fu.b
      }
   }

   private fun Class<out SimplePacket>.b(): Fd {
      return this.a(var1).a
   }

   public override fun <T : SimplePacket> registerServerHandler(p0: KClass<Any>, p1: (Any, VsiPlayer) -> Unit): RegisteredListener {
      this.a(JvmClassMappingKt.getJavaClass(klass)).b.add(TypeIntrinsics.beforeCheckcastToFunctionOfArity(handler, 2) as (SimplePacket?, VsiPlayer?) -> Unit)
      return org/valkyrienskies/core/impl/shadow/Fu##Lambda_1_166(this, klass, handler)
   }

   public override fun <T : SimplePacket> registerClientHandler(p0: KClass<Any>, p1: (Any) -> Unit): RegisteredListener {
      this.a(JvmClassMappingKt.getJavaClass(klass)).c.add(TypeIntrinsics.beforeCheckcastToFunctionOfArity(handler, 1) as (SimplePacket?) -> Unit)
      return org/valkyrienskies/core/impl/shadow/Fu##Lambda_2_180(this, klass, handler)
   }

   public override fun sendToServer(p0: SimplePacket) {
      this.b((Class<? extends SimplePacket>)packet.getClass()).b(Fw.a(packet))
   }

   public override fun sendToClient(p0: SimplePacket, p1: VsiPlayer) {
      this.b((Class<? extends SimplePacket>)packet.getClass()).a(Fw.a(packet), player)
   }

   public override fun sendToClients(p0: SimplePacket, vararg p1: VsiPlayer) {
      if (players.length == 0) {
         throw IllegalArgumentException("Failed requirement.".toString())
      } else {
         val var10000: Fd = this.b((Class<? extends SimplePacket>)packet.getClass())
         val var10001: ByteBuf = Fw.a(packet)
         var var3: Array<VsiPlayer> = Arrays.copyOf(players, players.length)
         val var15: Fb = var10000.a
         val var16: Fc = var10000.a(var10001)
         var3 = Arrays.copyOf(var3, var3.length)
         val var11: Fc = var16
         val var8: Fb = var15
         var3 = var3
         var var4: Int = 0

         for (var5 in var3.length..var4) {
            var8.a(var11, var3[var4])
         }
      }
   }

   public override fun sendToAllClients(p0: SimplePacket) {
      val var10000: Fd = this.b((Class<? extends SimplePacket>)packet.getClass())
      val var2: ByteBuf = Fw.a(packet)
      var10000.a.b(var10000.a(var2))
   }

   public override fun register(p0: KClass<out SimplePacket>) {
      if (!klass.isData()) {
         throw IllegalStateException(("SimplePacket (${JvmClassMappingKt.getJavaClass(klass)}) must be a data class!").toString())
      } else {
         var var10000: Boolean
         run label69@{
            val var4: Fd = this.b.get().a("SimplePacket - ${JvmClassMappingKt.getJavaClass(klass)}")
            val var10: Fu.b = Fu.b(var4, null, null, 6, null)
            this.c.put(JvmClassMappingKt.getJavaClass(klass), var10)
            var4.a(klass.org/valkyrienskies/core/impl/shadow/Fu##Lambda_5_270(klass, var10, var4))
            val var11: Fh = klass.org/valkyrienskies/core/impl/shadow/Fu##Lambda_6_281(klass, var10, var4)
            var4.a.a(var4, var11)
            val var6: java.lang.Iterable
            if ((var6 = KClasses.getDeclaredMemberFunctions(klass)) !is java.util.Collection || !(var6 as java.util.Collection).isEmpty()) {
               val var7: java.util.Iterator = var6.iterator()

               while (var7.hasNext()) {
                  if ((var7.next() as KFunction).getName() == "receivedByClient") {
                     var10000 = true
                     return@label69
                  }
               }
            }

            var10000 = false
         }

         if (var10000) {
            this.registerClientHandler1(klass, <unrepresentable>.a)
         }

         run label75@{
            val var8: java.lang.Iterable
            if ((var8 = KClasses.getDeclaredMemberFunctions(klass)) !is java.util.Collection || !(var8 as java.util.Collection).isEmpty()) {
               val var9: java.util.Iterator = var8.iterator()

               while (var9.hasNext()) {
                  if ((var9.next() as KFunction).getName() == "receivedByServer") {
                     var10000 = true
                     return@label75
                  }
               }
            }

            var10000 = false
         }

         if (var10000) {
            this.registerServerHandler1(klass, <unrepresentable>.a)
         }
      }
   }

   @JvmStatic
   fun a(var0: Fu, var1: KClass, var2: Function2) {
      var0.a(JvmClassMappingKt.getJavaClass(var1)).b.remove(var2)
   }

   @JvmStatic
   fun a(var0: Fu, var1: KClass, var2: Function1) {
      var0.a(JvmClassMappingKt.getJavaClass(var1)).c.remove(var2)
   }

   @JvmStatic
   fun a(var0: KClass, var1: Fu.b, var2: Fd, var3: Fc) {
      val var5: SimplePacket = Fw.a(var0, var3.b)
      val var7: java.util.Iterator = var1.c.iterator()

      while (var7.hasNext()) {
         (var7.next() as Function1)(var5)
      }

      if (var1.c.isEmpty()) {
         Fu.a.a(a).warn("No client handlers registered for the received SimplePacket ($var2)")
      }
   }

   @JvmStatic
   fun a(var0: KClass, var1: Fu.b, var2: Fd, var3: Fc, var4: VsiPlayer) {
      val var6: SimplePacket = Fw.a(var0, var3.b)
      val var8: java.util.Iterator = var1.b.iterator()

      while (var8.hasNext()) {
         (var8.next() as Function2)(var6, var4)
      }

      if (var1.b.isEmpty()) {
         Fu.a.a(a).warn("No server handlers registered for the received SimplePacket ($var2)")
      }
   }

   public companion object a {
      @JvmStatic
      @JvmSynthetic
      private KProperty<Object>[] a;

      private final val a: Logger
         private final get() {
            val var10000: Logger = Fu.d
            return var10000
         }


      @JvmStatic
      @JvmSynthetic
      fun a(var0: Fu.a): Logger {
         val var10000: Logger = Fu.d
         var10000
      }

      @JvmStatic
      fun {
         val var0: Array<KProperty>
         (var0 = arrayOfNulls(1))[0] = Reflection.property1(
            PropertyReference1Impl(Fu.a.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         )
         a = var0
      }
   }

   private data class b(p0: Fd,
      p1: MutableList<(SimplePacket, VsiPlayer) -> Unit> = ArrayList() as java.util.List,
      p2: MutableList<(SimplePacket) -> Unit> = ArrayList() as java.util.List
   ) {
      public final val c: Fd
      public final val b: MutableList<(SimplePacket, VsiPlayer) -> Unit>
      public final val a: MutableList<(SimplePacket) -> Unit>

      init {
         this.a = var1
         this.b = var2
         this.c = var3
      }

      public operator fun d(): Fd {
         return this.a
      }

      public operator fun e(): MutableList<(SimplePacket, VsiPlayer) -> Unit> {
         return this.b
      }

      public operator fun f(): MutableList<(SimplePacket) -> Unit> {
         return this.c
      }

      @JvmStatic
      public fun a(p0: Fd = ..., p1: MutableList<(SimplePacket, VsiPlayer) -> Unit> = ..., p2: MutableList<(SimplePacket) -> Unit> = ...): org.valkyrienskies.core.impl.shadow.Fu.b {
         return Fu.b(var0, var1, var2)
      }

      @JvmStatic
      @JvmSynthetic
      fun `a$default`(var0: Fu.b, var1: Fd, var2: java.util.List, var3: java.util.List, var4: Int): Fu.b {
         if ((var4 and 1) != 0) {
            var1 = var0.a
         }

         if ((var4 and 2) != 0) {
            var2 = var0.b
         }

         if ((var4 and 4) != 0) {
            var3 = var0.c
         }

         Fu.b(var1, var2, var3)
      }

      public override fun toString(): String {
         return "SimplePacketInfo(type=${this.a}, serverHandlers=${this.b}, clientHandlers=${this.c})"
      }

      public override fun hashCode(): Int {
         return (this.a.hashCode() * 31 + this.b.hashCode()) * 31 + this.c.hashCode()
      }

      public override operator fun equals(p0: Any?): Boolean {
         label34@
         if (this === other) {
            return true
         } else {
            return other is Fu.b && this.a == (other as Fu.b).a && this.b == (other as Fu.b).b && this.c == (other as Fu.b).c
         }
      }
   }
}
