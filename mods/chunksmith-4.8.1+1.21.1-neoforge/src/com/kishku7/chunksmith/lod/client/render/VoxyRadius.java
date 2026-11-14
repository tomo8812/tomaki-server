package com.kishku7.chunksmith.lod.client.render;

import com.kishku7.chunksmith.lod.LodWarnings;
import com.kishku7.chunksmith.lod.client.VoxyConfigReader;
import me.cortex.voxy.client.config.VoxyConfig;

public final class VoxyRadius {
   private static final String CAUSE_CONFIG = "voxy-config-unreadable";

   private VoxyRadius() {
   }

   public static int blocks() {
      Object config;
      try {
         config = VoxyConfigReader.staticField(VoxyConfig.class, "CONFIG");
      } catch (RuntimeException | LinkageError e) {
         LodWarnings.once(
            "voxy-config-unreadable",
            "voxy is installed but its configuration could not be read ("
               + e
               + "). Falling back to a LOD radius of 256 blocks instead of the distance voxy is actually set to, so you will see far less distant terrain than voxy can draw. This normally means a voxy fork whose config class differs from upstream. Please report it, with your voxy version."
         );
         return 0;
      }

      return VoxyConfigReader.radiusBlocks(config);
   }
}
