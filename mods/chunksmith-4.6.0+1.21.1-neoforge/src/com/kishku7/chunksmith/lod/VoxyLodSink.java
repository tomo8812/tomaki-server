package com.kishku7.chunksmith.lod;

import java.nio.file.Path;
import me.cortex.voxy.common.world.service.VoxelIngestService;
import me.cortex.voxy.commonImpl.VoxyCommon;
import me.cortex.voxy.commonImpl.VoxyInstance;
import net.minecraft.world.level.chunk.LevelChunk;

public final class VoxyLodSink implements LodSink {
   private static final String CAUSE_INCOMPATIBLE = "voxy-incompatible";
   private volatile boolean broken;
   private final Path storeDir;
   private boolean lastFed;

   public VoxyLodSink(Path storeDir) {
      this.storeDir = storeDir;
   }

   @Override
   public boolean offer(Object chunk) {
      if (chunk instanceof CsLodChunk record) {
         LiveFeedLedger.touch(this.storeDir, record.getChunkX(), record.getChunkZ(), this.lastFed);
         return true;
      } else {
         this.lastFed = false;
         if (this.broken) {
            return true;
         }

         if (chunk instanceof LevelChunk levelChunk) {
            try {
               if (VoxyCommon.getInstance() == null) {
                  return true;
               }

               this.lastFed = VoxelIngestService.tryAutoIngestChunk(levelChunk);
               return this.lastFed;
            } catch (LinkageError error) {
               this.standDown(error);
               return true;
            }
         } else {
            return true;
         }
      }
   }

   @Override
   public int queueDepth() {
      if (this.broken) {
         return 0;
      }

      try {
         VoxyInstance instance = VoxyCommon.getInstance();
         return instance == null ? 0 : instance.getIngestService().getTaskCount();
      } catch (LinkageError error) {
         this.standDown(error);
         return 0;
      }
   }

   @Override
   public boolean feedsRendererDirectly() {
      if (this.broken) {
         return false;
      }

      try {
         return VoxyCommon.getInstance() != null;
      } catch (LinkageError error) {
         this.standDown(error);
         return false;
      }
   }

   private void standDown(LinkageError error) {
      this.broken = true;
      LodWarnings.once(
         "voxy-incompatible",
         "voxy is installed, but this build of it does not match the voxy Chunksmith was built against ("
            + error
            + "). Chunksmith will keep generating and will keep writing its own LOD store, but it can no longer feed voxy directly. Distant terrain will not appear in voxy while you generate. This normally means a voxy fork that changed a method or a field. Please report it, with your voxy version."
      );
   }

   @Override
   public String toString() {
      return "VoxyLodSink" + (this.broken ? " (stood down -- incompatible voxy)" : "");
   }
}
