package dev.terminalmc.clientsort.network.handler;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.config.ServerClassPolicy;
import dev.terminalmc.clientsort.exception.PayloadHandlerException;
import dev.terminalmc.clientsort.network.handler.validate.PolicyManager;
import dev.terminalmc.clientsort.network.handler.validate.SchemaValidator;
import dev.terminalmc.clientsort.network.payload.StackFillPayload;
import dev.terminalmc.clientsort.network.payload.StackFillResultPayload;
import java.util.function.Function;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class StackFillHandler extends PayloadHandler {
   private StackFillHandler() {
   }

   public static void handle(StackFillPayload payload, MinecraftServer server, ServerPlayer player) {
      server.execute(
         () -> processPayload(
            server,
            player,
            payload.srcContainerId(),
            menu -> checkPolicy(menu, payload.srcSlotIds(), payload.dstSlotIds()),
            menu -> {
               SchemaValidator.validateSlotArray(player, menu, payload.srcSlotIds());
               SchemaValidator.validateSlotArray(player, menu, payload.dstSlotIds());
            },
            menu -> fillStacks(server, menu, payload.srcSlotIds(), payload.dstSlotIds(), payload.reversed()),
            StackFillPayload.TYPE,
            StackFillResultPayload.TYPE,
            (result, message) -> new StackFillResultPayload(result.code, message)
         )
      );
   }

   private static void fillStacks(MinecraftServer server, AbstractContainerMenu menu, int[] srcSlotIds, int[] dstSlotIds, boolean reversed) throws PayloadHandlerException {
      int start = reversed ? srcSlotIds.length - 1 : 0;
      Function<Integer, Boolean> end = reversed ? ix -> ix >= 0 : ix -> ix < srcSlotIds.length;
      Function<Integer, Integer> step = reversed ? ix -> ix - 1 : ix -> ix + 1;

      for (int i = start; end.apply(i); i = step.apply(i)) {
         int srcSlotId = srcSlotIds[i];
         Slot srcSlot = (Slot)menu.slots.get(srcSlotId);
         ItemStack srcStack = srcSlot.getItem();
         ItemStack srcStackCopy = srcSlot.getItem().copy();
         if (!srcStack.isEmpty() && srcSlot.mayPlace(srcStack)) {
            for (int dstSlotId : dstSlotIds) {
               Slot dstSlot = (Slot)menu.slots.get(dstSlotId);
               ItemStack dstStack = dstSlot.getItem();
               ItemStack dstStackCopy = dstSlot.getItem().copy();
               if (!dstStack.isEmpty()
                  && dstSlot.mayPlace(srcStack)
                  && dstStack.getCount() < dstSlot.getMaxStackSize(dstStack)
                  && ItemStack.isSameItemSameComponents(srcStack, dstStack)) {
                  ItemStack expected = srcStack.copyWithCount(Math.min(srcStack.getCount() + dstStack.getCount(), dstSlot.getMaxStackSize(dstStack)));
                  dstSlot.safeInsert(srcStack);
                  validate(
                     server,
                     expected,
                     dstSlot.getItem(),
                     () -> String.format(
                        "Stack Fill operation failed to safe-insert from slot %d with item '%s' to slot %d with item '%s'",
                        srcSlotId,
                        srcStackCopy,
                        dstSlotId,
                        dstStackCopy
                     ),
                     msg -> setPolicy(menu, dstSlotIds, msg)
                  );
                  if (srcStack.isEmpty()) {
                     break;
                  }
               }
            }
         }
      }
   }

   private static void checkPolicy(AbstractContainerMenu menu, int[] srcSlotIds, int[] dstSlotIds) throws PayloadHandlerException.UnsupportedOpException {
      Container srcContainer = srcSlotIds.length > 0 ? ((Slot)menu.slots.get(srcSlotIds[0])).container : null;
      Object srcObject = ClientSort.getObj(srcContainer, menu);
      if (srcObject == null) {
         throw new PayloadHandlerException.UnsupportedOpException(
            "Reference src object is null for inputs '%s', '%s'!"
               .formatted(srcContainer == null ? "null" : srcContainer.getClass().getName(), menu == null ? "null" : menu.getClass().getName())
         );
      }

      Container dstContainer = dstSlotIds.length > 0 ? ((Slot)menu.slots.get(dstSlotIds[0])).container : null;
      Object dstObject = ClientSort.getObj(dstContainer, menu);
      if (dstObject == null) {
         throw new PayloadHandlerException.UnsupportedOpException(
            "Reference dst object is null for inputs '%s', '%s'!"
               .formatted(dstContainer == null ? "null" : dstContainer.getClass().getName(), menu == null ? "null" : menu.getClass().getName())
         );
      }

      PolicyManager.checkPolicy(srcObject.getClass(), bl -> bl.stackFillEnabled);
      PolicyManager.checkPolicy(dstObject.getClass(), bl -> bl.stackFillEnabled);
   }

   private static void setPolicy(AbstractContainerMenu menu, int[] dstSlotIds, String message) {
      Container dstContainer = dstSlotIds.length > 0 ? ((Slot)menu.slots.get(dstSlotIds[0])).container : null;
      Object object = ClientSort.getObj(dstContainer, menu);
      if (object == null) {
         ClientSort.LOG
            .warn(
               "Could not set policy: reference object is null for inputs '{}', '{}'!",
               dstContainer == null ? "null" : dstContainer.getClass().getName(),
               menu == null ? "null" : menu.getClass().getName()
            );
      } else {
         PolicyManager.setPolicy(new ServerClassPolicy(object.getClass().getName(), true, false, true), StackFillPayload.ID.toString(), message);
      }
   }
}
