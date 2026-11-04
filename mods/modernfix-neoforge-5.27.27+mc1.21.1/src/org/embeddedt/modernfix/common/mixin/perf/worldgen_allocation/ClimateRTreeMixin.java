package org.embeddedt.modernfix.common.mixin.perf.worldgen_allocation;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.Climate.RTree;
import org.embeddedt.modernfix.annotation.FeatureLevel;
import org.embeddedt.modernfix.annotation.RequiresFeatureLevel;
import org.embeddedt.modernfix.world.gen.ClimateRTreeBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RTree.class)
@RequiresFeatureLevel(FeatureLevel.BETA)
public class ClimateRTreeMixin {
   @Overwrite
   public static <T> RTree<T> create(List<Pair<ParameterPoint, T>> points) {
      if (points.isEmpty()) {
         throw new IllegalArgumentException("Need at least one value to build the search tree.");
      } else {
         return new RTree(ClimateRTreeBuilder.build(points));
      }
   }
}
