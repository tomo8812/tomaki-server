package dev.terminalmc.clientsort.client.inventory.operator.server;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.inventory.operator.SingleUseOperator;
import dev.terminalmc.clientsort.client.network.handler.CollectResultHandler;
import dev.terminalmc.clientsort.client.network.handler.SortResultHandler;
import dev.terminalmc.clientsort.client.network.handler.StackFillResultHandler;
import dev.terminalmc.clientsort.client.network.handler.TransferResultHandler;
import dev.terminalmc.clientsort.client.order.SortContext;
import dev.terminalmc.clientsort.client.order.SortOrder;
import dev.terminalmc.clientsort.client.platform.services.ClientPlatformServices;
import dev.terminalmc.clientsort.network.payload.CollectPayload;
import dev.terminalmc.clientsort.network.payload.SortPayload;
import dev.terminalmc.clientsort.network.payload.StackFillPayload;
import dev.terminalmc.clientsort.network.payload.TransferPayload;
import dev.terminalmc.clientsort.util.Localization;
import dev.terminalmc.clientsort.util.inject.ISlot;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

public class ServerOperator extends SingleUseOperator {
   public ServerOperator(AbstractContainerScreen<?> screen, Slot originSlot, Operation op) {
      super(screen, originSlot, op);
   }

   @Override
   protected void sort(SortOrder sortOrder) {
      if (!sortOrder.equals(SortOrder.NONE)) {
         if (this.originScopeSlots.length == 0) {
            if (ClientSort.debug()) {
               dev.terminalmc.clientsort.client.ClientSort.LOG.warn("Cannot perform operation SORT: origin scope is empty!");
            }
         } else {
            String id = UUID.randomUUID().toString();
            CollectResultHandler.onCompletion.put(id, collectResult -> {
               if (collectResult.isSuccess()) {
                  SortResultHandler.onCompletion = sortResult -> {
                     if (!sortResult.isSuccess()) {
                        if (!sortResult.isUnknown() && Config.options().useClientFallback) {
                           SingleUseOperator.sort(this.screen, this.originSlot, true, sortOrder);
                        } else {
                           this.setOverlayMessage(Component.translatable(sortResult.translationKey));
                        }
                     }
                  };
                  ServerOperator sorter = new ServerOperator(this.screen, this.originSlot, this.op);
                  int[] slotMapping = sorter.createSlotMapping(sortOrder);
                  if (ClientSort.debug()) {
                     dev.terminalmc.clientsort.client.ClientSort.LOG.info("Sending payload for operation SORT");
                  }

                  ClientPlatformServices.getInstance().sendToServer(new SortPayload(this.screen.getMenu().containerId, slotMapping));
               } else if (!collectResult.isUnknown() && Config.options().useClientFallback) {
                  SingleUseOperator.sort(this.screen, this.originSlot, true, sortOrder);
               } else {
                  this.setOverlayMessage(Component.translatable(collectResult.translationKey));
               }
            });
            dev.terminalmc.clientsort.client.ClientSort.taskManager.schedule(20, () -> CollectResultHandler.onCompletion.remove(id));
            int[] scopeArray = this.createSlotIdArray(this.originScopeSlots);
            this.sendCollectPayload(scopeArray, id);
         }
      }
   }

