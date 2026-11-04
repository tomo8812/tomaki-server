package maki.lodcpu;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;

@Mod("maki_lod_cpu")
public final class MakiLodCpu {
   public MakiLodCpu() {
      NeoForge.EVENT_BUS.addListener(this::tick);
      NeoForge.EVENT_BUS.addListener(var0 -> {
         if (var0.getEntity() instanceof ServerPlayer var1) {
            PlayerCenter.login(var1);
         }
      });
      NeoForge.EVENT_BUS.addListener(var0 -> {
         if (var0.getEntity() instanceof ServerPlayer var1) {
            PlayerCenter.logout(var1);
         }
      });
      NeoForge.EVENT_BUS.addListener(var0 -> PlayerCenter.shutdown(var0.getServer()));
      NeoForge.EVENT_BUS.addListener(var0 -> PlayerCenter.register(var0.getDispatcher()));
   }

   private void tick(Post var1) {
      MinecraftServer var2 = var1.getServer();
      CpuGovernor.tick(var2.getAverageTickTimeNanos() / 1000000.0, var2.getPlayerCount());
      PlayerCenter.tick(var2);
   }
}
