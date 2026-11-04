package net.caffeinemc.mods.lithium.common.hopper;

import net.caffeinemc.mods.lithium.common.block.entity.inventory_change_tracking.InventoryChangeTracker;
import net.caffeinemc.mods.lithium.mixin.block.hopper.CompoundContainerAccessor;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LithiumDoubleStackList extends LithiumStackList {
   private final LithiumStackList first;
   private final LithiumStackList second;
   final LithiumDoubleInventory doubleInventory;
   private long signalStrengthChangeCount;

   public LithiumDoubleStackList(LithiumDoubleInventory doubleInventory, LithiumStackList first, LithiumStackList second, int maxCountPerStack) {
      super(maxCountPerStack);
      this.first = first;
      this.second = second;
      this.doubleInventory = doubleInventory;
   }

   public static LithiumDoubleStackList getOrCreate(
      LithiumDoubleInventory doubleInventory, LithiumStackList first, LithiumStackList second, int maxCountPerStack
   ) {
      LithiumDoubleStackList parentStackList = first.parent;
      if (parentStackList == null || parentStackList != second.parent || parentStackList.first != first || parentStackList.second != second) {
         if (parentStackList != null) {
            parentStackList.doubleInventory.lithium$emitRemoved();
         }

         parentStackList = new LithiumDoubleStackList(doubleInventory, first, second, maxCountPerStack);
         first.parent = parentStackList;
         second.parent = parentStackList;
      }

      return parentStackList;
   }

   @Override
   public long getModCount() {
      return this.first.getModCount() + this.second.getModCount();
   }

   @Override
   public void changedALot() {
      throw new UnsupportedOperationException("Call changed() on the inventory half only!");
   }

   @Override
   public void changed() {
      throw new UnsupportedOperationException("Call changed() on the inventory half only!");
   }

   @Override
   public ItemStack set(int index, ItemStack element) {
      return index >= this.first.size() ? this.second.set(index - this.first.size(), element) : this.first.set(index, element);
   }

   @Override
   public void add(int slot, ItemStack element) {
      throw new UnsupportedOperationException("Call add(int value, ItemStack element) on the inventory half only!");
   }

   @Override
   public ItemStack remove(int index) {
      throw new UnsupportedOperationException("Call remove(int value, ItemStack element) on the inventory half only!");
   }

   @Override
   public void clear() {
      this.first.clear();
      this.second.clear();
   }

   @Override
   public int getSignalStrength(Container inventory) {
      boolean signalStrengthOverride = this.first.hasSignalStrengthOverride() || this.second.hasSignalStrengthOverride();
      if (signalStrengthOverride) {
         return 0;
      }

      int cachedSignalStrength = this.cachedSignalStrength;
      if (cachedSignalStrength != -1 && this.getModCount() == this.signalStrengthChangeCount) {
         return cachedSignalStrength;
      }

      cachedSignalStrength = this.calculateSignalStrength(Integer.MAX_VALUE);
      this.signalStrengthChangeCount = this.getModCount();
      this.cachedSignalStrength = cachedSignalStrength;
      return cachedSignalStrength;
   }

   @Override
   public void setReducedSignalStrengthOverride() {
      this.first.setReducedSignalStrengthOverride();
      this.second.setReducedSignalStrengthOverride();
   }

   @Override
   public void clearSignalStrengthOverride() {
      this.first.clearSignalStrengthOverride();
      this.second.clearSignalStrengthOverride();
   }

   @Override
   public void runComparatorUpdatePatternOnFailedExtract(LithiumStackList masterStackList, Container inventory) {
      if (inventory instanceof CompoundContainer) {
         this.first.runComparatorUpdatePatternOnFailedExtract(this, ((CompoundContainerAccessor)inventory).getFirst());
         this.second.runComparatorUpdatePatternOnFailedExtract(this, ((CompoundContainerAccessor)inventory).getSecond());
      }
   }

   @NotNull
   public ItemStack get(int index) {
      return index >= this.first.size() ? (ItemStack)this.second.get(index - this.first.size()) : (ItemStack)this.first.get(index);
   }

   public int size() {
      return this.first.size() + this.second.size();
   }

   @Override
   public void setNextInventoryModificationCallback(@NotNull InventoryChangeTracker nextInventoryModificationCallback) {
      throw new UnsupportedOperationException("Call setNextInventoryModificationCallback() on the inventory halves only!");
   }

   @Override
   public void removeInventoryModificationCallback(@NotNull InventoryChangeTracker inventoryModificationCallback) {
      this.first.removeInventoryModificationCallback(inventoryModificationCallback);
      this.second.removeInventoryModificationCallback(inventoryModificationCallback);
   }

   @Override
   public boolean hasSignalStrengthOverride() {
      throw new UnsupportedOperationException("Call hasSignalStrengthOverride() on the inventory halves only!");
   }

   @Override
   int calculateSignalStrength(int inventorySize) {
      return super.calculateSignalStrength(inventorySize);
   }

   @Override
   public boolean maybeSendsComparatorUpdatesOnFailedExtract() {
      return this.first.maybeSendsComparatorUpdatesOnFailedExtract() || this.second.maybeSendsComparatorUpdatesOnFailedExtract();
   }

   @Override
   public int getOccupiedSlots() {
      return this.first.getOccupiedSlots() + this.second.getOccupiedSlots();
   }

   @Override
   public int getFullSlots() {
      return this.first.getFullSlots() + this.second.getFullSlots();
   }

   @Override
   public void changedInteractionConditions() {
      this.first.changedInteractionConditions();
      this.second.changedInteractionConditions();
   }

   @Override
   public void lithium$notify(@Nullable ItemStack publisher, int subscriberData) {
      throw new UnsupportedOperationException("Call lithium$notify() on the inventory halves only!");
   }

   @Override
   public void lithium$forceUnsubscribe(ItemStack publisher, int subscriberData) {
      throw new UnsupportedOperationException("Call lithium$forceUnsubscribe() on the inventory halves only!");
   }

   @Override
   public void lithium$notifyCount(ItemStack stack, int index, int newCount) {
      throw new UnsupportedOperationException("Call lithium$notifyCount() on the inventory halves only!");
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

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object remove(int param1) {
      return this.remove(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void add(int param1, Object nullx) {
      this.add(var1, (ItemStack)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object set(int param1, Object nullx) {
      return this.set(var1, (ItemStack)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @NotNull
   public Object get(int param1) {
      return this.get(var1);
   }
}
