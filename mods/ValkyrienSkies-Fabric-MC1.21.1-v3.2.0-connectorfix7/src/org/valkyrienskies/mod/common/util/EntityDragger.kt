package org.valkyrienskies.mod.common.util

import net.minecraft.class_1297
import net.minecraft.class_1309
import net.minecraft.class_1531
import net.minecraft.class_1657
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_243
import net.minecraft.class_3222
import net.minecraft.class_3532
import net.minecraft.class_3959
import net.minecraft.class_3965
import net.minecraft.class_746
import net.minecraft.class_239.class_240
import net.minecraft.class_3959.class_242
import net.minecraft.class_3959.class_3960
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.ships.properties.ShipTransform
import org.valkyrienskies.mod.api.ValkyrienSkies
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.config.VSClientConfig
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager

public object EntityDragger {
   private const val ADDED_MOVEMENT_DECAY: Double = 0.9
   private const val HULL_EDGE_MARGIN: Double = 0.5

   public fun dragEntitiesWithShips(entities: Iterable<class_1297>, preTick: Boolean = false) {
      for (entity in entities) {
         val var10000: IEntityDraggingInformationProvider = entity as? IEntityDraggingInformationProvider
         if ((entity as? IEntityDraggingInformationProvider) != null) {
            val var59: EntityDraggingInformation = var10000.draggingInformation
            if (var59 != null) {
               val isGliding: Boolean = (entity as? class_1309) != null && (entity as? class_1309).method_6128()
               val isFreefallingPlayer: Boolean = entity is class_1657 && !entity.method_24828() && !(entity as class_1657).method_31549().field_7479
               val isFlyingPlayer: Boolean = entity is class_1657 && (entity as class_1657).method_31549().field_7479
               var carriedShipId: java.lang.Long = var59.lastShipStoodOn
               if (carriedShipId != null && entity is class_1657) {
                  val dragTheEntity: Ship = shipObjectWorld.allShips.getById(carriedShipId)
                  val addedMovement: AABBic = if (dragTheEntity != null) dragTheEntity.shipAABB else null
                  val var60: Boolean
                  if (dragTheEntity != null && addedMovement != null) {
                     val worldPos: class_243 = entity.method_19538()
                     val shipDraggingEntity: Vector3d = dragTheEntity.prevTickTransform.worldToShip
                        .transformPosition(worldPos.field_1352, worldPos.field_1351, worldPos.field_1350, Vector3d())
                        var60 = shipDraggingEntity.x >= addedMovement.minX() - 0.5
                        && shipDraggingEntity.x <= addedMovement.maxX() + 1.0 + 0.5
                        && shipDraggingEntity.z >= addedMovement.minZ() - 0.5
                        && shipDraggingEntity.z <= addedMovement.maxZ() + 1.0 + 0.5
                        && shipDraggingEntity.y >= addedMovement.minY() - 1.0
                        && shipDraggingEntity.y <= addedMovement.maxY() + 2.0
                     } else {
                     var60 = false
                  }

                  if (var60 || !entity.method_24828() && !entity.method_5799()) {
                     if (entity.method_24828() && var60) {
                        var59.ticksSinceStoodOnShip = 0
                     }
                  } else {
                     var59.lastShipStoodOn = null
                     var59.addedMovementLastTick = Vector3d() as Vector3dc
                     var59.addedYawRotLastTick = 0.0
                     carriedShipId = null
                  }
               }

               if ((isGliding || isFreefallingPlayer || isFlyingPlayer) && carriedShipId != null) {
                  val var35: Ship = shipObjectWorld.allShips.getById(carriedShipId)
                  val var37: AABBic = if (var35 != null) var35.shipAABB else null
                  val var61: Boolean
                  if (var35 != null && var37 != null) {
                     val var41: class_243 = entity.method_19538()
                     val var42: Vector3d = var35.worldToShip.transformPosition(var41.field_1352, var41.field_1351, var41.field_1350, Vector3d())
                     var61 = var42.x >= var37.minX() - VSClientConfig.CLIENT.influenceExtendLeft
                        && var42.x <= var37.maxX() + VSClientConfig.CLIENT.influenceExtendRight
                        && var42.y >= var37.minY() - VSClientConfig.CLIENT.influenceExtendBottom
                        && var42.y <= var37.maxY() + VSClientConfig.CLIENT.influenceExtendTop
                        && var42.z >= var37.minZ() - VSClientConfig.CLIENT.influenceExtendBack
                        && var42.z <= var37.maxZ() + VSClientConfig.CLIENT.influenceExtendFront
                     } else {
                     var61 = false
                  }

                  if (var61) {
                     var59.ticksSinceStoodOnShip = 0
                  } else {
                     var59.lastShipStoodOn = null
                     var59.addedMovementLastTick = Vector3d() as Vector3dc
                     var59.addedYawRotLastTick = 0.0
                  }
               }

               var var36: Boolean = false
               var var38: Vector3dc = null
               var var40: Double = 0.0
               val var43: java.lang.Long = var59.lastShipStoodOn
               if (var43 != null && entity.method_5854() == null && isDraggable(entity)) {
                  if (!var59.isEntityBeingDraggedByAShip()) {
                     var38 = Vector3d(var59.addedMovementLastTick)
                     var40 = 0.0
                  } else {
                     val var44: Ship = shipObjectWorld.allShips.getById(var43)
                     if (var44 != null) {
                        var36 = true
                        val acceleration: Vector3dc = if (preTick)
                           Vector3d(entity.method_23317(), entity.method_23318(), entity.method_23321())
                           else
                           Vector3d(entity.field_6014, entity.field_6036, entity.field_5969)
                           val var50: ShipTransform = if (var44 is ClientShip) (var44 as ClientShip).getTransform() else var44.transform
                        val var62: Vector3d = var50.shipToWorld
                           .transformPosition(var44.prevTickTransform.worldToShip.transformPosition(Vector3d(acceleration)))
                           var38 = var62.sub(acceleration, Vector3d())
                        if (entity !is class_1531) {
                           val var52: Double = entity.method_36454()
                           val newLookIdeal: Vector3d = var50.shipToWorld
                              .transformDirection(
                                 var44.prevTickTransform.worldToShip
                                    .transformDirection(Vector3d(Math.sin(-Math.toRadians(var52)), 0.0, Math.cos(-Math.toRadians(var52))))
                              )
                              val var58: Double = Math.cos(Math.asin(-newLookIdeal.y()))
                           val newYRot: Double = -Math.atan2(newLookIdeal.x() / var58, newLookIdeal.z() / var58)
                           var entityYRotCorrected: Double = entity.method_36454() % 360.0
                           if (entityYRotCorrected <= -180.0) {
                              entityYRotCorrected += 360.0
                           }

                           if (entityYRotCorrected >= 180.0) {
                              entityYRotCorrected -= 360.0
                           }

                           var addedYRotFromDragging: Double = Math.toDegrees(newYRot) - entityYRotCorrected
                           if (addedYRotFromDragging <= -180.0) {
                              addedYRotFromDragging += 360.0
                           }

                           if (addedYRotFromDragging >= 180.0) {
                              addedYRotFromDragging -= 360.0
                           }

                           var40 = addedYRotFromDragging
                        } else {
                           val stored: java.lang.Double = var59.draggedArmorStandRelYaw
                           var var53: Double = if (stored != null)
                              entity.method_36454() - dragEntitiesWithShips$worldYawFromRel(stored, var44.prevTickTransform)
                              else
                              0.0

                           while (var53 <= -180.0) {
                              var53 += 360.0
                           }

                           while (var53 > 180.0) {
                              var53 -= 360.0
                           }

                           if (stored == null || var59.changedShipLastTick || Math.abs(var53) > 1.0) {
                              var59.draggedArmorStandRelYaw = dragEntitiesWithShips$relYawFromWorld((double)entity.method_36454(), var50)
                           }

                           val var63: java.lang.Double = var59.draggedArmorStandRelYaw
                           var var57: Double = dragEntitiesWithShips$worldYawFromRel(var63, var50) - entity.method_36454()

                           while (var57 <= -180.0) {
                              var57 += 360.0
                           }

                           while (var57 > 180.0) {
                              var57 -= 360.0
                           }

                           var40 = var57
                        }
                     }
                  }
               }

               label426@
               if (var36 && var38 != null && var38.isFinite() && !java.lang.Double.isInfinite(var40) && !java.lang.Double.isNaN(var40)) {
                  entity.method_5857(entity.method_5829().method_997(ValkyrienSkies.toMinecraft(var38)))
                  entity.method_5814(entity.method_23317() + var38.x(), entity.method_23318() + var38.y(), entity.method_23321() + var38.z())
                  if (var59.shouldImpulseMovement && (!entity.method_37908().field_9236 || entity is class_746)) {
                     val var48: Vector3d = Vector3d(var59.addedMovementLastTick).sub(var38)
                     entity.method_5762(var48.x, var48.y, var48.z)
                  }

                  var59.addedMovementLastTick = var38
                  if (!java.lang.Double.isInfinite(var40) && !java.lang.Double.isNaN(var40)) {
                     if (!entity.method_37908().method_8608()) {
                        if (entity !is class_3222) {
                           entity.method_36456((entity.method_36454() + (float)var40 + 360.0F) % 360.0F)
                           entity.method_5847((entity.method_5791() + (float)var40 + 360.0F) % 360.0F)
                           if (entity is class_1309) {
                              (entity as class_1309).field_6283 = ((entity as class_1309).field_6283 + (float)var40 + 360.0F) % 360.0F
                           }
                        } else {
                           entity.method_36456(class_3532.method_15393(entity.method_36454() + (float)var40))
                           (entity as class_3222).field_6241 = class_3532.method_15393((entity as class_3222).field_6241 + (float)var40)
                           (entity as class_3222).field_6283 = class_3532.method_15393((entity as class_3222).field_6283 + (float)var40)
                        }
                     } else if (!entity.method_5787() && entity !is class_1657) {
                        entity.method_36456(class_3532.method_15393(entity.method_36454() + (float)var40))
                        entity.method_5847(class_3532.method_15393(entity.method_5791() + (float)var40))
                        if (entity is class_1309) {
                           (entity as class_1309).field_6283 = class_3532.method_15393((entity as class_1309).field_6283 + (float)var40)
                        }
                     } else {
                        entity.method_36456(entity.method_36454() + (float)var40)
                        entity.method_5847(entity.method_5791() + (float)var40)
                        if (entity is class_1309) {
                           (entity as class_1309).field_6283 += (float)var40
                        }
                     }

                     if (entity is class_1531) {
                        entity.field_5982 = entity.method_36454()
                        (entity as class_1531).field_6259 = (entity as class_1531).field_6241
                        (entity as class_1531).field_6220 = (entity as class_1531).field_6283
                     }

                     var59.addedYawRotLastTick = var40
                  }
                  break@label426
               } else if ((!entity.method_37908().field_9236 || entity is class_746) && var59.addedMovementLastTick.lengthSquared() > 1.0E-6) {
                  entity.method_5762(var59.addedMovementLastTick.x(), var59.addedMovementLastTick.y(), var59.addedMovementLastTick.z())
                  var59.addedMovementLastTick = Vector3d() as Vector3dc
                  var59.addedYawRotLastTick = 0.0
               }

               var59.ticksSinceStoodOnShip = var59.ticksSinceStoodOnShip + 1
               var59.mountedToEntity = entity.method_5854() != null
            }
         }
      }
   }

