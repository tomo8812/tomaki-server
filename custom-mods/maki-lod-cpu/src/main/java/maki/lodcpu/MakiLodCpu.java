package maki.lodcpu;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("maki_lod_cpu")
public final class MakiLodCpu {
    private static final Logger LOG = LoggerFactory.getLogger("MakiLodCpu");
    public MakiLodCpu(){
        LOG.info("Maki LOD CPU initialized; follow ownership, CPU governor and task migration commands are enabled.");
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerAboutToStartEvent e)->UnifiedPregenReset.prepareBeforeLod(e.getServer()));
        NeoForge.EVENT_BUS.addListener(this::tick);
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent e)->{if(e.getEntity() instanceof net.minecraft.server.level.ServerPlayer p)PlayerCenter.login(p);});
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent e)->{if(e.getEntity() instanceof net.minecraft.server.level.ServerPlayer p)PlayerCenter.logout(p);});
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppingEvent e)->{
            PlayerCenter.shutdown(e.getServer());
            UnifiedPregenReset.stopped(e.getServer());
            LOG.info("Maki LOD CPU stopped; authoritative normal-world data remains untouched and unified pregen state stays resumable.");
        });
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.RegisterCommandsEvent e)->PlayerCenter.register(e.getDispatcher()));
    }
    private void tick(ServerTickEvent.Post event){
        var server=event.getServer();
        CpuGovernor.tick(server.getAverageTickTimeNanos()/1_000_000.0,server.getPlayerCount());
        PlayerCenter.tick(server);
    }
}
