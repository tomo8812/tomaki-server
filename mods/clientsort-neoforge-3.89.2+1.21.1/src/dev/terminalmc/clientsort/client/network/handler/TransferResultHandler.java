package dev.terminalmc.clientsort.client.network.handler;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.network.handler.util.ResultHandlerUtil;
import dev.terminalmc.clientsort.network.handler.validate.PayloadResult;
import dev.terminalmc.clientsort.network.payload.TransferPayload;
import dev.terminalmc.clientsort.network.payload.TransferResultPayload;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.Nullable;

public class TransferResultHandler {
   @Nullable
   public static Consumer<PayloadResult> onCompletion;

   private TransferResultHandler() {
   }

   public static void handle(TransferResultPayload payload, Minecraft mc, LocalPlayer player) {
      mc.execute(() -> {
         PayloadResult result = ResultHandlerUtil.interpretResult(payload.result(), payload.message(), TransferPayload.ID);
         if (onCompletion != null) {
            try {
               onCompletion.accept(result);
            } catch (Exception e) {
               ClientSort.LOG.error("Failed to run completion callback for payload '{}' with result '{}': {}", TransferPayload.ID, result.name(), e);
            }

            onCompletion = null;
         }
      });
   }
}
