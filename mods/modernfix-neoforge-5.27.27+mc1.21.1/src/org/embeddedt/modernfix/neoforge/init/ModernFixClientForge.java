package org.embeddedt.modernfix.neoforge.init;

import com.mojang.blaze3d.platform.InputConstants;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Pre;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent.DebugText;
import net.neoforged.neoforge.client.event.RenderFrameEvent.Post;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.level.LevelEvent.Unload;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.embeddedt.modernfix.ModernFixClient;
import org.embeddedt.modernfix.screen.ModernFixConfigScreen;

public class ModernFixClientForge {
   private static ModernFixClient commonMod;
   private KeyMapping configKey;
   private static final List<String> brandingList = new ArrayList<>();

   public ModernFixClientForge(ModContainer modContainer, IEventBus modBus) {
      commonMod = new ModernFixClient();
      modBus.addListener(this::keyBindRegister);
      modBus.addListener(this::onClientSetup);
      modContainer.registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory)(mc, screen) -> new ModernFixConfigScreen(screen));
   }

   private void keyBindRegister(RegisterKeyMappingsEvent event) {
      this.configKey = new KeyMapping("key.modernfix.config", KeyConflictContext.UNIVERSAL, InputConstants.UNKNOWN, "key.modernfix");
      event.register(this.configKey);
   }

   private void onClientSetup(FMLClientSetupEvent event) {
   }

   @SubscribeEvent
   public void onConfigKey(Pre event) {
      if (this.configKey != null && this.configKey.consumeClick()) {
         Minecraft.getInstance().setScreen(new ModernFixConfigScreen(Minecraft.getInstance().screen));
      }
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   public void onRenderOverlay(DebugText event) {
      if (commonMod.brandingString != null && Minecraft.getInstance().getDebugOverlay().showDebugScreen()) {
         if (brandingList.size() == 0) {
            brandingList.add("");
            brandingList.add(commonMod.brandingString);
         }

         int targetIdx = 0;
         int numSeenBlanks = 0;

         List<String> right;
         for (right = event.getRight(); targetIdx < right.size(); targetIdx++) {
            String s = right.get(targetIdx);
            if (s == null || s.length() == 0) {
               numSeenBlanks++;
            }

            if (numSeenBlanks == 3) {
               break;
            }
         }

         right.addAll(targetIdx, brandingList);
      }
   }

   @SubscribeEvent
   public void onDisconnect(Unload event) {
      if (event.getLevel().isClientSide()) {
         DebugScreenOverlay overlay = Minecraft.getInstance().getDebugOverlay();
         Minecraft.getInstance().tell(overlay::clearChunkCache);
      }
   }

   @SubscribeEvent
   public void onServerStarting(ServerStartedEvent event) {
      commonMod.onServerStarted(event.getServer());
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onRenderTickEnd(Post event) {
      commonMod.onRenderTickEnd();
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onRecipes(RecipesUpdatedEvent e) {
      commonMod.onRecipesUpdated();
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onTags(TagsUpdatedEvent e) {
      commonMod.onTagsUpdated();
   }

   // $VF: synthetic method
   private static void lambda$onClientSetup$1() {
      ModLoader.addLoadingIssue(ModLoadingIssue.warning("modernfix.connectedness_dynresoruces", new Object[0]));
   }
}
