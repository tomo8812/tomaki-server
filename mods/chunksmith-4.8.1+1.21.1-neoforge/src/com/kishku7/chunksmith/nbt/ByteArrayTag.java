package com.kishku7.chunksmith.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class ByteArrayTag extends Tag {
   private byte[] value;

   protected ByteArrayTag(String name) {
      super(name);
   }

   public ByteArrayTag(String name, byte[] value) {
      super(name);
      this.value = value;
   }

   @Override
   public void read(DataInput input) throws IOException {
      int size = input.readInt();
      this.value = new byte[size];
      input.readFully(this.value);
   }

   @Override
   public void skip(DataInput input) throws IOException {
      int size = input.readInt();
      input.skipBytes(size);
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.value.length);
      output.write(this.value);
   }

   @Override
   public Tag search(DataInput input, byte type, String name) throws IOException {
      this.skip(input);
      return null;
   }

   @Override
   public byte type() {
      return 7;
   }

   @Override
   public String typeName() {
      return "TAG_Byte_Array";
   }

   @Override
   public String print(int level) {
      return "%s%s('%s'): %s".formatted(" ".repeat(level * 2), this.typeName(), this.name, Arrays.toString(this.value));
   }

   public byte[] value() {
      return this.value;
   }

   public void value(byte[] value) {
      this.value = value;
   }
}
