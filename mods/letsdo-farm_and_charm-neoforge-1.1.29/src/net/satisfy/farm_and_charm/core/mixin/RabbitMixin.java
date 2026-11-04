package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Rabbit.class)
public class RabbitMixin {
   @Inject(method = "isFood(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
   private void addCustomFoodItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      if (stack.is((Item)ObjectRegistry.STRAWBERRY.get()) || stack.is((Item)ObjectRegistry.LETTUCE.get())) {
         cir.setReturnValue(true);
      }
   }
}
