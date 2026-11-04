package dev.terminalmc.clientsort.client.network.handler.util;

import dev.terminalmc.clientsort.client.ClientSort;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.network.handler.validate.PayloadResult;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public class ResultHandlerUtil {
   private ResultHandlerUtil() {
   }

   public static PayloadResult interpretResult(int code, String message, ResourceLocation payloadId) {
      PayloadResult result = PayloadResult.get(code);
      if (result.isSuccess()) {
         if (ClientSort.debug()) {
            ClientSort.LOG.info("Received success result for payload '{}'", payloadId);
         }
      } else {
         ClientSort.LOG.warn("Received failure result with code {} ({}) for payload '{}': {}", result.code, result.name(), payloadId, message);
         switch (result) {
            case INCONSISTENT_STATE:
            case UNSUPPORTED_OP:
               if (Config.options().useClientFallback) {
                  ClientSort.LOG.info("Client fallback is enabled: retrying without server acceleration.");
               } else if (Minecraft.getInstance().getSingleplayerServer() != null) {
                  ClientSort.LOG
                     .info(
                        "If you still want to perform this operation, you may either edit the server-side policy in the '{}' config file, disable server acceleration, or enable client fallback. Otherwise, you may edit your client-side policy to disable this operation in this inventory type.",
                        "clientsort-server.json"
                     );
               } else {
                  ClientSort.LOG
                     .info(
                        "If you still want to perform this operation, you may disable server acceleration or enable client fallback. Otherwise, you may edit your client-side policy to disable this operation in this inventory type."
                     );
               }
               break;
            case INVALID_DATA:
            case FAILURE:
               ClientSort.LOG.warn("Please make a note of the inventory type and report this to the developer.");
               break;
            case UNKNOWN:
               ClientSort.LOG.error("Result code {} is not recognized. Are you using the same mod version as the server?", code);
         }
      }

      return result;
   }
}
