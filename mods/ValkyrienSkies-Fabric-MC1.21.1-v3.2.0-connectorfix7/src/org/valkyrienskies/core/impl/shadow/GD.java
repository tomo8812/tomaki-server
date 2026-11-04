package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;
import java.text.ParseException;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

public final class GD extends SimpleModule {
   public GD() {
      this.addSerializer(MutableClassToInstanceMap.class, GD.b.INSTANCE)
      this.addDeserializer(MutableClassToInstanceMap.class, GD.a.INSTANCE)
      super.addAbstractTypeMapping(ClassToInstanceMap.class, MutableClassToInstanceMap.class)
   }

   // $VF: synthetic method
   private final <A, B extends A> void a() {
      Intrinsics.reifiedOperationMarker(4, "A")
      Class var10001 = Object.class
      Intrinsics.reifiedOperationMarker(4, "B")
      super.addAbstractTypeMapping(var10001, Object.class)
   }

   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bÂ\u0002\u0018\u00002\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0003\u0010\u0004J#\u0010\t\u001a\u0006\u0012\u0002\b\u00030\u00022\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\u0007H\u0016¢\u0006\u0004\b\t\u0010\n",
      d2 = {
            "Lorg/valkyrienskies/core/impl/shadow/GD$a;",
            "Lcom/fasterxml/jackson/databind/deser/std/StdDeserializer;",
            "Lcom/google/common/collect/MutableClassToInstanceMap;",
            "<init>",
            "()V",
            "Lcom/fasterxml/jackson/core/JsonParser;",
            "p0",
            "Lcom/fasterxml/jackson/databind/DeserializationContext;",
            "p1",
            "a",
            "(Lcom/fasterxml/jackson/core/JsonParser;Lcom/fasterxml/jackson/databind/DeserializationContext;)Lcom/google/common/collect/MutableClassToInstanceMap;"
      }
   )
   static final class a extends StdDeserializer<MutableClassToInstanceMap<?>> {
      public static final GD.a INSTANCE = new GD.a();

      private a() {
         super(MutableClassToInstanceMap.class)
      }

      private static MutableClassToInstanceMap<?> a(JsonParser var0, DeserializationContext var1) {
         Intrinsics.checkNotNullParameter(var0, "")
         Intrinsics.checkNotNullParameter(var1, "")
         if (var0.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new ParseException("Expected start of object", var0.getCurrentLocation().getLineNr())
         }

         MutableClassToInstanceMap var4 = MutableClassToInstanceMap.create()

         for (JsonToken var2 = var0.nextToken(); var2 != JsonToken.END_OBJECT; var2 = var0.nextToken()) {
            if (var2 != JsonToken.FIELD_NAME) {
               throw new ParseException("Expected field name", var0.getCurrentLocation().getLineNr())
            }

            Class var5 = Class.forName(var0.getCurrentName())
            var0.nextToken()
            Object var3 = var0.getCodec().readValue(var0, var5)
            Intrinsics.checkNotNullExpressionValue(var4, "")
            ((Map)var4).put(var5, var3)
         }

         Intrinsics.checkNotNullExpressionValue(var4, "")
         return var4
      }

      // $VF: synthetic method
      @Override
      public final Object deserialize(JsonParser p0, DeserializationContext p1) {
         p1 = p1
         JsonParser var5 = p0
         Intrinsics.checkNotNullParameter(p0, "")
         Intrinsics.checkNotNullParameter(p1, "")
         if (var5.getCurrentToken() != JsonToken.START_OBJECT) {
            throw new ParseException("Expected start of object", var5.getCurrentLocation().getLineNr())
         }

         MutableClassToInstanceMap var7 = MutableClassToInstanceMap.create()

         for (JsonToken var3 = var5.nextToken(); var3 != JsonToken.END_OBJECT; var3 = var5.nextToken()) {
            if (var3 != JsonToken.FIELD_NAME) {
               throw new ParseException("Expected field name", var5.getCurrentLocation().getLineNr())
            }

            Class var8 = Class.forName(var5.getCurrentName())
            var5.nextToken()
            Object var4 = var5.getCodec().readValue(var5, var8)
            Intrinsics.checkNotNullExpressionValue(var7, "")
            ((Map)var7).put(var8, var4)
         }

         Intrinsics.checkNotNullExpressionValue(var7, "")
         return var7
      }
   }

   @Metadata(
      mv = {1, 8, 0},
      k = 1,
      xi = 48,
      d1 = "\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\bÂ\u0002\u0018\u00002\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0003\u0010\u0004J+\u0010\u000b\u001a\u00020\n2\n\u0010\u0005\u001a\u0006\u0012\u0002\b\u00030\u00022\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\bH\u0016¢\u0006\u0004\b\u000b\u0010\f",
      d2 = {
            "Lorg/valkyrienskies/core/impl/shadow/GD$b;",
            "Lcom/fasterxml/jackson/databind/ser/std/StdSerializer;",
            "Lcom/google/common/collect/MutableClassToInstanceMap;",
            "<init>",
            "()V",
            "p0",
            "Lcom/fasterxml/jackson/core/JsonGenerator;",
            "p1",
            "Lcom/fasterxml/jackson/databind/SerializerProvider;",
            "p2",
            "",
            "a",
            "(Lcom/google/common/collect/MutableClassToInstanceMap;Lcom/fasterxml/jackson/core/JsonGenerator;Lcom/fasterxml/jackson/databind/SerializerProvider;)V"
      }
   )
   static final class b extends StdSerializer<MutableClassToInstanceMap<?>> {
      public static final GD.b INSTANCE = new GD.b();

      private b() {
         super(MutableClassToInstanceMap.class)
      }

      private static void a(MutableClassToInstanceMap<?> var0, JsonGenerator var1, SerializerProvider var2) {
         Intrinsics.checkNotNullParameter(var0, "")
         Intrinsics.checkNotNullParameter(var1, "")
         Intrinsics.checkNotNullParameter(var2, "")
         var1.writeStartObject()

         for (Entry var3 : var0.entrySet()) {
            var1.writeFieldName(((Class)var3.getKey()).getName())
            var2.defaultSerializeValue(var3.getValue(), var1)
         }

         var1.writeEndObject()
      }

      // $VF: synthetic method
      @Override
      public final void serialize(Object p0, JsonGenerator p1, SerializerProvider p2) {
         MutableClassToInstanceMap var10000 = (MutableClassToInstanceMap)p0
         p2 = p2
         p1 = p1
         p0 = var10000
         Intrinsics.checkNotNullParameter(var10000, "")
         Intrinsics.checkNotNullParameter(p1, "")
         Intrinsics.checkNotNullParameter(p2, "")
         p1.writeStartObject()

         for (Entry var4 : p0.entrySet()) {
            p1.writeFieldName(((Class)var4.getKey()).getName())
            p2.defaultSerializeValue(var4.getValue(), p1)
         }

         p1.writeEndObject()
      }
   }
}
