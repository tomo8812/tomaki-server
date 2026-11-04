package com.kishku7.chunksmith.lod;

import com.kishku7.chunksmith.lod.net.CsLodServerNet;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@EventBusSubscriber(modid = "chunksmith")
public final class LodInit {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");

   private LodInit() {
   }

   @SubscribeEvent
   public static void onServerAboutToStart(ServerAboutToStartEvent event) {
      if (LodPlatform.isModLoaded("distanthorizons")) {
         try {
            CsLodDhSupport.setServer(event.getServer());
            CsLodDhSupport.register();
         } catch (LinkageError error) {
            LOGGER.warn("Chunksmith: Distant Horizons present but incompatible, skipping: {}", error.toString());
         }
      }
   }

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      LodSupport.announce(event.getServer());
      LodSupport.install(event.getServer());
      CsLodServerNet.onServerStarted(event.getServer());
   }

   @SubscribeEvent
   public static void onServerStopped(ServerStoppedEvent event) {
      CsLodServerNet.onServerStopped();
      LodSupport.shutdown();
   }

   @SubscribeEvent
   public static void onServerTick(Post event) {
      CsLodServerNet.tick(event.getServer());
   }

   @SubscribeEvent
   public static void onLoggedOut(PlayerLoggedOutEvent event) {
      CsLodServerNet.onDisconnect(event.getEntity().getUUID());
   }
}
