package net.caffeinemc.mods.lithium.mixin.util.item_component_and_count_tracking;

import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangePublisher;
import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangeSubscriber;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin implements ChangePublisher<ItemEntity>, ChangeSubscriber.CountChangeSubscriber<ItemStack> {
   @Unique
   private ChangeSubscriber<ItemEntity> subscriber;
   @Unique
   private int subscriberData;

   @Shadow
   public abstract ItemStack getItem();

   @Unique
   private void startTrackingChanges() {
      ItemStack stack = this.getItem();
      if (!stack.isEmpty()) {
         ((ChangePublisher)stack).lithium$subscribe(this, 0);
      }
   }

   @Override
   public void lithium$subscribe(ChangeSubscriber<ItemEntity> subscriber, int subscriberData) {
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
   public int lithium$unsubscribe(ChangeSubscriber<ItemEntity> subscriber) {
      int retval = ChangeSubscriber.dataOf(this.subscriber, subscriber, this.subscriberData);
      this.subscriberData = ChangeSubscriber.dataWithout(this.subscriber, subscriber, this.subscriberData);
      this.subscriber = ChangeSubscriber.without(this.subscriber, subscriber);
      if (this.subscriber == null) {
         ItemStack stack = this.getItem();
         if (!stack.isEmpty()) {
            ((ChangePublisher)stack).lithium$unsubscribe(this);
         }
      }

      return retval;
   }

   public void lithium$notify(ItemStack publisher, int subscriberData) {
      if (publisher != this.getItem()) {
         throw new IllegalStateException("Received notification from an unexpected publisher");
      }

      if (this.subscriber != null) {
         this.subscriber.lithium$notify((ItemEntity)this, this.subscriberData);
      }
   }

   public void lithium$forceUnsubscribe(ItemStack publisher, int subscriberData) {
      if (this.subscriber != null) {
         this.subscriber.lithium$forceUnsubscribe((ItemEntity)this, this.subscriberData);
         this.subscriber = null;
         this.subscriberData = 0;
      }
   }

   public void lithium$notifyCount(ItemStack publisher, int subscriberData, int newCount) {
      if (publisher != this.getItem()) {
         throw new IllegalStateException("Received notification from an unexpected publisher");
      }

      if (this.subscriber instanceof ChangeSubscriber.CountChangeSubscriber<ItemEntity> countChangeSubscriber) {
         countChangeSubscriber.lithium$notifyCount((ItemEntity)this, this.subscriberData, newCount);
      }
   }

   @Inject(method = "setItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
   private void beforeSetStack(ItemStack newStack, CallbackInfo ci) {
      if (this.subscriber != null) {
         ItemStack oldStack = this.getItem();
         if (oldStack != newStack) {
            if (!oldStack.isEmpty()) {
               ((ChangePublisher)oldStack).lithium$unsubscribe(this);
            }

            if (!newStack.isEmpty()) {
               ((ChangePublisher)newStack).lithium$subscribe(this, this.subscriberData);
               this.subscriber.lithium$notify((ItemEntity)this, this.subscriberData);
            } else {
               this.subscriber.lithium$forceUnsubscribe((ItemEntity)this, this.subscriberData);
               this.subscriber = null;
               this.subscriberData = 0;
            }
         }
      }
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
   public void lithium$notify(Object param1, int nullx) {
      this.lithium$notify((ItemStack)var1, nullx);
   }
}
