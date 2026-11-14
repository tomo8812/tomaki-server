package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.lod.net.CsLodChannel;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.registration.NetworkRegistry;

public final class ClientPlatform {
   private static IEventBus modBus;

   private ClientPlatform() {
   }

   public static void bootstrap(Object bus) {
      modBus = (IEventBus)bus;
   }

   public static boolean isModLoaded(String modId) {
      return ModList.get().isLoaded(modId);
   }

   public static Path gameDir() {
      return FMLPaths.GAMEDIR.get();
   }

   public static void onClientSetup(Runnable action) {
      modBus.addListener(FMLClientSetupEvent.class, event -> event.enqueueWork(action));
   }

   public static void registerClientNetworking(Consumer<byte[]> onPayload) {
      CsLodChannel.setClientSink(onPayload);
   }

   public static void sendToServer(byte[] data) {
      ClientPacketListener connection = Minecraft.getInstance().getConnection();
      if (connection != null && NetworkRegistry.hasChannel(connection, CsLodChannel.Payload.TYPE.id())) {
         PacketDistributor.sendToServer(new CsLodChannel.Payload(data), new CustomPacketPayload[0]);
      }
   }

   public static void onJoin(Runnable action) {
      NeoForge.EVENT_BUS.addListener(LoggingIn.class, event -> action.run());
   }

   public static void onDisconnect(Runnable action) {
      NeoForge.EVENT_BUS.addListener(LoggingOut.class, event -> action.run());
   }

   public static void onClientTick(Runnable action) {
      NeoForge.EVENT_BUS.addListener(Post.class, event -> action.run());
   }
}
