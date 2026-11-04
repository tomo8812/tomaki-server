package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.jvm.internal.Ref.IntRef
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2246
import net.minecraft.class_2338
import net.minecraft.class_2402
import net.minecraft.class_2561
import net.minecraft.class_2680
import net.minecraft.class_2741
import net.minecraft.class_2769
import net.minecraft.class_3218
import net.minecraft.class_3737
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig

public object DryCommand {
   private const val DRY_SHIP_NO_BLOCKS_MESSAGE: String = "command.valkyrienskies.dry.no_blocks"
   private const val DRY_SHIP_SUCCESS_MESSAGE: String = "command.valkyrienskies.dry.success"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then((class_2170.method_9247("dry").requires({ it: class_2168 ->
         it.method_9259(VSGameConfig.SERVER.Commands.dryShipCommandPerms)
      }) as LiteralArgumentBuilder).then(class_2170.method_9244("ship", ShipArgument.Companion.ships()).executes({ it: CommandContext ->
         val var10000: DryCommand = INSTANCE
         var10000.dryShip(it, ShipArgument.Companion.getShip(it, "ship"))
      })))
   }

   public fun dryShip(context: CommandContext<class_2168>, ship: Ship): Int {
      val level: class_3218 = (context.getSource() as class_2168).method_9225()
      if (ship.shipAABB == null) {
         (context.getSource() as class_2168).method_9213(class_2561.method_43471("command.valkyrienskies.dry.no_blocks") as class_2561)
         return 0
      } else {
         val dryCount: IntRef = IntRef()
         val var10000: AABBic = ship.shipAABB
         VSGameUtilsKt.forEach(var10000, { x: Int, y: Int, z: Int ->
            val pos: class_2338 = class_2338(x, y, z)
            val state: class_2680 = `$level`.method_8320(pos)
            if (state.method_51176()) {
               `$level`.method_8652(pos, class_2246.field_10124.method_9564(), 2)
               `$dryCount`.element++
            } else if (state.method_26204() is class_2402 && state.method_26204() !is class_3737) {
               `$level`.method_22352(pos, true)
               `$level`.method_8652(pos, class_2246.field_10124.method_9564(), 2)
               `$dryCount`.element++
            } else if (state.method_28498(class_2741.field_12508 as class_2769)) {
               `$level`.method_8652(pos, state.method_11657(class_2741.field_12508 as class_2769, false) as class_2680, 2)
               `$dryCount`.element++
            }

            Unit.INSTANCE
         })
         (context.getSource() as class_2168).method_9226({ 
            class_2561.method_43469("command.valkyrienskies.dry.success", arrayOf(`$dryCount`.element)) as class_2561
         }, true)
         return 1
      }
   }
}
