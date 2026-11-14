package maki.chunksmithcontroller;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("maki_chunksmith_controller")
public final class MakiChunksmithController {
    private static final Logger LOG = LoggerFactory.getLogger("MakiChunksmithController");
    private static final ControlledWorkQueue WORK=new ControlledWorkQueue();
    public static int activeWork(){return WORK.running();}
    public MakiChunksmithController(){
        LOG.info("Maki Chunksmith Controller initialized: generation resource control and completion monitoring enabled.");
        NativeLodTelemetry.observeWrites();
        NeoForge.EVENT_BUS.addListener((maki.chunksmithfixes.GenerationWorkEvent e)->WORK.submit(e));
        NeoForge.EVENT_BUS.addListener(this::tick);
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppedEvent e)->NativeLodTelemetry.clear());
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent e)->{if(e.getEntity() instanceof net.minecraft.server.level.ServerPlayer p)PlayerCenter.login(p);});
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent e)->{if(e.getEntity() instanceof net.minecraft.server.level.ServerPlayer p)PlayerCenter.logout(p);});
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppingEvent e)->{
            WORK.clear();PlayerCenter.shutdown(e.getServer());
            LOG.info("Maki Chunksmith Controller stopped; authoritative normal-world data remains untouched and native pregen state stays resumable.");
        });
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.RegisterCommandsEvent e)->PlayerCenter.register(e.getDispatcher()));
    }
    private void tick(ServerTickEvent.Post event){
        var server=event.getServer();
        CpuGovernor.tick(server.getAverageTickTimeNanos()/1_000_000.0,server.getPlayerCount());
        PlayerCenter.tick(server);
        int slots=Math.max(0,CpuGovernor.cap()-PregenFlowController.fullOutstanding());
        WORK.tick(slots,CpuGovernor.permitsWork(server.getAverageTickTimeNanos()/1_000_000.0)&&!PlayerCenter.diskBlocked());
    }
}
