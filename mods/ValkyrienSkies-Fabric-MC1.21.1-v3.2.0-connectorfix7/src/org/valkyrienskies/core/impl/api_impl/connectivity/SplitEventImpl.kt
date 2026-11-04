package org.valkyrienskies.core.impl.api_impl.connectivity

import org.joml.Vector3ic
import org.valkyrienskies.core.api.VsBeta
import org.valkyrienskies.core.api.events.SplitEvent

@VsBeta
public data class SplitEventImpl(wasPocket: Boolean, oldRoot: Vector3ic, newRootA: Vector3ic, newRootB: Vector3ic, voxelType: Int) : SplitEvent {
   public open val wasPocket: Boolean
   public open val oldRoot: Vector3ic
   public open val newRootA: Vector3ic
   public open val newRootB: Vector3ic
   public open val voxelType: Int
   public open var dimensionId: String?

   init {
      this.wasPocket = wasPocket
      this.oldRoot = oldRoot
      this.newRootA = newRootA
      this.newRootB = newRootB
      this.voxelType = voxelType
   }

   public override fun toString(): String {
      return "SplitEvent(wasPocket=${this.wasPocket}, oldRoot=${this.oldRoot}, newRootA=${this.newRootA}, newRootB=${this.newRootB}, voxelType=${this.voxelType}, dimensionId=${this.dimensionId})"
   }

   public operator fun component1(): Boolean {
      return this.wasPocket
   }

   public operator fun component2(): Vector3ic {
      return this.oldRoot
   }

   public operator fun component3(): Vector3ic {
      return this.newRootA
   }

   public operator fun component4(): Vector3ic {
      return this.newRootB
   }

   public operator fun component5(): Int {
      return this.voxelType
   }

   public fun copy(
      wasPocket: Boolean = this.wasPocket,
      oldRoot: Vector3ic = this.oldRoot,
      newRootA: Vector3ic = this.newRootA,
      newRootB: Vector3ic = this.newRootB,
      voxelType: Int = this.voxelType
   ): SplitEventImpl {
      return SplitEventImpl(wasPocket, oldRoot, newRootA, newRootB, voxelType)
   }

   public override fun hashCode(): Int {
      return (((java.lang.Boolean.hashCode(this.wasPocket) * 31 + this.oldRoot.hashCode()) * 31 + this.newRootA.hashCode()) * 31 + this.newRootB.hashCode())
            * 31
         + Integer.hashCode(this.voxelType)
      }

   public override operator fun equals(other: Any?): Boolean {
      label46@
      if (this === other) {
         return true
      } else {
         return other is SplitEventImpl
            && this.wasPocket == (other as SplitEventImpl).wasPocket
            && this.oldRoot == (other as SplitEventImpl).oldRoot
            && this.newRootA == (other as SplitEventImpl).newRootA
            && this.newRootB == (other as SplitEventImpl).newRootB
            && this.voxelType == (other as SplitEventImpl).voxelType
         }
   }
}
