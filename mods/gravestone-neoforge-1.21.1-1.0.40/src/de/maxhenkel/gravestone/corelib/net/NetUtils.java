package de.maxhenkel.gravestone.corelib.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;

@Deprecated
public class NetUtils {
   @Deprecated
   public static void sendTo(ServerPlayer player, Message<?> message) {
      PacketDistributor.sendToPlayer(player, message, new CustomPacketPayload[0]);
   }

   @OnlyIn(Dist.CLIENT)
   @Deprecated
   public static void sendToServer(Message<?> message) {
      PacketDistributor.sendToServer(message, new CustomPacketPayload[0]);
   }
}
