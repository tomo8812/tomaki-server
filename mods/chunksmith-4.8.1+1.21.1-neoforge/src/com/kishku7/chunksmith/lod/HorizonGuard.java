package com.kishku7.chunksmith.lod;

import com.kishku7.chunksmith.PlatformCompat;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HorizonGuard {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static final int KEEP_EVERY_TICKS = 20;
   private static final Pattern C2ME_RAW_SERIALIZER = Pattern.compile("(?m)^\\s*gcFreeChunkSerializer\\s*=\\s*true\\b");
   private static volatile MinecraftServer server;
   private static int tickCounter;

   private HorizonGuard() {
   }

   public static void install(MinecraftServer current) {
      server = current;
      String reason = null;
      if (PlatformCompat.ENABLE_MOONRISE_WORKAROUNDS) {
         reason = "Moonrise is installed, and it saves chunks its own way; your pregen can still run with the horizon off";
      } else if (PlatformCompat.ENABLE_C2ME_TICKET_COMPAT && c2meRawSerializerOn()) {
         reason = "C2ME's gcFreeChunkSerializer is on, and it writes chunks past the point Chunksmith can stop; turn it off in config/c2me.toml";
      }

      LodHorizon.setUnavailableReason(reason);
      if (reason != null) {
         LOGGER.info("Chunksmith: LOD horizon unavailable on this server -- {}.", reason);
      }
   }

   private static boolean c2meRawSerializerOn() {
      try {
         Path config = Path.of("config", "c2me.toml");
         return Files.isRegularFile(config) && C2ME_RAW_SERIALIZER.matcher(Files.readString(config)).find();
      } catch (Exception e) {
         return false;
      }
   }

   public static boolean armed() {
      return LodHorizon.anyArmed();
   }

   public static void tick(MinecraftServer current) {
      if (LodHorizon.anyArmed() && ++tickCounter % 20 == 0) {
         int radius = current.getPlayerList().getViewDistance() + 1;

         for (ServerLevel level : current.getAllLevels()) {
            String world = LodSupport.dimensionId(level);
            if (LodHorizon.isArmed(world)) {
               for (ServerPlayer player : level.players()) {
                  ChunkPos at = player.chunkPosition();
                  LodHorizon.keepAround(world, at.getMinBlockX() >> 4, at.getMinBlockZ() >> 4, radius);
               }
            }
         }
      }
   }

   public static boolean discardChunk(ServerLevel level, ChunkPos pos) {
      if (!drop(level, pos)) {
         return false;
      }

      LodHorizon.noteDiscarded(LodSupport.dimensionId(level));
      return true;
   }

   public static boolean drop(ServerLevel level, ChunkPos pos) {
      if (LodHorizon.anyArmed() && level != null && pos != null) {
         String world = LodSupport.dimensionId(level);
         int chunkX = pos.getMinBlockX() >> 4;
         int chunkZ = pos.getMinBlockZ() >> 4;
         if (!LodHorizon.inDiscardZone(world, chunkX, chunkZ)) {
            return false;
         } else if (playerNear(level, chunkX, chunkZ)) {
            LodHorizon.keepAround(world, chunkX, chunkZ, 0);
            return false;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   public static boolean dropPoi(Object poiManager, ChunkPos pos) {
      MinecraftServer current = server;
      if (LodHorizon.anyArmed() && current != null) {
         for (ServerLevel level : current.getAllLevels()) {
            if (level.getPoiManager() == poiManager) {
               return drop(level, pos);
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private static boolean playerNear(ServerLevel level, int chunkX, int chunkZ) {
      int radius = level.getServer().getPlayerList().getViewDistance() + 1;

      for (ServerPlayer player : level.players()) {
         ChunkPos at = player.chunkPosition();
         if (Math.abs((at.getMinBlockX() >> 4) - chunkX) <= radius && Math.abs((at.getMinBlockZ() >> 4) - chunkZ) <= radius) {
            return true;
         }
      }

      return false;
   }
}
