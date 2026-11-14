package com.chaosthedude.naturescompass.sorting;

import com.chaosthedude.naturescompass.util.BiomeUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SourceSorting implements ISorting<String> {
   private static final Minecraft mc = Minecraft.getInstance();

   @Override
   public int compare(Biome biome1, Biome biome2) {
      return this.getValue(biome1).compareTo(this.getValue(biome2));
   }

   public String getValue(Biome biome) {
      return mc.level != null ? BiomeUtils.getBiomeSource(mc.level, biome) : null;
   }

   @Override
   public ISorting<?> next() {
      return new TagsSorting();
   }

   @Override
   public String getLocalizedName() {
      return I18n.get("string.naturescompass.source", new Object[0]);
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
