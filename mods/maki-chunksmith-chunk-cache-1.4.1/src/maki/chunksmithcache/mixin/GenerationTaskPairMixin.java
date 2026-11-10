package maki.chunksmithcache.mixin;

import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.platform.NeoForgeWorld;
import com.kishku7.chunksmith.util.RegionCache.WorldState;
import maki.chunksmithcache.CacheManager;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(targets = "com.kishku7.chunksmith.GenerationTask", remap = false)
public abstract class GenerationTaskPairMixin {
   @Shadow
   @Final
   private Selection selection;

   @Redirect(method = "run()V", at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/util/RegionCache$WorldState;isGenerated(II)Z"), require = 1)
   private boolean maki$requirePersistentWorldState(WorldState state, int x, int z) {
      boolean generated = state.isGenerated(x, z);
      return this.selection.world() instanceof NeoForgeWorld world
         ? CacheManager.chunksmithWorldStateGenerated(world.getWorld(), new ChunkPos(x, z), generated)
         : generated;
   }

   @Redirect(method = "run()V", at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/lod/CsLodPresenceIndex;hasLod(II)Z", ordinal = 0), require = 1)
   private boolean maki$requireFullSideForHorizonSkip(CsLodPresenceIndex index, int x, int z) {
      boolean lod = index.hasLod(x, z);
      if (!lod) {
         return false;
      } else {
         return this.selection.world() instanceof NeoForgeWorld world && CacheManager.unifiedPregenOwns(world.getWorld())
            ? CacheManager.unifiedPairSkipHint(world.getWorld(), new ChunkPos(x, z), index)
            : lod;
      }
   }

   @Redirect(method = "run()V", at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/lod/CsLodPresenceIndex;hasLod(II)Z", ordinal = 1), require = 1)
   private boolean maki$observeNativeFullPair(CsLodPresenceIndex index, int x, int z) {
      boolean lod = index.hasLod(x, z);
      if (lod && this.selection.world() instanceof NeoForgeWorld world && CacheManager.unifiedPregenOwns(world.getWorld())) {
         lod = CacheManager.chunksmithLodSkipHint(world.getWorld(), new ChunkPos(x, z), index);
      }

      if (lod && this.selection.world() instanceof NeoForgeWorld world) {
         CacheManager.noteObservedNormalFullLod(world.getWorld(), new ChunkPos(x, z));
      }

      return lod;
   }

   @Redirect(method = "run()V", at = @At(value = "INVOKE", target = "Lcom/kishku7/chunksmith/lod/CsLodPresenceIndex;hasLod(II)Z", ordinal = 2), require = 1)
   private boolean maki$observeAsyncLod(CsLodPresenceIndex index, int x, int z) {
      boolean lod = index.hasLod(x, z);
      if (lod && this.selection.world() instanceof NeoForgeWorld world && CacheManager.unifiedPregenOwns(world.getWorld())) {
         lod = CacheManager.chunksmithLodSkipHint(world.getWorld(), new ChunkPos(x, z), index);
      }

      if (lod && this.selection.world() instanceof NeoForgeWorld world) {
         CacheManager.noteObservedLodPresent(world.getWorld(), new ChunkPos(x, z));
      }

      return lod;
   }
}
