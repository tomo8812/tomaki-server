package com.kishku7.chunksmith.worldenter.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;

@EventBusSubscriber(modid = "chunksmith", value = Dist.CLIENT)
public final class WorldEnterClientInit {
   private WorldEnterClientInit() {
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      WorldEnterClientHook.tick(Minecraft.getInstance());
   }
}
