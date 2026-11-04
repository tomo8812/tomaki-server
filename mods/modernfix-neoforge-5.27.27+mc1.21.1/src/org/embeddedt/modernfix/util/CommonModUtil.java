package org.embeddedt.modernfix.util;

import org.embeddedt.modernfix.core.ModernFixMixinPlugin;

public class CommonModUtil {
   public static void runWithoutCrash(CommonModUtil.SafeRunnable r, String errorMsg) {
      try {
         r.run();
      } catch (Throwable e) {
         ModernFixMixinPlugin.instance.logger.error(errorMsg, e);
      }
   }

   @FunctionalInterface
   public interface SafeRunnable {
      void run() throws Throwable;
   }
}
