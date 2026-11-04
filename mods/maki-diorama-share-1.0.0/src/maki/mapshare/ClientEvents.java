package maki.mapshare;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;

@EventBusSubscriber(modid = "maki_diorama_share", value = Dist.CLIENT)
public final class ClientEvents {
   @SubscribeEvent
   public static void tick(Post var0) {
      ClientSharing.tick();
   }
}
