package com.kishku7.chunksmith.lod;

import java.nio.file.Path;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

public final class LodPlatform {
   private LodPlatform() {
   }

   public static boolean isModLoaded(String modId) {
      return ModList.get().isLoaded(modId);
   }

   public static Path configDir() {
      return FMLPaths.CONFIGDIR.get();
   }
}
