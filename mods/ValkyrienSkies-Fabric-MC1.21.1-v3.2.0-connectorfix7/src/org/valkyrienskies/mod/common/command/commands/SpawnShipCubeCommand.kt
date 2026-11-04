package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import java.util.ArrayList
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2246
import net.minecraft.class_2338
import net.minecraft.class_243
import net.minecraft.class_2561
import net.minecraft.class_3218
import net.minecraft.class_3222
import org.valkyrienskies.mod.common.assembly.ShipAssembler

public object SpawnShipCubeCommand {
   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then(
         class_2170.method_9247("perf-test")
            .then(
               class_2170.method_9247("spawn-ship-cube")
                  .then(
                     class_2170.method_9244("size", IntegerArgumentType.integer(1, 20) as ArgumentType)
                        .executes(
                           lambda_2@{ ctx: CommandContext ->
                              val source: class_2168 = ctx.getSource() as class_2168
                              val player: class_3222 = source.method_9207()
                              val var10000: class_3218 = source.method_9225()
                              val level: class_3218 = var10000
                              val size: Int = IntegerArgumentType.getInteger(ctx, "size")
                              val lookVec: class_243 = player.method_5720()
                              val distance: Double = size + 10
                              val centerX: Double = player.method_23317() + lookVec.field_1352 * (size + 10)
                              val centerY: Double = player.method_23318() + lookVec.field_1351 * (size + 10)
                              val centerZ: Double = player.method_23321() + lookVec.field_1350 * distance
                              source.method_9226({ 
                                 class_2561.method_43470("[VS2] Spawning $`$size`x$`$size`x$`$size` ship cube ($`$totalShips` ships)...") as class_2561
                              }, true)
                              val overallStart: Long = System.currentTimeMillis()
                              val spacing: Int = 2
                              val placeStart: Long = System.currentTimeMillis()
                              val blockSets: java.util.List = ArrayList()

                              repeat(size) { placeMs ->
                                 repeat(size) { y ->
                                    repeat(size) { assembleStart ->
                                       val e: class_2338 = class_2338(
                                          (int)centerX + (placeMs - size / 2) * spacing,
                                          (int)centerY + (y - size / 2) * spacing,
                                          (int)centerZ + (assembleStart - size / 2) * spacing
                                       )
                                       level.method_8652(e, class_2246.field_10085.method_9564(), 3)
                                       blockSets.add(SetsKt.hashSetOf(arrayOf(e)))
                                    }
                                 }
                              }

                              var var34: java.util.List
                              try {
                                 var34 = ShipAssembler.batchAssembleToShips(level, blockSets, 1.0)
                              } catch (var30: Exception) {
                                 source.method_9213(class_2561.method_43470("[VS2] Batch assembly failed: ${var30.getMessage()}") as class_2561)
                                 return@lambda_2 0
                              }

                              source.method_9226(
                                 { 
                                    class_2561.method_43470(
                                       "[VS2] Created ${`$results`.size()} ships in $`$totalMs`ms (place: $`$placeMs`ms, assemble: $`$assembleMs`ms, ${`$totalMs`
                                          / (long)`$results`.size()}ms/ship)"
                                    ) as class_2561
                                 },
                                 true
                              )
                              return@lambda_2 var34.size()
                           }
                        )
                  )
            )
      )
   }
}
