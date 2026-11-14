package com.kishku7.chunksmith.util;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ChunkSettleSupport {
   private static volatile boolean enabled;
   private static volatile long delayTicks;
   private static volatile long maxHeld;
   private static final List<ChunkSettleWindow> LIVE = new CopyOnWriteArrayList<>();

   private ChunkSettleSupport() {
   }

   public static void configure(boolean enabled, long delayTicks, long maxHeld) {
      ChunkSettleSupport.enabled = enabled;
      ChunkSettleSupport.delayTicks = Math.max(0L, delayTicks);
      ChunkSettleSupport.maxHeld = Math.max(0L, maxHeld);
   }

   public static boolean isEnabled() {
      return enabled;
   }

   public static ChunkSettleWindow newWindow() {
      if (!enabled) {
         return null;
      }

      ChunkSettleWindow window = new ChunkSettleWindow(delayTicks, maxHeld);
      LIVE.add(window);
      return window;
   }

   public static void tick(long gameTime) {
      if (!LIVE.isEmpty()) {
         for (ChunkSettleWindow window : LIVE) {
            if (window.isDrained()) {
               LIVE.remove(window);
            } else {
               window.releaseDue(gameTime);
            }
         }
      }
   }

   public static int liveWindowCount() {
      return LIVE.size();
   }

   public static void forget() {
      LIVE.clear();
   }
}
