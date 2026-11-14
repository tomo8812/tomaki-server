package com.kishku7.chunksmith.nbt;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CompoundTag extends Tag {
   private Map<String, Tag> value = new HashMap<>();

   protected CompoundTag(String name) {
      super(name);
   }

   public CompoundTag(String name, Map<String, Tag> value) {
      super(name);
      this.value = value;
   }

   @Override
   public void read(DataInput input) throws IOException {
      this.value = new HashMap<>();

      Tag tag;
      while (0 != (tag = Tag.load(input)).type()) {
         this.value.put(tag.name(), tag);
      }
   }

   @Override
   public void skip(DataInput input) throws IOException {
      while (0 != Tag.pass(input)) {
      }
   }

   @Override
   public void write(DataOutput output) throws IOException {
      for (Tag tag : this.value.values()) {
         Tag.save(output, tag);
      }

      output.writeByte(0);
   }

   @Override
   public Tag search(DataInput input, byte type, String name) throws IOException {
      Tag tag;
      while ((tag = Tag.find(input, type, name)) == null) {
      }

      return 0 == tag.type() ? null : tag;
   }

   @Override
   public byte type() {
      return 10;
   }

   @Override
   public String typeName() {
      return "TAG_Compound";
   }

   @Override
   public String print(int level) {
      int size = this.value.size();
      String entry = size == 1 ? "entry" : "entries";
      String indent = " ".repeat(level * 2);
      StringBuilder compoundBuilder = new StringBuilder("%s%s('%s'): %d %s".formatted(" ".repeat(level * 2), this.typeName(), this.name, size, entry));
      compoundBuilder.append('\n').append(indent).append("{\n");

      for (Tag tag : this.value.values()) {
         compoundBuilder.append(tag.print(level + 1)).append('\n');
      }

      compoundBuilder.append(indent).append('}');
      return compoundBuilder.toString();
   }

   public Optional<Tag> get(String name) {
      return Optional.ofNullable(this.value.get(name));
   }

   public Optional<ByteArrayTag> getByteArray(String name) {
      return this.get(name).filter(ByteArrayTag.class::isInstance).flatMap(tag -> Optional.of((ByteArrayTag)tag));
   }

   public Optional<ByteTag> getByte(String name) {
      return this.get(name).filter(ByteTag.class::isInstance).flatMap(tag -> Optional.of((ByteTag)tag));
   }

   public Optional<CompoundTag> getCompound(String name) {
      return this.get(name).filter(CompoundTag.class::isInstance).flatMap(tag -> Optional.of((CompoundTag)tag));
   }

   public Optional<DoubleTag> getDouble(String name) {
      return this.get(name).filter(DoubleTag.class::isInstance).flatMap(tag -> Optional.of((DoubleTag)tag));
   }

   public Optional<FloatTag> getFloat(String name) {
      return this.get(name).filter(FloatTag.class::isInstance).flatMap(tag -> Optional.of((FloatTag)tag));
   }

   public Optional<IntArrayTag> getIntArray(String name) {
      return this.get(name).filter(IntArrayTag.class::isInstance).flatMap(tag -> Optional.of((IntArrayTag)tag));
   }

   public Optional<IntTag> getInt(String name) {
      return this.get(name).filter(IntTag.class::isInstance).flatMap(tag -> Optional.of((IntTag)tag));
   }

   public Optional<ListTag> getList(String name) {
      return this.get(name).filter(ListTag.class::isInstance).flatMap(tag -> Optional.of((ListTag)tag));
   }

   public Optional<LongArrayTag> getLongArray(String name) {
      return this.get(name).filter(LongArrayTag.class::isInstance).flatMap(tag -> Optional.of((LongArrayTag)tag));
   }

   public Optional<LongTag> getLong(String name) {
      return this.get(name).filter(LongTag.class::isInstance).flatMap(tag -> Optional.of((LongTag)tag));
   }

   public Optional<ShortTag> getShort(String name) {
      return this.get(name).filter(ShortTag.class::isInstance).flatMap(tag -> Optional.of((ShortTag)tag));
   }

   public Optional<StringTag> getString(String name) {
      return this.get(name).filter(StringTag.class::isInstance).flatMap(tag -> Optional.of((StringTag)tag));
   }

   public void put(Tag tag) {
      this.value.put(tag.name(), tag);
   }

   public void remove(String name) {
      this.value.remove(name);
   }
}
