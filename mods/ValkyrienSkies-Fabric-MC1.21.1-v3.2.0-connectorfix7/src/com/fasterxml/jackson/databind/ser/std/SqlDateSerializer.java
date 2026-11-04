package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;

@JacksonStdImpl
public class SqlDateSerializer extends DateTimeSerializerBase<Date> {
   public SqlDateSerializer() {
      this(null, null);
   }

   protected SqlDateSerializer(Boolean useTimestamp, DateFormat customFormat) {
      super(Date.class, useTimestamp, customFormat);
   }

   public SqlDateSerializer withFormat(Boolean timestamp, DateFormat customFormat) {
      return new SqlDateSerializer(timestamp, customFormat);
   }

   protected long _timestamp(Date value) {
      return value == null ? 0L : value.getTime();
   }

   public void serialize(Date value, JsonGenerator g, SerializerProvider provider) throws IOException {
      if (this._asTimestamp(provider)) {
         g.writeNumber(this._timestamp(value));
      } else if (this._customFormat == null) {
         g.writeString(value.toString());
      } else {
         this._serializeAsString(value, g, provider);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) throws IOException {
      this.serialize((Date)var1, var2, var3);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected long _timestamp(Object var1) {
      return this._timestamp((Date)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public DateTimeSerializerBase withFormat(Boolean var1, DateFormat var2) {
      return this.withFormat(var1, var2);
   }
}
