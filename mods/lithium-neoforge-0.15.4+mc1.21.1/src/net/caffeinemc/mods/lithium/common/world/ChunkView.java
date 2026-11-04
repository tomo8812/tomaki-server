package net.caffeinemc.mods.lithium.common.world;

import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.Nullable;

public interface ChunkView {
   @Nullable
   ChunkAccess lithium$getLoadedChunk(int var1, int var2);
}
