package dev.terminalmc.clientsort.client.network.handler;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.network.handler.util.ResultHandlerUtil;
import dev.terminalmc.clientsort.network.handler.validate.PayloadResult;
import dev.terminalmc.clientsort.network.payload.StackFillPayload;
import dev.terminalmc.clientsort.network.payload.StackFillResultPayload;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

public class StackFillResultHandler {
   @Nullable
   public static Consumer<PayloadResult> onCompletion;

   private StackFillResultHandler() {
   }

   public static void handle(StackFillResultPayload payload, Minecraft mc, LocalPlayer player) {
      mc.execute(() -> {
         PayloadResult result = ResultHandlerUtil.interpretResult(payload.result(), payload.message(), StackFillPayload.ID);
         if (onCompletion != null) {
            try {
               onCompletion.accept(result);
            } catch (Exception e) {
               ClientSort.LOG.error("Failed to run completion callback for payload '{}' with result '{}': {}", StackFillPayload.ID, result.name(), e);
            }

            onCompletion = null;
         }
      });
   }
}
