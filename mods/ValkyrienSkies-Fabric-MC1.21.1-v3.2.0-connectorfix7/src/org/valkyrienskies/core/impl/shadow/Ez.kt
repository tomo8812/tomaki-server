package org.valkyrienskies.core.impl.shadow

import it.unimi.dsi.fastutil.objects.ObjectCollection
import it.unimi.dsi.fastutil.objects.ObjectIterator
import org.joml.Matrix3d
import org.joml.Matrix3dc
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.Wing
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.api.world.PhysLevel
import org.valkyrienskies.core.util.datastructures.BlockPos2ObjectOpenHashMap

public object Ez {
   @JvmStatic
   public fun a(p0: PhysLevel, p1: ShipTransform, p2: Hg, p3: Ey, p4: Matrix3dc): Pair<Vector3dc, Vector3dc> {
      val var5: Vector3d = Vector3d()
      val var6: Vector3d = Vector3d()
      val var7: Vector3dc = var2.d
      val var48: Vector3dc = var2.e
      val var49: ObjectIterator = (Ey.a(var3).values() as ObjectCollection).iterator()

      while (var49.hasNext()) {
         val var8: Ex
         val var9: BlockPos2ObjectOpenHashMap
         if ((var9 = (var8 = var49.next() as Ex).a).containsNullKey) {
            val var10000: Int = var9.keys[var9.n * 3]
            val var10001: Int = var9.keys[var9.n * 3 + 1]
            val var10002: Int = var9.keys[var9.n * 3 + 2]
            val var10: Wing = var9.values[var9.n] as Wing
            val var50: Boolean = var8.e
            val var15: Matrix4dc = var8.c
            val var92: Vector3d = var8.c.transformDirection(Vector3d(var10.wingNormal))
            val var16: Vector3dc = var92
            val var93: Vector3d = var15.transformPosition(Vector3d((double)var10000 + 0.5, (double)var10001 + 0.5, (double)var10002 + 0.5))
            var var63: Vector3dc = var93
            val var94: Vector3d = var1.shipToWorld.transformPosition(var93, Vector3d()).sub(var1.positionInWorld)
            val var72: Vector3dc = var94
            val var95: Vector3d = Vector3d(var48).cross(var94).add(var7)
            val var76: Vector3dc = var95
            val var96: Vector3d = var1.shipToWorld.transformDirection(var16, Vector3d())
            val var79: Vector3dc = var96
            val var97: Vector3d = var76.sub(Vector3d(var96).mul(var96.dot(var76)), Vector3d())
            if ((var16 = var97).lengthSquared() > 1.0E-12) {
               val var98: Vector3d = Vector3d(var16).normalize()
               val var32: Double = var98.angle(var76) * -Math.signum(var79.dot(var76)) + var10.wingCamberAttackAngleBias
               val var17: Vector3d
               if (!((var17 = var76.mul(-1.0, Vector3d())).lengthSquared() < 1.0E-12)) {
                  var17.normalize()
                  val var36: Double = var0.aerodynamicUtils.getAirDensityForY(var1.shipToWorld.transformPosition(var63, Vector3d()).y(), var0.dimension)
                  val var99: Vector3d = var79.mul(
                     RangesKt.coerceIn(var10.wingLiftPower * Math.sin(var32 * 2.0) * var36 * var16.lengthSquared(), -1.0E7, 1.0E7), Vector3d()
                  )
                  var63 = var99
                  val var45: Double = var10.wingDragPower * ((1.0 - Math.cos(var32 * 2.0)) / 2.0) * var36 * var76.lengthSquared()
                  val var101: Vector3dc
                  if (var50) {
                     val var100: Vector3d = var17.mul(var45, Vector3d())
                     var101 = var100
                  } else {
                     var101 = Vector3d()
                  }

                  val var102: Vector3d = var63.add(var101, Vector3d())
                  val var52: Vector3dc
                  if (!((var52 = var102).lengthSquared() > 1.0E16)) {
                     val var58: Vector3d = var72.cross(var52, Vector3d())
                     var5.add(var52)
                     var6.add(var58)
                  }
               }
            }
         }

         // $VF: Unable to resugar Kotlin loop from Java for loop
         var var18: Int = var9.n
         while (true) {
            if (var18 >= 0) break
            if (var9.keys[var18 * 3] != 0 || var9.keys[var18 * 3 + 1] != 0 || var9.keys[var18 * 3 + 2] != 0) {
               val var103: Int = var9.keys[var18 * 3]
               val var116: Int = var9.keys[var18 * 3 + 1]
               val var119: Int = var9.keys[var18 * 3 + 2]
               val var53: Wing = var9.values[var18] as Wing
               val var54: Boolean = var8.e
               val var80: Matrix4dc = var8.c
               val var104: Vector3d = var8.c.transformDirection(Vector3d(var53.wingNormal))
               val var83: Vector3dc = var104
               val var105: Vector3d = var80.transformPosition(Vector3d((double)var103 + 0.5, (double)var116 + 0.5, (double)var119 + 0.5))
               var var68: Vector3dc = var105
               val var106: Vector3d = var1.shipToWorld.transformPosition(var105, Vector3d()).sub(var1.positionInWorld)
               val var75: Vector3dc = var106
               val var107: Vector3d = Vector3d(var48).cross(var106).add(var7)
               val var78: Vector3dc = var107
               val var108: Vector3d = var1.shipToWorld.transformDirection(var83, Vector3d())
               val var81: Vector3dc = var108
               val var109: Vector3d = var78.sub(Vector3d(var108).mul(var108.dot(var78)), Vector3d())
               if ((var83 = var109).lengthSquared() > 1.0E-12) {
                  val var110: Vector3d = Vector3d(var83).normalize()
                  val var86: Double = var110.angle(var78) * -Math.signum(var81.dot(var78)) + var53.wingCamberAttackAngleBias
                  val var85: Vector3d
                  if (!((var85 = var78.mul(-1.0, Vector3d())).lengthSquared() < 1.0E-12)) {
                     var85.normalize()
                     val var87: Double = var0.aerodynamicUtils.getAirDensityForY(var1.shipToWorld.transformPosition(var68, Vector3d()).y(), var0.dimension)
                     val var111: Vector3d = var81.mul(
                        RangesKt.coerceIn(var53.wingLiftPower * Math.sin(var86 * 2.0) * var87 * var83.lengthSquared(), -1.0E7, 1.0E7), Vector3d()
                     )
                     var68 = var111
                     val var91: Double = var53.wingDragPower * ((1.0 - Math.cos(var86 * 2.0)) / 2.0) * var87 * var78.lengthSquared()
                     val var113: Vector3dc
                     if (var54) {
                        val var112: Vector3d = var85.mul(var91, Vector3d())
                        var113 = var112
                     } else {
                        var113 = Vector3d()
                     }

                     val var114: Vector3d = var68.add(var113, Vector3d())
                     val var56: Vector3dc
                     if (!((var56 = var114).lengthSquared() > 1.0E16)) {
                        val var61: Vector3d = var75.cross(var56, Vector3d())
                        var5.add(var56)
                        var6.add(var61)
                     }
                  }
               }
            }

            var18--
         }
      }

      val var47: Vector3d
      if ((var47 = var4.invert(Matrix3d()).transform(var1.worldToShip.transformDirection(var6, Vector3d()))).length() > Math.PI * 4) {
         var6.mul((Math.PI * 4) / var47.length())
      }

      return Pair(var5, var6)
   }
}
