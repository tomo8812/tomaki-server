package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.core.entity.BowlAccessor;
import net.satisfy.farm_and_charm.core.entity.ai.DogEatFromBowlGoal;
import net.satisfy.farm_and_charm.core.entity.ai.WhineAtBowlGoal;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Wolf.class)
public class WolfMixin implements BowlAccessor.StayNearBowl {
   @Unique
   private boolean fedRecently = false;
   @Unique
   private BlockPos stayCenter = null;
   @Unique
   private boolean wasSittingLastTick = false;

   @Inject(method = "registerGoals()V", at = @At("TAIL"))
   private void farmAndCharm$addFeedingGoal(CallbackInfo ci) {
      Mob self = (Mob)this;
      ((MobAccessor)self).farmAndCharm$getGoalSelector().addGoal(13, new DogEatFromBowlGoal((Wolf)this));
      ((MobAccessor)self).farmAndCharm$getGoalSelector().addGoal(14, new WhineAtBowlGoal((Wolf)this));
   }

   @Inject(method = "tick()V", at = @At("TAIL"))
   private void farmAndCharm$handleStayRestriction(CallbackInfo ci) {
      Wolf wolf = (Wolf)this;
      boolean isSittingNow = wolf.isInSittingPose();
      if (!this.wasSittingLastTick && isSittingNow) {
         this.fedRecently = false;
         this.stayCenter = null;
      }

      this.wasSittingLastTick = isSittingNow;
      if (this.fedRecently && this.stayCenter != null && !isSittingNow) {
         double dist = this.stayCenter.distSqr(wolf.blockPosition());
         if (dist > 256.0) {
            wolf.getNavigation().moveTo(this.stayCenter.getX() + 0.5, this.stayCenter.getY(), this.stayCenter.getZ() + 0.5, 1.0);
         }
      }
   }

   @Inject(
      method = "mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void farmAndCharm$prioritizeDogFood(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
      Wolf wolf = (Wolf)this;
      ItemStack stack = player.getItemInHand(hand);
      if (!wolf.level().isClientSide && wolf.isTame() && stack.is((Item)ObjectRegistry.DOG_FOOD.get())) {
         if (wolf.getHealth() < wolf.getMaxHealth()) {
            wolf.heal(4.0F);
         }

         wolf.eat(wolf.level(), stack.copyWithCount(1));
         stack.shrink(1);
         cir.setReturnValue(InteractionResult.CONSUME);
      }
   }

   @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"))
   private void farmAndCharm$clearOnDeath(CallbackInfo ci) {
      this.fedRecently = false;
      this.stayCenter = null;
   }

   @Override
   public void farmAndCharm$setStayCenter(BlockPos pos) {
      this.fedRecently = true;
      this.stayCenter = pos.immutable();
   }

   @Override
   public void farmAndCharm$clearStayRestriction() {
      this.fedRecently = false;
      this.stayCenter = null;
   }

   @Override
   public boolean farmAndCharm$isWithinStayRange(BlockPos pos) {
      return this.fedRecently && this.stayCenter != null && pos.distSqr(this.stayCenter) <= 256.0;
   }

   @Override
   public BlockPos farmAndCharm$getStayCenter() {
      return this.stayCenter != null ? this.stayCenter : ((Wolf)this).blockPosition();
   }

   @Override
   public boolean farmAndCharm$hasStayRestriction() {
      return this.fedRecently;
   }
}
