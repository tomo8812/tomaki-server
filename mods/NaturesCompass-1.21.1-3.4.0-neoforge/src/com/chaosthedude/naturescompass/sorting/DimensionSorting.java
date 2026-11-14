package com.chaosthedude.naturescompass.sorting;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.chaosthedude.naturescompass.util.BiomeUtils;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

public class DimensionSorting implements ISorting<String> {
   private static final Minecraft mc = Minecraft.getInstance();

   @Override
   public int compare(Biome biome1, Biome biome2) {
      return this.getValue(biome1).compareTo(this.getValue(biome2));
   }

   public String getValue(Biome biome) {
      if (mc.level != null) {
         Optional<ResourceLocation> optionalBiomeKey = BiomeUtils.getKeyForBiome(mc.level, biome);
         if (optionalBiomeKey.isPresent()) {
            return BiomeUtils.dimensionKeysToString(NaturesCompass.dimensionKeysForAllowedBiomeKeys.get(optionalBiomeKey.get()));
         }
      }

      return "";
   }

   @Override
   public ISorting<?> next() {
      return new RainfallSorting();
   }

   @Override
   public String getLocalizedName() {
      return I18n.get("string.naturescompass.dimension", new Object[0]);
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
