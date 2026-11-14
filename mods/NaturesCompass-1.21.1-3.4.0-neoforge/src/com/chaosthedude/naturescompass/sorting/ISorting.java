package com.chaosthedude.naturescompass.sorting;

import java.util.Comparator;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISorting<T> extends Comparator<Biome> {
   int compare(Biome var1, Biome var2);

   T getValue(Biome var1);

   ISorting<?> next();

   String getLocalizedName();

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   default int compare(Object param1, Object nullx) {
      return this.compare((Biome)var1, (Biome)nullx);
   }
}
