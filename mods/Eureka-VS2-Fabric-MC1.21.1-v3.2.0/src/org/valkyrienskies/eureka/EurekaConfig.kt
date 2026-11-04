package org.valkyrienskies.eureka

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema
import org.jetbrains.annotations.NotNull

public object EurekaConfig {
   @JvmField
   @NotNull
   public final val CLIENT: org.valkyrienskies.eureka.EurekaConfig.Client = EurekaConfig.Client()

   @JvmField
   @NotNull
   public final val ADVANCED: org.valkyrienskies.eureka.EurekaConfig.Server = EurekaConfig.Server()

   @JvmField
   @NotNull
   public final val VANILLA: org.valkyrienskies.eureka.EurekaConfig.Server

   @JvmField
   @NotNull
   public final val SERVER: org.valkyrienskies.eureka.EurekaConfig.Server = ADVANCED

   @JvmStatic
   fun {
      val var0: EurekaConfig.Server = EurekaConfig.Server()
      var0.enginePowerLinear = 500000.0F
      var0.engineHeatGain = 0.03F
      var0.engineBoost = 0.2
      var0.engineBoostOffset = 2.5
      var0.maxReverseSpeedFromEngines = 8.0
      var0.baseImpulseElevationRate = 2.0
      var0.baseImpulseDescendRate = 4.0
      var0.turnSpeed = 3.0
      var0.turnAcceleration = 10.0
      VANILLA = var0
   }

   public class Client {
      @JsonSchema(
         description = "Master toggle for the piloted-ship HUD. When off, the Speed/Altitude/Heading readouts are all hidden (and greyed out in the helm menu)."
      )
      public final var displayHud: Boolean = true

      @JsonSchema(description = "Show the piloted ship's speed as small text at the top-center of the screen.")
      public final var displaySpeed: Boolean = true

      @JsonSchema(description = "Show the piloted ship's altitude (Y) at the top-center of the screen.")
      public final var displayAltitude: Boolean = true

      @JsonSchema(description = "Show the piloted ship's compass heading at the top-center of the screen.")
      public final var displayHeading: Boolean = true
   }

   public class Server {
      @JsonSchema(description = "Movement power per engine when heated fully")
      public final var enginePowerLinear: Float = 100000.0F

      @JsonSchema(description = "Movement power per engine with minimal heat")
      public final var enginePowerLinearMin: Float = 10000.0F

      @JsonSchema(description = "Turning power per engine when heated fully")
      public final var enginePowerAngular: Float = 1.0F

      @JsonSchema(description = "Turning power per engine when minimal heat")
      public final var enginePowerAngularMin: Float

      @JsonSchema(description = "The amount of heat a engine loses per tick")
      public final var engineHeatLoss: Float = 0.01F

      @JsonSchema(description = "The amount of heat a gain per tick (when burning)")
      public final var engineHeatGain: Float = 0.09F

      @JsonSchema(description = "Increases heat gained at low heat level, and increased heat decreases when at high heat and not consuming fuel")
      public final var engineHeatChangeExponent: Float = 0.1F

      @JsonSchema(description = "Pause fuel consumption and power when block is powered")
      public final var engineRedstoneBehaviorPause: Boolean

      @JsonSchema(description = "Number of Balloons a single engine can power. 0 disables the feature")
      public final var maxBalloonsPerEngine: Int

      @JsonSchema(description = "Avoids consuming fuel when heat is 100%")
      public final var engineFuelSaving: Boolean

      @JsonSchema(description = "Increasing this value will result in more items being able to converted to fuel")
      public final var engineMinCapacity: Int = 2000

      @JsonSchema(description = "Fuel burn time multiplier")
      public final var engineFuelMultiplier: Float = 2.0F

      @JsonSchema(description = "Extra engine power for when having multiple engines per engine")
      public final var engineBoost: Double = 4.0

      @JsonSchema(description = "At what amount of engines the boost will start taking effect")
      public final var engineBoostOffset: Double = 5.0

      @JsonSchema(description = "The final linear boost will be raised to the power of 2, and the result of the delta is multiple by this value")
      public final var engineBoostExponentialPower: Double = 1.0E-6

      @JsonSchema(description = "Max speed of a ship with engines (actual max speed varies with engines and mass.)")
      public final var maxSpeedFromEngines: Double = 24.0

      @JsonSchema(description = "Max reverse speed of a ship with engines")
      public final var maxReverseSpeedFromEngines: Double = 12.0

      @JsonSchema(description = "The speed at which the ship stabilizes")
      public final var stabilizationSpeed: Double = 10.0

