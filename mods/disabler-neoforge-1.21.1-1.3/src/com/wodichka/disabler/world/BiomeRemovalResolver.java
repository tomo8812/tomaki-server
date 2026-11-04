package com.wodichka.disabler.world;

import com.mojang.datafixers.util.Pair;
import com.wodichka.disabler.config.DisablerConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Climate.ParameterList;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.Climate.TargetPoint;

public final class BiomeRemovalResolver {
   private BiomeRemovalResolver() {
   }

   public static Holder<Biome> resolveReplacement(Holder<Biome> selectedBiome, TargetPoint targetPoint, List<BiomeRemovalResolver.Candidate> possibleBiomes) {
      if (DisablerConfig.hasBlockedBiomes() && isBlocked(selectedBiome)) {
         BiomeRemovalResolver.SurfaceKind selectedKind = BiomeRemovalResolver.SurfaceKind.fromBiome(selectedBiome);
         return possibleBiomes.stream()
            .filter(candidate -> candidate.surfaceKind() == selectedKind)
            .min(Comparator.comparingLong(candidate -> candidate.distanceTo(targetPoint)))
            .map(BiomeRemovalResolver.Candidate::biome)
            .orElse(selectedBiome);
      } else {
         return selectedBiome;
      }
   }

   public static List<BiomeRemovalResolver.Candidate> collectAllowedBiomes(ParameterList<Holder<Biome>> parameterList) {
      List<BiomeRemovalResolver.Candidate> allowedBiomes = new ArrayList<>();

      for (Pair<ParameterPoint, Holder<Biome>> entry : parameterList.values()) {
         Holder<Biome> biome = (Holder<Biome>)entry.getSecond();
         ResourceLocation biomeId = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
         if (!isBlocked(biome) && biomeId != null && !getBiomeExceptionIds().contains(biomeId)) {
            allowedBiomes.add(new BiomeRemovalResolver.Candidate(biome, (ParameterPoint)entry.getFirst(), BiomeRemovalResolver.SurfaceKind.fromId(biomeId)));
         }
      }

      return List.copyOf(allowedBiomes);
   }

   private static Set<ResourceLocation> getBiomeExceptionIds() {
      return DisablerConfig.getBiomeExceptionIds();
   }

   public static boolean isBlocked(Holder<Biome> biome) {
      ResourceLocation biomeId = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
      return biomeId != null && DisablerConfig.isBlockedBiome(biomeId);
   }

   public record Candidate(Holder<Biome> biome, ParameterPoint parameterPoint, BiomeRemovalResolver.SurfaceKind surfaceKind) {
      private long distanceTo(TargetPoint targetPoint) {
         return square(this.parameterPoint.temperature().distance(targetPoint.temperature()))
            + square(this.parameterPoint.humidity().distance(targetPoint.humidity()))
            + square(this.parameterPoint.continentalness().distance(targetPoint.continentalness()))
            + square(this.parameterPoint.erosion().distance(targetPoint.erosion()))
            + square(this.parameterPoint.depth().distance(targetPoint.depth()))
            + square(this.parameterPoint.weirdness().distance(targetPoint.weirdness()));
      }

      private static long square(long value) {
         return value * value;
      }
   }

   public enum SurfaceKind {
      LAND,
      WATER;

      private static BiomeRemovalResolver.SurfaceKind fromBiome(Holder<Biome> biome) {
         ResourceLocation biomeId = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
         return biomeId == null ? LAND : fromId(biomeId);
      }

      private static BiomeRemovalResolver.SurfaceKind fromId(ResourceLocation biomeId) {
         String path = biomeId.getPath().toLowerCase(Locale.ROOT);
         return !path.contains("ocean")
               && !path.contains("river")
               && !path.contains("lake")
               && !path.contains("water")
               && !path.contains("reef")
               && !path.contains("kelp")
               && !path.contains("coral")
            ? LAND
            : WATER;
      }

      // $VF: synthetic method
      private static BiomeRemovalResolver.SurfaceKind[] $values() {
         return new BiomeRemovalResolver.SurfaceKind[]{LAND, WATER};
      }
   }
}
