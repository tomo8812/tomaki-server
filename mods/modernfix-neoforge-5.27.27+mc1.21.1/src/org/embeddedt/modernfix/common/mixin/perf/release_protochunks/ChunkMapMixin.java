package org.embeddedt.modernfix.common.mixin.perf.release_protochunks;

import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.concurrent.Executor;
import java.util.function.BooleanSupplier;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkLevel;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.ChunkPos;
import org.embeddedt.modernfix.duck.release_protochunks.IClearableChunkHolder;
import org.embeddedt.modernfix.duck.release_protochunks.ISuspendedHolderTrackingChunkMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin implements ISuspendedHolderTrackingChunkMap {
   private static final int MFIX$TICKS_TO_WAIT_BEFORE_SUSPENDING = 100;
   @Shadow
   @Final
   public Long2ObjectLinkedOpenHashMap<ChunkHolder> updatingChunkMap;
   @Shadow
   @Final
   private BlockableEventLoop<Runnable> mainThreadExecutor;
   @Shadow
   @Final
   public Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads;
   private final Long2IntOpenHashMap mfix$protoChunksToDrop = new Long2IntOpenHashMap();
   private int mfix$dropTickCounter = 0;

   @Shadow
   protected abstract void lambda$scheduleUnload$12(ChunkHolder var1, long var2);

   @Inject(method = "processUnloads(Ljava/util/function/BooleanSupplier;)V", at = @At("RETURN"))
   private void dropProtoChunks(BooleanSupplier hasMoreTime, CallbackInfo ci) {
      int suspended = 0;
      int iterations = 0;
      this.mfix$dropTickCounter++;
      ObjectIterator<Entry> dropIterator = this.mfix$protoChunksToDrop.long2IntEntrySet().fastIterator();

      while (dropIterator.hasNext() && suspended < 50 && iterations < 500 && (hasMoreTime.getAsBoolean() || this.mfix$protoChunksToDrop.size() > 1000)) {
         iterations++;
         Entry entry = (Entry)dropIterator.next();
         long pos = entry.getLongKey();
         ChunkHolder holder = (ChunkHolder)this.updatingChunkMap.get(pos);
         if (holder == null || holder.getTicketLevel() < IClearableChunkHolder.LOWEST_DROPPABLE_TICKET_LEVEL || !ChunkLevel.isLoaded(holder.getTicketLevel())) {
            dropIterator.remove();
         } else if (!holder.isReadyForSaving()) {
            entry.setValue(this.mfix$dropTickCounter);
         } else if (this.mfix$dropTickCounter - entry.getIntValue() >= 100) {
            dropIterator.remove();
            this.pendingUnloads.put(pos, holder);
            this.lambda$scheduleUnload$12(holder, pos);
            ((IClearableChunkHolder)holder).mfix$resetProtoChunkFutures();
            suspended++;
         }
      }
   }

   @Override
   public void mfix$markForSuspensionCheck(ChunkPos pos) {
      this.mfix$protoChunksToDrop.put(pos.toLong(), this.mfix$dropTickCounter);
   }

   @Override
   public Executor mfix$getMainThreadExecutor() {
      return this.mainThreadExecutor;
   }
}
