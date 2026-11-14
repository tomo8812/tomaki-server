package com.kishku7.chunksmith.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListTag extends Tag {
   private byte type;
   private List<Tag> value = new ArrayList<>();

   protected ListTag(String name) {
      super(name);
   }

   public ListTag(String name, byte type, List<Tag> value) {
      super(name);
      this.type = type;
      this.value = value;
   }

   @Override
   public void read(DataInput input) throws IOException {
      this.type = input.readByte();
      int size = input.readInt();
      this.value = new ArrayList<>();

      for (int i = 0; i < size; i++) {
         Tag tag = Tag.create(this.type, "");
         tag.read(input);
         this.value.add(tag);
      }
   }

   @Override
   public void skip(DataInput input) throws IOException {
      this.type = input.readByte();
      int size = input.readInt();
      Tag tag = Tag.create(this.type, "");

      for (int i = 0; i < size; i++) {
         tag.skip(input);
      }
   }

   @Override
   public void write(DataOutput output) throws IOException {
      output.writeByte(this.type);
      int size = this.value.size();
      output.writeInt(size);

      for (Tag tag : this.value) {
         tag.write(output);
      }
   }

   @Override
   public Tag search(DataInput input, byte type, String name) throws IOException {
      this.skip(input);
      return null;
   }

   @Override
   public byte type() {
      return 9;
   }

   @Override
   public String typeName() {
      return "TAG_List";
   }

   @Override
   public String print(int level) {
      int size = this.value.size();
      String entry = size == 1 ? "entry" : "entries";
      String indent = " ".repeat(level * 2);
      StringBuilder listBuilder = new StringBuilder("%s%s('%s'): %d %s".formatted(indent, this.typeName(), this.name, size, entry));
      listBuilder.append('\n').append(indent).append("{\n");

      for (Tag tag : this.value) {
         listBuilder.append(tag.print(level + 1)).append('\n');
      }

      listBuilder.append(indent).append('}');
      return listBuilder.toString();
   }

   public List<Tag> value() {
      return this.value;
   }

   public void value(List<Tag> value) {
      if (!value.isEmpty()) {
         this.type = value.get(0).type();
      }

      this.value = value;
   }
}
