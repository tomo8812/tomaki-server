package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.jvm.internal.Ref.IntRef
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2246
import net.minecraft.class_2338
import net.minecraft.class_2488
import net.minecraft.class_2561
import net.minecraft.class_3218
import org.joml.primitives.AABBic
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig

public object DesnowCommand {
   private const val DESNOW_SHIP_NO_BLOCKS_MESSAGE: String = "command.valkyrienskies.desnow.no_blocks"
   private const val DESNOW_SHIP_SUCCESS_MESSAGE: String = "command.valkyrienskies.desnow.success"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then((class_2170.method_9247("desnow").requires({ it: class_2168 ->
         it.method_9259(VSGameConfig.SERVER.Commands.desnowShipCommandPerms)
      }) as LiteralArgumentBuilder).then(class_2170.method_9244("ship", ShipArgument.Companion.ships()).executes({ it: CommandContext ->
         val var10000: DesnowCommand = INSTANCE
         var10000.desnowShip(it, ShipArgument.Companion.getShip(it, "ship"))
      })))
   }

   public fun desnowShip(context: CommandContext<class_2168>, ship: Ship): Int {
      val level: class_3218 = (context.getSource() as class_2168).method_9225()
      if (ship.shipAABB == null) {
         (context.getSource() as class_2168).method_9213(class_2561.method_43471("command.valkyrienskies.desnow.no_blocks") as class_2561)
         return 0
      } else {
         val clearCount: IntRef = IntRef()
         val var10000: AABBic = ship.shipAABB
         VSGameUtilsKt.forEach(var10000, { x: Int, y: Int, z: Int ->
            val pos: class_2338 = class_2338(x, y, z)
            if (`$level`.method_8320(pos).method_26204() is class_2488) {
               `$level`.method_8652(pos, class_2246.field_10124.method_9564(), 2)
               `$clearCount`.element++
            }

            Unit.INSTANCE
         })
         (context.getSource() as class_2168).method_9226({ 
            class_2561.method_43469("command.valkyrienskies.desnow.success", arrayOf(`$clearCount`.element)) as class_2561
         }, true)
         return 1
      }
   }
}
