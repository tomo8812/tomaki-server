package com.kishku7.chunksmith.platform;

import com.kishku7.chunksmith.PlatformCompat;
import com.kishku7.chunksmith.ducks.MinecraftServerExtension;
import com.kishku7.chunksmith.lod.LodSupport;
import com.kishku7.chunksmith.mixin.ChunkMapMixin;
import com.kishku7.chunksmith.mixin.ChunkStorageAccessor;
import com.kishku7.chunksmith.mixin.IOWorkerAccessor;
import com.kishku7.chunksmith.mixin.ServerChunkCacheMixin;
import com.kishku7.chunksmith.platform.util.Location;
import com.kishku7.chunksmith.util.ChunkSettleSupport;
import com.kishku7.chunksmith.util.ChunkSettleWindow;
import com.kishku7.chunksmith.util.Input;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Unit;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.IOWorker;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.storage.LevelResource;

public class NeoForgeWorld implements World {
   private static final TicketType<Unit> CHUNKY = TicketType.create("chunky", (unit, unit2) -> 0);
   private static final boolean UPDATE_CHUNK_NBT = Boolean.getBoolean("chunky.updateChunkNbt");
   private final ServerLevel world;
   private final Border worldBorder;
   private ChunkSettleWindow settleWindow;

   public NeoForgeWorld(ServerLevel world) {
      this.world = world;
      this.worldBorder = new NeoForgeBorder(world.getWorldBorder());
   }

   @Override
   public String getName() {
      return this.world.dimension().location().toString();
   }

   @Override
   public String getKey() {
      return this.getName();
   }

   @Override
   public CompletableFuture<Boolean> isChunkGenerated(int x, int z) {
      if (Thread.currentThread() != this.world.getServer().getRunningThread()) {
         return CompletableFuture.<CompletableFuture<Boolean>>supplyAsync(() -> this.isChunkGenerated(x, z), this.world.getServer())
            .thenCompose(Function.identity());
      }

      ChunkPos chunkPos = new ChunkPos(x, z);
      ServerChunkCache serverChunkCache = this.world.getChunkSource();
      ChunkMap chunkStorage = serverChunkCache.chunkMap;
      ChunkMapMixin chunkMapMixin = (ChunkMapMixin)chunkStorage;
      ChunkHolder loadedChunkHolder = chunkMapMixin.invokeGetVisibleChunkIfPresent(chunkPos.toLong());
      if (loadedChunkHolder != null && loadedChunkHolder.getLatestStatus() == ChunkStatus.FULL) {
         return CompletableFuture.completedFuture(true);
      }

      if (UPDATE_CHUNK_NBT) {
         return chunkMapMixin.invokeReadChunk(chunkPos)
            .thenApply(
               optionalNbt -> optionalNbt.filter(chunkNbt -> chunkNbt.contains("Status"))
                  .map(chunkNbt -> chunkNbt.getString("Status"))
                  .map(status -> "minecraft:full".equals(status) || "full".equals(status))
                  .orElse(false)
            );
      }

      FieldSelector statusSelector = new FieldSelector(StringTag.TYPE, "Status");
      CollectFields statusCollector = new CollectFields(new FieldSelector[]{statusSelector});
      return serverChunkCache.chunkScanner().scanChunk(chunkPos, statusCollector).thenApply(ignored -> {
         if (!(statusCollector.getResult() instanceof CompoundTag chunkNbt)) {
            return false;
         } else {
            String status = chunkNbt.getString("Status");
            return "minecraft:full".equals(status) || "full".equals(status);
         }
      });
   }

   private Executor ticketSafePoint() {
      return ((MinecraftServerExtension)this.world.getServer())::chunksmith$atTicketSafePoint;
   }

   @Override
   public CompletableFuture<Void> getChunkAtAsync(int x, int z) {
      if (!((MinecraftServerExtension)this.world.getServer()).chunksmith$onTicketSafePoint()) {
         return CompletableFuture.<CompletableFuture<Void>>supplyAsync(() -> this.getChunkAtAsync(x, z), this.ticketSafePoint())
            .thenCompose(Function.identity());
      }

      ChunkPos chunkPos = new ChunkPos(x, z);
      ServerChunkCache serverChunkCache = this.world.getChunkSource();
      if (this.settleWindow == null) {
         this.settleWindow = ChunkSettleSupport.newWindow();
      }

      serverChunkCache.addRegionTicket(CHUNKY, chunkPos, 0, Unit.INSTANCE);
      if (!PlatformCompat.ENABLE_C2ME_TICKET_COMPAT) {
         ((ServerChunkCacheMixin)serverChunkCache).invokeRunDistanceManagerUpdates();
      }

      boolean create = PlatformCompat.ENABLE_MOONRISE_WORKAROUNDS || PlatformCompat.ENABLE_C2ME_TICKET_COMPAT;
      return ((ServerChunkCacheMixin)this.world.getChunkSource())
         .invokeGetChunkFutureMainThread(x, z, ChunkStatus.FULL, create)
         .thenApplyAsync(Function.identity(), ((ChunkMapMixin)serverChunkCache.chunkMap).getMainThreadExecutor())
         .whenCompleteAsync((result, throwable) -> {
            if (throwable == null && result != null) {
               result.ifSuccess(chunkAccess -> {
                  if (chunkAccess instanceof LevelChunk levelChunk) {
                     LodSupport.offer(this.world, levelChunk);
                  }
               });
            }

            if (this.settleWindow == null) {
               serverChunkCache.removeRegionTicket(CHUNKY, chunkPos, 0, Unit.INSTANCE);
            } else {
               this.settleWindow.offer(x, z, this.world.getGameTime(), () -> serverChunkCache.removeRegionTicket(CHUNKY, chunkPos, 0, Unit.INSTANCE));
            }

            ((MinecraftServerExtension)this.world.getServer()).chunksmith$markChunkSystemHousekeeping();
         }, this.ticketSafePoint())
         .thenApply(ignored -> null);
   }

