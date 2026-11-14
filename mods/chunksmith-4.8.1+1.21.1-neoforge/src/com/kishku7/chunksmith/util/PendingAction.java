package com.kishku7.chunksmith.util;

import java.util.concurrent.TimeUnit;

public class PendingAction {
   private final Runnable action;
   private final long expiry;

   public PendingAction(Runnable action) {
      this.action = action;
      this.expiry = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1L);
   }

   public Runnable getAction() {
      return this.action;
   }

   public boolean hasExpired() {
      return System.currentTimeMillis() > this.expiry;
   }
}
