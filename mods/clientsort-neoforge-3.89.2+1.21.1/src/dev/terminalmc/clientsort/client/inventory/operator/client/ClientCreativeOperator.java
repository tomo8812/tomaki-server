package dev.terminalmc.clientsort.client.inventory.operator.client;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.interaction.InteractionManager;
import dev.terminalmc.clientsort.client.util.SoundManager;
import dev.terminalmc.clientsort.util.inject.ISlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ClientCreativeOperator extends ClientOperator {
   public ClientCreativeOperator(AbstractContainerScreen<?> screen, Slot originSlot, Operation op) {
      super(screen, originSlot, op);
   }

   @Override
   protected void collect() {
      if (this.originScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            ClientSort.LOG.warn("Cannot perform operation COLLECT: origin scope is empty!");
         }
      } else {
         if (ClientSort.debug()) {
            ClientSort.LOG.info("Starting operation COLLECT");
         }

         for (int i = this.originScopeSlots.length - 1; i >= 0; i--) {
            Slot srcSlot = this.originScopeSlots[i];
            ItemStack srcStack = this.originScopeStacks[i];
            if (!srcStack.isEmpty() && srcStack.getCount() < srcSlot.getMaxStackSize(srcStack)) {
               for (int j = 0; j < i; j++) {
                  Slot dstSlot = this.originScopeSlots[j];
                  ItemStack dstStack = this.originScopeStacks[j];
                  if (!dstStack.isEmpty() && dstStack.getCount() < dstSlot.getMaxStackSize(dstStack) && ItemStack.isSameItemSameComponents(srcStack, dstStack)) {
                     int delta = dstSlot.getMaxStackSize(dstStack) - dstStack.getCount();
                     delta = Math.min(delta, srcStack.getCount());
                     srcStack.shrink(delta);
                     dstStack.grow(delta);
                     int srcSlotId = ((ISlot)srcSlot).clientsort$getIndexInMenu();
                     int dstSlotId = ((ISlot)dstSlot).clientsort$getIndexInMenu();
                     InteractionManager.push(() -> {
                        Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(srcStack.copy(), srcSlotId);
                        Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(dstStack.copy(), dstSlotId);
                        return InteractionManager.TICK_WAITER;
                     });
                     if (srcStack.getCount() <= 0) {
                        break;
                     }
                  }
               }
            }
         }

         InteractionManager.push(() -> {
            Minecraft.getInstance().player.inventoryMenu.broadcastChanges();
            if (ClientSort.debug()) {
               ClientSort.LOG.info("Finished operation COLLECT");
            }

            return InteractionManager.TICK_WAITER;
         });
      }
   }

   @Override
   protected void sort(int[] key, boolean playSound) {
      if (this.originScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            ClientSort.LOG.warn("Cannot perform operation SORT: origin scope is empty!");
         }
      } else {
         if (ClientSort.debug()) {
            ClientSort.LOG.info("Starting operation SORT");
         }

         for (int i = 0; i < key.length; i++) {
            ItemStack srcItem = this.originScopeStacks[key[i]];
            ItemStack dstItem = this.originScopeStacks[i];
            if (!srcItem.isEmpty() || !dstItem.isEmpty()) {
               int dstSlotId = ((ISlot)this.originScopeSlots[i]).clientsort$getIndexInMenu();
               InteractionManager.push(() -> {
                  Minecraft.getInstance().player.inventoryMenu.getSlot(dstSlotId).set(srcItem);
                  Minecraft.getInstance().gameMode.handleCreativeModeItemAdd(srcItem, dstSlotId);
                  if (playSound) {
                     SoundManager.play();
                  }

                  return InteractionManager.TICK_WAITER;
               });
            }
         }

         InteractionManager.push(() -> {
            Minecraft.getInstance().player.inventoryMenu.broadcastChanges();
            if (ClientSort.debug()) {
               ClientSort.LOG.info("Finished operation SORT");
            }

            return InteractionManager.TICK_WAITER;
         });
      }
   }

   @Override
   protected void fillStacks() {
      if (ClientSort.debug()) {
         ClientSort.LOG.warn("Operation STACK_FILL is not supported by {}", this.getClass().getSimpleName());
      }
   }

   @Override
   protected void matchTransfer() {
      if (ClientSort.debug()) {
         ClientSort.LOG.warn("Operation MATCH_TRANSFER is not supported by {}", this.getClass().getSimpleName());
      }
   }

   @Override
   protected void transfer() {
      if (ClientSort.debug()) {
         ClientSort.LOG.warn("Operation TRANSFER is not supported by {}", this.getClass().getSimpleName());
      }
   }
}
