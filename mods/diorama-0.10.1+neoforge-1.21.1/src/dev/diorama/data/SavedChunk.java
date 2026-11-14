package dev.diorama.data;

import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import org.jspecify.annotations.Nullable;

public record SavedChunk(
   int chunkX,
   int chunkZ,
   boolean finished,
   @Nullable PalettedContainer<BlockState>[] states,
   @Nullable PalettedContainerRO<Holder<Biome>>[] biomes,
   @Nullable DataLayer[] blockLight,
   @Nullable DataLayer[] skyLight,
   int[] surface,
   int[] solid
) {
}
