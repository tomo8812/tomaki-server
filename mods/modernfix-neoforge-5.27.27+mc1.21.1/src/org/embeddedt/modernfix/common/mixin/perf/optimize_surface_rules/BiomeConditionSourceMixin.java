package org.embeddedt.modernfix.common.mixin.perf.optimize_surface_rules;

import java.util.List;
import java.util.Set;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.SurfaceRules.BiomeConditionSource;
import net.minecraft.world.level.levelgen.SurfaceRules.Condition;
import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import org.embeddedt.modernfix.world.gen.ExtendedSurfaceContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BiomeConditionSource.class)
public class BiomeConditionSourceMixin {
   @Shadow
   @Final
   public List<ResourceKey<Biome>> biomes;

   @Inject(
      method = "apply(Lnet/minecraft/world/level/levelgen/SurfaceRules$Context;)Lnet/minecraft/world/level/levelgen/SurfaceRules$Condition;",
      at = @At("HEAD"),
      cancellable = true
   )
   private void mfix$optimizeCondition(Context p_context, CallbackInfoReturnable<Condition> cir) {
      Set<ResourceKey<Biome>> possibleBiomes = ((ExtendedSurfaceContext)p_context).mfix$getPossibleBiomes();
      if (possibleBiomes != null) {
         if (this.mfix$guaranteedNoMatch(possibleBiomes)) {
            cir.setReturnValue((Condition)() -> false);
         } else if (this.mfix$alwaysMatches(possibleBiomes)) {
            cir.setReturnValue((Condition)() -> true);
         }
      }
   }

   private boolean mfix$guaranteedNoMatch(Set<ResourceKey<Biome>> possibleBiomesInChunk) {
      List<ResourceKey<Biome>> testBiomes = this.biomes;

      for (int i = 0; i < testBiomes.size(); i++) {
         if (possibleBiomesInChunk.contains(testBiomes.get(i))) {
            return false;
         }
      }

      return true;
   }

   private boolean mfix$alwaysMatches(Set<ResourceKey<Biome>> possibleBiomesInChunk) {
      List<ResourceKey<Biome>> testBiomes = this.biomes;

      for (ResourceKey<Biome> biome : possibleBiomesInChunk) {
         if (!testBiomes.contains(biome)) {
            return false;
         }
      }

      return true;
   }
}
