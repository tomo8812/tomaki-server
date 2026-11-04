package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.class_2168
import net.minecraft.class_2170
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig

public object RenameCommand {
   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then(
         (class_2170.method_9247("rename").requires({ it: class_2168 ->
               it.method_9259(VSGameConfig.SERVER.Commands.renameShipCommandPerms)
            }) as LiteralArgumentBuilder)
            .then(
               class_2170.method_9244("ship", ShipArgument.Companion.ships())
                  .then(class_2170.method_9244("newName", StringArgumentType.string() as ArgumentType).executes({ it: CommandContext ->
                     val var10000: VsiCore = vsCore
                     val var10001: ShipArgument.Companion = ShipArgument.Companion
                     val var1: Ship = var10001.getShip(it, "ship")
                     val var2: ServerShip = var1 as ServerShip
                     val var10002: java.lang.String = StringArgumentType.getString(it, "newName")
                     var10000.renameShip(var2, var10002)
                     1
                  }))
            )
      )
   }
}
