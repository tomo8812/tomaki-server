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

    public static double adjustVsPhysicsDelta(Object pipeline, double deltaSeconds) {
        if (!IdlePregenController.isActive()) {
            return deltaSeconds;
        }
        return VsPhysicsPauseController.adjustPhysicsDelta(pipeline, deltaSeconds);
    }

    public static void afterVsGameFrame(Object pipeline) {
        if (IdlePregenController.isActive()) {
            VsPhysicsPauseController.afterGameFrame(pipeline);
        }
    }

    public static void completeVsPhysicsStep(Object pipeline) {
        VsPhysicsPauseController.completePhysicsStep(pipeline);
    }
}