   @Override
   public UUID getUUID() {
      throw new UnsupportedOperationException();
   }

   @Override
   public void settleDrain() {
      ChunkSettleWindow window = this.settleWindow;
      if (window != null) {
         this.settleWindow = null;
         this.ticketSafePoint().execute(window::drain);
      }
   }

   @Override
   public void settleLoad(int chunkX, int chunkZ, int radius) {
      this.ticketSafePoint().execute(() -> this.world.getChunkSource().addRegionTicket(CHUNKY, new ChunkPos(chunkX, chunkZ), radius, Unit.INSTANCE));
   }

   @Override
   public void settleRelease(int chunkX, int chunkZ, int radius) {
      this.ticketSafePoint().execute(() -> this.world.getChunkSource().removeRegionTicket(CHUNKY, new ChunkPos(chunkX, chunkZ), radius, Unit.INSTANCE));
   }

   @Override
   public int getSeaLevel() {
      return this.world.getSeaLevel();
   }

   @Override
   public Location getSpawn() {
      BlockPos pos = this.world.getSharedSpawnPos();
      float yaw = this.world.getSharedSpawnAngle();
      return new Location(this, pos.getX(), pos.getY(), pos.getZ(), yaw, 0.0F);
   }

   @Override
   public Border getWorldBorder() {
      return this.worldBorder;
   }

   @Override
   public int getElevation(int x, int z) {
      int height = this.world.getHeight(Types.MOTION_BLOCKING, x, z) + 1;
      int logicalHeight = this.world.getLogicalHeight();
      if (height >= logicalHeight) {
         MutableBlockPos pos = new MutableBlockPos(x, logicalHeight, z);
         int air = 0;

         while (pos.getY() > this.world.getMinBuildHeight()) {
            pos = pos.move(Direction.DOWN);
            BlockState blockState = this.world.getBlockState(pos);
            if (blockState.isSolid() && air > 1) {
               return pos.getY() + 1;
            }

            air = blockState.isAir() ? air + 1 : 0;
         }
      }

      return height;
   }

   @Override
   public int getMaxElevation() {
      return this.world.getLogicalHeight();
   }

   @Override
   public void playEffect(Player player, String effect) {
      Location location = player.getLocation();
      BlockPos pos = BlockPos.containing(location.getX(), location.getY(), location.getZ());
      Input.tryInteger(effect).ifPresent(eventId -> this.world.levelEvent(eventId, pos, 0));
   }

   @Override
   public void playSound(Player player, String sound) {
      Location location = player.getLocation();
      ResourceLocation soundId = ResourceLocation.tryParse(sound);
      if (soundId != null) {
         this.world
            .getServer()
            .registryAccess()
            .registryOrThrow(Registries.SOUND_EVENT)
            .getOptional(soundId)
            .ifPresent(soundEvent -> this.world.playSound(null, location.getX(), location.getY(), location.getZ(), soundEvent, SoundSource.MASTER, 2.0F, 1.0F));
      }
   }

   @Override
   public Optional<Path> getDirectory(String name) {
      if (name == null) {
         return Optional.empty();
      }

      ResourceKey<Level> dimension = this.world.dimension();
      Path directory = DimensionType.getStorageFolder(dimension, this.world.getServer().getWorldPath(LevelResource.ROOT)).normalize().resolve(name);
      return Files.exists(directory) ? Optional.of(directory) : Optional.empty();
   }

   @Override
   public long getQueuedChunkWrites() {
      try {
         ChunkMap chunkMap = this.world.getChunkSource().chunkMap;
         IOWorker worker = ((ChunkStorageAccessor)chunkMap).chunksmith$getWorker();
         if (worker == null) {
            return -1L;
         }

         Map<?, ?> pendingWrites = ((IOWorkerAccessor)worker).chunksmith$getPendingWrites();
         return pendingWrites == null ? -1L : pendingWrites.size();
      } catch (Throwable t) {
         return -1L;
      }
   }

   public ServerLevel getWorld() {
      return this.world;
   }
}