      @JsonSchema(description = "The amount extra that each floater will make the ship float, per kg mass")
      public final var floaterBuoyantFactorPerKg: Double = 50000.0

      @JsonSchema(description = "The maximum amount extra each floater will multiply the buoyant force by, irrespective of mass")
      public final var maxFloaterBuoyantFactor: Double = 1.0

      @JsonSchema(description = "how much the mass decreases the speed.")
      public final var speedMassScale: Double = 1.0

      @JsonSchema(description = "The speed a ship with no engines can move at")
      public final var baseSpeed: Double = 3.0

      @JsonSchema(description = "Forward/backward thrust multiplier for a ship grounded on land, to overcome ground friction. 1 = no assist")
      public final var landThrustAssist: Double = 5.33

      @JsonSchema(description = "Forward/backward thrust multiplier for a ship travelling on water. 1 = no assist")
      public final var waterThrustAssist: Double = 8.0

      @JsonSchema(description = "Vertical sensitivity when ascending")
      public final var baseImpulseElevationRate: Double = 5.0

      @JsonSchema(description = "Vertical sensitivity when descending")
      public final var baseImpulseDescendRate: Double = 10.0

      @JsonSchema(description = "The max elevation speed boost gained by having extra extra balloons")
      public final var balloonElevationMaxSpeed: Double = 5.5

      @JsonSchema(description = "Ascend and descend acceleration")
      public final var elevationSnappiness: Double = 1.0

      @JsonSchema(description = "Allow Eureka controlled ships to be affected by fluid drag")
      public final var doFluidDrag: Boolean

      @JsonSchema(description = "Amount of mass in kg a balloon can lift")
      public final var massPerBalloon: Double = 5000.0

      @JsonSchema(
         description = "Multiplier on balloon FLIGHT LIFT -- the anti-gravity up-force balloons apply in air and water alike. This is NOT water buoyancy. 1 = normal lift, 0 = balloons provide no lift (debug lever for ships hovering above the waterline). Staying afloat on water is a separate system: see floaterBuoyantFactorPerKg / maxFloaterBuoyantFactor."
      )
      public final var balloonLiftMultiplier: Double = 1.0

      @JsonSchema(
         description = "Water altitude-hold: a HYBRID ship (has both floaters AND balloons) pins its current Y the moment its keel touches water, so it sails on the surface instead of balloon lift floating it back into the air. Hands off the helm (or cruise) = hold; press descend/ascend to choose a new depth, which re-latches when you let go; rising clear of the water returns to normal in-air hover. Vertical-only, so sailing speed is unaffected."
      )
      public final var enableWaterAltitudeHold: Boolean = true

      @JsonSchema(
         description = "Stiffness of the water altitude-hold spring. Higher pins the Y tighter and faster but can feel abrupt; lower is softer and may sag slightly. Critically damped, so it never oscillates. Default 9.0."
      )
      public final var waterAltitudeHoldStiffness: Double = 9.0

      @JsonSchema(
         description = "How submerged the ship must be (fraction 0..1) for the water altitude-hold to ENGAGE. Once engaged it stays until the hull fully clears the water. Small = engages as soon as the keel touches. Default 0.05."
      )
      public final var waterAltitudeHoldMinOverlap: Double = 0.05

      @JsonSchema(
         description = "Base turn rate -- the turning speed for taps and short holds. Holding ramps the ship up to this rate over turnAccelDelay seconds, so a TAP only reaches a small fraction of it (fine, pixel-level steering at low values). Engines no longer affect turning, so this and turnAcceleration fully control it. Low = slow, gentle turns; high = fast base turns."
      )
      public final var turnSpeed: Double = 0.5

      @JsonSchema(
         description = "Extra turn sharpness that engages ONLY after holding a turn longer than turnAccelDelay -- then the turn rate climbs beyond the turnSpeed base, sharper the longer you hold. Tapping never triggers it. 0 = turns stay at the turnSpeed base rate with no acceleration."
      )
      public final var turnAcceleration: Double = 8.0

      @JsonSchema(
         description = "How long (seconds) a turn key must be held before turnAcceleration kicks in. Below this, only turnSpeed applies -- so tap-tap-tap gives small, fine orbit adjustments while a sustained hold ramps the turn sharper. Default 0.6."
      )
      public final var turnAccelDelay: Double = 0.6

      @JsonSchema(
         description = "Seconds you must hold the OPPOSITE turn (A/D) to cancel a locked orbit while cruising (leaving horizontal/vertical cruise running). Taps/shorter holds only ADD influence and never cancel. Canceling the last active set turns cruise off. Default 3.0."
      )
      public final var turnCancelHold: Double = 3.0

