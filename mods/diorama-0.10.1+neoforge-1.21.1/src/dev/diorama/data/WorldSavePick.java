package dev.diorama.data;

import dev.diorama.Diorama;
import dev.diorama.Facing;
import dev.diorama.bake.BlockLooks;
import dev.diorama.bake.ChunkSource;
import dev.diorama.bake.MapPick;
import dev.diorama.bake.TileKey;
import dev.diorama.bake.TurnedSource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.jspecify.annotations.Nullable;

public final class WorldSavePick {
   private final IntegratedServer server;
   private final ResourceKey<Level> dimension;
   private final ClientLevel level;

   private WorldSavePick(IntegratedServer server, ClientLevel level) {
      this.server = server;
      this.dimension = level.dimension();
      this.level = level;
   }

   public static @Nullable WorldSavePick current() {
      Minecraft mc = Minecraft.getInstance();
      IntegratedServer server = mc.getSingleplayerServer();
      ClientLevel level = mc.level;
      return server != null && level != null ? new WorldSavePick(server, level) : null;
   }

   public CompletableFuture<@Nullable BlockPos> pick(ChunkSource map, Facing facing, BlockLooks looks, double u, double v, boolean fogOfWar) {
      return !this.server.isRunning() ? CompletableFuture.completedFuture(null) : this.server.submit(() -> {
         if (!this.server.isSameThread()) {
            return null;
         }

         ServerLevel serverLevel = this.server.getLevel(this.dimension);
         if (serverLevel == null) {
            return null;
         }

         TileKey tile = new TileKey(Integer.MIN_VALUE, (int)Math.floor(u / 128.0), (int)Math.floor(v / 128.0));
         MapPick pick = MapPick.fromWorldTop(new TurnedSource(new WorldSavePick.Source(map, serverLevel, this.level), facing), looks, tile, fogOfWar);
         return pick.pick(u, v) == 0 ? facing.worldPos(new BlockPos((int)Math.floor(u), pick.y, pick.z)) : null;
      }).exceptionally(t -> {
         Diorama.LOGGER.warn("Couldn't read the world's chunks at map point {}, {}", new Object[]{u, v, t});
         return null;
      });
   }

   private static final class Source implements ChunkSource {
      private final ChunkSource map;
      private final ServerLevel serverLevel;
      private final ClientLevel level;
      private final Map<Long, ChunkSnapshot> read = new HashMap<>();

      Source(ChunkSource map, ServerLevel serverLevel, ClientLevel level) {
         this.map = map;
         this.serverLevel = serverLevel;
         this.level = level;
      }

      @Override
      public @Nullable ChunkSnapshot chunk(int chunkX, int chunkZ) {
         ChunkSnapshot known = this.map.chunk(chunkX, chunkZ);
         return known != null ? known : this.read.computeIfAbsent(ChunkSnapshot.key(chunkX, chunkZ), k -> {
            ChunkAccess chunk = this.serverLevel.getChunkSource().getChunk(chunkX, chunkZ, ChunkStatus.EMPTY, true);
            return chunk == null ? null : ChunkSnapshot.fromServer(chunk, this.level);
         });
      }

      @Override
      public int minY() {
         return this.map.minY();
      }

      @Override
      public int maxY() {
         return this.map.maxY();
      }

      @Override
      public int topY() {
         return this.map.topY();
      }

      @Override
      public int roof() {
         return this.map.roof();
      }

      @Override
      public boolean isSeen(int x, int y, int z) {
         return this.map.isSeen(x, y, z);
      }
   }
}
