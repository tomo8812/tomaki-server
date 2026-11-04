package org.valkyrienskies.mod.common.config

import org.jetbrains.annotations.NotNull
import org.valkyrienskies.core.internal.config.ConfigCategory
import org.valkyrienskies.core.internal.config.ConfigEntry
import org.valkyrienskies.mod.mixinducks.feature.mass_tooltip.MassTooltipVisibility

public object VSGameConfig {
   @JvmField
   @NotNull
   public final val CLIENT: org.valkyrienskies.mod.common.config.VSGameConfig.Client = VSGameConfig.Client()

   @JvmField
   @NotNull
   public final val SERVER: org.valkyrienskies.mod.common.config.VSGameConfig.Server = VSGameConfig.Server()

   @JvmField
   @NotNull
   public final val COMMON: org.valkyrienskies.mod.common.config.VSGameConfig.Common = VSGameConfig.Common()

   public class Client {
      @ConfigCategory(title = "Tooltips")
      @NotNull
      public final val Tooltip: org.valkyrienskies.mod.common.config.VSGameConfig.Client.TOOLTIP = VSGameConfig.Client.TOOLTIP()

      @ConfigCategory(title = "Block Tint")
      @NotNull
      public final val BlockTinting: org.valkyrienskies.mod.common.config.VSGameConfig.Client.BLOCKTINT = VSGameConfig.Client.BLOCKTINT()

      @ConfigCategory(title = "SoundEffect")
      @NotNull
      public final val SoundEffect: org.valkyrienskies.mod.common.config.VSGameConfig.Client.SOUNDEFFECT = VSGameConfig.Client.SOUNDEFFECT()

      @ConfigCategory(title = "Connectivity")
      @NotNull
      public final val Connectivity: org.valkyrienskies.mod.common.config.VSGameConfig.Client.CONNECTIVITY = VSGameConfig.Client.CONNECTIVITY()

      @ConfigEntry(description = "Renders the VS2 debug HUD with TPS")
      public final var renderDebugText: Boolean

      @ConfigEntry(description = "Grace period before a player's camera is no longer considered to be within a sealed area")
      public final var sealedAreaCameraGracePeriod: Int = 2

      @ConfigEntry(description = "Recommend ship slugs in mc commands where player names could be used ex. /tp ship-name wich could pollute user autocomplete")
      public final var recommendSlugsInMcCommands: Boolean = true

      @ConfigEntry(description = "The way ships are rendered by default")
      @NotNull
      public final var defaultRenderer: ShipRenderer = ShipRenderer.VANILLA

      public class BLOCKTINT {
         @ConfigEntry(description = "Partly fixes the block tinting issue with blocks on ships")
         public final var fixBlockTinting: Boolean
      }

      public class CONNECTIVITY {
         @ConfigEntry(
            description = "Enable client connectivity; increases client load, but allows for client-sided sealed space visuals, like occluding water in a submarine."
         )
         public final var enableClientConnectivity: Boolean
      }

      public class SOUNDEFFECT {
         @ConfigEntry(description = "Enable doppler effect for sounds")
         public final var enableDopplerEffect: Boolean = true

         @ConfigEntry(description = "Doppler effect scaler. 1.0 for realistic; smaller value results in weaker doppler effect")
         public final var dopplerEffectScale: Double = 0.05
      }

      public class TOOLTIP {
         @ConfigEntry(description = "Set when the Mass Tooltip is Visible")
         @NotNull
         public final var massTooltipVisibility: MassTooltipVisibility = MassTooltipVisibility.ADVANCED

         @ConfigEntry(description = "Use Imperial Units to show Mass")
         public final var useImperialUnits: Boolean
      }
   }

   public class Common {
      @ConfigCategory(title = "Advanced")
      @JvmField
      @NotNull
      public final val ADVANCED: org.valkyrienskies.mod.common.config.VSGameConfig.Common.Advanced = VSGameConfig.Common.Advanced()

