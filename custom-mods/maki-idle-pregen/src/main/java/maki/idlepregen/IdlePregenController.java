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
    private static volatile boolean active;
    private static boolean freezeOwned;

    private IdlePregenController() {
    }

    static void tick(MinecraftServer server) {
        if (owner != null && owner != server) {
            leave(owner, "server-changed");
        }

        owner = server;
        boolean shouldBeActive =
                server.isDedicatedServer()
                && server.getPlayerCount() == 0
                && idleWorkRunning();

        if (shouldBeActive) {
            if (!active) {
                enter(server);
            } else {
                observeFreezeOwnership(server);
                VsPhysicsPauseController.tick(server);
            }
        } else if (active) {
            leave(server, server.getPlayerCount() > 0 ? "player-online" : "idle-work-stopped");
        }
    }

    static void shutdown(MinecraftServer server) {
        if (owner == server && active) {
            leave(server, "server-stopping");
        }

        if (owner == server) {
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

        Chunksmith chunksmith = ChunksmithProvider.get();
        return chunksmith != null && !chunksmith.getGenerationTasks().isEmpty();
    }

    private static void enter(MinecraftServer server) {
        freezeOwned = false;
        ServerTickRateManager tickRateManager = server.tickRateManager();
        if (!tickRateManager.isFrozen()) {
            tickRateManager.setFrozen(true);
            freezeOwned = tickRateManager.isFrozen();
        }

        VsPhysicsPauseController.beginSession();
        active = true;
        VsPhysicsPauseController.tick(server);
        LOG.log(Level.INFO,
                "Entered idle pregen mode; freezeOwned=" + freezeOwned
                        + ", vsAsyncPauseOwned=" + VsPhysicsPauseController.ownsAsyncPause());
    }

    private static void observeFreezeOwnership(MinecraftServer server) {
        if (freezeOwned && !server.tickRateManager().isFrozen()) {
            freezeOwned = false;
            LOG.log(Level.INFO,
                    "Server freeze was changed externally while idle pregen was active; ownership released");
        }
    }

    private static void leave(MinecraftServer server, String reason) {
        active = false;

        if (freezeOwned) {
            ServerTickRateManager tickRateManager = server.tickRateManager();
            if (tickRateManager.isFrozen()) {
                tickRateManager.setFrozen(false);
            }
            freezeOwned = false;
        }

        VsPhysicsPauseController.leaveSession();
        LOG.log(Level.INFO, "Left idle pregen mode: " + reason);
    }
}
