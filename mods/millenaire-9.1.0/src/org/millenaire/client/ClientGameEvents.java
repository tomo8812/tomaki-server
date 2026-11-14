package org.millenaire.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.event.CustomizeGuiOverlayEvent.DebugText;
import org.millenaire.block.mock.MockPayloadBlockEntity;

@EventBusSubscriber(modid = "millenaire", value = Dist.CLIENT)
public final class ClientGameEvents {
   private ClientGameEvents() {
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      if (Minecraft.getInstance().player == null) {
         FireplaceSmokeHandler.clearAll();
      } else {
         FireplaceSmokeHandler.tick();
      }
   }

   @SubscribeEvent
   public static void onDebugText(DebugText event) {
      Minecraft mc = Minecraft.getInstance();
      Entity camera = mc.getCameraEntity();
      if (camera != null && mc.level != null && !mc.showOnlyReducedInfo()) {
         if (camera.pick(20.0, 0.0F, false) instanceof BlockHitResult hit
            && hit.getType() == Type.BLOCK
            && mc.level.getBlockEntity(hit.getBlockPos()) instanceof MockPayloadBlockEntity be) {
            String var7 = be.getPayload();
            event.getRight().add("");
            event.getRight().add(ChatFormatting.UNDERLINE + "Millénaire marker payload");
            event.getRight().add(var7.isEmpty() ? "<not set>" : var7);
         }
      }
   }
}
