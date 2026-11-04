package org.valkyrienskies.core.impl.shadow

import java.util.ArrayList
import javax.inject.Inject
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import org.joml.primitives.AABBi
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.physics.blockstates.BoxesBlockShape
import org.valkyrienskies.core.api.physics.blockstates.CollisionPoint
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.core.internal.collision.VsiSolidShapeUtils

@dY
public class Do @Inject  public constructor(p0: dT<VsiCore>) : VsiSolidShapeUtils {
   @JvmStatic
   @JvmSynthetic
   private KProperty<Object>[] a;

   private final val c: VsiCore
      private final get() {
         return FZ.a(this.b, a[0])
      }


   private final val a: Int
   private final val b: Int

   init {
      this.b = var1
      this.c = 10
      this.d = 20
   }

   public override fun mergeBoxes(p0: MutableList<AABBic>): MutableList<AABBic> {
      if (boxes.size() < 2) {
         return boxes
      } else {
         var var2: Boolean = false

         label110@ while (!var2) {
            var var3: Int = 0

            for (var4 in boxes.size()..var3) {
               var var5: Int = var3 + 1

               for (var6 in boxes.size()..var5) {
                  var var10000: AABBic = boxes.get(var3) as AABBic
                  val var8: AABBic = boxes.get(var5) as AABBic
                  var10000 = if (var10000.minY() == var8.minY()
                        && var10000.maxY() == var8.maxY()
                        && var10000.minZ() == var8.minZ()
                        && var10000.maxZ() == var8.maxZ()
                        && var10000.minX() <= var8.maxX() + 1
                        && var10000.maxX() + 1 >= var8.minX())
                     AABBi(
                        Math.min(var10000.minX(), var8.minX()),
                        var10000.minY(),
                        var10000.minZ(),
                        Math.max(var10000.maxX(), var8.maxX()),
                        var10000.maxY(),
                        var10000.maxZ()
                     )
                     else
                     (
                        if (var10000.minX() == var8.minX()
                              && var10000.maxX() == var8.maxX()
                              && var10000.minZ() == var8.minZ()
                              && var10000.maxZ() == var8.maxZ()
                              && var10000.minY() <= var8.maxY() + 1
                              && var10000.maxY() + 1 >= var8.minY())
                           AABBi(
                              var10000.minX(),
                              Math.min(var10000.minY(), var8.minY()),
                              var10000.minZ(),
                              var10000.maxX(),
                              Math.max(var10000.maxY(), var8.maxY()),
                              var10000.maxZ()
                           )
                           else
                           (
                              if (var10000.minX() == var8.minX()
                                    && var10000.maxX() == var8.maxX()
                                    && var10000.minY() == var8.minY()
                                    && var10000.maxY() == var8.maxY()
                                    && var10000.minZ() <= var8.maxZ() + 1
                                    && var10000.maxZ() + 1 >= var8.minZ())
                                 AABBi(
                                    var10000.minX(),
                                    var10000.minY(),
                                    Math.min(var10000.minZ(), var8.minZ()),
                                    var10000.maxX(),
                                    var10000.maxY(),
                                    Math.max(var10000.maxZ(), var8.maxZ())
                                 )
                                 else
                                 null
                           )
                     )
                     if (var10000 != null) {
                     boxes.remove(var5)
                     boxes.remove(var3)
                     boxes.add(var10000)
                     continue@label110
                  }
               }
            }

            var2 = true
         }

         return boxes
      }
   }

