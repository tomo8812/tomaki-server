package com.kishku7.chunksmith.lod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Consumer;
import me.cortex.voxy.common.world.service.VoxelIngestService;
import me.cortex.voxy.commonImpl.VoxyCommon;
import me.cortex.voxy.commonImpl.WorldIdentifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunkSection;

public final class CsLodVoxyInjector {
   private static final int VOXY_QUEUE_LIMIT = 512;
   private static final String CAUSE_INCOMPATIBLE = "voxy-incompatible";

   private CsLodVoxyInjector() {
   }

   public static boolean voxyAvailable() {
      if (!LodPlatform.isModLoaded("voxy")) {
         return false;
      }

      try {
         return VoxyCommon.getInstance() != null;
      } catch (LinkageError error) {
         warnIncompatible(error);
         return false;
      }
   }

   private static void warnIncompatible(LinkageError error) {
      LodWarnings.once(
         "voxy-incompatible",
         "voxy is installed, but this build of it does not match the voxy Chunksmith was built against ("
            + error
            + "). Chunksmith cannot feed LODs into it. This normally means a voxy fork that changed a method or a field. Please report it, with your voxy version."
      );
   }

   public static int inject(ServerLevel level, Path storeRoot, Consumer<String> progress) throws IOException {
      WorldIdentifier world = WorldIdentifier.of(level);
      int[] chunks = new int[]{0};
      int[] sections = new int[]{0};

      int visited;
      try {
         visited = CsLodRegionStore.forEachChunk(storeRoot, record -> {
            awaitVoxyCapacity();
            sections[0] += injectChunk(level, world, record);
            chunks[0]++;
            if (chunks[0] % 500 == 0) {
               progress.accept("injected " + chunks[0] + " chunks (" + sections[0] + " sections)");
            }
         });
      } catch (LinkageError error) {
         warnIncompatible(error);
         progress.accept("ABORTED after " + chunks[0] + " chunks: this voxy will not accept our data (" + error + ")");
         return chunks[0];
      }

      progress.accept(
         "done: " + chunks[0] + " chunks, " + sections[0] + " sections injected into voxy" + (visited == chunks[0] ? "" : " (" + visited + " visited)")
      );
      return chunks[0];
   }

   private static int injectChunk(ServerLevel level, WorldIdentifier world, CsLodChunk record) {
      int injected = 0;
      List<CsLodChunk.Section> sections = record.getSections();

      for (int i = 0; i < sections.size(); i++) {
         CsLodChunk.Section section = sections.get(i);
         LevelChunkSection rebuilt = CsLodSectionBuilder.rebuild(level, record, section);
         DataLayer sky = light(section.getSkyLight(), section.getUniformSky());
         DataLayer block = light(section.getBlockLight(), section.getUniformBlockLight());
         VoxelIngestService.rawIngest(world, rebuilt, record.getChunkX(), record.getMinSectionY() + i, record.getChunkZ(), block, sky);
         injected++;
      }

      return injected;
   }

   private static DataLayer light(byte[] packed, int uniform) {
      if (packed != null) {
         return new DataLayer((byte[])packed.clone());
      } else {
         return uniform > 0 ? new DataLayer(uniform) : new DataLayer();
      }
   }

   private static void awaitVoxyCapacity() {
      try {
         while (VoxyCommon.getInstance() != null && VoxyCommon.getInstance().getIngestService().getTaskCount() > 512) {
            Thread.sleep(20L);
         }
      } catch (InterruptedException e) {
         Thread.currentThread().interrupt();
      }
   }
}
