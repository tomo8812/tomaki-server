package org.valkyrienskies.mod.common.command.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.class_1297
import net.minecraft.class_1937
import net.minecraft.class_2168
import net.minecraft.class_2170
import net.minecraft.class_2338
import net.minecraft.class_239
import net.minecraft.class_2561
import net.minecraft.class_3965
import org.valkyrienskies.core.api.ships.Ship
import org.valkyrienskies.mod.common.VSGameUtilsKt
import org.valkyrienskies.mod.common.config.VSGameConfig

public object GetShipCommand {
   private const val GET_SHIP_SUCCESS_MESSAGE: String = "command.valkyrienskies.get_ship.success"
   private const val GET_SHIP_FAIL_MESSAGE: String = "command.valkyrienskies.get_ship.fail"
   private const val GET_SHIP_ONLY_USABLE_BY_ENTITIES_MESSAGE: String = "command.valkyrienskies.get_ship.only_usable_by_entities"

   public fun register(vs: LiteralArgumentBuilder<class_2168>) {
      vs.then((class_2170.method_9247("get-ship").requires({ it: class_2168 ->
         it.method_9259(VSGameConfig.SERVER.Commands.getShipCommandPerms)
      }) as LiteralArgumentBuilder).executes({ it: CommandContext ->
         var success: Boolean = false
         val sourceEntity: class_1297 = (it.getSource() as class_2168).method_9228()
         val var5: Byte
         if (sourceEntity != null) {
            val rayTrace: class_239 = sourceEntity.method_5745(10.0, 1.0F, false)
            if (rayTrace is class_3965) {
               val var10000: class_1937 = sourceEntity.method_37908()
               val var10001: class_2338 = (rayTrace as class_3965).method_17777()
               val ship: Ship = VSGameUtilsKt.getShipManagingPos(var10000, var10001)
               if (ship != null) {
                  (it.getSource() as class_2168).method_9226({ 
                     class_2561.method_43469("command.valkyrienskies.get_ship.success", arrayOf(`$ship`.slug, `$ship`.id)) as class_2561
                  }, true)
                  success = true
               }
            }

            if (success) {
               var5 = 1
            } else {
               (it.getSource() as class_2168).method_9213(class_2561.method_43471("command.valkyrienskies.get_ship.fail") as class_2561)
               var5 = 0
            }
         } else {
            (it.getSource() as class_2168).method_9213(class_2561.method_43471("command.valkyrienskies.get_ship.only_usable_by_entities") as class_2561)
            var5 = 0
         }

         var5
      }))
   }
}
