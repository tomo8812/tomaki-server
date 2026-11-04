package org.embeddedt.modernfix.duck.release_protochunks;

import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.FullChunkStatus;

public interface IClearableChunkHolder {
   int LOWEST_DROPPABLE_TICKET_LEVEL = ChunkLevel.byStatus(FullChunkStatus.FULL) + 2;

   void mfix$resetProtoChunkFutures();
}
