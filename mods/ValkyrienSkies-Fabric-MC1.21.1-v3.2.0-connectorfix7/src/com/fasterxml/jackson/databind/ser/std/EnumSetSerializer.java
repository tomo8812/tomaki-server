package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.ContainerSerializer;
import java.io.IOException;
import java.util.EnumSet;

public class EnumSetSerializer extends AsArraySerializerBase<EnumSet<? extends Enum<?>>> {
   public EnumSetSerializer(JavaType elemType) {
      super(EnumSet.class, elemType, true, null, null);
   }

   public EnumSetSerializer(EnumSetSerializer src, BeanProperty property, TypeSerializer vts, JsonSerializer<?> valueSerializer, Boolean unwrapSingle) {
      super(src, property, vts, valueSerializer, unwrapSingle);
   }

   public EnumSetSerializer _withValueTypeSerializer(TypeSerializer vts) {
      return this;
   }

   public EnumSetSerializer withResolved(BeanProperty property, TypeSerializer vts, JsonSerializer<?> elementSerializer, Boolean unwrapSingle) {
      return new EnumSetSerializer(this, property, vts, elementSerializer, unwrapSingle);
   }

   public boolean isEmpty(SerializerProvider prov, EnumSet<? extends Enum<?>> value) {
      return value.isEmpty();
   }

   public boolean hasSingleElement(EnumSet<? extends Enum<?>> value) {
      return value.size() == 1;
   }

   public final void serialize(EnumSet<? extends Enum<?>> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      int len = value.size();
      if (len != 1
         || (this._unwrapSingle != null || !provider.isEnabled(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED)) && this._unwrapSingle != Boolean.TRUE) {
         gen.writeStartArray(value, len);
         this.serializeContents(value, gen, provider);
         gen.writeEndArray();
      } else {
         this.serializeContents(value, gen, provider);
      }
   }

   public void serializeContents(EnumSet<? extends Enum<?>> value, JsonGenerator gen, SerializerProvider provider) throws IOException {
      JsonSerializer<Object> enumSer = this._elementSerializer;

      for (Enum<?> en : value) {
         if (enumSer == null) {
            enumSer = provider.findContentValueSerializer(en.getDeclaringClass(), this._property);
         }

         enumSer.serialize(en, gen, provider);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serializeContents(Object var1, JsonGenerator var2, SerializerProvider var3) throws IOException {
      this.serializeContents((EnumSet<? extends Enum<?>>)var1, var2, var3);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) throws IOException {
      this.serialize((EnumSet<? extends Enum<?>>)var1, var2, var3);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public AsArraySerializerBase withResolved(BeanProperty var1, TypeSerializer var2, JsonSerializer var3, Boolean var4) {
      return this.withResolved(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public ContainerSerializer _withValueTypeSerializer(TypeSerializer var1) {
      return this._withValueTypeSerializer(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean hasSingleElement(Object var1) {
      return this.hasSingleElement((EnumSet<? extends Enum<?>>)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean isEmpty(SerializerProvider var1, Object var2) {
      return this.isEmpty(var1, (EnumSet<? extends Enum<?>>)var2);
   }
}
