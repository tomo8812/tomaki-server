package dev.terminalmc.clientsort.network.handler;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.config.ServerConfig;
import dev.terminalmc.clientsort.exception.PayloadHandlerException;
import dev.terminalmc.clientsort.network.handler.validate.PayloadResult;
import dev.terminalmc.clientsort.platform.services.PlatformServices;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

public abstract class PayloadHandler {
   public static void processPayload(
      MinecraftServer server,
      ServerPlayer player,
      int containerId,
      PayloadHandler.ThrowingConsumer<AbstractContainerMenu> contextValidator,
      PayloadHandler.ThrowingConsumer<AbstractContainerMenu> schemaValidator,
      PayloadHandler.ThrowingConsumer<AbstractContainerMenu> operator,
      Type<?> payloadType,
      Type<?> responseType,
      BiFunction<PayloadResult, String, CustomPacketPayload> responseProvider
   ) {
      AbstractContainerMenu menu = null;
      PayloadResult result = PayloadResult.SUCCESS;
      String message = "";
      if (player.gameMode.getGameModeForPlayer().equals(GameType.SPECTATOR)) {
         result = PayloadResult.UNSUPPORTED_OP;
         message = "This operation is not available in spectator mode.";
         if (PlatformServices.getInstance().canSendToPlayer(player, responseType)) {
            PlatformServices.getInstance().sendToPlayer(player, responseProvider.apply(result, message));
         }
      } else {
         try {
            menu = getMenu(player, containerId);
            menu.suppressRemoteUpdates();
            contextValidator.accept(menu);
            schemaValidator.accept(menu);
            operator.accept(menu);
         } catch (Exception e) {
            if (e instanceof PayloadHandlerException ex) {
               result = ex.result;
               message = ex.getMessage();
            } else {
               result = PayloadResult.FAILURE;
               message = PayloadHandlerException.GENERIC_MESSAGE;
               ClientSort.LOG.error("Encountered an unexpected exception while handling payload '{}' from player '{}': {}", payloadType.id(), player, e);
            }
         } finally {
            if (menu != null) {
               menu.resumeRemoteUpdates();
               menu.slotsChanged(((Slot)menu.slots.getFirst()).container);
               menu.slotsChanged(((Slot)menu.slots.getLast()).container);
            }

            if (PlatformServices.getInstance().canSendToPlayer(player, responseType)) {
               PlatformServices.getInstance().sendToPlayer(player, responseProvider.apply(result, message));
            }
         }
      }
   }

   public static void validate(MinecraftServer server, ItemStack expected, ItemStack actual, Supplier<String> message, Consumer<String> policySetter) throws PayloadHandlerException.InconsistentStateException {
      boolean invalid = false;
      boolean log = false;
      boolean error = false;
      if (ServerConfig.serverOptions().alwaysLogUnexpectedResults) {
         log = true;
      }

      if (server.isDedicatedServer() && ServerConfig.serverOptions().validationActiveServer) {
         log = true;
         error = true;
      }

      if ((log || ServerConfig.serverOptions().validateItemType)
         && !expected.isEmpty()
         && !actual.isEmpty()
         && !ItemStack.isSameItemSameComponents(expected, actual)) {
         invalid = true;
      }

      if (log || ServerConfig.serverOptions().validateStackSize) {
         int sizeDifference = expected.getCount() > actual.getCount() ? expected.getCount() - actual.getCount() : actual.getCount() - expected.getCount();
         if (sizeDifference > 0 && sizeDifference >= ServerConfig.serverOptions().validateStackSizeThreshold) {
            invalid = true;
         }
      }

      if (invalid && log) {
         String msg = String.format("%s: Expected '%s' in destination after set, got '%s'!", message.get(), expected, actual);
         if (error) {
            ClientSort.LOG.error(msg);
            policySetter.accept(msg);
            throw new PayloadHandlerException.InconsistentStateException(msg);
         }

         ClientSort.LOG.warn(msg);
      }
   }

   @NotNull
   private static AbstractContainerMenu getMenu(ServerPlayer player, int containerId) throws PayloadHandlerException.InvalidDataException {
      AbstractContainerMenu menu;
      if (containerId == player.inventoryMenu.containerId) {
         menu = player.inventoryMenu;
      } else {
         if (containerId != player.containerMenu.containerId) {
            throw new PayloadHandlerException.InvalidDataException(
               String.format("Container ID '%d' does not match player inventory or container!", containerId)
            );
         }

         menu = player.containerMenu;
      }

      if (!menu.stillValid(player)) {
         throw new PayloadHandlerException.InvalidDataException(String.format("Container ID '%d' is not valid for the player!", containerId));
      } else {
         return menu;
      }
   }

   @FunctionalInterface
   public interface ThrowingConsumer<T> {
      void accept(T var1) throws Exception;
   }
}
