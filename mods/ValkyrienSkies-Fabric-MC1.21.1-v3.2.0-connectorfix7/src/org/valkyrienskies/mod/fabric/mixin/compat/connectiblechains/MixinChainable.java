package org.valkyrienskies.mod.fabric.mixin.compat.connectiblechains;

import com.github.legoatoom.connectiblechains.entity.Chainable;
import com.github.legoatoom.connectiblechains.entity.Chainable.ChainData;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.class_1297;
import net.minecraft.class_1530;
import org.joml.Vector3dc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(Chainable.class)
public interface MixinChainable {
   @WrapOperation(method = "tickChain", at = @At(value = "INVOKE", target = "Lcom/github/legoatoom/connectiblechains/entity/Chainable;getMaxChainLength()D"))
   private static double adjustMaxChainLength(Operation<Double> original, @Local(argsOnly = true) class_1530 entity) {
      Ship ship = VSGameUtilsKt.getShipManaging(entity);
      if (ship != null) {
         Vector3dc scale = ship.getTransform().getScaling();
         return (Double)original.call(new Object[0]) * scale.get(scale.maxComponent());
      } else {
         return (Double)original.call(new Object[0]);
      }
   }

   @WrapWithCondition(
      method = "attachChain(Lnet/minecraft/world/entity/decoration/HangingEntity;Lcom/github/legoatoom/connectiblechains/entity/Chainable$ChainData;Lnet/minecraft/world/entity/Entity;Z)V",
      at = @At(
         value = "INVOKE",
         target = "Lcom/github/legoatoom/connectiblechains/entity/ChainCollisionEntity;createCollision(Lnet/minecraft/world/entity/Entity;Lcom/github/legoatoom/connectiblechains/entity/Chainable$ChainData;)V"
      )
   )
   private static boolean skipCreatingCollision(class_1297 entity, ChainData chainData) {
      return VSGameUtilsKt.getShipManaging(entity) == VSGameUtilsKt.getShipManaging(((Chainable)entity).getChainHolder(chainData));
   }
}
