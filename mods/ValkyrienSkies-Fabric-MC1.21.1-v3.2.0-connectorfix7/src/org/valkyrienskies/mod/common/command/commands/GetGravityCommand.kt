package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.math.MathKt
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2561
import net.minecraft.class_3218
import org.valkyrienskies.core.api.util.AerodynamicUtils
import org.valkyrienskies.mod.common.command.VSCommandsKt
import org.valkyrienskies.mod.common.config.VSGameConfig

public object GetGravityCommand {
   private const val GET_GRAVITY_MESSAGE: String = "command.valkyrienskies.get_gravity"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then((class_2170.method_9247("get-gravity").requires({ it: class_2168 ->
         it.method_9259(VSGameConfig.SERVER.Commands.getAirValuesPerms)
      }) as LiteralArgumentBuilder).executes({ it: CommandContext ->
         val level: class_3218 = (it.getSource() as class_2168).method_9225()
         val var10000: AerodynamicUtils = shipObjectWorld.getAerodynamicUtils()
         val gravity: Double = (var10000.getAtmosphereForDimension(dimensionId).getThird() as java.lang.Number).doubleValue()
         (it.getSource() as class_2168).method_9226({ 
            val var3: Array<Any> = arrayOfNulls(2)
            var3[0] = VSCommandsKt.toNiceString(dimensionId)
            var3[1] = `$gravity`
            class_2561.method_43469("command.valkyrienskies.get_gravity", var3) as class_2561
         }, true)
         MathKt.roundToInt(gravity)
      }))
   }
}
