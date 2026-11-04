package org.valkyrienskies.mod.common.assembly

import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import java.util.Queue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.reflect.KProperty
import net.minecraft.class_1923
import net.minecraft.class_1937
import net.minecraft.class_2596
import net.minecraft.class_2626
import net.minecraft.class_2637
import net.minecraft.class_2672
import net.minecraft.class_310
import net.minecraft.class_634
import net.minecraft.class_638
import net.minecraft.class_746
import org.apache.logging.log4j.Logger
import org.joml.Vector2i
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.properties.ChunkClaim
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.networking.PacketRestartChunkUpdates
import org.valkyrienskies.mod.common.networking.PacketStopChunkUpdates
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.mixinducks.feature.seamless_copy.SeamlessCopyClientPacketListenerDuck
import org.valkyrienskies.mod.mixinducks.feature.tickets.PlayerKnownShipsDuck
import org.valkyrienskies.mod.util.ClassLogger

@SourceDebugExtension(["SMAP\nSeamlessChunksManager.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SeamlessChunksManager.kt\norg/valkyrienskies/mod/common/assembly/SeamlessChunksManager\n+ 2 Util.kt\norg/valkyrienskies/core/util/UtilKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,190:1\n42#2,2:191\n42#2,2:194\n1863#3:193\n1864#3:196\n1863#3,2:197\n*S KotlinDebug\n*F\n+ 1 SeamlessChunksManager.kt\norg/valkyrienskies/mod/common/assembly/SeamlessChunksManager\n*L\n69#1:191,2\n86#1:194,2\n80#1:193\n80#1:196\n51#1:197,2\n*E\n"])
public class SeamlessChunksManager(listener: class_634) {
   private final val listener: class_634
   private final val shipQueuedUpdates: ConcurrentHashMap<ChunkClaim, ConcurrentLinkedQueue<class_2596<*>>>
   private final val queuedUpdates: ConcurrentHashMap<class_1923, ConcurrentLinkedQueue<class_2596<*>>>
   private final val stalledChunks: LongOpenHashSet
   private final var dispatching: Boolean
   private final val CHUNK_BUDGET_MS: Long
   private final val deferredDispatch: ConcurrentLinkedQueue<class_2596<*>>

   init {
      this.listener = listener
      this.shipQueuedUpdates = ConcurrentHashMap<>()
      this.queuedUpdates = ConcurrentHashMap<>()
      this.stalledChunks = LongOpenHashSet()
      val `$this$_init__u24lambda_u244`: SimplePacketNetworking = vsCore.simplePacketNetworking
      `$this$_init__u24lambda_u244`.registerClientHandler1(PacketStopChunkUpdates::class, { var1: PacketStopChunkUpdates ->
         for (`element$iv` in var1.component1()) {
            `this$0`.stalledChunks.add(VectorConversionsMCKt.toMinecraft(`element$iv` as Vector2i).method_8324())
         }

         Unit.INSTANCE
      })
      `$this$_init__u24lambda_u244`.registerClientHandler1(PacketRestartChunkUpdates::class, { packet: PacketRestartChunkUpdates ->
         class_310.method_1551().execute({ 
            `this$0`.onRestartUpdates(`$packet`)
         })
         Unit.INSTANCE
      })
      vsApi.getShipLoadEventClient().on({ p0: Any ->
         `$tmp0`(p0)
      })
      this.CHUNK_BUDGET_MS = 5L
      this.deferredDispatch = ConcurrentLinkedQueue<>()
   }

   private fun onShipLoad(ship: ClientShip) {
      val packets: ConcurrentLinkedQueue = this.shipQueuedUpdates.remove(ship.getChunkClaim())
      if (packets != null && !packets.isEmpty()) {
         Companion.logger.debug("Executing ${packets.size()} deferred updates for ship ID=${ship.getId()} at ${ship.getChunkClaim()}")
         val var7: Queue = packets

         while (true) {
            val var10000: Any = var7.poll()
            if (var10000 == null) {
               break
            }

            this.deferredDispatch.add(var10000 as class_2596<*>)
         }
      }

      val var8: class_746 = class_310.method_1551().field_1724
      if (var8 is PlayerKnownShipsDuck) {
         (var8 as PlayerKnownShipsDuck).vs_addKnownShip(ship.getId())
      }
   }

