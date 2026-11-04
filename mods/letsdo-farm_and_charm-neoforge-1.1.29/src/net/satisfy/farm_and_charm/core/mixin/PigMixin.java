package net.satisfy.farm_and_charm.core.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.satisfy.farm_and_charm.core.registry.ObjectRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Pig.class)
public abstract class PigMixin extends Animal {
   protected PigMixin(EntityType<? extends Animal> entityType, Level level) {
      super(entityType, level);
   }

   @Inject(method = "registerGoals()V", at = @At("RETURN"))
   private void farmAndCharm$addCustomFoodTempts(CallbackInfo ci) {
      this.goalSelector
         .addGoal(
            4,
            new TemptGoal(
               this,
               1.2,
               itemStack -> itemStack.is((Item)ObjectRegistry.TOMATO.get())
                  || itemStack.is((Item)ObjectRegistry.STRAWBERRY.get())
                  || itemStack.is((Item)ObjectRegistry.ONION.get())
                  || itemStack.is((Item)ObjectRegistry.LETTUCE.get())
                  || itemStack.is((Item)ObjectRegistry.BARLEY.get())
                  || itemStack.is((Item)ObjectRegistry.CORN.get())
                  || itemStack.is((Item)ObjectRegistry.OAT.get()),
               false
            )
         );
   }

   @Inject(method = "isFood(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
   private void addCustomFoodItems(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
      if (stack.is((Item)ObjectRegistry.TOMATO.get())
         || stack.is((Item)ObjectRegistry.STRAWBERRY.get())
         || stack.is((Item)ObjectRegistry.ONION.get())
         || stack.is((Item)ObjectRegistry.LETTUCE.get())
         || stack.is((Item)ObjectRegistry.BARLEY.get())
         || stack.is((Item)ObjectRegistry.CORN.get())
         || stack.is((Item)ObjectRegistry.OAT.get())) {
         cir.setReturnValue(true);
      }
   }
}
