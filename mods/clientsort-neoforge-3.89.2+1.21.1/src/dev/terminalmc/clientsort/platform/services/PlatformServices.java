package dev.terminalmc.clientsort.platform.services;

import dev.terminalmc.clientsort.platform.Services;
import java.nio.file.Path;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public interface PlatformServices {
   PlatformServices INSTANCE = Services.load(PlatformServices.class);

   static PlatformServices getInstance() {
      return INSTANCE;
   }

   boolean isDevEnv();

   boolean isModLoaded(String var1);

   boolean hasNamedLogger();

   @Nullable
   String getModVersion(String var1);

   String getPlatformName();

   Path getGameDir();

   Path getConfigDir();

   boolean canSendToPlayer(ServerPlayer var1, Type<?> var2);

   void sendToPlayer(ServerPlayer var1, CustomPacketPayload var2);
}
