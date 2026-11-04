package org.embeddedt.modernfix.neoforge.init;

import com.google.common.collect.ImmutableList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.ModLoadingIssue;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.core.ModernFixMixinPlugin;
import org.embeddedt.modernfix.duck.suspend_integrated_server_during_load.IDeferrableIntegratedServer;
import org.embeddedt.modernfix.neoforge.ModernFixConfig;
import org.embeddedt.modernfix.neoforge.packet.ClientLoadFinishedPayload;
import org.embeddedt.modernfix.neoforge.packet.SmartIngredientSyncPayload;

@Mod("modernfix")
public class ModernFixForge {
   private static ModernFix commonMod;
   public static boolean launchDone = false;
   public static boolean registryEventsFired = false;
   private static final List<Pair<List<String>, String>> MOD_WARNINGS = ImmutableList.of(Pair.of(ImmutableList.of("ferritecore"), "modernfix.no_ferritecore"));

   public ModernFixForge(ModContainer modContainer, IEventBus modBus) {
      commonMod = new ModernFix();
      NeoForge.EVENT_BUS.register(this);
      modBus.addListener(this::commonSetup);
      modBus.addListener(this::registerItems);
      modBus.addListener(this::registerNetworkChannel);
      if (FMLEnvironment.dist == Dist.CLIENT) {
         NeoForge.EVENT_BUS.register(new ModernFixClientForge(modContainer, modBus));
      }

      modContainer.registerConfig(Type.COMMON, ModernFixConfig.COMMON_CONFIG);
   }

   private void registerItems(RegisterEvent event) {
      if (Boolean.getBoolean("modernfix.largeRegistryTest")) {
         event.register(Registries.ITEM, helper -> {
            Properties props = new Properties();

            for (int i = 0; i < 1000000; i++) {
               helper.register(ResourceLocation.fromNamespaceAndPath("modernfix", "item_" + i), new Item(props));
            }
         });
      }
   }

   public void commonSetup(FMLCommonSetupEvent event) {
      if (ModernFixMixinPlugin.instance.isOptionEnabled("feature.warn_missing_perf_mods.Warnings")) {
         event.enqueueWork(() -> {
            boolean atLeastOneWarning = false;

            for (Pair<List<String>, String> warning : MOD_WARNINGS) {
               boolean isPresent = !FMLLoader.isProduction() || ((List)warning.getLeft()).stream().anyMatch(name -> ModList.get().isLoaded(name));
               if (!isPresent) {
                  atLeastOneWarning = true;
                  ModLoader.addLoadingIssue(ModLoadingIssue.warning((String)warning.getRight(), new Object[0]));
               }
            }

            if (atLeastOneWarning) {
               ModLoader.addLoadingIssue(ModLoadingIssue.warning("modernfix.perf_mod_warning", new Object[0]));
            }
         });
      }

      event.enqueueWork(ModernFix::runAuditIfRequested);
   }

   private void registerNetworkChannel(RegisterPayloadHandlersEvent event) {
      PayloadRegistrar registrar = event.registrar("1").executesOn(HandlerThread.MAIN).optional();
      if (ModernFixMixinPlugin.instance.isOptionEnabled("perf.smart_ingredient_sync.Channel")) {
         registrar.playToClient(SmartIngredientSyncPayload.TYPE, SmartIngredientSyncPayload.STREAM_CODEC, (payload, ctx) -> {});
      }

      registrar.playToClient(ClientLoadFinishedPayload.TYPE, ClientLoadFinishedPayload.STREAM_CODEC, (payload, ctx) -> ctx.enqueueWork(() -> {
         Minecraft mc = Minecraft.getInstance();
         if (mc.hasSingleplayerServer()) {
            ((IDeferrableIntegratedServer)mc.getSingleplayerServer()).mfix$markClientLoadFinished();
         }
      }));
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onServerDead(ServerStoppedEvent event) {
      commonMod.onServerDead(event.getServer());
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onServerStarted(ServerStartedEvent event) {
      commonMod.onServerStarted();
   }
}
