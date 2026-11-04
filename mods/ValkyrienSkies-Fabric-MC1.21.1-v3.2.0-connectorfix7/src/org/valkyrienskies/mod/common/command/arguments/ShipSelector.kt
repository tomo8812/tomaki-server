package org.valkyrienskies.mod.common.command.arguments

import kotlin.jvm.internal.SourceDebugExtension
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.Ship

@SourceDebugExtension(["SMAP\nShipSelecter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipSelecter.kt\norg/valkyrienskies/mod/common/command/arguments/ShipSelector\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,23:1\n1#2:24\n*E\n"])
public data class ShipSelector(slug: String? = null, id: Long? = null, maxAmount: Int = Integer.MAX_VALUE) {
   public final val slug: String?
   public final val id: Long?
   public final val maxAmount: Int

   init {
      this.slug = slug
      this.id = id
      this.maxAmount = maxAmount
   }

   public fun select(queryableShipData: QueryableShipData<Ship>): Set<Ship> {
      var var6: Sequence = CollectionsKt.asSequence(queryableShipData)
      if (this.slug != null) {
         var6 = SequencesKt.filter(var6, { it: Ship ->
            it.slug == `$slug`
         })
      }

      if (this.id != null) {
         var6 = SequencesKt.filter(var6, { it: Ship ->
            it.id == `$id`
         })
      }

      return SequencesKt.toSet(SequencesKt.take(var6, this.maxAmount))
   }

   public operator fun component1(): String? {
      return this.slug
   }

   public operator fun component2(): Long? {
      return this.id
   }

   public operator fun component3(): Int {
      return this.maxAmount
   }

   public fun copy(slug: String? = this.slug, id: Long? = this.id, maxAmount: Int = this.maxAmount): ShipSelector {
      return ShipSelector(slug, id, maxAmount)
   }

   public override fun toString(): String {
      return "ShipSelector(slug=${this.slug}, id=${this.id}, maxAmount=${this.maxAmount})"
   }

   public override fun hashCode(): Int {
      return ((if (this.slug == null) 0 else this.slug.hashCode()) * 31 + (if (this.id == null) 0 else this.id.hashCode())) * 31
         + Integer.hashCode(this.maxAmount)
      }

   public override operator fun equals(other: Any?): Boolean {
      label34@
      if (this === other) {
         return true
      } else {
         return other is ShipSelector
            && this.slug == (other as ShipSelector).slug
            && this.id == (other as ShipSelector).id
            && this.maxAmount == (other as ShipSelector).maxAmount
         }
   }

   fun ShipSelector() {
      this(null, null, 0, 7, null)
   }
}
