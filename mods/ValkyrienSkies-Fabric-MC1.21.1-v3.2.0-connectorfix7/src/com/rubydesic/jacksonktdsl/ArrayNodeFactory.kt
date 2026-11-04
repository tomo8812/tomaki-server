package com.rubydesic.jacksonktdsl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode

public interface ArrayNodeFactory {
   public abstract operator fun get(vararg elements: Any): ArrayNode {
   }

   public abstract operator fun get(elements: List<JsonNode>): ArrayNode {
   }

   public open operator fun get(vararg elements: JsonNode): ArrayNode {
   }

   public open operator fun get(elements: Iterable<JsonNode>): ArrayNode {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun get(`$this`: ArrayNodeFactory, vararg elements: JsonNode): ArrayNode {
         `$this`.get(ArraysKt.asList(elements))
      }

      @JvmStatic
      fun get(`$this`: ArrayNodeFactory, elements: MutableIterable<JsonNode>): ArrayNode {
         `$this`.get(CollectionsKt.toList(elements))
      }
   }
}
