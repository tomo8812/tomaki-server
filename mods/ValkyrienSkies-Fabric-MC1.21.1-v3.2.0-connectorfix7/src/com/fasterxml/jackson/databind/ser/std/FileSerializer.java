package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;

public class FileSerializer extends StdScalarSerializer<File> {
   public FileSerializer() {
      super(File.class);
   }

   public void serialize(File value, JsonGenerator g, SerializerProvider provider) throws IOException {
      g.writeString(value.getAbsolutePath());
   }

   @Override
   public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
      return this.createSchemaNode("string", true);
   }

   @Override
   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      this.visitStringFormat(visitor, typeHint);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) throws IOException {
      this.serialize((File)var1, var2, var3);
   }
}
