package com.kishku7.chunksmith.platform;

import com.kishku7.chunksmith.platform.util.Location;
import java.util.UUID;

public interface Player extends Sender {
   UUID getUUID();

   void teleport(Location var1);

   void sendActionBar(String var1);
}
