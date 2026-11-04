package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.world.entity.animal.Cat.CatAvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatAvoidEntityGoal.class)
public class CatAvoidEntityGoalMixin {
   @Inject(method = "canUse()Z", at = @At("HEAD"), cancellable = true)
   private void modifyCanUse(CallbackInfoReturnable<Boolean> cir) {
      CatAvoidEntityGoal<?> goal = (CatAvoidEntityGoal<?>)this;
      if (goal.toAvoid instanceof Player p && p.isHolding(((Item)ObjectRegistry.CAT_FOOD.get()).asItem())) {
         cir.setReturnValue(false);
      }
   }

   @Inject(method = "canContinueToUse()Z", at = @At("HEAD"), cancellable = true)
   private void modifyCanContinueToUse(CallbackInfoReturnable<Boolean> cir) {
      CatAvoidEntityGoal<?> goal = (CatAvoidEntityGoal<?>)this;
      if (goal.toAvoid instanceof Player p && p.isHolding(((Item)ObjectRegistry.CAT_FOOD.get()).asItem())) {
         cir.setReturnValue(false);
      }
   }
}
