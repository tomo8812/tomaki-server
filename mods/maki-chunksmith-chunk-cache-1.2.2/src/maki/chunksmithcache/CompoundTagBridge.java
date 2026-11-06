package maki.chunksmithcache;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

final class CompoundTagBridge {
   private CompoundTagBridge() {
   }

   static void put(CompoundTag var0, String var1, Tag var2) {
      var0.put(var1, var2);
   }
}
