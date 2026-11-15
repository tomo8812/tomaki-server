package maki.chunksmithcontroller;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("maki_chunksmith_controller")
public final class MakiChunksmithController {
   private static final Logger LOG = LoggerFactory.getLogger("MakiChunksmithController");
   private static final ControlledWorkQueue WORK = new ControlledWorkQueue();

   public static int activeWork() {
      return WORK.running();
   }

   public MakiChunksmithController() {
      LOG.info("Maki Chunksmith Controller initialized: generation resource control and completion monitoring enabled.");
      NativeLodTelemetry.observeWrites();
      NeoForge.EVENT_BUS.addListener(e -> WORK.submit(e));
      NeoForge.EVENT_BUS.addListener(this::tick);
      NeoForge.EVENT_BUS.addListener(e -> NativeLodTelemetry.clear());
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
         WORK.clear();
         PlayerCenter.shutdown(e.getServer());
         LOG.info("Maki Chunksmith Controller stopped; authoritative normal-world data remains untouched and native pregen state stays resumable.");
      });
      NeoForge.EVENT_BUS.addListener(e -> PlayerCenter.register(e.getDispatcher()));
   }

   private void tick(Post event) {
      MinecraftServer server = event.getServer();
      CpuGovernor.tick(server.getAverageTickTimeNanos() / 1000000.0, server.getPlayerCount());
      PlayerCenter.tick(server);
      int slots = Math.max(0, CpuGovernor.cap() - PregenFlowController.fullOutstanding());
      WORK.tick(slots, CpuGovernor.permitsWork(server.getAverageTickTimeNanos() / 1000000.0) && !PlayerCenter.diskBlocked());
   }
}
