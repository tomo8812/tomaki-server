package com.kishku7.chunksmith.lod;

import com.seibel.distanthorizons.api.DhApi.Delayed;
import com.seibel.distanthorizons.api.interfaces.world.IDhApiLevelWrapper;
import com.seibel.distanthorizons.api.objects.DhApiResult;
import java.io.IOException;
import java.nio.file.Path;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;

public final class CsLodDhPusher {
   private CsLodDhPusher() {
   }

   public static int push(ServerLevel level, IDhApiLevelWrapper wrapper, Path storeRoot, Consumer<String> progress) throws IOException {
      if (CsLodDhSupport.isDisabled()) {
         progress.accept("Distant Horizons was ruled out earlier this session; not pushing");
         return 0;
      }

      int[] pushed = new int[]{0};
      int[] failed = new int[]{0};

      try {
         CsLodRegionStore.forEachChunk(storeRoot, record -> {
            LevelChunk chunk = synthesize(level, record);
            DhApiResult<Void> result = Delayed.terrainRepo.overwriteChunkDataAsync(wrapper, new Object[]{chunk, level});
            if (result.success) {
               pushed[0]++;
            } else {
               failed[0]++;
               if (failed[0] == 1) {
                  progress.accept("first failure: " + result.message);
               }
            }

            if ((pushed[0] + failed[0]) % 250 == 0) {
               progress.accept("pushed " + pushed[0] + ", failed " + failed[0]);
            }
         });
      } catch (LinkageError e) {
         CsLodDhSupport.disable(e);
         progress.accept(
            "stopped after "
               + pushed[0]
               + " chunks: this Distant Horizons does not have the API Chunksmith was built against ("
               + CsLodDhSupport.version()
               + "). DH is disabled for this session; nothing else is affected."
         );
         return pushed[0];
      }

      progress.accept(
         "done: pushed " + pushed[0] + ", failed " + failed[0] + ". NOTE a 'success' here does NOT prove DH kept it; check the DB and LOOK at the terrain"
      );
      return pushed[0];
   }

   private static LevelChunk synthesize(ServerLevel level, CsLodChunk record) {
      LevelChunk chunk = new LevelChunk(level, new ChunkPos(record.getChunkX(), record.getChunkZ()));
      LevelChunkSection[] sections = chunk.getSections();
      int count = Math.min(sections.length, record.getSections().size());

      for (int i = 0; i < count; i++) {
         sections[i] = CsLodSectionBuilder.rebuild(level, record, record.getSections().get(i));
      }

      return chunk;
   }
}
