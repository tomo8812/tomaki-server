package com.chaosthedude.naturescompass.util;

import com.chaosthedude.naturescompass.NaturesCompass;
import com.chaosthedude.naturescompass.config.ConfigHandler;
import com.chaosthedude.naturescompass.items.NaturesCompassItem;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.WorldWorkerManager;
import net.neoforged.neoforge.common.WorldWorkerManager.IWorker;
import org.apache.commons.lang3.RandomStringUtils;

public class BiomeSearchWorker implements IWorker {
   private final String id = RandomStringUtils.random(8, "0123456789abcdef");
   private final int sampleSpace;
   private final int maxSamples;
   private final int maxRadius;
   private ServerLevel level;
   private ResourceLocation biomeKey;
   private BlockPos startPos;
   private int samples;
   private int nextLength;
   private Direction direction;
   private ItemStack stack;
   private Player player;
   private int x;
   private int z;
   private int[] yValues;
   private int length;
   private boolean finished;
   private int lastRadiusThreshold;
   private List<BlockPos> prevPos;
   private boolean checkingConnections;
   private BlockPos candidatePos;
   private int prevPosIndex;
   private int connectionsCheckSampleIndex;
   private int connectionsCheckSamples;
   private int consecutiveNonMatchingSamples;

   public BiomeSearchWorker(ServerLevel level, Player player, ItemStack stack, ResourceLocation biomeKey, BlockPos startPos, List<BlockPos> prevPos) {
      this.level = level;
      this.player = player;
      this.stack = stack;
      this.biomeKey = biomeKey;
      this.startPos = startPos;
      this.prevPos = prevPos;
      this.x = startPos.getX();
      this.z = startPos.getZ();
      this.yValues = Mth.outFromOrigin(startPos.getY(), level.getMinBuildHeight() + 1, level.getMaxBuildHeight(), 64).toArray();
      this.sampleSpace = (Integer)ConfigHandler.GENERAL.sampleSpaceModifier.get() * BiomeUtils.getBiomeSize(level);
      this.maxSamples = (Integer)ConfigHandler.GENERAL.maxSamples.get();
      this.maxRadius = (Integer)ConfigHandler.GENERAL.radiusModifier.get() * BiomeUtils.getBiomeSize(level);
      this.nextLength = this.sampleSpace;
      this.length = 0;
      this.samples = 0;
      this.direction = Direction.UP;
      this.finished = false;
      this.lastRadiusThreshold = 0;
   }

   public void start() {
      if (!this.stack.isEmpty() && this.stack.getItem() == NaturesCompass.naturesCompass) {
         if (this.maxRadius > 0 && this.sampleSpace > 0) {
            NaturesCompass.LOGGER
               .info(
                  "BiomeSearchWorker "
                     + this.id
                     + ": Starting search: "
                     + this.sampleSpace
                     + " sample space, "
                     + this.maxSamples
                     + " max samples, "
                     + this.maxRadius
                     + " max radius, "
                     + this.prevPos.size()
                     + " previous locations"
               );
            WorldWorkerManager.addWorker(this);
         } else {
            this.fail();
         }
      }
   }

   public boolean hasWork() {
      return this.biomeKey != null && !this.finished && this.getRadius() <= this.maxRadius && this.samples <= this.maxSamples;
   }

   public boolean doWork() {
      if (this.checkingConnections) {
         this.checkConnections();
         return !this.finished;
      }

      if (this.hasWork()) {
         if (this.direction == Direction.NORTH) {
            this.z = this.z - this.sampleSpace;
         } else if (this.direction == Direction.EAST) {
            this.x = this.x + this.sampleSpace;
         } else if (this.direction == Direction.SOUTH) {
            this.z = this.z + this.sampleSpace;
         } else if (this.direction == Direction.WEST) {
            this.x = this.x - this.sampleSpace;
         }

         int sampleX = QuartPos.fromBlock(this.x);
         int sampleZ = QuartPos.fromBlock(this.z);

         for (int y : this.yValues) {
            int sampleY = QuartPos.fromBlock(y);
            Biome biomeAtPos = (Biome)this.level
               .getChunkSource()
               .getGenerator()
               .getBiomeSource()
               .getNoiseBiome(sampleX, sampleY, sampleZ, this.level.getChunkSource().randomState().sampler())
               .value();
            Optional<ResourceLocation> optionalBiomeAtPosKey = BiomeUtils.getKeyForBiome(this.level, biomeAtPos);
            if (optionalBiomeAtPosKey.isPresent() && optionalBiomeAtPosKey.get().equals(this.biomeKey)) {
               if (this.prevPos.isEmpty()) {
                  this.prevPos.add(new BlockPos(this.x, y, this.z));
                  this.succeed();
                  return false;
               }

               this.checkingConnections = true;
               this.candidatePos = new BlockPos(this.x, y, this.z);
               this.prevPosIndex = 0;
               this.connectionsCheckSampleIndex = 0;
               return true;
            }
         }

         this.finishSample();
      }

      if (this.hasWork()) {
         return true;
      }

      if (!this.finished) {
         this.fail();
      }

      return false;
   }

