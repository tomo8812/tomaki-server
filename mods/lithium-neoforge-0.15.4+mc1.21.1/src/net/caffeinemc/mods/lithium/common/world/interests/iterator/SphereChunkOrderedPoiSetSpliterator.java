package net.caffeinemc.mods.lithium.common.world.interests.iterator;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.caffeinemc.mods.lithium.common.util.Distances;
import net.caffeinemc.mods.lithium.common.world.interests.RegionBasedStorageSectionExtended;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.village.poi.PoiSection;

public class SphereChunkOrderedPoiSetSpliterator extends AbstractSpliterator<Stream<PoiSection>> {
   private final int limit;
   private final int minChunkZ;
   private final BlockPos origin;
   private final double radiusSq;
   private final RegionBasedStorageSectionExtended<PoiSection> storage;
   private final int maxChunkZ;
   int chunkX;
   int chunkZ;
   int iterated;

   public SphereChunkOrderedPoiSetSpliterator(int radius, BlockPos origin, RegionBasedStorageSectionExtended<PoiSection> storage) {
      super(
         (long)((origin.getX() + radius + 1 >> 4) - (origin.getX() - radius - 1 >> 4) + 1)
            * ((origin.getZ() + radius + 1 >> 4) - (origin.getZ() - radius - 1 >> 4) + 1),
         16
      );
      this.origin = origin;
      this.radiusSq = radius * radius;
      this.storage = storage;
      int minChunkX = origin.getX() - radius - 1 >> 4;
      int maxChunkX = origin.getX() + radius + 1 >> 4;
      this.minChunkZ = origin.getZ() - radius - 1 >> 4;
      this.maxChunkZ = origin.getZ() + radius + 1 >> 4;
      this.limit = (maxChunkX - minChunkX + 1) * (this.maxChunkZ - this.minChunkZ + 1);
      this.chunkX = minChunkX;
      this.chunkZ = this.minChunkZ;
      this.iterated = 0;
   }

   @Override
   public boolean tryAdvance(Consumer<? super Stream<PoiSection>> action) {
      while (this.iterated < this.limit) {
         this.iterated++;
         boolean progress = false;
         if (Distances.getMinChunkToBlockDistanceL2Sq(this.origin, this.chunkX, this.chunkZ) <= this.radiusSq) {
            action.accept(this.storage.lithium$getWithinChunkColumn(this.chunkX, this.chunkZ));
            progress = true;
         }

         this.chunkZ++;
         if (this.chunkZ > this.maxChunkZ) {
            this.chunkX++;
            this.chunkZ = this.minChunkZ;
         }

         if (progress) {
            return true;
         }
      }

      return false;
   }
}
