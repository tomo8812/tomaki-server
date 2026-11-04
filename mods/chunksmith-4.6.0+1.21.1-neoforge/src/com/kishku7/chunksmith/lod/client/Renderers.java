package com.kishku7.chunksmith.lod.client;

import com.kishku7.chunksmith.lod.LodWarnings;
import com.kishku7.chunksmith.lod.client.render.DhRadius;
import com.kishku7.chunksmith.lod.client.render.VoxyRadius;
import com.kishku7.chunksmith.lod.client.render.VoxyTarget;

public final class Renderers {
   private static final String CAUSE_VOXY_SEAM = "voxy-seam-unloadable";
   private static final String CAUSE_DH_SEAM = "dh-seam-unloadable";

   private Renderers() {
   }

   public static boolean hasVoxy() {
      return ClientPlatform.isModLoaded("voxy") && VoxyTarget.supported();
   }

   public static boolean hasDh() {
      return ClientPlatform.isModLoaded("distanthorizons");
   }

   public static boolean any() {
      return hasVoxy() || hasDh();
   }

   public static int configuredRadiusBlocks() {
      int blocks = 0;
      if (hasDh()) {
         try {
            blocks = Math.max(blocks, DhRadius.blocks());
         } catch (LinkageError error) {
            LodWarnings.once(
               "dh-seam-unloadable",
               "Distant Horizons is installed but Chunksmith could not load its own DH radius reader ("
                  + error
                  + "). Falling back to a LOD radius of 256 blocks. Please report this."
            );
         }
      }

      if (hasVoxy()) {
         try {
            blocks = Math.max(blocks, VoxyRadius.blocks());
         } catch (LinkageError error) {
            LodWarnings.once(
               "voxy-seam-unloadable",
               "voxy is installed but Chunksmith could not load its own voxy radius reader ("
                  + error
                  + "). Falling back to a LOD radius of 256 blocks, which is far less distant terrain than voxy can draw. Please report this, with your voxy version."
            );
         }
      }

      return blocks > 0 ? blocks : 256;
   }

   public static String describe() {
      return !any() ? "no LOD renderer installed" : (hasVoxy() ? "voxy " : "") + (hasDh() ? "distant-horizons " : "") + "detected";
   }
}
