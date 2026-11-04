package net.satisfy.candlelight.forge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "candlelight", dist = Dist.CLIENT)
public class CandlelightNeoForgeClientMod {
   public CandlelightNeoForgeClientMod(ModContainer container) {
      container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
   }
}
