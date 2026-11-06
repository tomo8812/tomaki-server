package maki.lodcpu;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("maki_lod_cpu")
public final class MakiLodCpu {
   private static final Logger LOG = LoggerFactory.getLogger("MakiLodCpu");

   public MakiLodCpu() {
      LOG.info("Maki LOD CPU initialized; follow ownership, CPU governor and task migration commands are enabled.");
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
         LOG.info("Maki LOD CPU stopped; authoritative Chunksmith task progress and migration backups remain in their durable stores.");
      });
      NeoForge.EVENT_BUS.addListener(e -> PlayerCenter.register(e.getDispatcher()));
   }

   private void tick(Post event) {
      MinecraftServer server = event.getServer();
      CpuGovernor.tick(server.getAverageTickTimeNanos() / 1000000.0, server.getPlayerCount());
      PlayerCenter.tick(server);
   }
}
