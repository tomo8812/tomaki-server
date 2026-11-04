package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;
import net.satisfy.farm_and_charm.core.entity.BowlAccessor;
import net.satisfy.farm_and_charm.core.entity.ai.CatEatFromBowlGoal;
import net.satisfy.farm_and_charm.core.entity.ai.MeowAtBowlGoal;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import net.satisfy.farm_and_charm.platform.PlatformHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Cat.class)
public class CatMixin implements BowlAccessor.FedTracker {
   @Unique
   private boolean farmAndCharm$fedFromBowl = false;
   @Unique
   private int farmAndCharm$fedTimer = 0;

   @Inject(method = "registerGoals()V", at = @At("TAIL"))
   private void farmAndCharm$addFeedingGoal(CallbackInfo ci) {
      Mob self = (Mob)this;
      ((MobAccessor)self).farmAndCharm$getGoalSelector().addGoal(13, new CatEatFromBowlGoal((Cat)this));
      ((MobAccessor)self).farmAndCharm$getGoalSelector().addGoal(14, new MeowAtBowlGoal((Cat)this));
   }

   @Inject(method = "tick()V", at = @At("HEAD"))
   private void farmAndCharm$tickFedStatus(CallbackInfo ci) {
      if (this.farmAndCharm$fedFromBowl) {
         this.farmAndCharm$fedTimer++;
         if (this.farmAndCharm$fedTimer >= 12000) {
            this.farmAndCharm$fedFromBowl = false;
            this.farmAndCharm$fedTimer = 0;
         }
      }
   }

   @Inject(
      method = "mobInteract(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/InteractionResult;",
      at = @At("RETURN"),
      cancellable = true
   )
   private void farmAndCharm$useCatFood(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
      InteractionResult ret = (InteractionResult)cir.getReturnValue();
      if (!ret.consumesAction()) {
         Cat self = (Cat)this;
         ItemStack stack = player.getItemInHand(hand);
         if (stack.is(((Item)ObjectRegistry.CAT_FOOD.get()).asItem())) {
            if (PlatformHelper.enableCatTamingChance()) {
               boolean changed = false;
               if (self.isTame()) {
                  if (self.getHealth() < self.getMaxHealth()) {
                     self.heal(4.0F);
                     changed = true;
                  }

                  if (!self.level().isClientSide && self.getAge() == 0 && !self.isInLove()) {
                     self.setInLove(player);
                     changed = true;
                  }
               } else {
                  if (!self.level().isClientSide && self.getRandom().nextInt(3) == 0) {
                     self.tame(player);
                     changed = true;
                  }

                  changed = true;
               }

               if (changed) {
                  if (!self.level().isClientSide) {
                     stack.consume(1, player);
                  }

                  self.gameEvent(GameEvent.EAT);
                  cir.setReturnValue(self.level().isClientSide ? InteractionResult.CONSUME : InteractionResult.SUCCESS);
               }
            }
         }
      }
   }

   @Override
   public void farmAndCharm$$resetFed() {
      this.farmAndCharm$fedFromBowl = false;
      this.farmAndCharm$fedTimer = 0;
   }

   @Override
   public void farmAndCharm$$markAsFed() {
      this.farmAndCharm$fedFromBowl = true;
      this.farmAndCharm$fedTimer = 0;
   }

   @Override
   public boolean farmAndCharm$$isFed() {
      return this.farmAndCharm$fedFromBowl;
   }
}
