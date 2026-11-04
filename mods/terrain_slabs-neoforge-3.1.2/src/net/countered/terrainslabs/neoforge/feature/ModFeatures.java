package net.countered.terrainslabs.neoforge.feature;

import net.countered.terrainslabs.generation.SlabFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModFeatures {
   public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, "terrain_slabs");
   public static final DeferredHolder<Feature<?>, Feature<NoneFeatureConfiguration>> SLAB_FEATURE = FEATURES.register(
      "slab_feature", () -> new SlabFeature(NoneFeatureConfiguration.CODEC)
   );
}
