@file:SourceDebugExtension(["SMAP\nRaycastUtils.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RaycastUtils.kt\norg/valkyrienskies/mod/common/world/RaycastUtilsKt\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,222:1\n1863#2,2:223\n1863#2,2:225\n*S KotlinDebug\n*F\n+ 1 RaycastUtils.kt\norg/valkyrienskies/mod/common/world/RaycastUtilsKt\n*L\n198#1:223,2\n157#1:225,2\n*E\n"])

package org.valkyrienskies.mod.common.world

import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import java.util.Optional
import java.util.function.Predicate
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.DoubleRef
import kotlin.jvm.internal.Ref.ObjectRef
import net.minecraft.class_1297
import net.minecraft.class_1922
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2374
import net.minecraft.class_238
import net.minecraft.class_243
import net.minecraft.class_3959
import net.minecraft.class_3965
import net.minecraft.class_3966
import net.minecraft.class_239.class_240
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.joml.Vector2d
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBd
import org.joml.primitives.AABBdc
import org.joml.primitives.LineSegmentf
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.core.util.AABBdUtilKt
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.mixinducks.feature.clip_replace.ClipContextDuck
import org.valkyrienskies.mod.util.McMathUtilKt

private final val logger: Logger = LogManager.getLogger("RaycastUtilsKt")
public final val VanillaClipContext: RayCastExtraParameter = RayCastExtraParameter(false, null, false, true, null, null, 55, null)

@JvmOverloads
public fun class_1937.clipIncludeShips(ctx: class_3959, shouldTransformHitPos: Boolean = true, skipShip: Long? = null, skipWorld: Boolean = false): class_3965 {
   val ccd: ClipContextDuck = ctx as ClipContextDuck
   (ctx as ClipContextDuck).setRayCastExtraParameter(RayCastExtraParameter(shouldTransformHitPos, skipShip, skipWorld, false, null, null, 56, null))
   val ret: class_3965 = `$this$clipIncludeShips`.method_17742(ctx)
   ccd.reset()
   return ret
}

