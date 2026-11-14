package org.millenaire.compat;

import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;

public final class LegacyItemStacks {
   static final int MC_1_20_1_DATA_VERSION = 3465;

   private LegacyItemStacks() {
   }

   public static void upgradeList(CompoundTag tag, String key) {
      int current = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
      if (current > 3465 && tag.contains(key, 9)) {
         ListTag list = tag.getList(key, 10);

         for (int i = 0; i < list.size(); i++) {
            CompoundTag stack = list.getCompound(i);
            if (isLegacyShape(stack)) {
               list.set(i, upgrade(stack, current));
            }
         }
      }
   }

   static boolean isLegacyShape(CompoundTag stack) {
      return stack.contains("Count", 99) && !stack.contains("count");
   }

   static CompoundTag upgrade(CompoundTag stack, int currentDataVersion) {
      Tag fixed = (Tag)DataFixers.getDataFixer().update(References.ITEM_STACK, new Dynamic(NbtOps.INSTANCE, stack), 3465, currentDataVersion).getValue();
      if (fixed instanceof CompoundTag result) {
         if (stack.contains("Slot", 99)) {
            result.putByte("Slot", stack.getByte("Slot"));
         }

         return result;
      } else {
         return stack;
      }
   }
}
