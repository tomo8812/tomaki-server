package com.kishku7.chunksmith.platform;

import com.kishku7.chunksmith.platform.util.Location;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface World {
   String getName();

   String getKey();

   CompletableFuture<Boolean> isChunkGenerated(int var1, int var2);

   CompletableFuture<Void> getChunkAtAsync(int var1, int var2);

   UUID getUUID();

   int getSeaLevel();

   default void settleDrain() {
   }

   default void settleLoad(int chunkX, int chunkZ, int radius) {
   }

   default void settleRelease(int chunkX, int chunkZ, int radius) {
   }

   Location getSpawn();

   Border getWorldBorder();

   int getElevation(int var1, int var2);

   default CompletableFuture<Integer> getElevationAtAsync(int x, int z) {
      return CompletableFuture.completedFuture(this.getElevation(x, z));
   }

   int getMaxElevation();

   void playEffect(Player var1, String var2);

   void playSound(Player var1, String var2);

   Optional<Path> getDirectory(String var1);

   default Optional<Path> getEntitiesDirectory() {
      return this.getDirectory("entities");
   }

   default Optional<Path> getPOIDirectory() {
      return this.getDirectory("poi");
   }

   default Optional<Path> getRegionDirectory() {
      return this.getDirectory("region");
   }

   default long getQueuedChunkWrites() {
      return -1L;
   }
}
