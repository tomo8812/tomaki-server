package net.caffeinemc.mods.lithium.mixin.util.block_tracking;

import net.caffeinemc.mods.lithium.common.block.BlockStateFlagHolder;
import net.caffeinemc.mods.lithium.common.block.BlockStateFlags;
import net.caffeinemc.mods.lithium.common.block.TrackedBlockStatePredicate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.BlockStateBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = BlockStateBase.class, priority = 1010)
public class BlockStateBaseMixin implements BlockStateFlagHolder {
   @Unique
   private int flags;

   @Override
   public void lithium$initializeFlags() {
      TrackedBlockStatePredicate.FULLY_INITIALIZED.set(true);
      int flags = 0;

      for (int i = 0; i < BlockStateFlags.FLAGS.length; i++) {
         if (BlockStateFlags.FLAGS[i].test((BlockState)this)) {
            flags |= 1 << i;
         }
      }

      this.flags = flags;
   }

   @Override
   public int lithium$getAllFlags() {
      return this.flags;
   }
}
