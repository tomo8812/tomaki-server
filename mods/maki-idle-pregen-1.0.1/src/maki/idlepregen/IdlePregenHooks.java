package maki.idlepregen;

import net.minecraft.server.MinecraftServer;

public final class IdlePregenHooks {
   private IdlePregenHooks() {
   }

   public static void tick(MinecraftServer var0) {
      IdlePregenController.tick(var0);
   }

   public static void shutdown(MinecraftServer var0) {
      IdlePregenController.shutdown(var0);
   }
}