      public class Advanced {
         @ConfigEntry(
            description = "Renders mob pathfinding nodes. Must be set on client and server to work. Requires the system property -Dorg.valkyrienskies.render_pathfinding=true"
         )
         public final var renderPathfinding: Boolean
      }
   }

   public class Server {
      @ConfigCategory(title = "FTB Chunks")
      @NotNull
      public final val FTBChunks: org.valkyrienskies.mod.common.config.VSGameConfig.Server.FTBCHUNKS = VSGameConfig.Server.FTBCHUNKS()

      @ConfigCategory(title = "ComputerCraft")
      @NotNull
      public final val ComputerCraft: org.valkyrienskies.mod.common.config.VSGameConfig.Server.COMPUTERCRAFT = VSGameConfig.Server.COMPUTERCRAFT()

      @ConfigCategory(title = "Weather 2")
      @NotNull
      public final val Weather2: org.valkyrienskies.mod.common.config.VSGameConfig.Server.WEATHER2 = VSGameConfig.Server.WEATHER2()

      @ConfigCategory(title = "Dynmap")
      @NotNull
      public final val Dynmap: org.valkyrienskies.mod.common.config.VSGameConfig.Server.DYNMAP = VSGameConfig.Server.DYNMAP()

      @ConfigCategory(title = "CBC")
      @NotNull
      public final val Cbc: org.valkyrienskies.mod.common.config.VSGameConfig.Server.CBC = VSGameConfig.Server.CBC()

      @ConfigCategory(title = "Ocean Waves")
      @NotNull
      public final val OceanWaves: org.valkyrienskies.mod.common.config.VSGameConfig.Server.OCEANWAVES = VSGameConfig.Server.OCEANWAVES()

      @ConfigEntry(
         description = "Moving ships silently cut away the kelp their hull physically passes through, instead of leaving it clipped inside the boat. Only the touched kelp goes -- the rest of the strand stays and nothing is dropped. Seagrass and other plants are left alone (they already phase harmlessly through ships)."
      )
      public final var shipsDestroyPlants: Boolean = true

      @ConfigEntry(
         description = "By default, the vanilla server prevents block interacts past a certain distance to prevent cheat clients from breaking blocks halfway across the map. This approach breaks down in the face of extremely large ships, where the distance from the block origin to the nearest face is greater than the interact distance check allows."
      )
      public final var enableInteractDistanceChecks: Boolean = true

      @ConfigEntry(description = "If true, enables buoyancy from serverside air pockets.")
      public final var enablePocketBuoyancy: Boolean

      @ConfigEntry(description = "Buoyancy factor added per cubic meter of air pocket inside a ship")
      public final var buoyancyFactorPerPocketVolume: Double = 0.05

      @ConfigEntry(description = "If true, teleportation into the shipyard is redirected to the ship it belongs to instead.")
      public final var transformTeleports: Boolean = true

      @ConfigEntry(
         description = "By default, the server checks that player movement is legal, and if it isn't, rubber-bands the player with the infamous \"moved too quickly\" message. Since players on VS ships will move illegally, they will be affected by this check frequently. This option disables that check. (it doesn't work very well anyway, don't worry)"
      )
      public final var enableMovementChecks: Boolean

      @ConfigEntry(
         description = "If true, when a player disconnects, their position on the ship is saved such that if the ship is moved, when they reconnect they will be teleported to the same position in the ship as they left, instead of being left behind."
      )
      public final var teleportReconnectedPlayers: Boolean = true

      @ConfigEntry(
         description = "If true, when a mob gets unloaded, its position on a ship is saved such that if the ship is moved, when the mob loads back in it will be teleported to the same position in the ship. This helps prevent mobs from falling off of ships."
      )
      public final var saveMobsPositionOnShip: Boolean = true

      @ConfigEntry(description = "If true, prevents water and other fluids from flowing out of the ship's bounding box.")
      public final var preventFluidEscapingShip: Boolean = true

