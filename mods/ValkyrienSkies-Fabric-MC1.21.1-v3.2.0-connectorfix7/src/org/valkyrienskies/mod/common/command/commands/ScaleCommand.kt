package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.class_2168
import net.minecraft.class_2170
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig

public object ScaleCommand {
   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then(
         (class_2170.method_9247("scale").requires({ it: class_2168 ->
               it.method_9259(VSGameConfig.SERVER.Commands.scaleShipCommandPerms)
            }) as LiteralArgumentBuilder)
            .then(
               class_2170.method_9244("ship", ShipArgument.Companion.ships())
                  .then(class_2170.method_9244("newScale", DoubleArgumentType.doubleArg(0.001) as ArgumentType).executes({ it: CommandContext ->
                     val var10000: VsiCore = vsCore
                     val var10001: Any = it.getSource()
                     var var1: ServerShipWorld = shipWorld
                     var1 = var1
                     val var10002: ShipArgument.Companion = ShipArgument.Companion
                     val var3: Ship = var10002.getShip(it, "ship")
                     var10000.scaleShip(var1, var3 as ServerShip, DoubleArgumentType.getDouble(it, "newScale"))
                     1
                  }))
            )
      )
   }
}
