package maki.chunksmithfixes;

import net.neoforged.fml.common.Mod;

@Mod("maki_chunksmith_fixes")
public final class MakiChunksmithFixes {
    public MakiChunksmithFixes() {
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStartingEvent e)->LodUpdateQueue.start(e.getServer()));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.tick.ServerTickEvent.Post e)->LodUpdateQueue.tick(e.getServer()));
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.addListener((net.neoforged.neoforge.event.server.ServerStoppingEvent e)->LodUpdateQueue.stop());
    }
}
