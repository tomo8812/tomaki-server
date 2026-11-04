package org.valkyrienskies.core.impl.shadow;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map.Entry;
import kotlin.jvm.internal.Intrinsics;

public final class GQ {
   // $VF: synthetic method
   private static <T> T a(ObjectMapper var0, ByteBuf var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      var0 = var0;
      InputStream var3 = (InputStream)(new ByteBufInputStream(var1));
      Intrinsics.needClassReification();
      return var0.readValue(var3, new GQ$c());
   }

   public static final <T> T a(ObjectMapper var0, ByteBuf var1, Class<T> var2) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      return var0.readValue((InputStream)(new ByteBufInputStream(var1)), var2);
   }

   private static ObjectNode a(ObjectNode var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      ObjectNode var1 = var0.objectNode();
      Iterator var10000 = var0.fields();
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      Iterator var4 = var10000;

      while (var4.hasNext()) {
         Entry var2;
         Intrinsics.checkNotNullExpressionValue(var2 = (Entry)var4.next(), "");
         String var3 = (String)var2.getKey();
         JsonNode var5 = (JsonNode)var2.getValue();
         var1.replace(var3, var5);
      }

      Intrinsics.checkNotNullExpressionValue(var1, "");
      return var1;
   }

   private static ObjectNode a(ObjectNode var0, String var1, JsonNode var2) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      Intrinsics.checkNotNullParameter(var2, "");
      ObjectNode var4;
      (var4 = a(var0)).replace(var1, var2);
      return var4;
   }

   // $VF: synthetic method
   private static <A, B extends A> SimpleModule a(SimpleModule var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.reifiedOperationMarker(4, "A");
      Class var10001 = Object.class;
      Intrinsics.reifiedOperationMarker(4, "B");
      SimpleModule var10000 = var0.addAbstractTypeMapping(var10001, Object.class);
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      return var10000;
   }

   // $VF: synthetic method
   private static <A, B> SimpleModule b(SimpleModule var0) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.reifiedOperationMarker(4, "A");
      Class var10001 = Object.class;
      Intrinsics.reifiedOperationMarker(4, "B");
      SimpleModule var10000 = var0.setMixInAnnotation(var10001, Object.class);
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      return var10000;
   }

   public static final void a(ObjectMapper var0, final AnnotationIntrospector var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      var0.registerModule(new SimpleModule() {
         @Override
         public final void setupModule(Module.SetupContext context) {
            Intrinsics.checkNotNullParameter(context, "");
            context.insertAnnotationIntrospector(var1);
            super.setupModule(context);
         }
      });
   }

   private static ObjectNode a(ObjectNode var0, ObjectNode var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      var0 = a(var0);
      Iterator var10000 = var1.fields();
      Intrinsics.checkNotNullExpressionValue(var10000, "");
      Iterator var5 = var10000;

      while (var5.hasNext()) {
         Entry var2;
         Intrinsics.checkNotNullExpressionValue(var2 = (Entry)var5.next(), "");
         String var3 = (String)var2.getKey();
         JsonNode var6;
         if ((var6 = (JsonNode)var2.getValue()).isObject() && var0.has(var3) && var0.get(var3).isObject()) {
            ObjectNode var10002 = var0.get(var3);
            Intrinsics.checkNotNull(var10002);
            var10002 = var10002;
            Intrinsics.checkNotNull(var6);
            var0.replace(var3, a(var10002, (ObjectNode)var6));
         } else {
            var0.replace(var3, var6.deepCopy());
         }
      }

      return var0;
   }

   private static void b(ObjectMapper var0, final AnnotationIntrospector var1) {
      Intrinsics.checkNotNullParameter(var0, "");
      Intrinsics.checkNotNullParameter(var1, "");
      var0.registerModule(new SimpleModule() {
         @Override
         public final void setupModule(Module.SetupContext context) {
            Intrinsics.checkNotNullParameter(context, "");
            context.appendAnnotationIntrospector(var1);
            super.setupModule(context);
         }
      });
   }
}
