package org.valkyrienskies.eureka.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import java.util.Arrays
import net.minecraft.class_124
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2561
import net.minecraft.class_5250
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.command.arguments.ShipArgument

public object ShipWeightCommand {
   private const val AFLOAT_ADDED_FACTOR: Double = 1.0
   private const val DRY_ADDED_FACTOR: Double = 7.5
   private const val SAFETY_MARGIN: Int = 1
   private const val GRAVITY_MAGNITUDE: Double = 10.0

   public fun register(dispatcher: CommandDispatcher<class_2168>) {
      dispatcher.register(
         class_2170.method_9247("vs")
            .then(
               class_2170.method_9247("get-ship-weight")
                  .then(
                     (class_2170.method_9244("ship", ShipArgument.Companion.ships() as ArgumentType)
                           .then(class_2170.method_9247("floater").executes({ it: CommandContext ->
                              val var10000: ShipWeightCommand = INSTANCE
                              var10000.getShipWeight(it, true)
                           })) as RequiredArgumentBuilder)
                        .then(class_2170.method_9247("balloon").executes({ it: CommandContext ->
                           val var10000: ShipWeightCommand = INSTANCE
                           var10000.getShipWeight(it, false)
                        }))
                  )
            ) as LiteralArgumentBuilder
      )
   }

