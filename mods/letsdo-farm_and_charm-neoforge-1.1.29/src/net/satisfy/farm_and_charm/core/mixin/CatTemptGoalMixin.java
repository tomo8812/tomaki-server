package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.world.entity.animal.Cat.CatTemptGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CatTemptGoal.class)
public class CatTemptGoalMixin {
   @Shadow
   @Nullable
   private Player selectedPlayer;

   @Inject(method = "canScare()Z", at = @At("HEAD"), cancellable = true)
   private void modifyCanScare(CallbackInfoReturnable<Boolean> cir) {
      if (this.selectedPlayer != null && this.selectedPlayer.isHolding(((Item)ObjectRegistry.CAT_FOOD.get()).asItem())) {
         cir.setReturnValue(false);
      }
   }
}
