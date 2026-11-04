package org.valkyrienskies.core.impl.shadow

import io.netty.buffer.ByteBuf
import io.netty.buffer.CompositeByteBuf
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import java.util.ArrayList
import java.util.HashSet
import javax.inject.Provider
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function2
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.internal.hooks.VsiCoreHooks
import org.valkyrienskies.core.internal.world.VsiPlayer

public class Fb(p0: Provider<VsiCoreHooks>) {
   @JvmStatic
   @JvmSynthetic
   private KProperty<Object>[] c;

   private final val f: VsiCoreHooks
      private final get() {
         return FZ.a(this.d, c[0])
      }


   private final val g: ArrayList<Fd>
   private final val j: Int2ObjectOpenHashMap<MutableSet<Fh>>
   private final val a: Int2ObjectOpenHashMap<MutableSet<Fa>>
   private final val e: HashSet<Fh>
   private final val d: HashSet<Fa>
   private final var c: Boolean
   private final var k: Boolean
   private final val b: ArrayList<Fc>
   public final lateinit var i: (ByteBuf) -> Unit
   public final lateinit var h: (ByteBuf, VsiPlayer) -> Unit

   init {
      this.d = var1
      this.e = ArrayList<>()
      this.f = Int2ObjectOpenHashMap()
      this.g = Int2ObjectOpenHashMap()
      this.h = HashSet<>()
      this.i = HashSet<>()
      this.k = ArrayList<>()
   }

   public fun a(p0: String): Fd {
      val var3: Fd
      this.e.add(var3 = Fd(this, this.e.size(), var1))
      return var3
   }

   public fun a(p0: Fh): RegisteredListener {
      this.h.add(var1)
      return org/valkyrienskies/core/impl/shadow/Fb##Lambda_0_162(this, var1)
   }

   public fun a(p0: Fa): RegisteredListener {
      this.i.add(var1)
      return org/valkyrienskies/core/impl/shadow/Fb##Lambda_1_169(this, var1)
   }

