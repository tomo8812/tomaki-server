package dev.diorama.bake;

import net.minecraft.world.level.levelgen.WorldgenRandom;
import org.jspecify.annotations.Nullable;

public record Overlays(@Nullable Long slimeSeed, boolean spawnSpots) {
   public static final int CHUNK_WEST = 1;
   public static final int CHUNK_NORTH = 2;
   public static final int SLIME = 4;
   public static final int SPAWN_AT_NIGHT = 8;
   public static final int SPAWN_ANY_TIME = 16;
   public static final int DAYLIGHT_SPAWN_LIMIT = 7;

   public static boolean slimeChunk(long seed, int chunkX, int chunkZ) {
      return WorldgenRandom.seedSlimeChunk(chunkX, chunkZ, seed, 987234911L).nextInt(10) == 0;
   }
}
