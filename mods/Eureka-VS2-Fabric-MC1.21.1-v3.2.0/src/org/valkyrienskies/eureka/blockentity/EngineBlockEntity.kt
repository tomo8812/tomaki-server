package org.valkyrienskies.eureka.blockentity

import kotlin.jvm.internal.MutablePropertyReference1
import kotlin.jvm.internal.MutablePropertyReference1Impl
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KProperty
import net.minecraft.class_1262
import net.minecraft.class_1278
import net.minecraft.class_1657
import net.minecraft.class_1661
import net.minecraft.class_1662
import net.minecraft.class_1703
import net.minecraft.class_1737
import net.minecraft.class_1792
import net.minecraft.class_1799
import net.minecraft.class_1935
import net.minecraft.class_1937
import net.minecraft.class_2338
import net.minecraft.class_2350
import net.minecraft.class_2371
import net.minecraft.class_2382
import net.minecraft.class_2487
import net.minecraft.class_2561
import net.minecraft.class_2624
import net.minecraft.class_2680
import net.minecraft.class_2769
import net.minecraft.class_3218
import net.minecraft.class_5250
import net.minecraft.class_7225.class_7874
import org.joml.Math
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaProperties
import org.valkyrienskies.eureka.gui.engine.EngineScreenMenu
import org.valkyrienskies.eureka.registry.FuelRegistry
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.KtContainerData
import org.valkyrienskies.mod.common.VSGameUtilsKt

public class EngineBlockEntity(pos: class_2338, state: class_2680) : class_2624(EurekaBlockEntities.INSTANCE.ENGINE.get(), pos, state), class_1737, class_1278 {
   @JvmStatic
   @JvmSynthetic
   KProperty<Object>[] $$delegatedProperties = arrayOf(
      Reflection.mutableProperty1(MutablePropertyReference1Impl(EngineBlockEntity.class, "heatLevel", "getHeatLevel()I", 0) as MutablePropertyReference1),
      Reflection.mutableProperty1(MutablePropertyReference1Impl(EngineBlockEntity.class, "fuelLeft", "getFuelLeft()I", 0) as MutablePropertyReference1),
      Reflection.mutableProperty1(MutablePropertyReference1Impl(EngineBlockEntity.class, "fuelTotal", "getFuelTotal()I", 0) as MutablePropertyReference1)
   );
   public final val data: KtContainerData = KtContainerData()

   private final var heatLevel: Int by this.data.provideDelegate(this, $$delegatedProperties[0])
      private final get() {
         return this.heatLevel$delegate.getValue(this, $$delegatedProperties[0])
      }

      private final set(<set-?>) {
         this.heatLevel$delegate.setValue(this, $$delegatedProperties[0], `<set-?>`)
      }


   private final var fuelLeft: Int by this.data.provideDelegate(this, $$delegatedProperties[1])
      private final get() {
         return this.fuelLeft$delegate.getValue(this, $$delegatedProperties[1])
      }

      private final set(<set-?>) {
         this.fuelLeft$delegate.setValue(this, $$delegatedProperties[1], `<set-?>`)
      }


   private final var fuelTotal: Int by this.data.provideDelegate(this, $$delegatedProperties[2])
      private final get() {
         return this.fuelTotal$delegate.getValue(this, $$delegatedProperties[2])
      }

      private final set(<set-?>) {
         this.fuelTotal$delegate.setValue(this, $$delegatedProperties[2], `<set-?>`)
      }


   public final var fuel: class_1799
   private final var lastFuelValue: Int
   private final var heat: Float

   init {
      val var10001: class_1799 = class_1799.field_8037
      this.fuel = var10001
      this.lastFuelValue = 1600
   }

   private final val ship: LoadedServerShip?
      private final get() {
         val var10000: class_1937 = this.field_11863
         val var1: class_3218 = var10000 as class_3218
         val var10001: class_2338 = this.method_11016()
         return VSGameUtilsKt.getLoadedShipManagingPos(var1, var10001 as class_2382)
      }


   fun method_5465(containerId: Int, inventory: class_1661): class_1703 {
      EngineScreenMenu(containerId, inventory, this) as class_1703
   }

   fun method_17823(): class_2561 {
      val var10000: class_5250 = class_2561.method_43471("gui.vs_eureka.engine")
      var10000 as class_2561
   }

   fun method_11282(): class_2371<class_1799> {
      val var10000: class_2371 = class_2371.method_10212(this.fuel, arrayOfNulls(0))
      var10000
   }

