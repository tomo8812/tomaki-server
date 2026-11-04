package org.valkyrienskies.mod.common.entity

import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1297
import net.minecraft.class_1299
import net.minecraft.class_1309
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2374
import net.minecraft.class_2382
import net.minecraft.class_243
import net.minecraft.class_2487
import net.minecraft.class_2561
import net.minecraft.class_2596
import net.minecraft.class_2602
import net.minecraft.class_2604
import net.minecraft.class_2940
import net.minecraft.class_2943
import net.minecraft.class_2945
import net.minecraft.class_310
import net.minecraft.class_315
import net.minecraft.class_3218
import net.minecraft.class_3222
import net.minecraft.class_3231
import net.minecraft.class_5250
import net.minecraft.class_1297.class_5529
import net.minecraft.class_2945.class_9222
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.Vector3f
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.LoadedShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.config.VSKeyBindings
import org.valkyrienskies.mod.common.networking.PacketPlayerDriving
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt

@SourceDebugExtension(["SMAP\nShipMountingEntity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipMountingEntity.kt\norg/valkyrienskies/mod/common/entity/ShipMountingEntity\n+ 2 LoadedServerShip.kt\norg/valkyrienskies/core/api/ships/LoadedServerShipKt\n+ 3 AttachmentHolder.kt\norg/valkyrienskies/core/api/attachment/AttachmentHolderKt\n*L\n1#1,318:1\n46#2:319\n80#3:320\n*S KotlinDebug\n*F\n+ 1 ShipMountingEntity.kt\norg/valkyrienskies/mod/common/entity/ShipMountingEntity\n*L\n237#1:319\n237#1:320\n*E\n"])
public open class ShipMountingEntity(type: class_1299<ShipMountingEntity>, level: class_1937) : class_1297(type, level), ShipMountedToDataProvider {
   public final var isController: Boolean
   public final var driveShipId: Long?
   public final var driveRelPos: Vector3d?
   public final var fadePrompt: Boolean
   private final var promptSent: Boolean

   init {
      this.field_23807 = false
      this.field_5960 = true
   }

   private fun serverSeatTick(lvl: class_3218) {
      val var10000: java.util.List = this.method_5685()
      val var7: class_1297 = CollectionsKt.firstOrNull(var10000) as class_1297
      if (var7 != null) {
         val shipId: java.lang.Long = this.driveShipId
         val rel: Vector3d = this.driveRelPos
         if (this.driveShipId != null && this.driveRelPos != null) {
            val ship: ServerShip = shipObjectWorld.allShips.getById(shipId)
            if (ship == null) {
               this.standRider(lvl, var7, null)
            } else {
               val world: Vector3d = ship.getShipToWorld().transformPosition(Vector3d(rel))
               this.method_5808(world.x, world.y, world.z, this.method_36454(), this.method_36455())
               if (!this.fadePrompt) {
                  val var8: class_3222 = var7 as? class_3222
                  if ((var7 as? class_3222) != null) {
                     var8.method_7353(SEATED_PROMPT, true)
                  }
               } else if (!this.promptSent) {
                  this.promptSent = true
                  val var9: class_3222 = var7 as? class_3222
                  if ((var7 as? class_3222) != null) {
                     var9.method_7353(SEATED_PROMPT, true)
                  }
               }
            }
         }
      }
   }

   private fun standRider(lvl: class_3218, rider: class_1297, ship: Ship?) {
      val rel: Vector3d = this.driveRelPos
      val target: Vector3d = if (ship != null && this.driveRelPos != null) ship.shipToWorld.transformPosition(Vector3d(rel)) else null
      rider.method_5848()
      if (target != null && rider is class_3222) {
         (rider as class_3222).method_5859(target.x, target.y, target.z)
      }

      rider.method_18799(class_243(rider.method_18798().field_1352, 0.0, rider.method_18798().field_1350))
      if (ship != null && rel != null && rider is IEntityDraggingInformationProvider) {
         (rider as IEntityDraggingInformationProvider).draggingInformation.serverRelativePlayerPosition = Vector3d(rel) as Vector3dc
         (rider as IEntityDraggingInformationProvider).vs$dragImmediately(ship)
      }

      rider.method_38785()
      this.method_5768()
   }

