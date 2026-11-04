package net.caffeinemc.mods.lithium.common.world;

import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Objects;
import net.caffeinemc.mods.lithium.common.tracking.block.ChunkSectionChangeCallback;
import net.caffeinemc.mods.lithium.common.tracking.block.SectionedBlockChangeTracker;
import net.caffeinemc.mods.lithium.common.tracking.entity.SectionedEntityMovementTracker;
import net.caffeinemc.mods.lithium.common.util.deduplication.LithiumInterner;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface LithiumData {
   LithiumData.Data lithium$getData();

   record Data(
      GameEventDispatcherStorage gameEventDispatchers,
      ItemStack ominousBanner,
      ReferenceOpenHashSet<PathNavigation> activeNavigations,
      LithiumInterner<SectionedBlockChangeTracker> blockChangeTrackers,
      LithiumInterner<SectionedEntityMovementTracker<?, ?>> entityMovementTrackers,
      Long2ReferenceOpenHashMap<ChunkSectionChangeCallback> chunkSectionChangeCallbacks
   ) {
      public Data(Provider registries) {
         this(
            new GameEventDispatcherStorage(),
            tryGetOminousBanner(registries),
            new ReferenceOpenHashSet(),
            new LithiumInterner<>(),
            new LithiumInterner<>(),
            new Long2ReferenceOpenHashMap()
         );
      }

      @Nullable
      private static ItemStack tryGetOminousBanner(Provider registries) {
         try {
            return Objects.requireNonNullElse(registries, RegistryAccess.EMPTY)
               .lookup(Registries.BANNER_PATTERN)
               .<ItemStack>map(Raid::getLeaderBannerInstance)
               .orElse(null);
         } catch (Exception e) {
            return null;
         }
      }
   }
}