public fun class_1937.clipIncludeShipsImpl(ctx: class_3959, vanillaClip: Operation<class_3965>): class_3965 {
   if (ctx !is ClipContextDuck) {
      val var59: Any = vanillaClip.call(arrayOf(ctx))
      return var59 as class_3965
   } else {
      val var10000: class_3965
      if ((ctx as ClipContextDuck).getRayCastExtraParameter().skipWorld) {
         val closestHit: class_243 = ctx.method_17747().method_1020(ctx.method_17750())
         var10000 = class_3965.method_17778(
            ctx.method_17747(),
            class_2350.method_10142(closestHit.field_1352, closestHit.field_1351, closestHit.field_1350),
            class_2338.method_49638(ctx.method_17747() as class_2374)
         )
      } else {
         var10000 = vanillaClip.call(arrayOf(ctx)) as class_3965
      }

      if (shipObjectWorld == null) {
         logger.error("shipObjectWorld was empty for level raytrace, this should not be possible! Returning vanilla result.")
         return var10000
      } else {
         var var24: class_3965 = var10000
         var closestHitPos: class_243 = var10000.field_1329
         var closestHitDist: Double = var10000.field_1329.method_1025(ctx.method_17750())
         var var10002: class_243 = ctx.method_17750()
         val var72: Vector3dc = VectorConversionsMCKt.toJOML(var10002)
         val var10003: class_243 = ctx.method_17747()
         val var36: AABBd = AABBd(var72, VectorConversionsMCKt.toJOML(var10003)).correctBounds()
         val clipAABB: AABBdc = var36
         val clipSegment: LineSegmentf = LineSegmentf(ctx.method_17750().method_46409(), ctx.method_17747().method_46409())

         for (ship in VSGameUtilsKt.getShipsIntersecting(`$this$clipIncludeShipsImpl`, clipAABB)) {
            val chopParam: Vector2d = Vector2d()
            val expandedAABB: AABBd = AABBdUtilKt.expand(AABBd(ship.worldAABB), 1.0)
            val intersectType: Int = expandedAABB.intersectsLineSegment(clipSegment, chopParam)
            if (intersectType != -1) {
               val var37: Long = ship.id
               val var10001: java.lang.Long = (ctx as ClipContextDuck).getRayCastExtraParameter().skipShip
               if (var10001 == null || var37 != var10001) {
                  when (intersectType) {
                     1 -> {
                        val var64: class_243 = ctx.method_17750()
                        if (expandedAABB.containsPoint(VectorConversionsMCKt.toJOML(var64))) {
                           val var44: class_243 = ctx.method_17750()
                           val var29: Vector3d = VectorConversionsMCKt.toJOML(var44)
                           val var45: class_243 = ctx.method_17750()
                           val var46: Vector3d = VectorConversionsMCKt.toJOML(var45)
                           val var65: class_243 = ctx.method_17747()
                           val var66: Vector3d = VectorConversionsMCKt.toJOML(var65)
                           var10002 = ctx.method_17750()
                           val var33: Vector3d = var46.add(var66.sub(VectorConversionsMCKt.toJOML(var10002)).mul(chopParam.y))
                        } else {
                           val var47: class_243 = ctx.method_17750()
                           val var48: Vector3d = VectorConversionsMCKt.toJOML(var47)
                           val var67: class_243 = ctx.method_17747()
                           val var68: Vector3d = VectorConversionsMCKt.toJOML(var67)
                           var10002 = ctx.method_17750()
                           val var30: Vector3d = var48.add(var68.sub(VectorConversionsMCKt.toJOML(var10002)).mul(chopParam.x))
                           val var49: class_243 = ctx.method_17747()
                           val var34: Vector3d = VectorConversionsMCKt.toJOML(var49)
                        }
                     }
                     2 -> {
                        val var40: class_243 = ctx.method_17750()
                        val var41: Vector3d = VectorConversionsMCKt.toJOML(var40)
                        val var60: class_243 = ctx.method_17747()
                        val var61: Vector3d = VectorConversionsMCKt.toJOML(var60)
                        var10002 = ctx.method_17750()
                        val var28: Vector3d = var41.add(var61.sub(VectorConversionsMCKt.toJOML(var10002)).mul(chopParam.x))
                        val var42: class_243 = ctx.method_17750()
                        val var43: Vector3d = VectorConversionsMCKt.toJOML(var42)
                        val var62: class_243 = ctx.method_17747()
                        val var63: Vector3d = VectorConversionsMCKt.toJOML(var62)
                        var10002 = ctx.method_17750()
                        val var32: Vector3d = var43.add(var63.sub(VectorConversionsMCKt.toJOML(var10002)).mul(chopParam.y))
                     }
                     else -> {
                        val var38: class_243 = ctx.method_17750()
                        val var27: Vector3d = VectorConversionsMCKt.toJOML(var38)
                        val var39: class_243 = ctx.method_17747()
                        val var31: Vector3d = VectorConversionsMCKt.toJOML(var39)
                     }
                  }

                  run label98@{
                     val var50: ClientShip = ship as? ClientShip
                     if ((ship as? ClientShip) != null) {
                        val var51: ShipTransform = var50.renderTransform
                        if (var51 != null) {
                           var52 = var51.worldToShip
                           if (var52 != null) {
                              return@label98
                           }
                        }
                     }

                     var52 = ship.worldToShip
                  }

                  run label102@{
                     val var53: ClientShip = ship as? ClientShip
                     if ((ship as? ClientShip) != null) {
                        val var54: ShipTransform = var53.renderTransform
                        if (var54 != null) {
                           var55 = var54.shipToWorld
                           if (var55 != null) {
                              return@label102
                           }
                        }
                     }

                     var55 = ship.shipToWorld
                  }

                  val var56: RayCastExtraParameter = (ctx as ClipContextDuck).getRayCastExtraParameter()
                  var10002 = ctx.method_17750()
                  val var69: Vector3d = var52.transformPosition(VectorConversionsMCKt.toJOML(var10002))
                  var56._posStart = VectorConversionsMCKt.toMinecraft(var69)
                  val var57: RayCastExtraParameter = (ctx as ClipContextDuck).getRayCastExtraParameter()
                  var10002 = ctx.method_17747()
                  val var70: Vector3d = var52.transformPosition(VectorConversionsMCKt.toJOML(var10002))
                  var57._posEnd = VectorConversionsMCKt.toMinecraft(var70)
                  val shipHit: class_3965 = vanillaClip.call(arrayOf(ctx)) as class_3965
                  (ctx as ClipContextDuck).getRayCastExtraParameter()._posStart = null
                  (ctx as ClipContextDuck).getRayCastExtraParameter()._posEnd = null
                  val var71: class_243 = shipHit.field_1329
                  val var58: Vector3d = var55.transformPosition(VectorConversionsMCKt.toJOML(var71))
                  val var35: class_243 = VectorConversionsMCKt.toMinecraft(var58)
                  val shipHitDist: Double = var35.method_1025(ctx.method_17750())
                  if (shipHitDist < closestHitDist && shipHit.method_17783() != class_240.field_1333) {
                     var24 = shipHit
                     closestHitPos = var35
                     closestHitDist = shipHitDist
                  }
               }
            }
         }

         if ((ctx as ClipContextDuck).getRayCastExtraParameter().shouldTransformHitPos) {
            var24.field_1329 = closestHitPos
         }

         return var24
      }
   }
}

