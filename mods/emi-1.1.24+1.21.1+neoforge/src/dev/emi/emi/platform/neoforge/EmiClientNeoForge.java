package dev.emi.emi.platform.neoforge;

import dev.emi.emi.EmiPort;
import dev.emi.emi.data.EmiData;
import dev.emi.emi.network.EmiNetwork;
import dev.emi.emi.platform.EmiClient;
import dev.emi.emi.registry.EmiTags;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiReloadManager;
import dev.emi.emi.screen.ConfigScreen;
import dev.emi.emi.screen.EmiScreenBase;
import dev.emi.emi.screen.EmiScreenManager;
import dev.emi.emi.screen.StackBatcher;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent.Render.Foreground;
import net.neoforged.neoforge.client.event.ModelEvent.RegisterAdditional;
import net.neoforged.neoforge.client.event.ScreenEvent.Render.Post;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = "emi", bus = Bus.MOD, value = Dist.CLIENT)
public class EmiClientNeoForge {
   @SubscribeEvent
   public static void clientInit(FMLClientSetupEvent event) {
      StackBatcher.EXTRA_RENDER_LAYERS.addAll(Arrays.stream(NeoForgeRenderTypes.values()).map(f -> f.get()).toList());
      EmiClient.init();
      EmiNetwork.initClient(packet -> PacketDistributor.sendToServer(EmiPacketHandler.wrap(packet), new CustomPacketPayload[0]));
      NeoForge.EVENT_BUS.addListener(EmiClientNeoForge::recipesReloaded);
      NeoForge.EVENT_BUS.addListener(EmiClientNeoForge::tagsReloaded);
      NeoForge.EVENT_BUS.addListener(EmiClientNeoForge::renderScreenForeground);
      NeoForge.EVENT_BUS.addListener(EmiClientNeoForge::postRenderScreen);
      ((ModContainer)ModList.get().getModContainerById("emi").orElseThrow())
         .registerExtensionPoint(IConfigScreenFactory.class, (IConfigScreenFactory)(container, last) -> new ConfigScreen(last));
   }

   @SubscribeEvent
   public static void registerAdditionalModels(RegisterAdditional event) {
      Minecraft client = Minecraft.getInstance();
      EmiTags.registerTagModels(client.getResourceManager(), event::register, "standalone");
   }

   @SubscribeEvent
   public static void registerResourceReloaders(RegisterClientReloadListenersEvent event) {
      EmiData.init(reloader -> event.registerReloadListener(reloader));
   }

   public static void recipesReloaded(RecipesUpdatedEvent event) {
      EmiReloadManager.reloadRecipes();
   }

   public static void tagsReloaded(TagsUpdatedEvent event) {
      EmiReloadManager.reloadTags();
   }

   public static void renderScreenForeground(Foreground event) {
      EmiDrawContext context = EmiDrawContext.wrap(event.getGuiGraphics());
      AbstractContainerScreen<?> screen = event.getContainerScreen();
      EmiScreenBase base = EmiScreenBase.of(screen);
      if (base != null) {
         Minecraft client = Minecraft.getInstance();
         context.push();
         context.matrices().translate(-screen.getGuiLeft(), -screen.getGuiTop(), 0.0);
         EmiPort.setPositionTexShader();
         EmiScreenManager.render(context, event.getMouseX(), event.getMouseY(), client.getTimer().getGameTimeDeltaPartialTick(false));
         context.pop();
      }
   }

   public static void postRenderScreen(Post event) {
      EmiDrawContext context = EmiDrawContext.wrap(event.getGuiGraphics());
      Screen screen = event.getScreen();
      if (screen instanceof AbstractContainerScreen) {
         EmiScreenBase base = EmiScreenBase.of(screen);
         if (base != null) {
            Minecraft client = Minecraft.getInstance();
            context.push();
            EmiPort.setPositionTexShader();
            EmiScreenManager.drawForeground(context, event.getMouseX(), event.getMouseY(), client.getTimer().getGameTimeDeltaPartialTick(false));
            context.pop();
         }
      }
   }
}
