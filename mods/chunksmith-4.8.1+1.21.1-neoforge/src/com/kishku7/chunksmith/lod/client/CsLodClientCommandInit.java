package com.kishku7.chunksmith.lod.client;

import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

@EventBusSubscriber(modid = "chunksmith", value = Dist.CLIENT)
public final class CsLodClientCommandInit {
   private CsLodClientCommandInit() {
   }

   @SubscribeEvent
   public static void onRegisterClientCommands(RegisterClientCommandsEvent event) {
      event.getDispatcher().register(CsLodClientCommand.build("csclient", (source, line) -> source.sendSuccess(() -> Component.literal(line), false)));
   }
}
