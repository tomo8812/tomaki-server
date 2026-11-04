package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.lod.client.net.CsLodClientNet;
import com.kishku7.chunksmith.lod.client.render.DhTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CsLodClientBoot {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");

   private CsLodClientBoot() {
   }

   public static void init() {
      CsLodClientNet.register();
      ClientPlatform.onClientSetup(CsLodClientBoot::bindRenderers);
      LOGGER.info("Chunksmith: LOD client ready ({})", Renderers.describe());
   }

   private static void bindRenderers() {
      if (Renderers.hasDh()) {
         try {
            DhTarget.bind();
         } catch (LinkageError error) {
            LOGGER.warn("Distant Horizons present but incompatible, skipping: {}", error.toString());
         }
      }
   }
}
