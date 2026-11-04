package org.valkyrienskies.core.impl.shadow

import kotlin.coroutines.CoroutineContext
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineScopeKt
import org.apache.logging.log4j.Logger
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.impl.api.ShipInternal
import org.valkyrienskies.core.impl.game.ChunkAllocatorProvider
import org.valkyrienskies.core.internal.ships.VsiQueryableShipData
import org.valkyrienskies.core.internal.world.VsiShipWorld
import org.valkyrienskies.core.internal.world.chunks.VsiBlockType
import org.valkyrienskies.core.internal.world.chunks.VsiBlockTypes

public abstract class Ev<ShipObjectType extends Eq> : VsiShipWorld {
   private final val chunkAllocators: ChunkAllocatorProvider
   private final val blockTypes: VsiBlockTypes
   private final val _dispatcher: Gy
   public final val dispatcher: CoroutineDispatcher
   public final val coroutineScope: CoroutineScope

   public final var tickNumber: Int
      private set

   open fun Ev(var1: ChunkAllocatorProvider, var2: VsiBlockTypes) {
      this.chunkAllocators = var1
      this.blockTypes = var2
      this._dispatcher = Gy()
      this.dispatcher = this._dispatcher
      this.coroutineScope = CoroutineScopeKt.plus(CoroutineScopeKt.MainScope(), this._dispatcher as CoroutineContext)
   }

   public abstract val allShips: VsiQueryableShipData<ShipInternal>

   public abstract val loadedShips: VsiQueryableShipData<Any>

   @Deprecated(message = "use loadedShips", replaceWith = @ReplaceWith(expression = "loadedShips", imports = {}))
   public final val shipObjects: Map<Long, Any>
      public final get() {
         return this.loadedShips.getIdToShipData()
      }


   public override fun isChunkInShipyard(chunkX: Int, chunkZ: Int, dimensionId: String): Boolean {
      return this.chunkAllocators.forDimension(dimensionId).isChunkInShipyard(chunkX, chunkZ)
   }

   public override fun isBlockInShipyard(blockX: Int, blockY: Int, blockZ: Int, dimensionId: String): Boolean {
      return this.chunkAllocators.forDimension(dimensionId).isBlockInShipyard(blockX, blockY, blockZ)
   }

   public open fun preTick() {
      try {
         val var1: Gy = this._dispatcher
         CollectionsKt.removeAll(this._dispatcher.a, <unrepresentable>.a)
      } catch (var2: Exception) {
         Companion.logger.error("Error while ticking ships", var2)
      }

      val var3: Int = this.tickNumber++
   }

   public override fun onSetBlock(
      posX: Int,
      posY: Int,
      posZ: Int,
      dimensionId: String,
      oldBlockType: VsiBlockType,
      newBlockType: VsiBlockType,
      oldBlockMass: Double,
      newBlockMass: Double
   ) {
      val var10000: ShipInternal = this.allShips.getByChunkPos(posX shr 4, posZ shr 4, dimensionId)
      if (var10000 != null) {
         var10000.onSetBlock(posX, posY, posZ, newBlockType == this.blockTypes.air, oldBlockMass, newBlockMass, this is ServerShipWorld)
      }
   }

   @Deprecated(message = "redundant", replaceWith = @ReplaceWith(expression = "loadedShips.getShipDataIntersecting(aabb)", imports = []))
   public override fun getShipObjectsIntersecting(aabb: AABBdc): List<Any> {
      return CollectionsKt.toList(this.loadedShips.getIntersecting(aabb))
   }

   public abstract fun destroyWorld() {
   }

   public companion object {
      @JvmStatic
      @JvmSynthetic
      KProperty<Object>[] a;

      private final val logger: Logger
         private final get() {
            val var10000: Logger = Ev.logger$delegate
            return var10000
         }


      @JvmStatic
      fun {
         val var0: Array<KProperty>
         (var0 = arrayOfNulls(1))[0] = Reflection.property1(
            PropertyReference1Impl(Ev.Companion.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
         )
         a = var0
      }
   }
}
