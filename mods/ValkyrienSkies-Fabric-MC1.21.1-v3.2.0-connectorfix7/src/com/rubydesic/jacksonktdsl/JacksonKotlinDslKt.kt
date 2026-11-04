package com.rubydesic.jacksonktdsl

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode

private final val defaultNodeFactory: JsonNodeFactory = JsonNodeFactory.instance
public final val arr: ArrayNodeFactory

public fun obj(block: (ObjectNodeBuilder) -> Unit): ObjectNode {
   val var10000: JsonNodeFactory = defaultNodeFactory
   return obj(var10000, block)
}

public fun JsonNodeFactory.obj(block: (ObjectNodeBuilder) -> Unit): ObjectNode {
   return ObjectNodeBuilderImpl(`$this$obj`, null, 2, null).build(block)
}

public final val arr: ArrayNodeFactory
   public final get() {
      return ArrayNodeFactoryImpl(`$this$arr`)
   }


fun {
   val var10000: JsonNodeFactory = defaultNodeFactory
   arr = arr
}
