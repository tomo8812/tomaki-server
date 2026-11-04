package org.embeddedt.modernfix.util;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.util.thread.ProcessorMailbox;
import org.jetbrains.annotations.NotNull;

public class SingleThreadedWorkerService extends AbstractExecutorService {
   private final AtomicReference<Thread> thread = new AtomicReference<>();
   private final ExecutorService executorService = Executors.newSingleThreadExecutor(r -> {
      Thread t = new Thread(r, "Worker-Main");
      t.setPriority(1);
      this.thread.set(t);
      return t;
   });

   @Override
   public void shutdown() {
      this.executorService.shutdown();
   }

   @NotNull
   @Override
   public List<Runnable> shutdownNow() {
      return this.executorService.shutdownNow();
   }

   @Override
   public boolean isShutdown() {
      return this.executorService.isShutdown();
   }

   @Override
   public boolean isTerminated() {
      return this.executorService.isTerminated();
   }

   @Override
   public boolean awaitTermination(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
      return this.executorService.awaitTermination(timeout, unit);
   }

   private static boolean isForcedAsyncCommand(Runnable command) {
      return command instanceof ProcessorMailbox;
   }

   @Override
   public void execute(@NotNull Runnable command) {
      if (!isForcedAsyncCommand(command) && Thread.currentThread() == this.thread.get()) {
         command.run();
      } else {
         this.executorService.execute(command);
      }
   }
}
