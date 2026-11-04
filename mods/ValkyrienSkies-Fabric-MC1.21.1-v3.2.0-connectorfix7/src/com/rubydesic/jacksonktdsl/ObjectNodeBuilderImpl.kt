package com.rubydesic.jacksonktdsl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode

internal class ObjectNodeBuilderImpl(nodeFactory: JsonNodeFactory, obj: ObjectNode = nodeFactory.objectNode()) : ObjectNodeBuilder {
   public final val nodeFactory: JsonNodeFactory
   public final val obj: ObjectNode

   init {
      this.nodeFactory = nodeFactory
      this.obj = obj
   }

   public override infix fun String.to(other: Any?) {
      this.obj.replace(`$this$to`, this.nodeFactory.pojoNode(other))
   }

   public override infix fun String.to(other: JsonNode) {
      this.obj.replace(`$this$to`, other)
   }

   public fun build(block: (ObjectNodeBuilder) -> Unit): ObjectNode {
      val var2: ObjectNode = this.obj
      block(this)
      return var2
   }

   override fun java.lang.String.to(other: (ObjectNodeBuilder?) -> Unit) {
      ObjectNodeBuilder.DefaultImpls.to(this, `$this$to`, other)
   }

   override fun java.lang.String.invoke(other: JsonNode) {
      ObjectNodeBuilder.DefaultImpls.invoke(this, `$this$invoke`, other)
   }
}
