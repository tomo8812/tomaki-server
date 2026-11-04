package com.kishku7.chunksmith.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringTag extends Tag {
   private String value;

   protected StringTag(String name) {
      super(name);
   }

   public StringTag(String name, String value) {
      super(name);
      this.value = value;
   }

   @Override
   public void read(DataInput input) throws IOException {
      this.value = input.readUTF();
   }

   @Override
   public void skip(DataInput input) throws IOException {
      int size = input.readUnsignedShort();
      input.skipBytes(size);
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeUTF(this.value);
   }

   @Override
   public Tag search(DataInput input, byte type, String name) throws IOException {
      this.skip(input);
      return null;
   }

   @Override
   public byte type() {
      return 8;
   }

   @Override
   public String typeName() {
      return "TAG_String";
   }

   @Override
   public String print(int level) {
      return "%s%s('%s'): '%s'".formatted(" ".repeat(level * 2), this.typeName(), this.name, this.value);
   }

   public String value() {
      return this.value;
   }

   public void value(String value) {
      this.value = value;
   }
}
