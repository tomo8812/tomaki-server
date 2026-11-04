package org.valkyrienskies.mod.common

import net.minecraft.class_1937
import net.minecraft.class_1944
import net.minecraft.class_2338
import net.minecraft.class_2374
import net.minecraft.class_243
import net.minecraft.class_3481
import net.minecraft.class_3532
import net.minecraft.class_3562
import net.minecraft.class_3959
import net.minecraft.class_3965
import net.minecraft.class_239.class_240
import net.minecraft.class_2902.class_2903
import net.minecraft.class_3959.class_242
import net.minecraft.class_3959.class_3960
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.common.world.RaycastUtilsKt

public object CompatUtil {
   @JvmOverloads
   public fun toSameSpaceAs(level: class_1937?, position: Vector3dc, targetShip: Ship?, sourceShip: Ship? = null): Vector3d {
      val result: Vector3d = Vector3d(position)
      var var10000: Ship = sourceShip
      if (sourceShip == null) {
         var10000 = if (level != null) VSGameUtilsKt.getShipManagingPos(level, result) else null
      }

      if (!(var10000 == targetShip)) {
         if (var10000 != null) {
            val var7: Matrix4dc = var10000.shipToWorld
            if (var7 != null) {
               var7.transformPosition(result)
            }
         }

         if (targetShip != null) {
            val var8: Matrix4dc = targetShip.worldToShip
            if (var8 != null) {
               var8.transformPosition(result)
            }
         }
      }

      return result
   }

   @JvmOverloads
   public fun toSameSpaceAs(level: class_1937?, position: class_243, targetShip: Ship?, sourceShip: Ship? = null): class_243 {
      return VectorConversionsMCKt.toMinecraft(this.toSameSpaceAs(level, VectorConversionsMCKt.toJOML(position), targetShip, sourceShip))
   }

   public fun toSameSpaceAs(level: class_1937?, position: Vector3dc, target: Vector3dc): Vector3d {
      return toSameSpaceAs$default(this, level, position, VSGameUtilsKt.getShipManagingPos(level, target), null, 8, null)
   }

   public fun toSameSpaceAs(level: class_1937?, position: class_243, target: class_243): class_243 {
      return VectorConversionsMCKt.toMinecraft(
         toSameSpaceAs$default(
            this, level, VectorConversionsMCKt.toJOML(position), VSGameUtilsKt.getShipManagingPos(level, target as class_2374), null, 8, null
         )
      )
   }

   public fun toSameSpaceAs(level: class_1937?, position: class_243, target: class_2338): class_243 {
      return VectorConversionsMCKt.toMinecraft(
         toSameSpaceAs$default(this, level, VectorConversionsMCKt.toJOML(position), VSGameUtilsKt.getShipManagingPos(level, target), null, 8, null)
      )
   }

   public fun toSameSpaceAs(level: class_1937?, px: Double, py: Double, pz: Double, target: class_243): class_243 {
      return VectorConversionsMCKt.toMinecraft(this.toSameSpaceAs(level, Vector3d(px, py, pz), VectorConversionsMCKt.toJOML(target)))
   }

   public fun toSameSpaceAs(level: class_1937?, px: Double, py: Double, pz: Double, target: class_2338): class_243 {
      return VectorConversionsMCKt.toMinecraft(
         toSameSpaceAs$default(this, level, Vector3d(px, py, pz), VSGameUtilsKt.getShipManagingPos(level, target), null, 8, null)
      )
   }

   public fun getWorldHeightmapPosIncludingShips(level: class_1937, types: class_2903, pos: class_2338): class_2338 {
      val var10002: class_243 = level.method_8598(types, pos).method_46558()
      val worldHeight: class_2338 = class_2338.method_49638(toSameSpaceAs$default(this, level, var10002, null, null, 8, null) as class_2374)
      val var10000: class_3965 = this.getShipHeightmapHitAboveWorldHeight(level, types, worldHeight)
      return if (var10000 == null)
         worldHeight
         else
         class_2338(worldHeight.method_10263(), class_3532.method_15357(Math.nextDown(var10000.field_1329.field_1351)) + 1, worldHeight.method_10260())
      }

   public fun getWorldHeightIncludingShips(level: class_1937, types: class_2903, x: Int, z: Int): Int {
      return this.getWorldHeightmapPosIncludingShips(level, types, class_2338(x, level.method_31607(), z)).method_10264()
   }

   public fun getShipHeightmapPosAboveWorldHeight(level: class_1937, types: class_2903, worldHeight: class_2338): class_2338? {
      val var10000: class_3965 = this.getShipHeightmapHitAboveWorldHeight(level, types, worldHeight)
      if (var10000 != null) {
         val var4: class_2338 = var10000.method_17777()
         if (var4 != null) {
            return var4.method_10062()
         }
      }

      return null
   }

