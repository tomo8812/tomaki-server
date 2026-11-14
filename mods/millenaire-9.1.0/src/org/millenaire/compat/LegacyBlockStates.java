package org.millenaire.compat;

import java.util.Map;
import net.minecraft.nbt.CompoundTag;

public final class LegacyBlockStates {
   private static final Map<String, String> RENAMED = Map.of("minecraft:grass", "minecraft:short_grass");

   private LegacyBlockStates() {
   }

   public static CompoundTag upgrade(CompoundTag stateTag) {
      String renamed = RENAMED.get(stateTag.getString("Name"));
      if (renamed == null) {
         return stateTag;
      }

      CompoundTag copy = stateTag.copy();
      copy.putString("Name", renamed);
      return copy;
   }
}
