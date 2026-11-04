package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.io.IOException;

public class StackTraceElementDeserializer extends StdScalarDeserializer<StackTraceElement> {
   private static final long serialVersionUID = 1L;
   protected final JsonDeserializer<?> _adapterDeserializer;

   @Deprecated
   public StackTraceElementDeserializer() {
      this(null);
   }

   protected StackTraceElementDeserializer(JsonDeserializer<?> ad) {
      super(StackTraceElement.class);
      this._adapterDeserializer = ad;
   }

   public static JsonDeserializer<?> construct(DeserializationContext ctxt) throws JsonMappingException {
      if (ctxt == null) {
         return new StackTraceElementDeserializer();
      }

      JsonDeserializer<?> adapterDeser = ctxt.findNonContextualValueDeserializer(ctxt.constructType(StackTraceElementDeserializer.Adapter.class));
      return new StackTraceElementDeserializer(adapterDeser);
   }

   public StackTraceElement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      JsonToken t = p.currentToken();
      if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
         if (t == JsonToken.START_ARRAY && ctxt.isEnabled(DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS)) {
            p.nextToken();
            StackTraceElement value = this.deserialize(p, ctxt);
            if (p.nextToken() != JsonToken.END_ARRAY) {
               this.handleMissingEndArrayForSingle(p, ctxt);
            }

            return value;
         } else {
            return (StackTraceElement)ctxt.handleUnexpectedToken(this._valueClass, p);
         }
      } else {
         StackTraceElementDeserializer.Adapter adapted;
         if (this._adapterDeserializer == null) {
            adapted = ctxt.readValue(p, StackTraceElementDeserializer.Adapter.class);
         } else {
            adapted = (StackTraceElementDeserializer.Adapter)this._adapterDeserializer.deserialize(p, ctxt);
         }

         return this.constructValue(ctxt, adapted);
      }
   }

   protected StackTraceElement constructValue(DeserializationContext ctxt, StackTraceElementDeserializer.Adapter adapted) {
      return this.constructValue(
         ctxt, adapted.className, adapted.methodName, adapted.fileName, adapted.lineNumber, adapted.moduleName, adapted.moduleVersion, adapted.classLoaderName
      );
   }

   @Deprecated
   protected StackTraceElement constructValue(
      DeserializationContext ctxt, String className, String methodName, String fileName, int lineNumber, String moduleName, String moduleVersion
   ) {
      return this.constructValue(ctxt, className, methodName, fileName, lineNumber, moduleName, moduleVersion, null);
   }

   protected StackTraceElement constructValue(
      DeserializationContext ctxt,
      String className,
      String methodName,
      String fileName,
      int lineNumber,
      String moduleName,
      String moduleVersion,
      String classLoaderName
   ) {
      return new StackTraceElement(className, methodName, fileName, lineNumber);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JacksonException {
      return this.deserialize(var1, var2);
   }

   public static final class Adapter {
      public String className = "";
      public String classLoaderName;
      public String declaringClass;
      public String format;
      public String fileName = "";
      public String methodName = "";
      public int lineNumber = -1;
      public String moduleName;
      public String moduleVersion;
      public boolean nativeMethod;
   }
}
