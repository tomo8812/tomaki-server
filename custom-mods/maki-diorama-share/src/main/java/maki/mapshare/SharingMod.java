package maki.mapshare;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod("maki_diorama_share")
public final class SharingMod {
    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.IntValue HISTORY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        HISTORY = builder
            .comment("Deaths displayed per player, for this viewer only. The server keeps up to 1000 per player regardless of this value.")
            .defineInRange("deathHistoryCount", 1, 1, 1000);
        SPEC = builder.build();
    }

    public SharingMod(IEventBus modBus, ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, SPEC, "maki-diorama-share-client.toml");
        modBus.addListener(ShareNetwork::register);
        ServerSharing.register();
    }
}
