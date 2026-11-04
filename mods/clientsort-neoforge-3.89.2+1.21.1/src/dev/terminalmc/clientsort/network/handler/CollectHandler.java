package dev.terminalmc.clientsort.network.handler;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.config.ServerClassPolicy;
import dev.terminalmc.clientsort.exception.PayloadHandlerException;
import dev.terminalmc.clientsort.network.handler.validate.PolicyManager;
import dev.terminalmc.clientsort.network.handler.validate.SchemaValidator;
import dev.terminalmc.clientsort.network.payload.CollectPayload;
import dev.terminalmc.clientsort.network.payload.CollectResultPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CollectHandler extends PayloadHandler {
   private CollectHandler() {
   }

   public static void handle(CollectPayload payload, MinecraftServer server, ServerPlayer player) {
      server.execute(
         () -> processPayload(
            server,
            player,
            payload.containerId(),
            menu -> checkPolicy(player, menu, payload.slotIds()),
            menu -> SchemaValidator.validateSlotArray(player, menu, payload.slotIds()),
            menu -> collect(server, menu, payload.slotIds()),
            CollectPayload.TYPE,
            CollectResultPayload.TYPE,
            (result, message) -> new CollectResultPayload(result.code, message, payload.id())
         )
      );
   }

   private static void collect(MinecraftServer server, AbstractContainerMenu menu, int[] slotIds) throws PayloadHandlerException {
      for (int i = slotIds.length - 1; i >= 0; i--) {
         int srcSlotId = slotIds[i];
         Slot srcSlot = (Slot)menu.slots.get(srcSlotId);
         ItemStack srcStack = srcSlot.getItem();
         ItemStack srcStackCopy = srcStack.copy();
         if (!srcStack.isEmpty() && srcSlot.mayPlace(srcStack) && srcStack.getCount() < srcSlot.getMaxStackSize(srcStack)) {
            for (int j = 0; j < i; j++) {
               int dstSlotId = slotIds[j];
               Slot dstSlot = (Slot)menu.slots.get(dstSlotId);
               ItemStack dstStack = dstSlot.getItem();
               ItemStack dstStackCopy = dstStack.copy();
               if (!dstStack.isEmpty()
                  && dstSlot.mayPlace(srcStack)
                  && dstStack.getCount() < dstSlot.getMaxStackSize(dstStack)
                  && ItemStack.isSameItemSameComponents(srcStack, dstStack)) {
                  dstSlot.safeInsert(srcStack);
                  ItemStack expected = srcStackCopy.copyWithCount(
                     Math.min(srcStackCopy.getCount() + dstStackCopy.getCount(), dstSlot.getMaxStackSize(srcStackCopy))
                  );
                  validate(
                     server,
                     expected,
                     dstSlot.getItem(),
                     () -> String.format(
                        "Collect operation failed to safe-insert from slot %d with item '%s' to slot %d with item '%s'",
                        srcSlotId,
                        srcStackCopy,
                        dstSlotId,
                        dstStackCopy
                     ),
                     msg -> setPolicy(menu, slotIds, msg)
                  );
                  if (srcStack.isEmpty()) {
                     break;
                  }
               }
            }
         }
      }
   }

   private static void checkPolicy(ServerPlayer player, AbstractContainerMenu menu, int[] slotIds) throws PayloadHandlerException.UnsupportedOpException {
      Container container = slotIds.length > 0 ? ((Slot)menu.slots.get(slotIds[0])).container : null;
      Object object = ClientSort.getObj(container, menu);
      if (object == null) {
         throw new PayloadHandlerException.UnsupportedOpException(
            "Reference object is null for inputs '%s', '%s'!"
               .formatted(container == null ? "null" : container.getClass().getName(), menu == null ? "null" : menu.getClass().getName())
         );
      }

      if (container != player.getInventory()) {
         PolicyManager.checkPolicy(object.getClass(), bl -> bl.sortEnabled);
      }
   }

   private static void setPolicy(AbstractContainerMenu menu, int[] slotIds, String message) {
      Container container = slotIds.length > 0 ? ((Slot)menu.slots.get(slotIds[0])).container : null;
      Object object = ClientSort.getObj(container, menu);
      if (object == null) {
         ClientSort.LOG
            .warn(
               "Could not set policy: reference object is null for inputs '{}', '{}'!",
               container == null ? "null" : container.getClass().getName(),
               menu == null ? "null" : menu.getClass().getName()
            );
      } else {
         PolicyManager.setPolicy(new ServerClassPolicy(object.getClass().getName(), false, true, true), CollectPayload.ID.toString(), message);
      }
   }
}
