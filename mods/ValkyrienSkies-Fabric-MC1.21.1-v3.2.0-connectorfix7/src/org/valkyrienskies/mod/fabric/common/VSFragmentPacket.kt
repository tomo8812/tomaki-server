package org.valkyrienskies.mod.fabric.common

import kotlin.jvm.functions.Function1
import net.minecraft.class_2540
import net.minecraft.class_2960
import net.minecraft.class_8710
import net.minecraft.class_9135
import net.minecraft.class_9139
import net.minecraft.class_8710.class_9154

public class VSFragmentPacket(data: ByteArray) : class_8710 {
   internal final val data: ByteArray

   init {
      this.data = data
   }

   fun method_56479(): class_9154<VSFragmentPacket> {
      VS_FRAGMENT_TYPE
   }

   @JvmStatic
   fun {
      val var10000: class_2960 = class_2960.method_60655("valkyrienskies", "vs_fragment")
      VS_FRAGMENT_RL = var10000
      VS_FRAGMENT_TYPE = class_9154(VS_FRAGMENT_RL)
      val var0: class_9139 = class_9139.method_56434(class_9135.field_48987, { p0: VSFragmentPacket ->
         (`$tmp0` as Function1)(p0) as ByteArray
      }, { it: ByteArray ->
         VSFragmentPacket(it)
      })
      VS_FRAGMENT_CODEC = var0
   }

   public companion object {
      public final val VS_FRAGMENT_RL: class_2960
      public final val VS_FRAGMENT_TYPE: class_9154<VSFragmentPacket>
      public final val VS_FRAGMENT_CODEC: class_9139<class_2540, VSFragmentPacket>
   }
}
