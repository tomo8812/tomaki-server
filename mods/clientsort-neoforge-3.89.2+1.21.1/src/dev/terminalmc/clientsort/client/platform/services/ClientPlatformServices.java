package dev.terminalmc.clientsort.client.platform.services;

import dev.terminalmc.clientsort.client.platform.ClientServices;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;

public interface ClientPlatformServices {
   ClientPlatformServices INSTANCE = ClientServices.load(ClientPlatformServices.class);

   static ClientPlatformServices getInstance() {
      return INSTANCE;
   }

   boolean canSendToServer(Type<?> var1);

   void sendToServer(CustomPacketPayload var1);
}