   fun method_11281(nonNullList: class_2371<class_1799>) {
      if (!(nonNullList as java.util.Collection).isEmpty()) {
         this.fuel = CollectionsKt.first(nonNullList as java.util.List) as class_1799
      } else {
         this.fuel = class_1799.field_8037
      }
   }

   public fun tick() {
      var var10000: class_1937 = this.field_11863
      if (!var10000.field_9236) {
         var eurekaShipControl: EurekaShipControl
         run label82@{
            val var9: LoadedServerShip = this.ship
            eurekaShipControl = if (var9 != null) var9.getAttachment(EurekaShipControl.class) as EurekaShipControl else null
            if (eurekaShipControl != null) {
               var10 = eurekaShipControl.engineCfg
               if (var10 != null) {
                  return@label82
               }
            }

            var10 = EurekaConfig.SERVER
         }

         val maxEffectiveFuel: Float = 100.0F - var10.engineHeatGain
         var10000 = this.field_11863
         val isPowered: Boolean = var10000.method_49803(this.method_11016())
         if (EurekaConfig.SERVER.engineRedstoneBehaviorPause && isPowered) {
            if (eurekaShipControl != null) {
               eurekaShipControl.reportEngineFuel(this.fuelFraction())
            }
         } else {
            if (!isPowered) {
               if (this.fuelLeft > 0) {
                  if (EurekaConfig.SERVER.engineFuelSaving) {
                     if (this.heat <= maxEffectiveFuel) {
                        this.heat = this.heat + this.scaleEngineHeating(var10.engineHeatGain)
                        this.fuelLeft = this.fuelLeft + -1
                     }
                  } else {
                     this.fuelLeft = this.fuelLeft + -1
                     if (this.heat <= maxEffectiveFuel) {
                        this.heat = this.heat + this.scaleEngineHeating(var10.engineHeatGain)
                     }
                  }

                  if (!this.fuel.method_7960() && this.lastFuelValue <= EurekaConfig.SERVER.engineMinCapacity - this.fuelLeft) {
                     this.consumeFuel()
                  }
               } else if (!this.fuel.method_7960()) {
                  this.consumeFuel()
               }
            }

            val var8: Int = this.heatLevel
            this.heatLevel = Math.min((int)((float)java.lang.Math.ceil((double)(this.heat * 4.0F / 100.0F))), 4)
            if (var8 != this.heatLevel) {
               var10000 = this.field_11863
               var10000.method_8652(
                  this.method_11016(), this.method_11010().method_11657(EurekaProperties.INSTANCE.HEAT as class_2769, this.heatLevel) as class_2680, 11
               )
            }

            if (this.heat > 0.0F) {
               if (eurekaShipControl != null) {
                  var effectiveHeat: Float = 1.0F
                  if (this.heat < maxEffectiveFuel) {
                     effectiveHeat = this.heat / 100.0F
                  }

                  eurekaShipControl.powerLinear = eurekaShipControl.powerLinear
                     + (double)Math.lerp(EurekaConfig.SERVER.enginePowerLinearMin, var10.enginePowerLinear, effectiveHeat)
                     eurekaShipControl.powerAngular = eurekaShipControl.powerAngular
                     + (double)Math.lerp(EurekaConfig.SERVER.enginePowerAngularMin, EurekaConfig.SERVER.enginePowerAngular, effectiveHeat)
                     this.heat = this.heat - eurekaShipControl.consumed
               }

               this.heat = java.lang.Math.max(this.heat - this.scaleEngineCooling(EurekaConfig.SERVER.engineHeatLoss), 0.0F)
            }

            if (eurekaShipControl != null) {
               eurekaShipControl.reportEngineFuel(this.fuelFraction())
            }
         }
      }
   }

   private fun fuelFraction(): Double {
      val burnPerItem: Int = if (!this.fuel.method_7960()) this.getScaledFuel() else this.lastFuelValue
      return if (burnPerItem <= 0)
         0.0
         else
         RangesKt.coerceIn(
            ((double)this.fuelLeft / (double)burnPerItem + (double)this.fuel.method_7947())
               / (double)(if (!this.fuel.method_7960()) this.fuel.method_7914() else 64),
            0.0,
            1.0
         )
      }

   public fun isBurning(): Boolean {
      return this.fuelLeft > 0
   }

