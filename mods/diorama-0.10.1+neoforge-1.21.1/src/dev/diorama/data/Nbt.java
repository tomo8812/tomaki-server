package dev.diorama.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.jspecify.annotations.Nullable;

public final class Nbt {
   private Nbt() {
   }

   public static CompoundTag compound(CompoundTag tag, String name) {
      return tag.getCompound(name);
   }

   public static CompoundTag compound(ListTag list, int index) {
      return list.getCompound(index);
   }

   public static ListTag list(CompoundTag tag, String name) {
      return tag.getList(name, 10);
   }

   public static String string(CompoundTag tag, String name) {
      return tag.getString(name);
   }

   public static int sectionY(CompoundTag section) {
      return section.getByte("Y");
   }

   public static long longValue(CompoundTag tag, String name) {
      return tag.getLong(name);
   }

   public static byte @Nullable [] bytes(CompoundTag tag, String name) {
      return tag.contains(name, 7) ? tag.getByteArray(name) : null;
   }

   public static long @Nullable [] longs(CompoundTag tag, String name) {
      return tag.contains(name, 12) ? tag.getLongArray(name) : null;
   }
}
