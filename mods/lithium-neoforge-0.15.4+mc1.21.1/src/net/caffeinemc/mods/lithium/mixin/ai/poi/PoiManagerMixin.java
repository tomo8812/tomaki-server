package net.caffeinemc.mods.lithium.mixin.ai.poi;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.caffeinemc.mods.lithium.common.util.Distances;
import net.caffeinemc.mods.lithium.common.world.interests.PointOfInterestSetExtended;
import net.caffeinemc.mods.lithium.common.world.interests.PointOfInterestStorageExtended;
import net.caffeinemc.mods.lithium.common.world.interests.RegionBasedStorageSectionExtended;
import net.caffeinemc.mods.lithium.common.world.interests.iterator.NearbyPointOfInterestStream;
import net.caffeinemc.mods.lithium.common.world.interests.iterator.SinglePointOfInterestTypeFilter;
import net.caffeinemc.mods.lithium.common.world.interests.iterator.SphereChunkOrderedPoiSetSpliterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiManager.Occupancy;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(PoiManager.class)
public abstract class PoiManagerMixin extends SectionStorage<PoiSection> implements PointOfInterestStorageExtended {
   public PoiManagerMixin(
      SimpleRegionStorage storageAccess,
      Function<Runnable, Codec<PoiSection>> codecFactory,
      Function<Runnable, PoiSection> factory,
      RegistryAccess registryManager,
      ChunkIOErrorReporter errorHandler,
      LevelHeightAccessor world
   ) {
      super(storageAccess, codecFactory, factory, registryManager, errorHandler, world);
   }

   @Overwrite
   @VisibleForDebug
   public Stream<PoiRecord> getInChunk(Predicate<Holder<PoiType>> predicate, ChunkPos pos, Occupancy status) {
      return ((RegionBasedStorageSectionExtended)this).lithium$getWithinChunkColumn(pos.x, pos.z).flatMap(set -> set.getRecords(predicate, status));
   }

   @Overwrite
   public Optional<BlockPos> getRandom(
      Predicate<Holder<PoiType>> typePredicate, Predicate<BlockPos> posPredicate, Occupancy status, BlockPos pos, int radius, RandomSource rand
   ) {
      ArrayList<PoiRecord> list = this.withinSphereChunkSectionSorted(typePredicate, pos, radius, status);

      for (int i = list.size() - 1; i >= 0; i--) {
         PoiRecord currentPOI = list.set(rand.nextInt(i + 1), list.get(i));
         list.set(i, currentPOI);
         if (posPredicate.test(currentPOI.getPos())) {
            return Optional.of(currentPOI.getPos());
         }
      }

      return Optional.empty();
   }

   @Overwrite
   public Optional<BlockPos> findClosest(Predicate<Holder<PoiType>> predicate, BlockPos pos, int radius, Occupancy status) {
      return this.findClosest(predicate, null, pos, radius, status);
   }

   @Overwrite
   public Optional<BlockPos> findClosest(Predicate<Holder<PoiType>> predicate, Predicate<BlockPos> posPredicate, BlockPos pos, int radius, Occupancy status) {
      Stream<PoiRecord> pointOfInterestStream = this.streamOutwards(
         pos, radius, status, true, false, predicate, posPredicate == null ? null : poi -> posPredicate.test(poi.getPos())
      );
      return pointOfInterestStream.<BlockPos>map(PoiRecord::getPos).findFirst();
   }

   @Overwrite
   public long getCountInRange(Predicate<Holder<PoiType>> predicate, BlockPos pos, int radius, Occupancy status) {
      return this.withinSphereChunkSectionSorted(predicate, pos, radius, status).size();
   }

   @Overwrite
   public Stream<PoiRecord> getInRange(Predicate<Holder<PoiType>> predicate, BlockPos sphereOrigin, int radius, Occupancy status) {
      return this.withinSphereChunkSectionSortedStream(predicate, sphereOrigin, radius, status);
   }

   @Override
   public Optional<PoiRecord> lithium$findNearestForPortalLogic(
      BlockPos origin, int radius, Holder<PoiType> type, Occupancy status, Predicate<PoiRecord> afterSortPredicate, WorldBorder worldBorder
   ) {
      boolean worldBorderIsFarAway = worldBorder == null || worldBorder.getDistanceToBorder(origin.getX(), origin.getZ()) > radius + 3;
      Predicate<PoiRecord> poiPredicateAfterSorting;
      if (worldBorderIsFarAway) {
         poiPredicateAfterSorting = afterSortPredicate;
      } else {
         poiPredicateAfterSorting = poi -> worldBorder.isWithinBounds(poi.getPos()) && afterSortPredicate.test(poi);
      }

      return this.streamOutwards(origin, radius, status, true, true, new SinglePointOfInterestTypeFilter(type), poiPredicateAfterSorting).findFirst();
   }

   private Stream<PoiRecord> withinSphereChunkSectionSortedStream(Predicate<Holder<PoiType>> predicate, BlockPos origin, int radius, Occupancy status) {
      double radiusSq = radius * radius;
      RegionBasedStorageSectionExtended<PoiSection> storage = (RegionBasedStorageSectionExtended<PoiSection>)this;
      Stream<Stream<PoiSection>> stream = StreamSupport.stream(new SphereChunkOrderedPoiSetSpliterator(radius, origin, storage), false);
      return stream.flatMap(
         setStream -> setStream.flatMap(
            set -> set.getRecords(predicate, status).filter(point -> Distances.isWithinCircleRadius(origin, radiusSq, point.getPos()))
         )
      );
   }

   private ArrayList<PoiRecord> withinSphereChunkSectionSorted(Predicate<Holder<PoiType>> predicate, BlockPos origin, int radius, Occupancy status) {
      double radiusSq = radius * radius;
      int minChunkX = origin.getX() - radius - 1 >> 4;
      int minChunkZ = origin.getZ() - radius - 1 >> 4;
      int maxChunkX = origin.getX() + radius + 1 >> 4;
      int maxChunkZ = origin.getZ() + radius + 1 >> 4;
      RegionBasedStorageSectionExtended<PoiSection> storage = (RegionBasedStorageSectionExtended<PoiSection>)this;
      ArrayList<PoiRecord> points = new ArrayList<>();
      Consumer<PoiRecord> collector = point -> {
         if (Distances.isWithinCircleRadius(origin, radiusSq, point.getPos())) {
            points.add(point);
         }
      };

      for (int x = minChunkX; x <= maxChunkX; x++) {
         for (int z = minChunkZ; z <= maxChunkZ; z++) {
            for (PoiSection set : storage.lithium$getInChunkColumn(x, z)) {
               ((PointOfInterestSetExtended)set).lithium$collectMatchingPoints(predicate, status, collector);
            }
         }
      }

      return points;
   }

   private Stream<PoiRecord> streamOutwards(
      BlockPos origin,
      int radius,
      Occupancy status,
      boolean useSquareDistanceLimit,
      boolean preferNegativeY,
      Predicate<Holder<PoiType>> typePredicate,
      @Nullable Predicate<PoiRecord> afterSortingPredicate
   ) {
      RegionBasedStorageSectionExtended<PoiSection> storage = (RegionBasedStorageSectionExtended<PoiSection>)this;
      return StreamSupport.stream(
         new NearbyPointOfInterestStream(typePredicate, status, useSquareDistanceLimit, preferNegativeY, afterSortingPredicate, origin, radius, storage), false
      );
   }
}
