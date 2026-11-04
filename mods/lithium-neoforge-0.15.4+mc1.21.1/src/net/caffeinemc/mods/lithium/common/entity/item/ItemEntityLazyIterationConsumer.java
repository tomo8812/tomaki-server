package net.caffeinemc.mods.lithium.common.entity.item;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Predicate;
import net.caffeinemc.mods.lithium.mixin.util.accessors.ItemEntityAccessor;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.AbortableIterationConsumer.Continuation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class ItemEntityLazyIterationConsumer implements AbortableIterationConsumer<ItemEntity> {
   private final ItemStack stack;
   private final AABB box;
   private final Predicate<ItemEntity> predicate;
   private final ArrayList<ItemEntity> mergeEntities;
   private final ItemEntity searchingEntity;
   private int adjustedStackCount;

   public ItemEntityLazyIterationConsumer(ItemEntity searchingEntity, AABB box, Predicate<ItemEntity> predicate) {
      this.searchingEntity = searchingEntity;
      this.box = box;
      this.predicate = predicate;
      this.mergeEntities = new ArrayList<>();
      this.stack = this.searchingEntity.getItem();
      this.adjustedStackCount = this.stack.getCount();
   }

   public ArrayList<ItemEntity> getMergeEntities() {
      return this.mergeEntities;
   }

   public Continuation accept(ItemEntity otherItemEntity) {
      if (this.box.intersects(otherItemEntity.getBoundingBox()) && this.predicate.test(otherItemEntity)) {
         int receivedItemCount = predictReceivedItemCount(this.searchingEntity, this.stack, this.adjustedStackCount, otherItemEntity);
         if (receivedItemCount != 0) {
            this.mergeEntities.add(otherItemEntity);
            this.adjustedStackCount += receivedItemCount;
            if (this.adjustedStackCount <= 0 || this.adjustedStackCount >= this.stack.getMaxStackSize()) {
               return Continuation.ABORT;
            }
         }

         return Continuation.CONTINUE;
      } else {
         return Continuation.CONTINUE;
      }
   }

   private static int predictReceivedItemCount(ItemEntity thisEntity, ItemStack thisStack, int adjustedStackCount, ItemEntity otherEntity) {
      ItemStack otherStack;
      if (!Objects.equals(((ItemEntityAccessor)thisEntity).lithium$getOwner(), ((ItemEntityAccessor)otherEntity).lithium$getOwner())
         || !ItemEntity.areMergable(thisStack, otherStack = otherEntity.getItem())) {
         return 0;
      } else {
         return otherStack.getCount() < adjustedStackCount
            ? getTransferAmount(thisStack.getMaxStackSize(), adjustedStackCount, otherStack.getCount())
            : -getTransferAmount(otherStack.getMaxStackSize(), otherStack.getCount(), adjustedStackCount);
      }
   }

   private static int getTransferAmount(int maxCount, int targetStackCount, int sourceStackCount) {
      return Math.min(Math.min(maxCount, 64) - targetStackCount, sourceStackCount);
   }

   // $VF: synthetic method
   // $VF: bridge method
   public Continuation accept(Object param1) {
      return this.accept((ItemEntity)var1);
   }
}
