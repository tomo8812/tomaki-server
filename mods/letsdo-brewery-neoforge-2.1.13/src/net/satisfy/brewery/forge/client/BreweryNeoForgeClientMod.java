package net.satisfy.brewery.forge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "brewery", dist = Dist.CLIENT)
public class BreweryNeoForgeClientMod {
   public BreweryNeoForgeClientMod(ModContainer container) {
      container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
   }
}
