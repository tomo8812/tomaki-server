package net.satisfy.bakery.neoforge.core.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.forge.REIPluginClient;
import net.satisfy.bakery.core.compat.rei.BakeryREIClientPlugin;

@REIPluginClient
public class BakeryREIClientPluginForge implements REIClientPlugin {
   public void registerCategories(CategoryRegistry registry) {
      BakeryREIClientPlugin.registerCategories(registry);
   }

   public void registerDisplays(DisplayRegistry registry) {
      BakeryREIClientPlugin.registerDisplays(registry);
   }

   public void registerEntries(EntryRegistry registry) {
      BakeryREIClientPlugin.registerEntries(registry);
   }
}
