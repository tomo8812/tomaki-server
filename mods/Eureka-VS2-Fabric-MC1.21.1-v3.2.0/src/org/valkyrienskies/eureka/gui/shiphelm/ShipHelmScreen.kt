package org.valkyrienskies.eureka.gui.shiphelm

import java.util.Arrays
import java.util.HashMap
import java.util.Locale
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_1661
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_239
import net.minecraft.class_2561
import net.minecraft.class_310
import net.minecraft.class_327
import net.minecraft.class_332
import net.minecraft.class_342
import net.minecraft.class_364
import net.minecraft.class_3965
import net.minecraft.class_4185
import net.minecraft.class_4587
import net.minecraft.class_465
import net.minecraft.class_5250
import net.minecraft.class_5348
import net.minecraft.class_638
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaConfigLoader
import org.valkyrienskies.mod.common.VSGameUtilsKt

@SourceDebugExtension(["SMAP\nShipHelmScreen.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ShipHelmScreen.kt\norg/valkyrienskies/eureka/gui/shiphelm/ShipHelmScreen\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,663:1\n1#2:664\n*E\n"])
public class ShipHelmScreen(handler: ShipHelmScreenMenu, playerInventory: class_1661, text: class_2561) : class_465(handler, playerInventory, text) {
   private final lateinit var assembleButton: ShipHelmButton
   private final lateinit var alignButton: ShipHelmButton
   private final lateinit var disassembleButton: ShipHelmButton
   private final lateinit var advancedCheckbox: ShipHelmCheckbox
   private final lateinit var vanillaCheckbox: ShipHelmCheckbox
   private final lateinit var cruiseMasterCheckbox: ShipHelmCheckbox
   private final lateinit var cruiseSpeedCheckbox: ShipHelmCheckbox
   private final lateinit var cruiseTurnCheckbox: ShipHelmCheckbox
   private final lateinit var cruiseVerticalCheckbox: ShipHelmCheckbox
   private final lateinit var cruiseSpeedBox: class_342
   private final lateinit var cruiseTurnBox: class_342
   private final lateinit var cruiseVerticalBox: class_342
   private final lateinit var displayHudCheckbox: ShipHelmCheckbox
   private final lateinit var displaySpeedCheckbox: ShipHelmCheckbox
   private final lateinit var displayAltitudeCheckbox: ShipHelmCheckbox
   private final lateinit var displayHeadingCheckbox: ShipHelmCheckbox
   private final lateinit var assemblerMasterCheckbox: ShipHelmCheckbox
   private final lateinit var assemblerFloaterCheckbox: ShipHelmCheckbox
   private final lateinit var assemblerBalloonCheckbox: ShipHelmCheckbox
   private final lateinit var assemblerFloaterBox: class_342
   private final lateinit var assemblerBalloonBox: class_342
   private final lateinit var shipWeightBox: class_342
   private final var weightBoxX: Int
   private final var weightBoxW: Int
   private final lateinit var keepActiveCheckbox: ShipHelmCheckbox
   private final lateinit var waterLockCheckbox: ShipHelmCheckbox
   private final lateinit var renameButton: ShipHelmIconButton
   private final lateinit var renameBox: class_342
   private final var renaming: Boolean
   private final var pos: class_2338?
   private final var ship: Ship?

   init {
      val var4: class_239 = class_310.method_1551().field_1765
      this.pos = if ((var4 as? class_3965) != null) (var4 as? class_3965).method_17777() else null
      var var10000: ShipHelmScreen = this
      val var10001: Ship
      if (this.pos != null) {
         val it: class_2338 = this.pos
         val var8: class_638 = class_310.method_1551().field_1687
         var10001 = if (var8 != null) VSGameUtilsKt.getShipManagingPos(var8 as class_1937, it) else null
         var10000 = this
      } else {
         var10001 = null
      }

      var10000.ship = var10001
      this.field_25267 = 8
      this.field_25268 = 7
      this.field_2792 = 320
      this.field_2779 = 210
   }