   public override fun cutBoxes(p0: MutableList<AABBic>, p1: AABBic): MutableList<AABBic> {
      val var3: AABBi = AABBi()
      val var4: AABBi = AABBi()
      if (boxes.isEmpty()) {
         return boxes
      } else {
         var var5: Boolean = false

         label74@ while (!var5) {
            var var6: Int = 0

            for (var7 in boxes.size()..var6) {
               val var8: AABBic = boxes.get(var6) as AABBic
               var3.setMin(var8.minX(), var8.minY(), var8.minZ())
               var3.setMax(var8.maxX(), var8.maxY(), var8.maxZ())
               var4.setMin(cut.minX(), cut.minY(), cut.minZ())
               var4.setMax(cut.maxX(), cut.maxY(), cut.maxZ())
               if (var3.intersectsAABB(var4)) {
                  boxes.remove(var6)
                  if (var8.minX() < cut.minX()) {
                     boxes.add(AABBi(var8.minX(), var8.minY(), var8.minZ(), cut.minX() - 1, var8.maxY(), var8.maxZ()))
                  }

                  if (var8.maxX() > cut.maxX()) {
                     boxes.add(AABBi(cut.maxX() + 1, var8.minY(), var8.minZ(), var8.maxX(), var8.maxY(), var8.maxZ()))
                  }

                  if (var8.minY() < cut.minY()) {
                     boxes.add(AABBi(cut.minX(), var8.minY(), var8.minZ(), cut.maxX(), cut.minY() - 1, var8.maxZ()))
                  }

                  if (var8.maxY() > cut.maxY()) {
                     boxes.add(AABBi(cut.minX(), cut.maxY() + 1, var8.minZ(), cut.maxX(), var8.maxY(), var8.maxZ()))
                  }

                  if (var8.minZ() < cut.minZ()) {
                     boxes.add(AABBi(cut.minX(), cut.minY(), var8.minZ(), cut.maxX(), cut.maxY(), cut.minZ() - 1))
                  }

                  if (var8.maxZ() > cut.maxZ()) {
                     boxes.add(AABBi(cut.minX(), cut.minY(), cut.maxZ() + 1, cut.maxX(), cut.maxY(), var8.maxZ()))
                  }
                  continue@label74
               }
            }

            var5 = true
         }

         return boxes
      }
   }

