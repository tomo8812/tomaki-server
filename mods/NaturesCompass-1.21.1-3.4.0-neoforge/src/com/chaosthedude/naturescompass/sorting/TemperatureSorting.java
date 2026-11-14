package com.chaosthedude.naturescompass.sorting;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TemperatureSorting implements ISorting<Float> {
   @Override
   public int compare(Biome biome1, Biome biome2) {
      return this.getValue(biome1).compareTo(this.getValue(biome2));
   }

   public Float getValue(Biome biome) {
      return biome.getBaseTemperature();
   }

   @Override
   public ISorting<?> next() {
      return new NameSorting();
   }

   @Override
   public String getLocalizedName() {
      return I18n.get("string.naturescompass.temperature", new Object[0]);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object getValue(Biome param1) {
      return this.getValue(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public int compare(Object param1, Object nullx) {
      return this.compare((Biome)var1, (Biome)nullx);
   }
}
