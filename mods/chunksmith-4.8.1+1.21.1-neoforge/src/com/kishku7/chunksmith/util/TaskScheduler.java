package com.kishku7.chunksmith.util;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskScheduler {
   private static final Logger LOGGER = LoggerFactory.getLogger("Chunksmith");
   private final ExecutorService executor;
   private final Set<Future<?>> futures = ConcurrentHashMap.newKeySet();

   public TaskScheduler() {
      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 5L, TimeUnit.MINUTES, new SynchronousQueue<>());
      threadPoolExecutor.setThreadFactory(runnable -> {
         Thread thread = new Thread(runnable);
         thread.setDaemon(true);
         return thread;
      });
      threadPoolExecutor.prestartAllCoreThreads();
      threadPoolExecutor.allowCoreThreadTimeOut(true);
      this.executor = threadPoolExecutor;
   }

   public void runTask(Runnable runnable) {
      this.futures.add(this.executor.submit(() -> {
         try {
            runnable.run();
         } catch (Throwable t) {
            LOGGER.error("Generation task threw an uncaught exception", t);
            throw t;
         }
      }));
      this.futures.removeIf(Future::isDone);
   }

   public void cancelTasks() {
      for (Future<?> future : this.futures) {
         future.cancel(true);
      }

      this.futures.clear();
   }
}
