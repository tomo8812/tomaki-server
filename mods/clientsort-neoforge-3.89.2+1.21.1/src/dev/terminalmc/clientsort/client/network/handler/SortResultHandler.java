package dev.terminalmc.clientsort.client.network.handler;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.network.handler.util.ResultHandlerUtil;
import dev.terminalmc.clientsort.network.handler.validate.PayloadResult;
import dev.terminalmc.clientsort.network.payload.SortPayload;
import dev.terminalmc.clientsort.network.payload.SortResultPayload;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

public class SortResultHandler {
   @Nullable
   public static Consumer<PayloadResult> onCompletion;

   private SortResultHandler() {
   }

   public static void handle(SortResultPayload payload, Minecraft mc, LocalPlayer player) {
      mc.execute(() -> {
         PayloadResult result = ResultHandlerUtil.interpretResult(payload.result(), payload.message(), SortPayload.ID);
         if (onCompletion != null) {
            try {
               onCompletion.accept(result);
            } catch (Exception e) {
               ClientSort.LOG.error("Failed to run completion callback for payload '{}' with result '{}': {}", SortPayload.ID, result.name(), e);
            }

            onCompletion = null;
         }
      });
   }
}
