package org.valkyrienskies.core.impl.shadow

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableSet
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import it.unimi.dsi.fastutil.longs.Long2ObjectMap
import java.util.Map.Entry
import javax.inject.Inject
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.bodies.properties.BodyKinematics
import org.valkyrienskies.core.impl.api.ServerShipInternal
import org.valkyrienskies.core.internal.physics.PhysicsEntityServer
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.core.util.VSCoreUtilKt

public class FJ @Inject  public constructor(p0: Et, p1: Fl, p2: Fe) {
   private final val d: Et
   private final val a: Fl
   private final val c: Fe
   public final var b: Boolean
   @JvmStatic
   private Logger h = Ga.a(a, FJ.a.a[0]);

   init {
      this.b = var1
      this.c = var2
      this.d = var3
      this.e = true
   }

   public fun a(p0: FL) {
      this.e = !this.e
      if (!this.e) {
         var var2: Et = this.b
         val var7: EB = this.b.d
         val var8: ER = this.b.d.a
         val var10: java.util.Iterator = (this.b.d.a.g as java.util.Map).entrySet().iterator()

         while (var10.hasNext()) {
            val var3: Entry
            val var4: VsiPlayer = (var3 = var10.next() as Entry).getKey() as VsiPlayer
            val var16: ImmutableSet = var3.getValue() as ImmutableSet
            val var5: ByteBuf = Unpooled.buffer()
            val var6: Fl = this.c
            if (this.c.e) {
               for (var23 in CollectionsKt.chunked(var16 as java.lang.Iterable, 3)) {
                  a(var5, var1, this, var4, var23)
               }
            } else {
               val var10004: ImmutableList = var16.asList()
               a(var5, var1, this, var4, var10004 as MutableList<ServerShipInternal>)
            }
         }

         var2 = this.b
         val var12: EB = this.b.d
         val var13: ER = this.b.d.a
         val var15: java.util.Iterator = (this.b.d.a.h as java.util.Map).entrySet().iterator()

         while (var15.hasNext()) {
            val var18: Entry
            val var21: VsiPlayer = (var18 = var15.next() as Entry).getKey() as VsiPlayer
            val var19: ImmutableSet = var18.getValue() as ImmutableSet
            val var22: ByteBuf = Unpooled.buffer()
            val var24: Fl = this.c
            if (this.c.e) {
               for (var25 in CollectionsKt.chunked(var19 as java.lang.Iterable, 10)) {
                  b(var22, var1, this, var21, var25)
               }
            } else {
               val var26: ImmutableList = var19.asList()
               b(var22, var1, this, var21, var26 as MutableList<PhysicsEntityServer>)
            }
         }
      }
   }

   @JvmStatic
   fun a(var0: ByteBuf, var1: FL, var2: FJ, var3: VsiPlayer, var4: MutableList<ServerShipInternal>) {
      var0.clear()

      try {
         FJ.a.a(var0, var4, var1)
      } catch (var5: Exception) {
         FJ.a.a(a).error(var5)
      }

      val var6: Fe = var2.d
      val var10000: Fd = var2.d.d
      var10000.a(var0, var3)
   }

   @JvmStatic
   fun b(var0: ByteBuf, var1: FL, var2: FJ, var3: VsiPlayer, var4: MutableList<PhysicsEntityServer>) {
      var0.clear()
      FJ.a.b(var0, var4, var1)
      val var5: Fe = var2.d
      val var10000: Fd = var2.d.e
      var10000.a(var0, var3)
   }

   public companion object a {
      @JvmStatic
      @JvmSynthetic
      KProperty<Object>[] a;
      public const val a: Int = 48
      public const val b: Int = 128

      @JvmStatic
      public fun a(p0: ByteBuf, p1: List<ServerShipInternal>, p2: FL) {
         val var3: Long2ObjectMap = var2.a
         var0.writeInt(var2.b)

         for (var7 in var5 = var1) {
            val var4: FE
            val var10000: FE = var4 = var3.get(var7.getId()) as FE
            if (var10000 != null) {
               val var10: BodyKinematics = FH.a.a(var4, var7)
               var0.writeLong(var7.getId())
               VSCoreUtilKt.writeVec3d(var0, var10.positionInModel)
               VSCoreUtilKt.writeVec3AsFloat(var0, var10.scaling)
               VSCoreUtilKt.writeNormQuatdAs3F(var0, var10.rotation)
               VSCoreUtilKt.writeVec3d(var0, var10.position)
               VSCoreUtilKt.writeVec3AsFloat(var0, var7.getVelocity())
               VSCoreUtilKt.writeVec3AsFloat(var0, var7.getAngularVelocity())
               var var11: AABBic = var7.getShipAABB()
               if (var11 == null) {
                  var11 = AABBi(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE)
               }

               VSCoreUtilKt.writeAABBi(var0, var11)
            }
         }
      }

      @JvmStatic
      public fun b(p0: ByteBuf, p1: List<PhysicsEntityServer>, p2: FL) {
         val var3: Long2ObjectMap = var2.a
         var0.writeInt(var2.b)

         for (var9 in var7 = var1) {
            val var4: FE
            val var10000: FE = var4 = var3.get(var9.id) as FE
            if (var10000 != null) {
               val var10: Hg = var4.a
               var0.writeLong(var9.id)
               VSCoreUtilKt.writeVec3d(var0, var10.b)
               VSCoreUtilKt.writeNormQuatdAs3F(var0, var10.c)
               var0.writeFloat((float)var4.c)
            }
         }
      }

      private final val c: Logger
         private final get() {
            val var10000: Logger = FJ.h
            return var10000
         }


      @JvmStatic
      @JvmSynthetic
      fun a(var0: FJ.a): Logger {
         val var10000: Logger = FJ.h
         var10000
      }

      @JvmStatic
      fun {
         val var0: Array<KProperty>
         (var0 = arrayOfNulls(1))[0] = Reflection.property1(
            PropertyReference1Impl(FJ.a.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         )
         a = var0
      }
   }
}
