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
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig

@SourceDebugExtension(["SMAP\nKeepActiveCommand.kt\nKotlin\n*S Kotlin\n*F\n+ 1 KeepActiveCommand.kt\norg/valkyrienskies/mod/common/command/commands/KeepActiveCommand\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,49:1\n1863#2,2:50\n*S KotlinDebug\n*F\n+ 1 KeepActiveCommand.kt\norg/valkyrienskies/mod/common/command/commands/KeepActiveCommand\n*L\n33#1:50,2\n*E\n"])
public object KeepActiveCommand {
   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then(
         (class_2170.method_9247("set-keep-active").requires({ it: class_2168 ->
               it.method_9259(VSGameConfig.SERVER.Commands.setStaticShipCommandPerms)
            }) as LiteralArgumentBuilder)
            .then(
               class_2170.method_9244("ships", ShipArgument.Companion.ships())
                  .then(class_2170.method_9244("keep-active", BoolArgumentType.bool() as ArgumentType).executes({ it: CommandContext ->
                     val var10000: ShipArgument.Companion = ShipArgument.Companion
                     val var13: java.util.List = CollectionsKt.toList(var10000.getShips(it, "ships"))
                     val value: Boolean = BoolArgumentType.getBool(it, "keep-active")
                     var changed: Int = 0

                     for (`element$iv` in var13) {
                        val ship: ServerShip = `element$iv` as ServerShip
                        val var14: LoadedServerShip = `element$iv` as ServerShip as? LoadedServerShip
                        if ((ship as? LoadedServerShip) != null) {
                           settings.keepActive = value
                           changed++
                        }
                     }

                     (it.getSource() as class_2168).method_9226({ 
                        class_2561.method_43470("Set keepActive=$`$value` on $`$applied` ship(s)") as class_2561
                     }, true)
                     changed
                  }))
            )
      )
   }
}
