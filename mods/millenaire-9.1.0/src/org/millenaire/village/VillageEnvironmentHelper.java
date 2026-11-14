package org.millenaire.village;

import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.event.EventHooks;
import org.millenaire.block.AppleTreeSaplingBlock;
import org.millenaire.building.BuildingInstance;
import org.millenaire.building.SpecialPoint;
import org.millenaire.culture.ModCultures;
import org.millenaire.culture.VillageType;
import org.millenaire.goal.gathering.handler.SaplingPlantingHandler;
import org.slf4j.Logger;

public final class VillageEnvironmentHelper {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String PEN_TAG = "millenaire_pen";
   private static final Set<String> WARNED_UNKNOWN_ANIMALS = ConcurrentHashMap.newKeySet();
   private static final Set<EntityType<?>> NON_MOB_PEN_TYPES = ConcurrentHashMap.newKeySet();

   private VillageEnvironmentHelper() {
   }

   public static void despawnDangerousMobs(ServerLevel level, AABB dangerousMobsArea, Village village) {
      for (Monster mob : level.getEntitiesOfClass(
         Monster.class, dangerousMobsArea, e -> (e instanceof Creeper || e instanceof EnderMan) && !e.getTags().contains("millenaire_pen")
      )) {
         mob.discard();
      }

      List<BuildingInstance> allMobsBuildings = village.getOperationalBuildingsWithTag("despawnallmobs");
      if (!allMobsBuildings.isEmpty()) {
         VillageType vType = ModCultures.getVillageType(village.getVillageTypeId());
         int radius = vType != null ? vType.radius() : 90;

         for (BuildingInstance b : allMobsBuildings) {
            BlockPos origin = b.getOrigin();
            AABB box = new AABB(
               origin.getX() - radius, origin.getY() - 20, origin.getZ() - radius, origin.getX() + radius, origin.getY() + 50, origin.getZ() + radius
            );

            for (Monster mob : level.getEntitiesOfClass(Monster.class, box, e -> !e.getTags().contains("millenaire_pen"))) {
               mob.discard();
            }
         }
      }
   }

   public static void updatePens(ServerLevel level, Village village, boolean completeRespawn) {
      boolean isDay = level.isDay();
      if (!isDay || completeRespawn) {
         for (BuildingInstance building : village.getBuildings()) {
            if (building.isOperational()) {
               List<SpecialPoint> animalPoints = building.getPointsByType("animalSpawn");
               if (!animalPoints.isEmpty()) {
                  long lastSpawn = building.getLastAnimalSpawnTick();
                  if (completeRespawn || lastSpawn <= 0L || level.getGameTime() - lastSpawn >= 12000L) {
                     Map<EntityType<?>, List<BlockPos>> pointsByAnimal = new LinkedHashMap<>();
                     Set<EntityType<?>> genericGroups = new HashSet<>();

                     for (SpecialPoint sp : animalPoints) {
                        String animalType = sp.subtype() != null ? sp.subtype() : "cow";
                        EntityType<?> entityType = resolveAnimalType(animalType);
                        if (entityType != null) {
                           pointsByAnimal.computeIfAbsent(entityType, k -> new ArrayList<>()).add(sp.pos());
                           if (sp.isGeneric()) {
                              genericGroups.add(entityType);
                           }
                        }
                     }

                     boolean spawned = false;

                     for (Entry<EntityType<?>, List<BlockPos>> entry : pointsByAnimal.entrySet()) {
                        EntityType<?> entityType = entry.getKey();
                        List<BlockPos> spawnPositions = entry.getValue();
                        boolean generic = genericGroups.contains(entityType);
                        if (!generic || !NON_MOB_PEN_TYPES.contains(entityType)) {
                           int target = Math.min(spawnPositions.size(), 8);
                           BlockPos origin = building.getOrigin();
                           AABB area = new AABB(
                              origin.getX() - 15, origin.getY() - 20, origin.getZ() - 15, origin.getX() + 15, origin.getY() + 20, origin.getZ() + 15
                           );
                           int existing = generic
                              ? countGenericPenHeads(level, area, entityType)
                              : level.getEntitiesOfClass(Animal.class, area, e -> e.getType() == entityType && !e.isBaby()).size();
                           if (existing < target) {
                              for (int i = existing; i < target; i++) {
                                 if (completeRespawn || level.random.nextInt(10) == 0) {
                                    BlockPos spawnPos = spawnPositions.get(level.random.nextInt(spawnPositions.size()));
                                    Entity animal = entityType.create(level);
                                    if (animal != null) {
                                       animal.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, level.random.nextFloat() * 360.0F, 0.0F);
                                       if (generic) {
                                          if (!(animal instanceof Mob mob)) {
                                             if (NON_MOB_PEN_TYPES.add(entityType)) {
                                                LOGGER.warn(
                                                   "Generic animal spawn '{}' is not a mob and can never be counted — its spawn points are skipped",
                                                   EntityType.getKey(entityType)
                                                );
                                             }
                                             break;
                                          }

                                          preparePenMob(level, mob, spawnPos);
                                       }

                                       level.addFreshEntity(animal);
                                       spawned = true;
                                    }
                                 }
                              }
                           }
                        }
                     }

                     if (spawned) {
                        building.setLastAnimalSpawnTick(level.getGameTime());
                     }
                  }
               }
            }
         }
      }
   }

   @Nullable
   static EntityType<?> resolveAnimalType(String animalType) {
      if (SpecialPoint.isGenericSubtype(animalType)) {
         EntityType<?> type = (EntityType<?>)EntityType.byString(animalType).orElse(null);
         if (type == null && WARNED_UNKNOWN_ANIMALS.add(animalType)) {
            LOGGER.warn("Generic animal spawn '{}' is not a known entity type (missing mod?) — its spawn points are skipped", animalType);
         }

         return type;
      } else {
         return (EntityType<?>)EntityType.byString("minecraft:" + animalType).orElse(null);
      }
   }

   private static int countGenericPenHeads(ServerLevel level, AABB area, EntityType<?> entityType) {
      return level.getEntitiesOfClass(Mob.class, area, e -> e.getType() == entityType && !(e instanceof AgeableMob ageable && ageable.isBaby())).size();
   }

   private static void preparePenMob(ServerLevel level, Mob mob, BlockPos spawnPos) {
      EventHooks.finalizeMobSpawn(mob, level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.STRUCTURE, null);
      mob.setPersistenceRequired();
      mob.addTag("millenaire_pen");
   }

   public static void tickGroveSaplings(ServerLevel level, Village village) {
      for (BuildingInstance building : village.getOperationalBuildingsWithTag("grove")) {
         for (SpecialPoint sp : building.getPointsByType("treeSpawn")) {
            if (level.random.nextInt(200) == 0) {
               BlockPos saplingPos = sp.pos();
               if (level.isLoaded(saplingPos)) {
                  BlockState state = level.getBlockState(saplingPos);
                  if (state.getBlock() instanceof SaplingBlock saplingBlock) {
                     saplingBlock.advanceTree(level, saplingPos, state, level.random);
                  } else if (state.getBlock() instanceof AppleTreeSaplingBlock appleTreeSapling) {
                     appleTreeSapling.advanceTree(level, saplingPos, state, level.random);
                  } else if (sp.isGeneric()
                     && state.getBlock() instanceof BonemealableBlock bonemealable
                     && state.is(SaplingPlantingHandler.resolveSaplingFromSubtype(sp.subtype()))
                     && bonemealable.isValidBonemealTarget(level, saplingPos, state)) {
                     bonemealable.performBonemeal(level, level.random, saplingPos, state);
                  }
               }
            }
         }
      }
   }
}
