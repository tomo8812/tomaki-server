package com.rubydesic.jacksonktdsl

import com.fasterxml.jackson.databind.JsonNode

public interface ObjectNodeBuilder {
   public abstract infix fun String.to(other: Any?) {
   }

   public abstract infix fun String.to(other: JsonNode) {
   }

   public open infix fun String.to(other: (ObjectNodeBuilder) -> Unit) {
   }

   public open operator fun String.invoke(other: JsonNode) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun to(`$this`: ObjectNodeBuilder, `$receiver`: java.lang.String, other: (ObjectNodeBuilder?) -> Unit) {
         `$receiver` to JacksonKotlinDslKt.obj(other) as JsonNode
      }

      @JvmStatic
      fun invoke(`$this`: ObjectNodeBuilder, `$receiver`: java.lang.String, other: JsonNode) {
         `$receiver` to other
      }
   }
}
