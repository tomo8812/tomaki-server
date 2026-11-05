package maki.chunksmithcache;

import net.minecraft.WorldVersion;
import net.minecraft.world.level.storage.DataVersion;

final class WorldVersionBridge {
   private WorldVersionBridge() {
   }

   static DataVersion getDataVersion(WorldVersion var0) {
      return var0.getDataVersion();
   }
}
