package net.caffeinemc.mods.lithium.common.world.interests;

import java.util.stream.Stream;

public interface RegionBasedStorageSectionExtended<R> {
   Stream<R> lithium$getWithinChunkColumn(int var1, int var2);

   Iterable<R> lithium$getInChunkColumn(int var1, int var2);
}
