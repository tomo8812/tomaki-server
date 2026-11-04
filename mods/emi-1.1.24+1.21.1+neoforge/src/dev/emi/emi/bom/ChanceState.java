package dev.emi.emi.bom;

public record ChanceState(float chance, boolean chanced) {
   public static ChanceState DEFAULT = new ChanceState(1.0F, false);

   public ChanceState produce(float chance) {
      return chance == 1.0F ? this : new ChanceState(this.chance / chance, true);
   }

   public ChanceState consume(float chance) {
      return chance == 1.0F ? this : new ChanceState(this.chance * chance, true);
   }
}
