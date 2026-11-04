package net.caffeinemc.mods.lithium.mixin.entity.equipment_tracking;

import net.caffeinemc.mods.lithium.common.entity.EquipmentEntity;
import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangePublisher;
import net.caffeinemc.mods.lithium.common.util.change_tracking.ChangeSubscriber;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ChangeSubscriber.CountChangeSubscriber<ItemStack>, EquipmentEntity {
   public LivingEntityMixin(EntityType<?> type, Level world) {
      super(type, world);
   }

   @Inject(
      method = "onEquipItem(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)V",
      require = 1,
      allow = 1,
      at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/LivingEntity;firstTick:Z")
   )
   private void handleStackEquip(EquipmentSlot slot, ItemStack oldStack, ItemStack newStack, CallbackInfo ci) {
      if (!this.level().isClientSide()) {
         this.lithium$onEquipmentReplaced(oldStack, newStack);
      }
   }

   public void lithium$notify(@Nullable ItemStack publisher, int zero) {
      if (this instanceof EquipmentEntity.EquipmentTrackingEntity equipmentTrackingEntity) {
         equipmentTrackingEntity.lithium$onEquipmentChanged();
      }
   }

   public void lithium$notifyCount(ItemStack publisher, int zero, int newCount) {
      if (newCount == 0) {
         ((ChangePublisher)publisher).lithium$unsubscribeWithData(this, zero);
      }

      this.lithium$onEquipmentReplaced(publisher, ItemStack.EMPTY);
   }

   public void lithium$forceUnsubscribe(ItemStack publisher, int zero) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void lithium$onEquipmentReplaced(ItemStack oldStack, ItemStack newStack) {
      if (this instanceof EquipmentEntity.TickableEnchantmentTrackingEntity enchantmentTrackingEntity) {
         enchantmentTrackingEntity.lithium$updateHasTickableEnchantments(oldStack, newStack);
      }

      if (this instanceof EquipmentEntity.EquipmentTrackingEntity equipmentTrackingEntity) {
         equipmentTrackingEntity.lithium$onEquipmentChanged();
      }

      if (!oldStack.isEmpty()) {
         ((ChangePublisher)oldStack).lithium$unsubscribeWithData(this, 0);
      }

      if (!newStack.isEmpty()) {
         ((ChangePublisher)newStack).lithium$subscribe(this, 0);
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
   public void lithium$notify(@Nullable Object param1, int nullx) {
      this.lithium$notify((ItemStack)var1, nullx);
   }
}
