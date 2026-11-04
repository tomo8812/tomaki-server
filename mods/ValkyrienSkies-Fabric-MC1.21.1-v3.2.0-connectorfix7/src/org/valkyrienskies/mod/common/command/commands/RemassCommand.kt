package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.IntRef
import net.minecraft.class_1937
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2561
import net.minecraft.class_3218
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.BlockStateInfo
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig

@SourceDebugExtension(["SMAP\nRemassCommand.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RemassCommand.kt\norg/valkyrienskies/mod/common/command/commands/RemassCommand\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,49:1\n1863#2,2:50\n*S KotlinDebug\n*F\n+ 1 RemassCommand.kt\norg/valkyrienskies/mod/common/command/commands/RemassCommand\n*L\n26#1:50,2\n*E\n"])
public object RemassCommand {
   private const val REMASSED_SHIPS_SUCCESS_MESSAGE: String = "command.valkyrienskies.remass.success"
   private const val REMASSED_SHIP_FAIL_MESSAGE: String = "command.valkyrienskies.remass.fail"
   private const val REMASSED_ONE_SHIP_MESSAGE: String = "command.valkyrienskies.remass.success_one"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then((class_2170.method_9247("remass").requires({ it: class_2168 ->
         it.method_9259(VSGameConfig.SERVER.Commands.remassShipCommandPerms)
      }) as LiteralArgumentBuilder).then(class_2170.method_9244("ships", ShipArgument.Companion.ships()).executes({ it: CommandContext ->
         val var10000: ShipArgument.Companion = ShipArgument.Companion
         val var10: java.util.List = CollectionsKt.toList(var10000.getShips(it, "ships"))
         val successful: IntRef = IntRef()

         for (`element$iv` in var10) {
            val ship: ServerShip = `element$iv` as ServerShip
            val var11: BlockStateInfo = BlockStateInfo.INSTANCE
            val var10001: class_3218 = (it.getSource() as class_2168).method_9225()
            if (var11.remassShip(var10001 as class_1937, ship)) {
               successful.element++
            } else {
               (it.getSource() as class_2168).method_9213(class_2561.method_43469("command.valkyrienskies.remass.fail", arrayOf(ship.slug)) as class_2561)
            }
         }

         (it.getSource() as class_2168).method_9226({ 
            class_2561.method_43469("command.valkyrienskies.remass.success", arrayOf(`$successful`.element)) as class_2561
         }, true)
         successful.element
      })))
   }
}
