package dev.terminalmc.clientsort;

import dev.terminalmc.clientsort.command.Commands;
import dev.terminalmc.clientsort.network.Registration;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod("clientsort")
@EventBusSubscriber(modid = "clientsort")
public class ClientSortNeoForge {
   public ClientSortNeoForge() {
      ClientSort.init();
   }

   @SubscribeEvent
   static void register(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1").optional();
      Registration.PAYLOADS_C2S.forEach(rp -> registerC2S(registrar, (Registration.RegisterablePayloadC2S<?>)rp));
   }

   private static <T extends CustomPacketPayload> void registerC2S(PayloadRegistrar registrar, Registration.RegisterablePayloadC2S<T> rp) {
      registrar.playToServer(
         rp.type,
         rp.streamCodec,
         new DirectionalPayloadHandler(
            (payload, context) -> rp.handler.accept((T)payload, context.player().getServer(), (ServerPlayer)context.player()),
            (payload, context) -> rp.handler.accept((T)payload, context.player().getServer(), (ServerPlayer)context.player())
         )
      );
   }

   private static <T extends CustomPacketPayload> void registerPayloadS2C(PayloadRegistrar registrar, Registration.RegisterablePayloadS2C<T> rp) {
      registrar.playToClient(rp.type, rp.streamCodec, new DirectionalPayloadHandler((payload, context) -> {}, (payload, context) -> {}));
   }

   @EventBusSubscriber(modid = "clientsort", value = Dist.DEDICATED_SERVER)
   static class DedicatedServerEventHandler {
      @SubscribeEvent
      static void register(RegisterPayloadHandlersEvent event) {
         PayloadRegistrar registrar = event.registrar("1").optional();
         Registration.PAYLOADS_S2C.forEach(rp -> ClientSortNeoForge.registerPayloadS2C(registrar, (Registration.RegisterablePayloadS2C<?>)rp));
      }
   }

   @EventBusSubscriber(modid = "clientsort")
   static class GameEventHandler {
      @SubscribeEvent
      static void registerCommands(RegisterCommandsEvent event) {
         Commands.register(event.getDispatcher(), event.getBuildContext());
      }
   }
}
