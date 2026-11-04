package net.fabricmc.loader.impl.launch;

import net.neoforged.fml.loading.FMLLoader;

public final class MappingConfiguration {
   private final String gameId = "minecraft";
   private final String gameVersion = FMLLoader.versionInfo().mcVersion();

   public String getGameId() {
      return "minecraft";
   }

   public String getGameVersion() {
      return this.gameVersion;
   }

   public boolean matches(String gameId, String gameVersion) {
      return ("minecraft" == null || gameId == null || gameId.equals("minecraft"))
         && (this.gameVersion == null || gameVersion == null || gameVersion.equals(this.gameVersion));
   }

   public String getTargetNamespace() {
      return "mojang";
   }

   public boolean requiresPackageAccessHack() {
      return false;
   }
}
