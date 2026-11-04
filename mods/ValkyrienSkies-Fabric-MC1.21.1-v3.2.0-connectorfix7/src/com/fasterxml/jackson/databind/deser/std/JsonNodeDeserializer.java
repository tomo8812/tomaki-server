package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.LogicalType;
import java.io.IOException;

public class JsonNodeDeserializer extends BaseNodeDeserializer<JsonNode> {
   private static final JsonNodeDeserializer instance = new JsonNodeDeserializer();

   protected JsonNodeDeserializer() {
      super(JsonNode.class, null);
   }

   protected JsonNodeDeserializer(JsonNodeDeserializer base, boolean mergeArrays, boolean mergeObjects) {
      super(base, mergeArrays, mergeObjects);
   }

   @Override
   protected JsonDeserializer<?> _createWithMerge(boolean mergeArrays, boolean mergeObjects) {
      return new JsonNodeDeserializer(this, mergeArrays, mergeObjects);
   }

   public static JsonDeserializer<? extends JsonNode> getDeserializer(Class<?> nodeClass) {
      if (nodeClass == ObjectNode.class) {
         return JsonNodeDeserializer.ObjectDeserializer.getInstance();
      } else {
         return nodeClass == ArrayNode.class ? JsonNodeDeserializer.ArrayDeserializer.getInstance() : instance;
      }
   }

   public JsonNode getNullValue(DeserializationContext ctxt) {
      return ctxt.getNodeFactory().nullNode();
   }

   @Override
   public Object getAbsentValue(DeserializationContext ctxt) {
      return null;
   }

   public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      BaseNodeDeserializer.ContainerStack stack = new BaseNodeDeserializer.ContainerStack();
      JsonNodeFactory nodeF = ctxt.getNodeFactory();
      switch (p.currentTokenId()) {
         case 1:
            return this._deserializeContainerNoRecursion(p, ctxt, nodeF, stack, nodeF.objectNode());
         case 2:
            return nodeF.objectNode();
         case 3:
            return this._deserializeContainerNoRecursion(p, ctxt, nodeF, stack, nodeF.arrayNode());
         case 4:
         default:
            return this._deserializeAnyScalar(p, ctxt);
         case 5:
            return this._deserializeObjectAtName(p, ctxt, nodeF, stack);
      }
   }

   @Override
   public Boolean supportsUpdate(DeserializationConfig config) {
      return this._supportsUpdates;
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public JsonDeserializer createContextual(DeserializationContext var1, BeanProperty var2) throws JsonMappingException {
      return super.createContextual(var1, var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean isCachable() {
      return super.isCachable();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public LogicalType logicalType() {
      return super.logicalType();
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object deserializeWithType(JsonParser var1, DeserializationContext var2, TypeDeserializer var3) throws IOException {
      return super.deserializeWithType(var1, var2, var3);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object getNullValue(DeserializationContext var1) throws JsonMappingException {
      return this.getNullValue(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JacksonException {
      return this.deserialize(var1, var2);
   }

   static final class ArrayDeserializer extends BaseNodeDeserializer<ArrayNode> {
      private static final long serialVersionUID = 1L;
      protected static final JsonNodeDeserializer.ArrayDeserializer _instance = new JsonNodeDeserializer.ArrayDeserializer();

      protected ArrayDeserializer() {
         super(ArrayNode.class, true);
      }

      public static JsonNodeDeserializer.ArrayDeserializer getInstance() {
         return _instance;
      }

      protected ArrayDeserializer(JsonNodeDeserializer.ArrayDeserializer base, boolean mergeArrays, boolean mergeObjects) {
         super(base, mergeArrays, mergeObjects);
      }

      @Override
      protected JsonDeserializer<?> _createWithMerge(boolean mergeArrays, boolean mergeObjects) {
         return new JsonNodeDeserializer.ArrayDeserializer(this, mergeArrays, mergeObjects);
      }

      public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         if (p.isExpectedStartArrayToken()) {
            JsonNodeFactory nodeF = ctxt.getNodeFactory();
            ArrayNode arrayNode = nodeF.arrayNode();
            this._deserializeContainerNoRecursion(p, ctxt, nodeF, new BaseNodeDeserializer.ContainerStack(), arrayNode);
            return arrayNode;
         } else {
            return (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
         }
      }

      public ArrayNode deserialize(JsonParser p, DeserializationContext ctxt, ArrayNode arrayNode) throws IOException {
         if (p.isExpectedStartArrayToken()) {
            this._deserializeContainerNoRecursion(p, ctxt, ctxt.getNodeFactory(), new BaseNodeDeserializer.ContainerStack(), arrayNode);
            return arrayNode;
         } else {
            return (ArrayNode)ctxt.handleUnexpectedToken(ArrayNode.class, p);
         }
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object deserialize(JsonParser var1, DeserializationContext var2, Object var3) throws IOException, JacksonException {
         return this.deserialize(var1, var2, (ArrayNode)var3);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JacksonException {
         return this.deserialize(var1, var2);
      }
   }

   static final class ObjectDeserializer extends BaseNodeDeserializer<ObjectNode> {
      private static final long serialVersionUID = 1L;
      protected static final JsonNodeDeserializer.ObjectDeserializer _instance = new JsonNodeDeserializer.ObjectDeserializer();

      protected ObjectDeserializer() {
         super(ObjectNode.class, true);
      }

      public static JsonNodeDeserializer.ObjectDeserializer getInstance() {
         return _instance;
      }

      protected ObjectDeserializer(JsonNodeDeserializer.ObjectDeserializer base, boolean mergeArrays, boolean mergeObjects) {
         super(base, mergeArrays, mergeObjects);
      }

      @Override
      protected JsonDeserializer<?> _createWithMerge(boolean mergeArrays, boolean mergeObjects) {
         return new JsonNodeDeserializer.ObjectDeserializer(this, mergeArrays, mergeObjects);
      }

      public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
         JsonNodeFactory nodeF = ctxt.getNodeFactory();
         if (p.isExpectedStartObjectToken()) {
            ObjectNode root = nodeF.objectNode();
            this._deserializeContainerNoRecursion(p, ctxt, nodeF, new BaseNodeDeserializer.ContainerStack(), root);
            return root;
         } else if (p.hasToken(JsonToken.FIELD_NAME)) {
            return this._deserializeObjectAtName(p, ctxt, nodeF, new BaseNodeDeserializer.ContainerStack());
         } else {
            return p.hasToken(JsonToken.END_OBJECT) ? nodeF.objectNode() : (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p);
         }
      }

      public ObjectNode deserialize(JsonParser p, DeserializationContext ctxt, ObjectNode node) throws IOException {
         return !p.isExpectedStartObjectToken() && !p.hasToken(JsonToken.FIELD_NAME)
            ? (ObjectNode)ctxt.handleUnexpectedToken(ObjectNode.class, p)
            : (ObjectNode)this.updateObject(p, ctxt, node, new BaseNodeDeserializer.ContainerStack());
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object deserialize(JsonParser var1, DeserializationContext var2, Object var3) throws IOException, JacksonException {
         return this.deserialize(var1, var2, (ObjectNode)var3);
      }

      // $VF: synthetic method
      // $VF: bridge method
      @Override
      public Object deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JacksonException {
         return this.deserialize(var1, var2);
      }
   }
}
