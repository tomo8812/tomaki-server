package org.valkyrienskies.core.api.ships

import org.joml.Matrix4dc

public data class WingGroupChanges(changedWings: List<PositionedWing>?, changedTransform: Matrix4dc?) {
   public final val changedWings: List<PositionedWing>?
   public final val changedTransform: Matrix4dc?

   init {
      this.changedWings = changedWings
      this.changedTransform = changedTransform
   }

   public operator fun component1(): List<PositionedWing>? {
      return this.changedWings
   }

   public operator fun component2(): Matrix4dc? {
      return this.changedTransform
   }

   public fun copy(changedWings: List<PositionedWing>? = this.changedWings, changedTransform: Matrix4dc? = this.changedTransform): WingGroupChanges {
      return WingGroupChanges(changedWings, changedTransform)
   }

   public override fun toString(): String {
      return "WingGroupChanges(changedWings=${this.changedWings}, changedTransform=${this.changedTransform})"
   }

   public override fun hashCode(): Int {
      return (if (this.changedWings == null) 0 else this.changedWings.hashCode()) * 31
         + (if (this.changedTransform == null) 0 else this.changedTransform.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is WingGroupChanges
            && this.changedWings == (other as WingGroupChanges).changedWings
            && this.changedTransform == (other as WingGroupChanges).changedTransform
         }
   }
}
