package com.kishku7.chunksmith.platform;

import com.kishku7.chunksmith.ChunksmithNeoForge;
import com.kishku7.chunksmith.ducks.MinecraftServerExtension;
import com.kishku7.chunksmith.integration.Integration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerInterface;
import net.minecraft.server.level.ServerLevel;

public class NeoForgeServer implements Server {
   private final ChunksmithNeoForge plugin;
   private final MinecraftServer server;

   public NeoForgeServer(ChunksmithNeoForge plugin, MinecraftServer server) {
      this.plugin = plugin;
      this.server = server;
   }

   @Override
   public Map<String, Integration> getIntegrations() {
      return Map.of();
   }

   @Override
   public Optional<World> getWorld(String name) {
      return Optional.ofNullable(ResourceLocation.tryParse(name))
         .map(resourceLocation -> this.server.getLevel(ResourceKey.create(Registries.DIMENSION, resourceLocation)))
         .or(() -> {
            for (ServerLevel level : this.server.getAllLevels()) {
               if (name.equals(level.dimension().location().getPath())) {
                  return Optional.of(level);
               }
            }

            return Optional.empty();
         })
         .map(NeoForgeWorld::new);
   }

   @Override
   public List<World> getWorlds() {
      List<World> worlds = new ArrayList<>();
      this.server.getAllLevels().forEach(world -> worlds.add(new NeoForgeWorld(world)));
      return worlds;
   }

   @Override
   public int getMaxWorldSize() {
      return this.server instanceof ServerInterface serverInterface ? serverInterface.getProperties().maxWorldSize : this.server.getAbsoluteMaxWorldSize();
   }

   @Override
   public Sender getConsole() {
      return new NeoForgeSender(this.server.createCommandSourceStack());
   }

   @Override
   public Collection<Player> getPlayers() {
      return this.server.getPlayerList().getPlayers().stream().map(NeoForgePlayer::new).collect(Collectors.toList());
   }

   @Override
   public Optional<Player> getPlayer(String name) {
      return Optional.ofNullable(this.server.getPlayerList().getPlayerByName(name)).map(NeoForgePlayer::new);
   }

   @Override
   public Config getConfig() {
      return this.plugin.getChunky().getConfig();
   }

   @Override
   public double getMillisPerTick() {
      return ((MinecraftServerExtension)this.server).chunksmith$getMillisPerTick();
   }
}
