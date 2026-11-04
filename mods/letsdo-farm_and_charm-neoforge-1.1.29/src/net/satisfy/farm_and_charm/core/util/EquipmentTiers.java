package net.satisfy.farm_and_charm.core.util;

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

public enum EquipmentTiers implements Tier {
   COPPER(2, 200, 5.0F, 2.0F, 12, () -> Ingredient.of(new ItemLike[]{Items.COPPER_INGOT}));

   private final int level;
   private final int uses;
   private final float speed;
   private final float damage;
   private final int enchantmentValue;
   private final LazyLoadedValue<Ingredient> repairIngredient;

   EquipmentTiers(int j, int k, float f, float g, int l, Supplier<Ingredient> supplier) {
      this.level = j;
      this.uses = k;
      this.speed = f;
      this.damage = g;
      this.enchantmentValue = l;
      this.repairIngredient = new LazyLoadedValue(supplier);
   }

   public int getUses() {
      return this.uses;
   }

   public float getSpeed() {
      return this.speed;
   }

   public float getAttackDamageBonus() {
      return this.damage;
   }

   @NotNull
   public TagKey<Block> getIncorrectBlocksForDrops() {
      return BlockTags.INCORRECT_FOR_STONE_TOOL;
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   @NotNull
   public Ingredient getRepairIngredient() {
      return (Ingredient)this.repairIngredient.get();
   }

   // $VF: synthetic method
   private static EquipmentTiers[] $values() {
      return new EquipmentTiers[]{COPPER};
   }
}