   public override fun generateCollisionPointsForBoxes(p0: List<AABBic>): List<CollisionPoint> {
      val var2: ArrayList = ArrayList()
      val var3: java.util.Iterator = boxes.iterator()

      while (var3.hasNext()) {
         val var4: AABBic
         val var5: Int = (var4 = var3.next() as AABBic).maxX() + 1 - var4.minX()
         val var6: Int = var4.maxY() + 1 - var4.minY()
         val var7: Int = var4.maxZ() + 1 - var4.minZ()
         if (var5 shl 1 <= var6 && var5 shl 1 <= var7) {
            val var20: Float = var5 / 2.0F
            val var27: Float = (var4.maxX() + 1 + var4.minX()) / 2.0F
            val var34: Float = var4.minY() + var20
            val var37: Float = var4.minZ() + var20
            val var39: Float = var4.maxY() + 1 - var20
            val var12: Float = var4.maxZ() + 1 - var20
            var2.add(CollisionPoint(var27 / 16.0F, var34 / 16.0F, var37 / 16.0F, var20 / 16.0F))
            var2.add(CollisionPoint(var27 / 16.0F, var34 / 16.0F, var12 / 16.0F, var20 / 16.0F))
            var2.add(CollisionPoint(var27 / 16.0F, var39 / 16.0F, var37 / 16.0F, var20 / 16.0F))
            var2.add(CollisionPoint(var27 / 16.0F, var39 / 16.0F, var12 / 16.0F, var20 / 16.0F))
         } else if (var6 shl 1 <= var5 && var6 shl 1 <= var7) {
            val var19: Float = var6 / 2.0F
            val var26: Float = (var4.maxY() + 1 + var4.minY()) / 2.0F
            val var33: Float = var4.minX() + var19
            val var36: Float = var4.minZ() + var19
            val var38: Float = var4.maxX() + 1 - var19
            val var11: Float = var4.maxZ() + 1 - var19
            var2.add(CollisionPoint(var33 / 16.0F, var26 / 16.0F, var36 / 16.0F, var19 / 16.0F))
            var2.add(CollisionPoint(var33 / 16.0F, var26 / 16.0F, var11 / 16.0F, var19 / 16.0F))
            var2.add(CollisionPoint(var38 / 16.0F, var26 / 16.0F, var36 / 16.0F, var19 / 16.0F))
            var2.add(CollisionPoint(var38 / 16.0F, var26 / 16.0F, var11 / 16.0F, var19 / 16.0F))
         } else if (var7 shl 1 <= var5 && var7 shl 1 <= var6) {
            val var18: Float = var7 / 2.0F
            val var25: Float = (var4.maxZ() + 1 + var4.minZ()) / 2.0F
            val var32: Float = var4.minX() + var18
            val var35: Float = var4.minY() + var18
            val var9: Float = var4.maxX() + 1 - var18
            val var10: Float = var4.maxY() + 1 - var18
            var2.add(CollisionPoint(var32 / 16.0F, var35 / 16.0F, var25 / 16.0F, var18 / 16.0F))
            var2.add(CollisionPoint(var32 / 16.0F, var10 / 16.0F, var25 / 16.0F, var18 / 16.0F))
            var2.add(CollisionPoint(var9 / 16.0F, var35 / 16.0F, var25 / 16.0F, var18 / 16.0F))
            var2.add(CollisionPoint(var9 / 16.0F, var10 / 16.0F, var25 / 16.0F, var18 / 16.0F))
         } else if (var5 >= 2 * var6 && var5 >= 2 * var7) {
            val var17: Float = Math.min(var6, var7) / 2.0F
            val var24: Float = (var4.maxY() + 1 + var4.minY()) / 2.0F
            val var31: Float = (var4.maxZ() + 1 + var4.minZ()) / 2.0F
            var2.add(CollisionPoint(((float)var4.minX() + var17) / 16.0F, var24 / 16.0F, var31 / 16.0F, var17 / 16.0F))
            var2.add(CollisionPoint((float)(var4.maxX() + 1 + var4.minX()) / 2.0F / 16.0F, var24 / 16.0F, var31 / 16.0F, var17 / 16.0F))
            var2.add(CollisionPoint(((float)(var4.maxX() + 1) - var17) / 16.0F, var24 / 16.0F, var31 / 16.0F, var17 / 16.0F))
         } else if (var6 >= 2 * var5 && var6 >= 2 * var7) {
            val var16: Float = Math.min(var5, var7) / 2.0F
            val var23: Float = (var4.maxX() + 1 + var4.minX()) / 2.0F
            val var30: Float = (var4.maxZ() + 1 + var4.minZ()) / 2.0F
            var2.add(CollisionPoint(var23 / 16.0F, ((float)var4.minY() + var16) / 16.0F, var30 / 16.0F, var16 / 16.0F))
            var2.add(CollisionPoint(var23 / 16.0F, (float)(var4.maxY() + 1 + var4.minY()) / 2.0F / 16.0F, var30 / 16.0F, var16 / 16.0F))
            var2.add(CollisionPoint(var23 / 16.0F, ((float)(var4.maxY() + 1) - var16) / 16.0F, var30 / 16.0F, var16 / 16.0F))
         } else if (var7 >= 2 * var5 && var7 >= 2 * var6) {
            val var15: Float = Math.min(var5, var6) / 2.0F
            val var22: Float = (var4.maxX() + 1 + var4.minX()) / 2.0F
            val var29: Float = (var4.maxY() + 1 + var4.minY()) / 2.0F
            var2.add(CollisionPoint(var22 / 16.0F, var29 / 16.0F, ((float)var4.minZ() + var15) / 16.0F, var15 / 16.0F))
            var2.add(CollisionPoint(var22 / 16.0F, var29 / 16.0F, (float)(var4.maxZ() + 1 + var4.minZ()) / 2.0F / 16.0F, var15 / 16.0F))
            var2.add(CollisionPoint(var22 / 16.0F, var29 / 16.0F, ((float)(var4.maxZ() + 1) - var15) / 16.0F, var15 / 16.0F))
         } else if (boxes.size() == 1) {
            val var13: Float = Math.min(var5, Math.min(var6, var7)) / 4.0F
            var2.add(
               CollisionPoint(((float)var4.minX() + var13) / 16.0F, ((float)var4.minY() + var13) / 16.0F, ((float)var4.minZ() + var13) / 16.0F, var13 / 16.0F)
            )
            var2.add(
               CollisionPoint(
                  ((float)var4.minX() + var13) / 16.0F, ((float)var4.minY() + var13) / 16.0F, ((float)var4.maxZ() + 1.0F - var13) / 16.0F, var13 / 16.0F
               )
            )
            var2.add(
               CollisionPoint(
                  ((float)var4.minX() + var13) / 16.0F, ((float)var4.maxY() + 1.0F - var13) / 16.0F, ((float)var4.minZ() + var13) / 16.0F, var13 / 16.0F
               )
            )
            var2.add(
               CollisionPoint(
                  ((float)var4.minX() + var13) / 16.0F, ((float)var4.maxY() + 1.0F - var13) / 16.0F, ((float)var4.maxZ() + 1.0F - var13) / 16.0F, var13 / 16.0F
               )
            )
            var2.add(
               CollisionPoint(
                  ((float)var4.maxX() + 1.0F - var13) / 16.0F, ((float)var4.minY() + var13) / 16.0F, ((float)var4.minZ() + var13) / 16.0F, var13 / 16.0F
               )
            )
            var2.add(
               CollisionPoint(
                  ((float)var4.maxX() + 1.0F - var13) / 16.0F, ((float)var4.minY() + var13) / 16.0F, ((float)var4.maxZ() + 1.0F - var13) / 16.0F, var13 / 16.0F
               )
            )
            var2.add(
               CollisionPoint(
                  ((float)var4.maxX() + 1.0F - var13) / 16.0F, ((float)var4.maxY() + 1.0F - var13) / 16.0F, ((float)var4.minZ() + var13) / 16.0F, var13 / 16.0F
               )
            )
            var2.add(
               CollisionPoint(
                  ((float)var4.maxX() + 1.0F - var13) / 16.0F,
                  ((float)var4.maxY() + 1.0F - var13) / 16.0F,
                  ((float)var4.maxZ() + 1.0F - var13) / 16.0F,
                  var13 / 16.0F
               )
            )
         } else {
            var2.add(
               CollisionPoint(
                  (float)(var4.maxX() + 1 + var4.minX()) / 2.0F / 16.0F,
                  (float)(var4.maxY() + 1 + var4.minY()) / 2.0F / 16.0F,
                  (float)(var4.maxZ() + 1 + var4.minZ()) / 2.0F / 16.0F,
                  (float)Math.min(var5, Math.min(var6, var7)) / 2.0F / 16.0F
               )
            )
         }
      }

      return var2
   }

