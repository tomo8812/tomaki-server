package org.valkyrienskies.core.impl.api_impl.physics.blockstates

import java.util.ArrayList
import javax.inject.Inject
import kotlin.jvm.internal.DefaultConstructorMarker
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.apache.logging.log4j.Logger
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape
import org.valkyrienskies.core.api.physics.blockstates.CollisionPoint

public class BoxesBlockShapeImpl private constructor(boundingBox: AABBic,
      positiveBoxes: Collection<AABBic>,
      negativeBoxes: Collection<AABBic>,
      collisionPoints: Collection<CollisionPoint>
   ) :
   BoxesBlockShape {
   public open val boundingBox: AABBic
   public open val positiveBoxes: Collection<AABBic>
   public open val negativeBoxes: Collection<AABBic>
   public open val collisionPoints: Collection<CollisionPoint>
   private final val hashCode: Int

   init {
      this.boundingBox = boundingBox
      this.positiveBoxes = positiveBoxes
      this.negativeBoxes = negativeBoxes
      this.collisionPoints = collisionPoints
      this.hashCode = (
               ((this as BoxesBlockShapeImpl).boundingBox.hashCode() * 31 + (this as BoxesBlockShapeImpl).positiveBoxes.hashCode()) * 31
                  + (this as BoxesBlockShapeImpl).negativeBoxes.hashCode()
            )
            * 31
         + (this as BoxesBlockShapeImpl).collisionPoints.hashCode()
      }

   public override operator fun equals(other: Any?): Boolean {
      return this === other
         || other is BoxesBlockShapeImpl
            && this.boundingBox == (other as BoxesBlockShapeImpl).boundingBox
            && this.positiveBoxes == (other as BoxesBlockShapeImpl).positiveBoxes
            && this.negativeBoxes == (other as BoxesBlockShapeImpl).negativeBoxes
            && this.collisionPoints == (other as BoxesBlockShapeImpl).collisionPoints
         }

   public override fun hashCode(): Int {
      return this.hashCode
   }

   @JvmSynthetic
   fun BoxesBlockShapeImpl(
      boundingBox: AABBic,
      positiveBoxes: java.util.Collection,
      negativeBoxes: java.util.Collection,
      collisionPoints: java.util.Collection,
      `$constructor_marker`: DefaultConstructorMarker
   ) {
      this(boundingBox, positiveBoxes, negativeBoxes, collisionPoints)
   }

   public class BuilderImpl @Inject  public constructor() : BoxesBlockShape.Builder {
      private final val positiveBoxes: MutableList<AABBic> = ArrayList() as java.util.List
      private final val negativeBoxes: MutableList<AABBic> = ArrayList() as java.util.List
      private final val collisionPoints: MutableList<CollisionPoint> = ArrayList() as java.util.List

      public open fun addPositiveBox(box: AABBic): org.valkyrienskies.core.impl.api_impl.physics.blockstates.BoxesBlockShapeImpl.BuilderImpl {
         InternalUtilsKt.require4bits("box", box)
         this.positiveBoxes.add(box)
         return this
      }

      public open fun addNegativeBox(box: AABBic): org.valkyrienskies.core.impl.api_impl.physics.blockstates.BoxesBlockShapeImpl.BuilderImpl {
         InternalUtilsKt.require4bits("box", box)
         this.negativeBoxes.add(box)
         return this
      }

      public open fun addCollisionPoint(point: CollisionPoint): org.valkyrienskies.core.impl.api_impl.physics.blockstates.BoxesBlockShapeImpl.BuilderImpl {
         this.collisionPoints.add(point)
         return this
      }

      public open fun addCollisionPoint(x: Float, y: Float, z: Float, radius: Float): org.valkyrienskies.core.impl.api_impl.physics.blockstates.BoxesBlockShapeImpl.BuilderImpl {
         this.collisionPoints.add(CollisionPoint(x, y, z, radius))
         return this
      }

      public open fun build(): BoxesBlockShapeImpl {
         if (this.positiveBoxes.isEmpty() != this.collisionPoints.isEmpty()) {
            throw IllegalArgumentException("there are positive boxes but no collision points, or vice versa for $this")
         } else {
            for (var2 in this.positiveBoxes) {
               val var4: java.util.Iterator = this.negativeBoxes.iterator()

               var var10000: Any
               while (true) {
                  if (!var4.hasNext()) {
                     var10000 = null
                     break
                  }

                  val var5: Any
                  if (var2.intersectsAABB((var5 = var4.next()) as AABBic)) {
                     var10000 = var5
                     break
                  }
               }

               if (var10000 as AABBic != null) {
               }
            }

            val var7: java.lang.Iterable = this.positiveBoxes
            var var9: AABBi = AABBi(0, 0, 0, 0, 0, 0)

            for (var12 in var7) {
               val var14: AABBi = var9.union(var12 as AABBic)
               var9 = var14
            }

            return BoxesBlockShapeImpl(var9, this.positiveBoxes, this.negativeBoxes, CollectionsKt.toList(this.collisionPoints), null)
         }
      }

      public override fun toString(): String {
         return "BoxesBlockShape.Builder(positiveBoxes=${this.positiveBoxes}, negativeBoxes=${this.negativeBoxes}, collisionPoints=${this.collisionPoints})"
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun addPositiveBox(box: AABBic): BoxesBlockShape.Builder {
         this.addPositiveBox(box) as BoxesBlockShape.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun addNegativeBox(box: AABBic): BoxesBlockShape.Builder {
         this.addNegativeBox(box) as BoxesBlockShape.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun addCollisionPoint(point: CollisionPoint): BoxesBlockShape.Builder {
         this.addCollisionPoint(point) as BoxesBlockShape.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun addCollisionPoint(x: Float, y: Float, z: Float, radius: Float): BoxesBlockShape.Builder {
         this.addCollisionPoint(x, y, z, radius) as BoxesBlockShape.Builder
      }

      // $VF: bridge methodSupplier
      @JvmSynthetic
      override final fun build(): BoxesBlockShape {
         this.build() as BoxesBlockShape
      }

      public companion object {
         @JvmStatic
         @JvmSynthetic
         KProperty<Object>[] $$delegatedProperties;

         public final val logger: Logger
            public final get() {
               val var10000: Logger = BoxesBlockShapeImpl.BuilderImpl.logger$delegate
               return var10000
            }


         @JvmStatic
         fun {
            val var0: Array<KProperty>
            (var0 = arrayOfNulls(1))[0] = Reflection.property1(
               PropertyReference1Impl(BoxesBlockShapeImpl.BuilderImpl.Companion.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1
            )
            $$delegatedProperties = var0
         }
      }
   }
}
