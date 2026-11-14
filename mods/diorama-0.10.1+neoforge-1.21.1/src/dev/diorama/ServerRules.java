package dev.diorama;

import dev.diorama.share.ServerLocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;

public final class ServerRules {
   static final String FAIR_PLAY = "§f§a§i§r§x§a§e§r§o";
   static final String RESET = "§r§e§s§e§t§x§a§e§r§o";
   static final String NETHER_ALLOWED = "§x§a§e§r§o§m§m§n§e§t§h§e§r§i§s§f§a§i§r";
   static final String NO_MINIMAP = "§n§o§m§i§n§i§m§a§p";
   private static boolean fairPlay;
   private static boolean netherAllowed;
   private static boolean noMinimap;
   private static ServerLocks locks = ServerLocks.NONE;
   private static String lastMapOffMessage = "";
   private static int mapOffMessages;
   private static boolean alwaysFogOfWar = Boolean.getBoolean("diorama.fogOfWar");

   private ServerRules() {
   }

   public static void onServerMessage(Component message) {
      String text = message.getString();
      if (text.contains("§r§e§s§e§t§x§a§e§r§o")) {
         fairPlay = false;
         netherAllowed = false;
         noMinimap = false;
      }

      if (text.contains("§f§a§i§r§x§a§e§r§o")) {
         fairPlay = true;
         Diorama.LOGGER.info("Server asked for fair play: cave slices show only what you've seen, and the radar is off");
      }

      if (text.contains("§x§a§e§r§o§m§m§n§e§t§h§e§r§i§s§f§a§i§r")) {
         netherAllowed = true;
      }

      if (text.contains("§n§o§m§i§n§i§m§a§p")) {
         noMinimap = true;
         Diorama.LOGGER.info("Server disabled minimaps");
      }
   }

   public static void onLocks(ServerLocks server) {
      locks = server;
      Diorama.LOGGER.info("The server locks map settings: {}", server);
   }

   public static void onDisconnect() {
      fairPlay = false;
      netherAllowed = false;
      noMinimap = false;
      locks = ServerLocks.NONE;
   }

   public static boolean fogOfWar(boolean nether) {
      return fogOfWar(nether, nether);
   }

   public static boolean fogOfWar(boolean nether, boolean roofed) {
      return alwaysFogOfWar || locks.caveFogOfWar() || fairPlay && (!nether || !netherAllowed) || roofed && !locks.caveMapsAllowed();
   }

   public static boolean fogOfWarForced() {
      return locks.caveFogOfWar() || fairPlay && !netherAllowed;
   }

   public static boolean caveMapsAllowed() {
      return locks.caveMapsAllowed();
   }

   public static boolean radarAllowed() {
      return !fairPlay && locks.radarAllowed();
   }

   public static boolean minimapAllowed() {
      return !noMinimap && !mapOffHere();
   }

   public static boolean mapOff(String dimension) {
      return mapOffByServer(dimension) || Settings.get().mapOffDimensions.contains(dimension);
   }

   public static boolean mapOffByServer(String dimension) {
      return locks.mapOffDimensions().contains(dimension);
   }

   public static boolean mapOffHere() {
      ClientLevel level = Minecraft.getInstance().level;
      return level != null && mapOff(level.dimension().location().toString());
   }

   public static boolean sayMapOffHere(Minecraft mc) {
      if (mc.level != null && mapOffHere()) {
         String here = mc.level.dimension().location().toString();
         Component message = Component.translatable(
            !mapOffByServer(here) ? "diorama.map_off.here" : (mc.hasSingleplayerServer() ? "diorama.map_off.here_file" : "diorama.map_off.here_server")
         );
         lastMapOffMessage = message.getString();
         mapOffMessages++;
         mc.gui.setOverlayMessage(message, false);
         return true;
      } else {
         return false;
      }
   }

   public static String lastMapOffMessage() {
      return mapOffMessages + ": " + lastMapOffMessage;
   }

   public static void setAlwaysFogOfWar(boolean value) {
      alwaysFogOfWar = value;
   }

   public static boolean alwaysFogOfWar() {
      return alwaysFogOfWar;
   }
}
