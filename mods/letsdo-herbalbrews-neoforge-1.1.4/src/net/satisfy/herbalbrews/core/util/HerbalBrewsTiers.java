package net.satisfy.herbalbrews.core.util;

import java.util.function.Supplier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public enum HerbalBrewsTiers implements Tier {
   GLASS(2, 5.0F, 2.0F, 12, () -> Ingredient.of(new ItemLike[]{Items.GLASS}));

   private final int maxUses;
   private final float miningSpeedMultiplier;
   private final float attackDamage;
   private final int enchantability;
   private final LazyLoadedValue<Ingredient> repairIngredient;

   HerbalBrewsTiers(int maxUses, float miningSpeedMultiplier, float attackDamage, int enchantability, Supplier<Ingredient> repairIngredient) {
      this.maxUses = maxUses;
      this.miningSpeedMultiplier = miningSpeedMultiplier;
      this.attackDamage = attackDamage;
      this.enchantability = enchantability;
      this.repairIngredient = new LazyLoadedValue(repairIngredient);
   }

   public int getUses() {
      return this.maxUses;
   }

   public float getSpeed() {
      return this.miningSpeedMultiplier;
   }

   public float getAttackDamageBonus() {
      return this.attackDamage;
   }

   public TagKey<Block> getIncorrectBlocksForDrops() {
      return BlockTags.INCORRECT_FOR_STONE_TOOL;
   }

   public int getEnchantmentValue() {
      return this.enchantability;
   }

   @NotNull
   public Ingredient getRepairIngredient() {
      return (Ingredient)this.repairIngredient.get();
   }

   // $VF: synthetic method
   private static HerbalBrewsTiers[] $values() {
      return new HerbalBrewsTiers[]{GLASS};
   }
}
