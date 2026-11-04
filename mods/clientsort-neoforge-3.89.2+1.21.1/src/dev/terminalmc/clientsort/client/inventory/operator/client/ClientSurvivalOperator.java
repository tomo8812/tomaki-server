package dev.terminalmc.clientsort.client.inventory.operator.client;

import dev.terminalmc.clientsort.ClientSort;
import dev.terminalmc.clientsort.client.config.Config;
import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.interaction.InteractionManager;
import dev.terminalmc.clientsort.client.util.SoundManager;
import dev.terminalmc.clientsort.mixin.client.accessor.AbstractContainerScreenAccessor;
import dev.terminalmc.clientsort.util.inject.ISlot;
import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.function.Function;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ClientSurvivalOperator extends ClientOperator {
   public ClientSurvivalOperator(AbstractContainerScreen<?> screen, Slot originSlot, Operation op) {
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

         ArrayDeque<InteractionManager.InteractionEvent> clickEvents = new ArrayDeque<>();

         for (int i = this.originScopeSlots.length - 1; i >= 0; i--) {
            Slot srcSlot = this.originScopeSlots[i];
            ItemStack srcStack = this.originScopeStacks[i];
            if (!srcStack.isEmpty() && srcStack.getCount() < srcSlot.getMaxStackSize(srcStack)) {
               clickEvents.add(this.createClickEvent(srcSlot, 0, false));

               for (int j = 0; j < i; j++) {
                  Slot dstSlot = this.originScopeSlots[j];
                  ItemStack dstStack = this.originScopeStacks[j];
                  if (!dstStack.isEmpty() && dstStack.getCount() < dstSlot.getMaxStackSize(dstStack) && ItemStack.isSameItemSameComponents(srcStack, dstStack)) {
                     int delta = dstSlot.getMaxStackSize(dstStack) - dstStack.getCount();
                     delta = Math.min(delta, srcStack.getCount());
                     srcStack.shrink(delta);
                     dstStack.grow(delta);
                     clickEvents.add(this.createClickEvent(dstSlot, 0, false));
                     if (srcStack.getCount() <= 0) {
                        break;
                     }
                  }
               }

               if (clickEvents.size() > 1) {
                  InteractionManager.pushAll(clickEvents);
                  InteractionManager.triggerSend(InteractionManager.TriggerType.GUI_CONFIRM);
                  clickEvents.clear();
                  if (srcStack.getCount() > 0) {
                     InteractionManager.push(this.createClickEvent(srcSlot, 0, false));
                  } else {
                     this.originScopeStacks[i] = ItemStack.EMPTY;
                  }
               }

               clickEvents.clear();
            }
         }

         if (ClientSort.debug()) {
            InteractionManager.push(() -> {
               ClientSort.LOG.info("Finished operation COLLECT");
               return InteractionManager.TICK_WAITER;
            });
         }
      }
   }

   @Override
   protected void sort(int[] sortedIds, boolean playSound) {
      if (this.originScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            ClientSort.LOG.warn("Cannot perform operation SORT: origin scope is empty!");
         }
      } else {
         if (ClientSort.debug()) {
            ClientSort.LOG.info("Starting operation SORT");
         }

         int slotCount = this.originScopeStacks.length;
         int[] srcToDst = new int[slotCount];
         int i = 0;

         while (i < slotCount) {
            srcToDst[sortedIds[i]] = i++;
         }

         BitSet done = new BitSet(slotCount);
         BitSet empty = new BitSet(slotCount);

         for (int ix = 0; ix < slotCount; ix++) {
            if (ix == sortedIds[ix]) {
               done.set(ix);
            }

            if (this.originScopeStacks[ix].isEmpty()) {
               empty.set(ix);
            }
         }

         for (int ix = 0; ix < slotCount; ix++) {
            if (!done.get(ix)) {
               if (empty.get(sortedIds[ix])) {
                  done.set(sortedIds[ix]);
               } else {
                  InteractionManager.push(this.createClickEvent(this.originScopeSlots[sortedIds[ix]], 0, playSound));
                  ItemStack carriedStack = this.originScopeStacks[sortedIds[ix]];
                  this.originScopeStacks[sortedIds[ix]] = ItemStack.EMPTY;
                  empty.set(sortedIds[ix]);
                  Slot workingSlot = this.originScopeSlots[sortedIds[ix]];
                  int dstId = ix;

                  while (true) {
                     label126: {
                        if (!empty.get(dstId) && ItemStack.isSameItemSameComponents(carriedStack, this.originScopeStacks[dstId])) {
                           if (carriedStack.getCount() == this.originScopeStacks[dstId].getCount()) {
                              done.set(dstId);
                              dstId = srcToDst[dstId];
                              break label126;
                           }

                           if (carriedStack.getCount() < this.originScopeStacks[dstId].getCount()) {
                              Slot dstSlot = this.originScopeSlots[dstId];
                              InteractionManager.push(this.createClickEvent(workingSlot, 0, playSound));
                              InteractionManager.push(this.createClickEvent(dstSlot, 0, playSound));
                              InteractionManager.push(this.createClickEvent(workingSlot, 0, playSound));
                              InteractionManager.push(this.createClickEvent(dstSlot, 0, playSound));
                              InteractionManager.push(this.createClickEvent(workingSlot, 0, playSound));
                              ItemStack tmp = carriedStack;
                              carriedStack = this.originScopeStacks[dstId];
                              this.originScopeStacks[dstId] = tmp;
                              done.set(dstId);
                              dstId = srcToDst[dstId];
                              break label126;
                           }
                        }

                        int mouseButton = 0;
                        boolean clickOnBundleWithItem = isBundle(this.originScopeStacks[dstId]) && !carriedStack.isEmpty();
                        boolean clickOnItemWithBundle = isBundle(carriedStack) && !this.originScopeStacks[dstId].isEmpty();
                        if (Config.options().bundlesUseLeftClick && (clickOnBundleWithItem || clickOnItemWithBundle)) {
                           mouseButton = 1;
                        }

                        InteractionManager.push(this.createClickEvent(this.originScopeSlots[dstId], mouseButton, playSound));
                        ItemStack tmp = carriedStack;
                        carriedStack = this.originScopeStacks[dstId];
                        this.originScopeStacks[dstId] = tmp;
                        done.set(dstId);
                        if (empty.get(dstId)) {
                           break;
                        }

                        dstId = srcToDst[dstId];
                     }

                     if (done.get(dstId)) {
                        break;
                     }
                  }
               }
            }
         }

         if (ClientSort.debug()) {
            InteractionManager.push(() -> {
               ClientSort.LOG.info("Finished operation SORT");
               return InteractionManager.TICK_WAITER;
            });
         }
      }
   }

   @Override
   protected void fillStacks() {
      if (this.originScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            ClientSort.LOG.warn("Cannot perform operation STACK_FILL: origin scope is empty!");
         }
      } else if (this.otherScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            ClientSort.LOG.warn("Cannot perform operation STACK_FILL: other scope is empty!");
         }
      } else {
         if (ClientSort.debug()) {
            ClientSort.LOG.info("Starting operation STACK_FILL");
         }

         boolean playSound = SoundManager.shouldPlayOtherSounds();
         if (playSound) {
            SoundManager.resetForCount(SoundManager.estimateStackFillSounds(this.originScopeStacks, this.otherScopeStacks));
         }

         ArrayDeque<InteractionManager.InteractionEvent> clickEvents = new ArrayDeque<>();

         for (int i = this.originScopeSlots.length - 1; i >= 0; i--) {
            Slot srcSlot = this.originScopeSlots[i];
            ItemStack srcStack = this.originScopeStacks[i];
            if (!srcStack.isEmpty()) {
               clickEvents.add(this.createClickEvent(srcSlot, 0, false));

               for (int j = 0; j < this.otherScopeSlots.length; j++) {
                  Slot dstSlot = this.otherScopeSlots[j];
                  ItemStack dstStack = this.otherScopeStacks[j];
                  if (!dstStack.isEmpty() && dstStack.getCount() < dstSlot.getMaxStackSize(dstStack) && ItemStack.isSameItemSameComponents(srcStack, dstStack)) {
                     int delta = dstSlot.getMaxStackSize(dstStack) - dstStack.getCount();
                     delta = Math.min(delta, srcStack.getCount());
                     srcStack.setCount(srcStack.getCount() - delta);
                     dstStack.setCount(dstStack.getCount() + delta);
                     clickEvents.add(this.createClickEvent(dstSlot, 0, playSound));
                     if (srcStack.getCount() <= 0) {
                        break;
                     }
                  }
               }

               if (clickEvents.size() > 1) {
                  InteractionManager.pushAll(clickEvents);
                  InteractionManager.triggerSend(InteractionManager.TriggerType.GUI_CONFIRM);
                  clickEvents.clear();
                  if (srcStack.getCount() > 0) {
                     InteractionManager.push(this.createClickEvent(srcSlot, 0, false));
                  } else {
                     this.originScopeStacks[i] = ItemStack.EMPTY;
                  }
               }

               clickEvents.clear();
            }
         }

         if (ClientSort.debug()) {
            InteractionManager.push(() -> {
               ClientSort.LOG.info("Finished operation STACK_FILL");
               return InteractionManager.TICK_WAITER;
            });
         }
      }
   }

   @Override
   protected void matchTransfer() {
      this.transfer(collectMatchingSlots(this.originScopeSlots, this.otherScopeStacks, Config.options().alwaysMatchByType, Config.options().typeMatchItemCache));
   }

   @Override
   protected void transfer() {
      this.transfer(this.originScopeSlots);
   }

   protected void transfer(Slot[] originSlots) {
      if (this.originScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            ClientSort.LOG.warn("Cannot perform operation (MATCH_)TRANSFER: origin scope is empty!");
         }
      } else if (originSlots.length == 0) {
         if (ClientSort.debug()) {
            ClientSort.LOG.warn("Cannot perform operation (MATCH_)TRANSFER: origin slots is empty!");
         }
      } else if (this.otherScopeSlots.length == 0) {
         if (ClientSort.debug()) {
            ClientSort.LOG.warn("Cannot perform operation (MATCH_)TRANSFER: other scope is empty!");
         }
      } else {
         if (ClientSort.debug()) {
            ClientSort.LOG.info("Starting operation (MATCH_)TRANSFER");
         }

         ItemStack[] originStacks = this.originScopeStacks;
         if (originSlots != this.originScopeSlots) {
            originStacks = new ItemStack[originSlots.length];

            for (int i = 0; i < originSlots.length; i++) {
               originStacks[i] = originSlots[i].getItem().copy();
            }
         }

         boolean playSound = SoundManager.shouldPlayOtherSounds();
         if (playSound) {
            SoundManager.resetForCount(SoundManager.estimateTransferSounds(originStacks, this.otherScopeStacks));
         }

         ArrayDeque<InteractionManager.InteractionEvent> clickEvents = new ArrayDeque<>();
         boolean reversed = Config.options().transferReverseOrder;
         int start = reversed ? originSlots.length - 1 : 0;
         Function<Integer, Boolean> end = reversed ? ix -> ix >= 0 : ix -> ix < originSlots.length;
         Function<Integer, Integer> step = reversed ? ix -> ix - 1 : ix -> ix + 1;

         for (int i = start; end.apply(i); i = step.apply(i)) {
            Slot srcSlot = originSlots[i];
            ItemStack srcStack = originStacks[i];
            if (!srcStack.isEmpty()) {
               clickEvents.add(this.createClickEvent(srcSlot, 0, false));
               int emptySlotId = -1;

               for (int j = 0; j < this.otherScopeSlots.length; j++) {
                  Slot dstSlot = this.otherScopeSlots[j];
                  ItemStack dstStack = this.otherScopeStacks[j];
                  if (dstStack.isEmpty()) {
                     if (emptySlotId == -1) {
                        emptySlotId = j;
                     }
                  } else if (dstStack.getCount() < dstSlot.getMaxStackSize(dstStack) && ItemStack.isSameItemSameComponents(srcStack, dstStack)) {
                     int delta = dstSlot.getMaxStackSize(dstStack) - dstStack.getCount();
                     delta = Math.min(delta, srcStack.getCount());
                     srcStack.setCount(srcStack.getCount() - delta);
                     dstStack.setCount(dstStack.getCount() + delta);
                     clickEvents.add(this.createClickEvent(dstSlot, 0, playSound));
                     if (srcStack.isEmpty()) {
                        break;
                     }
                  }
               }

               if (!srcStack.isEmpty() && emptySlotId != -1) {
                  Slot dstSlot = this.otherScopeSlots[emptySlotId];
                  this.otherScopeStacks[emptySlotId] = srcStack.copy();
                  srcStack.setCount(0);
                  clickEvents.add(this.createClickEvent(dstSlot, 0, playSound));
               }

               if (clickEvents.size() > 1) {
                  InteractionManager.pushAll(clickEvents);
                  InteractionManager.triggerSend(InteractionManager.TriggerType.GUI_CONFIRM);
                  clickEvents.clear();
                  if (srcStack.getCount() > 0) {
                     InteractionManager.push(this.createClickEvent(srcSlot, 0, false));
                  } else {
                     originStacks[i] = ItemStack.EMPTY;
                  }
               }

               clickEvents.clear();
            }
         }

         if (ClientSort.debug()) {
            InteractionManager.push(() -> {
               ClientSort.LOG.info("Finished operation (MATCH_)TRANSFER");
               return InteractionManager.TICK_WAITER;
            });
         }
      }
   }

   private InteractionManager.InteractionEvent createClickEvent(Slot slot, int button, boolean playSound) {
      return new InteractionManager.CallbackEvent(() -> {
         ((AbstractContainerScreenAccessor)this.screen).clientsort$slotClicked(slot, ((ISlot)slot).clientsort$getIndexInMenu(), button, ClickType.PICKUP);
         if (playSound) {
            SoundManager.play();
         }

         return InteractionManager.TICK_WAITER;
      });
   }

   private static boolean isBundle(ItemStack stack) {
      return stack.is(Items.BUNDLE) || stack.getItem() instanceof BundleItem;
   }
}