   public fun a(p0: Fd, p1: Fh): RegisteredListener {
      (this.f.computeIfAbsent(var1.b, org/valkyrienskies/core/impl/shadow/Fb##Lambda_2_183()) as java.util.Set).add(var2)
      return org/valkyrienskies/core/impl/shadow/Fb##Lambda_3_197(this, var1, var2)
   }

   public fun a(p0: Fd, p1: Fa): RegisteredListener {
      (this.g.computeIfAbsent(var1.b, org/valkyrienskies/core/impl/shadow/Fb##Lambda_4_183()) as java.util.Set).add(var2)
      return org/valkyrienskies/core/impl/shadow/Fb##Lambda_5_208(this, var1, var2)
   }

   public fun a(p0: ByteBuf) {
      val var4: Fc = this.b(var1)
      if (this.j) {
         this.c(var4)
      } else {
         val var3: ArrayList = this.k
         var4.b.retain()
         var3.add(var4)
      }
   }

   private fun c(p0: Fc) {
      var var2: Fd = var1.a
      val var5: java.util.Set = this.g.get(var1.a.b) as java.util.Set
      var var7: java.util.Iterator = this.i.iterator()

      while (var7.hasNext()) {
         (var7.next() as Fa).handlePacket(var1)
      }

      if (var5 != null) {
         var7 = var5.iterator()

         while (var7.hasNext()) {
            (var7.next() as Fa).handlePacket(var1)
         }
      }

      if (this.i.isEmpty() && (var5 == null || var5.isEmpty())) {
         val var10000: Logger = a.b
         var2 = var1.a
         var10000.warn("Received a packet ${var1.a.c} on the client, but no handlers were registered")
      }
   }

   public fun a(p0: ByteBuf, p1: VsiPlayer) {
      val var3: Fc = this.b(var1)
      if (this.b) {
         this.b(var3, var2)
      }
   }

   private fun b(p0: Fc, p1: VsiPlayer) {
      var var3: Fd = var1.a
      val var6: java.util.Set = this.f.get(var1.a.b) as java.util.Set
      var var8: java.util.Iterator = this.h.iterator()

      while (var8.hasNext()) {
         (var8.next() as Fh).handlePacket(var1, var2)
      }

      if (var6 != null) {
         var8 = var6.iterator()

         while (var8.hasNext()) {
            (var8.next() as Fh).handlePacket(var1, var2)
         }
      }

      if (this.h.isEmpty() && (var6 == null || var6.isEmpty())) {
         val var10000: Logger = a.b
         var3 = var1.a
         var10000.warn("Received a packet ${var1.a.c} on the server, but no handlers were registered")
      }
   }

   private fun b(p0: ByteBuf): Fc {
      val var10000: Any = this.e.get(var1.readInt())
      return Fc(var10000 as Fd, var1)
   }

   @JvmStatic
   private fun d(p0: Fc): ByteBuf {
      val var1: CompositeByteBuf = Unpooled.compositeBuffer(2)
      val var2: ByteBuf
      val var10000: ByteBuf = var2 = Unpooled.buffer(4)
      val var4: Fd = var0.a
      var10000.writeInt(var0.a.b)
      val var3: Array<ByteBuf>
      (var3 = arrayOfNulls(2))[0] = var2
      var3[1] = var0.b
      val var6: CompositeByteBuf = var1.addComponents(true, var3)
      return var6 as ByteBuf
   }

   public fun a(p0: Fc) {
      var var10000: Function1 = this.l
      if (this.l == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      var10000(d(var1))
   }

   public fun a(p0: Fc, p1: VsiPlayer) {
      var var10000: Function2 = this.m
      if (this.m == null) {
         Intrinsics.throwUninitializedPropertyAccessException("")
         var10000 = null
      }

      var10000(d(var1), var2)
   }

   public fun a(p0: Fc, vararg p1: VsiPlayer) {
      var2 = var2
      var var3: Int = 0

      for (var4 in var2.length..var3) {
         this.a(var1, var2[var3])
      }
   }

   public fun b(p0: Fc) {
      val var10000: Et = FZ.<VsiCoreHooks>a(this.d, c[0]).getCurrentShipServerWorld() as Et
      if (var10000 == null) {
         throw IllegalArgumentException(("Tried to send a packet of type ${var1.a} to all clients, but there is no server currently running!").toString())
      } else {
         for (var3 in var10000.getPlayers()) {
            this.a(var1, var3)
         }
      }
   }

   public fun a() {
      this.b = true
   }

   public fun b() {
      this.j = true

      val var1: java.lang.Iterable
      for (var2 in var1 = this.k) {
         this.c(var2)
         var2.b.release()
      }

      this.k.clear()
   }

   public fun c() {
      this.b = false
      this.j = false
   }

   @JvmStatic
   fun a(var0: Fb, var1: Fh) {
      var0.h.remove(var1)
   }

   @JvmStatic
   fun a(var0: Fb, var1: Fa) {
      var0.i.remove(var1)
   }

   @JvmStatic
   fun a(var0: Int): HashSet {
      HashSet()
   }

   @JvmStatic
   fun a(var0: Fb, var1: Fd, var2: Fh) {
      val var10000: java.util.Set = var0.f.get(var1.b) as java.util.Set
      if (var10000 != null) {
         var10000.remove(var2)
      }
   }

   @JvmStatic
   fun b(var0: Int): HashSet {
      HashSet()
   }

   @JvmStatic
   fun a(var0: Fb, var1: Fd, var2: Fa) {
      val var10000: java.util.Set = var0.g.get(var1.b) as java.util.Set
      if (var10000 != null) {
         var10000.remove(var2)
      }
   }

   public companion object a {
      @JvmStatic
      @JvmSynthetic
      KProperty<Object>[] a;
      public const val a: Boolean = false

      public final val b: Logger
         public final get() {
            val var10000: Logger = Fb.n
            return var10000
         }


      @JvmStatic
      fun {
         val var0: Array<KProperty>
         (var0 = arrayOfNulls(1))[0] = Reflection.property1(
            PropertyReference1Impl(Fb.a.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         )
         a = var0
      }
   }
}