   private fun getShipWeight(ctx: CommandContext<class_2168>, floater: Boolean): Int {
      val ship: Ship = ShipArgument.Companion.getShip(ctx, "ship")
      if (ship !is LoadedServerShip) {
         (ctx.getSource() as class_2168)
            .method_9213(class_2561.method_43470("Ship '${ship.getSlug()}' is not loaded -- get closer to it and try again.") as class_2561)
            return 0
      } else {
         val mass: Double = (ship as LoadedServerShip).getInertiaData().getMass()
         val cfg: EurekaConfig.Server = EurekaConfig.SERVER
         val control: EurekaShipControl = (ship as LoadedServerShip).getAttachment(EurekaShipControl.class) as EurekaShipControl
         var var10000: java.lang.String = (ship as LoadedServerShip).getSlug()
         if (var10000 == null) {
            var10000 = "unnamed ship"
         }

         val msg: class_5250 = class_2561.method_43470("Ship '$var10000' weighs ${this.fmtKg(mass)}").method_27692(class_124.field_1068)
         if (floater) {
            val liftPerBalloon: Double = Math.min(cfg.floaterBuoyantFactorPerKg / mass, 15.0 * cfg.maxFloaterBuoyantFactor)
            if (liftPerBalloon <= 0.0) {
               (ctx.getSource() as class_2168)
                  .method_9213(
                     class_2561.method_43470(
                        "Floaters provide no buoyancy with the current config (floaterBuoyantFactorPerKg = ${cfg.floaterBuoyantFactorPerKg})."
                     ) as class_2561
                  )
                  return 0
            }

            val ascendFactor: Int = (int)Math.ceil(1.0 / liftPerBalloon) + 1
            val dry: Int = Math.max((int)Math.ceil(7.5 / liftPerBalloon), ascendFactor)
            val needed: Int = if (control != null) control.floaters else 0
            val balloons: Double = needed / 15.0
            msg.method_10852(this.newline() as class_2561)
            msg.method_10852(
               class_2561.method_43470("Floaters to stay afloat: ${this.fmtInt(ascendFactor)} -- rides low, interior may flood")
                  .method_27692(class_124.field_1075) as class_2561
            )
            msg.method_10852(this.newline() as class_2561)
            msg.method_10852(class_2561.method_43470("Floaters to ride high and dry: ${this.fmtInt(dry)}").method_27692(class_124.field_1075) as class_2561)
            val placed: StringBuilder = StringBuilder("Floaters on ship: ")
            if (needed % 15 == 0) {
               placed.append(this.fmtInt(needed / 15))
            } else {
               val var27: Array<Any> = arrayOf(balloons)
               val var10001: java.lang.String = java.lang.String.format("%.1f", Arrays.copyOf(var27, var27.length))
               placed.append(var10001).append(" (some are redstone-weakened)")
            }

            val var29: class_124
            if (balloons >= dry) {
               placed.append(" -- enough to ride dry!")
               var29 = class_124.field_1060
            } else if (balloons >= ascendFactor) {
               placed.append(" -- stays afloat but rides low; ${this.fmtInt((int)Math.ceil((double)dry - balloons))} more to ride dry")
               var29 = class_124.field_1054
            } else {
               placed.append(" -- will sink! need ${this.fmtInt((int)Math.ceil((double)ascendFactor - balloons))} more to stay afloat")
               var29 = class_124.field_1061
            }

            msg.method_10852(this.newline() as class_2561)
            msg.method_10852(class_2561.method_43470(placed.toString()).method_27692(var29) as class_2561)
            msg.method_10852(this.newline() as class_2561)
            msg.method_10852(
               class_2561.method_43470("Ride height scales with floater count -- add more to sit higher.")
                  .method_27695(arrayOf(class_124.field_1080, class_124.field_1056)) as class_2561
            )
         } else {
            val var20: Double = cfg.massPerBalloon * cfg.balloonLiftMultiplier
            if (var20 <= 0.0) {
               (ctx.getSource() as class_2168)
                  .method_9213(
                     class_2561.method_43470(
                        "Balloons provide no lift with the current config (massPerBalloon = ${cfg.massPerBalloon}, balloonLiftMultiplier = ${cfg.balloonLiftMultiplier})."
                     ) as class_2561
                  )
                  return 0
            }

            val var23: Int = (int)Math.ceil(mass * (1.0 + Math.max(0.0, cfg.assemblerBalloonAscendRate) / 10.0) / var20)
            val var24: Int = if (control != null) control.balloons else 0
            msg.method_10852(this.newline() as class_2561)
            msg.method_10852(
               class_2561.method_43470("Balloons needed to ascend: ${this.fmtInt(var23)} (each lifts ${this.fmtKg(var20)})").method_27692(class_124.field_1075) as class_2561
            )
            val enough: Boolean = var24 >= var23
            msg.method_10852(this.newline() as class_2561)
            msg.method_10852(
               class_2561.method_43470(
                     "Balloons on ship: ${this.fmtInt(var24)}${if (enough) " -- enough to ascend!" else " -- need ${this.fmtInt(var23 - var24)} more"}"
                  )
                  .method_27692(if (enough) class_124.field_1060 else class_124.field_1054) as class_2561
            )
            if (cfg.maxBalloonsPerEngine > 0) {
               msg.method_10852(this.newline() as class_2561)
               msg.method_10852(
                  class_2561.method_43470("Note: maxBalloonsPerEngine = ${cfg.maxBalloonsPerEngine} -- lift is also limited by engine power.")
                     .method_27695(arrayOf(class_124.field_1080, class_124.field_1056)) as class_2561
               )
            }
         }

         if (control == null || control.helms < 1) {
            msg.method_10852(this.newline() as class_2561)
            msg.method_10852(
               class_2561.method_43470("Note: this ship has no helm -- floaters and balloons only work on helm-assembled ships.")
                  .method_27695(arrayOf(class_124.field_1080, class_124.field_1056)) as class_2561
            )
         }

         (ctx.getSource() as class_2168).method_9226({ 
            `$msg` as class_2561
         }, false)
         return 1
      }
   }

   private fun newline(): class_5250 {
      return class_2561.method_43470("\n")
   }

   private fun fmtInt(v: Int): String {
      val var4: Array<Any> = arrayOf(v)
      val var10000: java.lang.String = java.lang.String.format("%,d", Arrays.copyOf(var4, var4.length))
      return var10000
   }

   private fun fmtKg(v: Double): String {
      val var5: Array<Any> = arrayOf(v)
      val var10000: java.lang.String = java.lang.String.format("%,.0f kg", Arrays.copyOf(var5, var5.length))
      return var10000
   }
}
