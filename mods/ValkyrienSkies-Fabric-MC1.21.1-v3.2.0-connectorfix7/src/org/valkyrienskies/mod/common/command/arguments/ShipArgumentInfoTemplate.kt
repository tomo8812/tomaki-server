package org.valkyrienskies.mod.common.command.arguments

import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.class_2314
import net.minecraft.class_7157
import net.minecraft.class_2314.class_7217

public class ShipArgumentInfoTemplate(info: ShipArgumentInfo, selectorOnly: Boolean) : class_7217<ShipArgument> {
   private final val info: ShipArgumentInfo
   internal final val selectorOnly: Boolean

   init {
      this.info = info
      this.selectorOnly = selectorOnly
   }

   public open fun instantiate(commandBuildContext: class_7157): ShipArgument {
      return if (this.selectorOnly) ShipArgument.Companion.selectorOnly() else ShipArgument.Companion.ships()
   }

   fun method_41728(): class_2314<ShipArgument, *> {
      this.info as class_2314
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun method_41730(commandBuildContext: class_7157): ArgumentType {
      this.instantiate(commandBuildContext) as ArgumentType
   }
}
