package org.embeddedt.modernfix.common.mixin.perf.ingredient_item_deduplication;

import java.util.stream.Stream;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Ingredient.Value;
import org.embeddedt.modernfix.neoforge.recipe.IngredientValueDeduplicator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Ingredient.class)
public class IngredientMixin {
   @ModifyVariable(method = "<init>(Ljava/util/stream/Stream;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private static Stream<? extends Value> injectDeduplicationPass(Stream<? extends Value> stream) {
      return stream.map(IngredientValueDeduplicator::deduplicate);
   }

   @ModifyVariable(method = "<init>([Lnet/minecraft/world/item/crafting/Ingredient$Value;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
   private static Value[] injectDeduplicationPassArray(Value[] values) {
      if (values.length == 0) {
         return values;
      }

      Value[] newValues = new Value[values.length];

      for (int i = 0; i < values.length; i++) {
         newValues[i] = IngredientValueDeduplicator.deduplicate(values[i]);
      }

      return newValues;
   }
}
