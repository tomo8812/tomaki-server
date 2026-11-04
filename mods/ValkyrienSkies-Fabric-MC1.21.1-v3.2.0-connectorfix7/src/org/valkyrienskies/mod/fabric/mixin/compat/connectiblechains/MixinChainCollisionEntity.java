package org.valkyrienskies.mod.fabric.mixin.compat.connectiblechains;

import com.github.legoatoom.connectiblechains.entity.ChainCollisionEntity;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

@Mixin(ChainCollisionEntity.class)
public abstract class MixinChainCollisionEntity extends class_1297 {
   @Inject(method = "canBeCollidedWith", at = @At("HEAD"), cancellable = true)
   private void disableOnShips(CallbackInfoReturnable<Boolean> cir) {
      if (VSGameUtilsKt.isBlockInShipyard(this.method_37908(), this.method_19538())) {
         cir.setReturnValue(false);
      }
   }

   public MixinChainCollisionEntity(class_1299<?> entityType, class_1937 level) {
      super(entityType, level);
   }
}
