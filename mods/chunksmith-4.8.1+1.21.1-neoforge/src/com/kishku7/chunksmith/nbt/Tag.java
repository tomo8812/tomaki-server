package com.kishku7.chunksmith.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class Tag {
   protected static final int INDENT = 2;
   protected final String name;

   protected Tag(String name) {
      this.name = name;
   }

   public static Tag load(DataInput input) throws IOException {
      byte type = input.readByte();
      if (0 == type) {
         return new EndTag();
      }

      String name = input.readUTF();
      Tag tag = create(type, name);
      tag.read(input);
      return tag;
   }

   public static byte pass(DataInput input) throws IOException {
      byte type = input.readByte();
      if (0 == type) {
         return type;
      }

      int size = input.readUnsignedShort();
      input.skipBytes(size);
      create(type, "").skip(input);
      return type;
   }

   public static void save(DataOutput output, Tag tag) throws IOException {
      byte type = tag.type();
      output.writeByte(type);
      if (0 != type) {
         output.writeUTF(tag.name());
         tag.write(output);
      }
   }

   public static Tag find(DataInput input, byte type, String name) throws IOException {
      byte t = input.readByte();
      if (0 == t) {
         return new EndTag();
      } else {
         String n = input.readUTF();
         Tag tag = create(t, n);
         if (type == t && name.equals(n)) {
            tag.read(input);
            return tag;
         } else {
            return tag.search(input, type, name);
         }
      }
   }

   public static Tag create(byte type, String name) {
      return switch (type) {
         case 0 -> new EndTag();
         case 1 -> new ByteTag(name);
         case 2 -> new ShortTag(name);
         case 3 -> new IntTag(name);
         case 4 -> new LongTag(name);
         case 5 -> new FloatTag(name);
         case 6 -> new DoubleTag(name);
         case 7 -> new ByteArrayTag(name);
         case 8 -> new StringTag(name);
         case 9 -> new ListTag(name);
         case 10 -> new CompoundTag(name);
         case 11 -> new IntArrayTag(name);
         case 12 -> new LongArrayTag(name);
         default -> throw new IllegalArgumentException("Invalid tag type %d".formatted(type));
      };
   }

   public String name() {
      return this.name;
   }

   abstract void read(DataInput var1) throws IOException;

   abstract void skip(DataInput var1) throws IOException;

   abstract void write(DataOutput var1) throws IOException;

   abstract Tag search(DataInput var1, byte var2, String var3) throws IOException;

   abstract byte type();

   abstract String typeName();

   abstract String print(int var1);

   @Override
   public String toString() {
      return this.print(0);
   }
}
