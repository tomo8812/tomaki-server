package net.caffeinemc.mods.lithium.common.entity.item;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Hash.Strategy;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenCustomHashMap;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;
import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangePublisher;
import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangeSubscriber;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.AbortableIterationConsumer.Continuation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemEntityList extends AbstractList<ItemEntity> implements ChangeSubscriber.CountChangeSubscriber<ItemEntity> {
   private static final Strategy<ItemStack> STRATEGY = new Strategy<ItemStack>() {
      public int hashCode(ItemStack itemStack) {
         return HashCommon.mix(ItemStack.hashItemAndComponents(itemStack));
      }

      public boolean equals(ItemStack itemStack, ItemStack otherItemStack) {
         return itemStack == otherItemStack || itemStack != null && otherItemStack != null && ItemStack.isSameItemSameComponents(itemStack, otherItemStack);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public boolean equals(Object param1, Object nullx) {
         return this.equals((ItemStack)var1, (ItemStack)nullx);
      }

      // $VF: synthetic method
      // $VF: bridge method
      public int hashCode(Object param1) {
         return this.hashCode((ItemStack)var1);
      }
   };
   public static final int UPGRADE_THRESHOLD = 10;
   private final ArrayList<ItemEntity> delegate;
   private final ArrayList<ItemEntity> delegateWithNulls;
   private final Object2ReferenceOpenCustomHashMap<ItemStack, IntArrayList> elementsByCategory;
   private final Object2ReferenceOpenCustomHashMap<ItemStack, IntArrayList> maxHalfFullElementsByCategory;
   private final IntOpenHashSet tempUncategorizedElements;

   public ItemEntityList(ArrayList<ItemEntity> delegate) {
      this.delegate = delegate;
      this.delegateWithNulls = new ArrayList<>(delegate);
      this.elementsByCategory = new Object2ReferenceOpenCustomHashMap(STRATEGY);
      this.maxHalfFullElementsByCategory = new Object2ReferenceOpenCustomHashMap(STRATEGY);
      this.tempUncategorizedElements = new IntOpenHashSet();

      for (int i = 0; i < this.delegateWithNulls.size(); i++) {
         ItemEntity element = this.delegateWithNulls.get(i);
         this.addToCategories(element, i, false);
         this.subscribeElement(element, i);
      }
   }

   protected void markElementAsOutdated(ItemEntity element, int index) {
      boolean added = this.tempUncategorizedElements.add(index);
      if (added) {
         this.removeFromCategories(element, index);
      }
   }

   protected void processOutdated() {
      if (!this.tempUncategorizedElements.isEmpty()) {
         this.tempUncategorizedElements.forEach(index -> {
            ItemEntity element = this.delegateWithNulls.get(index);
            if (element != null) {
               if (element.getItem().isEmpty()) {
                  this.delegateWithNulls.set(index, null);
                  this.unsubscribeElement(element);
               } else {
                  this.addToCategories(element, index, true);
               }
            }
         });
         this.tempUncategorizedElements.clear();
      }
   }

   @Override
   public int size() {
      return this.delegate.size();
   }

   @Override
   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return this.delegate.contains(o);
   }

   @NotNull
   @Override
   public Object[] toArray() {
      return this.delegate.toArray();
   }

   @NotNull
   @Override
   public <U> U[] toArray(U @NotNull [] a) {
      return this.delegate.toArray(a);
   }

   public boolean add(ItemEntity element) {
      this.processOutdated();
      if (element.getItem().isEmpty()) {
         this.delegateWithNulls.add(null);
      } else {
         int index = this.delegateWithNulls.size();
         this.delegateWithNulls.add(element);
         this.addToCategories(element, index, false);
         this.subscribeElement(element, index);
      }

      return this.delegate.add(element);
   }

   private void addToCategories(ItemEntity element, int index, boolean insertionSort) {
      ItemStack stack = element.getItem();
      if (!stack.isEmpty()) {
         ItemStack key = addToCategoryList(index, stack, null, this.elementsByCategory, insertionSort);
         if (isMaxHalfFull(stack)) {
            addToCategoryList(index, stack, key, this.maxHalfFullElementsByCategory, insertionSort);
         }
      }
   }

   private static ItemStack addToCategoryList(
      int index, ItemStack stack, @Nullable ItemStack key, Object2ReferenceOpenCustomHashMap<ItemStack, IntArrayList> categoryMap, boolean insertionSort
   ) {
      if (stack.isEmpty()) {
         return key;
      }

      IntArrayList categoryList = (IntArrayList)categoryMap.get(stack);
      if (categoryList == null) {
         if (key == null) {
            key = stack.copy();
         }

         categoryList = new IntArrayList();
         categoryMap.put(key, categoryList);
      }

      if (insertionSort) {
         int binarySearchIndex = Collections.binarySearch(categoryList, index);
         binarySearchIndex = -(binarySearchIndex + 1);
         categoryList.add(binarySearchIndex, index);
      } else {
         categoryList.add(index);
      }

      return key;
   }

   private void subscribeElement(ItemEntity element, int index) {
      ((ChangePublisher)element).lithium$subscribe(this, index);
   }

   private static boolean isMaxHalfFull(ItemStack stack) {
      int count = stack.getCount();
      int maxCount = stack.getMaxStackSize();
      return isMaxHalfFull(count, maxCount);
   }

   private static boolean isMaxHalfFull(int count, int maxCount) {
      return count * 2 <= maxCount;
   }

   @Override
   public boolean remove(Object o) {
      boolean remove = this.delegate.remove(o);
      if (remove && o instanceof ItemEntity element) {
         this.processOutdated();
         this.removeElement(element);
      }

      return remove;
   }

   public ItemEntity remove(int index) {
      ItemEntity element = this.delegate.remove(index);
      if (element != null) {
         this.processOutdated();
         this.removeElement(element);
      }

      return element;
   }

   private void removeElement(ItemEntity element) {
      if (!element.getItem().isEmpty()) {
         int index = this.unsubscribeElement(element);
         if (index == this.delegateWithNulls.size() - 1) {
            ItemEntity remove = this.delegateWithNulls.remove(index);
            if (remove != element) {
               throw new IllegalStateException("Element mismatch, expected " + element + " but got " + remove);
            }
         } else {
            this.delegateWithNulls.set(index, null);
         }

         this.removeFromCategories(element, index);
      }

      int size = this.delegateWithNulls.size();
      if (size > 64 && size > this.delegate.size() * 2) {
         this.reinitialize();
      }
   }

   private void reinitialize() {
      for (ItemEntity element : this.delegate) {
         this.unsubscribeElement(element);
      }

      this.tempUncategorizedElements.clear();
      this.delegateWithNulls.clear();
      this.elementsByCategory.clear();
      this.maxHalfFullElementsByCategory.clear();
      int i = 0;
      int j = 0;

      while (i < this.delegate.size()) {
         ItemEntity element = this.delegate.get(i);
         if (!element.getItem().isEmpty()) {
            this.delegateWithNulls.add(element);
            this.addToCategories(element, j, false);
            this.subscribeElement(element, j);
            j++;
         }

         i++;
      }
   }

   private int unsubscribeElement(ItemEntity element) {
      return ((ChangePublisher)element).lithium$unsubscribe(this);
   }

   private void removeFromCategories(ItemEntity element, int index) {
      ItemStack stack = element.getItem();
      if (!stack.isEmpty()) {
         removeFromCategoryList(this.elementsByCategory, stack, index);
         if (isMaxHalfFull(stack)) {
            removeFromCategoryList(this.maxHalfFullElementsByCategory, stack, index);
         }
      }
   }

   private static void removeFromCategoryList(Object2ReferenceOpenCustomHashMap<ItemStack, IntArrayList> elementsByCategory, ItemStack stack, int index) {
      IntArrayList categoryList = (IntArrayList)elementsByCategory.get(stack);
      if (categoryList != null) {
         categoryList.rem(index);
         if (categoryList.isEmpty()) {
            elementsByCategory.remove(stack);
         }
      }
   }

   @Override
   public boolean containsAll(@NotNull Collection<?> c) {
      return this.delegate.containsAll(c);
   }

   @Override
   public void clear() {
      for (ItemEntity element : this.delegate) {
         this.unsubscribeElement(element);
      }

      this.delegate.clear();
      this.tempUncategorizedElements.clear();
      this.delegateWithNulls.clear();
      this.elementsByCategory.clear();
      this.maxHalfFullElementsByCategory.clear();
   }

   @Override
   public boolean equals(Object o) {
      return this.delegate.equals(o);
   }

   @Override
   public int hashCode() {
      return this.delegate.hashCode();
   }

   public ItemEntity get(int index) {
      return this.delegate.get(index);
   }

   public ItemEntity set(int i, ItemEntity newElement) {
      ItemEntity previousElement = this.delegate.set(i, newElement);
      if (previousElement != newElement) {
         this.processOutdated();
         int index;
         if (previousElement.getItem().isEmpty()) {
            if (this.delegateWithNulls.size() != this.delegate.size()) {
               this.reinitialize();
               return previousElement;
            }

            index = i;
         } else {
            index = this.unsubscribeElement(previousElement);
            this.removeFromCategories(previousElement, index);
         }

         if (newElement.getItem().isEmpty()) {
            this.delegateWithNulls.set(index, null);
            return previousElement;
         }

         ItemEntity replaced = this.delegateWithNulls.set(index, newElement);
         this.addToCategories(newElement, index, true);
         this.subscribeElement(newElement, index);
         if (replaced != null && replaced != previousElement && !replaced.getItem().isEmpty()) {
            throw new IllegalStateException("Element mismatch, expected " + previousElement + " but got " + replaced);
         }
      }

      return previousElement;
   }

   @Override
   public int indexOf(Object o) {
      return this.delegate.indexOf(o);
   }

   @Override
   public int lastIndexOf(Object o) {
      return this.delegate.lastIndexOf(o);
   }

   @Override
   public <U> U[] toArray(IntFunction<U[]> generator) {
      return this.delegate.toArray(generator);
   }

   @Override
   public Stream<ItemEntity> stream() {
      return this.delegate.stream();
   }

   @Override
   public Stream<ItemEntity> parallelStream() {
      return this.delegate.parallelStream();
   }

   @Override
   public void forEach(Consumer<? super ItemEntity> action) {
      this.delegate.forEach(action);
   }

   public void lithium$notify(@Nullable ItemEntity publisher, int subscriberData) {
      this.markElementAsOutdated(publisher, subscriberData);
   }

   public void lithium$forceUnsubscribe(ItemEntity publisher, int subscriberData) {
      this.markElementAsOutdated(publisher, subscriberData);
   }

   public void lithium$notifyCount(ItemEntity element, int index, int newCount) {
      this.processOutdated();
      ItemStack stack = element.getItem();
      if (newCount <= 0) {
         this.removeFromCategories(element, index);
      } else {
         boolean wasMaxHalfFull = isMaxHalfFull(stack);
         boolean isMaxHalfFull = isMaxHalfFull(newCount, stack.getMaxStackSize());
         if (wasMaxHalfFull != isMaxHalfFull) {
            if (isMaxHalfFull) {
               addToCategoryList(index, stack, null, this.maxHalfFullElementsByCategory, true);
            } else {
               removeFromCategoryList(this.maxHalfFullElementsByCategory, stack, index);
            }
         }
      }
   }

   public Continuation consumeForEntityStacking(ItemEntity searchingEntity, AbortableIterationConsumer<ItemEntity> itemEntityConsumer) {
      this.processOutdated();
      ItemStack stack = searchingEntity.getItem();
      int count = stack.getCount();
      int maxCount = stack.getMaxStackSize();
      return count * 2 >= maxCount
         ? this.consumeElements(itemEntityConsumer, (IntArrayList)this.maxHalfFullElementsByCategory.get(stack))
         : this.consumeElements(itemEntityConsumer, (IntArrayList)this.elementsByCategory.get(stack));
   }

   private Continuation consumeElements(AbortableIterationConsumer<ItemEntity> elementConsumer, IntArrayList categoryList) {
      if (categoryList == null) {
         return Continuation.CONTINUE;
      }

      int expectedModCount = this.modCount;
      int size = categoryList.size();

      for (int i = 0; i < size; i++) {
         if (expectedModCount != this.modCount) {
            throw new ConcurrentModificationException("Collection was modified during iteration!");
         }

         ItemEntity element = this.delegateWithNulls.get(categoryList.getInt(i));
         Continuation next = elementConsumer.accept(element);
         if (next != Continuation.CONTINUE) {
            return next;
         }
      }

      return Continuation.CONTINUE;
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
   public Object set(int param1, Object nullx) {
      return this.set(var1, (ItemEntity)nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object get(int param1) {
      return this.get(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public boolean add(Object param1) {
      return this.add((ItemEntity)var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void lithium$notifyCount(Object param1, int nullx, int nullxx) {
      this.lithium$notifyCount((ItemEntity)var1, nullx, nullxx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void lithium$forceUnsubscribe(Object param1, int nullx) {
      this.lithium$forceUnsubscribe((ItemEntity)var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void lithium$notify(@Nullable Object param1, int nullx) {
      this.lithium$notify((ItemEntity)var1, nullx);
   }
}
