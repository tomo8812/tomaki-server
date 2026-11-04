package org.embeddedt.modernfix.duck.release_protochunks;

import java.util.concurrent.Executor;
import net.minecraft.world.level.ChunkPos;

public interface ISuspendedHolderTrackingChunkMap {
   void mfix$markForSuspensionCheck(ChunkPos var1);

   Executor mfix$getMainThreadExecutor();
}
