package com.kishku7.chunksmith.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ChunkMap.class)
public interface ChunkMapMixin {
   @Invoker("getVisibleChunkIfPresent")
   ChunkHolder invokeGetVisibleChunkIfPresent(long var1);

   @Invoker("readChunk")
   CompletableFuture<Optional<CompoundTag>> invokeReadChunk(ChunkPos var1);

   @Invoker
   void invokeTick(BooleanSupplier var1);

   @Accessor
   BlockableEventLoop<Runnable> getMainThreadExecutor();

   @Accessor("toDrop")
   LongSet getToDrop();

   @Accessor("unloadQueue")
   Queue<Runnable> getUnloadQueue();

   @Accessor("pendingUnloads")
   Long2ObjectLinkedOpenHashMap<ChunkHolder> getPendingUnloads();

   @Accessor("visibleChunkMap")
   Long2ObjectLinkedOpenHashMap<ChunkHolder> getVisibleChunkMap();
}