   public fun getShipHeightmapHitAboveWorldHeight(level: class_1937, types: class_2903, worldHeight: class_2338): class_3965? {
      if (worldHeight.method_10264() >= level.method_31600()) {
         return null
      } else {
         val start: class_243 = class_243(worldHeight.method_10263() + 0.5, level.method_31600(), worldHeight.method_10260() + 0.5)
         val end: class_243 = worldHeight.method_46558()
         if (types === class_2903.field_13203) {
            return this.clipAboveWorldHeightIgnoringLeafHits(level, start, end)
         } else {
            var var10000: class_3960
            when (CompatUtil.WhenMappings.$EnumSwitchMapping$0[types.ordinal()]) {
               1, 2 -> var10000 = class_3960.field_17559
               else -> var10000 = class_3960.field_17558
            }

            var var9: class_242
            when (CompatUtil.WhenMappings.$EnumSwitchMapping$0[types.ordinal()]) {
               1, 2, 3, 4 -> var9 = class_242.field_1347
               else -> var9 = class_242.field_1348
            }

            val hit: class_3965 = RaycastUtilsKt.clipIncludeShips$default(level, class_3959(start, end, var10000, var9, null), false, null, true, 6, null)
            return if (hit.method_17783() === class_240.field_1333) null else hit
         }
      }
   }

   private fun clipAboveWorldHeightIgnoringLeafHits(level: class_1937, start: class_243, end: class_243): class_3965? {
      var currentStart: class_243 = start

      do {
         val hit: class_3965 = RaycastUtilsKt.clipIncludeShips$default(
            level, class_3959(currentStart, end, class_3960.field_17558, class_242.field_1347, null), false, null, true, 6, null
         )
         if (hit.method_17783() === class_240.field_1333) {
            return null
         }

         if (!level.method_8320(hit.method_17777()).method_26164(class_3481.field_15503)) {
            return hit
         }

         currentStart = hit.field_1329.method_1031(0.0, -0.25, 0.0)
      } while (!(currentStart.field_1351 <= end.field_1351))

      return null
   }

   @JvmOverloads
   public fun getCompoundBrightness(level: class_1937, lightType: class_1944, pos: class_2338, ship: Ship? = null): Int {
      var shipx: Ship = ship
      if (ship == null) {
         shipx = VSGameUtilsKt.getShipManagingPos(level, pos)
      }

      val listener: class_3562 = level.method_22336().method_15562(lightType)
      if (shipx == null) {
         return listener.method_15543(pos)
      } else {
         val var8: Matrix4dc = shipx.shipToWorld
val var10001: class_243 = pos.method_46558()
val worldPos: class_2338 = class_2338.method_49638(VectorConversionsMCKt.transformPosition(var8, var10001) as class_2374)
var var9: Int
         when (CompatUtil.WhenMappings.$EnumSwitchMapping$1[lightType.ordinal()]) {
            1 -> var9 = RangesKt.coerceAtMost(listener.method_15543(pos) + listener.method_15543(worldPos), 15)
            2 -> var9 = Math.min(listener.method_15543(pos), listener.method_15543(worldPos))
            else -> throw NoWhenBranchMatchedException()
         }

         return var9
      }
   }

   @JvmOverloads
   fun toSameSpaceAs(level: class_1937?, position: Vector3dc, targetShip: Ship?): Vector3d {
      toSameSpaceAs$default(this, level, position, targetShip, null, 8, null)
   }

   @JvmOverloads
   fun toSameSpaceAs(level: class_1937?, position: class_243, targetShip: Ship?): class_243 {
      toSameSpaceAs$default(this, level, position, targetShip, null, 8, null)
   }

   @JvmOverloads
   fun getCompoundBrightness(level: class_1937, lightType: class_1944, pos: class_2338): Int {
      getCompoundBrightness$default(this, level, lightType, pos, null, 8, null)
   }

   // $VF: Class flags could not be determined
   @JvmSynthetic
   internal class WhenMappings {
      @JvmStatic
      @JvmSynthetic
      public int[] $EnumSwitchMapping$0;
      @JvmStatic
      @JvmSynthetic
      public int[] $EnumSwitchMapping$1;

      @JvmStatic
      fun {
         var var0: IntArray = IntArray(class_2903.values().length)

         try {
            var0[class_2903.field_13202.ordinal()] = 1
         } catch (var7: NoSuchFieldError) {
         }

         try {
            var0[class_2903.field_13194.ordinal()] = 2
         } catch (var6: NoSuchFieldError) {
         }

         try {
            var0[class_2903.field_13197.ordinal()] = 3
         } catch (var5: NoSuchFieldError) {
         }

         try {
            var0[class_2903.field_13203.ordinal()] = 4
         } catch (var4: NoSuchFieldError) {
         }

         $EnumSwitchMapping$0 = var0
         var0 = IntArray(class_1944.values().length)

         try {
            var0[class_1944.field_9282.ordinal()] = 1
         } catch (var3: NoSuchFieldError) {
         }

         try {
            var0[class_1944.field_9284.ordinal()] = 2
         } catch (var2: NoSuchFieldError) {
         }

         $EnumSwitchMapping$1 = var0
      }
   }
}
