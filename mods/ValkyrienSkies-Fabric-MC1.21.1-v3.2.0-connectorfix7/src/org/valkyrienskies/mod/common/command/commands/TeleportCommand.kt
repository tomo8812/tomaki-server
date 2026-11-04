package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.RequiredArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import kotlin.jvm.internal.SourceDebugExtension
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2277
import net.minecraft.class_243
import net.minecraft.class_2561
import net.minecraft.class_3218
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.world.ServerShipWorld
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.internal.ShipTeleportData
import org.valkyrienskies.core.internal.VsiCore
import org.valkyrienskies.mod.common.command.VSCommandsKt
import org.valkyrienskies.mod.common.command.arguments.RelativeVector3
import org.valkyrienskies.mod.common.command.arguments.RelativeVector3Argument
import org.valkyrienskies.mod.common.command.arguments.ShipArgument
import org.valkyrienskies.mod.common.config.VSGameConfig
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt

@SourceDebugExtension(["SMAP\nTeleportCommand.kt\nKotlin\n*S Kotlin\n*F\n+ 1 TeleportCommand.kt\norg/valkyrienskies/mod/common/command/commands/TeleportCommand\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,200:1\n1863#2,2:201\n1863#2,2:203\n1863#2,2:205\n1863#2,2:207\n*S KotlinDebug\n*F\n+ 1 TeleportCommand.kt\norg/valkyrienskies/mod/common/command/commands/TeleportCommand\n*L\n45#1:201,2\n82#1:203,2\n125#1:205,2\n174#1:207,2\n*E\n"])
public object TeleportCommand {
   private const val TELEPORT_SHIP_SUCCESS_MESSAGE: String = "command.valkyrienskies.teleport.success"
   private const val TELEPORT_ONE_SHIP_SUCCESS_MESSAGE: String = "command.valkyrienskies.teleport.success_one"
   private const val TELEPORTED_MULTIPLE_SHIPS_SUCCESS: String = "command.valkyrienskies.teleport.multiple_ship_success"
   private const val TELEPORT_FIRST_ARG_CAN_ONLY_INPUT_1_SHIP: String = "command.valkyrienskies.mc_teleport.can_only_teleport_to_one_ship"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then(
         (class_2170.method_9247("teleport").requires({ it: class_2168 ->
               it.method_9259(VSGameConfig.SERVER.Commands.teleportShipCommandPerms)
            }) as LiteralArgumentBuilder)
            .then(
               class_2170.method_9244("ships", ShipArgument.Companion.ships())
                  .then(
                     (class_2170.method_9244("position", class_2277.method_9737() as ArgumentType)
                           .executes(
                              { it: CommandContext ->
                                 val var10000: ShipArgument.Companion = ShipArgument.Companion
                                 val var11: java.util.List = CollectionsKt.toList(var10000.getShips(it, "ships"))
                                 val position: class_243 = class_2277.method_9736(it, "position")
                                 val var12: class_3218 = (it.getSource() as class_2168).method_9225()
                                 val dimensionId: java.lang.String = dimensionId
                                 val var13: VsiCore = vsCore
                                 val shipTeleportData: ShipTeleportData = VsiCore.newShipTeleportData$default(
                                    var13, VectorConversionsMCKt.toJOML(position), null, null, null, dimensionId, null, null, 110, null
                                 )

                                 for (`element$iv` in var11) {
                                    val ship: ServerShip = `element$iv` as ServerShip
                                    val var14: VsiCore = vsCore
                                    val var10001: Any = it.getSource()
                                    val var15: ShipWorld = shipWorld
                                    var14.teleportShip(var15 as ServerShipWorld, ship, shipTeleportData)
                                 }

                                 (it.getSource() as class_2168)
                                    .method_9226(
                                       { 
                                          class_2561.method_43469(
                                             "command.valkyrienskies.teleport.success", arrayOf(`$r`.size(), VSCommandsKt.getMessage(`$shipTeleportData`))
                                          ) as class_2561
                                       },
                                       true
                                    )
                                    var11.size()
                              }
                           ) as RequiredArgumentBuilder)
                        .then(
                           (class_2170.method_9244("euler-angles", RelativeVector3Argument.Companion.relativeVector3())
                                 .executes(
                                    { it: CommandContext ->
                                       val var10000: ShipArgument.Companion = ShipArgument.Companion
                                       val var13: java.util.List = CollectionsKt.toList(var10000.getShips(it, "ships"))
                                       val position: class_243 = class_2277.method_9736(it, "position")
                                       val eulerAngles: RelativeVector3 = RelativeVector3Argument.Companion.getRelativeVector3(it, "euler-angles")
                                       val source: class_2168 = it.getSource() as class_2168
                                       val var14: class_3218 = (it.getSource() as class_2168).method_9225()
                                       val dimensionId: java.lang.String = dimensionId
                                       val var15: VsiCore = vsCore
                                       val shipTeleportData: ShipTeleportData = VsiCore.newShipTeleportData$default(
                                          var15,
                                          VectorConversionsMCKt.toJOML(position),
                                          eulerAngles.toEulerRotationFromMCEntity(
                                             (double)source.method_9210().field_1343, (double)source.method_9210().field_1342
                                          ),
                                          null,
                                          null,
                                          dimensionId,
                                          null,
                                          null,
                                          108,
                                          null
                                       )

                                       for (`element$iv` in var13) {
                                          val ship: ServerShip = `element$iv` as ServerShip
                                          val var16: VsiCore = vsCore
                                          val var10001: Any = it.getSource()
                                          val var17: ShipWorld = shipWorld
                                          var16.teleportShip(var17 as ServerShipWorld, ship, shipTeleportData)
                                       }

                                       (it.getSource() as class_2168)
                                          .method_9226(
                                             { 
                                                class_2561.method_43469(
                                                   "command.valkyrienskies.teleport.success",
                                                   arrayOf(`$r`.size(), VSCommandsKt.getMessage(`$shipTeleportData`))
                                                ) as class_2561
                                             },
                                             true
                                          )
                                          var13.size()
                                    }
                                 ) as RequiredArgumentBuilder)
                              .then(
                                 (class_2170.method_9244("velocity", RelativeVector3Argument.Companion.relativeVector3())
                                       .executes(
                                          { it: CommandContext ->
                                             val var10000: ShipArgument.Companion = ShipArgument.Companion
                                             val var14: java.util.List = CollectionsKt.toList(var10000.getShips(it, "ships"))
                                             val position: class_243 = class_2277.method_9736(it, "position")
                                             val eulerAngles: RelativeVector3 = RelativeVector3Argument.Companion.getRelativeVector3(it, "euler-angles")
                                             val velocity: RelativeVector3 = RelativeVector3Argument.Companion.getRelativeVector3(it, "velocity")
                                             val source: class_2168 = it.getSource() as class_2168
                                             val var15: class_3218 = (it.getSource() as class_2168).method_9225()
                                             val dimensionId: java.lang.String = dimensionId
                                             val var16: VsiCore = vsCore
                                             val shipTeleportData: ShipTeleportData = VsiCore.newShipTeleportData$default(
                                                var16,
                                                VectorConversionsMCKt.toJOML(position),
                                                eulerAngles.toEulerRotationFromMCEntity(
                                                   (double)source.method_9210().field_1343, (double)source.method_9210().field_1342
                                                ),
                                                velocity.toVector3d(0.0, 0.0, 0.0),
                                                null,
                                                dimensionId,
                                                null,
                                                null,
                                                104,
                                                null
                                             )

                                             for (`element$iv` in var14) {
                                                val ship: ServerShip = `element$iv` as ServerShip
                                                val var17: VsiCore = vsCore
                                                val var10001: Any = it.getSource()
                                                val var18: ShipWorld = shipWorld
                                                var17.teleportShip(var18 as ServerShipWorld, ship, shipTeleportData)
                                             }

                                             (it.getSource() as class_2168)
                                                .method_9226(
                                                   { 
                                                      class_2561.method_43469(
                                                         "command.valkyrienskies.teleport.success",
                                                         arrayOf(`$r`.size(), VSCommandsKt.getMessage(`$shipTeleportData`))
                                                      ) as class_2561
                                                   },
                                                   true
                                                )
                                                var14.size()
                                          }
                                       ) as RequiredArgumentBuilder)
                                    .then(
                                       class_2170.method_9244("angular-velocity", RelativeVector3Argument.Companion.relativeVector3())
                                          .executes(
                                             { it: CommandContext ->
                                                val var10000: ShipArgument.Companion = ShipArgument.Companion
                                                val var15: java.util.List = CollectionsKt.toList(var10000.getShips(it, "ships"))
                                                val position: class_243 = class_2277.method_9736(it, "position")
                                                val eulerAngles: RelativeVector3 = RelativeVector3Argument.Companion.getRelativeVector3(it, "euler-angles")
                                                val velocity: RelativeVector3 = RelativeVector3Argument.Companion.getRelativeVector3(it, "velocity")
                                                val angularVelocity: RelativeVector3 = RelativeVector3Argument.Companion
                                                   .getRelativeVector3(it, "angular-velocity")
                                                   val source: class_2168 = it.getSource() as class_2168
                                                val var16: class_3218 = (it.getSource() as class_2168).method_9225()
                                                val dimensionId: java.lang.String = dimensionId
                                                val var17: VsiCore = vsCore
                                                val shipTeleportData: ShipTeleportData = VsiCore.newShipTeleportData$default(
                                                   var17,
                                                   VectorConversionsMCKt.toJOML(position),
                                                   eulerAngles.toEulerRotationFromMCEntity(
                                                      (double)source.method_9210().field_1343, (double)source.method_9210().field_1342
                                                   ),
                                                   velocity.toVector3d(0.0, 0.0, 0.0),
                                                   angularVelocity.toVector3d(0.0, 0.0, 0.0),
                                                   dimensionId,
                                                   null,
                                                   null,
                                                   96,
                                                   null
                                                )

                                                for (`element$iv` in var15) {
                                                   val ship: ServerShip = `element$iv` as ServerShip
                                                   val var18: VsiCore = vsCore
                                                   val var10001: Any = it.getSource()
                                                   val var19: ShipWorld = shipWorld
                                                   var18.teleportShip(var19 as ServerShipWorld, ship, shipTeleportData)
                                                }

                                                (it.getSource() as class_2168)
                                                   .method_9226(
                                                      { 
                                                         class_2561.method_43469(
                                                            "command.valkyrienskies.teleport.success",
                                                            arrayOf(`$r`.size(), VSCommandsKt.getMessage(`$shipTeleportData`))
                                                         ) as class_2561
                                                      },
                                                      true
                                                   )
                                                   var15.size()
                                             }
                                          )
                                    )
                              )
                        )
                  )
            )
      )
   }
}
