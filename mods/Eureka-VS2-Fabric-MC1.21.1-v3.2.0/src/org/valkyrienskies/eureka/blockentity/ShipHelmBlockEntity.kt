package org.valkyrienskies.eureka.blockentity

import java.util.ArrayList
import java.util.HashSet
import java.util.UUID
import kotlin.jvm.internal.PropertyReference1
import kotlin.jvm.internal.PropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import kotlin.math.MathKt
import kotlin.reflect.KProperty
import net.minecraft.class_1297
import net.minecraft.class_1657
import net.minecraft.class_1661
import net.minecraft.class_1703
import net.minecraft.class_1922
import net.minecraft.class_1937
import net.minecraft.class_2248
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_238
import net.minecraft.class_2382
import net.minecraft.class_2383
import net.minecraft.class_2510
import net.minecraft.class_2561
import net.minecraft.class_2586
import net.minecraft.class_2596
import net.minecraft.class_2604
import net.minecraft.class_265
import net.minecraft.class_2680
import net.minecraft.class_2739
import net.minecraft.class_2741
import net.minecraft.class_2752
import net.minecraft.class_2760
import net.minecraft.class_2769
import net.minecraft.class_3218
import net.minecraft.class_3222
import net.minecraft.class_3486
import net.minecraft.class_3908
import net.minecraft.class_5250
import net.minecraft.class_7923
import net.minecraft.class_2183.class_2184
import net.minecraft.class_2338.class_2339
import net.minecraft.class_2350.class_2351
import net.minecraft.server.MinecraftServer
import org.apache.logging.log4j.Logger
import org.joml.Matrix4dc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.joml.primitives.AABBdc
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.properties.ShipInertiaData
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaConfigLoader
import org.valkyrienskies.eureka.block.AnchorBlock
import org.valkyrienskies.eureka.block.BalloonBlock
import org.valkyrienskies.eureka.block.EngineBlock
import org.valkyrienskies.eureka.block.FloaterBlock
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.command.AssemblerPreferences
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.EurekaAssembler
import org.valkyrienskies.eureka.util.ShipAssembler
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.util.ShipSettings
import org.valkyrienskies.mod.common.util.ShipSettingsKt
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt
import org.valkyrienskies.mod.util.ClassLogger
import org.valkyrienskies.mod.util.LoggingKt