   private fun parseDriveData(): Pair<Long, Vector3d>? {
      val data: java.lang.String = this.field_6011.method_12789(DRIVE_DATA) as java.lang.String
      if (data.length() == 0) {
         return null
      } else {
         val parts: java.util.List = StringsKt.split$default(data, charArrayOf(';'), false, 0, 6, null)
         if (parts.size() < 4) {
            return null
         } else {
            val var12: java.lang.Long = StringsKt.toLongOrNull(parts.get(0) as java.lang.String)
            if (var12 != null) {
               val var11: Long = var12
               val var13: java.lang.Double = StringsKt.toDoubleOrNull(parts.get(1) as java.lang.String)
               if (var13 != null) {
                  val x: Double = var13
                  val var14: java.lang.Double = StringsKt.toDoubleOrNull(parts.get(2) as java.lang.String)
                  label42@
                  if (var14 != null) {
                     val y: Double = var14
                     val var15: java.lang.Double = StringsKt.toDoubleOrNull(parts.get(3) as java.lang.String)
                     return if (var15 != null) TuplesKt.to(var11, Vector3d(x, y, var15)) else null
                  } else {
                     return null
                  }
               } else {
                  return null
               }
            } else {
               return null
            }
         }
      }
   }

   private fun clientSelfDrive() {
      val var10000: Pair = this.parseDriveData()
      if (var10000 != null) {
         val var4: Ship = shipObjectWorld.allShips.getById((var10000.getFirst() as java.lang.Number).longValue())
         if (var4 != null) {
            val world: Vector3d = var4.transform.shipToWorld.transformPosition(var10000.getSecond() as Vector3d)
            this.method_5814(world.x, world.y, world.z)
         }
      }
   }

   open fun method_24829(livingEntity: class_1309): class_243 {
      var var10000: class_1937 = this.method_37908()
      var var10001: class_243 = this.method_19538()
      if (VSGameUtilsKt.isBlockInShipyard(var10000, var10001)) {
         var10000 = this.method_37908()
         var10001 = this.method_19538()
         if (VSGameUtilsKt.getShipManagingPos(var10000, var10001 as class_2374) == null) {
            val var4: class_243 = livingEntity.method_19538()
            var4
         }
      }

      val var3: class_243 = super.method_24829(livingEntity)
      var3
   }

   open fun method_52538(entity: class_1297): class_243 {
      val var10000: class_243 = this.method_19538()
      var10000
   }

   open fun method_5749(compound: class_2487) {
   }

   open fun method_5652(compound: class_2487) {
   }

   open fun method_31746(): Boolean {
      !this.vs$isPassengerSeat() && super.method_31746()
   }

   open fun method_5693(builder: class_9222) {
      builder.method_56912(IS_PASSENGER_SEAT, false)
      builder.method_56912(DRIVE_DATA, "")
   }

   public fun `vs$isPassengerSeat`(): Boolean {
      val var10000: Any = this.field_6011.method_12789(IS_PASSENGER_SEAT)
      return var10000 as java.lang.Boolean
   }

   public override fun provideShipMountedToData(passenger: class_1297, partialTicks: Float?): ShipMountedToData? {
      val drive: Pair = this.parseDriveData()
      label28@
      if (drive != null) {
         val var6: Ship = shipObjectWorld.allShips.getById((drive.getFirst() as java.lang.Number).longValue())
         val var9: LoadedShip = var6 as? LoadedShip
         if ((var6 as? LoadedShip) == null) {
            return null
         } else {
            val rel: Vector3d = drive.getSecond() as Vector3d
            return ShipMountedToData(var9, Vector3d(rel.x, rel.y - 0.5, rel.z))
         }
      } else {
         val var10000: class_1937 = this.method_37908()
         val var10001: class_243 = this.method_19538()
         val var8: LoadedShip = VSGameUtilsKt.getLoadedShipManagingPos(var10000, VectorConversionsMCKt.toJOML(var10001))
         return if (var8 == null) null else ShipMountedToData(var8, VectorConversionsMCKt.toJOML(this.method_52538(passenger)))
      }
   }

   open fun method_5650(removalReason: class_5529) {
      if (this.isController && !this.method_37908().field_9236) {
         val var10000: class_1937 = this.method_37908()
         val var10001: class_2338 = this.method_24515()
         val var2: LoadedServerShip = VSGameUtilsKt.getLoadedShipManagingPos(var10000, var10001 as class_2382) as LoadedServerShip
         if (var2 != null) {
            val var8: SeatedControllingPlayer = var2.removeAttachment(SeatedControllingPlayer.class)
         }
      }

      super.method_5650(removalReason)
   }

