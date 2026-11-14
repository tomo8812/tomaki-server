package com.kishku7.chunksmith.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntTag extends Tag {
   private int value;

   protected IntTag(String name) {
      super(name);
   }

   public IntTag(String name, int value) {
      super(name);
      this.value = value;
   }

   @Override
   public void read(DataInput input) throws IOException {
      this.value = input.readInt();
   }

   @Override
   public void skip(DataInput input) throws IOException {
      input.skipBytes(4);
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeInt(this.value);
   }

   @Override
   public Tag search(DataInput input, byte type, String name) throws IOException {
      this.skip(input);
      return null;
   }

   @Override
   public byte type() {
      return 3;
   }

   @Override
   public String typeName() {
      return "TAG_Int";
   }

   @Override
   public String print(int level) {
      return "%s%s('%s'): %d".formatted(" ".repeat(level * 2), this.typeName(), this.name, this.value);
   }

   public int value() {
      return this.value;
   }

   public void value(int value) {
      this.value = value;
   }
}
