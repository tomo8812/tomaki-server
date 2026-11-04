package net.caffeinemc.mods.lithium.mixin.ai.poi;

import com.google.common.collect.AbstractIterator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.caffeinemc.mods.lithium.common.util.Pos;
import net.caffeinemc.mods.lithium.common.util.collections.ListeningLong2ObjectOpenHashMap;
import net.caffeinemc.mods.lithium.common.world.interests.RegionBasedStorageSectionExtended;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionStorage.class)
public abstract class SectionStorageMixin<R> implements RegionBasedStorageSectionExtended<R> {
   @Mutable
   @Shadow
   @Final
   private Long2ObjectMap<Optional<R>> storage;
   @Shadow
   @Final
   protected LevelHeightAccessor levelHeightAccessor;
   private Long2ObjectOpenHashMap<BitSet> columns;

   @Shadow
   protected abstract Optional<R> get(long var1);

   @Shadow
   protected abstract void readColumn(ChunkPos var1);

   @Inject(method = "<init>", at = @At("RETURN"))
   private void init(
      SimpleRegionStorage storageAccess,
      Function codecFactory,
      Function factory,
      RegistryAccess registryManager,
      ChunkIOErrorReporter errorHandler,
      LevelHeightAccessor world,
      CallbackInfo ci
   ) {
      this.columns = new Long2ObjectOpenHashMap();
      this.storage = new ListeningLong2ObjectOpenHashMap<Optional<R>>(this::onEntryAdded, this::onEntryRemoved);
   }

   private void onEntryRemoved(long key, Optional<R> value) {
      int y = Pos.SectionYIndex.fromSectionCoord(this.levelHeightAccessor, SectionPos.y(key));
      if (y >= 0 && y < Pos.SectionYIndex.getNumYSections(this.levelHeightAccessor)) {
         int x = SectionPos.x(key);
         int z = SectionPos.z(key);
         long pos = ChunkPos.asLong(x, z);
         BitSet flags = (BitSet)this.columns.get(pos);
         if (flags != null) {
            flags.clear(y);
            if (flags.isEmpty()) {
               this.columns.remove(pos);
            }
         }
      }
   }

   private void onEntryAdded(long key, Optional<R> value) {
      int y = Pos.SectionYIndex.fromSectionCoord(this.levelHeightAccessor, SectionPos.y(key));
      if (y >= 0 && y < Pos.SectionYIndex.getNumYSections(this.levelHeightAccessor)) {
         int x = SectionPos.x(key);
         int z = SectionPos.z(key);
         long pos = ChunkPos.asLong(x, z);
         BitSet flags = (BitSet)this.columns.get(pos);
         if (flags == null) {
            this.columns.put(pos, flags = new BitSet(Pos.SectionYIndex.getNumYSections(this.levelHeightAccessor)));
         }

         flags.set(y, value.isPresent());
      }
   }

   @Override
   public Stream<R> lithium$getWithinChunkColumn(int chunkX, int chunkZ) {
      BitSet sectionsWithPOI = this.getNonEmptyPOISections(chunkX, chunkZ);
      if (sectionsWithPOI.isEmpty()) {
         return Stream.empty();
      }

      List<R> list = new ArrayList<>();
      int minYSection = Pos.SectionYCoord.getMinYSection(this.levelHeightAccessor);

      for (int chunkYIndex = sectionsWithPOI.nextSetBit(0); chunkYIndex != -1; chunkYIndex = sectionsWithPOI.nextSetBit(chunkYIndex + 1)) {
         int chunkY = chunkYIndex + minYSection;
         R r = (R)((Optional)this.storage.get(SectionPos.asLong(chunkX, chunkY, chunkZ))).orElse(null);
         if (r != null) {
            list.add(r);
         }
      }

      return list.stream();
   }

   @Override
   public Iterable<R> lithium$getInChunkColumn(int chunkX, int chunkZ) {
      BitSet sectionsWithPOI = this.getNonEmptyPOISections(chunkX, chunkZ);
      if (sectionsWithPOI.isEmpty()) {
         return Collections::emptyIterator;
      }

      Long2ObjectMap<Optional<R>> loadedElements = this.storage;
      LevelHeightAccessor world = this.levelHeightAccessor;
      return () -> new AbstractIterator<R>() {
         private int nextBit = sectionsWithPOI.nextSetBit(0);

         protected R computeNext() {
            while (this.nextBit >= 0) {
               Optional<R> next = (Optional<R>)loadedElements.get(SectionPos.asLong(chunkX, Pos.SectionYCoord.fromSectionIndex(world, this.nextBit), chunkZ));
               this.nextBit = sectionsWithPOI.nextSetBit(this.nextBit + 1);
               if (next.isPresent()) {
                  return next.get();
               }
            }

            return (R)this.endOfData();
         }
      };
   }

   private BitSet getNonEmptyPOISections(int chunkX, int chunkZ) {
      long pos = ChunkPos.asLong(chunkX, chunkZ);
      BitSet flags = this.getNonEmptySections(pos, false);
      if (flags != null) {
         return flags;
      }

      this.readColumn(new ChunkPos(pos));
      return this.getNonEmptySections(pos, true);
   }

   private BitSet getNonEmptySections(long pos, boolean required) {
      BitSet set = (BitSet)this.columns.get(pos);
      if (set == null && required) {
         throw new NullPointerException("No data is present for column: " + new ChunkPos(pos));
      } else {
         return set;
      }
   }
}
