package org.valkyrienskies.core.api.physics.blockstates

import kotlin.jvm.internal.SourceDebugExtension
import org.joml.primitives.AABBic

public interface BoxesBlockShape : SolidBlockShape {
   public val positiveBoxes: Collection<AABBic>

   public val negativeBoxes: Collection<AABBic>

   public val collisionPoints: Collection<CollisionPoint>

   @SourceDebugExtension(["SMAP\nBoxesBlockShape.kt\nKotlin\n*S Kotlin\n*F\n+ 1 BoxesBlockShape.kt\norg/valkyrienskies/core/api/physics/blockstates/BoxesBlockShape$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,89:1\n1#2:90\n1863#3,2:91\n1863#3,2:93\n1863#3,2:95\n*S KotlinDebug\n*F\n+ 1 BoxesBlockShape.kt\norg/valkyrienskies/core/api/physics/blockstates/BoxesBlockShape$Builder\n*L\n46#1:91,2\n63#1:93,2\n82#1:95,2\n*E\n"])
   public interface Builder {
      public abstract fun addPositiveBox(box: AABBic): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
      }

      public open fun addPositiveBoxes(boxes: Iterable<AABBic>): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
         val it: BoxesBlockShape.Builder = this

         for (`element$iv` in boxes) {
            this.addPositiveBox(`element$iv` as AABBic)
         }

         return this
      }

      public abstract fun addNegativeBox(box: AABBic): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
      }

      public open fun addNegativeBoxes(boxes: Iterable<AABBic>): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
         val it: BoxesBlockShape.Builder = this

         for (`element$iv` in boxes) {
            this.addNegativeBox(`element$iv` as AABBic)
         }

         return this
      }

      public abstract fun addCollisionPoint(x: Float, y: Float, z: Float, radius: Float): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
      }

      public abstract fun addCollisionPoint(point: CollisionPoint): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
      }

      public open fun addCollisionPoints(points: Iterable<CollisionPoint>): org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape.Builder {
         val it: BoxesBlockShape.Builder = this

         for (`element$iv` in points) {
            this.addCollisionPoint(`element$iv` as CollisionPoint)
         }

         return this
      }

      public abstract fun build(): BoxesBlockShape {
      }
   }
}
