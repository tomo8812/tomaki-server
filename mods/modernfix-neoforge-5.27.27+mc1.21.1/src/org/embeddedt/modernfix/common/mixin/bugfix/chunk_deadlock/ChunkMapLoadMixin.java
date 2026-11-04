package org.embeddedt.modernfix.common.mixin.bugfix.chunk_deadlock;

import com.llamalad7.mixinextras.sugar.Local;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache.MainThreadExecutor;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkStatusTasks.class)
public abstract class ChunkMapLoadMixin {
   @Unique
   private static final ThreadLocal<CompletableFuture<ChunkAccess>> MFIX_SURROGATE_FUTURE = new ThreadLocal<>();

   @Redirect(
      method = "full",
      at = @At(
         value = "INVOKE",
         target = "Ljava/util/concurrent/CompletableFuture;supplyAsync(Ljava/util/function/Supplier;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;",
         ordinal = 0
      )
   )
   private static CompletableFuture<ChunkAccess> createSurrogateFuture(
      Supplier<ChunkAccess> supplier, Executor executor, @Local(ordinal = 0, argsOnly = true) WorldGenContext worldGenContext
   ) {
      CompletableFuture<ChunkAccess> surrogate = new CompletableFuture<>();
      MainThreadExecutor mainThreadExecutor = ((ServerChunkCacheAccessor)worldGenContext.level().getChunkSource()).mfix$getMainThreadProcessor();
      CompletableFuture.runAsync(() -> {}, executor).thenApplyAsync($ -> {
         MFIX_SURROGATE_FUTURE.set(surrogate);

         try {
            return supplier.get();
         } finally {
            MFIX_SURROGATE_FUTURE.remove();
         }
      }, mainThreadExecutor).whenComplete((either, throwable) -> {
         if (throwable != null) {
            if (!surrogate.isDone()) {
               surrogate.completeExceptionally(throwable);
            } else {
               ReportedException exc = new ReportedException(CrashReport.forThrowable(throwable, "Exception during promotion of chunk to FULL status"));
               MinecraftServer.setFatalException(exc);
            }
         } else {
            surrogate.complete(either);
         }
      });
      return surrogate;
   }

   @Inject(method = "lambda$full$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/chunk/LevelChunk;runPostLoad()V"))
   private static void completeSurrogateFuture(CallbackInfoReturnable<ChunkAccess> cir, @Local(ordinal = 0) LevelChunk levelChunk) {
      CompletableFuture<ChunkAccess> future = MFIX_SURROGATE_FUTURE.get();
      if (future != null) {
         future.complete(levelChunk);
      }
   }
}
