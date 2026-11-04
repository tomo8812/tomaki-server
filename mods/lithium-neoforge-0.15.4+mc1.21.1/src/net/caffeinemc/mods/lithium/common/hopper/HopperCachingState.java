package net.caffeinemc.mods.lithium.common.hopper;

public class HopperCachingState {
   public enum BlockInventory {
      UNKNOWN,
      BLOCK_STATE,
      BLOCK_ENTITY,
      REMOVAL_TRACKING_BLOCK_ENTITY,
      NO_BLOCK_INVENTORY;

      // $VF: synthetic method
      private static HopperCachingState.BlockInventory[] $values() {
         return new HopperCachingState.BlockInventory[]{UNKNOWN, BLOCK_STATE, BLOCK_ENTITY, REMOVAL_TRACKING_BLOCK_ENTITY, NO_BLOCK_INVENTORY};
      }
   }
}
