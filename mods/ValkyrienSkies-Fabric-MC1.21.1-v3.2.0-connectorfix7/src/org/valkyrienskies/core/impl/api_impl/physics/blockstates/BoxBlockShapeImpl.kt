package org.valkyrienskies.core.impl.api_impl.physics.blockstates

import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.physics.blockstates.BoxBlockShape

public data class BoxBlockShapeImpl(boundingBox: AABBic) : BoxBlockShape {
   public open val boundingBox: AABBic

   init {
      this.boundingBox = boundingBox
      InternalUtilsKt.require4bits("boundingBox", this.boundingBox)
   }

   public operator fun component1(): AABBic {
      return this.boundingBox
   }

   public fun copy(boundingBox: AABBic = this.boundingBox): BoxBlockShapeImpl {
      return BoxBlockShapeImpl(boundingBox)
   }

   public override fun toString(): String {
      return "BoxBlockShapeImpl(boundingBox=${this.boundingBox})"
   }

   public override fun hashCode(): Int {
      return this.boundingBox.hashCode()
   }

   public override operator fun equals(other: Any?): Boolean {
      label22@
      if (this === other) {
         return true
      } else {
         return other is BoxBlockShapeImpl && this.boundingBox == (other as BoxBlockShapeImpl).boundingBox
      }
   }
}
