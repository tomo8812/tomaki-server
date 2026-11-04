package org.valkyrienskies.mod.common.networking

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import java.util.ArrayList
import java.util.Arrays
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nVSPacketFragmenter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSPacketFragmenter.kt\norg/valkyrienskies/mod/common/networking/VSPacketFragmenter\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,95:1\n12364#2,2:96\n*S KotlinDebug\n*F\n+ 1 VSPacketFragmenter.kt\norg/valkyrienskies/mod/common/networking/VSPacketFragmenter\n*L\n74#1:96,2\n*E\n"])
public object VSPacketFragmenter {
   public const val MAX_PAYLOAD_SIZE: Int = 900000
   private const val FRAGMENT_HEADER_SIZE: Int = 8
   private final val nextGroupId: AtomicInteger = AtomicInteger(0)
   private final val pending: ConcurrentHashMap<Int, Array<ByteBuf?>> = ConcurrentHashMap()

   @JvmStatic
   public fun needsSplitting(buf: ByteBuf): Boolean {
      return buf.readableBytes() > 900000
   }

   @JvmStatic
   public fun split(buf: ByteBuf): List<ByteBuf> {
      val groupId: Int = nextGroupId.getAndIncrement()
      val maxChunkSize: Int = 899992
      val totalFragments: Int = (buf.readableBytes() + 899992 - 1) / 899992
      val fragments: ArrayList = ArrayList(totalFragments)

      repeat(totalFragments) { i ->
         val chunkSize: Int = Math.min(maxChunkSize, buf.readableBytes())
         val fragment: ByteBuf = Unpooled.buffer(8 + chunkSize)
         fragment.writeInt(groupId)
         fragment.writeShort(i)
         fragment.writeShort(totalFragments)
         fragment.writeBytes(buf, chunkSize)
         fragments.add(fragment)
      }

      return fragments
   }

   @JvmStatic
   public fun onReceiveFragment(buf: ByteBuf): ByteBuf? {
      val groupId: Int = buf.readInt()
      val index: Int = buf.readUnsignedShort()
      val var10000: Any = pending.computeIfAbsent(groupId, { p0: Any ->
         `$tmp0`(p0) as Array<ByteBuf>
      })
      val fragments: Array<ByteBuf> = var10000 as Array<ByteBuf>
      (var10000 as Array<ByteBuf>)[index] = Unpooled.copiedBuffer(buf)
      val `$this$all$iv`: Array<Any> = fragments
      var var7: Int = 0
      val var8: Int = fragments.length

      while (true) {
         if (var7 >= var8) {
            var13 = true
            break
         }

         if (`$this$all$iv`[var7] == null) {
            var13 = false
            break
         }

         var7++
      }

      if (var13) {
         pending.remove(groupId)
         return Unpooled.wrappedBuffer(Arrays.copyOf(fragments, fragments.length))
      } else {
         return null
      }
   }

   @JvmStatic
   public fun clear() {
      for (var10000 in pending.values()) {
         for (buf in var10000 as Array<ByteBuf>) {
            if (buf != null) {
               buf.release()
            }
         }
      }

      pending.clear()
   }
}
