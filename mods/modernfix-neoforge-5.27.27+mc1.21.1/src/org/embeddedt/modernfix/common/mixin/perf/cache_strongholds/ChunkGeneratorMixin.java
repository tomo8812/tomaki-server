package org.embeddedt.modernfix.common.mixin.perf.cache_strongholds;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import java.lang.ref.SoftReference;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import org.embeddedt.modernfix.ModernFix;
import org.embeddedt.modernfix.duck.IChunkGenerator;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ChunkGeneratorStructureState.class)
public class ChunkGeneratorMixin implements IChunkGenerator {
   @Shadow
   @Final
   private long concentricRingsSeed;
   @Shadow
   @Final
   private BiomeSource biomeSource;
   private Path mfix$dimensionPath;
   private MinecraftServer mfix$server;
   private SoftReference<Map<String, List<ChunkPos>>> mfix$cachedPositions = new SoftReference<>(null);
   private static final String CACHE_FILENAME = "mfix_stronghold_cache_v2.nbt";

   @Override
   public void mfix$setStrongholdCachePath(Path cachePath, MinecraftServer server) {
      this.mfix$dimensionPath = cachePath;
      this.mfix$server = server;
   }

   @WrapMethod(method = "generateRingPositions")
   private CompletableFuture<List<ChunkPos>> modernfix$cacheRingPositions(
      Holder<StructureSet> structureSet,
      ConcentricRingsStructurePlacement placement,
      Operation<CompletableFuture<List<ChunkPos>>> original,
      @Share("threadPool") LocalRef<ExecutorService> threadPoolRef
   ) {
      if (this.mfix$server != null && this.mfix$dimensionPath != null) {
         String cacheKey = this.mfix$makeCacheKey(placement);
         if (cacheKey == null) {
            return (CompletableFuture<List<ChunkPos>>)original.call(new Object[]{structureSet, placement});
         }

         List<ChunkPos> cached = this.mfix$readFromCache(cacheKey);
         if (cached != null) {
            ModernFix.LOGGER.debug("Using cached stronghold positions for {}", cacheKey);
            return CompletableFuture.completedFuture(List.copyOf(cached));
         }

         MinecraftServer server = this.mfix$server;
         ExecutorService strongholdPool = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 2));
         threadPoolRef.set(strongholdPool);

         try {
            return ((CompletableFuture)original.call(new Object[]{structureSet, placement})).thenApplyAsync(positions -> {
               if (server.isRunning()) {
                  this.mfix$writeToCache(cacheKey, (List<ChunkPos>)positions);
               }

               return (List<ChunkPos>)positions;
            }, Util.ioPool());
         } finally {
            strongholdPool.shutdown();
         }
      } else {
         return (CompletableFuture<List<ChunkPos>>)original.call(new Object[]{structureSet, placement});
      }
   }

   @Redirect(
      method = "generateRingPositions",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/Util;backgroundExecutor()Ljava/util/concurrent/ExecutorService;")
   )
   private ExecutorService useDedicatedService(@Share("threadPool") LocalRef<ExecutorService> threadPoolRef) {
      ExecutorService pool = (ExecutorService)threadPoolRef.get();
      return pool != null ? pool : Util.backgroundExecutor();
   }

   private String mfix$makeCacheKey(ConcentricRingsStructurePlacement placement) {
      RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, this.mfix$server.registryAccess());
      String placementKey = ConcentricRingsStructurePlacement.CODEC.codec().encodeStart(ops, placement).result().<String>map(Tag::toString).orElse(null);
      String biomeSourceKey = BiomeSource.CODEC.encodeStart(ops, this.biomeSource).result().<String>map(Tag::toString).orElse(null);
      if (placementKey != null && biomeSourceKey != null) {
         String data = placementKey + ";biomes=" + biomeSourceKey + ";seed=" + this.concentricRingsSeed;

         try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);

            for (byte b : hash) {
               sb.append(String.format("%02x", b));
            }

            return sb.toString();
         } catch (NoSuchAlgorithmException e) {
            return null;
         }
      } else {
         ModernFix.LOGGER.warn("Failed to create cache key for concentric structure placement");
         return null;
      }
   }

   private synchronized List<ChunkPos> mfix$readFromCache(String cacheKey) {
      Map<String, List<ChunkPos>> cache = this.mfix$getOrLoadCache();
      return cache.get(cacheKey);
   }

   private synchronized void mfix$writeToCache(String cacheKey, List<ChunkPos> positions) {
      Map<String, List<ChunkPos>> cache = this.mfix$getOrLoadCache();
      cache.put(cacheKey, List.copyOf(positions));
      this.mfix$cachedPositions = new SoftReference<>(cache);
      this.mfix$saveCacheFile(cache);
   }

   private Map<String, List<ChunkPos>> mfix$getOrLoadCache() {
      Map<String, List<ChunkPos>> cache = this.mfix$cachedPositions.get();
      if (cache != null) {
         return cache;
      }

      cache = this.mfix$loadCacheFile();
      this.mfix$cachedPositions = new SoftReference<>(cache);
      return cache;
   }

   private Map<String, List<ChunkPos>> mfix$loadCacheFile() {
      Path file = this.mfix$dimensionPath.resolve("mfix_stronghold_cache_v2.nbt");
      if (!Files.exists(file)) {
         return new HashMap<>();
      }

      try {
         CompoundTag root = NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap());
         Map<String, List<ChunkPos>> result = new HashMap<>();

         for (String key : root.getAllKeys()) {
            if (root.contains(key, 11)) {
               int[] data = root.getIntArray(key);
               if (data.length >= 2 && data.length % 2 == 0) {
                  List<ChunkPos> positions = new ArrayList<>(data.length / 2);

                  for (int i = 0; i < data.length; i += 2) {
                     positions.add(new ChunkPos(data[i], data[i + 1]));
                  }

                  result.put(key, positions);
               }
            }
         }

         return result;
      } catch (Exception e) {
         ModernFix.LOGGER.warn("Failed to read stronghold cache, will recompute", e);
         return new HashMap<>();
      }
   }

   private void mfix$saveCacheFile(Map<String, List<ChunkPos>> cache) {
      CompoundTag root = new CompoundTag();

      for (Entry<String, List<ChunkPos>> entry : cache.entrySet()) {
         List<ChunkPos> positions = entry.getValue();
         int[] data = new int[positions.size() * 2];

         for (int i = 0; i < positions.size(); i++) {
            ChunkPos pos = positions.get(i);
            data[i * 2] = pos.x;
            data[i * 2 + 1] = pos.z;
         }

         root.putIntArray(entry.getKey(), data);
      }

      Path file = this.mfix$dimensionPath.resolve("mfix_stronghold_cache_v2.nbt");

      try {
         NbtIo.writeCompressed(root, file);
      } catch (Exception e) {
         ModernFix.LOGGER.warn("Failed to write stronghold cache", e);
      }
   }
}
