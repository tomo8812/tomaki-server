package dev.diorama.neoforge;

import dev.diorama.Claims;
import dev.diorama.Diorama;
import dev.diorama.DioramaClient;
import dev.diorama.MapSession;
import dev.diorama.Maps;
import dev.diorama.MinimapHud;
import dev.diorama.ServerRules;
import dev.diorama.Settings;
import dev.diorama.SettingsScreen;
import dev.diorama.TrackedPlayers;
import dev.diorama.WorldKey;
import dev.diorama.XaeroChannels;
import dev.diorama.bake.BlockLooks;
import dev.diorama.drawing.DrawingStore;
import dev.diorama.render.XaeroRadarIcons;
import dev.diorama.share.ShareClient;
import dev.diorama.share.ShareProtocol;
import dev.diorama.waypoint.WaypointHud;
import dev.diorama.waypoint.WaypointShare;
import dev.diorama.waypoint.WaypointStore;
import dev.diorama.waypoint.Waystones;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientChatReceivedEvent.System;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingIn;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent.LoggingOut;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.GameShuttingDownEvent;
import net.neoforged.neoforge.event.level.ChunkEvent.Load;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

@Mod(value = "diorama", dist = Dist.CLIENT)
public final class DioramaNeoForge {
   public DioramaNeoForge(IEventBus modBus, ModContainer container) {
      DioramaClient.init();
      ShareClient.init();
      ShareProtocol.toServer(data -> {
         ClientPacketListener connection = Minecraft.getInstance().getConnection();
         if (connection != null) {
            connection.send(new ShareProtocol.Packet(data));
         }
      });
      container.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory)(mod, parent) -> new SettingsScreen(parent));
      modBus.addListener(FMLClientSetupEvent.class, event -> event.enqueueWork(Settings::get));
      modBus.addListener(RegisterKeyMappingsEvent.class, event -> DioramaClient.KEY_MAPPINGS.forEach(event::register));
      modBus.addListener(RegisterGuiLayersEvent.class, event -> {
         WaypointHud waypoints = new WaypointHud();
         MinimapHud minimap = new MinimapHud();
         event.registerBelowAll(Diorama.id("waypoints"), (graphics, deltaTracker) -> waypoints.render(graphics, deltaTracker));
         event.registerAboveAll(Diorama.id("minimap"), (graphics, deltaTracker) -> minimap.render(graphics, deltaTracker));
      });
      NeoForge.EVENT_BUS.addListener(Load.class, event -> {
         if (event.getLevel() instanceof ClientLevel level && event.getChunk() instanceof LevelChunk chunk && Minecraft.getInstance().isRunning()) {
            MapSession.chunkLoaded(level, chunk);
         }
      });
      NeoForge.EVENT_BUS.addListener(EventPriority.HIGHEST, true, System.class, event -> ServerRules.onServerMessage(event.getMessage()));
      NeoForge.EVENT_BUS.addListener(RegisterClientCommandsEvent.class, event -> WaypointShare.register(event.getDispatcher()));
      NeoForge.EVENT_BUS.addListener(LoggingOut.class, event -> {
         ServerRules.onDisconnect();
         WaypointStore.onLeave();
         Maps.onLeave();
         DrawingStore.onLeave();
         WorldKey.onJoinOrLeave();
         XaeroChannels.onJoinOrLeave();
         ShareClient.onJoinOrLeave();
         TrackedPlayers.clear();
         Waystones.clear();
         Claims.clear();
      });
      NeoForge.EVENT_BUS.addListener(LoggingIn.class, event -> {
         ServerRules.onDisconnect();
         WaypointStore.onLeave();
         Maps.onLeave();
         DrawingStore.onLeave();
         WorldKey.onJoinOrLeave();
         BlockLooks.invalidate();
         XaeroChannels.onJoinOrLeave();
         ShareClient.onJoinOrLeave();
         TrackedPlayers.clear();
         Waystones.clear();
         Claims.clear();
      });
      modBus.addListener(EventPriority.LOWEST, false, RegisterPayloadHandlersEvent.class, XaeroChannels::register);
      modBus.addListener(RegisterClientReloadListenersEvent.class, event -> event.registerReloadListener(XaeroRadarIcons.LISTENER));
      NeoForge.EVENT_BUS.addListener(GameShuttingDownEvent.class, event -> MapSession.close());
      NeoForge.EVENT_BUS.addListener(Post.class, event -> {
         Minecraft mc = Minecraft.getInstance();
         if (mc.isRunning()) {
            DioramaClient.tick(mc);
         }
      });
   }
}