   public override fun generateShapeFromBoxes(p0: MutableList<AABBic>): BoxesBlockShape? {
      val var2: AABBi = AABBi(0, 0, 0, 15, 15, 15)
      if (posBoxes.isEmpty()) {
         return this.c.newSolidStateBoxesShapeBuilder().addNegativeBox(var2).build()
      } else {
         this.mergeBoxes(posBoxes)
         if (posBoxes.size() > this.c) {
            return null
         } else {
            val var3: Array<AABBic>
            (var3 = arrayOfNulls(1))[0] = var2
            var var5: java.util.List = CollectionsKt.mutableListOf(var3)

            for (var4 in posBoxes) {
               var5 = this.cutBoxes(var5, var4)
            }

            this.mergeBoxes(var5)
            if (var5.size() > this.c) {
               return null
            } else {
               val var7: java.util.List
               return if ((var7 = this.generateCollisionPointsForBoxes(posBoxes)).size() > this.d)
                  null
                  else
                  this.c.newSolidStateBoxesShapeBuilder().addCollisionPoints(var7).addNegativeBoxes(var5).addPositiveBoxes(posBoxes).build()
               }
         }
      }
   }

   @JvmStatic
   fun a(var0: AABBic, var1: AABBic): AABBi {
      label52@
      if (var0.minY() == var1.minY()
         && var0.maxY() == var1.maxY()
         && var0.minZ() == var1.minZ()
         && var0.maxZ() == var1.maxZ()
         && var0.minX() <= var1.maxX() + 1
         && var0.maxX() + 1 >= var1.minX()) {
         AABBi(Math.min(var0.minX(), var1.minX()), var0.minY(), var0.minZ(), Math.max(var0.maxX(), var1.maxX()), var0.maxY(), var0.maxZ())
      } else if (var0.minX() == var1.minX()
         && var0.maxX() == var1.maxX()
         && var0.minZ() == var1.minZ()
         && var0.maxZ() == var1.maxZ()
         && var0.minY() <= var1.maxY() + 1
         && var0.maxY() + 1 >= var1.minY()) {
         AABBi(var0.minX(), Math.min(var0.minY(), var1.minY()), var0.minZ(), var0.maxX(), Math.max(var0.maxY(), var1.maxY()), var0.maxZ())
      } else {
         if (var0.minX() == var1.minX()
               && var0.maxX() == var1.maxX()
               && var0.minY() == var1.minY()
               && var0.maxY() == var1.maxY()
               && var0.minZ() <= var1.maxZ() + 1
               && var0.maxZ() + 1 >= var1.minZ())
            AABBi(var0.minX(), var0.minY(), Math.min(var0.minZ(), var1.minZ()), var0.maxX(), var0.maxY(), Math.max(var0.maxZ(), var1.maxZ()))
            else
            null
         }
   }

