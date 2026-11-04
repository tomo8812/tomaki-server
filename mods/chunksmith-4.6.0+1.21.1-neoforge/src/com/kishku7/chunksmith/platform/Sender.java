package com.kishku7.chunksmith.platform;

import com.kishku7.chunksmith.platform.util.Location;

public interface Sender {
   boolean isPlayer();

   String getName();

   World getWorld();

   Location getLocation();

   boolean hasPermission(String var1);

   void sendMessage(String var1, boolean var2, Object... var3);

   default void sendMessage(String key, Object... args) {
      this.sendMessage(key, false, args);
   }

   default void sendMessagePrefixed(String key, Object... args) {
      this.sendMessage(key, true, args);
   }
}
