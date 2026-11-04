package net.satisfy.candlelight.core.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.satisfy.candlelight.core.block.CookingPanBlock;
import net.satisfy.candlelight.core.registry.EntityTypeRegistry;
import net.satisfy.candlelight.core.util.Wearables;
import net.satisfy.candlelight.platform.PlatformHelper;
import net.satisfy.farm_and_charm.core.block.entity.RoasterBlockEntity;
import org.jetbrains.annotations.NotNull;

public class CookingPanBlockEntity extends RoasterBlockEntity {
   private int storedDamage;

   public CookingPanBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.COOKING_PAN_BLOCK_ENTITY.get(), pos, state);
   }

   protected BooleanProperty getLitProperty() {
      return CookingPanBlock.LIT;
   }

   protected BooleanProperty getRoastingProperty() {
      return CookingPanBlock.COOKING;
   }

   protected void loadAdditional(CompoundTag tag, Provider provider) {
      super.loadAdditional(tag, provider);
      this.storedDamage = tag.getInt("StoredDamage");
   }

   protected void saveAdditional(@NotNull CompoundTag tag, Provider provider) {
      super.saveAdditional(tag, provider);
      tag.putInt("StoredDamage", this.storedDamage);
   }

   public int getStoredDamage() {
      return this.storedDamage;
   }

   public void setStoredDamage(int storedDamage) {
      this.storedDamage = storedDamage;
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
