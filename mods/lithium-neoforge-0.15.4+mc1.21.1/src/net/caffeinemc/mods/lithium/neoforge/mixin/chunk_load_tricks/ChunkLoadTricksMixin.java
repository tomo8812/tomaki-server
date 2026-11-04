package net.caffeinemc.mods.lithium.neoforge.mixin.chunk_load_tricks;

import net.caffeinemc.mods.lithium.common.world.ChunkLoadTricks;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ChunkLoadTricks.class)
public class ChunkLoadTricksMixin {
   @Overwrite
   public static ChunkAccess tryRetrieveCurrentlyLoading(ChunkHolder holder) {
      return holder != null ? holder.currentlyLoading : null;
   }
}
