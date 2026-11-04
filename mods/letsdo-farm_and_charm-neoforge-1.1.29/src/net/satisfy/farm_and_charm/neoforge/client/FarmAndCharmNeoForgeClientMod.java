package net.satisfy.farm_and_charm.neoforge.client;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "farm_and_charm", dist = Dist.CLIENT)
public class FarmAndCharmNeoForgeClientMod {
   public FarmAndCharmNeoForgeClientMod(ModContainer container) {
      container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
   }
}