   private void checkConnections() {
      if (this.prevPosIndex >= this.prevPos.size()) {
         this.prevPos.add(this.candidatePos);
         this.checkingConnections = false;
         this.succeed();
      } else {
         BlockPos currentPrevPos = this.prevPos.get(this.prevPosIndex);
         int deltaX = this.candidatePos.getX() - currentPrevPos.getX();
         int deltaZ = this.candidatePos.getZ() - currentPrevPos.getZ();
         if (this.connectionsCheckSampleIndex == 0) {
            double distance = Math.sqrt((double)(deltaX * deltaX) + deltaZ * deltaZ);
            if (distance <= this.sampleSpace * 2) {
               this.checkingConnections = false;
               this.finishSample();
            } else {
               this.connectionsCheckSamples = (int)(distance / this.sampleSpace);
               this.consecutiveNonMatchingSamples = 0;
               this.connectionsCheckSampleIndex = 1;
            }
         } else if (this.connectionsCheckSampleIndex >= this.connectionsCheckSamples) {
            this.checkingConnections = false;
            this.finishSample();
         } else {
            double t = (double)this.connectionsCheckSampleIndex / this.connectionsCheckSamples;
            int checkSampleX = QuartPos.fromBlock((int)(currentPrevPos.getX() + deltaX * t));
            int checkSampleZ = QuartPos.fromBlock((int)(currentPrevPos.getZ() + deltaZ * t));
            boolean foundMatchingBiome = false;

            for (int y : this.yValues) {
               int checkSampleY = QuartPos.fromBlock(y);
               Biome biomeAtCheck = (Biome)this.level
                  .getChunkSource()
                  .getGenerator()
                  .getBiomeSource()
                  .getNoiseBiome(checkSampleX, checkSampleY, checkSampleZ, this.level.getChunkSource().randomState().sampler())
                  .value();
               Optional<ResourceLocation> checkBiomeKey = BiomeUtils.getKeyForBiome(this.level, biomeAtCheck);
               if (checkBiomeKey.isPresent() && checkBiomeKey.get().equals(this.biomeKey)) {
                  foundMatchingBiome = true;
                  break;
               }
            }

            if (foundMatchingBiome) {
               this.consecutiveNonMatchingSamples = 0;
            } else {
               this.consecutiveNonMatchingSamples++;
               int consecutiveNonMatchingThreshold = Math.clamp(this.connectionsCheckSamples / 4, 1, 10);
               if (this.consecutiveNonMatchingSamples >= consecutiveNonMatchingThreshold) {
                  this.prevPosIndex++;
                  this.connectionsCheckSampleIndex = 0;
                  this.consecutiveNonMatchingSamples = 0;
                  return;
               }
            }

            this.connectionsCheckSampleIndex++;
         }
      }
   }

   private void finishSample() {
      this.samples++;
      this.length = this.length + this.sampleSpace;
      if (this.length >= this.nextLength) {
         if (this.direction == Direction.UP) {
            this.direction = Direction.NORTH;
         } else {
            this.nextLength = this.nextLength + this.sampleSpace;
            this.direction = this.direction.getClockWise();
         }

         this.length = 0;
      }

      int radius = this.getRadius();
      if (radius > 500 && radius / 500 > this.lastRadiusThreshold) {
         if (!this.stack.isEmpty() && this.stack.getItem() == NaturesCompass.naturesCompass) {
            ((NaturesCompassItem)this.stack.getItem()).setSearchRadius(this.stack, this.roundRadius(radius, 500), this.player);
         }

         this.lastRadiusThreshold = radius / 500;
      }
   }

   private void succeed() {
      NaturesCompass.LOGGER.info("BiomeSearchWorker " + this.id + ": Search succeeded: " + this.getRadius() + " radius, " + this.samples + " samples");
      if (!this.stack.isEmpty() && this.stack.getItem() == NaturesCompass.naturesCompass) {
         ((NaturesCompassItem)this.stack.getItem())
            .succeed(this.stack, this.player, this.x, this.z, this.prevPos, this.samples, (Boolean)ConfigHandler.GENERAL.displayCoordinates.get());
      } else {
         NaturesCompass.LOGGER.error("BiomeSearchWorker " + this.id + ": Invalid compass after search");
      }

      this.finished = true;
   }

   private void fail() {
      NaturesCompass.LOGGER.info("BiomeSearchWorker " + this.id + ": Search failed: " + this.getRadius() + " radius, " + this.samples + " samples");
      if (!this.stack.isEmpty() && this.stack.getItem() == NaturesCompass.naturesCompass) {
         ((NaturesCompassItem)this.stack.getItem()).fail(this.stack, this.player, this.roundRadius(this.getRadius(), 500), this.samples);
      } else {
         NaturesCompass.LOGGER.error("BiomeSearchWorker " + this.id + ": Invalid compass after search");
      }

      this.finished = true;
   }

   public void stop() {
      NaturesCompass.LOGGER.info("BiomeSearchWorker " + this.id + ": Search stopped: " + this.getRadius() + " radius, " + this.samples + " samples");
      this.finished = true;
   }

   private int getRadius() {
      return BiomeUtils.getDistanceToBiome(this.startPos, this.x, this.z);
   }

   private int roundRadius(int radius, int roundTo) {
      return radius / roundTo * roundTo;
   }
}
