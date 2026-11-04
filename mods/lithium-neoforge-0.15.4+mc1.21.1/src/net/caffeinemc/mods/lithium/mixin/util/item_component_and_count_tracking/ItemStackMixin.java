package net.caffeinemc.mods.lithium.mixin.util.item_component_and_count_tracking;

import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangePublisher;
import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangeSubscriber;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ChangePublisher<ItemStack>, ChangeSubscriber<PatchedDataComponentMap> {
   @Shadow
   @Final
   PatchedDataComponentMap components;
   @Shadow
   private int count;
   @Unique
   private ChangeSubscriber<ItemStack> subscriber;
   @Unique
   private int subscriberData;

   @Shadow
   public abstract boolean isEmpty();

   @Override
   public void lithium$subscribe(ChangeSubscriber<ItemStack> subscriber, int subscriberData) {
      if (this.isEmpty()) {
         throw new IllegalStateException("Cannot subscribe to an empty ItemStack!");
      }

      if (this.subscriber == null) {
         this.startTrackingChanges();
      }

      this.subscriber = ChangeSubscriber.combine(this.subscriber, this.subscriberData, subscriber, subscriberData);
      if (this.subscriber instanceof ChangeSubscriber.Multi) {
         this.subscriberData = 0;
      } else {
         this.subscriberData = subscriberData;
      }
   }

   @Override
   public int lithium$unsubscribe(ChangeSubscriber<ItemStack> subscriber) {
      if (this.isEmpty()) {
         throw new IllegalStateException("Cannot unsubscribe from an empty ItemStack!");
      }

      int retval = ChangeSubscriber.dataOf(this.subscriber, subscriber, this.subscriberData);
      this.subscriberData = ChangeSubscriber.dataWithout(this.subscriber, subscriber, this.subscriberData);
      this.subscriber = ChangeSubscriber.without(this.subscriber, subscriber);
      if (this.subscriber == null) {
         ((ChangePublisher)this.components).lithium$unsubscribe(this);
      }

      return retval;
   }

   @Override
   public void lithium$unsubscribeWithData(ChangeSubscriber<ItemStack> subscriber, int subscriberData) {
      if (this.isEmpty()) {
         throw new IllegalStateException("Cannot unsubscribe from an empty ItemStack!");
      }

      this.subscriberData = ChangeSubscriber.dataWithout(this.subscriber, subscriber, this.subscriberData, subscriberData, true);
      this.subscriber = ChangeSubscriber.without(this.subscriber, subscriber, subscriberData, true);
      if (this.subscriber == null) {
         ((ChangePublisher)this.components).lithium$unsubscribe(this);
      }
   }

   @Override
   public boolean lithium$isSubscribedWithData(ChangeSubscriber<ItemStack> subscriber, int subscriberData) {
      if (this.isEmpty()) {
         throw new IllegalStateException("Cannot be subscribed to an empty ItemStack!");
      } else {
         return ChangeSubscriber.containsSubscriber(this.subscriber, this.subscriberData, subscriber, subscriberData);
      }
   }

   public void lithium$forceUnsubscribe(PatchedDataComponentMap publisher, int subscriberData) {
      if (publisher != this.components) {
         throw new IllegalStateException("Invalid publisher, expected " + this.components + " but got " + publisher);
      }

      this.subscriber.lithium$forceUnsubscribe((ItemStack)this, this.subscriberData);
      this.subscriber = null;
      this.subscriberData = 0;
   }

   @Unique
   private void startTrackingChanges() {
      ((ChangePublisher)this.components).lithium$subscribe(this, 0);
   }

   @Inject(method = "setCount(I)V", at = @At("HEAD"))
   private void beforeChangeCount(int count, CallbackInfo ci) {
      if (count != this.count) {
         if (this.subscriber instanceof ChangeSubscriber.CountChangeSubscriber<ItemStack> countChangeSubscriber) {
            countChangeSubscriber.lithium$notifyCount((ItemStack)this, this.subscriberData, count);
         }

         if (count == 0) {
            ((ChangePublisher)this.components).lithium$unsubscribe(this);
            if (this.subscriber != null) {
               this.subscriber.lithium$forceUnsubscribe((ItemStack)this, this.subscriberData);
               this.subscriber = null;
               this.subscriberData = 0;
            }
         }
      }
   }

   public void lithium$notify(PatchedDataComponentMap publisher, int subscriberData) {
      if (publisher != this.components) {
         throw new IllegalStateException("Invalid publisher, expected " + this.components + " but got " + publisher);
      }

      if (this.subscriber != null) {
         this.subscriber.lithium$notify((ItemStack)this, this.subscriberData);
      }
   }

   @Inject(method = "set(Lnet/minecraft/core/component/DataComponentType;Ljava/lang/Object;)Ljava/lang/Object;", at = @At("RETURN"))
   private <T> void onSetComponent(DataComponentType<? super T> type, @Nullable T value, CallbackInfoReturnable<T> cir) {
      if (type == DataComponents.ENCHANTMENTS && this.subscriber instanceof ChangeSubscriber.EnchantmentSubscriber<ItemStack> enchantmentSubscriber) {
         enchantmentSubscriber.lithium$notifyAfterEnchantmentChange((ItemStack)this, this.subscriberData);
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void lithium$forceUnsubscribe(Object param1, int nullx) {
      this.lithium$forceUnsubscribe((PatchedDataComponentMap)var1, nullx);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void lithium$notify(Object param1, int nullx) {
      this.lithium$notify((PatchedDataComponentMap)var1, nullx);
   }
}
