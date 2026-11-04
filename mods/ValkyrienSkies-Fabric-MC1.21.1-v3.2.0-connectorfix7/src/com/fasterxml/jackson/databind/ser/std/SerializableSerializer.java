package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

@JacksonStdImpl
public class SerializableSerializer extends StdSerializer<JsonSerializable> {
   public static final SerializableSerializer instance = new SerializableSerializer();

   protected SerializableSerializer() {
      super(JsonSerializable.class);
   }

   public boolean isEmpty(SerializerProvider serializers, JsonSerializable value) {
      return value instanceof JsonSerializable.Base ? ((JsonSerializable.Base)value).isEmpty(serializers) : false;
   }

   public void serialize(JsonSerializable value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
      value.serialize(gen, serializers);
   }

   public final void serializeWithType(JsonSerializable value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
      value.serializeWithType(gen, serializers, typeSer);
   }

   @Override
   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      visitor.expectAnyFormat(typeHint);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) throws IOException {
      this.serialize((JsonSerializable)var1, var2, var3);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean isEmpty(SerializerProvider var1, Object var2) {
      return this.isEmpty(var1, (JsonSerializable)var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serializeWithType(Object var1, JsonGenerator var2, SerializerProvider var3, TypeSerializer var4) throws IOException {
      this.serializeWithType((JsonSerializable)var1, var2, var3, var4);
   }
}
