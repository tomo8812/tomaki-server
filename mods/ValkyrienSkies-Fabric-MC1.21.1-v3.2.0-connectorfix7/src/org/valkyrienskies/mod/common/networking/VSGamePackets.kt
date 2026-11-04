package org.valkyrienskies.mod.common.networking

import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1297
import net.minecraft.class_1299
import net.minecraft.class_1309
import net.minecraft.class_1657
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2382
import net.minecraft.class_243
import net.minecraft.class_2960
import net.minecraft.class_310
import net.minecraft.class_3218
import net.minecraft.class_3222
import net.minecraft.class_638
import net.minecraft.class_746
import net.minecraft.class_7923
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.event.RegisteredListener
import org.valkyrienskies.core.api.ships.ClientShip
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.QueryableShipData
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.impl.networking.simple.SimplePacketNetworking
import org.valkyrienskies.core.internal.world.VsiPlayer
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.entity.handling.VSEntityHandler
import org.valkyrienskies.mod.common.entity.handling.VSEntityManager
import org.valkyrienskies.mod.common.util.EntityLerper
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider
import org.valkyrienskies.mod.common.util.MinecraftPlayer
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.mixinducks.feature.tickets.PlayerKnownShipsDuck
import org.valkyrienskies.mod.mixinducks.world.entity.PlayerDuck

@SourceDebugExtension(["SMAP\nVSGamePackets.kt\nKotlin\n*S Kotlin\n*F\n+ 1 VSGamePackets.kt\norg/valkyrienskies/mod/common/networking/VSGamePackets\n+ 2 AttachmentHolder.kt\norg/valkyrienskies/core/api/attachment/AttachmentHolderKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,314:1\n66#2:315\n1#3:316\n216#4,2:317\n*S KotlinDebug\n*F\n+ 1 VSGamePackets.kt\norg/valkyrienskies/mod/common/networking/VSGamePackets\n*L\n52#1:315\n96#1:317,2\n*E\n"])
public object VSGamePackets {
   public fun register() {
      val `$this$register_u24lambda_u240`: SimplePacketNetworking = vsCore.simplePacketNetworking
      `$this$register_u24lambda_u240`.register1(PacketPlayerDriving::class)
      `$this$register_u24lambda_u240`.register1(PacketRequestPassengerSeat::class)
      `$this$register_u24lambda_u240`.register1(PacketStopChunkUpdates::class)
      `$this$register_u24lambda_u240`.register1(PacketRestartChunkUpdates::class)
      `$this$register_u24lambda_u240`.register1(PacketSyncVSEntityTypes::class)
      `$this$register_u24lambda_u240`.register1(PacketEntityShipMotion::class)
      `$this$register_u24lambda_u240`.register1(PacketMobShipRotation::class)
      `$this$register_u24lambda_u240`.register1(PacketPlayerShipMotion::class)
      `$this$register_u24lambda_u240`.register1(PacketChangeKnownShips::class)
   }

