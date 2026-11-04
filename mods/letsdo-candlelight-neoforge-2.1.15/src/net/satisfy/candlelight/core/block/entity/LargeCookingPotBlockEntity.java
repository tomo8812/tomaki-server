package net.satisfy.candlelight.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.satisfy.candlelight.core.block.LargeCookingPotBlock;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import net.satisfy.candlelight.core.util.Wearables;
import net.satisfy.candlelight.platform.PlatformHelper;
import net.satisfy.farm_and_charm.core.block.entity.CookingPotBlockEntity;

public class LargeCookingPotBlockEntity extends CookingPotBlockEntity {
   public LargeCookingPotBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.LARGE_COOKING_POT_BLOCK_ENTITY.get(), pos, state);
   }

   protected BooleanProperty getLitProperty() {
      return LargeCookingPotBlock.LIT;
   }

   protected BooleanProperty getCookingProperty() {
      return LargeCookingPotBlock.COOKING;
   }

   protected int getExtraOutputCount(ItemStack output) {
      Player owner = this.getOwner();
      if (owner != null && this.level != null && Wearables.hasChefSet(owner)) {
         return this.level.random.nextFloat() < PlatformHelper.chefSetExtraPortionChance() ? 1 : 0;
      } else {
         return 0;
      }
   }
}
