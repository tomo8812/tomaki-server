package com.kishku7.chunksmith.lod.client.render;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public final class DhPushGuard {
   private static final ThreadLocal<Boolean> PUSHING = ThreadLocal.withInitial(() -> Boolean.FALSE);
   private static final AtomicLong forced = new AtomicLong();

   private DhPushGuard() {
   }

   public static <T> T pushing(Supplier<T> push) {
      PUSHING.set(Boolean.TRUE);

      try {
         return push.get();
      } finally {
         PUSHING.set(Boolean.FALSE);
      }
   }

   public static boolean isPushing() {
      return PUSHING.get();
   }

   public static void forced() {
      forced.incrementAndGet();
   }

   public static long forcedCount() {
      return forced.get();
   }
}
