package maki.lodcpu;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod("maki_lod_cpu")
public final class MakiLodCpu {
    public MakiLodCpu(){
        NeoForge.EVENT_BUS.addListener(this::tick);
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent e)->{if(e.getEntity() instanceof net.minecraft.server.level.ServerPlayer p)PlayerCenter.login(p);});
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent e)->{if(e.getEntity() instanceof net.minecraft.server.level.ServerPlayer p)PlayerCenter.logout(p);});
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppingEvent e)->PlayerCenter.shutdown(e.getServer()));
        NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.RegisterCommandsEvent e)->PlayerCenter.register(e.getDispatcher()));
    }
    private void tick(ServerTickEvent.Post event){
        var server=event.getServer();
        CpuGovernor.tick(server.getAverageTickTimeNanos()/1_000_000.0,server.getPlayerCount());
        PlayerCenter.tick(server);
    }
}
