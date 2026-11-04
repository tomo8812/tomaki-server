package org.embeddedt.modernfix.util;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.PreparableReloadListener.PreparationBarrier;
import net.minecraft.util.profiling.ProfilerFiller;

public class NamedPreparableResourceListener implements PreparableReloadListener {
   private final PreparableReloadListener delegate;

   public NamedPreparableResourceListener(PreparableReloadListener delegate) {
      this.delegate = delegate;
   }

   public CompletableFuture<Void> reload(
      PreparationBarrier stage,
      ResourceManager resourceManager,
      ProfilerFiller preparationsProfiler,
      ProfilerFiller reloadProfiler,
      Executor backgroundExecutor,
      Executor gameExecutor
   ) {
      return this.delegate.reload(stage, resourceManager, preparationsProfiler, reloadProfiler, backgroundExecutor, gameExecutor);
   }

   public String getName() {
      return this.delegate.getName() + " [" + this.delegate.getClass().getName() + "]";
   }
}
