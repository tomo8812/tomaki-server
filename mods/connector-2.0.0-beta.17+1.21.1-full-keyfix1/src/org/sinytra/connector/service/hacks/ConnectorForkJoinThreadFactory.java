package org.sinytra.connector.service.hacks;

import com.mojang.logging.LogUtils;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.VarHandle;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.ForkJoinPool.ForkJoinWorkerThreadFactory;
import org.sinytra.connector.util.ConnectorUtil;
import org.slf4j.Logger;

public class ConnectorForkJoinThreadFactory implements ForkJoinWorkerThreadFactory {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ForkJoinWorkerThreadFactory factory;

   public ConnectorForkJoinThreadFactory(ForkJoinWorkerThreadFactory factory) {
      this.factory = factory;
   }

   @Override
   public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
      ForkJoinWorkerThread thread = this.factory.newThread(pool);

      try {
         thread.setContextClassLoader(Thread.currentThread().getContextClassLoader());
      } catch (Exception e) {
         System.out.println("Sinytra Connector: Error trying to create new thread: " + e);
      }

      return thread;
   }

   public static void install() {
      try {
         ForkJoinWorkerThreadFactory factory = new ConnectorForkJoinThreadFactory(ForkJoinPool.defaultForkJoinWorkerThreadFactory);
         MethodHandle handle = ConnectorUtil.TRUSTED_LOOKUP
            .findStaticSetter(ForkJoinPool.class, "defaultForkJoinWorkerThreadFactory", ForkJoinWorkerThreadFactory.class);
         handle.invoke((ForkJoinWorkerThreadFactory)factory);
         VarHandle commonHandle = ConnectorUtil.TRUSTED_LOOKUP.findVarHandle(ForkJoinPool.class, "factory", ForkJoinWorkerThreadFactory.class);
         commonHandle.set((ForkJoinPool)ForkJoinPool.commonPool(), (ForkJoinWorkerThreadFactory)factory);
      } catch (Throwable t) {
         LOGGER.error("Error injecting default thread pool factory", t);
      }
   }
}
