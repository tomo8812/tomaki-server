package org.embeddedt.modernfix.common.mixin.perf.ingredient_item_deduplication;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient.ItemValue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemValue.class)
public class IngredientItemValueMixin {
   @ModifyExpressionValue(
      method = "getItems",
      at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/crafting/Ingredient$ItemValue;item:Lnet/minecraft/world/item/ItemStack;")
   )
   private ItemStack mfix$defensiveCopy(ItemStack original) {
      return original.copy();
   }
}
