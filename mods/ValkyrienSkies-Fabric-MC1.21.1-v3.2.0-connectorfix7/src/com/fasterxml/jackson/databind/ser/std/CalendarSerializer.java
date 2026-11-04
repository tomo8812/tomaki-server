package com.fasterxml.jackson.databind.ser.std;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Calendar;

@JacksonStdImpl
public class CalendarSerializer extends DateTimeSerializerBase<Calendar> {
   public static final CalendarSerializer instance = new CalendarSerializer();

   public CalendarSerializer() {
      this(null, null);
   }

   public CalendarSerializer(Boolean useTimestamp, DateFormat customFormat) {
      super(Calendar.class, useTimestamp, customFormat);
   }

   public CalendarSerializer withFormat(Boolean timestamp, DateFormat customFormat) {
      return new CalendarSerializer(timestamp, customFormat);
   }

   protected long _timestamp(Calendar value) {
      return value == null ? 0L : value.getTimeInMillis();
   }

   public void serialize(Calendar value, JsonGenerator g, SerializerProvider provider) throws IOException {
      if (this._asTimestamp(provider)) {
         g.writeNumber(this._timestamp(value));
      } else {
         this._serializeAsString(value.getTime(), g, provider);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) throws IOException {
      this.serialize((Calendar)var1, var2, var3);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected long _timestamp(Object var1) {
      return this._timestamp((Calendar)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public DateTimeSerializerBase withFormat(Boolean var1, DateFormat var2) {
      return this.withFormat(var1, var2);
   }
}
