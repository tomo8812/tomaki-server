package maki.idlepregen;

import net.minecraft.server.MinecraftServer;

public final class IdlePregenHooks {
    private IdlePregenHooks() {
    }

    public static void tick(MinecraftServer server) {
        IdlePregenController.tick(server);
    }

    public static void shutdown(MinecraftServer server) {
        IdlePregenController.shutdown(server);
    }
}
