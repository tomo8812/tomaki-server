package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2561
import org.valkyrienskies.core.api.ships.LoadedServerShip
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig
import org.valkyrienskies.mod.common.util.SplittingDisablerAttachment

@SourceDebugExtension(["SMAP\nSplittingCommand.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SplittingCommand.kt\norg/valkyrienskies/mod/common/command/commands/SplittingCommand\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,46:1\n1863#2,2:47\n*S KotlinDebug\n*F\n+ 1 SplittingCommand.kt\norg/valkyrienskies/mod/common/command/commands/SplittingCommand\n*L\n25#1:47,2\n*E\n"])
public object SplittingCommand {
   private const val SET_SPLITTING_MESSAGE: String = "command.valkyrienskies.set_splitting"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then(
         (class_2170.method_9247("splitting").requires({ it: class_2168 ->
               it.method_9259(VSGameConfig.SERVER.Commands.deleteShipCommandPerms)
            }) as LiteralArgumentBuilder)
            .then(
               class_2170.method_9244("ships", ShipArgument.Companion.ships())
                  .then(class_2170.method_9244("enable", BoolArgumentType.bool() as ArgumentType).executes(lambda_3@{ it: CommandContext ->
                     val var10000: ShipArgument.Companion = ShipArgument.Companion
                     val ships: java.util.Set = var10000.getShips(it, "ships")
                     val enable: Boolean = BoolArgumentType.getBool(it, "enable")

                     for (`element$iv` in ships) {
                        val ship: Ship = `element$iv` as Ship
                        if (`element$iv` as Ship is LoadedServerShip) {
                           (ship as LoadedServerShip).setAttachment(SplittingDisablerAttachment(enable))
                        }
                     }

                     if (ships.isEmpty()) {
                        return@lambda_3 0
                     } else {
                        (it.getSource() as class_2168).method_9226({ 
                           class_2561.method_43469("command.valkyrienskies.set_splitting", arrayOf(`$enable`, `$ships`.size())) as class_2561
                        }, true)
                        return@lambda_3 1
                     }
                  }))
            )
      )
   }
}