@SourceDebugExtension(["SMAP\nShipHelmBlockEntity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipHelmBlockEntity.kt\norg/valkyrienskies/eureka/blockentity/ShipHelmBlockEntity\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,562:1\n1#2:563\n*E\n"])
public class ShipHelmBlockEntity(pos: class_2338, state: class_2680) : class_2586(EurekaBlockEntities.INSTANCE.SHIP_HELM.get(), pos, state), class_3908 {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.property1(PropertyReference1Impl(ShipHelmBlockEntity.class, "logger", "getLogger()Lorg/apache/logging/log4j/Logger;", 0) as PropertyReference1)
   );
   private final val seats: MutableList<ShipMountingEntity> = ArrayList() as java.util.List
   private final var shouldDisassembleWhenPossible: Boolean
   private final var orphanScanCooldown: Int

   private final val logger: Logger by LoggingKt.logger().provideDelegate_j8jxBIw/* $VF was: provideDelegate-j8jxBIw */(this, $$delegatedProperties[0])
      private final get() {
         return ClassLogger.getValue_impl/* $VF was: getValue-impl */(this.logger$delegate, this, $$delegatedProperties[0])
      }


   private final val ship: LoadedServerShip?
      private final get() {
         val var10000: class_1937 = this.field_11863
         val var1: class_3218 = var10000 as class_3218
         val var10001: class_2338 = this.method_11016()
         return VSGameUtilsKt.getLoadedShipManagingPos(var1, var10001 as class_2382)
      }


   private final val control: EurekaShipControl?
      private final get() {
         val var10000: LoadedServerShip = this.ship
         return if (var10000 != null) var10000.getAttachment(EurekaShipControl.class) as EurekaShipControl else null
      }


   public final val keepActive: Boolean
      public final get() {
         val var10000: LoadedServerShip = this.ship
         if (var10000 != null) {
            val var1: ShipSettings = ShipSettingsKt.getSettings(var10000)
            if (var1 != null) {
               return var1.getKeepActive()
            }
         }

         return false
      }


   public fun setKeepActive(value: Boolean) {
      val var10000: LoadedServerShip = this.ship
      if (var10000 != null) {
         ShipSettingsKt.getSettings(var10000).setKeepActive(value)
      }
   }

   public final val assembledBlockCount: Int
      public final get() {
         val var10000: EurekaShipControl = this.control
         return if (var10000 != null) var10000.assembledBlocks else 0
      }


   public final val estimatedTopSpeed: Int
      public final get() {
         val var10000: EurekaShipControl = this.control
         return (int)Math.ceil(if (var10000 != null) var10000.estimateTopSpeed() else 0.0)
      }


   public final val shipMass: Int
      public final get() {
         val var10000: LoadedServerShip = this.ship
         if (var10000 != null) {
            val var1: ShipInertiaData = var10000.getInertiaData()
            if (var1 != null) {
               return MathKt.roundToInt(var1.getMass())
            }
         }

         return MathKt.roundToInt(0.0)
      }


   public final val waterAltitudeHold: Boolean
      public final get() {
         return EurekaConfig.SERVER.enableWaterAltitudeHold
      }


   public fun toggleWaterAltitudeHold() {
      EurekaConfig.SERVER.enableWaterAltitudeHold = !EurekaConfig.SERVER.enableWaterAltitudeHold
      EurekaConfigLoader.save()
   }

   public final val vanillaControls: Boolean
      public final get() {
         val var10000: EurekaShipControl = this.control
         return var10000 != null && var10000.vanillaControls
      }


   public fun toggleVanillaControls() {
      val var10000: EurekaShipControl = this.control
      if (var10000 != null) {
         var10000.vanillaControls = !var10000.vanillaControls
         var10000.cancelCruiseForModeSwitch()
      }
   }

   public fun setVanillaControls(value: Boolean) {
      val var10000: EurekaShipControl = this.control
      if (var10000 != null) {
         if (var10000.vanillaControls == value) {
            return
         }

         var10000.vanillaControls = value
         var10000.cancelCruiseForModeSwitch()
      }
   }

   public final val engineCount: Int
      public final get() {
         val var10000: EurekaShipControl = this.control
         return if (var10000 != null) var10000.engines else 0
      }


   public final val enginePowerPercent: Int
      public final get() {
         val var10000: EurekaShipControl = this.control
         return if (var10000 != null) var10000.engineFuelPercent else 0
      }


   public final val cruising: Boolean
      public final get() {
         val var10000: EurekaShipControl = this.control
         return var10000 != null && var10000.isCruising
      }


   public final val cruiseSpeedArmed: Boolean
      public final get() {
         val var10000: EurekaShipControl = this.control
         return var10000 != null && var10000.cruiseHorizontalArmed
      }


   public final val cruiseTurnArmed: Boolean
      public final get() {
         val var10000: EurekaShipControl = this.control
         return var10000 != null && var10000.cruiseTurnArmed
      }


   public final val cruiseVerticalArmed: Boolean
      public final get() {
         val var10000: EurekaShipControl = this.control
         return var10000 != null && var10000.cruiseVerticalArmed
      }


   public final val cruiseSpeedHundredths: Int
      public final get() {
         val var10000: EurekaShipControl = this.control
         return MathKt.roundToInt((if (var10000 != null) var10000.cruiseSpeedMps() else 0.0) * 100.0)
      }


   public final val cruiseTurnHundredths: Int
      public final get() {
         val var10000: EurekaShipControl = this.control
         return MathKt.roundToInt((if (var10000 != null) var10000.cruiseTurnDegPerSec() else 0.0) * 100.0)
      }


   public final val cruiseVerticalHundredths: Int
      public final get() {
         val var10000: EurekaShipControl = this.control
         return MathKt.roundToInt((if (var10000 != null) var10000.cruiseVerticalMps() else 0.0) * 100.0)
      }


   private final val helmSeatDirection: class_2350
      private final get() {
         val var10000: class_2350 = (this.method_11010().method_11654(class_2741.field_12481 as class_2769) as class_2350).method_10153()
         return var10000
      }


   public fun setCruise(enable: Boolean) {
      val var10000: EurekaShipControl = this.control
      if (var10000 != null) {
         var10000.setCruiseFromMenu(enable, this.helmSeatDirection)
      }
   }

   public fun setCruiseAxis(axis: Int, armed: Boolean) {
      val var10000: EurekaShipControl = this.control
      if (var10000 != null) {
         var10000.setCruiseAxisArmed(axis, armed, this.helmSeatDirection)
      }
   }

   public fun setCruiseValue(axis: Int, value: Double) {
      val var10000: EurekaShipControl = this.control
      if (var10000 != null) {
         var10000.setCruiseValueMenu(axis, value, this.helmSeatDirection)
      }
   }

   public final val assembled: Boolean
      public final get() {
         return this.ship != null
      }


   public final val aligning: Boolean
      public final get() {
         val var10000: EurekaShipControl = this.control
         return var10000 != null && var10000.aligning
      }


   public open fun createMenu(id: Int, playerInventory: class_1661, player: class_1657): class_1703 {
      return ShipHelmScreenMenu(id, playerInventory, this)
   }

   fun method_5476(): class_2561 {
      val var10000: class_5250 = class_2561.method_43471("gui.vs_eureka.ship_helm")
      var10000 as class_2561
   }

   public fun spawnSeat(blockPos: class_2338, state: class_2680, level: class_3218): ShipMountingEntity {
      val newPos: class_2338 = blockPos.method_10093(state.method_11654(class_2383.field_11177 as class_2769) as class_2350)
      val newState: class_2680 = level.method_8320(newPos)
      val newShape: class_265 = newState.method_26218(level as class_1922, newPos)
      val newBlock: class_2248 = newState.method_26204()
      var height: Double = 0.0
      height = 0.5
      if (!newState.method_26215()) {
         height = if (newBlock !is class_2510
               || newState.method_28498(class_2510.field_11572 as class_2769)
                  && newState.method_11654(class_2510.field_11572 as class_2769) != class_2760.field_12617)
            newShape.method_1105(class_2351.field_11052)
            else
            0.5
         }

      var var10002: class_2338 = blockPos.method_10074()
      val deckTopY: java.lang.Double = this.floorTopWorldY(level, var10002)
      var10002 = newPos.method_10074()
      val frontFloorTopY: java.lang.Double = this.floorTopWorldY(level, var10002)
      val standDrop: Double = if (deckTopY != null && frontFloorTopY != null) RangesKt.coerceIn(deckTopY - frontFloorTopY, 0.0, 0.5) else 0.0
      val var10000: class_1297 = ValkyrienSkiesMod.INSTANCE.getSHIP_MOUNTING_ENTITY_TYPE().method_5883(level as class_1937)
      val `$this$spawnSeat_u24lambda_u243`: ShipMountingEntity = var10000 as ShipMountingEntity
      val seatEntityPos: Vector3dc = Vector3d(newPos.method_10263() + 0.5, newPos.method_10264() - 0.5 + height - standDrop, newPos.method_10260() + 0.5) as Vector3dc
      `$this$spawnSeat_u24lambda_u243`.method_24203(seatEntityPos.x(), seatEntityPos.y(), seatEntityPos.z())
      val var10001: class_2184 = class_2184.field_9851
      val var21: class_2382 = (state.method_11654(class_2741.field_12481 as class_2769) as class_2350).method_10163()
      `$this$spawnSeat_u24lambda_u243`.method_5702(
         var10001, VectorConversionsMCKt.toDoubles(var21).method_1019(`$this$spawnSeat_u24lambda_u243`.method_19538())
      )
      `$this$spawnSeat_u24lambda_u243`.setController(true)
      val entity: ShipMountingEntity = var10000 as ShipMountingEntity
      level.method_30771((var10000 as ShipMountingEntity) as class_1297)
      return entity
   }

   private fun floorTopWorldY(level: class_3218, pos: class_2338): Double? {
      val state: class_2680 = level.method_8320(pos)
      if (state.method_26215()) {
         return null
      } else {
         val shape: class_265 = state.method_26218(level as class_1922, pos)
         return if (shape.method_1110()) null else (double)pos.method_10264() + shape.method_1105(class_2351.field_11052)
      }
   }

   private fun sampleKeelInWater(level: class_3218, ship: LoadedServerShip): Boolean {
      val aabb: AABBdc = ship.getWorldAABB()
      val keelY: Int = (int)Math.floor(aabb.minY())
      val minX: Int = (int)Math.floor(aabb.minX())
      val maxX: Int = (int)Math.floor(aabb.maxX())
      val minZ: Int = (int)Math.floor(aabb.minZ())
      val maxZ: Int = (int)Math.floor(aabb.maxZ())
      val stepX: Int = Math.max(1, (maxX - minX) / 5)
      val stepZ: Int = Math.max(1, (maxZ - minZ) / 5)
      val pos: class_2339 = class_2339()

      // $VF: Unable to resugar Kotlin loop from Java for loop
      var x: Int = minX
      while (true) {
         if (x <= maxX) break
         // $VF: Unable to resugar Kotlin loop from Java for loop
         var z: Int = minZ
         while (true) {
            if (z <= maxZ) break
            pos.method_10103(x, keelY, z)
            if (level.method_22340(pos as class_2338) && level.method_8316(pos as class_2338).method_15767(class_3486.field_15517)) {
               return true
            }

            z += stepZ
         }

         x += stepX
      }

      return false
   }

   public fun startRiding(player: class_1657, force: Boolean, blockPos: class_2338, state: class_2680, level: class_3218): Boolean {
      for (seat in this.seats.size() - 1 downTo 0) {
         if (!this.seats.get(seat).method_5782()) {
            this.seats.get(seat).method_5768()
            this.seats.remove(seat)
         } else if (!this.seats.get(seat).method_5805()) {
            this.seats.remove(seat)
         }
      }

      val var10002: class_2680 = this.method_11010()
      val var11: ShipMountingEntity = this.spawnSeat(blockPos, var10002, level)
      val ride: Boolean = player.method_5873(var11 as class_1297, force)
      if (ride) {
         val var10000: EurekaShipControl = this.control
         if (var10000 != null) {
            var10000.seatedPlayer = player
         }

         this.seats.add(var11)
         if (player is class_3222) {
            (player as class_3222)
               .field_13987
               .method_14364(
                  class_2604(
                     var11.method_5628(),
                     var11.method_5667(),
                     var11.method_23317(),
                     var11.method_23318(),
                     var11.method_23321(),
                     var11.method_36455(),
                     var11.method_36454(),
                     var11.method_5864(),
                     0,
                     var11.method_18798(),
                     (double)var11.method_5791()
                  ) as class_2596
               )
               val var12: java.util.List = var11.method_5841().method_46357()
            if (var12 != null) {
               (player as class_3222).field_13987.method_14364(class_2739(var11.method_5628(), var12) as class_2596)
            }

            (player as class_3222).field_13987.method_14364(class_2752(var11 as class_1297) as class_2596)
         }
      }

      return ride
   }

   public fun tick() {
      val curShip: LoadedServerShip = this.ship
      val curControl: EurekaShipControl = if (curShip != null) curShip.getAttachment(EurekaShipControl.class) as EurekaShipControl else null
      if (this.shouldDisassembleWhenPossible && curControl != null && curControl.canDisassemble) {
         this.disassemble()
      }

      if (curControl != null) {
         curControl.ship = curShip
      }

      val sLevel: class_1937 = this.field_11863
      if (curControl != null && curShip != null && this.field_11863 is class_3218) {
         curControl.keelInWater = this.sampleKeelInWater(this.field_11863 as class_3218, curShip)
         val lvl: SeatedControllingPlayer = curShip.getAttachment(SeatedControllingPlayer.class) as SeatedControllingPlayer
         curControl.updateInputHolds(
            (sLevel as class_3218).method_8510(),
            if (lvl != null) lvl.getForwardImpulse() else 0.0F,
            if (lvl != null) lvl.getLeftImpulse() else 0.0F,
            if (lvl != null) lvl.getUpImpulse() else 0.0F
         )
      }

      val var9: class_1937 = this.field_11863
      if (this.field_11863 is class_3218) {
         if (this.seats.isEmpty()) {
            this.orphanScanCooldown += -1
            if (this.orphanScanCooldown <= 0) {
               this.orphanScanCooldown = 20
               val iter: class_238 = class_238(
                     this.method_11016().method_10093(this.method_11010().method_11654(class_2741.field_12481 as class_2769) as class_2350)
                  )
                  .method_1014(0.5)

               for (rider in (var9 as class_3218).method_18456()) {
                  val worldPos: class_1297 = rider.method_5854()
                  if (worldPos is ShipMountingEntity
                     && (worldPos as ShipMountingEntity).method_5805()
                     && iter.method_1008(
                        (worldPos as ShipMountingEntity).method_23317(),
                        (worldPos as ShipMountingEntity).method_23318(),
                        (worldPos as ShipMountingEntity).method_23321()
                     )) {
                     (worldPos as ShipMountingEntity).setController(true)
                     this.seats.add(worldPos)
                  }
               }
            }
         }

         val var10: java.util.Iterator = this.seats.iterator()

         while (var10.hasNext()) {
            val var11: ShipMountingEntity = var10.next() as ShipMountingEntity
            val var10000: java.util.List = var11.method_5685()
            val var12: class_1297 = CollectionsKt.firstOrNull(var10000) as class_1297
            if (var12 == null || !var11.method_5805()) {
               var11.method_5768()
               var10.remove()
            } else if (var12 is class_1657 && (var12 as class_1657).method_5715()) {
               (var12 as class_1657).method_5848()
               if (curShip != null && var12 is class_3222) {
                  val var14: Vector3d = curShip.getShipToWorld().transformPosition(Vector3d(var11.method_23317(), var11.method_23318(), var11.method_23321()))
                  (var12 as class_3222).method_5859(var14.x, var14.y, var14.z)
               }

               var11.method_5768()
               var10.remove()
            } else if (curShip != null) {
               val var13: Vector3d = curShip.getShipToWorld().transformPosition(Vector3d(var11.method_23317(), var11.method_23318(), var11.method_23321()))
               var12.method_5808(var13.x, var13.y, var13.z, var12.method_36454(), var12.method_36455())
            }
         }
      }
   }

   public fun assemble(player: class_1657) {
      val var10000: class_1937 = this.field_11863
      val level: class_3218 = var10000 as class_3218
      if ((var10000 as class_3218).method_8320(this.method_11016()).method_26204() is ShipHelmBlock) {
         val helmCount: IntRef = IntRef()
         val balloonCount: IntRef = IntRef()
         val floaterCount: IntRef = IntRef()
         val anchorCount: IntRef = IntRef()
         val activeAnchorCount: IntRef = IntRef()
         val engineCount: IntRef = IntRef()
         val blockCount: IntRef = IntRef()
         val var19: ShipAssembler = ShipAssembler.INSTANCE
         val var10002: class_2338 = this.method_11016()
         val blockPositions: HashSet = var19.collectBlockPositions(
            level,
            var10002,
            { it: class_2680 ->
               val allowed: Boolean = !it.method_26215()
                  && !it.method_26164(ASSEMBLE_BLACKLIST)
                  && (
                     EurekaConfig.SERVER.blockBlacklist.isEmpty()
                        || !EurekaConfig.SERVER.blockBlacklist.contains(class_7923.field_41175.method_10221(it.method_26204()).toString())
                  )
                  if (allowed) {
                  val var9: Int = `$blockCount`.element++
                  val var11: class_2248 = it.method_26204()
                  if (var11 is ShipHelmBlock) {
                     val var10: Int = `$helmCount`.element++
                  } else if (var11 is BalloonBlock) {
                     val var12: Int = `$balloonCount`.element++
                  } else if (var11 is FloaterBlock) {
                     val var10001: Int = `$floaterCount`.element
                     val var10003: java.lang.Comparable = it.method_11654(class_2741.field_12511 as class_2769)
                     `$floaterCount`.element = var10001 + (15 - (var10003 as java.lang.Number).intValue())
                  } else if (var11 is EngineBlock) {
                     val var13: Int = `$engineCount`.element++
                  } else if (var11 is AnchorBlock) {
                     var var14: Int = `$anchorCount`.element++
                     if (it.method_11654(class_2741.field_12484 as class_2769) as java.lang.Boolean) {
                        var14 = `$activeAnchorCount`.element++
                     }
                  }
               }

               allowed
            }
         )
         if (blockPositions == null) {
            player.method_7353(class_2561.method_43469("gui.vs_eureka.too_big", arrayOf(EurekaConfig.SERVER.maxShipBlocks)) as class_2561, true)
            this.logger.warn("Failed to assemble to large of a ship for ${player.method_5477().getString()}")
         } else {
            val var20: AssemblerPreferences = AssemblerPreferences.INSTANCE
            var var10001: UUID = player.method_5667()
            val prefs: AssemblerPreferences.Prefs = var20.get(var10001)
            if (prefs.enabled && (prefs.floater || prefs.balloon)) {
               val builtShip: EurekaAssembler.Outcome = EurekaAssembler.INSTANCE
                  .apply(
                     level,
                     player,
                     blockPositions,
                     prefs.floater,
                     prefs.balloon,
                     floaterCount.element / 15,
                     balloonCount.element,
                     prefs.floaterBonusPercent,
                     prefs.balloonBonusPercent
                  )
                  if (builtShip is EurekaAssembler.Cancelled) {
                  player.method_7353((builtShip as EurekaAssembler.Cancelled).message, false)
                  return
               }

               if (builtShip !is EurekaAssembler.Applied) {
                  throw NoWhenBranchMatchedException()
               }

               floaterCount.element = floaterCount.element + (builtShip as EurekaAssembler.Applied).floatersPlaced * 15
               balloonCount.element = balloonCount.element + (builtShip as EurekaAssembler.Applied).balloonsPlaced
               val var21: AssemblerPreferences = AssemblerPreferences.INSTANCE
               var10001 = player.method_5667()
               var21.clearBonuses(var10001)
            }

            val shipId: Long = ShipAssembler.INSTANCE.finishAssembly(level, blockPositions).getId()
            val loaded: LoadedServerShip = VSGameUtilsKt.getShipObjectWorld(level).getLoadedShips().getById(shipId) as LoadedServerShip
            if (loaded != null) {
               assemble$applyControl(helmCount, balloonCount, floaterCount, anchorCount, activeAnchorCount, engineCount, blockCount, loaded)
            } else {
               val var22: MinecraftServer = level.method_8503()
               VSGameUtilsKt.executeIf(
                  var22,
                  { 
                     VSGameUtilsKt.getShipObjectWorld(`$level`).getLoadedShips().getById(`$shipId`) != null
                  },
                  { 
                     val var10000: LoadedServerShip = VSGameUtilsKt.getShipObjectWorld(`$level`).getLoadedShips().getById(`$shipId`) as LoadedServerShip
                     if (var10000 != null) {
                        assemble$applyControl(
                           `$helmCount`, `$balloonCount`, `$floaterCount`, `$anchorCount`, `$activeAnchorCount`, `$engineCount`, `$blockCount`, var10000
                        )
                     }
                  }
               )
            }
         }
      }
   }

   public fun disassemble() {
      val var10000: LoadedServerShip = this.ship
      if (var10000 != null) {
         if (this.field_11863 != null) {
            val level: class_1937 = this.field_11863
            val var5: EurekaShipControl = this.control
            if (var5 != null) {
               if (!var5.canDisassemble) {
                  this.shouldDisassembleWhenPossible = true
                  var5.disassembling = true
                  var5.aligning = true
               } else {
                  val var6: Matrix4dc = var10000.getShipToWorld()
                  val var10001: class_2338 = this.method_11016()
                  val inWorld: Vector3d = var6.transformPosition(VectorConversionsMCKt.toJOMLD(var10001 as class_2382))
                  val var7: ShipAssembler = ShipAssembler.INSTANCE
                  val var8: class_3218 = level as class_3218
                  val var10002: ServerShip = var10000 as ServerShip
                  val var10003: class_2338 = this.method_11016()
                  val var10004: class_2338 = class_2338.method_49637(inWorld.x, inWorld.y, inWorld.z)
                  var7.unfillShip(var8, var10002, var10003, var10004)
                  this.shouldDisassembleWhenPossible = false
               }
            }
         }
      }
   }

   public fun align() {
      val var10000: EurekaShipControl = this.control
      if (var10000 != null) {
         var10000.aligning = !var10000.aligning
      }
   }

   fun method_11012() {
      if (super.field_11863 != null && !super.field_11863.field_9236) {
         var i: Int = 0

         for (var2 in this.seats.size()..i) {
            this.seats.get(i).method_5768()
         }

         this.seats.clear()
      }

      super.method_11012()
   }

   public fun sit(player: class_1657, force: Boolean = false): Boolean {
      if (!force) {
         val var10000: class_1297 = player.method_5854()
         if ((if (var10000 != null) var10000.method_5864() else null) == ValkyrienSkiesMod.INSTANCE.getSHIP_MOUNTING_ENTITY_TYPE()) {
            val var3: java.util.List = this.seats
            val var10001: class_1297 = player.method_5854()
            if (var3.contains(var10001 as ShipMountingEntity)) {
               player.method_17355(this)
               return true
            }
         }
      }

      val var10003: class_2338 = this.method_11016()
      val var10004: class_2680 = this.method_11010()
      val var10005: class_1937 = this.field_11863
      return this.startRiding(player, force, var10003, var10004, var10005 as class_3218)
   }

   @JvmStatic
   fun `assemble$applyControl`(
      helmCount: IntRef,
      balloonCount: IntRef,
      floaterCount: IntRef,
      anchorCount: IntRef,
      activeAnchorCount: IntRef,
      engineCount: IntRef,
      blockCount: IntRef,
      loadedShip: LoadedServerShip
   ) {
      val control: EurekaShipControl = EurekaShipControl.Companion.getOrCreate(loadedShip)
      control.helms = helmCount.element
      control.balloons = balloonCount.element
      control.floaters = floaterCount.element
      control.anchors = anchorCount.element
      control.anchorsActive = activeAnchorCount.element
      control.engines = engineCount.element
      control.assembledBlocks = blockCount.element
   }
}
