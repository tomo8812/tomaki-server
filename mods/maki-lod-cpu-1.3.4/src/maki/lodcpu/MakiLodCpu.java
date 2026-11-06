package maki.lodcpu;

import maki.statusio.StatusIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;

@Mod("maki_lod_cpu")
public final class MakiLodCpu {
   public MakiLodCpu() {
      NeoForge.EVENT_BUS.addListener(this::tick);
      NeoForge.EVENT_BUS.addListener(e -> {
         if (e.getEntity() instanceof ServerPlayer p) {
            PlayerCenter.login(p);
         }
      });
      NeoForge.EVENT_BUS.addListener(e -> {
         if (e.getEntity() instanceof ServerPlayer p) {
            PlayerCenter.logout(p);
         }
      });
      NeoForge.EVENT_BUS.addListener(e -> {
         PlayerCenter.shutdown(e.getServer());
         StatusIo.flush(2000L);
      });
      NeoForge.EVENT_BUS.addListener(e -> PlayerCenter.register(e.getDispatcher()));
   }

   private void tick(Post event) {
      MinecraftServer server = event.getServer();
      CpuGovernor.tick(server.getAverageTickTimeNanos() / 1000000.0, server.getPlayerCount());
      PlayerCenter.tick(server);
   }
}