   public fun registerHandlers(): RegisteredListener {
      val `$this$registerHandlers_u24lambda_u2414`: SimplePacketNetworking = vsCore.simplePacketNetworking
      `$this$registerHandlers_u24lambda_u2414`.registerServerHandler1(
         PacketPlayerDriving::class, lambda_14_lambda_2@{ driving: PacketPlayerDriving, iPlayer: VsiPlayer ->
            val var10000: class_1657 = (iPlayer as MinecraftPlayer).player
            val attachment: class_1297 = (var10000 as class_3222).method_5854()
            val var13: ShipMountingEntity = attachment as? ShipMountingEntity
            if ((attachment as? ShipMountingEntity) == null) {
               return@lambda_14_lambda_2 Unit.INSTANCE
            } else {
               if (var13.isController) {
                  val var14: class_1937 = var13.method_37908()
                  val var10001: class_2338 = var13.method_24515()
                  val var6: LoadedShip = VSGameUtilsKt.getLoadedShipManagingPos(var14, var10001 as class_2382)
                  val var15: LoadedServerShip = var6 as? LoadedServerShip
                  if ((var6 as? LoadedServerShip) == null) {
                     return@lambda_14_lambda_2 Unit.INSTANCE
                  }

                  var var16: SeatedControllingPlayer = var15.getAttachment(SeatedControllingPlayer.class)
                  if (var16 == null) {
                     val var10002: class_2350 = var13.method_5735().method_10153()
                     val var11: SeatedControllingPlayer = SeatedControllingPlayer(var10002)
                     var15.setAttachment(var11)
                     var16 = var11
                  }

                  var16.forwardImpulse = driving.impulse.z
                  var16.leftImpulse = driving.impulse.x
                  var16.upImpulse = driving.impulse.y
                  var16.sprintOn = driving.sprint
                  var16.cruise = driving.cruise
               }

               return@lambda_14_lambda_2 Unit.INSTANCE
            }
         }
      )
      `$this$registerHandlers_u24lambda_u2414`.registerServerHandler1(
         PacketRequestPassengerSeat::class,
         { var0: PacketRequestPassengerSeat, iPlayer: VsiPlayer ->
            val var10000: class_1657 = (iPlayer as MinecraftPlayer).player
            val player: class_3222 = var10000 as class_3222
            val var4: class_1937 = (var10000 as class_3222).method_37908()
            (var4 as class_3218)
               .method_8503()
               .execute(
                  { 
                     // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
                     // java.lang.NullPointerException: Cannot invoke "java.util.List.stream()" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
                     //   at org.vineflower.kotlin.expr.KNewExprent.lambda$toJava$0(KNewExprent.java:128)
                     //   at java.base/java.util.stream.ReferencePipeline$7$1FlatMap.accept(ReferencePipeline.java:288)
                     //   at java.base/java.util.ArrayList$ArrayListSpliterator.forEachRemaining(ArrayList.java:1716)
                     //   at java.base/java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:570)
                     //   at java.base/java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:560)
                     //   at java.base/java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:635)
                     //   at java.base/java.util.stream.AbstractPipeline.evaluateToArrayNode(AbstractPipeline.java:291)
                     //   at java.base/java.util.stream.ReferencePipeline.toArray(ReferencePipeline.java:652)
                     //   at java.base/java.util.stream.ReferencePipeline.toArray(ReferencePipeline.java:658)
                     //   at java.base/java.util.stream.ReferencePipeline.toList(ReferencePipeline.java:663)
                     //   at org.vineflower.kotlin.expr.KNewExprent.toJava(KNewExprent.java:131)
                     //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1054)
                     //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1151)
                     //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:921)
                  }
               )
               Unit.INSTANCE
         }
      )
      `$this$registerHandlers_u24lambda_u2414`.registerClientHandler1(PacketSyncVSEntityTypes::class, { syncEntities: PacketSyncVSEntityTypes ->
         val var3: java.util.Iterator = syncEntities.entity2Handler.entrySet().iterator()

         val handler: java.lang.String
         while (true) {
            if (!var3.hasNext()) {
               Unit.INSTANCE
            }

            val var5: Entry = var3.next() as Entry
            val id: Int = (var5.getKey() as java.lang.Number).intValue()
            handler = var5.getValue() as java.lang.String
            val var10000: VSEntityManager = VSEntityManager.INSTANCE
            var var10001: class_1299 = (class_1299)class_7923.field_41177.method_10200(id)
            var10001 = var10001
            val var10002: class_2960 = class_2960.method_12829(handler)
            if (var10002 == null) {
               break
            }

            val var13: VSEntityHandler = VSEntityManager.INSTANCE.getHandler(var10002)
            if (var13 == null) {
               break
            }

            var10000.pair(var10001, var13)
         }

         throw IllegalStateException("No handler: $handler")
      })
      `$this$registerHandlers_u24lambda_u2414`.registerClientHandler1(
         PacketEntityShipMotion::class,
         lambda_14_lambda_9@{ setMotion: PacketEntityShipMotion ->
            val mc: class_310 = class_310.method_1551()
            if (mc.field_1687 == null) {
               return@lambda_14_lambda_9 Unit.INSTANCE
            } else {
               val level: class_638 = mc.field_1687
               val var10000: class_1297 = mc.field_1687.method_8469(setMotion.entityID)
               if (var10000 == null) {
                  return@lambda_14_lambda_9 Unit.INSTANCE
               } else if (!var10000.method_5787() && (mc.field_1724 == null || mc.field_1724.method_5628() != var10000.method_5628())) {
                  if (var10000 is IEntityDraggingInformationProvider) {
                     (var10000 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOnServerWriteOnly = if (setMotion.shipID != -1L)
                        setMotion.shipID
                        else
                        null
                     }

                  val ship: ClientShip = shipObjectWorld.allShips.getById(setMotion.shipID)
                  if (ship == null) {
                     if (var10000 is IEntityDraggingInformationProvider) {
                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn = null
                     }

                     return@lambda_14_lambda_9 Unit.INSTANCE
                  } else {
                     if (var10000 is IEntityDraggingInformationProvider) {
                        run label88@{
                           if ((var10000 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn != null) {
                              val var9: java.lang.Long = (var10000 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn
                              if (var9 != null && var9 == setMotion.shipID) {
                                 return@label88
                              }
                           }

                           (var10000 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn = if (setMotion.shipID != -1L)
                              setMotion.shipID
                              else
                              null
                              (var10000 as IEntityDraggingInformationProvider).draggingInformation.ignoreNextGroundStand = true
                        }

                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.shouldImpulseMovement = false
                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.ticksSinceLastServerPacket = 0
                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.ticksSinceStoodOnShip = 0
                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.relativePositionOnShip = ship.getWorldToShip()
                           .transformPosition(Vector3d(var10000.method_23317(), var10000.method_23318(), var10000.method_23321())) as Vector3dc
                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.previousRelativeVelocityOnShip = (var10000 as IEntityDraggingInformationProvider).draggingInformation.relativeVelocityOnShip
                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.relativeYawOnShip = EntityLerper.INSTANCE
                           .yawToShip(ship, (double)var10000.method_36454())
                           (var10000 as IEntityDraggingInformationProvider).draggingInformation.lerpPositionOnShip = Vector3d(
                           setMotion.x, setMotion.y, setMotion.z
                        ) as Vector3dc
                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.relativeVelocityOnShip = Vector3d(
                           setMotion.xVel, setMotion.yVel, setMotion.zVel
                        ) as Vector3dc
                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.lerpYawOnShip = setMotion.yRot
                        val var8: Vector3d = if ((var10000 as IEntityDraggingInformationProvider).draggingInformation.relativePositionOnShip != null)
                           ship.renderTransform.shipToWorld
                              .transformPosition(Vector3d((var10000 as IEntityDraggingInformationProvider).draggingInformation.relativePositionOnShip))
                           else
                           Vector3d(var10000.method_23317(), var10000.method_23318(), var10000.method_23321())
                           val worldPosition: Vector3d = ship.getTransform().shipToWorld.transformPosition(Vector3d(setMotion.x, setMotion.y, setMotion.z))
                        var10000.method_43391(worldPosition.x, worldPosition.y, worldPosition.z)
                        val worldVelocity: Vector3d = ship.getTransform().shipToWorld
                           .transformDirection(Vector3d(setMotion.xVel, setMotion.yVel, setMotion.zVel))
                           var10000.method_18800(worldVelocity.x, worldVelocity.y, worldVelocity.z)
                        (var10000 as IEntityDraggingInformationProvider).draggingInformation.lerpSteps = 3
                        if (var10000 !is class_1309) {
                           var10000.method_5814(var8.x, var8.y, var8.z)
                           var10000.method_5759(
                              worldPosition.x,
                              worldPosition.y,
                              worldPosition.z,
                              (float)Math.toDegrees(setMotion.yRot),
                              (float)Math.toDegrees(setMotion.xRot),
                              3
                           )
                        }
                     }

                     return@lambda_14_lambda_9 Unit.INSTANCE
                  }
               } else {
                  return@lambda_14_lambda_9 Unit.INSTANCE
               }
            }
         }
      )
      `$this$registerHandlers_u24lambda_u2414`.registerClientHandler1(
         PacketMobShipRotation::class,
         lambda_14_lambda_10@{ setRotation: PacketMobShipRotation ->
            val var10000: class_310 = class_310.method_1551()
            if (var10000 == null) {
               return@lambda_14_lambda_10 Unit.INSTANCE
            } else if (var10000.field_1687 == null) {
               return@lambda_14_lambda_10 Unit.INSTANCE
            } else {
               val level: class_638 = var10000.field_1687
               val var7: class_1297 = var10000.field_1687.method_8469(setRotation.entityID)
               if (var7 == null) {
                  return@lambda_14_lambda_10 Unit.INSTANCE
               } else if (!var7.method_5787() && var7 !is class_746) {
                  if (var7 is IEntityDraggingInformationProvider) {
                     (var7 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOnServerWriteOnly = if (setRotation.shipID != -1L)
                        setRotation.shipID
                        else
                        null
                     }

                  val var8: ClientShip = shipObjectWorld.allShips.getById(setRotation.shipID)
                  if (var8 == null) {
                     return@lambda_14_lambda_10 Unit.INSTANCE
                  } else {
                     if (var7 is IEntityDraggingInformationProvider) {
                        run label68@{
                           if ((var7 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn != null) {
                              val var9: java.lang.Long = (var7 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn
                              if (var9 != null && var9 == setRotation.shipID) {
                                 return@label68
                              }
                           }

                           (var7 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn = if (setRotation.shipID != -1L)
                              setRotation.shipID
                              else
                              null
                              (var7 as IEntityDraggingInformationProvider).draggingInformation.ignoreNextGroundStand = true
                        }

                        (var7 as IEntityDraggingInformationProvider).draggingInformation.ticksSinceStoodOnShip = 0
                        (var7 as IEntityDraggingInformationProvider).draggingInformation.relativeHeadYawOnShip = EntityLerper.INSTANCE
                           .yawToShip(var8, (double)var7.method_5791())
                           (var7 as IEntityDraggingInformationProvider).draggingInformation.lerpHeadYawOnShip = setRotation.yaw
                        (var7 as IEntityDraggingInformationProvider).draggingInformation.relativePitchOnShip = (double)var7.method_36455()
                        (var7 as IEntityDraggingInformationProvider).draggingInformation.lerpPitchOnShip = setRotation.pitch
                        (var7 as IEntityDraggingInformationProvider).draggingInformation.headLerpSteps = 3
                     }

                     return@lambda_14_lambda_10 Unit.INSTANCE
                  }
               } else {
                  return@lambda_14_lambda_10 Unit.INSTANCE
               }
            }
         }
      )
      `$this$registerHandlers_u24lambda_u2414`.registerServerHandler1(
         PacketPlayerShipMotion::class,
         lambda_14_lambda_11@{ motion: PacketPlayerShipMotion, iPlayer: VsiPlayer ->
            val var10000: class_3222 = (iPlayer as MinecraftPlayer).player as class_3222
            if (var10000 == null) {
               return@lambda_14_lambda_11 Unit.INSTANCE
            } else {
               if (var10000 is IEntityDraggingInformationProvider) {
                  run label45@{
                     if ((var10000 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn != null) {
                        val var7: java.lang.Long = (var10000 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn
                        if (var7 != null && var7 == motion.shipID) {
                           return@label45
                        }
                     }

                     (var10000 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn = if (motion.shipID != -1L) motion.shipID else null
                  }

                  (var10000 as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerPosition = Vector3d(motion.x, motion.y, motion.z) as Vector3dc
                  if (var10000.method_37908() != null) {
                     val var8: class_1937 = var10000.method_37908()
                     val ship: ServerShip = shipObjectWorld.allShips.getById(motion.shipID)
                     if (ship != null) {
                        val var9: Vector3d = ship.getShipToWorld().transformPosition(Vector3d(motion.x, motion.y, motion.z), Vector3d())
                        val posUpdate: class_243 = VectorConversionsMCKt.toMinecraft(var9)
                        if ((var10000 as PlayerDuck).vs_handledMovePacket()) {
                           var10000.method_5814(posUpdate.field_1352, posUpdate.field_1351, posUpdate.field_1350)
                           (var10000 as PlayerDuck).vs_setHandledMovePacket(false)
                        } else {
                           (var10000 as PlayerDuck).vs_setQueuedPositionUpdate(posUpdate)
                        }
                     }
                  }

                  (var10000 as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerYaw = motion.yRot
               }

               return@lambda_14_lambda_11 Unit.INSTANCE
            }
         }
      )
      `$this$registerHandlers_u24lambda_u2414`.registerServerHandler1(
         PacketEntityShipMotion::class, lambda_14_lambda_12@{ motion: PacketEntityShipMotion, iPlayer: VsiPlayer ->
            val var10000: class_1657 = (iPlayer as MinecraftPlayer).player
            val player: class_3222 = var10000 as class_3222
            val var6: class_1297 = (var10000 as class_3222).method_37908().method_8469(motion.entityID)
            if (var6 == null) {
               return@lambda_14_lambda_12 Unit.INSTANCE
            } else {
               if (var6 is IEntityDraggingInformationProvider) {
                  run label37@{
                     if ((var6 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn != null) {
                        val var7: java.lang.Long = (var6 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn
                        if (var7 != null && var7 == motion.shipID) {
                           return@label37
                        }
                     }

                     (var6 as IEntityDraggingInformationProvider).draggingInformation.lastShipStoodOn = if (motion.shipID != -1L) motion.shipID else null
                     (var6 as IEntityDraggingInformationProvider).draggingInformation.ignoreNextGroundStand = true
                  }

                  (var6 as IEntityDraggingInformationProvider).draggingInformation.relativePositionOnShip = Vector3d(motion.x, motion.y, motion.z) as Vector3dc
                  (var6 as IEntityDraggingInformationProvider).draggingInformation.relativeYawOnShip = motion.yRot
                  val var8: class_1937 = player.method_37908()
                  if (shipObjectWorld.allShips.getById(motion.shipID) != null) {
                     var6.method_33574(VSGameUtilsKt.toWorldCoordinates(player.method_37908(), class_243(motion.x, motion.y, motion.z)))
                  }
               }

               return@lambda_14_lambda_12 Unit.INSTANCE
            }
         }
      )
      return `$this$registerHandlers_u24lambda_u2414`.registerServerHandler1(
         PacketChangeKnownShips::class, lambda_14_lambda_13@{ ships: PacketChangeKnownShips, iPlayer: VsiPlayer ->
            val var10000: class_1657 = (iPlayer as MinecraftPlayer).player
            val player: PlayerKnownShipsDuck = var10000 as PlayerKnownShipsDuck
            if (ships.add) {
               run label33@{
                  val var3: ShipWorld = shipWorld
                  if (var3 != null) {
                     val var4: QueryableShipData = var3.loadedShips
                     if (var4 != null) {
                        var5 = var4.contains(ships.shipID)
                        return@label33
                     }
                  }

                  var5 = false
               }

               if (!var5) {
                  return@lambda_14_lambda_13 Unit.INSTANCE
               }
            }

            if (ships.add) {
               player.vs_addKnownShip(ships.shipID)
            } else {
               player.vs_removeKnownShip(ships.shipID)
            }

            return@lambda_14_lambda_13 Unit.INSTANCE
         }
      )
   }
}
