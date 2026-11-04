package com.kishku7.chunksmith.lod.net;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.Base64.Encoder;
import java.util.concurrent.ConcurrentHashMap;

public final class CsLodTokens {
   private static final SecureRandom RANDOM = new SecureRandom();
   private static final Encoder ENCODER = Base64.getUrlEncoder().withoutPadding();
   private final Map<String, CsLodTokens.Entry> byToken = new ConcurrentHashMap<>();
   private final Map<UUID, String> byPlayer = new ConcurrentHashMap<>();

   public String issue(UUID player, String ip) {
      this.revoke(player);
      byte[] bytes = new byte[32];
      RANDOM.nextBytes(bytes);
      String token = ENCODER.encodeToString(bytes);
      this.byToken.put(token, new CsLodTokens.Entry(player, ip, System.currentTimeMillis() + 600000L));
      this.byPlayer.put(player, token);
      return token;
   }

   public UUID validate(String token, String ip, CsLodTokens.OnlineCheck online) {
      if (token != null && !token.isEmpty()) {
         CsLodTokens.Entry entry = this.byToken.get(token);
         if (entry == null) {
            return null;
         } else if (System.currentTimeMillis() > entry.expiresAt) {
            this.byToken.remove(token);
            this.byPlayer.remove(entry.player, token);
            return null;
         } else if (!entry.ip.equals(ip)) {
            return null;
         } else {
            return !online.isOnline(entry.player) ? null : entry.player;
         }
      } else {
         return null;
      }
   }

   public void revoke(UUID player) {
      String existing = this.byPlayer.remove(player);
      if (existing != null) {
         this.byToken.remove(existing);
      }
   }

   public void clear() {
      this.byToken.clear();
      this.byPlayer.clear();
   }

   public int size() {
      return this.byToken.size();
   }

   private static final class Entry {
      private final UUID player;
      private final String ip;
      private final long expiresAt;

      private Entry(UUID player, String ip, long expiresAt) {
         this.player = player;
         this.ip = ip;
         this.expiresAt = expiresAt;
      }
   }

   @FunctionalInterface
   public interface OnlineCheck {
      boolean isOnline(UUID var1);
   }
}
