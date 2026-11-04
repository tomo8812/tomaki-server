package com.wodichka.disabler.item;

import com.wodichka.disabler.config.DisablerConfig;
import com.wodichka.disabler.mixin.ChunkMapAccessor;
import java.util.ArrayDeque;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public final class StorageInventoryScanner {
   private final StorageInventoryScanner.PlatformStorageSanitizer platformSanitizer;
   private final Queue<BlockEntity> pendingBlockEntities = new ArrayDeque<>();
   private int ticksUntilScan = 1;

   public StorageInventoryScanner(StorageInventoryScanner.PlatformStorageSanitizer platformSanitizer) {
      this.platformSanitizer = platformSanitizer;
   }

   public void tick(MinecraftServer server) {
      if (DisablerConfig.hasBlockedItems() && DisablerConfig.shouldScanStorageInventories()) {
         if (this.pendingBlockEntities.isEmpty()) {
            if (--this.ticksUntilScan > 0) {
               return;
            }

            this.collectLoadedBlockEntities(server);
            this.ticksUntilScan = DisablerConfig.getStorageScanIntervalTicks();
         }

         this.processPendingBlockEntities(DisablerConfig.getStorageScanBlockEntitiesPerTick());
      } else {
         this.pendingBlockEntities.clear();
         this.ticksUntilScan = 1;
      }
   }

   private void collectLoadedBlockEntities(MinecraftServer server) {
      this.pendingBlockEntities.clear();

      for (ServerLevel level : server.getAllLevels()) {
         for (ChunkHolder chunkHolder : ((ChunkMapAccessor)level.getChunkSource().chunkMap).disabler$getChunks()) {
            LevelChunk chunk = chunkHolder.getTickingChunk();
            if (chunk != null) {
               this.pendingBlockEntities.addAll(chunk.getBlockEntities().values());
            }
         }
      }
   }

   private void processPendingBlockEntities(int limit) {
      for (int processed = 0; processed < limit && !this.pendingBlockEntities.isEmpty(); processed++) {
         BlockEntity blockEntity = this.pendingBlockEntities.poll();
         if (blockEntity != null && !blockEntity.isRemoved() && blockEntity.hasLevel()) {
            this.sanitizeBlockEntity(blockEntity);
         }
      }
   }

   private void sanitizeBlockEntity(BlockEntity blockEntity) {
      if (!isLootrBlockEntity(blockEntity) && !shouldSkipBlockEntity(blockEntity)) {
         boolean changed = blockEntity instanceof Container container && BlockedItemCleaner.sanitizeContainer(container);
         changed |= this.platformSanitizer.sanitize(blockEntity);
         if (changed) {
            blockEntity.setChanged();
         }
      }
   }

   private static boolean shouldSkipBlockEntity(BlockEntity blockEntity) {
      Set<String> skippedNamespaces = DisablerConfig.getStorageScanSkippedNamespaces();
      if (skippedNamespaces.isEmpty()) {
         return false;
      }

      ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockEntity.getBlockState().getBlock());
      if (blockId != null && skippedNamespaces.contains(blockId.getNamespace())) {
         return true;
      }

      ResourceLocation typeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
      if (typeId != null && skippedNamespaces.contains(typeId.getNamespace())) {
         return true;
      }

      String className = blockEntity.getClass().getName().toLowerCase(Locale.ROOT);
      return skippedNamespaces.stream().anyMatch(namespace -> className.contains("." + namespace + ".") || className.startsWith(namespace + "."));
   }

   private static boolean isLootrBlockEntity(BlockEntity blockEntity) {
      ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(blockEntity.getBlockState().getBlock());
      if (blockId != null && "lootr".equals(blockId.getNamespace())) {
         return true;
      }

      ResourceLocation typeId = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(blockEntity.getType());
      return typeId != null && "lootr".equals(typeId.getNamespace()) ? true : blockEntity.getClass().getName().startsWith("noobanidus.mods.lootr.");
   }

   @FunctionalInterface
   public interface PlatformStorageSanitizer {
      boolean sanitize(BlockEntity var1);
   }
}
