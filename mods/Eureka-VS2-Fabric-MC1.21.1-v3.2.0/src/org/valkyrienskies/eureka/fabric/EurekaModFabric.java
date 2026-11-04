package org.valkyrienskies.eureka.fabric;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.EndTick;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.class_1297;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_310;
import net.minecraft.class_5616;
import net.minecraft.class_746;
import net.minecraft.class_7923;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.eureka.EurekaBlockEntities;
import org.valkyrienskies.eureka.EurekaConfig;
import org.valkyrienskies.eureka.EurekaConfigLoader;
import org.valkyrienskies.eureka.EurekaItems;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.eureka.block.IWoodType;
import org.valkyrienskies.eureka.block.WoodType;
import org.valkyrienskies.eureka.blockentity.renderer.ShipHelmBlockEntityRenderer;
import org.valkyrienskies.eureka.blockentity.renderer.WheelModels;
import org.valkyrienskies.eureka.client.EurekaSpeedHud;
import org.valkyrienskies.eureka.command.EurekaAssemblerCommand;
import org.valkyrienskies.eureka.command.ShipWeightCommand;
import org.valkyrienskies.eureka.fabric.registry.FuelRegistryImpl;
import org.valkyrienskies.eureka.registry.CreativeTabs;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class EurekaModFabric implements ModInitializer {
   public void onInitialize() {
      new ValkyrienSkiesModFabric().onInitialize();
      new FuelRegistryImpl();
      EurekaMod.init();
      CommandRegistrationCallback.EVENT.register((CommandRegistrationCallback)(dispatcher, registryAccess, environment) -> {
         ShipWeightCommand.INSTANCE.register(dispatcher);
         EurekaAssemblerCommand.INSTANCE.register(dispatcher);
      });
   }

   @Environment(EnvType.CLIENT)
   public static class Client implements ClientModInitializer {
      private static final Logger DBG_LOGGER = LoggerFactory.getLogger("EurekaClientDbg");
      private static int dbgClientTickCounter = 0;
      private static int dbgLastVehicleId = -2;

      public void onInitializeClient() {
         EurekaMod.initClient();
         class_5616.method_32144(EurekaBlockEntities.INSTANCE.getSHIP_HELM().get(), ShipHelmBlockEntityRenderer::new);
         HudRenderCallback.EVENT.register((HudRenderCallback)(guiGraphics, deltaTracker) -> EurekaSpeedHud.INSTANCE.render(guiGraphics));
         ClientCommandRegistrationCallback.EVENT
            .register(
               (ClientCommandRegistrationCallback)(dispatcher, registryAccess) -> dispatcher.register(
                  (LiteralArgumentBuilder)ClientCommandManager.literal("vs")
                     .then(
                        ClientCommandManager.literal("cruise-cancel-debug")
                           .then(
                              ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                                 .executes(
                                    ctx -> {
                                       boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                       EurekaConfig.SERVER.setDebugCruiseCancel(enabled);
                                       EurekaConfigLoader.save();
                                       ((FabricClientCommandSource)ctx.getSource())
                                          .sendFeedback(class_2561.method_43470("Eureka cruise-cancel debug " + (enabled ? "enabled" : "disabled")));
                                       return 1;
                                    }
                                 )
                           )
                     )
               )
            );
         ClientTickEvents.END_CLIENT_TICK
            .register(
               (EndTick)mc -> {
                  class_746 player = mc.field_1724;
                  if (player != null) {
                     class_1297 vehicle = player.method_5854();
                     int curVehicleId = vehicle != null ? vehicle.method_5628() : -1;
                     if (curVehicleId != dbgLastVehicleId) {
                        DBG_LOGGER.info(
                           "[VS-TD-DBG-CLIENT] vehicle CHANGE from={} to={} ({}) isPassenger={} pos=({},{},{})",
                           new Object[]{
                              dbgLastVehicleId,
                              curVehicleId,
                              vehicle != null ? vehicle.getClass().getSimpleName() : "null",
                              player.method_5765(),
                              player.method_23317(),
                              player.method_23318(),
                              player.method_23321()
                           }
                        );
                        dbgLastVehicleId = curVehicleId;
                     }

                     dbgClientTickCounter++;
                     if (dbgClientTickCounter % 20 == 0) {
                        if (vehicle != null || player.method_5765()) {
                           DBG_LOGGER.info(
                              "[VS-TD-DBG-CLIENT] tick vehicle={} isPassenger={} pos=({},{},{}) input=(f={},s={},shift={})",
                              new Object[]{
                                 vehicle != null ? vehicle.getClass().getSimpleName() + "#" + vehicle.method_5628() : "null",
                                 player.method_5765(),
                                 player.method_23317(),
                                 player.method_23318(),
                                 player.method_23321(),
                                 player.field_3913 != null ? player.field_3913.field_3905 : 0.0F,
                                 player.field_3913 != null ? player.field_3913.field_3907 : 0.0F,
                                 player.method_5715()
                              }
                           );
                        }
                     }
                  }
               }
            );
         ModelLoadingPlugin.register(context -> {
            for (IWoodType woodType : WoodType.getEntries()) {
               context.addModels(new class_2960[]{class_2960.method_60655("vs_eureka", "block/" + woodType.method_15434().toLowerCase() + "_ship_helm_wheel")});
            }
         });
         WheelModels.INSTANCE
            .setModelGetter(
               woodType -> class_310.method_1551()
                  .method_1554()
                  .getModel(class_2960.method_60655("vs_eureka", "block/" + woodType.method_15434().toLowerCase() + "_ship_helm_wheel"))
            );
         class_2378.method_39197(class_7923.field_44687, EurekaItems.INSTANCE.getTAB(), CreativeTabs.INSTANCE.create());
         ModContainer eureka = (ModContainer)FabricLoader.getInstance()
            .getModContainer("vs_eureka")
            .orElseThrow(() -> new IllegalStateException("Eureka's ModContainer couldn't be found!"));
         class_2960 packId = class_2960.method_60655("vs_eureka", "retro_helms");
         ResourceManagerHelper.registerBuiltinResourcePack(packId, eureka, "Eureka retro helms", ResourcePackActivationType.NORMAL);
      }
   }

   public static class ModMenu implements ModMenuApi {
      public ConfigScreenFactory<?> getModConfigScreenFactory() {
         return parent -> null;
      }
   }
}
