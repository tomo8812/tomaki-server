package org.valkyrienskies.core.api.world

import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nLevelYRange.kt\nKotlin\n*S Kotlin\n*F\n+ 1 LevelYRange.kt\norg/valkyrienskies/core/api/world/LevelYRange\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,40:1\n1863#2,2:41\n*S KotlinDebug\n*F\n+ 1 LevelYRange.kt\norg/valkyrienskies/core/api/world/LevelYRange\n*L\n35#1:41,2\n*E\n"])
public data class LevelYRange(minY: Int, maxY: Int) {
   public final val minY: Int
   public final val maxY: Int

   init {
      this.minY = minY
      this.maxY = maxY
      if (this.minY % 16 + (16 and ((this.minY % 16 xor 16) and (this.minY % 16 or -(this.minY % 16))) shr 31) != 0) {
         throw IllegalArgumentException("minY must be congruent to 0 mod 16, but minY = ${this.minY}")
      } else if ((this.maxY + 1) % 16 + (16 and (((this.maxY + 1) % 16 xor 16) and ((this.maxY + 1) % 16 or -((this.maxY + 1) % 16))) shr 31) != 0) {
         throw IllegalArgumentException("maxY must be congruent to 15 mod 16, but maxY = ${this.maxY}")
      } else if (this.maxY < this.minY) {
         throw IllegalArgumentException("maxY must not be less than minY, but maxY = ${this.maxY} and minY = ${this.minY}")
      }
   }

   public final val center: Int
      public final get() {
         return (this.minY + this.maxY + 1) / 2
      }


   public final val size: Int
      public final get() {
         return this.maxY - this.minY + 1
      }


   public inline fun forEachYChunkPos(lambda: (Int) -> Unit) {
      val var5: java.util.Iterator = (IntRange(this.minY shr 4, this.maxY shr 4) as java.lang.Iterable).iterator()

      while (var5.hasNext()) {
         lambda((var5 as IntIterator).nextInt())
      }
   }

   public operator fun component1(): Int {
      return this.minY
   }

   public operator fun component2(): Int {
      return this.maxY
   }

   public fun copy(minY: Int = this.minY, maxY: Int = this.maxY): LevelYRange {
      return LevelYRange(minY, maxY)
   }

   public override fun toString(): String {
      return "LevelYRange(minY=${this.minY}, maxY=${this.maxY})"
   }

   public override fun hashCode(): Int {
      return Integer.hashCode(this.minY) * 31 + Integer.hashCode(this.maxY)
   }

   public override operator fun equals(other: Any?): Boolean {
      label28@
      if (this === other) {
         return true
      } else {
         return other is LevelYRange && this.minY == (other as LevelYRange).minY && this.maxY == (other as LevelYRange).maxY
      }
   }
}
