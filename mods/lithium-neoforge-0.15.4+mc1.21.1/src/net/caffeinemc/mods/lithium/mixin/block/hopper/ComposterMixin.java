package net.caffeinemc.mods.lithium.mixin.block.hopper;

import net.caffeinemc.mods.lithium.common.hopper.BlockStateOnlyInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class ComposterMixin {
   @Mixin(targets = "net/minecraft/world/level/block/ComposterBlock$InputContainer")
   abstract static class ComposterBlockComposterInventoryMixin implements BlockStateOnlyInventory {
      @Shadow
      private boolean changed;

      @Inject(
         method = "setChanged",
         at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/ComposterBlock$InputContainer;removeItemNoUpdate(I)Lnet/minecraft/world/item/ItemStack;"
         )
      )
      private void resetDirty(CallbackInfo ci) {
         this.changed = false;
      }
   }

   @Mixin(targets = "net/minecraft/world/level/block/ComposterBlock$EmptyContainer")
   abstract static class ComposterBlockDummyInventoryMixin implements BlockStateOnlyInventory {
   }

   @Mixin(targets = "net/minecraft/world/level/block/ComposterBlock$OutputContainer")
   abstract static class ComposterBlockFullComposterInventoryMixin implements BlockStateOnlyInventory {
   }
}
