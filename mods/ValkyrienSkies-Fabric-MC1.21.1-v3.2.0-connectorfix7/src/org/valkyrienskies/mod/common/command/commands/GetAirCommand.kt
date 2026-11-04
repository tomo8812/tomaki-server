package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.math.MathKt
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2561
import net.minecraft.class_3218
import org.valkyrienskies.core.api.util.AerodynamicUtils
import org.valkyrienskies.mod.common.config.VSGameConfig

public object GetAirCommand {
   private const val AIR_VALUES_MESSAGE: String = "command.valkyrienskies.air_values"
   private const val AIR_VALUES_DENSITY_MESSAGE: String = "command.valkyrienskies.air_values.density"
   private const val AIR_VALUES_TEMPERATURE_MESSAGE: String = "command.valkyrienskies.air_values.temperature"
   private const val AIR_VALUES_PRESSURE_MESSAGE: String = "command.valkyrienskies.air_values.pressure"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then(
         ((((class_2170.method_9247("get-air").requires({ it: class_2168 ->
                        it.method_9259(VSGameConfig.SERVER.Commands.getAirValuesPerms)
                     }) as LiteralArgumentBuilder)
                     .executes(
                        { it: CommandContext ->
                           val aero: AerodynamicUtils = shipObjectWorld.getAerodynamicUtils()
                           val height: Double = (it.getSource() as class_2168).method_9222().field_1351
                           val var10000: class_3218 = (it.getSource() as class_2168).method_9225()
                           val dimId: java.lang.String = dimensionId
                           (it.getSource() as class_2168)
                              .method_9226(
                                 { 
                                    class_2561.method_43469(
                                       "command.valkyrienskies.air_values",
                                       arrayOf(Math.rint(`$height` * 10.0) / (double)10, `$density`, `$temperature`, `$pressure`)
                                    ) as class_2561
                                 },
                                 true
                              )
                              1
                        }
                     ) as LiteralArgumentBuilder)
                  .then(
                     class_2170.method_9247("density")
                        .executes(
                           { it: CommandContext ->
                              val aero: AerodynamicUtils = shipObjectWorld.getAerodynamicUtils()
                              val height: Double = (it.getSource() as class_2168).method_9222().field_1351
                              val var10000: class_3218 = (it.getSource() as class_2168).method_9225()
                              val density: Double = aero.getAirDensityForY(height, dimensionId)
                              (it.getSource() as class_2168)
                                 .method_9226(
                                    { 
                                       class_2561.method_43469(
                                          "command.valkyrienskies.air_values.density", arrayOf(Math.rint(`$height` * 10.0) / (double)10, `$density`)
                                       ) as class_2561
                                    },
                                    true
                                 )
                                 MathKt.roundToInt(density)
                           }
                        )
                  ) as LiteralArgumentBuilder)
               .then(
                  class_2170.method_9247("temperature")
                     .executes(
                        { it: CommandContext ->
                           val aero: AerodynamicUtils = shipObjectWorld.getAerodynamicUtils()
                           val height: Double = (it.getSource() as class_2168).method_9222().field_1351
                           val var10000: class_3218 = (it.getSource() as class_2168).method_9225()
                           val temperature: Double = aero.getAirTemperatureForY(height, dimensionId)
                           (it.getSource() as class_2168)
                              .method_9226(
                                 { 
                                    class_2561.method_43469(
                                       "command.valkyrienskies.air_values.temperature", arrayOf(Math.rint(`$height` * 10.0) / (double)10, `$temperature`)
                                    ) as class_2561
                                 },
                                 true
                              )
                              MathKt.roundToInt(temperature)
                        }
                     )
               ) as LiteralArgumentBuilder)
            .then(
               class_2170.method_9247("pressure")
                  .executes(
                     { it: CommandContext ->
                        val aero: AerodynamicUtils = shipObjectWorld.getAerodynamicUtils()
                        val height: Double = (it.getSource() as class_2168).method_9222().field_1351
                        val var10000: class_3218 = (it.getSource() as class_2168).method_9225()
                        val pressure: Double = aero.getAirPressureForY(height, dimensionId)
                        (it.getSource() as class_2168)
                           .method_9226(
                              { 
                                 class_2561.method_43469(
                                    "command.valkyrienskies.air_values.pressure", arrayOf(Math.rint(`$height` * 10.0) / (double)10, `$pressure`)
                                 ) as class_2561
                              },
                              true
                           )
                           MathKt.roundToInt(pressure)
                     }
                  )
            )
      )
   }
}
