package net.caffeinemc.mods.lithium.common.hopper;

import net.caffeinemc.mods.lithium.api.inventory.LithiumDefaultedList;
import net.caffeinemc.mods.lithium.common.block.entity.inventory_change_tracking.InventoryChangeTracker;
import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangePublisher;
import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangeSubscriber;
import net.caffeinemc.mods.lithium.mixin.block.hopper.NonNullListAccessor;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LithiumStackList extends NonNullList<ItemStack> implements LithiumDefaultedList, ChangeSubscriber.CountChangeSubscriber<ItemStack> {
   final int maxCountPerStack;
   protected int cachedSignalStrength;
   private ComparatorUpdatePattern cachedComparatorUpdatePattern;
   private boolean signalStrengthOverride;
   private long modCount;
   private int occupiedSlots;
   private int fullSlots;
   LithiumDoubleStackList parent;
   InventoryChangeTracker nextInventoryModificationCallback;
   // $VF: synthetic field
   static final boolean $assertionsDisabled = !LithiumStackList.class.desiredAssertionStatus();

   public LithiumStackList(NonNullList<ItemStack> original, int maxCountPerStack) {
      super(((NonNullListAccessor)original).getDelegate(), ItemStack.EMPTY);
      this.maxCountPerStack = maxCountPerStack;
      this.cachedSignalStrength = -1;
      this.cachedComparatorUpdatePattern = null;
      this.modCount = 0L;
      this.signalStrengthOverride = false;
      this.occupiedSlots = 0;
      this.fullSlots = 0;
      int size = this.size();

      for (int i = 0; i < size; i++) {
         ItemStack stack = (ItemStack)this.get(i);
         if (!stack.isEmpty()) {
            this.occupiedSlots++;
            if (stack.getMaxStackSize() <= stack.getCount()) {
               this.fullSlots++;
            }

            ((ChangePublisher)stack).lithium$subscribe(this, i);
         }
      }

      this.nextInventoryModificationCallback = null;
   }

   public LithiumStackList(int maxCountPerStack) {
      super(null, ItemStack.EMPTY);
      this.maxCountPerStack = maxCountPerStack;
      this.cachedSignalStrength = -1;
      this.nextInventoryModificationCallback = null;
   }

   public long getModCount() {
      return this.modCount;
   }

   public void changedALot() {
      this.changed();
      this.occupiedSlots = 0;
      this.fullSlots = 0;
      int size = this.size();

      for (int i = 0; i < size; i++) {
         ItemStack stack = (ItemStack)this.get(i);
         if (!stack.isEmpty()) {
            this.occupiedSlots++;
            if (stack.getMaxStackSize() <= stack.getCount()) {
               this.fullSlots++;
            }

            ((ChangePublisher)stack).lithium$unsubscribe(this);
         }
      }

      for (int i = 0; i < size; i++) {
         ItemStack stack = (ItemStack)this.get(i);
         if (!stack.isEmpty()) {
            ((ChangePublisher)stack).lithium$subscribe(this, i);
         }
      }
   }

   public void changed() {
      this.cachedSignalStrength = -1;
      this.cachedComparatorUpdatePattern = null;
      this.modCount++;
      InventoryChangeTracker inventoryModificationCallback = this.nextInventoryModificationCallback;
      if (inventoryModificationCallback != null) {
         this.nextInventoryModificationCallback = null;
         inventoryModificationCallback.lithium$emitContentModified();
      }
   }

   public ItemStack set(int index, ItemStack element) {
      ItemStack previous = (ItemStack)super.set(index, element);
      if (previous == element && !element.isEmpty()) {
         boolean notSubscribed = ((ChangePublisher)previous).lithium$isSubscribedWithData(this, index);
         if (!notSubscribed) {
            previous = ItemStack.EMPTY;
         }
      }

      if (previous != element) {
         if (!previous.isEmpty()) {
            ((ChangePublisher)previous).lithium$unsubscribeWithData(this, index);
         }

         if (!element.isEmpty()) {
            ((ChangePublisher)element).lithium$subscribe(this, index);
         }

         this.occupiedSlots = this.occupiedSlots + ((previous.isEmpty() ? 1 : 0) - (element.isEmpty() ? 1 : 0));
         this.fullSlots = this.fullSlots
            + ((element.getCount() >= element.getMaxStackSize() ? 1 : 0) - (previous.getCount() >= previous.getMaxStackSize() ? 1 : 0));
         this.changed();
      }

      return previous;
   }

   public void add(int slot, ItemStack element) {
      super.add(slot, element);
      if (!element.isEmpty()) {
         ((ChangePublisher)element).lithium$subscribe(this, this.indexOf(element));
      }

      this.changedALot();
   }

   public ItemStack remove(int index) {
      ItemStack previous = (ItemStack)super.remove(index);
      if (!previous.isEmpty()) {
         ((ChangePublisher)previous).lithium$unsubscribeWithData(this, index);
      }

      this.changedALot();
      return previous;
   }

   public void clear() {
      int size = this.size();

      for (int i = 0; i < size; i++) {
         ItemStack stack = (ItemStack)this.get(i);
         if (!stack.isEmpty()) {
            ((ChangePublisher)stack).lithium$unsubscribeWithData(this, i);
         }
      }

      super.clear();
      this.changedALot();
   }

   public boolean hasSignalStrengthOverride() {
      return this.signalStrengthOverride;
   }

   public int getSignalStrength(Container inventory) {
      if (this.signalStrengthOverride) {
         return 0;
      }

      int signalStrength = this.cachedSignalStrength;
      return signalStrength == -1 ? (this.cachedSignalStrength = this.calculateSignalStrength(inventory.getContainerSize())) : signalStrength;
   }

   int calculateSignalStrength(int inventorySize) {
      int i = 0;
      float f = 0.0F;
      inventorySize = Math.min(inventorySize, this.size());

      for (int j = 0; j < inventorySize; j++) {
         ItemStack itemStack = (ItemStack)this.get(j);
         if (!itemStack.isEmpty()) {
            f += (float)itemStack.getCount() / Math.min(this.maxCountPerStack, itemStack.getMaxStackSize());
            i++;
         }
      }

      f /= inventorySize;
      return Mth.floor(f * 14.0F) + (i > 0 ? 1 : 0);
   }

   public void setReducedSignalStrengthOverride() {
      this.signalStrengthOverride = true;
   }

   public void clearSignalStrengthOverride() {
      this.signalStrengthOverride = false;
   }

   public void runComparatorUpdatePatternOnFailedExtract(LithiumStackList masterStackList, Container inventory) {
      if (inventory instanceof BlockEntity) {
         if (this.cachedComparatorUpdatePattern == null) {
            this.cachedComparatorUpdatePattern = HopperHelper.determineComparatorUpdatePattern(inventory, masterStackList);
         }

         this.cachedComparatorUpdatePattern.apply((BlockEntity)inventory, masterStackList);
      }
   }

   public boolean maybeSendsComparatorUpdatesOnFailedExtract() {
      return this.cachedComparatorUpdatePattern == null || this.cachedComparatorUpdatePattern != ComparatorUpdatePattern.NO_UPDATE;
   }

   public int getOccupiedSlots() {
      return this.occupiedSlots;
   }

   public int getFullSlots() {
      return this.fullSlots;
   }

   @Override
   public void changedInteractionConditions() {
      this.changed();
   }

   public void setNextInventoryModificationCallback(@NotNull InventoryChangeTracker nextInventoryModificationCallback) {
      if (this.nextInventoryModificationCallback != null && this.nextInventoryModificationCallback != nextInventoryModificationCallback) {
         this.nextInventoryModificationCallback.emitCallbackReplaced();
      }

      this.nextInventoryModificationCallback = nextInventoryModificationCallback;
   }

   public void removeInventoryModificationCallback(@NotNull InventoryChangeTracker inventoryModificationCallback) {
      if (this.nextInventoryModificationCallback != null && this.nextInventoryModificationCallback == inventoryModificationCallback) {
         this.nextInventoryModificationCallback = null;
      }
   }

   public void lithium$notify(@Nullable ItemStack publisher, int subscriberData) {
   }

   public void lithium$forceUnsubscribe(ItemStack publisher, int subscriberData) {
      throw new UnsupportedOperationException("Cannot force unsubscribe on a LithiumStackList!");
   }

   public void lithium$notifyCount(ItemStack stack, int index, int newCount) {
      assert stack == this.get(index);
      int count = stack.getCount();
      if (newCount <= 0) {
         ((ChangePublisher)stack).lithium$unsubscribeWithData(this, index);
      }

      int maxCount = stack.getMaxStackSize();
      this.occupiedSlots -= newCount <= 0 ? 1 : 0;
      this.fullSlots += (newCount >= maxCount ? 1 : 0) - (count >= maxCount ? 1 : 0);
      this.changed();
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Object remove(int param1) {
      return this.remove(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public void add(int param1, Object nullx) {
      this.add(var1, (ItemStack)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Object set(int param1, Object nullx) {
      return this.set(var1, (ItemStack)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void lithium$notifyCount(Object param1, int nullx, int nullxx) {
      this.lithium$notifyCount((ItemStack)var1, nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void lithium$forceUnsubscribe(Object param1, int nullx) {
      this.lithium$forceUnsubscribe((ItemStack)var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void lithium$notify(@Nullable Object param1, int nullx) {
      this.lithium$notify((ItemStack)var1, nullx);
   }
}