   fun method_25426() {
      super.method_25426()
      val x: Int = (super.field_22789 - super.field_2792) / 2
      val y: Int = (super.field_22790 - super.field_2779) / 2
      var var10004: Int = x + 24
      var var10005: Int = y + 24
      var var10006: ShipHelmScreen.Companion = Companion
      var var10007: class_5250 = ADVANCED_TEXT
      val var47: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = ADVANCED_TEXT
      val var77: class_2561 = var10007 as class_2561
      var var10008: class_327 = super.field_22793
      this.advancedCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var47, var77, var10008, { 
         !(`this$0`.field_2797 as ShipHelmScreenMenu).vanillaControls
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 7)
         }
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 175
      var10005 = y + 24
      var10006 = Companion
      var10007 = VANILLA_TEXT
      val var49: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = VANILLA_TEXT
      val var80: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.vanillaCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var49, var80, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).vanillaControls
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 6)
         }
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 12
      var10005 = y + 40
      var10006 = Companion
      var10007 = CRUISE_TEXT
      val var51: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = CRUISE_TEXT
      val var83: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.cruiseMasterCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var51, var83, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).cruising
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 8)
         }
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 16
      var10005 = y + Companion.cruiseRowY(0)
      var10006 = Companion
      var10007 = CRUISE_SPEED_TEXT
      val var53: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = CRUISE_SPEED_TEXT
      val var86: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.cruiseSpeedCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var53, var86, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).cruiseSpeedArmed
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 9)
         }
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 16
      var10005 = y + Companion.cruiseRowY(1)
      var10006 = Companion
      var10007 = CRUISE_TURN_TEXT
      val var55: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = CRUISE_TURN_TEXT
      val var89: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.cruiseTurnCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var55, var89, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).cruiseTurnArmed
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 10)
         }
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 16
      var10005 = y + Companion.cruiseRowY(2)
      var10006 = Companion
      var10007 = CRUISE_VERTICAL_TEXT
      val var57: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = CRUISE_VERTICAL_TEXT
      val var92: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.cruiseVerticalCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var57, var92, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).cruiseVerticalArmed
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 11)
         }
      }) as class_364) as ShipHelmCheckbox
      this.cruiseSpeedBox = this.addCruiseBox(x + 74, y + Companion.cruiseRowY(0))
      this.cruiseTurnBox = this.addCruiseBox(x + 74, y + Companion.cruiseRowY(1))
      this.cruiseVerticalBox = this.addCruiseBox(x + 74, y + Companion.cruiseRowY(2))
      var10004 = x + 120
      var10005 = y + 40
      var10006 = Companion
      var10007 = DISPLAY_HUD_TEXT
      val var59: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = DISPLAY_HUD_TEXT
      val var95: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.displayHudCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var59, var95, var10008, { 
         EurekaConfig.CLIENT.displayHud
      }, { it: class_4185 ->
         EurekaConfig.CLIENT.displayHud = !EurekaConfig.CLIENT.displayHud
         EurekaConfigLoader.save()
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 124
      var10005 = y + Companion.cruiseRowY(0)
      var10006 = Companion
      var10007 = SPEED_TEXT
      val var61: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = SPEED_TEXT
      val var98: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.displaySpeedCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var61, var98, var10008, { 
         EurekaConfig.CLIENT.displaySpeed
      }, { it: class_4185 ->
         EurekaConfig.CLIENT.displaySpeed = !EurekaConfig.CLIENT.displaySpeed
         EurekaConfigLoader.save()
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 124
      var10005 = y + Companion.cruiseRowY(1)
      var10006 = Companion
      var10007 = ALTITUDE_TEXT
      val var63: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = ALTITUDE_TEXT
      val var101: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.displayAltitudeCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var63, var101, var10008, { 
         EurekaConfig.CLIENT.displayAltitude
      }, { it: class_4185 ->
         EurekaConfig.CLIENT.displayAltitude = !EurekaConfig.CLIENT.displayAltitude
         EurekaConfigLoader.save()
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 124
      var10005 = y + Companion.cruiseRowY(2)
      var10006 = Companion
      var10007 = HEADING_TEXT
      val var65: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = HEADING_TEXT
      val var104: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.displayHeadingCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var65, var104, var10008, { 
         EurekaConfig.CLIENT.displayHeading
      }, { it: class_4185 ->
         EurekaConfig.CLIENT.displayHeading = !EurekaConfig.CLIENT.displayHeading
         EurekaConfigLoader.save()
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 190
      var10005 = y + 40
      var10006 = Companion
      var10007 = ASSEMBLER_TEXT
      val var67: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = ASSEMBLER_TEXT
      val var107: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.assemblerMasterCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var67, var107, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).assemblerEnabled
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 12)
         }
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 194
      var10005 = y + Companion.cruiseRowY(0)
      var10006 = Companion
      var10007 = ASSEMBLER_FLOATER_TEXT
      val var69: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = ASSEMBLER_FLOATER_TEXT
      val var110: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.assemblerFloaterCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var69, var110, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).assemblerFloater
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 13)
         }
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 194
      var10005 = y + Companion.cruiseRowY(1)
      var10006 = Companion
      var10007 = ASSEMBLER_BALLOON_TEXT
      val var71: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = ASSEMBLER_BALLOON_TEXT
      val var113: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.assemblerBalloonCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var71, var113, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).assemblerBalloon
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 14)
         }
      }) as class_364) as ShipHelmCheckbox
      this.assemblerFloaterBox = this.addPctBox(x + 284, y + Companion.cruiseRowY(0))
      this.assemblerBalloonBox = this.addPctBox(x + 284, y + Companion.cruiseRowY(1))
      this.weightBoxX = x + 194 + Math.round((float)super.field_22793.method_27525(SHIP_WEIGHT_TEXT as class_5348) * 0.85F) + 5
      this.weightBoxW = x + 320 - 6 - this.weightBoxX
      var var3: class_364 = this.method_37063(
         class_342(super.field_22793, this.weightBoxX, y + Companion.cruiseRowY(2), this.weightBoxW, 12, class_2561.method_43473() as class_2561) as class_364
      )
      var it: class_342 = var3 as class_342
      (var3 as class_342).method_1858(true)
      it.method_1888(false)
      it.method_1860(-2039584)
      it.field_22763 = false
      it.method_1852("0")
      this.shipWeightBox = var3 as class_342
      var10004 = x + 14
      var10005 = y + 104
      var10006 = Companion
      var10007 = KEEP_ACTIVE_TEXT
      val var73: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = KEEP_ACTIVE_TEXT
      val var116: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.keepActiveCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var73, var116, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).keepActive
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 4)
         }
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 14
      var10005 = y + 104 + 13
      var10006 = Companion
      var10007 = WATER_LOCK_TEXT
      val var75: Int = var10006.checkboxWidth(var10007 as class_2561)
      var10007 = WATER_LOCK_TEXT
      val var119: class_2561 = var10007 as class_2561
      var10008 = super.field_22793
      this.waterLockCheckbox = this.method_37063(ShipHelmCheckbox(var10004, var10005, var75, var119, var10008, { 
         (`this$0`.field_2797 as ShipHelmScreenMenu).waterAltitudeHold
      }, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 5)
         }
      }) as class_364) as ShipHelmCheckbox
      var10004 = x + 126
      var10005 = y + Companion.btnRowY(0)
      val var134: class_5250 = ASSEMBLE_TEXT
      val var135: class_2561 = var134 as class_2561
      var var10009: class_327 = super.field_22793
      this.assembleButton = this.method_37063(ShipHelmButton(var10004, var10005, 68, 16, var135, var10009, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 0)
         }
      }) as class_364) as ShipHelmButton
      var10004 = x + 126
      var10005 = y + Companion.btnRowY(1)
      val var136: class_5250 = ALIGN_TEXT
      val var137: class_2561 = var136 as class_2561
      var10009 = super.field_22793
      this.alignButton = this.method_37063(ShipHelmButton(var10004, var10005, 68, 16, var137, var10009, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 1)
         }
      }) as class_364) as ShipHelmButton
      var10004 = x + 126
      var10005 = y + Companion.btnRowY(2)
      val var138: class_5250 = DISSEMBLE_TEXT
      val var139: class_2561 = var138 as class_2561
      var10009 = super.field_22793
      this.disassembleButton = this.method_37063(ShipHelmButton(var10004, var10005, 68, 16, var139, var10009, { it: class_4185 ->
         if (`this$0`.field_22787 != null && `this$0`.field_22787.field_1761 != null) {
            `this$0`.field_22787.field_1761.method_2900((`this$0`.field_2797 as ShipHelmScreenMenu).field_7763, 3)
         }
      }) as class_364) as ShipHelmButton
      var3 = this.method_37063(class_342(super.field_22793, x + 8, y + 3, 150, 12, RENAME_TEXT as class_2561) as class_364)
      it = var3 as class_342
      (var3 as class_342).method_1880(48)
      it.field_22764 = false
      this.renameBox = var3 as class_342
      var10004 = x + 276
      var10005 = y + 5
      val var140: class_5250 = RENAME_TEXT
      val var141: class_2561 = var140 as class_2561
      var10009 = super.field_22793
      this.renameButton = this.method_37063(ShipHelmIconButton(var10004, var10005, 38, 10, var141, var10009, { it: class_4185 ->
         `this$0`.toggleRename()
      }) as class_364) as ShipHelmIconButton
      var var10000: ShipHelmButton = this.disassembleButton
      if (this.disassembleButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("disassembleButton")
         var10000 = null
      }

      var10000.field_22763 = EurekaConfig.SERVER.allowDisassembly
      this.updateButtons()
   }

   private fun addCruiseBox(bx: Int, by: Int): class_342 {
      val var3: class_364 = this.method_37063(class_342(this.field_22793, bx, by, 36, 12, class_2561.method_43473() as class_2561) as class_364)
      val it: class_342 = var3 as class_342
      (var3 as class_342).method_1880(7)
      it.method_1858(true)
      it.method_1890({ s: java.lang.String ->
         NUMERIC.matches(s)
      })
      return var3 as class_342
   }

   private fun addPctBox(bx: Int, by: Int): class_342 {
      val var3: class_364 = this.method_37063(class_342(this.field_22793, bx, by, 30, 12, class_2561.method_43473() as class_2561) as class_364)
      val it: class_342 = var3 as class_342
      (var3 as class_342).method_1880(4)
      it.method_1858(true)
      it.method_1890({ s: java.lang.String ->
         PERCENT.matches(s)
      })
      return var3 as class_342
   }

   private fun toggleRename() {
      if (this.ship != null) {
         val s: Ship = this.ship
         if (!this.renaming) {
            var var10000: class_342 = this.renameBox
            if (this.renameBox == null) {
               Intrinsics.throwUninitializedPropertyAccessException("renameBox")
               var10000 = null
            }

            var var10001: java.lang.String = pendingNames.get(this.ship.getId())
            if (var10001 == null) {
               var10001 = s.getSlug()
            }

            run label93@{
               if (var10001 != null) {
                  var10001 = StringsKt.replace$default(var10001, '-', ' ', false, 4, null)
                  if (var10001 != null) {
                     return@label93
                  }
               }

               var10001 = ""
            }

            var10000.method_1852(var10001)
            var10000 = this.renameBox
            if (this.renameBox == null) {
               Intrinsics.throwUninitializedPropertyAccessException("renameBox")
               var10000 = null
            }

            var10000.field_22764 = true
            var var11: class_342 = this.renameBox
            if (this.renameBox == null) {
               Intrinsics.throwUninitializedPropertyAccessException("renameBox")
               var11 = null
            }

            this.method_25395(var11 as class_364)
            var10000 = this.renameBox
            if (this.renameBox == null) {
               Intrinsics.throwUninitializedPropertyAccessException("renameBox")
               var10000 = null
            }

            var10000.method_25365(true)
            this.renaming = true
            var var6: ShipHelmIconButton = this.renameButton
            if (this.renameButton == null) {
               Intrinsics.throwUninitializedPropertyAccessException("renameButton")
               var6 = null
            }

            var6.method_25355(SAVE_TEXT as class_2561)
         } else {
            var var7: class_342 = this.renameBox
            if (this.renameBox == null) {
               Intrinsics.throwUninitializedPropertyAccessException("renameBox")
               var7 = null
            }

            val var8: java.lang.String = var7.method_1882()
            val slugName: java.lang.String = StringsKt.replace$default(
               StringsKt.replace$default(StringsKt.trim(var8).toString(), "\"", "", false, 4, null), ' ', '-', false, 4, null
            )
            var var9: java.lang.String = pendingNames.get(s.getId())
            if (var9 == null) {
               var9 = s.getSlug()
            }

            if (slugName.length() > 0 && !(slugName == var9)) {
               if (this.field_22787 != null && this.field_22787.field_1724 != null && this.field_22787.field_1724.field_3944 != null) {
                  this.field_22787.field_1724.field_3944.method_45730("vs rename @v[id=${s.getId()}] \"$slugName\"")
               }

               pendingNames.put(s.getId(), slugName)
            }

            this.cancelRename()
         }
      }
   }

   private fun cancelRename() {
      this.renaming = false
      var var10000: class_342 = this.renameBox
      if (this.renameBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("renameBox")
         var10000 = null
      }

      var10000.field_22764 = false
      var10000 = this.renameBox
      if (this.renameBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("renameBox")
         var10000 = null
      }

      var10000.method_25365(false)
      val var2: class_364 = this.method_25399()
      var var10001: class_342 = this.renameBox
      if (this.renameBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("renameBox")
         var10001 = null
      }

      if (var2 === var10001) {
         this.method_25395(null)
      }

      var var3: ShipHelmIconButton = this.renameButton
      if (this.renameButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("renameButton")
         var3 = null
      }

      var3.method_25355(RENAME_TEXT as class_2561)
   }

   private fun commitCruiseBox(axis: Int, box: class_342) {
      val var10000: java.lang.String = box.method_1882()
      val var6: java.lang.Double = StringsKt.toDoubleOrNull(StringsKt.trim(var10000).toString())
      if (var6 != null) {
         val v: Double = var6.doubleValue()
         if (this.field_22787 != null && this.field_22787.field_1761 != null) {
            this.field_22787
               .field_1761
               .method_2900((this.field_2797 as ShipHelmScreenMenu).field_7763, ShipHelmScreenMenu.Companion.encodeCruiseValue(axis, v))
            }
      }

      box.method_25365(false)
      if (this.method_25399() === box) {
         this.method_25395(null)
      }
   }

   private fun cruiseBoxAxis(box: class_342): Int {
      var var10001: class_342 = this.cruiseSpeedBox
      if (this.cruiseSpeedBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("cruiseSpeedBox")
         var10001 = null
      }

      val var10000: Byte
      if (box == var10001) {
         var10000 = 0
      } else {
         var10001 = this.cruiseTurnBox
         if (this.cruiseTurnBox == null) {
            Intrinsics.throwUninitializedPropertyAccessException("cruiseTurnBox")
            var10001 = null
         }

         var10000 = (byte)(if (box == var10001) 1 else 2)
      }

      return var10000
   }

   private fun commitPctBox(which: Int, box: class_342) {
      val var10000: java.lang.String = box.method_1882()
      val var5: Int = StringsKt.toIntOrNull(StringsKt.removeSuffix(StringsKt.trim(var10000).toString(), "%"))
      if (var5 != null) {
         val pct: Int = var5.intValue()
         if (this.field_22787 != null && this.field_22787.field_1761 != null) {
            this.field_22787
               .field_1761
               .method_2900((this.field_2797 as ShipHelmScreenMenu).field_7763, ShipHelmScreenMenu.Companion.encodeAssemblerBonus(which, pct))
            }
      }

      box.method_25365(false)
      if (this.method_25399() === box) {
         this.method_25395(null)
      }
   }

   private fun pctBoxWhich(box: class_342): Int {
      var var10001: class_342 = this.assemblerFloaterBox
      if (this.assemblerFloaterBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("assemblerFloaterBox")
         var10001 = null
      }

      return if (box === var10001) 0 else 1
   }

   private fun updateButtons() {
      val isLookingAtShip: class_239 = class_310.method_1551().field_1765
      val newPos: class_2338 = if ((isLookingAtShip as? class_3965) != null) (isLookingAtShip as? class_3965).method_17777() else null
      if (newPos != null) {
         this.pos = newPos
      }

      val var18: Ship
      if (this.pos != null) {
         val advanced: class_2338 = this.pos
         val var10000: class_638 = class_310.method_1551().field_1687
         var18 = if (var10000 != null) VSGameUtilsKt.getShipManagingPos(var10000 as class_1937, advanced) else null
      } else {
         var18 = null
      }

      if (var18 != null) {
         this.ship = var18
      }

      if (this.ship != null) {
         val var13: Ship = this.ship
         if (pendingNames.get(this.ship.getId()) == var13.getSlug()) {
            pendingNames.remove(var13.getId())
         }
      }

      val var12: Boolean = this.ship != null
      val assembled: Boolean = (this.field_2797 as ShipHelmScreenMenu).assembled
      val var14: Boolean = !(this.field_2797 as ShipHelmScreenMenu).vanillaControls
      var var19: ShipHelmButton = this.assembleButton
      if (this.assembleButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("assembleButton")
         var19 = null
      }

      var19.field_22763 = !assembled
      var var20: ShipHelmButton = this.disassembleButton
      if (this.disassembleButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("disassembleButton")
         var20 = null
      }

      var20.field_22763 = EurekaConfig.SERVER.allowDisassembly && assembled
      var var21: ShipHelmButton = this.alignButton
      if (this.alignButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("alignButton")
         var21 = null
      }

      var var10001: ShipHelmButton = this.disassembleButton
      if (this.disassembleButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("disassembleButton")
         var10001 = null
      }

      var21.field_22763 = var10001.field_22763
      var var22: ShipHelmCheckbox = this.advancedCheckbox
      if (this.advancedCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("advancedCheckbox")
         var22 = null
      }

      var22.field_22763 = assembled
      var var23: ShipHelmCheckbox = this.vanillaCheckbox
      if (this.vanillaCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("vanillaCheckbox")
         var23 = null
      }

      var23.field_22763 = assembled
      var var24: ShipHelmCheckbox = this.keepActiveCheckbox
      if (this.keepActiveCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("keepActiveCheckbox")
         var24 = null
      }

      var24.field_22763 = assembled
      var var25: ShipHelmCheckbox = this.waterLockCheckbox
      if (this.waterLockCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("waterLockCheckbox")
         var25 = null
      }

      var25.field_22763 = true
      val var16: Boolean = assembled && var14
      var var26: ShipHelmCheckbox = this.cruiseMasterCheckbox
      if (this.cruiseMasterCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("cruiseMasterCheckbox")
         var26 = null
      }

      var26.field_22763 = var16
      var var27: ShipHelmCheckbox = this.cruiseSpeedCheckbox
      if (this.cruiseSpeedCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("cruiseSpeedCheckbox")
         var27 = null
      }

      var27.field_22763 = var16
      var var28: ShipHelmCheckbox = this.cruiseTurnCheckbox
      if (this.cruiseTurnCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("cruiseTurnCheckbox")
         var28 = null
      }

      var28.field_22763 = var16
      var var29: ShipHelmCheckbox = this.cruiseVerticalCheckbox
      if (this.cruiseVerticalCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("cruiseVerticalCheckbox")
         var29 = null
      }

      var29.field_22763 = var16
      var var40: class_342 = this.cruiseSpeedBox
      if (this.cruiseSpeedBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("cruiseSpeedBox")
         var40 = null
      }

      this.updateCruiseBox(var40, var16, (this.field_2797 as ShipHelmScreenMenu).cruiseSpeed)
      var var41: class_342 = this.cruiseTurnBox
      if (this.cruiseTurnBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("cruiseTurnBox")
         var41 = null
      }

      this.updateCruiseBox(var41, var16, (this.field_2797 as ShipHelmScreenMenu).cruiseTurn)
      var var42: class_342 = this.cruiseVerticalBox
      if (this.cruiseVerticalBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("cruiseVerticalBox")
         var42 = null
      }

      this.updateCruiseBox(var42, var16, (this.field_2797 as ShipHelmScreenMenu).cruiseVertical)
      val hudOn: Boolean = EurekaConfig.CLIENT.displayHud
      var var30: ShipHelmCheckbox = this.displaySpeedCheckbox
      if (this.displaySpeedCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("displaySpeedCheckbox")
         var30 = null
      }

      var30.field_22763 = hudOn
      var var31: ShipHelmCheckbox = this.displayAltitudeCheckbox
      if (this.displayAltitudeCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("displayAltitudeCheckbox")
         var31 = null
      }

      var31.field_22763 = hudOn
      var var32: ShipHelmCheckbox = this.displayHeadingCheckbox
      if (this.displayHeadingCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("displayHeadingCheckbox")
         var32 = null
      }

      var32.field_22763 = hudOn
      val asmOn: Boolean = (this.field_2797 as ShipHelmScreenMenu).assemblerEnabled
      var var33: ShipHelmCheckbox = this.assemblerFloaterCheckbox
      if (this.assemblerFloaterCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("assemblerFloaterCheckbox")
         var33 = null
      }

      var33.field_22763 = asmOn
      var var34: ShipHelmCheckbox = this.assemblerBalloonCheckbox
      if (this.assemblerBalloonCheckbox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("assemblerBalloonCheckbox")
         var34 = null
      }

      var34.field_22763 = asmOn
      var var43: class_342 = this.assemblerFloaterBox
      if (this.assemblerFloaterBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("assemblerFloaterBox")
         var43 = null
      }

      this.updatePctBox(var43, asmOn && (this.field_2797 as ShipHelmScreenMenu).assemblerFloater, (this.field_2797 as ShipHelmScreenMenu).assemblerFloaterBonus)
      var var44: class_342 = this.assemblerBalloonBox
      if (this.assemblerBalloonBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("assemblerBalloonBox")
         var44 = null
      }

      this.updatePctBox(var44, asmOn && (this.field_2797 as ShipHelmScreenMenu).assemblerBalloon, (this.field_2797 as ShipHelmScreenMenu).assemblerBalloonBonus)
      val var17: Array<Any> = arrayOf((this.field_2797 as ShipHelmScreenMenu).shipMass)
      val var35: java.lang.String = java.lang.String.format("%,d", Arrays.copyOf(var17, var17.length))
      var var36: class_342 = this.shipWeightBox
      if (this.shipWeightBox == null) {
         Intrinsics.throwUninitializedPropertyAccessException("shipWeightBox")
         var36 = null
      }

      if (!(var36.method_1882() == var35)) {
         var var37: class_342 = this.shipWeightBox
         if (this.shipWeightBox == null) {
            Intrinsics.throwUninitializedPropertyAccessException("shipWeightBox")
            var37 = null
         }

         var37.method_1852(var35)
      }

      var var38: ShipHelmIconButton = this.renameButton
      if (this.renameButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("renameButton")
         var38 = null
      }

      var38.field_22764 = var12
      var var39: ShipHelmIconButton = this.renameButton
      if (this.renameButton == null) {
         Intrinsics.throwUninitializedPropertyAccessException("renameButton")
         var39 = null
      }

      var39.field_22763 = var12
      if (!var12 && this.renaming) {
         this.cancelRename()
      }
   }

   private fun updateCruiseBox(box: class_342, usable: Boolean, syncedHundredths: Int) {
      box.field_22764 = usable
      box.method_1888(usable)
      if (usable && !box.method_25370()) {
         val var4: Locale = Locale.ROOT
         val var7: Array<Any> = arrayOf((double)syncedHundredths / 100.0)
         val var10001: java.lang.String = java.lang.String.format(var4, "%.2f", Arrays.copyOf(var7, var7.length))
         box.method_1852(var10001)
      }
   }

   private fun updatePctBox(box: class_342, usable: Boolean, syncedPercent: Int) {
      box.method_1888(usable)
      if (!box.method_25370()) {
         box.method_1852("$syncedPercent%")
      }
   }

   fun method_2389(guiGraphics: class_332, partialTicks: Float, mouseX: Int, mouseY: Int) {
      this.updateButtons()
      val x: Int = (super.field_22789 - super.field_2792) / 2
      val y: Int = (super.field_22790 - super.field_2779) / 2
      guiGraphics.method_25294(
         x - 1,
         (super.field_22790 - super.field_2779) / 2 - 1,
         x + super.field_2792 + 1,
         (super.field_22790 - super.field_2779) / 2 + super.field_2779 + 1,
         -16777216
      )
      guiGraphics.method_25294(x, y, x + super.field_2792, y + super.field_2779, -3750202)
      this.hLine(guiGraphics, x, y + 18)
      this.hLine(guiGraphics, x, y + 98)
      this.frame(guiGraphics, x + 10, y + Companion.boxRowY(150, 0), 92, 15)
      this.frame(guiGraphics, x + 10, y + Companion.boxRowY(150, 1), 92, 15)
      this.frame(guiGraphics, x + 10, y + Companion.boxRowY(150, 2), 92, 15)
      this.frame(guiGraphics, x + 218, y + Companion.boxRowY(150, 0), 92, 15)
      this.frame(guiGraphics, x + 218, y + Companion.boxRowY(150, 1), 92, 15)
      this.frame(guiGraphics, x + 218, y + Companion.boxRowY(150, 2), 92, 15)
   }

   private fun hLine(g: class_332, x: Int, absY: Int) {
      g.method_25294(x + 4, absY, x + this.field_2792 - 4, absY + 1, -7631989)
   }

   private fun frame(g: class_332, bx: Int, by: Int, w: Int, h: Int) {
      g.method_25294(bx, by, bx + w, by + h, -12566464)
      g.method_25294(bx + 1, by + 1, bx + w - 1, by + h - 1, -5197648)
   }

   fun method_2388(guiGraphics: class_332, i: Int, j: Int) {
      if ((super.field_2797 as ShipHelmScreenMenu).aligning) {
         var var10000: ShipHelmButton = this.alignButton
         if (this.alignButton == null) {
            Intrinsics.throwUninitializedPropertyAccessException("alignButton")
            var10000 = null
         }

         var10000.method_25355(ALIGNING_TEXT as class_2561)
         var10000 = this.alignButton
         if (this.alignButton == null) {
            Intrinsics.throwUninitializedPropertyAccessException("alignButton")
            var10000 = null
         }

         var10000.field_22763 = false
      } else {
         var var15: ShipHelmButton = this.alignButton
         if (this.alignButton == null) {
            Intrinsics.throwUninitializedPropertyAccessException("alignButton")
            var15 = null
         }

         var15.method_25355(ALIGN_TEXT as class_2561)
      }

      var var10002: class_5250 = PLUS_TEXT
      this.drawScaledLabel(guiGraphics, var10002 as class_2561, 276, Companion.cruiseRowY(0), 12, -13092808)
      var10002 = PLUS_TEXT
      this.drawScaledLabel(guiGraphics, var10002 as class_2561, 276, Companion.cruiseRowY(1), 12, -13092808)
      var10002 = SHIP_WEIGHT_TEXT
      this.drawScaledLabel(guiGraphics, var10002 as class_2561, 194, Companion.cruiseRowY(2), 12, -13092808)
      if (this.ship != null) {
         val s: Ship = this.ship
         if (!this.renaming) {
            var var16: java.lang.String = pendingNames.get(this.ship.getId())
            if (var16 == null) {
               var16 = s.getSlug()
            }

            if (var16 != null) {
               guiGraphics.method_51433(
                  super.field_22793,
                  "Name: ${StringsKt.replace$default(var16, '-', ' ', false, 4, null)}",
                  super.field_25267,
                  super.field_25268,
                  -13092808,
                  false
               )
            }
         }

         val topLine: java.lang.String = "Top Speed: ${(super.field_2797 as ShipHelmScreenMenu).topSpeed}m/s~"
         val var13: Array<Any> = arrayOf((super.field_2797 as ShipHelmScreenMenu).blockCount)
         val var17: java.lang.String = java.lang.String.format("%,d", Arrays.copyOf(var13, var13.length))
         val blockLine: java.lang.String = "Blocks: $var17"
         val var10: java.lang.String = this.dimensionsText(s)
         this.drawBoxText(guiGraphics, topLine, 10, Companion.boxRelY(150, 0), 92)
         this.drawBoxText(guiGraphics, blockLine, 10, Companion.boxRelY(150, 1), 92)
         this.drawBoxText(guiGraphics, var10, 10, Companion.boxRelY(150, 2), 92)
         this.drawBoxText(
            guiGraphics,
            if ((super.field_2797 as ShipHelmScreenMenu).hasEngines)
               "Engine Power: ${(super.field_2797 as ShipHelmScreenMenu).enginePower}%"
               else
               "Engine Power: --",
            218,
            Companion.boxRelY(150, 0),
            92
         )
      }
   }

   private fun dimensionsText(s: Ship): String {
      val var10000: AABBic = s.getShipAABB()
      if (var10000 == null) {
         return "H:- W:- L:-"
      } else {
         val h: Int = var10000.maxY() - var10000.minY() + 1
         val xExt: Int = var10000.maxX() - var10000.minX() + 1
         val zExt: Int = var10000.maxZ() - var10000.minZ() + 1
         return "H:$h W:${Math.min(xExt, zExt)} L:${Math.max(xExt, zExt)}"
      }
   }

   private fun drawScaledLabel(guiGraphics: class_332, text: class_2561, relX: Int, relY: Int, boxH: Int, color: Int) {
      val pose: class_4587 = guiGraphics.method_51448()
      pose.method_22903()
      pose.method_22905(0.85F, 0.85F, 1.0F)
      val tx: Float = relX / 0.85F
      val ty: Float = (relY + (boxH - this.field_22793.field_2000 * 0.85F) / 2.0F) / 0.85F
      guiGraphics.method_51439(this.field_22793, text, Math.round(tx), Math.round(ty), color, false)
      pose.method_22909()
   }

   private fun drawBoxText(guiGraphics: class_332, text: String, boxX: Int, topY: Int, boxW: Int) {
      val widest: Float = this.field_22793.method_1727(text)
      val scale: Float = if (widest <= 0.0F) 0.8F else Math.min(0.8F, (float)(boxW - 3) / widest)
      val pose: class_4587 = guiGraphics.method_51448()
      pose.method_22903()
      pose.method_22905(scale, scale, 1.0F)
      val tx: Float = (boxX + 2) / scale
      val ty: Float = (topY + (15 - this.field_22793.field_2000 * scale) / 2.0F) / scale
      guiGraphics.method_51433(this.field_22793, text, Math.round(tx), Math.round(ty), -13092808, false)
      pose.method_22909()
   }

   fun method_25404(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
      if (this.renaming) {
         when (keyCode) {
            256 -> {
               this.cancelRename()
               true
            }
            257, 335 -> {
               this.toggleRename()
               true
            }
            else -> {
               var var10000: class_342 = this.renameBox
               if (this.renameBox == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("renameBox")
                  var10000 = null
               }

               var10000.method_25404(keyCode, scanCode, modifiers)
               true
            }
         }
      } else {
         var f: class_364
         run label99@{
            f = this.method_25399()
            if (f is class_342) {
               var var10001: class_342 = this.cruiseSpeedBox
               if (this.cruiseSpeedBox == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("cruiseSpeedBox")
                  var10001 = null
               }

               if (f === var10001) {
                  return@label99
               }

               var10001 = this.cruiseTurnBox
               if (this.cruiseTurnBox == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("cruiseTurnBox")
                  var10001 = null
               }

               if (f === var10001) {
                  return@label99
               }

               var10001 = this.cruiseVerticalBox
               if (this.cruiseVerticalBox == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("cruiseVerticalBox")
                  var10001 = null
               }

               if (f === var10001) {
                  return@label99
               }
            }

            if (f is class_342) {
               var var7: class_342 = this.assemblerFloaterBox
               if (this.assemblerFloaterBox == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("assemblerFloaterBox")
                  var7 = null
               }

               if (f != var7) {
                  var7 = this.assemblerBalloonBox
                  if (this.assemblerBalloonBox == null) {
                     Intrinsics.throwUninitializedPropertyAccessException("assemblerBalloonBox")
                     var7 = null
                  }

                  if (f != var7) {
                     super.method_25404(keyCode, scanCode, modifiers)
                  }
               }

               when (keyCode) {
                  256 -> {
                     (f as class_342).method_25365(false)
                     this.method_25395(null)
                     true
                  }
                  257, 335 -> {
                     this.commitPctBox(this.pctBoxWhich(f as class_342), f as class_342)
                     true
                  }
                  else -> {
                     (f as class_342).method_25404(keyCode, scanCode, modifiers)
                     true
                  }
               }
            }

            super.method_25404(keyCode, scanCode, modifiers)
         }

         when (keyCode) {
            256 -> {
               (f as class_342).method_25365(false)
               this.method_25395(null)
               true
            }
            257, 335 -> {
               this.commitCruiseBox(this.cruiseBoxAxis(f as class_342), f as class_342)
               true
            }
            else -> {
               (f as class_342).method_25404(keyCode, scanCode, modifiers)
               true
            }
         }
      }
   }

   fun method_25406(mouseX: Double, mouseY: Double, button: Int): Boolean {
      this.method_25398(false)
      this.method_19355(mouseX, mouseY).filter({ p0: Any ->
         `$tmp0`(p0)
      }).isPresent() || super.method_25406(mouseX, mouseY, button)
   }

   public companion object {
      private final val pendingNames: HashMap<Long, String>
      private final val NUMERIC: Regex
      private final val PERCENT: Regex
      private const val PANEL_WIDTH: Int = 320
      private const val PANEL_HEIGHT: Int = 210
      private const val PANEL_BORDER: Int = -16777216
      private const val PANEL_BG: Int = -3750202
      private const val SEPARATOR: Int = -7631989
      private const val RENAME_BTN_W: Int = 38
      private const val RENAME_BTN_H: Int = 10
      private const val RENAME_BTN_X: Int = 276
      private const val RENAME_BTN_Y: Int = 5
      private const val RENAME_BOX_X: Int = 8
      private const val RENAME_BOX_Y: Int = 3
      private const val RENAME_BOX_W: Int = 150
      private const val RENAME_BOX_H: Int = 12
      private const val DIVIDER1_Y: Int = 18
      private const val DIVIDER2_Y: Int = 98
      private const val MODE_Y: Int = 24
      private const val MODE_ADV_X: Int = 24
      private const val MODE_VAN_X: Int = 175
      private const val HEADER_Y: Int = 40
      private const val ROW0_Y: Int = 54
      private const val ROW_DY: Int = 14
      private const val COL1_X: Int = 12
      private const val COL1_SUB_X: Int = 16
      private const val COL1_BOX_X: Int = 74
      private const val COL2_X: Int = 120
      private const val COL2_SUB_X: Int = 124
      private const val COL3_X: Int = 190
      private const val COL3_SUB_X: Int = 194
      private const val CRUISE_BOX_W: Int = 36
      private const val CRUISE_BOX_H: Int = 12
      private const val PANEL_RIGHT_MARGIN: Int = 6
      private const val PCT_BOX_W: Int = 30
      private const val PCT_BOX_H: Int = 12
      private const val PCT_BOX_X: Int = 284
      private const val PLUS_X: Int = 276
      private const val WEIGHT_TEXT_COLOR: Int = -2039584
      private const val MISC_X: Int = 14
      private const val MISC_Y: Int = 104
      private const val MISC_DY: Int = 13
      private const val BOX_H: Int = 15
      private const val INFO_Y0: Int = 150
      private const val BOX_ROW_DY: Int = 17
      private const val INFO_X: Int = 10
      private const val INFO_W: Int = 92
      private const val ENG_X: Int = 218
      private const val ENG_W: Int = 92
      private const val BTN_X: Int = 126
      private const val BTN_W: Int = 68
      private const val BTN_H: Int = 16
      private const val BTN_ROW_DY: Int = 18
      private const val INFO_MAX_SCALE: Float = 0.8F
      private const val INFO_TEXT: Int = -13092808
      private const val BOX_BORDER: Int = -12566464
      private const val BOX_BG: Int = -5197648
      private final val KEEP_ACTIVE_TEXT: class_5250
      private final val WATER_LOCK_TEXT: class_5250
      private final val ADVANCED_TEXT: class_5250
      private final val VANILLA_TEXT: class_5250
      private final val CRUISE_TEXT: class_5250
      private final val CRUISE_SPEED_TEXT: class_5250
      private final val CRUISE_TURN_TEXT: class_5250
      private final val CRUISE_VERTICAL_TEXT: class_5250
      private final val DISPLAY_HUD_TEXT: class_5250
      private final val SPEED_TEXT: class_5250
      private final val ALTITUDE_TEXT: class_5250
      private final val HEADING_TEXT: class_5250
      private final val ASSEMBLER_TEXT: class_5250
      private final val ASSEMBLER_FLOATER_TEXT: class_5250
      private final val ASSEMBLER_BALLOON_TEXT: class_5250
      private final val SHIP_WEIGHT_TEXT: class_5250
      private final val PLUS_TEXT: class_5250
      private final val RENAME_TEXT: class_5250
      private final val SAVE_TEXT: class_5250
      private final val ASSEMBLE_TEXT: class_5250
      private final val DISSEMBLE_TEXT: class_5250
      private final val ALIGN_TEXT: class_5250
      private final val ALIGNING_TEXT: class_5250

      private fun checkboxWidth(text: class_2561): Int {
         return 13 + class_310.method_1551().field_1772.method_27525(text as class_5348)
      }

      private fun cruiseRowY(row: Int): Int {
         return 54 + 14 * row
      }

      private fun boxRelY(y0: Int, row: Int): Int {
         return y0 + 17 * row
      }

      private fun boxRowY(y0: Int, row: Int): Int {
         return this.boxRelY(y0, row)
      }

      private fun btnRowY(row: Int): Int {
         return 150 + 18 * row
      }
   }
}