   private fun sendDrivingPacket() {
      if (this.method_37908().field_9236) {
         val opts: class_315 = class_310.method_1551().field_1690
         val forward: Boolean = opts.field_1894.method_1434()
         val backward: Boolean = opts.field_1881.method_1434()
         val left: Boolean = opts.field_1913.method_1434()
         val right: Boolean = opts.field_1849.method_1434()
         val up: Boolean = opts.field_1903.method_1434()
         val var10000: class_1309 = this.method_5642()
         val sprint: Boolean = var10000 != null && var10000.method_5624()
         val down: Boolean = VSKeyBindings.INSTANCE.shipDown.get().method_1434()
         val cruise: Boolean = VSKeyBindings.INSTANCE.shipCruise.get().method_1434()
         val impulse: Vector3f = Vector3f()
         impulse.z = if (forward == backward) 0.0F else (if (forward) 1.0F else -1.0F)
         impulse.x = if (left == right) 0.0F else (if (left) 1.0F else -1.0F)
         impulse.y = if (up == down) 0.0F else (if (up) 1.0F else -1.0F)
         vsCore.simplePacketNetworking.sendToServer1(PacketPlayerDriving(impulse, sprint, cruise))
      }
   }

   open fun method_5642(): class_1309? {
      val var2: class_1309
      if (this.isController) {
         val var10000: java.util.List = this.method_5685()
         val var1: Any = CollectionsKt.getOrNull(var10000, 0)
         var2 = var1 as? class_1309
      } else {
         var2 = null
      }

      var2
   }

   open fun method_18002(serverEntity: class_3231): class_2596<class_2602> {
      class_2604(this, serverEntity) as class_2596
   }

   @JvmStatic
   @JvmSynthetic
   fun `access$getEntityData$p`(`$this`: ShipMountingEntity): class_2945 {
      `$this`.field_6011
   }

   @JvmStatic
   fun {
      var var10000: class_2940 = class_2945.method_12791(ShipMountingEntity.class, class_2943.field_13323)
      IS_PASSENGER_SEAT = var10000
      var10000 = class_2945.method_12791(ShipMountingEntity.class, class_2943.field_13326)
      DRIVE_DATA = var10000
      val var1: class_5250 = class_2561.method_43470("You Are Seated: Press SHIFT to stand")
      SEATED_PROMPT = var1 as class_2561
   }

   public companion object {
      private final val IS_PASSENGER_SEAT: class_2940<Boolean>
      private final val DRIVE_DATA: class_2940<String>
      private final val SEATED_PROMPT: class_2561
      private const val SEAT_RENDER_DROP: Double = 0.5

      @JvmOverloads
      public fun spawnPassengerSeat(
         level: class_3218,
         worldX: Double,
         worldY: Double,
         worldZ: Double,
         yaw: Float,
         pitch: Float,
         shipId: Long,
         relX: Double,
         relY: Double,
         relZ: Double,
         fadePrompt: Boolean = false
      ): ShipMountingEntity? {
         val var10000: ShipMountingEntity = ValkyrienSkiesMod.INSTANCE.SHIP_MOUNTING_ENTITY_TYPE.method_5883(level as class_1937) as ShipMountingEntity
         if (var10000 == null) {
            return null
         } else {
            var10000.method_5808(worldX, worldY, worldZ, yaw, pitch)
            var10000.isController = false
            var10000.driveShipId = shipId
            var10000.driveRelPos = Vector3d(relX, relY, relZ)
            var10000.fadePrompt = fadePrompt
            ShipMountingEntity.access$getEntityData$p(var10000).method_12778(ShipMountingEntity.IS_PASSENGER_SEAT, true)
            ShipMountingEntity.access$getEntityData$p(var10000).method_12778(ShipMountingEntity.DRIVE_DATA, "$shipId;$relX;$relY;$relZ")
            level.method_8649(var10000)
            return var10000
         }
      }

      @JvmOverloads
      fun spawnPassengerSeat(
         level: class_3218, worldX: Double, worldY: Double, worldZ: Double, yaw: Float, pitch: Float, shipId: Long, relX: Double, relY: Double, relZ: Double
      ): ShipMountingEntity {
         spawnPassengerSeat$default(this, level, worldX, worldY, worldZ, yaw, pitch, shipId, relX, relY, relZ, false, 1024, null)
      }
   }
}
