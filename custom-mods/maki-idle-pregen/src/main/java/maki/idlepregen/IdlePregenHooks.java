package maki.idlepregen;

import net.minecraft.server.MinecraftServer;

public final class IdlePregenHooks {
    private static final ThreadLocal<Boolean> VS_IDLE_PHYSICS_STEP = new ThreadLocal<>();

    private IdlePregenHooks() {
    }

    public static void beginServerTick(MinecraftServer server) {
        MillenaireInitGuard.beginServerTick(server);
    }

    public static void tick(MinecraftServer server) {
        IdlePregenController.tick(server);
    }

    public static void shutdown(MinecraftServer server) {
        MillenaireInitGuard.shutdown(server);
        IdlePregenController.shutdown(server);
    }

    public static double adjustVsPhysicsDelta(Object pipeline, double deltaSeconds) {
        // FQ.a(double) is the outer boundary of one VS physics pass. Reset any
        // stale marker first, then publish a marker only for the current pass.
        VS_IDLE_PHYSICS_STEP.remove();
        if (!IdlePregenController.isActive()) {
            return deltaSeconds;
        }

        double adjusted = VsPhysicsPauseController.adjustPhysicsDelta(pipeline, deltaSeconds);
        if (adjusted == 0.0D) {
            VS_IDLE_PHYSICS_STEP.set(Boolean.TRUE);
        }
        return adjusted;
    }

    public static boolean isVsIdlePhysicsStep() {
        return Boolean.TRUE.equals(VS_IDLE_PHYSICS_STEP.get());
    }

    public static void afterVsGameFrame(Object pipeline) {
        if (IdlePregenController.isActive()) {
            VsPhysicsPauseController.afterGameFrame(pipeline);
        }
    }

    public static void completeVsPhysicsStep(Object pipeline) {
        try {
            VsPhysicsPauseController.completePhysicsStep(pipeline);
        } finally {
            VS_IDLE_PHYSICS_STEP.remove();
        }
    }
}
