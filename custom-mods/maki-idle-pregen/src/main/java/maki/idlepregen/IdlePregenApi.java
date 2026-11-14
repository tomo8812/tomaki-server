package maki.idlepregen;

public final class IdlePregenApi {
    private IdlePregenApi() {}
    public static boolean isActive() { return IdlePregenController.isActive(); }
}
