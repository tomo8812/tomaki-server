package com.kishku7.chunksmith.lod.net;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CsLodReachNotice {
   public static final long QUIET_MILLIS = 600000L;
   private final Map<UUID, Long> lastWarned = new ConcurrentHashMap<>();
   private final Set<UUID> answeredOnce = ConcurrentHashMap.newKeySet();

   public boolean shouldWarn(UUID player, long nowMillis) {
      Long previous = this.lastWarned.get(player);
      if (previous != null && nowMillis - previous < 600000L) {
         return false;
      }

      this.lastWarned.put(player, nowMillis);
      return true;
   }

   public boolean hasBeenAnsweredBefore(UUID player) {
      return !this.answeredOnce.add(player);
   }

   public void forget(UUID player) {
      this.lastWarned.remove(player);
      this.answeredOnce.remove(player);
   }

   public void clear() {
      this.lastWarned.clear();
      this.answeredOnce.clear();
   }

   public static boolean unreached(boolean running, long served, long rejected) {
      return running && served == 0L && rejected == 0L;
   }

   public static String explain(int port, boolean inBandFallback) {
      String consequence = inBandFallback
         ? " so clients are falling back to the slow in-band channel and will only get LOD for ground they have walked."
         : " and this platform has NO in-band fallback, so those clients are getting no LOD at all.";
      String closing = inBandFallback
         ? " Nothing is broken and no data is lost; this is only about speed."
         : " Nothing is damaged and no data is lost -- but until that port is reachable, LOD will not arrive.";
      return "Chunksmith: the LOD backchannel on port "
         + port
         + " is running but has never received a single request,"
         + consequence
         + " That port is almost certainly not reachable from your players: open or forward TCP "
         + port
         + " exactly as you did the game port, or -- if the game reaches them by a different address, such as through a proxy -- point them somewhere routable with '/cs set lodBackchannelHost <host>' and '/cs set lodBackchannelPort <port>'. One cause looks nothing like a firewall and is worth checking first: if 'server-ip=' is set in server.properties, the backchannel follows it, so a value like 127.0.0.1 -- routine on a hosted panel behind a proxy -- binds it to loopback where no player can reach it, however the port is forwarded. Override that with '/cs set lodBackchannelBindAddress 0.0.0.0'."
         + closing;
   }
}
