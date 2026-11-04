package org.embeddedt.modernfix.neoforge.util;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

public class ModUtil {
   private static final Set<Class<?>> erroredContexts = new HashSet<>();
   private static final ClassLoader targetClassLoader = Thread.currentThread().getContextClassLoader();
   public static ForkJoinPool commonPool = new ForkJoinPool(ForkJoinPool.getCommonPoolParallelism(), ModUtil.ModernFixForkJoinWorkerThread::new, null, false);

   private static class ModernFixForkJoinWorkerThread extends ForkJoinWorkerThread {
      ModernFixForkJoinWorkerThread(ForkJoinPool pool) {
         super(pool);
         this.setContextClassLoader(ModUtil.targetClassLoader);
      }
   }
}
