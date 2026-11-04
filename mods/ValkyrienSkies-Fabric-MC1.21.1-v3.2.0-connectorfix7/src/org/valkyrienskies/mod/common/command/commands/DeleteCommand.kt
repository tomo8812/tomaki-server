package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2561
import net.minecraft.class_3218
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.assembly.ShipAssembler
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig

@SourceDebugExtension(["SMAP\nDeleteCommand.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DeleteCommand.kt\norg/valkyrienskies/mod/common/command/commands/DeleteCommand\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,61:1\n1557#2:62\n1628#2,3:63\n*S KotlinDebug\n*F\n+ 1 DeleteCommand.kt\norg/valkyrienskies/mod/common/command/commands/DeleteCommand\n*L\n36#1:62\n36#1:63,3\n*E\n"])
public object DeleteCommand {
   private const val DELETED_SHIPS_MESSAGE: String = "command.valkyrienskies.delete.success"
   private const val DELETED_ONE_SHIP_MESSAGE: String = "command.valkyrienskies.delete.success_one"
   private const val GET_SHIP_FAIL_MESSAGE: String = "command.valkyrienskies.get_ship.fail"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then((class_2170.method_9247("delete").requires({ it: class_2168 ->
         it.method_9259(VSGameConfig.SERVER.Commands.deleteShipCommandPerms)
      }) as LiteralArgumentBuilder).then((class_2170.method_9244("ships", ShipArgument.Companion.ships()).executes({ it: CommandContext ->
         val var10000: DeleteCommand = INSTANCE
         val var10002: java.util.List = CollectionsKt.toList(ShipArgument.Companion.getShips(it, "ships"))
         deleteShip$default(var10000, it, var10002, false, 4, null)
      }) as RequiredArgumentBuilder).then(class_2170.method_9244("deleteBlocks", BoolArgumentType.bool() as ArgumentType).executes({ it: CommandContext ->
         val var10000: DeleteCommand = INSTANCE
         val var10002: java.util.List = CollectionsKt.toList(ShipArgument.Companion.getShips(it, "ships"))
         var10000.deleteShip(it, var10002, BoolArgumentType.getBool(it, "deleteBlocks"))
      }))))
   }

   public fun deleteShip(context: CommandContext<class_2168>, r: List<ServerShip>, deleteBlocks: Boolean = false): Int {
      val `$this$mapTo$iv$iv`: java.lang.Iterable = r
      val `destination$iv$iv`: java.util.Collection = ArrayList(CollectionsKt.collectionSizeOrDefault(r, 10))

      for (`item$iv$iv` in `$this$mapTo$iv$iv`) {
         val ship: ServerShip = `item$iv$iv` as ServerShip
         val var10000: ShipAssembler = ShipAssembler.INSTANCE
         val var10001: class_3218 = (context.getSource() as class_2168).method_9225()
         `destination$iv$iv`.add(var10000.deleteShip(var10001, ship, deleteBlocks, false))
      }

      val deletedShips: java.util.List = `destination$iv$iv` as java.util.List
      if (CollectionsKt.sumOfInt(`destination$iv$iv` as java.util.List) == 0) {
         (context.getSource() as class_2168).method_9213(class_2561.method_43471("command.valkyrienskies.get_ship.fail") as class_2561)
         return 0
      } else {
         if (CollectionsKt.sumOfInt(deletedShips) == 1) {
            (context.getSource() as class_2168).method_9226({ 
               class_2561.method_43469("command.valkyrienskies.delete.success_one", arrayOf((`$r`.get(0) as ServerShip).slug)) as class_2561
            }, true)
         } else {
            (context.getSource() as class_2168).method_9226({ 
               class_2561.method_43469("command.valkyrienskies.delete.success", arrayOf(`$r`.size())) as class_2561
            }, true)
         }

         return r.size()
      }
   }
}
