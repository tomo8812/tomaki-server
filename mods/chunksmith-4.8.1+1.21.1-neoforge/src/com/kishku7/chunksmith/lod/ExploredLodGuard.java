package com.kishku7.chunksmith.lod;

import com.kishku7.chunksmith.ChunksmithProvider;
import com.kishku7.chunksmith.platform.Config;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ExploredLodGuard {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private static volatile boolean failureLogged;

   private ExploredLodGuard() {
   }

   public static void onSave(ServerLevel level, ChunkAccess chunk) {
      if (chunk instanceof LevelChunk full && full.isUnsaved()) {
         take(level, full, true);
      }
   }

   public static void onUnload(ServerLevel level, LevelChunk chunk) {
      take(level, chunk, false);
   }

   private static void take(ServerLevel level, LevelChunk full, boolean countHeld) {
      if (ChunksmithProvider.isLoaded()) {
         try {
            Config config = ChunksmithProvider.get().getConfig();
            if (!config.isLodFromExploredChunksEnabled()) {
               return;
            }

            CsLodPresenceIndex presence = LodSupport.presenceIndexFor(level.getServer(), LodSupport.dimensionId(level));
            if (presence == null) {
               return;
            }

            ChunkPos pos = full.getPos();
            int chunkX = pos.getMinBlockX() >> 4;
            int chunkZ = pos.getMinBlockZ() >> 4;
            boolean held = presence.hasLod(chunkX, chunkZ);
            if (!ExploredLod.shouldTake(true, true, true, true, held)) {
               if (countHeld) {
                  ExploredLod.noteHeld();
               }

               return;
            }

            LodSupport.offer(level, full);
            presence.markLod(chunkX, chunkZ);
            ExploredLod.noteTaken();
         } catch (RuntimeException | LinkageError e) {
            ExploredLod.noteFailed();
            if (!failureLogged) {
               failureLogged = true;
               LOGGER.warn(
                  "Chunksmith: could not add an explored chunk to the LOD store; the chunk itself saved normally. Further failures are counted in /cs lod status, not logged.",
                  e
               );
            }
         }
      }
   }
}
