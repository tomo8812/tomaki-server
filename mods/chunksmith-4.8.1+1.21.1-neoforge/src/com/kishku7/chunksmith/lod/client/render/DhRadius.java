package com.kishku7.chunksmith.lod.client.render;

import com.seibel.distanthorizons.api.DhApi.Delayed;

public final class DhRadius {
   private DhRadius() {
   }

   public static int blocks() {
      try {
         Integer chunks = (Integer)Delayed.configs.graphics().chunkRenderDistance().getValue();
         return chunks != null && chunks > 0 ? chunks * 16 : 0;
      } catch (LinkageError e) {
         DhTarget.disable(e);
         return 0;
      } catch (RuntimeException e) {
         return 0;
      }
   }
}
