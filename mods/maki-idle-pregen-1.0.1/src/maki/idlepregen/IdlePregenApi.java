package maki.idlepregen;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class IdlePregenApi {
   private static final Set<String> EXTERNAL_IDLE_WORK = ConcurrentHashMap.newKeySet();

   private IdlePregenApi() {
   }

   public static boolean isActive() {
      return IdlePregenController.isActive();
   }

   public static void setExternalIdleWorkActive(String var0, boolean var1) {
      if (var0 != null && !var0.isBlank()) {
         if (var1) {
            EXTERNAL_IDLE_WORK.add(var0);
         } else {
            EXTERNAL_IDLE_WORK.remove(var0);
         }
      } else {
         throw new IllegalArgumentException("owner must not be blank");
      }
   }

   static boolean hasExternalIdleWork() {
      return !EXTERNAL_IDLE_WORK.isEmpty();
   }

   static void clearExternalIdleWork() {
      EXTERNAL_IDLE_WORK.clear();
   }
}
