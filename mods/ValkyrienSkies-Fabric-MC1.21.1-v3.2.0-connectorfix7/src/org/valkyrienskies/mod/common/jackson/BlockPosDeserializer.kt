package org.valkyrienskies.mod.common.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.node.LongNode
import net.minecraft.class_2338

public class BlockPosDeserializer : JsonDeserializer<class_2338> {
   @Throws(java/io/IOException::class)
   public open fun deserialize(p: JsonParser, ctxt: DeserializationContext): class_2338 {
      val var10000: TreeNode = p.getCodec().<TreeNode>readTree(p).get("long")
      val var6: java.lang.Number = (var10000 as LongNode).numberValue()
      val var7: class_2338 = class_2338.method_10092(var6 as java.lang.Long)
      return var7
   }

   // $VF: bridge methodSupplier
   @JvmSynthetic
   override fun deserialize(p0: JsonParser, p1: DeserializationContext) {
      this.deserialize(p0, p1)
   }
}
