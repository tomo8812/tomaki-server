package org.valkyrienskies.core.impl.shadow

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.jvm.internal.Intrinsics
import org.joml.Vector3d

public object IN {
   private const val a: Int = 36
   private const val b: Int = 84

   @JvmStatic
   public fun a(p0: IO): ByteArray {
      val var1: ByteArray
      val var2: ByteBuffer
      (var2 = ByteBuffer.wrap(var1 = ByteArray(36)).order(ByteOrder.LITTLE_ENDIAN)).putInt(var0.b)
      var2.putDouble(var0.c)
      Iv.a(var0.d, var2)
      return var1
   }

   @JvmStatic
   public fun a(p0: ByteArray): IO {
      if (var0.length != 36) {
         throw IllegalArgumentException(Intrinsics.stringPlus("rayCastResultBytes has the wrong size ", var0.length))
      } else {
         val var5: ByteBuffer
         val var1: Int = (var5 = ByteBuffer.wrap(var0).order(ByteOrder.LITTLE_ENDIAN)).getInt()
         val var3: Double = var5.getDouble()
         val var6: Vector3d
         return IO(var1, var3, var6)
      }
   }

   @JvmStatic
   public fun a(p0: IP): ByteArray {
      val var1: ByteArray
      val var2: ByteBuffer
      (var2 = ByteBuffer.wrap(var1 = ByteArray(84)).order(ByteOrder.LITTLE_ENDIAN)).putInt(var0.b)
      var2.putDouble(var0.c)
      Iv.a(var0.d, var2)
      Iv.a(var0.e, var2)
      Iv.a(var0.f, var2)
      return var1
   }

   @JvmStatic
   public fun b(p0: ByteArray): IP {
      if (var0.length != 84) {
         throw IllegalArgumentException(Intrinsics.stringPlus("rayCastResultWithVelocityBytes has the wrong size ", var0.length))
      } else {
         val var7: ByteBuffer
         val var1: Int = (var7 = ByteBuffer.wrap(var0).order(ByteOrder.LITTLE_ENDIAN)).getInt()
         val var3: Double = var7.getDouble()
         val var5: Vector3d
         val var6: Vector3d
         val var8: Vector3d
         return IP(var1, var3, var5, var6, var8)
      }
   }
}