   public fun class_1297.serversidePosition(): class_243 {
      if (`$this$serversidePosition` is IEntityDraggingInformationProvider
         && (`$this$serversidePosition` as IEntityDraggingInformationProvider).draggingInformation.isEntityBeingDraggedByAShip()
         && (`$this$serversidePosition` as IEntityDraggingInformationProvider).draggingInformation.bestRelativeEntityPosition() != null) {
         val var2: Vector3dc = (`$this$serversidePosition` as IEntityDraggingInformationProvider).draggingInformation.bestRelativeEntityPosition()
         return ValkyrienSkies.toMinecraft(var2)
      } else {
         val var10000: class_243 = `$this$serversidePosition`.method_19538()
         return var10000
      }
   }

   public fun class_1297.serversideEyePosition(): class_243 {
      if (`$this$serversideEyePosition` is IEntityDraggingInformationProvider
         && (`$this$serversideEyePosition` as IEntityDraggingInformationProvider).draggingInformation.isEntityBeingDraggedByAShip()
         && (`$this$serversideEyePosition` as IEntityDraggingInformationProvider).draggingInformation.bestRelativeEntityPosition() != null) {
         var var2: Vector3dc = (`$this$serversideEyePosition` as IEntityDraggingInformationProvider).draggingInformation.bestRelativeEntityPosition()
         var2 = var2.add(0.0, (double)`$this$serversideEyePosition`.method_18381(`$this$serversideEyePosition`.method_18376()), 0.0, Vector3d())
         return ValkyrienSkies.toMinecraft(var2)
      } else {
         val var10000: class_243 = `$this$serversideEyePosition`.method_33571()
         return var10000
      }
   }

