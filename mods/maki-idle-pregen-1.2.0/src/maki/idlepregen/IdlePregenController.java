package maki.idlepregen;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.ChunksmithProvider;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import net.minecraft.server.MinecraftServer;

final class IdlePregenController {
   private static final Logger LOG = System.getLogger("MakiIdlePregen");
   private static MinecraftServer owner;
   private static volatile boolean active;

   private IdlePregenController() {
   }

   static void tick(MinecraftServer server) {
      if (owner != null && owner != server) {
         leave("server-changed");
      }

      owner = server;
      boolean shouldBeActive = server.isDedicatedServer() && server.getPlayerCount() == 0 && idleWorkRunning();
      if (shouldBeActive) {
         if (!active) {
            enter(server);
         } else {
            VsPhysicsPauseController.tick(server);
         }
      } else if (active) {
         leave(server.getPlayerCount() > 0 ? "player-online" : "idle-work-stopped");
      }
   }

   static void shutdown(MinecraftServer server) {
      if (owner == server && active) {
         leave("server-stopping");
      }

      if (owner == server) {
         owner = null;
      }
   }

   static boolean isActive() {
      return active;
   }

   private static boolean idleWorkRunning() {
      if (!ChunksmithProvider.isLoaded()) {
         return false;
      }

      Chunksmith chunksmith = ChunksmithProvider.get();
      return chunksmith != null && !chunksmith.getGenerationTasks().isEmpty();
   }

   private static void enter(MinecraftServer server) {
      VsPhysicsPauseController.beginSession();
      active = true;
      VsPhysicsPauseController.tick(server);
      LOG.log(Level.INFO, "Entered idle pregen mode; village activity paused, vsAsyncPauseOwned=" + VsPhysicsPauseController.ownsAsyncPause());
   }

   private static void leave(String reason) {
      active = false;
      VsPhysicsPauseController.leaveSession();
      LOG.log(Level.INFO, "Left idle pregen mode: " + reason);
   }
}
