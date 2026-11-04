package org.valkyrienskies.mod.common.jackson

import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.KeyDeserializer
import net.minecraft.class_2338

public class BlockPosKeyDeserializer : KeyDeserializer {
   public override fun deserializeKey(key: String?, ctxt: DeserializationContext): Any? {
      if (key == null) {
         return null
      } else {
         try {
            return class_2338.method_10092(java.lang.Long.parseLong(key))
         } catch (var5: NumberFormatException) {
            throw JsonMappingException.from(ctxt, "Invalid BlockPos key '$key'", var5)
         }
      }
   }
}
