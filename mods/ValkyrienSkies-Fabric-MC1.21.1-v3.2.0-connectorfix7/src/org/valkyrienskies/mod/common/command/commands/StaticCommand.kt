package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2561
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig

@SourceDebugExtension(["SMAP\nStaticCommand.kt\nKotlin\n*S Kotlin\n*F\n+ 1 StaticCommand.kt\norg/valkyrienskies/mod/common/command/commands/StaticCommand\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,42:1\n1863#2,2:43\n*S KotlinDebug\n*F\n+ 1 StaticCommand.kt\norg/valkyrienskies/mod/common/command/commands/StaticCommand\n*L\n26#1:43,2\n*E\n"])
public object StaticCommand {
   private const val SET_SHIP_STATIC_SUCCESS_MESSAGE: String = "command.valkyrienskies.set_static.success"
   private const val SET_ONE_SHIP_STATIC_SUCCESS_MESSAGE: String = "command.valkyrienskies.set_static.success_one"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then(
         (class_2170.method_9247("set-static").requires({ it: class_2168 ->
               it.method_9259(VSGameConfig.SERVER.Commands.setStaticShipCommandPerms)
            }) as LiteralArgumentBuilder)
            .then(
               class_2170.method_9244("ships", ShipArgument.Companion.ships())
                  .then(class_2170.method_9244("is-static", BoolArgumentType.bool() as ArgumentType).executes({ it: CommandContext ->
                     val var10000: ShipArgument.Companion = ShipArgument.Companion
                     val var9: java.util.List = CollectionsKt.toList(var10000.getShips(it, "ships"))
                     val isStatic: Boolean = BoolArgumentType.getBool(it, "is-static")

                     for (`element$iv` in var9) {
                        (`element$iv` as ServerShip).isStatic = isStatic
                     }

                     (it.getSource() as class_2168).method_9226({ 
                        class_2561.method_43469("command.valkyrienskies.set_static.success", arrayOf(`$r`.size(), if (`$isStatic`) "true" else "false")) as class_2561
                     }, true)
                     var9.size()
                  }))
            )
      )
   }
}
