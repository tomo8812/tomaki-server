package org.embeddedt.modernfix.common.mixin.perf.optimize_surface_rules;

import java.util.Set;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import org.embeddedt.modernfix.world.gen.ExtendedSurfaceContext;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Context.class)
public class SurfaceRulesContextMixin implements ExtendedSurfaceContext {
   @Nullable
   Set<ResourceKey<Biome>> mfix$possibleBiomes;

   @Override
   public void mfix$applyPossibleBiomes() {
      this.mfix$possibleBiomes = ExtendedSurfaceContext.COMPUTED_POSSIBLE_BIOMES.get();
      ExtendedSurfaceContext.COMPUTED_POSSIBLE_BIOMES.remove();
   }

   @Nullable
   @Override
   public Set<ResourceKey<Biome>> mfix$getPossibleBiomes() {
      return this.mfix$possibleBiomes;
   }
}