   private fun getScaledFuel(): Int {
      return (int)(FuelRegistry.INSTANCE.get(this.fuel) * EurekaConfig.SERVER.engineFuelMultiplier)
   }

   private fun consumeFuel() {
      this.lastFuelValue = this.getScaledFuel()
      if (this.lastFuelValue > 0) {
         if (this.fuelLeft > 0 && this.lastFuelValue > EurekaConfig.SERVER.engineMinCapacity - this.fuelLeft) {
            return
         }

         this.fuelLeft = this.fuelLeft + this.lastFuelValue
         this.fuelTotal = java.lang.Math.max(this.lastFuelValue, EurekaConfig.SERVER.engineMinCapacity)
         if (this.fuel.method_7909().method_7857()) {
            val var10003: class_1792 = this.fuel.method_7909().method_7858()
            this.fuel = class_1799(var10003 as class_1935, 1)
         } else {
            this.method_5434(0, 1)
         }

         this.method_5431()
      }
   }

   private fun scaleEngineHeating(value: Float): Float {
      return (100 * EurekaConfig.SERVER.engineHeatChangeExponent - this.heat * EurekaConfig.SERVER.engineHeatChangeExponent + 1.0F) * value
   }

   private fun scaleEngineCooling(value: Float): Float {
      return (this.heat * EurekaConfig.SERVER.engineHeatChangeExponent + 1.0F) * value
   }

   fun method_11007(tag: class_2487, provider: class_7874) {
      if (!this.fuel.method_7960()) {
         tag.method_10566("FuelSlot", this.fuel.method_57358(provider))
      }

      tag.method_10569("FuelLeft", this.fuelLeft)
      tag.method_10569("PrevFuelTotal", this.fuelTotal)
      tag.method_10548("Heat", this.heat)
      super.method_11007(tag, provider)
   }

   fun method_11014(compoundTag: class_2487, provider: class_7874) {
      this.fuel = class_1799.method_57359(provider, compoundTag.method_10562("FuelSlot"))
      this.fuelLeft = compoundTag.method_10550("FuelLeft")
      this.fuelTotal = compoundTag.method_10550("PrevFuelTotal")
      this.heat = compoundTag.method_10583("Heat")
      super.method_11014(compoundTag, provider)
   }

   fun method_5448() {
      this.fuel = class_1799.field_8037
   }

   fun method_5439(): Int {
      1
   }

   fun method_5442(): Boolean {
      this.fuel.method_7960()
   }

   fun method_5438(slot: Int): class_1799 {
      val var10000: class_1799
      if (slot == 0) {
         var10000 = this.fuel
      } else {
         var10000 = class_1799.field_8037
      }

      var10000
   }

   fun method_5434(slot: Int, amount: Int): class_1799 {
      val var10000: class_1799 = class_1262.method_5430(CollectionsKt.listOf(this.fuel), slot, amount)
      var10000
   }

   fun method_5441(slot: Int): class_1799 {
      if (slot == 0) {
         this.fuel = class_1799.field_8037
      }

      val var10000: class_1799 = class_1799.field_8037
      var10000
   }

   fun method_5447(slot: Int, stack: class_1799) {
      if (slot == 0) {
         this.fuel = stack
      }
   }

   fun method_5431() {
      if (super.field_11863 != null) {
         class_2624.method_31663(super.field_11863, super.field_11867, this.method_11010())
      }
   }

   fun method_5443(player: class_1657): Boolean {
      val var10000: class_1937 = super.field_11863
      var10000.method_8321(super.field_11867) === this
         && player.method_5649(
               (double)super.field_11867.method_10263() + 0.5, (double)super.field_11867.method_10264() + 0.5, (double)super.field_11867.method_10260() + 0.5
            )
            <= 64.0
         }

   fun method_5494(side: class_2350): IntArray {
      intArrayOf(0)
   }

   fun method_5492(index: Int, itemStack: class_1799, direction: class_2350?): Boolean {
      direction != class_2350.field_11033 && this.method_5437(index, itemStack)
   }

   fun method_5493(index: Int, stack: class_1799, direction: class_2350): Boolean {
      index == 0 && direction === class_2350.field_11033 && !this.fuel.method_7960() && FuelRegistry.INSTANCE.get(this.fuel) <= 0
   }

   fun method_5437(index: Int, stack: class_1799): Boolean {
      index == 0 && FuelRegistry.INSTANCE.get(stack) > 0
   }

   fun method_7683(helper: class_1662) {
      helper.method_7400(this.fuel)
   }
}