   private fun onRestartUpdates(packet: PacketRestartChunkUpdates) {
      label47@ for (`element$iv` in packet.component1()) {
         val pos: class_1923 = VectorConversionsMCKt.toMinecraft(`element$iv` as Vector2i)
         this.stalledChunks.remove(pos.method_8324())
         val packets: ConcurrentLinkedQueue = this.queuedUpdates.remove(pos)
         if (packets != null && !packets.isEmpty()) {
            Companion.logger.debug("Executing ${packets.size()} deferred updates at <${pos.field_9181}, ${pos.field_9180}>")
            val var15: Queue = packets

            while (true) {
               val var10000: Any = var15.poll()
               if (var10000 == null) {
                  continue@label47
               }

               this.deferredDispatch.add(var10000 as class_2596<*>)
            }
         }
      }
   }

   public fun drainDeferredBatch() {
      if (!this.deferredDispatch.isEmpty()) {
         this.dispatching = true
         val deadline: Long = System.currentTimeMillis() + this.CHUNK_BUDGET_MS

         try {
            while (!this.deferredDispatch.isEmpty()) {
               val var10000: class_2596 = this.deferredDispatch.peek()
               if (var10000 == null) {
                  return
               }

               if (var10000 is class_2672 && System.currentTimeMillis() >= deadline) {
                  return
               }

               this.deferredDispatch.poll()
               if (var10000 is class_2626) {
                  this.listener.method_11136(var10000 as class_2626)
               } else if (var10000 is class_2637) {
                  this.listener.method_11100(var10000 as class_2637)
               } else {
                  if (var10000 !is class_2672) {
                     throw IllegalStateException("Didn't know how to dispatch packet: ${var10000.getClass()::class}")
                  }

                  this.listener.method_11128(var10000 as class_2672)
               }
            }
         } finally {
            this.dispatching = false
         }
      }
   }

   public fun cleanup() {
      this.stalledChunks.clear()
      this.queuedUpdates.clear()
      this.shipQueuedUpdates.clear()
      this.deferredDispatch.clear()
   }

   public fun queue(chunkX: Int, chunkZ: Int, packet: class_2596<*>, level: class_638): Boolean {
      if (VSGameUtilsKt.isChunkInShipyard(level as class_1937, chunkX, chunkZ) && VSGameUtilsKt.getShipManagingPos(level as class_1937, chunkX, chunkZ) == null
         )
       {
         Companion.logger.debug("Deferring ship update at <$chunkX, $chunkZ> for ${packet.getClass()::class}")
         this.shipQueuedUpdates.computeIfAbsent(vsCore.newChunkClaimFromChunkPos(chunkX, chunkZ), { p0: Any ->
            `$tmp0`(p0) as ConcurrentLinkedQueue
         }).add(packet)
         return true
      } else if (this.stalledChunks.contains(class_1923.method_8331(chunkX, chunkZ))) {
         Companion.logger.debug("Deferring update at <$chunkX, $chunkZ> for ${packet.getClass()::class}")
         this.queuedUpdates.computeIfAbsent(class_1923(chunkX, chunkZ), { p0: Any ->
            `$tmp0`(p0) as ConcurrentLinkedQueue
         }).add(packet)
         return true
      } else if (!this.dispatching && packet is class_2672 && VSGameUtilsKt.isChunkInShipyard(level as class_1937, chunkX, chunkZ)) {
         this.deferredDispatch.add(packet)
         return true
      } else {
         Companion.logger.trace("Received update at <$chunkX, $chunkZ> for ${packet.getClass()::class}")
         return false
      }
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      KProperty<Object>[] $$delegatedProperties = arrayOf(
         Reflection.property1(
            PropertyReference1Impl(SeamlessChunksManager.Companion.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         )
      );

      private final val logger: Logger
         private final get() {
            return ClassLogger.getValue_impl/* $VF was: getValue-impl */(SeamlessChunksManager.logger$delegate, this, $$delegatedProperties[0])
         }


      public fun get(): SeamlessChunksManager? {
         val var1: class_634 = class_310.method_1551().method_1562()
         return if ((var1 as? SeamlessCopyClientPacketListenerDuck) != null) (var1 as? SeamlessCopyClientPacketListenerDuck).vs_getChunks() else null
      }
   }
}
