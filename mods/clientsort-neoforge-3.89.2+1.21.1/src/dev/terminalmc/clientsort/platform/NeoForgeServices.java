package dev.terminalmc.clientsort.platform;

import dev.terminalmc.clientsort.platform.services.PlatformServices;
import java.nio.file.Path;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.fml.loading.moddiscovery.ModInfo;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgeServices implements PlatformServices {
   @Override
   public boolean isDevEnv() {
      return !FMLLoader.isProduction();
   }

   @Override
   public boolean isModLoaded(String modId) {
      return FMLLoader.getLoadingModList().getModFileById(modId) != null;
   }

   @Override
   public boolean hasNamedLogger() {
      return true;
   }

   @Nullable
   @Override
   public String getModVersion(String modId) {
      for (ModInfo mod : FMLLoader.getLoadingModList().getMods()) {
         if (mod.getModId().equals(modId)) {
            return mod.getVersion().toString();
         }
      }

      return null;
   }

   @Override
   public String getPlatformName() {
      return "NeoForge";
   }

   @Override
   public Path getGameDir() {
      return FMLPaths.GAMEDIR.get();
   }

   @Override
   public Path getConfigDir() {
      return FMLPaths.CONFIGDIR.get();
   }

   @Override
   public boolean canSendToPlayer(ServerPlayer player, Type<?> type) {
      return player.connection.hasChannel(type);
   }

   @Override
   public void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
      PacketDistributor.sendToPlayer(player, payload, new CustomPacketPayload[0]);
   }
}