   @JvmStatic
   fun a(var0: AABBi, var1: AABBi, var2: AABBic, var3: AABBic): Boolean {
      var0.setMin(var2.minX(), var2.minY(), var2.minZ())
      var0.setMax(var2.maxX(), var2.maxY(), var2.maxZ())
      var1.setMin(var3.minX(), var3.minY(), var3.minZ())
      var1.setMax(var3.maxX(), var3.maxY(), var3.maxZ())
      var0.intersectsAABB(var1)
   }

   @JvmStatic
   fun a(var0: AABBic, var1: AABBic, var2: MutableList<AABBic>) {
      if (var0.minX() < var1.minX()) {
         var2.add(AABBi(var0.minX(), var0.minY(), var0.minZ(), var1.minX() - 1, var0.maxY(), var0.maxZ()))
      }

      if (var0.maxX() > var1.maxX()) {
         var2.add(AABBi(var1.maxX() + 1, var0.minY(), var0.minZ(), var0.maxX(), var0.maxY(), var0.maxZ()))
      }

      if (var0.minY() < var1.minY()) {
         var2.add(AABBi(var1.minX(), var0.minY(), var0.minZ(), var1.maxX(), var1.minY() - 1, var0.maxZ()))
      }

      if (var0.maxY() > var1.maxY()) {
         var2.add(AABBi(var1.minX(), var1.maxY() + 1, var0.minZ(), var1.maxX(), var0.maxY(), var0.maxZ()))
      }

      if (var0.minZ() < var1.minZ()) {
         var2.add(AABBi(var1.minX(), var1.minY(), var0.minZ(), var1.maxX(), var1.maxY(), var1.minZ() - 1))
      }

      if (var0.maxZ() > var1.maxZ()) {
         var2.add(AABBi(var1.minX(), var1.minY(), var1.maxZ() + 1, var1.maxX(), var1.maxY(), var0.maxZ()))
      }
   }

   @JvmStatic
   fun {
      val var0: Array<KProperty>
      (var0 = arrayOfNulls(1))[0] = Reflection.property1(
         PropertyReference1Impl(Do.class, "vsCore", "getVsCore()Lorg/valkyrienskies/core/internal/VsiCore;", 0) as PropertyReference1
      )
      a = var0
   }
}
