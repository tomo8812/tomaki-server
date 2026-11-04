package com.archiangel.quadhotbar.client;

import com.archiangel.quadhotbar.QuadHotbarOverviewMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = "quadhotbar", dist = Dist.CLIENT)
public final class QuadHotbarClient {
   public QuadHotbarClient(IEventBus modEventBus, ModContainer modContainer) {
      modEventBus.addListener(QuadHotbarKeyMappings::register);
      modEventBus.addListener(event -> event.register((MenuType)QuadHotbarOverviewMenu.TYPE.get(), QuadHotbarOverviewScreen::new));
      modContainer.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory)(container, parent) -> new QuadHotbarConfigScreen(parent));
   }
}
