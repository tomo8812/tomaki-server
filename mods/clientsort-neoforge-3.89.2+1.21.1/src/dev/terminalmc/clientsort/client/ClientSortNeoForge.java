package dev.terminalmc.clientsort.client;

import dev.terminalmc.clientsort.client.command.Commands;
import dev.terminalmc.clientsort.client.gui.screen.config.ConfigScreenProvider;
import dev.terminalmc.clientsort.client.network.ClientRegistration;
import dev.terminalmc.clientsort.client.util.KeybindManager;
import dev.terminalmc.clientsort.util.Localization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.Pack.Position;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@Mod(value = "clientsort", dist = Dist.CLIENT)
@EventBusSubscriber(modid = "clientsort", value = Dist.CLIENT)
public class ClientSortNeoForge {
   public ClientSortNeoForge() {
      ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> (mc, parent) -> ConfigScreenProvider.getConfigScreen(parent));
      ClientSort.init();
   }

   @SubscribeEvent
   static void registerKeybinds(RegisterKeyMappingsEvent event) {
      KeybindManager.KEYBINDS.forEach(event::register);
   }

   @SubscribeEvent
   static void addPackFinders(AddPackFindersEvent event) {
      if (event.getPackType().equals(PackType.CLIENT_RESOURCES)) {
         event.addPackFinders(
            ResourceLocation.fromNamespaceAndPath("clientsort", "resourcepacks/clientsort-dark-mode"),
            PackType.CLIENT_RESOURCES,
            Localization.localized("resourcepack", "dark-mode"),
            PackSource.BUILT_IN,
            false,
            Position.TOP
         );
      }
   }

   @SubscribeEvent
   static void registerPayloads(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1").optional();
      ClientRegistration.PAYLOADS_S2C.forEach(rp -> registerS2C(registrar, (ClientRegistration.RegisterablePayloadS2C<?>)rp));
   }

   private static <T extends CustomPacketPayload> void registerS2C(PayloadRegistrar registrar, ClientRegistration.RegisterablePayloadS2C<T> rp) {
      registrar.playToClient(
         rp.type, rp.streamCodec, (payload, context) -> rp.handler.accept((T)payload, Minecraft.getInstance(), (LocalPlayer)context.player())
      );
   }

   @EventBusSubscriber(modid = "clientsort", value = Dist.CLIENT)
   static class ClientEventHandler {
      @SubscribeEvent
      static void registerClientCommands(RegisterClientCommandsEvent event) {
         Commands.register(event.getDispatcher(), event.getBuildContext());
      }

      @SubscribeEvent
      public static void afterClientTick(Post event) {
         ClientSort.afterClientTick(Minecraft.getInstance());
      }
   }
}
