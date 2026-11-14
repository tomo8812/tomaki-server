package dev.diorama.neoforge;

import dev.diorama.share.ShareProtocol;
import dev.diorama.share.ShareServer;
import dev.diorama.web.WebMap;
import dev.diorama.web.WebMapCommand;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("diorama")
public final class DioramaNeoForgeCommon {
   public DioramaNeoForgeCommon(IEventBus modBus) {
      modBus.addListener(RegisterPayloadHandlersEvent.class, DioramaNeoForgeCommon::register);
      ShareProtocol.sender((player, data) -> player.connection.send(new ShareProtocol.Packet(data)));
      NeoForge.EVENT_BUS.addListener(ServerStartedEvent.class, event -> {
         ShareServer.started(event.getServer(), FMLPaths.CONFIGDIR.get());
         WebMap.started(event.getServer(), FMLPaths.CONFIGDIR.get(), FMLPaths.MODSDIR.get());
      });
      NeoForge.EVENT_BUS.addListener(ServerStoppingEvent.class, event -> WebMap.stopped());
      NeoForge.EVENT_BUS.addListener(ServerStoppedEvent.class, event -> ShareServer.stopped(event.getServer()));
      NeoForge.EVENT_BUS.addListener(RegisterCommandsEvent.class, event -> WebMapCommand.register(event.getDispatcher()));
      NeoForge.EVENT_BUS.addListener(PlayerLoggedInEvent.class, event -> {
         if (event.getEntity() instanceof ServerPlayer player && player.connection.hasChannel(ShareProtocol.TYPE)) {
            ShareServer.listening(player);
         }
      });
   }

   private static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1").optional();
      registrar.playBidirectional(ShareProtocol.TYPE, ShareProtocol.CODEC, (packet, context) -> {
         if (context.flow() == PacketFlow.CLIENTBOUND) {
            ShareProtocol.toClient(packet.data());
         } else if (context.player() instanceof ServerPlayer player) {
            ShareServer.receive(player, packet.data());
         }
      });
   }
}
