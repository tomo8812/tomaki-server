package maki.chunksmithcache.mixin;

import com.kishku7.chunksmith.GenerationTask;
import com.kishku7.chunksmith.Selection;
import com.kishku7.chunksmith.lod.CsLodPresenceIndex;
import com.kishku7.chunksmith.platform.NeoForgeWorld;
import com.kishku7.chunksmith.util.RegionCache;
import maki.chunksmithcache.CacheManager;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The horizon fast-skip may only skip an owned pregen coordinate when both the
 * native LOD record and the pregen FULL side are present. Normal FULL chunks
 * still fall through to Chunksmith's native generated-state branch.
 */
@Mixin(targets="com.kishku7.chunksmith.GenerationTask",remap=false)
public abstract class GenerationTaskPairMixin {
    @Shadow @Final private Selection selection;

    @Redirect(
        method="run()V",
        at=@At(
            value="INVOKE",
            target="Lcom/kishku7/chunksmith/util/RegionCache$WorldState;isGenerated(II)Z"
        ),
        require=1
    )
    private boolean maki$requirePersistentWorldState(RegionCache.WorldState state,int x,int z) {
        boolean generated=state.isGenerated(x,z);
        if(selection.world() instanceof NeoForgeWorld world)
            return CacheManager.chunksmithWorldStateGenerated(world.getWorld(),new ChunkPos(x,z),generated);
        return generated;
    }

    @Redirect(
        method="run()V",
        at=@At(
            value="INVOKE",
            target="Lcom/kishku7/chunksmith/lod/CsLodPresenceIndex;hasLod(II)Z",
            ordinal=0
        ),
        require=1
    )
    private boolean maki$requireFullSideForHorizonSkip(CsLodPresenceIndex index,int x,int z) {
        boolean lod=index.hasLod(x,z);
        if(!lod)return false;
        if(selection.world() instanceof NeoForgeWorld world
            && CacheManager.unifiedPregenOwns(world.getWorld())) {
            return CacheManager.unifiedPairSkipHint(world.getWorld(),new ChunkPos(x,z),index);
        }
        return lod;
    }

    @Redirect(
        method="run()V",
        at=@At(
            value="INVOKE",
            target="Lcom/kishku7/chunksmith/lod/CsLodPresenceIndex;hasLod(II)Z",
            ordinal=1
        ),
        require=1
    )
    private boolean maki$observeNativeFullPair(CsLodPresenceIndex index,int x,int z) {
        boolean lod=index.hasLod(x,z);
        if(lod && selection.world() instanceof NeoForgeWorld world
            && CacheManager.unifiedPregenOwns(world.getWorld()))
            lod=CacheManager.chunksmithLodSkipHint(world.getWorld(),new ChunkPos(x,z),index);
        if(lod && selection.world() instanceof NeoForgeWorld world)
            CacheManager.noteObservedNormalFullLod(world.getWorld(),new ChunkPos(x,z));
        return lod;
    }

    // Chunksmith 4.6.0 compiles the thenCompose branch in run() as a
    // separate lambda$run$1 method. Its hasLod call is not in run()V.
    @Redirect(
        method="lambda$run$1(Lcom/kishku7/chunksmith/lod/CsLodPresenceIndex;Lcom/kishku7/chunksmith/util/ChunkCoordinate;ZZ[ZLjava/lang/Boolean;)Ljava/util/concurrent/CompletionStage;",
        at=@At(
            value="INVOKE",
            target="Lcom/kishku7/chunksmith/lod/CsLodPresenceIndex;hasLod(II)Z",
            ordinal=0
        ),
        require=1,
        allow=1
    )
    private boolean maki$observeAsyncLod(CsLodPresenceIndex index,int x,int z) {
        boolean lod=index.hasLod(x,z);
        if(lod && selection.world() instanceof NeoForgeWorld world
            && CacheManager.unifiedPregenOwns(world.getWorld()))
            lod=CacheManager.chunksmithLodSkipHint(world.getWorld(),new ChunkPos(x,z),index);
        if(lod && selection.world() instanceof NeoForgeWorld world) {
            ChunkPos pos=new ChunkPos(x,z);
            CacheManager.noteObservedLodPresent(world.getWorld(),pos);
            CacheManager.pregenCandidateSkipped(world.getWorld(),pos,(GenerationTask)(Object)this);
        }
        return lod;
    }
}
