package org.valkyrienskies.core.api.ships

import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.markers.KMappedMarker
import org.joml.primitives.AABBdc

@SourceDebugExtension(["SMAP\nQueryableShipData.kt\nKotlin\n*S Kotlin\n*F\n+ 1 QueryableShipData.kt\norg/valkyrienskies/core/api/ships/QueryableShipData\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,66:1\n1734#2,3:67\n*S KotlinDebug\n*F\n+ 1 QueryableShipData.kt\norg/valkyrienskies/core/api/ships/QueryableShipData\n*L\n48#1:67,3\n*E\n"])
public interface QueryableShipData<ShipType extends Ship> : java.util.Collection<ShipType>, KMappedMarker {
   public abstract fun getById(shipId: Long): Any? {
   }

   public abstract fun getByChunkPos(chunkX: Int, chunkZ: Int, dimensionId: String): Any? {
   }

   public abstract fun getIntersecting(aabb: AABBdc, dimensionId: String): Iterable<Any> {
   }

   @Deprecated(message = "This gets all ships in the AABB, irrespective of dimension. This is pretty much never what you want.", replaceWith = @ReplaceWith(expression = "getIntersecting(aabb, dimensionId)", imports = []))
   public abstract fun getIntersecting(aabb: AABBdc): Iterable<Any> {
   }

   public open fun contains(shipId: Long): Boolean {
      return this.getById(shipId) != null
   }

   public open operator fun contains(element: Any): Boolean {
      return this.contains(element.id)
   }

   public override fun containsAll(elements: Collection<Any>): Boolean {
      val `$this$all$iv`: java.lang.Iterable = elements
      var var10000: Boolean
      if ((elements as java.util.Collection).isEmpty()) {
         var10000 = true
      } else {
         val var4: java.util.Iterator = `$this$all$iv`.iterator()

         while (true) {
            if (!var4.hasNext()) {
               var10000 = true
               break
            }

            if (!this.contains((Object)(var4.next() as Ship))) {
               var10000 = false
               break
            }
         }
      }

      return var10000
   }

   public override fun isEmpty(): Boolean {
      return this.size() == 0
   }

   @Deprecated(message = "This gets all ships in the AABB, irrespective of dimension. This is pretty much never what you want.", replaceWith = @ReplaceWith(expression = "getIntersecting(aabb, dimensionId)", imports = []))
   public open fun getShipDataIntersecting(aabb: AABBdc): Iterable<Any> {
      return this.getIntersecting(aabb)
   }

   @Deprecated(message = "renamed", replaceWith = @ReplaceWith(expression = "getByChunkPos(chunkX, chunkZ, dimensionId)", imports = []))
   public open fun getShipDataFromChunkPos(chunkX: Int, chunkZ: Int, dimensionId: String): Any? {
      return this.getByChunkPos(chunkX, chunkZ, dimensionId)
   }

   @Deprecated(message = "Use the specific functions instead, such as #getById or #iterator")
   public val idToShipData: Map<Long, Any>

   // $VF: bridge methodSupplier
   @JvmDefault
   override fun contains(element: Any): Boolean {
      element is Ship && this.contains((ShipType)element)
   }
}