      @ConfigEntry(description = "If true, prevents vines from growing beyond the ship's bounding box.")
      public final var preventVinesEscapingShip: Boolean = true

      @ConfigEntry(description = "Blast force in newtons of a TNT explosion at the center of the explosion.")
      public final var explosionBlastForce: Double = 500000.0

      @ConfigEntry(description = "Allow natural mob spawning on ships")
      public final var allowMobSpawns: Boolean = true

      @ConfigEntry(description = "Allow rudimentary pathfinding on ships")
      public final var aiOnShips: Boolean = true

      @ConfigEntry(description = "Scale of the mini ship creator")
      public final var miniShipSize: Double = 0.5

      @ConfigEntry(description = "Minimum scale of ships")
      public final var minScaling: Double = 0.25

      @ConfigEntry(description = "Default mass for blocks that do not have it defined in data or code. Blocks with masses below 100 float in water")
      public final var defaultBlockMass: Double = 1000.0

      @ConfigEntry(description = "Default elasticity coefficient for blocks. Higher values make blocks more bouncy")
      public final var defaultBlockElasticity: Double = 0.3

      @ConfigEntry(description = "Default friction coefficient for blocks. Lower values make blocks more slippery")
      public final var defaultBlockFriction: Double = 0.5

      @ConfigEntry(description = "Default block hardness (unused value, placeholder for later)")
      public final var defaultBlockHardness: Double = 1.0

      @ConfigEntry(description = "Enable splitting in worldspace. (Experimental!)")
      public final var enableWorldSplitting: Boolean

      @ConfigEntry(
         description = "The default grace timer for splitting. A split won't occur after a block break at a position until this many ticks have passed. Note that setting this too high may prevent things like explosions from properly launching split ships. (in ticks)"
      )
      public final var defaultSplitGraceTimer: Int = 2

      @ConfigCategory(title = "Commands")
      @NotNull
      public final val Commands: org.valkyrienskies.mod.common.config.VSGameConfig.Server.COMMANDS = VSGameConfig.Server.COMMANDS()

      public class CBC {
         @ConfigEntry(description = "Should cannon shots apply a recoil force to ships")
         public final var shellRecoil: Boolean

         @ConfigEntry(description = "The force multiplier applied to recoil on ships")
         public final var shellRecoilMult: Double = 500000.0
      }

      public class COMMANDS {
         @ConfigEntry(description = "The permission level required to use the /vs delete command. Must be 0 <= x <= 4")
         public final var deleteShipCommandPerms: Int = 2

         @ConfigEntry(description = "The permission level required to use the /vs get-ship command. Must be 0 <= x <= 4")
         public final var getShipCommandPerms: Int

         @ConfigEntry(description = "The permission level required to use the /vs get-air and /vs get-gravity command. Must be 0 <= x <= 4")
         public final var getAirValuesPerms: Int

         @ConfigEntry(description = "The permission level required to use the /vs rename command. Must be 0 <= x <= 4")
         public final var renameShipCommandPerms: Int = 2

         @ConfigEntry(description = "The permission level required to use the /vs remass command. Must be 0 <= x <= 4")
         public final var remassShipCommandPerms: Int = 2

         @ConfigEntry(description = "The permission level required to use the /vs scale command. Must be 0 <= x <= 4")
         public final var scaleShipCommandPerms: Int = 2

         @ConfigEntry(description = "The permission level required to use the /vs set-static command. Must be 0 <= x <= 4")
         public final var setStaticShipCommandPerms: Int = 2

         @ConfigEntry(description = "The permission level required to use the /vs teleport command. Must be 0 <= x <= 4")
         public final var teleportShipCommandPerms: Int = 2

         @ConfigEntry(description = "The permission level required to use the /vs backend command. Must be 0 <= x <= 4")
         public final var changeBackendCommandPerms: Int = 4

         @ConfigEntry(description = "The permission level required to use the /vs dry command. Must be 0 <= x <= 4")
         public final var dryShipCommandPerms: Int = 2

