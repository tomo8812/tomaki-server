package org.valkyrienskies.eureka.command

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import java.util.UUID
import net.minecraft.class_124
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2561
import net.minecraft.class_3222

public object EurekaAssemblerCommand {
   public fun register(dispatcher: CommandDispatcher<class_2168>) {
      dispatcher.register(
         class_2170.method_9247("vs")
            .then(
               (class_2170.method_9247("eureka-assembler")
                     .then(
                        class_2170.method_9247("floater")
                           .then(class_2170.method_9244("enabled", BoolArgumentType.bool() as ArgumentType).executes({ it: CommandContext ->
                              val var10000: EurekaAssemblerCommand = INSTANCE
                              var10000.set(it, true)
                           }))
                     ) as LiteralArgumentBuilder)
                  .then(
                     class_2170.method_9247("balloon")
                        .then(class_2170.method_9244("enabled", BoolArgumentType.bool() as ArgumentType).executes({ it: CommandContext ->
                           val var10000: EurekaAssemblerCommand = INSTANCE
                           var10000.set(it, false)
                        }))
                  )
            ) as LiteralArgumentBuilder
      )
   }

   private fun set(ctx: CommandContext<class_2168>, floater: Boolean): Int {
      val player: class_3222 = (ctx.getSource() as class_2168).method_9207()
      val enabled: Boolean = BoolArgumentType.getBool(ctx, "enabled")
      if (floater) {
         val var10000: AssemblerPreferences = AssemblerPreferences.INSTANCE
         val var10001: UUID = player.method_5667()
         var10000.setFloater(var10001, enabled)
      } else {
         val var8: AssemblerPreferences = AssemblerPreferences.INSTANCE
         val var9: UUID = player.method_5667()
         var8.setBalloon(var9, enabled)
      }

      val what: java.lang.String = if (floater) "floater" else "balloon"
      val state: java.lang.String = if (enabled) "ON" else "OFF"
      val color: class_124 = if (enabled) class_124.field_1060 else class_124.field_1054
      (ctx.getSource() as class_2168).method_9226({ 
         class_2561.method_43470("Eureka Assembler $`$what` auto-fill: $`$state`").method_27692(`$color`) as class_2561
      }, false)
      return 1
   }
}
