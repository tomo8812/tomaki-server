package org.valkyrienskies.mod.common.command

import java.text.NumberFormat
import net.minecraft.class_2168
import net.minecraft.class_2561
import org.joml.Quaterniond
import org.joml.Quaterniondc
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.world.ShipWorld
import org.valkyrienskies.core.internal.ShipTeleportData
import org.valkyrienskies.mod.mixin.feature.commands.ClientSuggestionProviderAccessor

public fun ShipTeleportData.getMessage(): String {
   var var1: Array<Any>
   var var2: java.lang.String
   run label15@{
      var1 = arrayOfNulls(4)
      var2 = `$this$getMessage`.newDimension
      if (var2 != null) {
         var2 = toNiceString(var2)
         if (var2 != null) {
            return@label15
         }
      }

      var2 = ""
   }

   var1[0] = var2
   var1[1] = toNiceString(`$this$getMessage`.newPos)
   var1[2] = toNiceString(`$this$getMessage`.newRot)
   var1[3] = toNiceString(`$this$getMessage`.newVel)
   val var10000: java.lang.String = class_2561.method_43469("command.valkyrienskies.teleport.teleport_data", var1).getString()
   return var10000
}

public fun Vector3dc.toNiceString(): String {
   val var10000: java.lang.String = (`$this$toNiceString` as Vector3d).toString(NumberFormat.getInstance())
   return var10000
}

public fun Quaterniondc.toNiceString(): String {
   val var10000: java.lang.String = (`$this$toNiceString` as Quaterniond).toString(NumberFormat.getInstance())
   return var10000
}

public fun String.toNiceString(): String {
   val split: java.util.List = StringsKt.split$default(`$this$toNiceString`, arrayOf(":"), false, 0, 6, null)
   return "${split.get(2)}${split.get(3)}"
}

public final val shipWorld: ShipWorld
   public final get() {
      if (`$this$shipWorld` is class_2168) {
         return shipObjectWorld
      } else if (`$this$shipWorld` is ClientSuggestionProviderAccessor) {
         if ((`$this$shipWorld` as ClientSuggestionProviderAccessor).getMinecraft().field_1687 == null) {
            throw IllegalStateException("Required value was null.".toString())
         } else {
            return shipObjectWorld
         }
      } else {
         throw IllegalStateException("Command source wasn't CommandSourceStack or ClientSuggestionProvider")
      }
   }

