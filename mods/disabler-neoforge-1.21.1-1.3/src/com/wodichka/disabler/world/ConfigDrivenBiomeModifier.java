package com.wodichka.disabler.world;

import com.mojang.serialization.MapCodec;
import com.wodichka.disabler.config.DisablerConfig;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.MobSpawnSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier.Phase;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo.BiomeInfo.Builder;

public enum ConfigDrivenBiomeModifier implements BiomeModifier {
   INSTANCE;

   public void modify(Holder<Biome> biome, Phase phase, Builder builder) {
      if (phase == Phase.REMOVE) {
         ResourceLocation biomeId = biome.unwrapKey().<ResourceLocation>map(ResourceKey::location).orElse(null);
         boolean blockedBiome = biomeId != null && DisablerConfig.isBlockedBiome(biomeId);
         if (blockedBiome || DisablerConfig.hasBlockedMobs()) {
            MobSpawnSettingsBuilder spawnBuilder = builder.getMobSpawnSettings();

            for (MobCategory category : MobCategory.values()) {
               List<SpawnerData> spawns = spawnBuilder.getSpawner(category);
               if (blockedBiome) {
                  spawns.clear();
               } else {
                  spawns.removeIf(spawnerData -> DisablerConfig.isBlockedMob(spawnerData.type));
               }
            }
         }
      }
   }

   public MapCodec<? extends BiomeModifier> codec() {
      return (MapCodec<? extends BiomeModifier>)DisablerModifiers.CONFIG_SPAWN_BLOCKER.get();
   }

   // $VF: synthetic method
   private static ConfigDrivenBiomeModifier[] $values() {
      return new ConfigDrivenBiomeModifier[]{INSTANCE};
   }
}
