package maki.mapshare;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

@Mod("maki_diorama_share")
public final class SharingMod {
   public static final ModConfigSpec SPEC;
   public static final IntValue HISTORY;

   public SharingMod(IEventBus var1, ModContainer var2) {
      var2.registerConfig(Type.CLIENT, SPEC, "maki-diorama-share-client.toml");
      var1.addListener(ShareNetwork::register);
      ServerSharing.register();
   }

   static {
      Builder var0 = new Builder();
      HISTORY = var0.comment("Deaths displayed per player, for this viewer only. The server keeps up to 1000 per player regardless of this value.")
         .defineInRange("deathHistoryCount", 1, 1, 1000);
      SPEC = var0.build();
   }
}
