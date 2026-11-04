package dev.emi.emi.bom;

import dev.emi.emi.api.stack.EmiIngredient;

public class ChanceMaterialCost extends FlatMaterialCost {
   public long minBatch = 1L;
   public float chance;

   public ChanceMaterialCost(EmiIngredient ingredient, long amount, float chance) {
      super(ingredient, amount);
      this.chance = chance;
   }

   public void merge(long amount, float chance) {
      long sum = this.amount + amount;
      this.chance = (this.chance * (float)this.amount + chance * (float)amount) / (float)sum;
      this.amount += amount;
   }

   public void minBatch(long minBatch) {
      this.minBatch = Math.max(this.minBatch, minBatch);
   }

   @Override
   public long getEffectiveAmount() {
      return Math.max(this.minBatch, Math.round((float)this.amount * this.chance));
   }
}
