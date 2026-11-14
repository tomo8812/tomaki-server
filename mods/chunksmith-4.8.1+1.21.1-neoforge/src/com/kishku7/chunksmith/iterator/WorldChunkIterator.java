package com.kishku7.chunksmith.iterator;

import com.kishku7.chunksmith.Chunksmith;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.nbt.StringTag;
import com.kishku7.chunksmith.nbt.util.ChunkFilter;
import com.kishku7.chunksmith.nbt.util.RegionFile;
import com.kishku7.chunksmith.util.ChunkCoordinate;
import com.kishku7.chunksmith.util.Hilbert;
import com.kishku7.chunksmith.util.Parameter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

public class WorldChunkIterator implements ChunkIterator {
   private final Chunksmith chunky;
   private final int minRegionX;
   private final int minRegionZ;
   private final int maxRegionX;
   private final int maxRegionZ;
   private final int minChunkX;
   private final int minChunkZ;
   private final int maxChunkX;
   private final int maxChunkZ;
   private final String worldName;
   private final Queue<ChunkCoordinate> chunks;
   private final AtomicLong total = new AtomicLong();
   private final Path savePath;
   private final Path regionPath;
   private final String name;

   public WorldChunkIterator(Selection selection) {
      this.chunky = selection.chunky();
      int centerRegionX = selection.centerRegionX();
      int centerRegionZ = selection.centerRegionZ();
      int radiusRegionsX = selection.radiusRegionsX();
      int radiusRegionsZ = selection.radiusRegionsZ();
      this.minRegionX = centerRegionX - radiusRegionsX;
      this.minRegionZ = centerRegionZ - radiusRegionsZ;
      this.maxRegionX = centerRegionX + radiusRegionsX;
      this.maxRegionZ = centerRegionZ + radiusRegionsZ;
      int centerChunkX = selection.centerChunkX();
      int centerChunkZ = selection.centerChunkZ();
      int radiusChunksX = selection.radiusChunksX();
      int radiusChunksZ = selection.radiusChunksZ();
      this.minChunkX = centerChunkX - radiusChunksX;
      this.minChunkZ = centerChunkZ - radiusChunksZ;
      this.maxChunkX = centerChunkX + radiusChunksX;
      this.maxChunkZ = centerChunkZ + radiusChunksZ;
      this.chunks = new LinkedList<>();
      this.worldName = selection.world().getName();
      String saveFile = this.worldName.substring(this.worldName.indexOf(58) + 1);
      this.savePath = selection.chunky().getConfig().getDirectory().resolve(String.format("%s.csv", saveFile));
      this.regionPath = selection.world().getRegionDirectory().orElse(null);
      this.name = Parameter.of("csv", saveFile).toString();
   }

   @Override
   public boolean hasNext() {
      return !this.chunks.isEmpty();
   }

   public ChunkCoordinate next() {
      if (this.chunks.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.chunks.poll();
      }
   }

   @Override
   public long total() {
      return this.total.get();
   }

   @Override
   public String name() {
      return this.name;
   }

   @Override
   public boolean process() {
      if (this.regionPath == null) {
         return false;
      }

      long startTime = System.currentTimeMillis();
      AtomicLong updateTime = new AtomicLong(startTime);
      Function<String, Boolean> chunkStatusFilter = this.chunky.getConfig().isForceLoadExistingChunks()
         ? status -> !"minecraft:empty".equals(status) && !"empty".equals(status)
         : status -> "minecraft:full".equals(status) || "full".equals(status);
      StringBuilder saveData = new StringBuilder();

      try (Stream<Path> files = Files.list(this.regionPath)) {
         List<Path> regions = files.filter(
               file -> {
                  ChunkCoordinate regionCoordinatex = ChunkCoordinate.fromRegionFile(file.getFileName().toString()).orElse(null);
                  return regionCoordinatex != null
                     && regionCoordinatex.x() >= this.minRegionX
                     && regionCoordinatex.x() <= this.maxRegionX
                     && regionCoordinatex.z() >= this.minRegionZ
                     && regionCoordinatex.z() <= this.maxRegionZ;
               }
            )
            .toList();
         long totalRegions = regions.size();
         AtomicLong finishedRegions = new AtomicLong();

         for (Path region : regions) {
            ChunkCoordinate regionCoordinate = ChunkCoordinate.fromRegionFile(region.getFileName().toString()).orElseThrow(IllegalStateException::new);
            int regionX = regionCoordinate.x();
            int regionZ = regionCoordinate.z();
            RegionFile regionFile = new RegionFile(region.toFile(), ChunkFilter.of((byte)8, "Status"));

            for (ChunkCoordinate offset : Hilbert.chunkCoordinateOffsets()) {
               ChunkCoordinate chunkCoordinate = new ChunkCoordinate((regionX << 5) + offset.x(), (regionZ << 5) + offset.z());
               if (chunkCoordinate.x() >= this.minChunkX
                  && chunkCoordinate.x() <= this.maxChunkX
                  && chunkCoordinate.z() >= this.minChunkZ
                  && chunkCoordinate.z() <= this.maxChunkZ) {
                  regionFile.getChunk(chunkCoordinate.x(), chunkCoordinate.z())
                     .ifPresent(
                        chunk -> {
                           boolean generated = Optional.ofNullable(chunk.getData())
                              .filter(StringTag.class::isInstance)
                              .map(StringTag.class::cast)
                              .map(StringTag::value)
                              .map(chunkStatusFilter)
                              .orElse(false);
                           if (generated) {
                              this.chunks.add(chunkCoordinate);
                              saveData.append(chunkCoordinate.x()).append(',').append(chunkCoordinate.z()).append('\n');
                              this.total.incrementAndGet();
                           }
                        }
                     );
               }
            }

            finishedRegions.getAndIncrement();
            if (!this.chunky.getConfig().isSilent()) {
               long currentTime = System.currentTimeMillis();
               boolean updateIntervalElapsed = (currentTime - updateTime.get()) / 1000.0 > this.chunky.getConfig().getUpdateInterval();
               if (updateIntervalElapsed || finishedRegions.get() == totalRegions) {
                  this.chunky
                     .getServer()
                     .getConsole()
                     .sendMessagePrefixed(
                        "task_trim_update",
                        this.worldName,
                        finishedRegions.get(),
                        String.format("%.2f", 100.0F * (float)finishedRegions.get() / (float)totalRegions)
                     );
                  updateTime.set(currentTime);
               }
            }

            if (Thread.interrupted()) {
               Thread.currentThread().interrupt();
               return false;
            }
         }

         Files.writeString(this.savePath, saveData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
         return true;
      } catch (IOException e) {
         e.printStackTrace();
         return false;
      }
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public Object next() {
      return this.next();
   }
}
