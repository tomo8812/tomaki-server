package org.valkyrienskies.core.impl.api_impl.connectivity

import org.joml.Vector3ic
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.events.MergeEvent

@VsBeta
public data class MergeEventImpl(stillPocket: Boolean, oldRootA: Vector3ic, oldRootB: Vector3ic, newRoot: Vector3ic, voxelType: Int) : MergeEvent {
   public open val stillPocket: Boolean
   public open val oldRootA: Vector3ic
   public open val oldRootB: Vector3ic
   public open val newRoot: Vector3ic
   public open val voxelType: Int
   public open var dimensionId: String?

   init {
      this.stillPocket = stillPocket
      this.oldRootA = oldRootA
      this.oldRootB = oldRootB
      this.newRoot = newRoot
      this.voxelType = voxelType
   }

   public override fun toString(): String {
      return "MergeEvent(stillPocket=${this.stillPocket}, oldRootA=${this.oldRootA}, oldRootB=${this.oldRootB}, newRoot=${this.newRoot}, voxelType=${this.voxelType}, dimensionId=${this.dimensionId})"
   }

   public operator fun component1(): Boolean {
      return this.stillPocket
   }

   public operator fun component2(): Vector3ic {
      return this.oldRootA
   }

   public operator fun component3(): Vector3ic {
      return this.oldRootB
   }

   public operator fun component4(): Vector3ic {
      return this.newRoot
   }

   public operator fun component5(): Int {
      return this.voxelType
   }

   public fun copy(
      stillPocket: Boolean = this.stillPocket,
      oldRootA: Vector3ic = this.oldRootA,
      oldRootB: Vector3ic = this.oldRootB,
      newRoot: Vector3ic = this.newRoot,
      voxelType: Int = this.voxelType
   ): MergeEventImpl {
      return MergeEventImpl(stillPocket, oldRootA, oldRootB, newRoot, voxelType)
   }

   public override fun hashCode(): Int {
      return (((java.lang.Boolean.hashCode(this.stillPocket) * 31 + this.oldRootA.hashCode()) * 31 + this.oldRootB.hashCode()) * 31 + this.newRoot.hashCode())
            * 31
         + Integer.hashCode(this.voxelType)
      }

   public override operator fun equals(other: Any?): Boolean {
      label46@
      if (this === other) {
         return true
      } else {
         return other is MergeEventImpl
            && this.stillPocket == (other as MergeEventImpl).stillPocket
            && this.oldRootA == (other as MergeEventImpl).oldRootA
            && this.oldRootB == (other as MergeEventImpl).oldRootB
            && this.newRoot == (other as MergeEventImpl).newRoot
            && this.voxelType == (other as MergeEventImpl).voxelType
         }
   }
}
