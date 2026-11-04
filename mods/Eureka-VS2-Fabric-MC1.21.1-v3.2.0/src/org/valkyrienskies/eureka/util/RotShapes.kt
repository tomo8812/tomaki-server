package org.valkyrienskies.eureka.util

import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_259
import net.minecraft.class_265

public object RotShapes {
   public fun box(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): RotShape {
      return RotShapes.Box(x1, y1, z1, x2, y2, z2)
   }

   public fun or(vararg shapes: RotShape): RotShape {
      return RotShapes.Union(ArraysKt.asList(shapes))
   }

   private class Box(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) : RotShape {
      public final val x1: Double
      public final val y1: Double
      public final val z1: Double
      public final val x2: Double
      public final val y2: Double
      public final val z2: Double

      init {
         this.x1 = x1
         this.y1 = y1
         this.z1 = z1
         this.x2 = x2
         this.y2 = y2
         this.z2 = z2
      }

      public override fun rotate90(): RotShape {
         return RotShapes.Box(16 - this.z1, this.y1, this.x1, 16 - this.z2, this.y2, this.x2)
      }

      public override fun makeMcShape(): class_265 {
         val var10000: class_265 = class_259.method_1081(
            Math.min(this.x1, this.x2) / (double)16,
            Math.min(this.y1, this.y2) / (double)16,
            Math.min(this.z1, this.z2) / (double)16,
            Math.max(this.x1, this.x2) / (double)16,
            Math.max(this.y1, this.y2) / (double)16,
            Math.max(this.z1, this.z2) / (double)16
         )
         return var10000
      }

      override fun rotate180(): RotShape {
         RotShape.DefaultImpls.rotate180(this)
      }

      override fun rotate270(): RotShape {
         RotShape.DefaultImpls.rotate270(this)
      }

      override fun build(): class_265 {
         RotShape.DefaultImpls.build(this)
      }
   }

   @SourceDebugExtension(["SMAP\nRotShapes.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RotShapes.kt\norg/valkyrienskies/eureka/util/RotShapes$Union\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,58:1\n1557#2:59\n1628#2,3:60\n1797#2,3:63\n*S KotlinDebug\n*F\n+ 1 RotShapes.kt\norg/valkyrienskies/eureka/util/RotShapes$Union\n*L\n53#1:59\n53#1:60,3\n55#1:63,3\n*E\n"])
   private class Union(shapes: List<RotShape>) : RotShape {
      public final val shapes: List<RotShape>

      init {
         this.shapes = shapes
      }

      public override fun rotate90(): RotShape {
         val `$this$mapTo$iv$iv`: java.lang.Iterable = this.shapes
         val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(this.shapes, 10))

         for (`item$iv$iv` in `$this$mapTo$iv$iv`) {
            `destination$iv$iv`.add((`item$iv$iv` as RotShape).rotate90())
         }

         return RotShapes.Union(`destination$iv$iv` as MutableList<RotShape>)
      }

      public override fun makeMcShape(): class_265 {
         val `$this$fold$iv`: java.lang.Iterable = this.shapes
         var `accumulator$iv`: Any = class_259.method_1073()

         for (`element$iv` in `$this$fold$iv`) {
            `accumulator$iv` = class_259.method_1084((class_265)`accumulator$iv`, (`element$iv` as RotShape).makeMcShape())
         }

         return (class_265)`accumulator$iv`
      }

      override fun rotate180(): RotShape {
         RotShape.DefaultImpls.rotate180(this)
      }

      override fun rotate270(): RotShape {
         RotShape.DefaultImpls.rotate270(this)
      }

      override fun build(): class_265 {
         RotShape.DefaultImpls.build(this)
      }
   }
}
