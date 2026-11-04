package com.kishku7.chunksmith.lod.net;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CsLodCapNotice {
   public static final long QUIET_MILLIS = 600000L;
   private final Map<UUID, Long> lastWarned = new ConcurrentHashMap<>();

   public boolean shouldWarn(UUID player, long nowMillis) {
      Long previous = this.lastWarned.get(player);
      if (previous != null && nowMillis - previous < 600000L) {
         return false;
      }

      this.lastWarned.put(player, nowMillis);
      return true;
   }

   public void forget(UUID player) {
      this.lastWarned.remove(player);
   }

   public void clear() {
      this.lastWarned.clear();
   }

   public static String explain(CsLodIndexScan.Result result, long budgetMb) {
      return result.cappedByBudget()
         ? "lodIndexBudgetMb is set to "
            + budgetMb
            + " MB, which is what stopped the list. Regions past it will not be sent from anywhere the player stands; raise it or set it to 0 for no limit."
         : "the hard limit of 4096 regions per answer stopped the list. This one is measured nearest-first from the player, so travelling does bring the rest in.";
   }
}
