package org.valkyrienskies.core.impl.chunk_tracking

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIncludeProperties
import it.unimi.dsi.fastutil.longs.LongIterator
import it.unimi.dsi.fastutil.longs.LongOpenHashSet
import org.valkyrienskies.core.api.ships.properties.IShipActiveChunksSet
import org.valkyrienskies.core.api.util.functions.IntBinaryConsumer

@JsonIncludeProperties(["chunkClaimSet"])
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public data class ShipActiveChunksSet(chunkClaimSet: LongOpenHashSet) : IShipActiveChunksSet {
   private final val chunkClaimSet: LongOpenHashSet

   init {
      this.chunkClaimSet = chunkClaimSet
   }

   public override fun add(chunkX: Int, chunkZ: Int): Boolean {
      return this.chunkClaimSet.add(IShipActiveChunksSet.Companion.chunkPosToLong(chunkX, chunkZ))
   }

   public override fun remove(chunkX: Int, chunkZ: Int): Boolean {
      return this.chunkClaimSet.remove(IShipActiveChunksSet.Companion.chunkPosToLong(chunkX, chunkZ))
   }

   public override fun contains(chunkX: Int, chunkZ: Int): Boolean {
      return this.chunkClaimSet.contains(IShipActiveChunksSet.Companion.chunkPosToLong(chunkX, chunkZ))
   }

   @Deprecated(message = "Uses boxed integers as parameters and requires Unit return type, bad for performance and Java interop", replaceWith = @ReplaceWith(expression = "forEach(func)", imports = []))
   public override fun iterateChunkPos(func: (Int, Int) -> Unit) {
      val var10000: LongIterator = this.chunkClaimSet.iterator()
      val var2: LongIterator = var10000

      while (var2.hasNext()) {
         val var3: Long = var2.nextLong()
         func(IShipActiveChunksSet.Companion.longToChunkX(var3), IShipActiveChunksSet.Companion.longToChunkZ(var3))
      }
   }

   public override fun forEach(func: IntBinaryConsumer) {
      val var10000: LongIterator = this.chunkClaimSet.iterator()
      val var2: LongIterator = var10000

      while (var2.hasNext()) {
         val var3: Long = var2.nextLong()
         func.accept(IShipActiveChunksSet.Companion.longToChunkX(var3), IShipActiveChunksSet.Companion.longToChunkZ(var3))
      }
   }

   public open val size: Int
      public open get() {
         return this.chunkClaimSet.size()
      }


   public override operator fun equals(other: Any?): Boolean {
      return super.equals(other) || other is ShipActiveChunksSet && this.chunkClaimSet == (other as ShipActiveChunksSet).chunkClaimSet
   }

   public override fun hashCode(): Int {
      return this.chunkClaimSet.hashCode()
   }

   public fun clone(): ShipActiveChunksSet {
      val var10002: LongOpenHashSet = this.chunkClaimSet.clone()
      return ShipActiveChunksSet(var10002)
   }

   private operator fun component1(): LongOpenHashSet {
      return this.chunkClaimSet
   }

   public fun copy(chunkClaimSet: LongOpenHashSet = this.chunkClaimSet): ShipActiveChunksSet {
      return ShipActiveChunksSet(chunkClaimSet)
   }

   public override fun toString(): String {
      return "ShipActiveChunksSet(chunkClaimSet=${this.chunkClaimSet})"
   }

   public companion object {
      public fun create(): ShipActiveChunksSet {
         return ShipActiveChunksSet(LongOpenHashSet())
      }
   }
}
