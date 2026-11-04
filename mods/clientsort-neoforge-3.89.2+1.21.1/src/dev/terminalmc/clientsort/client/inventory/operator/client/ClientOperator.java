package dev.terminalmc.clientsort.client.inventory.operator.client;

import dev.terminalmc.clientsort.client.config.Operation;
import dev.terminalmc.clientsort.client.inventory.operator.SingleUseOperator;
import dev.terminalmc.clientsort.client.order.SortContext;
import dev.terminalmc.clientsort.client.order.SortOrder;
import dev.terminalmc.clientsort.client.util.SoundManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

public abstract class ClientOperator extends SingleUseOperator {
   public ClientOperator(AbstractContainerScreen<?> screen, Slot originSlot, Operation op) {
      super(screen, originSlot, op);
   }

   @Override
   protected void sort(SortOrder sortOrder) {
      if (!sortOrder.equals(SortOrder.NONE)) {
         this.collect();
         int[] key = new int[this.originScopeSlots.length];
         int i = 0;

         while (i < key.length) {
            key[i] = i++;
         }

         key = sortOrder.sort(key, this.originScopeStacks, new SortContext(Minecraft.getInstance().level));
         boolean playSound = SoundManager.shouldPlaySortingSounds();
         if (playSound) {
            SoundManager.resetForCount(SoundManager.estimateSortSounds(this.originScopeStacks));
         }

         this.sort(key, playSound);
      }
   }

   protected abstract void collect();

   protected abstract void sort(int[] var1, boolean var2);
}
