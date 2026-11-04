package net.caffeinemc.mods.lithium.mixin.collections.mob_spawning;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.biome.MobSpawnSettings.MobSpawnCost;
import net.minecraft.world.level.biome.MobSpawnSettings.SpawnerData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobSpawnSettings.class)
public class MobSpawnSettingsMixin {
   @Mutable
   @Shadow
   @Final
   private Map<MobCategory, WeightedRandomList<SpawnerData>> spawners;

   @Inject(method = "<init>(FLjava/util/Map;Ljava/util/Map;)V", at = @At("RETURN"))
   private void reinit(
      float creatureSpawnProbability, Map<MobCategory, WeightedRandomList<SpawnerData>> spawners, Map<EntityType<?>, MobSpawnCost> spawnCosts, CallbackInfo ci
   ) {
      Map<MobCategory, WeightedRandomList<SpawnerData>> spawns = Maps.newEnumMap(MobCategory.class);

      for (Entry<MobCategory, WeightedRandomList<SpawnerData>> entry : this.spawners.entrySet()) {
         spawns.put(entry.getKey(), entry.getValue());
      }

      this.spawners = spawns;
   }
}
