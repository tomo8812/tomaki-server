package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;

@JacksonStdImpl
public class TokenBufferDeserializer extends StdScalarDeserializer<TokenBuffer> {
   private static final long serialVersionUID = 1L;

   public TokenBufferDeserializer() {
      super(TokenBuffer.class);
   }

   @Override
   public LogicalType logicalType() {
      return LogicalType.Untyped;
   }

   public TokenBuffer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
      return ctxt.bufferForInputBuffering(p).deserialize(p, ctxt);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object deserialize(JsonParser var1, DeserializationContext var2) throws IOException, JacksonException {
      return this.deserialize(var1, var2);
   }
}