   public fun class_1297.serversideEyeRotation(): Double {
      if (`$this$serversideEyeRotation` is class_3222
         && `$this$serversideEyeRotation` is IEntityDraggingInformationProvider
         && (`$this$serversideEyeRotation` as IEntityDraggingInformationProvider).draggingInformation.isEntityBeingDraggedByAShip()
         && (`$this$serversideEyeRotation` as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerYaw != null) {
         val var10000: java.lang.Double = (`$this$serversideEyeRotation` as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerYaw
         return var10000 * 180.0 / Math.PI
      } else {
         return `$this$serversideEyeRotation`.method_36454()
      }
   }

   public fun class_1297.serversideEyePositionOrDefault(default: class_243): class_243 {
      if (`$this$serversideEyePositionOrDefault` is class_3222
         && `$this$serversideEyePositionOrDefault` is IEntityDraggingInformationProvider
         && (`$this$serversideEyePositionOrDefault` as IEntityDraggingInformationProvider).draggingInformation.isEntityBeingDraggedByAShip()
         && (`$this$serversideEyePositionOrDefault` as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerPosition != null) {
         val var10000: Vector3dc = (`$this$serversideEyePositionOrDefault` as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerPosition
         return ValkyrienSkies.toMinecraft(var10000)
      } else {
         return default
      }
   }

   public fun class_1297.serversideEyeRotationOrDefault(default: Double): Double {
      if (`$this$serversideEyeRotationOrDefault` is class_3222
         && `$this$serversideEyeRotationOrDefault` is IEntityDraggingInformationProvider
         && (`$this$serversideEyeRotationOrDefault` as IEntityDraggingInformationProvider).draggingInformation.isEntityBeingDraggedByAShip()
         && (`$this$serversideEyeRotationOrDefault` as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerYaw != null) {
         val var10000: java.lang.Double = (`$this$serversideEyeRotationOrDefault` as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerYaw
         return Math.toDegrees(var10000)
      } else {
         return default
      }
   }

   public fun class_1297.serversideWorldEyeRotationOrDefault(ship: Ship, default: Double): Double {
      if (`$this$serversideWorldEyeRotationOrDefault` is class_3222
         && `$this$serversideWorldEyeRotationOrDefault` is IEntityDraggingInformationProvider
         && (`$this$serversideWorldEyeRotationOrDefault` as IEntityDraggingInformationProvider).draggingInformation.isEntityBeingDraggedByAShip()
         && (`$this$serversideWorldEyeRotationOrDefault` as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerYaw != null) {
         val var10000: EntityLerper = EntityLerper.INSTANCE
         val var10002: java.lang.Double = (`$this$serversideWorldEyeRotationOrDefault` as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerYaw
         return var10000.yawToWorld(ship, var10002)
      } else {
         return default
      }
   }

   @JvmStatic
   public fun backOff(vec3: class_243, ship: Ship, player: class_1657, cLevel: class_1937): class_243 {
      val transformedVec: Vector3d = ship.worldToShip.transformDirection(ValkyrienSkies.toJOML(vec3), Vector3d())
      var d: Double = transformedVec.x
      var e: Double = transformedVec.y
      var f: Double = transformedVec.z

      while (d != 0.0 && !INSTANCE.isValidWalkablePosition(cLevel, ship, player, d, class_2350.field_11034)) {
         if (d < 0.025 && d >= -0.025) {
            d = 0.0
         } else if (d > 0.0) {
            d -= 0.025
         } else {
            d += 0.025
         }
      }

      while (f != 0.0 && !INSTANCE.isValidWalkablePosition(cLevel, ship, player, f, class_2350.field_11035)) {
         if (f < 0.025 && f >= -0.025) {
            f = 0.0
         } else if (f > 0.0) {
            f -= 0.025
         } else {
            f += 0.025
         }
      }

      while (e != 0.0 && !INSTANCE.isValidWalkablePosition(cLevel, ship, player, e, class_2350.field_11036)) {
         if (e < 0.025 && e >= -0.025) {
            e = 0.0
         } else if (e > 0.0) {
            e -= 0.025
         } else {
            e += 0.025
         }
      }

      while (
         d != 0.0
            && f != 0.0
            && e != 0.0
            && !INSTANCE.isValidWalkablePosition(cLevel, ship, player, d, class_2350.field_11034)
            && !INSTANCE.isValidWalkablePosition(cLevel, ship, player, f, class_2350.field_11035)
            && !INSTANCE.isValidWalkablePosition(cLevel, ship, player, e, class_2350.field_11036)
      ) {
         if (d < 0.025 && d >= -0.025) {
            d = 0.0
         } else if (d > 0.0) {
            d -= 0.025
         } else {
            d += 0.025
         }

         if (f < 0.025 && f >= -0.025) {
            f = 0.0
         } else if (f > 0.0) {
            f -= 0.025
         } else {
            f += 0.025
         }

         if (e < 0.025 && e >= -0.025) {
            e = 0.0
         } else if (e > 0.0) {
            e -= 0.025
         } else {
            e += 0.025
         }
      }

      val var10000: Vector3d = ship.shipToWorld.transformDirection(Vector3d(d, e, f)).normalize().mul(Math.sqrt(d * d + e * e + f * f))
      return ValkyrienSkies.toMinecraft(var10000)
   }

   private fun isValidWalkablePosition(level: class_1937, ship: Ship, player: class_1657, step: Double, dir: class_2350): Boolean {
      val result: class_3965 = level.method_17742(this.stepTowardsEdge(level, ship, player, step, dir))
      if (result.method_17783() != class_240.field_1332) {
         return false
      } else {
         val var10001: class_2338 = result.method_17777()
         val hitShip: LoadedShip = VSGameUtilsKt.getLoadedShipManagingPos(level, var10001 as class_2382)
         if (hitShip != null) {
            val var10000: class_2382 = result.method_17780().method_10163()
            val dot: Double = hitShip.getShipToWorld()
               .transformDirection(VectorConversionsMCKt.toJOMLD(var10000), Vector3d())
               .normalize()
               .dot(Vector3d(0.0, 1.0, 0.0))
               if (dot < 0.5 && dot > 0.001) {
               return false
            }
         }

         return true
      }
   }

   private fun stepTowardsEdge(level: class_1937?, ship: Ship, player: class_1657, step: Double, dir: class_2350): class_3959 {
      val var10000: class_243 = player.method_19538()
      val var10001: Vector3d = ship.transform.shipToWorld.transformDirection(Vector3d(dir.method_23955())).normalize().mul(step)
      val potentialPosition: class_243 = var10000.method_1019(ValkyrienSkies.toMinecraft(var10001))
      val downDirInShip: Vector3dc = ship.worldToShip.transformDirection(Vector3d(0.0, -1.0, 0.0), Vector3d()).normalize().mul((double)player.method_49476())
      val var10: Vector3d = ValkyrienSkies.toJOML(potentialPosition).add(downDirInShip, Vector3d())
      return class_3959(potentialPosition, ValkyrienSkies.toMinecraft(var10), class_3960.field_17558, class_242.field_1348, player as class_1297)
   }

   @JvmStatic
   public fun isDraggable(entity: class_1297): Boolean {
      return !VSEntityManager.isShipyardEntity(entity)
         && entity is IEntityDraggingInformationProvider
         && (entity as IEntityDraggingInformationProvider).vs$shouldDrag()
      }

   @JvmStatic
   fun `dragEntitiesWithShips$relYawFromWorld`(worldYawDeg: Double, t: ShipTransform): Double {
      val ls: Vector3d = t.worldToShip
         .transformDirection(Vector3d(Math.sin(-Math.toRadians(worldYawDeg)), 0.0, Math.cos(-Math.toRadians(worldYawDeg))), Vector3d())
         -Math.atan2(ls.x(), ls.z())
   }

   @JvmStatic
   fun `dragEntitiesWithShips$worldYawFromRel`(relYaw: Double, t: ShipTransform): Double {
      val lw: Vector3d = t.shipToWorld.transformDirection(Vector3d(Math.sin(-relYaw), 0.0, Math.cos(-relYaw)), Vector3d())
      -Math.toDegrees(Math.atan2(lw.x(), lw.z()))
   }
}
