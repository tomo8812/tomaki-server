package net.caffeinemc.mods.lithium.common.util.change_tracking;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ChangeSubscriber<T> {
   static <T> ChangeSubscriber<T> combine(ChangeSubscriber<T> prevSubscriber, int prevSData, @NotNull ChangeSubscriber<T> newSubscriber, int newSData) {
      if (prevSubscriber == null) {
         return newSubscriber;
      } else if (prevSubscriber instanceof ChangeSubscriber.Multi) {
         ArrayList<ChangeSubscriber<T>> subscribers = new ArrayList<>(((ChangeSubscriber.Multi)prevSubscriber).subscribers);
         IntArrayList subscriberDatas = new IntArrayList(((ChangeSubscriber.Multi)prevSubscriber).subscriberDatas);
         subscribers.add(newSubscriber);
         subscriberDatas.add(newSData);
         return new ChangeSubscriber.Multi<>(subscribers, subscriberDatas);
      } else {
         ArrayList<ChangeSubscriber<T>> subscribers = new ArrayList<>();
         IntArrayList subscriberDatas = new IntArrayList();
         subscribers.add(prevSubscriber);
         subscriberDatas.add(prevSData);
         subscribers.add(newSubscriber);
         subscriberDatas.add(newSData);
         return new ChangeSubscriber.Multi<>(subscribers, subscriberDatas);
      }
   }

   static <T> ChangeSubscriber<T> without(ChangeSubscriber<T> prevSubscriber, ChangeSubscriber<T> removedSubscriber) {
      return without(prevSubscriber, removedSubscriber, 0, false);
   }

   static <T> ChangeSubscriber<T> without(
      ChangeSubscriber<T> prevSubscriber, ChangeSubscriber<T> removedSubscriber, int removedSubscriberData, boolean matchData
   ) {
      if (prevSubscriber == removedSubscriber) {
         return null;
      }

      if (prevSubscriber instanceof ChangeSubscriber.Multi<T> multi) {
         int index = multi.indexOf(removedSubscriber, removedSubscriberData, matchData);
         if (index != -1) {
            if (multi.subscribers.size() == 2) {
               return multi.subscribers.get(1 - index);
            }

            ArrayList<ChangeSubscriber<T>> subscribers = new ArrayList<>(multi.subscribers);
            IntArrayList subscriberDatas = new IntArrayList(multi.subscriberDatas);
            subscribers.remove(index);
            subscriberDatas.removeInt(index);
            return new ChangeSubscriber.Multi<>(subscribers, subscriberDatas);
         } else {
            return prevSubscriber;
         }
      } else {
         return prevSubscriber;
      }
   }

   static <T> int dataWithout(ChangeSubscriber<T> prevSubscriber, ChangeSubscriber<T> removedSubscriber, int subscriberData) {
      return dataWithout(prevSubscriber, removedSubscriber, subscriberData, 0, false);
   }

   static <T> int dataWithout(
      ChangeSubscriber<T> prevSubscriber, ChangeSubscriber<T> removedSubscriber, int subscriberData, int removedSubscriberData, boolean matchData
   ) {
      if (prevSubscriber instanceof ChangeSubscriber.Multi<T> multi) {
         int index = multi.indexOf(removedSubscriber, removedSubscriberData, matchData);
         if (index != -1) {
            return multi.subscribers.size() == 2 ? multi.subscriberDatas.getInt(1 - index) : subscriberData;
         } else {
            return subscriberData;
         }
      } else {
         return prevSubscriber == removedSubscriber ? 0 : subscriberData;
      }
   }

   static int dataOf(ChangeSubscriber<?> subscribers, ChangeSubscriber<?> subscriber, int subscriberData) {
      return subscribers instanceof ChangeSubscriber.Multi<?> multi ? multi.subscriberDatas.getInt(multi.subscribers.indexOf(subscriber)) : subscriberData;
   }

   static boolean containsSubscriber(ChangeSubscriber<ItemStack> subscriber, int subscriberData, ChangeSubscriber<ItemStack> subscriber1, int subscriberData1) {
      return subscriber instanceof ChangeSubscriber.Multi<ItemStack> multi
         ? multi.indexOf(subscriber1, subscriberData1, true) != -1
         : subscriber == subscriber1 && subscriberData == subscriberData1;
   }

   void lithium$notify(@Nullable T var1, int var2);

   void lithium$forceUnsubscribe(T var1, int var2);

   interface CountChangeSubscriber<T> extends ChangeSubscriber<T> {
      void lithium$notifyCount(T var1, int var2, int var3);
   }

   interface EnchantmentSubscriber<T> extends ChangeSubscriber<T> {
      void lithium$notifyAfterEnchantmentChange(T var1, int var2);
   }

   class Multi<T> implements ChangeSubscriber.CountChangeSubscriber<T>, ChangeSubscriber.EnchantmentSubscriber<T> {
      private final ArrayList<ChangeSubscriber<T>> subscribers;
      private final IntArrayList subscriberDatas;

      public Multi(ArrayList<ChangeSubscriber<T>> subscribers, IntArrayList subscriberDatas) {
         this.subscribers = subscribers;
         this.subscriberDatas = subscriberDatas;
      }

      @Override
      public void lithium$notify(T publisher, int subscriberData) {
         ArrayList<ChangeSubscriber<T>> changeSubscribers = this.subscribers;

         for (int i = 0; i < changeSubscribers.size(); i++) {
            ChangeSubscriber<T> subscriber = changeSubscribers.get(i);
            subscriber.lithium$notify(publisher, this.subscriberDatas.getInt(i));
         }
      }

      @Override
      public void lithium$forceUnsubscribe(T publisher, int subscriberData) {
         ArrayList<ChangeSubscriber<T>> changeSubscribers = this.subscribers;

         for (int i = 0; i < changeSubscribers.size(); i++) {
            ChangeSubscriber<T> subscriber = changeSubscribers.get(i);
            subscriber.lithium$forceUnsubscribe(publisher, this.subscriberDatas.getInt(i));
         }
      }

      @Override
      public void lithium$notifyCount(T publisher, int subscriberData, int newCount) {
         ArrayList<ChangeSubscriber<T>> changeSubscribers = this.subscribers;

         for (int i = 0; i < changeSubscribers.size(); i++) {
            ChangeSubscriber<T> subscriber = changeSubscribers.get(i);
            if (subscriber instanceof ChangeSubscriber.CountChangeSubscriber<T> countChangeSubscriber) {
               countChangeSubscriber.lithium$notifyCount(publisher, this.subscriberDatas.getInt(i), newCount);
            }
         }
      }

      int indexOf(ChangeSubscriber<T> subscriber, int subscriberData, boolean matchData) {
         if (!matchData) {
            return this.subscribers.indexOf(subscriber);
         }

         for (int i = 0; i < this.subscribers.size(); i++) {
            if (this.subscribers.get(i) == subscriber && this.subscriberDatas.getInt(i) == subscriberData) {
               return i;
            }
         }

         return -1;
      }

      @Override
      public void lithium$notifyAfterEnchantmentChange(T publisher, int subscriberData) {
         ArrayList<ChangeSubscriber<T>> changeSubscribers = this.subscribers;

         for (int i = 0; i < changeSubscribers.size(); i++) {
            ChangeSubscriber<T> subscriber = changeSubscribers.get(i);
            if (subscriber instanceof ChangeSubscriber.EnchantmentSubscriber<T> enchantmentSubscriber) {
               enchantmentSubscriber.lithium$notifyAfterEnchantmentChange(publisher, this.subscriberDatas.getInt(i));
            }
         }
      }
   }
}
