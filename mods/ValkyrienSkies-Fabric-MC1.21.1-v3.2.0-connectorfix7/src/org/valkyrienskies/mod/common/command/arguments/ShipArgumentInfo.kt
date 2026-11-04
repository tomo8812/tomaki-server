package org.valkyrienskies.mod.common.command.arguments

import com.google.gson.JsonObject
import com.mojang.brigadier.arguments.ArgumentType
import net.minecraft.class_2314
import net.minecraft.class_2540
import net.minecraft.class_2314.class_7217

public class ShipArgumentInfo : class_2314<ShipArgument, ShipArgumentInfoTemplate> {
   public open fun serializeToNetwork(template: ShipArgumentInfoTemplate, friendlyByteBuf: class_2540) {
      friendlyByteBuf.method_52964(template.selectorOnly)
   }

   public open fun deserializeFromNetwork(friendlyByteBuf: class_2540): ShipArgumentInfoTemplate {
      return ShipArgumentInfoTemplate(this, friendlyByteBuf.readBoolean())
   }

   public open fun unpack(argumentType: ShipArgument): ShipArgumentInfoTemplate {
      return ShipArgumentInfoTemplate(this, argumentType.selectorOnly)
   }

   public open fun serializeToJson(template: ShipArgumentInfoTemplate, jsonObject: JsonObject) {
      jsonObject.addProperty("selectorOnly", template.selectorOnly)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun method_10007(template: class_7217, friendlyByteBuf: class_2540) {
      this.serializeToNetwork(template as ShipArgumentInfoTemplate, friendlyByteBuf)
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun method_10005(friendlyByteBuf: class_2540): class_7217 {
      this.deserializeFromNetwork(friendlyByteBuf) as class_7217
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun method_41726(argumentType: ArgumentType): class_7217 {
      this.unpack(argumentType as ShipArgument) as class_7217
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   fun method_10006(template: class_7217, jsonObject: JsonObject) {
      this.serializeToJson(template as ShipArgumentInfoTemplate, jsonObject)
   }
}
