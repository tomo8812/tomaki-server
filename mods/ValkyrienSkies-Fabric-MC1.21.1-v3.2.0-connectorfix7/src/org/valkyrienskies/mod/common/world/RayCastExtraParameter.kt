package org.valkyrienskies.mod.common.world

import net.minecraft.class_243

public data class RayCastExtraParameter(shouldTransformHitPos: Boolean = true,
   skipShip: Long? = null,
   skipWorld: Boolean = false,
   useVanillaClip: Boolean = false,
   _posStart: class_243? = null,
   _posEnd: class_243? = null
) {
   public final var shouldTransformHitPos: Boolean
   public final var skipShip: Long?
   public final var skipWorld: Boolean
   public final var useVanillaClip: Boolean
   public final var _posStart: class_243?
   public final var _posEnd: class_243?

   init {
      this.shouldTransformHitPos = shouldTransformHitPos
      this.skipShip = skipShip
      this.skipWorld = skipWorld
      this.useVanillaClip = useVanillaClip
      this._posStart = _posStart
      this._posEnd = _posEnd
   }

   public operator fun component1(): Boolean {
      return this.shouldTransformHitPos
   }

   public operator fun component2(): Long? {
      return this.skipShip
   }

   public operator fun component3(): Boolean {
      return this.skipWorld
   }

   public operator fun component4(): Boolean {
      return this.useVanillaClip
   }

   public operator fun component5(): class_243? {
      return this._posStart
   }

   public operator fun component6(): class_243? {
      return this._posEnd
   }

   public fun copy(
      shouldTransformHitPos: Boolean = this.shouldTransformHitPos,
      skipShip: Long? = this.skipShip,
      skipWorld: Boolean = this.skipWorld,
      useVanillaClip: Boolean = this.useVanillaClip,
      _posStart: class_243? = this._posStart,
      _posEnd: class_243? = this._posEnd
   ): RayCastExtraParameter {
      return RayCastExtraParameter(shouldTransformHitPos, skipShip, skipWorld, useVanillaClip, _posStart, _posEnd)
   }

   public override fun toString(): String {
      return "RayCastExtraParameter(shouldTransformHitPos=${this.shouldTransformHitPos}, skipShip=${this.skipShip}, skipWorld=${this.skipWorld}, useVanillaClip=${this.useVanillaClip}, _posStart=${this._posStart}, _posEnd=${this._posEnd})"
   }

   public override fun hashCode(): Int {
      return (
               (
                        (
                                 (java.lang.Boolean.hashCode(this.shouldTransformHitPos) * 31 + (if (this.skipShip == null) 0 else this.skipShip.hashCode()))
                                       * 31
                                    + java.lang.Boolean.hashCode(this.skipWorld)
                              )
                              * 31
                           + java.lang.Boolean.hashCode(this.useVanillaClip)
                     )
                     * 31
                  + (if (this._posStart == null) 0 else this._posStart.hashCode())
            )
            * 31
         + (if (this._posEnd == null) 0 else this._posEnd.hashCode())
      }

   public override operator fun equals(other: Any?): Boolean {
      label52@
      if (this === other) {
         return true
      } else {
         return other is RayCastExtraParameter
            && this.shouldTransformHitPos == (other as RayCastExtraParameter).shouldTransformHitPos
            && this.skipShip == (other as RayCastExtraParameter).skipShip
            && this.skipWorld == (other as RayCastExtraParameter).skipWorld
            && this.useVanillaClip == (other as RayCastExtraParameter).useVanillaClip
            && this._posStart == (other as RayCastExtraParameter)._posStart
            && this._posEnd == (other as RayCastExtraParameter)._posEnd
         }
   }

   fun RayCastExtraParameter() {
      this(false, null, false, false, null, null, 63, null)
   }
}
