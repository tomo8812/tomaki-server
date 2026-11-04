package com.fasterxml.jackson.databind.ext;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdScalarSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;

@JacksonStdImpl
public class SqlBlobSerializer extends StdScalarSerializer<Blob> {
   public SqlBlobSerializer() {
      super(Blob.class);
   }

   public boolean isEmpty(SerializerProvider provider, Blob value) {
      return value == null;
   }

   public void serialize(Blob value, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
      this._writeValue(value, gen, ctxt);
   }

   public void serializeWithType(Blob value, JsonGenerator gen, SerializerProvider ctxt, TypeSerializer typeSer) throws IOException {
      WritableTypeId typeIdDef = typeSer.writeTypePrefix(gen, typeSer.typeId(value, JsonToken.VALUE_EMBEDDED_OBJECT));
      this._writeValue(value, gen, ctxt);
      typeSer.writeTypeSuffix(gen, typeIdDef);
   }

   protected void _writeValue(Blob value, JsonGenerator gen, SerializerProvider ctxt) throws IOException {
      InputStream in = null;

      try {
         in = value.getBinaryStream();
      } catch (SQLException e) {
         ctxt.reportMappingProblem(e, "Failed to access `java.sql.Blob` value to write as binary value");
      }

      gen.writeBinary(ctxt.getConfig().getBase64Variant(), in, -1);
   }

   @Override
   public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
      JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
      if (v2 != null) {
         v2.itemsFormat(JsonFormatTypes.INTEGER);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serializeWithType(Object var1, JsonGenerator var2, SerializerProvider var3, TypeSerializer var4) throws IOException {
      this.serializeWithType((Blob)var1, var2, var3, var4);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) throws IOException {
      this.serialize((Blob)var1, var2, var3);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean isEmpty(SerializerProvider var1, Object var2) {
      return this.isEmpty(var1, (Blob)var2);
   }
}