         @ConfigEntry(description = "The permission level required to use the /vs desnow command. Must be 0 <= x <= 4")
         public final var desnowShipCommandPerms: Int = 2
      }

      public class COMPUTERCRAFT {
         @ConfigEntry(description = "Turtles leaving scaled up/down ship may cause issuesEnable/Disable Turtles Leaving Scaled Ships?")
         public final var canTurtlesLeaveScaledShips: Boolean
      }

      public class DYNMAP {
         @ConfigEntry(description = "Show Ships as Icon Markers on Dynmap")
         public final var showIconMarkers: Boolean = true

         @ConfigEntry(description = "Show Ships as Polyline Markers on Dynmap")
         public final var showPolylineMarkers: Boolean = true

         @ConfigEntry(description = "Show the Ship ID in the label")
         public final var showShipId: Boolean = true

         @ConfigEntry(description = "Show the Ship Mass in the label")
         public final var showShipMass: Boolean = true
      }

      public class FTBCHUNKS {
         @ConfigEntry(description = "Are Ships protected by FTB Chunk Claims?")
         public final var shipsProtectedByClaims: Boolean = true

         @ConfigEntry(description = "Are ships protected outside of build height (max and min)?")
         public final var shipsProtectionOutOfBuildHeight: Boolean
      }

      public class OCEANWAVES {
         @ConfigEntry(
            description = "Make water-borne ships physically bob/pitch/roll with ocean waves, in sync with Physics Mod / shader waves. Players and mobs stay attached to the deck. Needs a visual wave source (shader or Physics Mod ocean) to look right; leave OFF on flat vanilla water."
         )
         public final var enableWaveBuoyancy: Boolean = true

         @ConfigEntry(description = "Wave height in blocks. MUST match the shader's VS_WAVE_HEIGHT so the ship rides the visible swell.")
         public final var waveHeight: Double = 4.0

         @ConfigEntry(description = "Horizontal wave scale. Match Physics Mod 'oceanHorizontalWaveScale' (default 1.0).")
         public final var horizontalScale: Double = 1.0

         @ConfigEntry(description = "Wave speed multiplier. Raise/lower to match the visible wave motion.")
         public final var waveSpeed: Double = 1.0

         @ConfigEntry(description = "Gerstner detail iterations. Canonical Physics Mod value is 13.")
         public final var iterations: Int = 13

         @ConfigEntry(description = "How strongly ships are pulled to follow the wave surface (per unit mass).")
         public final var stiffness: Double = 8.0

         @ConfigEntry(description = "Vertical bob damping; higher = calmer, prevents runaway oscillation.")
         public final var damping: Double = 3.0

         @ConfigEntry(description = "Hull sample grid per axis (NxN). Higher = smoother pitch/roll, slightly more CPU.")
         public final var sampleGrid: Int = 3

         @ConfigEntry(description = "Time/phase offset (seconds) to line the physical bob up with the visible waves.")
         public final var phaseOffset: Double

         @ConfigEntry(description = "World X offset to align the wave field with Physics Mod (advanced).")
         public final var offsetX: Double

         @ConfigEntry(description = "World Z offset to align the wave field with Physics Mod (advanced).")
         public final var offsetZ: Double
      }

      public class WEATHER2 {
         @ConfigEntry(description = "If VS ships are affected by Weather2")
         public final var enableWeatherCompat: Boolean = true

         @ConfigEntry(description = "How much Weather 2's wind affects VS ships")
         public final var windMultiplier: Float = 0.1F

         @ConfigEntry(description = "The maximum velocity a VS ship can travel because of wind")
         public final var windMaxVel: Float = 20.0F

         @ConfigEntry(description = "In what range storms affect VS ships")
         public final var stormRange: Double = 150.0

         @ConfigEntry(description = "Storm effect dampening on VS ships")
         public final var stormDampening: Float
      }
   }
}
