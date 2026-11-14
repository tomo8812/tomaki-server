package com.kishku7.chunksmith.lod.client.render;

import com.kishku7.chunksmith.lod.CsLodChunk;
import com.kishku7.chunksmith.lod.CsLodSectionBuilder;
import com.kishku7.chunksmith.lod.LodWarnings;
import me.cortex.voxy.common.world.service.VoxelIngestService;
import me.cortex.voxy.commonImpl.VoxyCommon;
import me.cortex.voxy.commonImpl.WorldIdentifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;

public final class VoxyTarget {
   private static final int QUEUE_LIMIT = 512;
   private static final String CAUSE_INCOMPATIBLE = "voxy-incompatible";
   private static volatile boolean broken;

   private VoxyTarget() {
   }

   public static boolean supported() {
      return true;
   }

   public static boolean available() {
      if (broken) {
         return false;
      }

      try {
         return VoxyCommon.getInstance() != null;
      } catch (LinkageError error) {
         disable(error);
         return false;
      }
   }

   public static int inject(Level level, CsLodChunk record) {
      if (broken) {
         return 0;
      }

      try {
         return doInject(level, record);
      } catch (LinkageError error) {
         disable(error);
         return 0;
      }
   }

   private static int doInject(Level level, CsLodChunk record) {
      WorldIdentifier world = WorldIdentifier.of(level);
      int ingested = 0;

      for (int i = 0; i < record.getSections().size(); i++) {
         awaitCapacity();
         CsLodChunk.Section section = record.getSections().get(i);
         LevelChunkSection rebuilt = CsLodSectionBuilder.rebuild(level, record, section);
         DataLayer sky = light(section.getSkyLight(), section.getUniformSky());
         DataLayer block = light(section.getBlockLight(), section.getUniformBlockLight());
         VoxelIngestService.rawIngest(world, rebuilt, record.getChunkX(), record.getMinSectionY() + i, record.getChunkZ(), block, sky);
         ingested++;
      }

      return ingested;
   }

   private static void disable(LinkageError error) {
      broken = true;
      LodWarnings.once(
         "voxy-incompatible",
         "voxy is installed, but this build of it does not match the voxy Chunksmith was built against ("
            + error
            + "). Chunksmith cannot feed it, so NO distant terrain will appear in voxy; Distant Horizons, if you have it, is unaffected. This usually means a voxy fork that changed a method or a field. Please report it, with your voxy version."
      );
   }

   private static DataLayer light(byte[] packed, int uniform) {
      if (packed != null) {
         return new DataLayer((byte[])packed.clone());
      } else {
         return uniform > 0 ? new DataLayer(uniform) : new DataLayer();
      }
   }

   private static void awaitCapacity() {
      try {
         while (VoxyCommon.getInstance() != null && VoxyCommon.getInstance().getIngestService().getTaskCount() > 512) {
            Thread.sleep(20L);
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }
}
