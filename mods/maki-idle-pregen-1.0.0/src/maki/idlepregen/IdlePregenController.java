package maki.idlepregen;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.ChunksmithProvider;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTickRateManager;

final class IdlePregenController {
   private static final Logger LOG = System.getLogger("MakiIdlePregen");
   private static MinecraftServer owner;
   private static boolean active;
   private static boolean freezeOwned;

   private IdlePregenController() {
   }

   static void tick(MinecraftServer var0) {
      if (owner != null && owner != var0) {
         leave(owner, "server-changed");
      }

      owner = var0;
      boolean var1 = var0.isDedicatedServer() && var0.getPlayerCount() == 0 && idleWorkRunning();
      if (var1) {
         if (!active) {
            enter(var0);
         } else {
            observeFreezeOwnership(var0);
         }
      } else if (active) {
         leave(var0, var0.getPlayerCount() > 0 ? "player-online" : "idle-work-stopped");
      }
   }

   static void shutdown(MinecraftServer var0) {
      if (owner == var0 && active) {
         leave(var0, "server-stopping");
      }

      if (owner == var0) {
         owner = null;
         IdlePregenApi.clearExternalIdleWork();
      }
   }

   static boolean isActive() {
      return active;
   }

   private static boolean idleWorkRunning() {
      if (IdlePregenApi.hasExternalIdleWork()) {
         return true;
      }

      if (!ChunksmithProvider.isLoaded()) {
         return false;
      }

      Chunksmith var0 = ChunksmithProvider.get();
      return var0 != null && !var0.getGenerationTasks().isEmpty();
   }

   private static void enter(MinecraftServer var0) {
      freezeOwned = false;
      ServerTickRateManager var1 = var0.tickRateManager();
      if (!var1.isFrozen()) {
         var1.setFrozen(true);
         freezeOwned = var1.isFrozen();
      }

      active = true;
      LOG.log(Level.INFO, "Entered idle pregen mode; freezeOwned=" + freezeOwned);
   }

   private static void observeFreezeOwnership(MinecraftServer var0) {
      if (freezeOwned && !var0.tickRateManager().isFrozen()) {
         freezeOwned = false;
         LOG.log(Level.INFO, "Server freeze was changed externally while idle pregen was active; ownership released");
      }
   }

   private static void leave(MinecraftServer var0, String var1) {
      if (freezeOwned) {
         ServerTickRateManager var2 = var0.tickRateManager();
         if (var2.isFrozen()) {
            var2.setFrozen(false);
         }

         freezeOwned = false;
      }

      active = false;
      LOG.log(Level.INFO, "Left idle pregen mode: " + var1);
   }
}
