package com.kishku7.chunksmith.platform;

import com.kishku7.chunksmith.integration.Integration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Server {
   Map<String, Integration> getIntegrations();

   Optional<World> getWorld(String var1);

   List<World> getWorlds();

   int getMaxWorldSize();

   Sender getConsole();

   Collection<Player> getPlayers();

   Optional<Player> getPlayer(String var1);

   Config getConfig();

   default double getMillisPerTick() {
      return -1.0;
   }
}