      @JsonSchema(
         description = "Seconds you must hold the OPPOSITE forward/back input (W/S) to cancel the horizontal cruise set while cruising (leaving turn/vertical running). Taps only ADD speed influence. Canceling the last active set turns cruise off. Default 3.0."
      )
      public final var horizontalCancelHold: Double = 3.0

      @JsonSchema(
         description = "Seconds you must hold the OPPOSITE ascend/descend input (Space/V) to cancel the vertical cruise set while cruising (leaving horizontal/turn running). Taps only ADD climb influence. Canceling the last active set turns cruise off. Default 3.0."
      )
      public final var verticalCancelHold: Double = 3.0

      @JsonSchema(
         description = "Lock-in turn cruise: while CRUISING, holding a turn key spins the ship up and the achieved turn RATE is latched when you release -- so the ship holds a constant-radius circle hands-off instead of straightening. A brief tap = a gentle wide orbit; a longer hold = a sharper orbit (up to turnSpeed). Steer the opposite way to widen or straighten out. false = turning while cruising just steers live and brakes back to straight on release (legacy)."
      )
      public final var enableTurnCruise: Boolean = true

      @JsonSchema(
         description = "The maximum distance from center of mass to one end of the ship considered by the turn speed. At it's default of 16, it ensures that really large ships will turn at the same speed as a ship with a center of mass only 16 blocks away from the farthest point in the ship. That way, large ships do not turn painfully slowly"
      )
      public final var maxSizeForTurnSpeedPenalty: Double = 16.0

      @JsonSchema(description = "How much torque a ship will apply to try and keep level")
      public final var stabilizationTorqueConstant: Double = 15.0

      @JsonSchema(description = "How fast a ship will stop. 1 = fast stop, 0 = slow stop")
      public final var linearStabilizeMaxAntiVelocity: Double = 1.0

      @JsonSchema(description = "Stronger stabilization with higher mass, less at higher speeds.")
      public final var scaledInstability: Double = 70.0

      @JsonSchema(description = "Less stabilization at higher speed.")
      public final var unscaledInstability: Double = 0.1

      @JsonSchema(description = "How fast a ship will stop and accelerate.")
      public final var linearMassScaling: Double = 2.0E-4

      @JsonSchema(description = "Base mass for linear acceleration in Kg.")
      public final var linearBaseMass: Double = 50.0

      @JsonSchema(description = "Max smoothing value, will smooth out before reaching max value.")
      public final var linearMaxMass: Double = 10000.0

      @JsonSchema(description = "Max unscaled speed in m/s without engines.")
      public final var linearCasualSpeed: Double = 3.0

      @JsonSchema(description = "How much inertia affects Eureka ships. Max 10 = full inertia")
      public final var antiVelocityMassRelevance: Double = 0.8

      @JsonSchema(description = "Chance for popped balloons to pop adjacent balloons, per side")
      public final var popSideBalloonChance: Double = 0.3

      @JsonSchema(description = "Whether the ship helm assembles diagonally connected blocks or not")
      public final var diagonals: Boolean = true

      @JsonSchema(description = "Weight of ballast when lowest redstone power")
      public final var ballastWeight: Double = 10000.0

      @JsonSchema(description = "Weight of ballast when highest redstone power")
      public final var ballastNoWeight: Double = 1000.0

      @JsonSchema(description = "Whether or not disassembly is permitted")
      public final var allowDisassembly: Boolean = true

      @JsonSchema(description = "Maximum number of blocks allowed in a ship. Set to 0 for no limit")
      public final var maxShipBlocks: Int = '썐'

      @JsonSchema(description = "Blacklist of blocks that don't get assembled (Use Block Tag instead)")
      @NotNull
      public final var blockBlacklist: Set<String> =
         SetsKt.setOf(arrayOf("minecraft:water", "minecraft:sand", "minecraft:gravel", "minecraft:lava", "minecraft:fire", "minecraft:bedrock"))

      @JsonSchema(
         description = "Dev: action-bar a message each time a per-set cruise HOLD-cancel fires (Horizontal/Vertical/Turn). Read globally off EurekaConfig.SERVER; toggle in-game with /vs cruise-cancel-debug <bool>."
      )
      public final var debugCruiseCancel: Boolean

