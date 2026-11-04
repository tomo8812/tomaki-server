package dev.terminalmc.clientsort.client.platform;

import dev.terminalmc.clientsort.client.platform.services.ClientPlatformServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.neoforged.neoforge.network.PacketDistributor;

public class NeoForgeClientPlatformServices implements ClientPlatformServices {
   @Override
   public boolean canSendToServer(Type<?> type) {
      LocalPlayer player = Minecraft.getInstance().player;
      return player != null && player.connection.isAcceptingMessages() && player.connection.hasChannel(type);
   }

   @Override
   public void sendToServer(CustomPacketPayload payload) {
      PacketDistributor.sendToServer(payload, new CustomPacketPayload[0]);
   }
}
