package net.caffeinemc.mods.lithium.common.world.chunk;

import java.util.ArrayList;
import java.util.function.BiConsumer;
import net.caffeinemc.mods.lithium.common.tracking.block.ChunkSectionChangeCallback;
import net.caffeinemc.mods.lithium.mixin.util.accessors.LevelAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;

public class ChunkStatusTracker {
   private static final ArrayList<BiConsumer<ServerLevel, ChunkPos>> UNLOAD_CALLBACKS = new ArrayList<>();
   private static final ArrayList<BiConsumer<ServerLevel, LevelChunk>> LOAD_CALLBACKS = new ArrayList<>();

   public static void registerLoadCallback(BiConsumer<ServerLevel, LevelChunk> callback) {
      synchronized (LOAD_CALLBACKS) {
         LOAD_CALLBACKS.add(callback);
      }
   }

   public static void registerUnloadCallback(BiConsumer<ServerLevel, ChunkPos> callback) {
      synchronized (UNLOAD_CALLBACKS) {
         UNLOAD_CALLBACKS.add(callback);
      }
   }

   public static void onChunkAccessible(ServerLevel serverLevel, LevelChunk levelChunk) {
      if (((LevelAccessor)serverLevel).getThread() != Thread.currentThread()) {
         throw new IllegalStateException("ChunkStatusTracker.onChunkAccessible called on wrong thread!");
      }

      for (int i = 0; i < LOAD_CALLBACKS.size(); i++) {
         LOAD_CALLBACKS.get(i).accept(serverLevel, levelChunk);
      }
   }

   public static void onChunkInaccessible(ServerLevel serverLevel, ChunkPos pos) {
      if (((LevelAccessor)serverLevel).getThread() != Thread.currentThread()) {
         throw new IllegalStateException("ChunkStatusTracker.onChunkInaccessible called on wrong thread!");
      }

      for (int i = 0; i < UNLOAD_CALLBACKS.size(); i++) {
         UNLOAD_CALLBACKS.get(i).accept(serverLevel, pos);
      }
   }

   static {
      ChunkSectionChangeCallback.init();
   }
}
