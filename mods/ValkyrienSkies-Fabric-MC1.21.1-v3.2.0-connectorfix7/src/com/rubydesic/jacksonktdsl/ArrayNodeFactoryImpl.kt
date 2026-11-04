package com.rubydesic.jacksonktdsl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory

internal class ArrayNodeFactoryImpl(nodeFactory: JsonNodeFactory) : ArrayNodeFactory {
   private final val nodeFactory: JsonNodeFactory

   init {
      this.nodeFactory = nodeFactory
   }

   public override operator fun get(vararg elements: Any): ArrayNode {
      val arr: ArrayNode = this.nodeFactory.arrayNode(elements.length)

      for (`element$iv` in elements) {
         arr.addPOJO(`element$iv`)
      }

      return arr
   }

   public override operator fun get(elements: List<JsonNode>): ArrayNode {
      return ArrayNode(this.nodeFactory, elements)
   }

   override fun get(vararg elements: JsonNode): ArrayNode {
      ArrayNodeFactory.DefaultImpls.get(this, elements)
   }

   override fun get(elements: MutableIterable<JsonNode>): ArrayNode {
      ArrayNodeFactory.DefaultImpls.get(this, elements)
   }
}
