package org.valkyrienskies.mod.compat;

import net.caffeinemc.mods.sodium.client.render.chunk.map.ChunkTrackerHolder;
import net.minecraft.class_638;
import org.valkyrienskies.mod.mixin.ValkyrienCommonMixinConfigPlugin;

public class SodiumCompat {
   public static void onChunkAdded(class_638 level, int x, int z) {
      if (ValkyrienCommonMixinConfigPlugin.getVSRenderer() == VSRenderer.SODIUM) {
         ChunkTrackerHolder.get(level).onChunkStatusAdded(x, z, 1);
      }
   }

   public static void onChunkRemoved(class_638 level, int x, int z) {
      if (ValkyrienCommonMixinConfigPlugin.getVSRenderer() == VSRenderer.SODIUM) {
         ChunkTrackerHolder.get(level).onChunkStatusRemoved(x, z, 1);
      }
   }
}
