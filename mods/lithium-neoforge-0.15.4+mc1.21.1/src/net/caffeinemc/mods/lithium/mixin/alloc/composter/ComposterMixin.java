package net.caffeinemc.mods.lithium.mixin.alloc.composter;

import net.caffeinemc.mods.lithium.common.util.ArrayConstants;
import net.minecraft.core.Direction;
import net.minecraft.world.WorldlyContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

public class ComposterMixin {
   @Mixin(targets = "net/minecraft/world/level/block/ComposterBlock$InputContainer")
   abstract static class ComposterBlockComposterInventoryMixin implements WorldlyContainer {
      @Overwrite
      public int[] getSlotsForFace(Direction side) {
         return side == Direction.UP ? ArrayConstants.ZERO : ArrayConstants.EMPTY;
      }
   }

   @Mixin(targets = "net/minecraft/world/level/block/ComposterBlock$EmptyContainer")
   abstract static class ComposterBlockDummyInventoryMixin implements WorldlyContainer {
      @Overwrite
      public int[] getSlotsForFace(Direction side) {
         return ArrayConstants.EMPTY;
      }
   }

   @Mixin(targets = "net/minecraft/world/level/block/ComposterBlock$OutputContainer")
   abstract static class ComposterBlockFullComposterInventoryMixin implements WorldlyContainer {
      @Overwrite
      public int[] getSlotsForFace(Direction side) {
         return side == Direction.DOWN ? ArrayConstants.ZERO : ArrayConstants.EMPTY;
      }
   }
}