   @Override
   protected void fillStacks() {
      if (this.originScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG.warn("Cannot perform operation STACK_FILL: origin scope is empty!");
         }
      } else if (this.otherScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG.warn("Cannot perform operation STACK_FILL: other scope is empty!");
         }
      } else {
         StackFillResultHandler.onCompletion = result -> {
            if (!result.isSuccess()) {
               if (!result.isUnknown() && Config.options().useClientFallback) {
                  SingleUseOperator.fillStacks(this.screen, this.originSlot, true);
               } else {
                  this.setOverlayMessage(Component.translatable(result.translationKey));
               }
            }
         };
         int[] srcSlotIds = this.createSlotIdArray(this.originScopeSlots);
         int[] dstSlotIds = this.createSlotIdArray(this.otherScopeSlots);
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG.info("Sending payload for operation STACK_FILL");
         }

         ClientPlatformServices.getInstance()
            .sendToServer(new StackFillPayload(this.screen.getMenu().containerId, srcSlotIds, dstSlotIds, Config.options().transferReverseOrder));
      }
   }

   @Override
   protected void matchTransfer() {
      this.transfer(collectMatchingSlots(this.originScopeSlots, this.otherScopeStacks, Config.options().alwaysMatchByType, Config.options().typeMatchItemCache));
   }

   @Override
   protected void transfer() {
      this.transfer(null);
   }

   private void transfer(@Nullable Slot[] overrideSlots) {
      Slot[] originSlots = overrideSlots != null ? overrideSlots : this.originScopeSlots;
      if (this.originScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG.warn("Cannot perform operation TRANSFER: origin scope is empty!");
         }
      } else if (originSlots.length == 0) {
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG.warn("Cannot perform operation TRANSFER: origin slots is empty!");
         }
      } else if (this.otherScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG.warn("Cannot perform operation TRANSFER: other scope is empty!");
         }
      } else {
         TransferResultHandler.onCompletion = result -> {
            if (!result.isSuccess()) {
               if (result.isUnknown() || !Config.options().useClientFallback) {
                  this.setOverlayMessage(Component.translatable(result.translationKey));
               } else if (overrideSlots != null) {
                  SingleUseOperator.transferMatching(this.screen, this.originSlot, true);
               } else {
                  SingleUseOperator.transfer(this.screen, this.originSlot, true);
               }
            }
         };
         int[] srcSlotIds = this.createSlotIdArray(originSlots);
         int[] dstSlotIds = this.createSlotIdArray(this.otherScopeSlots);
         if (ClientSort.debug()) {
            dev.terminalmc.clientsort.client.ClientSort.LOG.info("Sending payload for operation TRANSFER");
         }

         ClientPlatformServices.getInstance()
            .sendToServer(new TransferPayload(this.screen.getMenu().containerId, srcSlotIds, dstSlotIds, Config.options().transferReverseOrder));
      }
   }

   private int[] createSlotIdArray(Slot[] slots) {
      int[] slotIds = new int[slots.length];

      for (int i = 0; i < slots.length; i++) {
         slotIds[i] = ((ISlot)slots[i]).clientsort$getIndexInMenu();
      }

      this.screenHelper.translateSlotIds(slotIds);
      return slotIds;
   }

   private void sendCollectPayload(int[] scopeArray, String id) {
      if (ClientSort.debug()) {
         dev.terminalmc.clientsort.client.ClientSort.LOG.info("Sending payload for operation COLLECT");
      }

      ClientPlatformServices.getInstance().sendToServer(new CollectPayload(this.screen.getMenu().containerId, scopeArray, id));
   }

   private int[] createSlotMapping(SortOrder sortOrder) {
      int[] sortedIds = new int[this.originScopeStacks.length];
      int i = 0;

      while (i < sortedIds.length) {
         sortedIds[i] = i++;
      }

      sortedIds = sortOrder.sort(sortedIds, this.originScopeStacks, new SortContext(Minecraft.getInstance().level));
      int[] slotMapping = new int[sortedIds.length * 2];

      for (int ix = 0; ix < sortedIds.length; ix++) {
         Slot from = this.originScopeSlots[sortedIds[ix]];
         Slot to = this.originScopeSlots[ix];
         slotMapping[ix * 2] = ((ISlot)from).clientsort$getIndexInMenu();
         slotMapping[ix * 2 + 1] = ((ISlot)to).clientsort$getIndexInMenu();
      }

      this.screenHelper.translateSlotIds(slotMapping);
      return slotMapping;
   }

   private void setOverlayMessage(Component message) {
      dev.terminalmc.clientsort.client.ClientSort.setOverlayMessage(
         this.screen,
         Localization.localized("name")
            .withStyle(ChatFormatting.RED)
            .append("\n")
            .append(message)
            .append("\n")
            .append(Localization.localized("message", "checkLogs")),
         100
      );
   }
}
