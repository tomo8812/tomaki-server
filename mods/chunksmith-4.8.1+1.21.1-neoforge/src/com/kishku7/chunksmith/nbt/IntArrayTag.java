package com.kishku7.chunksmith.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class IntArrayTag extends Tag {
   private int[] value;

   protected IntArrayTag(String name) {
      super(name);
   }

   public IntArrayTag(String name, int[] value) {
      super(name);
      this.value = value;
   }

   @Override
   public void read(DataInput input) throws IOException {
      int size = input.readInt();
      this.value = new int[size];

      for (int i = 0; i < size; i++) {
         this.value[i] = input.readInt();
      }
   }

   @Override
   public void skip(DataInput input) throws IOException {
      int size = input.readInt();
      input.skipBytes(4 * size);
   }

   @Override
   public void write(DataOutput output) throws IOException {
      int size = this.value.length;
      output.writeInt(size);

      for (int i : this.value) {
         output.writeInt(i);
      }
   }

   @Override
   public Tag search(DataInput input, byte type, String name) throws IOException {
      this.skip(input);
      return null;
   }

   @Override
   public byte type() {
      return 11;
   }

   @Override
   public String typeName() {
      return "TAG_Int_Array";
   }

   @Override
   public String print(int level) {
      return "%s%s('%s'): %s".formatted(" ".repeat(level * 2), this.typeName(), this.name, Arrays.toString(this.value));
   }

   public int[] value() {
      return this.value;
   }

   public void value(int[] value) {
      this.value = value;
   }
}