public fun class_1937.raytraceEntities(
   shooter: class_1297,
   origStartVecM: class_243,
   origEndVecM: class_243,
   origBoundingBoxM: class_238,
   filter: Predicate<class_1297>,
   maxDistance2: Double
): class_3966? {
   val distance2: DoubleRef = DoubleRef()
   distance2.element = maxDistance2
   val resultEntity: ObjectRef = ObjectRef()
   val location: ObjectRef = ObjectRef()
   val entities: java.util.List = `$this$raytraceEntities`.method_8333(shooter, origBoundingBoxM, filter)
   raytraceEntities$checkEntities(distance2, resultEntity, location, shooter, entities, origStartVecM, origEndVecM, 1.0)
   val origStartVec: Vector3d = VectorConversionsMCKt.toJOML(origStartVecM)
   val origEndVec: Vector3d = VectorConversionsMCKt.toJOML(origEndVecM)
   val start: Vector3d = Vector3d()
   val end: Vector3d = Vector3d()

   for (`element$iv` in VSGameUtilsKt.getShipsIntersecting(`$this$raytraceEntities`, VectorConversionsMCKt.toJOML(origBoundingBoxM))) {
      val it: Ship = `element$iv` as Ship
      (`element$iv` as Ship).worldToShip.transformPosition(origStartVec, start)
      it.worldToShip.transformPosition(origEndVec, end)
      raytraceEntities$checkEntities(
         distance2,
         resultEntity,
         location,
         shooter,
         entities,
         VectorConversionsMCKt.toMinecraft(start),
         VectorConversionsMCKt.toMinecraft(end),
         1.0 / it.transform.shipToWorldScaling.x()
      )
   }

   return if (resultEntity.element == null) null else class_3966(resultEntity.element as class_1297, location.element as class_243)
}

public fun class_1922.vanillaClip(ctx: class_3959): class_3965 {
   if (ctx !is ClipContextDuck) {
      val var10000: class_3965 = `$this$vanillaClip`.method_17742(ctx)
      return var10000
   } else {
      (ctx as ClipContextDuck).setRayCastExtraParameter(VanillaClipContext)
      val ret: class_3965 = `$this$vanillaClip`.method_17742(ctx)
      (ctx as ClipContextDuck).reset()
      return ret
   }
}

@JvmOverloads
fun class_1937.clipIncludeShips(ctx: class_3959, shouldTransformHitPos: Boolean, skipShip: java.lang.Long?): class_3965 {
   clipIncludeShips$default(`$this$clipIncludeShips`, ctx, shouldTransformHitPos, skipShip, false, 8, null)
}

@JvmOverloads
fun class_1937.clipIncludeShips(ctx: class_3959, shouldTransformHitPos: Boolean): class_3965 {
   clipIncludeShips$default(`$this$clipIncludeShips`, ctx, shouldTransformHitPos, null, false, 12, null)
}

@JvmOverloads
fun class_1937.clipIncludeShips(ctx: class_3959): class_3965 {
   clipIncludeShips$default(`$this$clipIncludeShips`, ctx, false, null, false, 14, null)
}

fun `raytraceEntities$checkEntities`(
   distance2: DoubleRef,
   resultEntity: ObjectRef<class_1297>,
   location: ObjectRef<class_243>,
   `$shooter`: class_1297,
   entities: MutableList<class_1297>,
   startVec: class_243,
   endVec: class_243,
   scale: Double
) {
   for (`element$iv` in entities) {
      val entity: class_1297 = `element$iv` as class_1297
      val var10000: class_238 = (`element$iv` as class_1297).method_5829().method_1014((double)(`element$iv` as class_1297).method_5871())
      val aabb: class_238 = McMathUtilKt.scale(var10000, scale)
      val clipO: Optional = aabb.method_992(startVec, endVec)
      if (aabb.method_1006(startVec)) {
         if (!(distance2.element < 0.0)) {
            resultEntity.element = entity
            location.element = clipO.orElse(startVec)
            distance2.element = 0.0
         }
      } else if (clipO.isPresent()) {
         val var20: Any = clipO.get()
         val clip: class_243 = var20 as class_243
         val d: Double = startVec.method_1025(var20 as class_243) / (scale * scale)
         if (!(d >= distance2.element) || distance2.element == 0.0) {
            if (entity.method_5668() === `$shooter`.method_5668()) {
               if (distance2.element == 0.0) {
                  resultEntity.element = entity
                  location.element = clip
               }
            } else {
               resultEntity.element = entity
               location.element = clip
               distance2.element = d
            }
         }
      }
   }
}
