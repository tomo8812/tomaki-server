package com.wodichka.disabler.world;

import com.mojang.serialization.MapCodec;
import com.wodichka.disabler.config.DisablerConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.neoforged.neoforge.common.world.StructureModifier;
import net.neoforged.neoforge.common.world.StructureSettingsBuilder;
import net.neoforged.neoforge.common.world.ModifiableStructureInfo.StructureInfo.Builder;
import net.neoforged.neoforge.common.world.StructureModifier.Phase;
import net.neoforged.neoforge.common.world.StructureSettingsBuilder.StructureSpawnOverrideBuilder;

public enum ConfigDrivenStructureModifier implements StructureModifier {
   INSTANCE;

   public void modify(Holder<Structure> structure, Phase phase, Builder builder) {
      if (phase == Phase.REMOVE) {
         ResourceLocation structureId = structure.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
         StructureSettingsBuilder settingsBuilder = builder.getStructureSettings();
         if (structureId != null && DisablerConfig.isBlockedStructure(structureId)) {
            clearStructure(settingsBuilder);
         } else {
            if (DisablerConfig.hasBlockedBiomes()) {
               List<Holder<Biome>> allowedBiomes = new ArrayList<>();

               for (Holder<Biome> biome : settingsBuilder.getBiomes()) {
                  ResourceLocation biomeId = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
                  if (biomeId != null && !DisablerConfig.isBlockedBiome(biomeId)) {
                     allowedBiomes.add(biome);
                  }
               }

               if (allowedBiomes.isEmpty()) {
                  clearStructure(settingsBuilder);
                  return;
               }

               settingsBuilder.setBiomes(HolderSet.direct(allowedBiomes));
            }

            if (DisablerConfig.hasBlockedMobs()) {
               for (MobCategory category : MobCategory.values()) {
                  StructureSpawnOverrideBuilder overrides = settingsBuilder.getSpawnOverrides(category);
                  if (overrides != null && !overrides.getSpawns().isEmpty()) {
                     overrides.removeSpawns(spawnerData -> DisablerConfig.isBlockedMob(spawnerData.type));
                  }
               }
            }
         }
      }
   }

   private static void clearStructure(StructureSettingsBuilder settingsBuilder) {
      settingsBuilder.setBiomes(HolderSet.empty());

      for (MobCategory category : MobCategory.values()) {
         settingsBuilder.removeSpawnOverrides(category);
      }
   }

   public MapCodec<? extends StructureModifier> codec() {
      return (MapCodec<? extends StructureModifier>)DisablerModifiers.CONFIG_STRUCTURE_BLOCKER.get();
   }

   // $VF: synthetic method
   private static ConfigDrivenStructureModifier[] $values() {
      return new ConfigDrivenStructureModifier[]{INSTANCE};
   }
}
