package net.satisfy.herbalbrews.core.blocks.entity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Clearable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.satisfy.herbalbrews.core.items.DrinkBlockItem;
import net.satisfy.herbalbrews.core.registry.EntityTypeRegistry;

public class JugBlockEntity extends BlockEntity implements Clearable {
   private final List<ItemStack> drinks = new ArrayList<>();

   public JugBlockEntity(BlockPos pos, BlockState state) {
      super((BlockEntityType)EntityTypeRegistry.JUG_BLOCK_ENTITY.get(), pos, state);
   }

   public void addDrink(ItemStack drink) {
      if (this.drinks.size() < 3 && drink.getItem() instanceof DrinkBlockItem) {
         this.drinks.add(drink.copy());
         this.setChanged();
         if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
         }
      }
   }

   public List<ItemStack> getDrinks() {
      return this.drinks;
   }

   public void clearDrinks() {
      this.drinks.clear();
      this.setChanged();
      if (this.level != null) {
         this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
      }
   }

   public void clearContent() {
      this.drinks.clear();
   }

   protected void saveAdditional(CompoundTag compoundTag, Provider provider) {
      super.saveAdditional(compoundTag, provider);
      ListTag drinkList = new ListTag();

      for (ItemStack drink : this.drinks) {
         drinkList.add(drink.save(provider, new CompoundTag()));
      }

      compoundTag.put("Drinks", drinkList);
   }

   protected void loadAdditional(CompoundTag compoundTag, Provider provider) {
      super.loadAdditional(compoundTag, provider);
      this.drinks.clear();
      if (compoundTag.contains("Drinks", 9)) {
         ListTag drinkList = compoundTag.getList("Drinks", 10);

         for (int i = 0; i < drinkList.size(); i++) {
            CompoundTag drinkTag = drinkList.getCompound(i);
            ItemStack drink = ItemStack.parseOptional(provider, drinkTag);
            this.drinks.add(drink);
         }
      }
   }

   public void applyEffects(LivingEntity user, int durationTicks) {
      for (ItemStack drink : this.drinks) {
         if (drink.getItem() instanceof DrinkBlockItem) {
            PotionContents data = (PotionContents)drink.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
            if (data.hasEffects()) {
               data.forEachEffect(mobEffectInstance -> {
                  mobEffectInstance.duration = durationTicks;
                  user.addEffect(mobEffectInstance);
               });
            }
         }
      }
   }
}
