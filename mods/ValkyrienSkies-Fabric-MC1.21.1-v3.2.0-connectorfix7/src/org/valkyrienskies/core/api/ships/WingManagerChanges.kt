package org.valkyrienskies.core.api.ships

public data class WingManagerChanges(wingGroupChanges: List<Pair<Int, WingGroupChanges>>?, deletedWingGroups: List<Int>?) {
   public final val wingGroupChanges: List<Pair<Int, WingGroupChanges>>?
   public final val deletedWingGroups: List<Int>?

   init {
      this.wingGroupChanges = wingGroupChanges
      this.deletedWingGroups = deletedWingGroups
   }

   public operator fun component1(): List<Pair<Int, WingGroupChanges>>? {
      return this.wingGroupChanges
   }

   public operator fun component2(): List<Int>? {
      return this.deletedWingGroups
   }

   public fun copy(wingGroupChanges: List<Pair<Int, WingGroupChanges>>? = this.wingGroupChanges, deletedWingGroups: List<Int>? = this.deletedWingGroups): WingManagerChanges {
      return WingManagerChanges(wingGroupChanges, deletedWingGroups)
   }

   public override fun toString(): String {
      return "WingManagerChanges(wingGroupChanges=${this.wingGroupChanges}, deletedWingGroups=${this.deletedWingGroups})"
   }

   public override fun hashCode(): Int {
      return (if (this.wingGroupChanges == null) 0 else this.wingGroupChanges.hashCode()) * 31
         + (if (this.deletedWingGroups == null) 0 else this.deletedWingGroups.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is WingManagerChanges
            && this.wingGroupChanges == (other as WingManagerChanges).wingGroupChanges
            && this.deletedWingGroups == (other as WingManagerChanges).deletedWingGroups
         }
   }
}
