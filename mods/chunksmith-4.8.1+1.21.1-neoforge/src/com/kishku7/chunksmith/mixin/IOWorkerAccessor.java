package com.kishku7.chunksmith.mixin;

import java.util.Map;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue.IntRunnable;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.chunk.storage.RegionFileStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(IOWorker.class)
public interface IOWorkerAccessor {
   @Accessor("mailbox")
   ProcessorMailbox<IntRunnable> chunksmith$getMailbox();

   @Accessor("pendingWrites")
   Map<?, ?> chunksmith$getPendingWrites();

   @Accessor("storage")
   RegionFileStorage chunksmith$getStorage();
}