      @JsonSchema(
         description = "Eureka Assembler: the target ASCEND speed in m/s the 'balloon' auto-fill sizes for. Sizing balloons for neutral hover (= 0 here, the old behavior) lets a heavy ship hold itself up but never climb, because the ship's usable climb force is only the balloon lift SURPLUS over its weight. This adds mass-proportional lift headroom so every assembled ship -- light hull or heavy -- can actually ascend at about this rate. Higher = climbs faster but converts more hull blocks to balloons; 0 = neutral hover only (may not climb). /vs get-ship-weight uses the same target, so its reported 'needed' balloon count matches what the assembler places."
      )
      public final var assemblerBalloonAscendRate: Double = 2.0

      @JsonSchema(
         description = "Eureka Assembler: ordered block ids the 'floater' auto-fill replaces, in priority order (planks first -- oak..bamboo -- then logs/stripped/bark). Add modded wood ids here to make them convertible. Wooden slabs are intentionally excluded (a double slab is not a plank)."
      )
      @NotNull
      public final var assemblerFloaterReplaceWhitelist: List<String> =
         CollectionsKt.listOf(
            arrayOf(
               "minecraft:oak_planks",
               "minecraft:spruce_planks",
               "minecraft:birch_planks",
               "minecraft:jungle_planks",
               "minecraft:acacia_planks",
               "minecraft:dark_oak_planks",
               "minecraft:crimson_planks",
               "minecraft:warped_planks",
               "minecraft:mangrove_planks",
               "minecraft:cherry_planks",
               "minecraft:bamboo_planks",
               "minecraft:oak_log",
               "minecraft:stripped_oak_log",
               "minecraft:oak_wood",
               "minecraft:stripped_oak_wood",
               "minecraft:spruce_log",
               "minecraft:stripped_spruce_log",
               "minecraft:spruce_wood",
               "minecraft:stripped_spruce_wood",
               "minecraft:birch_log",
               "minecraft:stripped_birch_log",
               "minecraft:birch_wood",
               "minecraft:stripped_birch_wood",
               "minecraft:jungle_log",
               "minecraft:stripped_jungle_log",
               "minecraft:jungle_wood",
               "minecraft:stripped_jungle_wood",
               "minecraft:acacia_log",
               "minecraft:stripped_acacia_log",
               "minecraft:acacia_wood",
               "minecraft:stripped_acacia_wood",
               "minecraft:dark_oak_log",
               "minecraft:stripped_dark_oak_log",
               "minecraft:dark_oak_wood",
               "minecraft:stripped_dark_oak_wood",
               "minecraft:crimson_stem",
               "minecraft:stripped_crimson_stem",
               "minecraft:crimson_hyphae",
               "minecraft:stripped_crimson_hyphae",
               "minecraft:warped_stem",
               "minecraft:stripped_warped_stem",
               "minecraft:warped_hyphae",
               "minecraft:stripped_warped_hyphae",
               "minecraft:mangrove_log",
               "minecraft:stripped_mangrove_log",
               "minecraft:mangrove_wood",
               "minecraft:stripped_mangrove_wood",
               "minecraft:cherry_log",
               "minecraft:stripped_cherry_log",
               "minecraft:cherry_wood",
               "minecraft:stripped_cherry_wood",
               "minecraft:bamboo_block",
               "minecraft:stripped_bamboo_block"
            )
         )

      @JsonSchema(
         description = "Eureka Assembler: ordered block ids the 'balloon' auto-fill replaces, in priority order. Defaults to every wool color (first), then every concrete color. Add block ids here to expand what balloons may be built from."
      )
      @NotNull
      public final var assemblerBalloonReplaceWhitelist: List<String> =
         CollectionsKt.listOf(
            arrayOf(
               "minecraft:white_wool",
               "minecraft:orange_wool",
               "minecraft:magenta_wool",
               "minecraft:light_blue_wool",
               "minecraft:yellow_wool",
               "minecraft:lime_wool",
               "minecraft:pink_wool",
               "minecraft:gray_wool",
               "minecraft:light_gray_wool",
               "minecraft:cyan_wool",
               "minecraft:purple_wool",
               "minecraft:blue_wool",
               "minecraft:brown_wool",
               "minecraft:green_wool",
               "minecraft:red_wool",
               "minecraft:black_wool",
               "minecraft:white_concrete",
               "minecraft:orange_concrete",
               "minecraft:magenta_concrete",
               "minecraft:light_blue_concrete",
               "minecraft:yellow_concrete",
               "minecraft:lime_concrete",
               "minecraft:pink_concrete",
               "minecraft:gray_concrete",
               "minecraft:light_gray_concrete",
               "minecraft:cyan_concrete",
               "minecraft:purple_concrete",
               "minecraft:blue_concrete",
               "minecraft:brown_concrete",
               "minecraft:green_concrete",
               "minecraft:red_concrete",
               "minecraft:black_concrete"
            )
         )
      }
}
