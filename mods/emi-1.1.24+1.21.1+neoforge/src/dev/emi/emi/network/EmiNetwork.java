package dev.emi.emi.network;

import dev.emi.emi.EmiPort;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.level.ServerPlayer;

public class EmiNetwork {
   public static final Type<FillRecipeC2SPacket> FILL_RECIPE = new Type(EmiPort.id("emi:fill_recipe"));
   public static final Type<CreateItemC2SPacket> CREATE_ITEM = new Type(EmiPort.id("emi:create_item"));
   public static final Type<CommandS2CPacket> COMMAND = new Type(EmiPort.id("emi:command"));
   public static final Type<EmiChessPacket> CHESS = new Type(EmiPort.id("emi:chess"));
   public static final Type<PingS2CPacket> PING = new Type(EmiPort.id("emi:ping"));
   private static BiConsumer<ServerPlayer, EmiPacket> clientSender;
   private static Consumer<EmiPacket> serverSender;

   public static void initServer(BiConsumer<ServerPlayer, EmiPacket> sender) {
      clientSender = sender;
   }

   public static void initClient(Consumer<EmiPacket> sender) {
      serverSender = sender;
   }

   public static void sendToClient(ServerPlayer player, EmiPacket packet) {
      clientSender.accept(player, packet);
   }

   public static void sendToServer(EmiPacket packet) {
      serverSender.accept(packet);
   }
}
