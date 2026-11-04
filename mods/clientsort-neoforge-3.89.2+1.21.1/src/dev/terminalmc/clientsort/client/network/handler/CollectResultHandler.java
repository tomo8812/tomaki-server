package dev.terminalmc.clientsort.client.network.handler;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.network.handler.util.ResultHandlerUtil;
import dev.terminalmc.clientsort.network.handler.validate.PayloadResult;
import dev.terminalmc.clientsort.network.payload.CollectPayload;
import dev.terminalmc.clientsort.network.payload.CollectResultPayload;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class CollectResultHandler {
   public static final Map<String, Consumer<PayloadResult>> onCompletion = new HashMap<>();

   private CollectResultHandler() {
   }

   public static void handle(CollectResultPayload payload, Minecraft mc, LocalPlayer player) {
      mc.execute(() -> {
         PayloadResult result = ResultHandlerUtil.interpretResult(payload.result(), payload.message(), CollectPayload.ID);
         Consumer<PayloadResult> callback = onCompletion.get(payload.id());
         onCompletion.remove(payload.id());
         if (callback != null) {
            try {
               callback.accept(result);
            } catch (Exception e) {
               ClientSort.LOG.error("Failed to run completion callback for payload '{}' with result '{}': {}", CollectPayload.ID, result.name(), e);
            }
         }
      });
   }
}
